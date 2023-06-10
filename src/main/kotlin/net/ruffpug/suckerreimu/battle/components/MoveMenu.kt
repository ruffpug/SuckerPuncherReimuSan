package net.ruffpug.suckerreimu.battle.components

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import net.ruffpug.suckerreimu.Game
import net.ruffpug.suckerreimu.Logger
import net.ruffpug.suckerreimu.TouchEvent
import net.ruffpug.suckerreimu.battle.MoveSnapshot
import net.ruffpug.suckerreimu.startAnimUpdaterAsync
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.CanvasTextAlign
import org.w3c.dom.END
import org.w3c.dom.START

/**
 * 技メニュー
 */
internal class MoveMenu {
    companion object {

        //  横のマージン
        private const val HORIZONTAL_MARGIN: Double = 32.0

        //  縦のマージン
        private const val VERTICAL_MARGIN: Double = 48.0

        //  横のパディング
        private const val HORIZONTAL_PADDING: Double = 16.0

        //  縦のパディング
        private const val VERTICAL_PADDING: Double = 24.0

        //  項目間の空白
        private const val ITEM_SPACE: Double = 16.0

        //  技メニュー項目の横幅
        private const val ITEM_WIDTH: Double = 400.0

        //  技メニュー項目の高さ
        private const val ITEM_HEIGHT: Double = 100.0

        //  技メニュー項目の最大数
        private const val MAX_ITEM_COUNT: Int = 4

        //  技メニュー項目の塗りスタイル
        private const val ITEM_FILL_STYLE: String = "#42A5F5"

        //  フォーカスされた技メニュー項目の塗りスタイル
        private const val FOCUSED_ITEM_FILL_STYLE: String = "#3295E5"

        //  非活性化された技メニュー項目の塗りスタイル
        private const val DISABLED_ITEM_FILL_STYLE: String = "#808080"

        //  技名のフォントサイズ
        private const val MOVE_NAME_FONT_SIZE: Int = 32

        //  技名のフォント
        private const val MOVE_NAME_FONT: String = "${MOVE_NAME_FONT_SIZE}px Arial"

        //  技名の塗りスタイル
        private const val MOVE_NAME_FILL_STYLE: String = "#FFFFFF"

        //  技PPのフォントサイズ
        private const val MOVE_PP_FONT_SIZE: Int = 24

        //  技PPのフォント
        private const val MOVE_PP_FONT: String = "${MOVE_PP_FONT_SIZE}px Arial"

        //  技PPの塗りスタイル
        private const val MOVE_PP_FILL_STYLE: String = "#FFFFFF"

        //  フェード期間
        private const val FADE_DURATION_IN_MS: Long = 100L

        //  各技メニュー項目の描画開始座標のリスト
        //  (上から順の左上描画開始地点座標のリスト)
        private val ITEM_POSITIONS: List<Pair<Double, Double>> = List(size = MAX_ITEM_COUNT) { index ->
            val itemCount = MAX_ITEM_COUNT - index
            val spaceCount = MAX_ITEM_COUNT - index - 1
            val x = Game.CANVAS_WIDTH - HORIZONTAL_MARGIN - ITEM_WIDTH
            val y = Game.CANVAS_HEIGHT - VERTICAL_MARGIN - ITEM_HEIGHT * itemCount - ITEM_SPACE * spaceCount

            x to y
        }
    }

    //  技リスト
    //  (技数が余る場合は先頭側からnull埋めされる。)
    private var moves: List<MoveSnapshot?> = emptyList()

    //  配置進捗率 (0.0: 画面外, 1.0: 配置完了)
    private var positioningProgress: Float = 0.0f

    //  現在フォーカス中の項目のインデックス
    private var focusedItemIndex: Int? = null

    //  選択された技
    private val selectedMove = MutableStateFlow<MoveSnapshot?>(value = null)

    /**
     * 描画処理を行う。
     */
    fun render(context: CanvasRenderingContext2D) {
        //  各技ごとに描画を行っていく。
        for (index in this.moves.indices) {
            this.renderItem(context, index)
        }
    }

    /**
     * タッチイベントが発生したとき。
     */
    fun onTouchEventOccurred(event: TouchEvent) {
        if (this.positioningProgress != 1.0f) return

        when (event) {

            //  押下された場合
            is TouchEvent.PointerDown -> {
                //  すでにフォーカス済みである場合、先にハンドリングしたものを優先する。
                if (this.focusedItemIndex != null) return

                //  タッチ座標に該当する項目を見つけ、フォーカス済みとして記録する。
                val index = this.findSelectedItemIndex(event.x, event.y) ?: return
                this.focusedItemIndex = index
                Logger.d { "技メニュー 押下: [$index], $event" }
            }

            //  移動された場合
            is TouchEvent.PointerMove -> {
                val focusedIndex = this.focusedItemIndex ?: return

                //  タッチ座標に該当する項目を見つけ、該当項目が現在フォーカス中の項目以外である場合、フォーカスをキャンセルする。
                val index = this.findSelectedItemIndex(event.x, event.y)
                if (focusedIndex != index) {
                    this.focusedItemIndex = null
                    Logger.d { "技メニュー 移動 フォーカスキャンセル: [$focusedIndex], [$index], $event" }
                }
            }

            //  押上された場合
            is TouchEvent.PointerUp -> {
                //  その時点でフォーカスされていた項目を選択したとみなす。
                val focusedIndex = this.focusedItemIndex ?: return
                this.focusedItemIndex = null
                val move = this.moves.elementAtOrNull(focusedIndex) ?: return
                if (move.remainingPp == 0) return

                //  技の選択を通知する。
                Logger.d { "技メニュー 選択: [$focusedIndex], $move, $event" }
                this.selectedMove.value = move
            }

            //  キャンセルされた場合
            is TouchEvent.PointerCancel -> {
                //  フォーカスを強制解除する。
                if (this.focusedItemIndex != null) {
                    Logger.d { "技メニュー キャンセル: [${this.focusedItemIndex}], $event" }
                    this.focusedItemIndex = null
                }
            }
        }
    }

    /**
     * 技メニューを表示する。
     * (戻り値で選択された技を返す。)
     */
    suspend fun showMoveMenu(moves: List<MoveSnapshot>): MoveSnapshot {
        try {
            //  技リストを設定する。
            this.moves = List(size = MAX_ITEM_COUNT) { i -> moves.elementAtOrNull(i - (MAX_ITEM_COUNT - moves.size)) }
            this.selectedMove.value = null

            //  アニメーションで表示させていく。
            startAnimUpdaterAsync(
                duration = FADE_DURATION_IN_MS,
                onUpdated = { progress -> this.positioningProgress = progress },
            )

            //  技が選択されるまで待機する。
            val selectedMove = this.selectedMove.filterNotNull().first()
            this.selectedMove.value = null

            //  アニメーションで非表示にしていく。
            startAnimUpdaterAsync(
                duration = FADE_DURATION_IN_MS,
                onUpdated = { progress -> this.positioningProgress = 1.0f - progress },
            )

            //  選択結果を返す。
            return selectedMove
        } finally {
            this.moves = emptyList()
        }
    }

    //  メニュー項目を描画する。
    private fun renderItem(context: CanvasRenderingContext2D, index: Int) {
        val move = this.moves.elementAtOrNull(index) ?: return

        //  フレームを描画する。
        val (destX, y) = ITEM_POSITIONS[index]
        val initialX = Game.CANVAS_WIDTH
        val x = initialX + (destX - initialX) * this.positioningProgress
        context.fillStyle = when {
            move.remainingPp == 0 -> DISABLED_ITEM_FILL_STYLE
            this.focusedItemIndex == index -> FOCUSED_ITEM_FILL_STYLE
            else -> ITEM_FILL_STYLE
        }
        context.fillRect(x, y, ITEM_WIDTH, ITEM_HEIGHT)

        //  技名を描画する。
        val nameX = x + HORIZONTAL_PADDING
        val nameY = y + VERTICAL_PADDING + MOVE_NAME_FONT_SIZE
        context.font = MOVE_NAME_FONT
        context.fillStyle = MOVE_NAME_FILL_STYLE
        context.fillText(move.moveKind.moveName, nameX, nameY)

        //  PPを描画する。
        val ppX = x + ITEM_WIDTH - HORIZONTAL_PADDING
        val ppY = nameY + MOVE_PP_FONT_SIZE
        context.font = MOVE_PP_FONT
        context.fillStyle = MOVE_PP_FILL_STYLE
        context.textAlign = CanvasTextAlign.END
        context.fillText("${move.remainingPp}/${move.maxPp}", ppX, ppY)
        context.textAlign = CanvasTextAlign.START
    }

    //  タッチ座標に対応した項目のインデックスを取得する。
    private fun findSelectedItemIndex(x: Double, y: Double): Int? {
        for (index in ITEM_POSITIONS.indices) {
            val (left, top) = ITEM_POSITIONS[index]
            val right = left + ITEM_WIDTH
            val bottom = top + ITEM_HEIGHT

            if (x in left..right && y in top..bottom) return index
        }

        return null
    }
}
