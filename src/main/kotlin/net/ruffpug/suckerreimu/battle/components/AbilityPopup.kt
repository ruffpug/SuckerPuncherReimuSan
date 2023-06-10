package net.ruffpug.suckerreimu.battle.components

import kotlinx.coroutines.delay
import net.ruffpug.suckerreimu.Game
import net.ruffpug.suckerreimu.battle.CharacterSide
import net.ruffpug.suckerreimu.startAnimUpdaterAsync
import org.w3c.dom.CanvasRenderingContext2D

/**
 * 特性ポップアップ
 */
internal class AbilityPopup(private val side: CharacterSide) {
    companion object {

        //  ポップアップの横幅
        private const val WIDTH: Double = 300.0

        //  ポップアップの高さ
        private const val HEIGHT: Double = 80.0

        //  横方向のマージン
        private const val HORIZONTAL_MARGIN: Double = 32.0

        //  横方向のパディング
        private const val HORIZONTAL_PADDING: Double = 16.0

        //  縦方向のパディング
        private const val VERTICAL_PADDING: Double = 18.0

        //  自分側の座標
        private val OWN_POSITION: Pair<Double, Double> = HORIZONTAL_MARGIN to Game.CANVAS_HEIGHT / 2 - HEIGHT / 2

        //  相手側の座標
        private val OPPONENT_POSITION: Pair<Double, Double> =
            Game.CANVAS_WIDTH - WIDTH - HORIZONTAL_MARGIN to Game.CANVAS_HEIGHT / 2 - HEIGHT / 2

        //  フレームの塗りスタイル
        private const val FRAME_FILL_STYLE: String = "#333333"

        //  フォントサイズ
        private const val FONT_SIZE: Int = 32

        //  フォント
        private const val FONT: String = "${FONT_SIZE}px Arial"

        //  文字の塗りスタイル
        private const val TEXT_FILL_STYLE: String = "#FFFFFF"

        //  表示アニメーションの期間
        private const val ANIM_DURATION_IN_MS: Long = 100L
    }

    //  アニメーションの進捗率 (0.0: 非表示, 1.0: 完全表示)
    private var animProgress: Float = 0.0f

    //  特性名
    private var abilityName: String? = null

    /**
     * 描画処理を行う。
     */
    fun render(context: CanvasRenderingContext2D) {
        if (this.animProgress == 0.0f) return
        val abilityName: String = this.abilityName ?: return

        //  フレームを描画する。
        val (destX, y) = when (this.side) {
            CharacterSide.OWN -> OWN_POSITION
            CharacterSide.OPPONENT -> OPPONENT_POSITION
        }
        val initialX = when (this.side) {
            CharacterSide.OWN -> WIDTH * -1
            CharacterSide.OPPONENT -> Game.CANVAS_WIDTH
        }
        val x = initialX + (destX - initialX) * this.animProgress
        context.fillStyle = FRAME_FILL_STYLE
        context.fillRect(x = x, y = y, w = WIDTH, h = HEIGHT)

        //  特性名を描画する。
        val nameX = x + HORIZONTAL_PADDING
        val nameY = y + VERTICAL_PADDING + FONT_SIZE
        context.font = FONT
        context.fillStyle = TEXT_FILL_STYLE
        context.fillText(text = abilityName, x = nameX, y = nameY)
    }

    /**
     * 特性ポップアップを表示する。
     */
    suspend fun showPopup(abilityName: String, durationInMs: Long) {
        val waitTimeInMs = (durationInMs - ANIM_DURATION_IN_MS * 2).coerceAtLeast(minimumValue = 0L)
        this.abilityName = abilityName
        this.animProgress = 0.0f

        //  フェードで表示・非表示を行い、指定期間の表示を行うようにする。
        startAnimUpdaterAsync(ANIM_DURATION_IN_MS) { this.animProgress = it }
        delay(waitTimeInMs)
        startAnimUpdaterAsync(ANIM_DURATION_IN_MS) { this.animProgress = 1.0f - it }

        this.abilityName = null
        this.animProgress = 0.0f
    }
}
