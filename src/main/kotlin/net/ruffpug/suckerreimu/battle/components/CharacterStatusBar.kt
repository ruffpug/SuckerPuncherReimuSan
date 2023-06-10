package net.ruffpug.suckerreimu.battle.components

import net.ruffpug.suckerreimu.Game
import net.ruffpug.suckerreimu.battle.CharacterSide
import net.ruffpug.suckerreimu.startAnimUpdaterAsync
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.CanvasTextAlign
import org.w3c.dom.END
import org.w3c.dom.START

/**
 * キャラクタステータスバー
 */
internal class CharacterStatusBar(private val side: CharacterSide) {
    companion object {

        //  キャラクタステータスバーの横幅
        private const val WIDTH: Double = 300.0

        //  キャラクタステータスバーの高さ
        private const val HEIGHT: Double = 120.0

        //  縦のマージン
        private const val VERTICAL_MARGIN: Double = 32.0

        //  横のマージン
        private const val HORIZONTAL_MARGIN: Double = 32.0

        //  縦のパディング
        private const val VERTICAL_PADDING: Double = 12.0

        //  横のパディング
        private const val HORIZONTAL_PADDING: Double = 16.0

        //  自分側の座標
        private val OWN_POSITION: Pair<Double, Double> =
            HORIZONTAL_MARGIN to Game.CANVAS_HEIGHT - HEIGHT - VERTICAL_MARGIN

        //  相手側の座標
        private val OPPONENT_POSITION: Pair<Double, Double> =
            Game.CANVAS_WIDTH - WIDTH - HORIZONTAL_MARGIN to VERTICAL_MARGIN

        //  フレームの塗りスタイル
        private const val FRAME_FILL_STYLE: String = "#A0A0A0"

        //  キャラクタ名のフォントサイズ
        private const val NAME_FONT_SIZE: Int = 32

        //  キャラクタ名のフォント
        private const val NAME_FONT: String = "${NAME_FONT_SIZE}px Arial"

        //  HP文字列のフォントサイズ
        private const val HP_STR_FONT_SIZE: Int = 20

        //  HP文字列のフォント
        private const val HP_STR_FONT: String = "${HP_STR_FONT_SIZE}px Arial"

        //  テキストの塗りスタイル
        private const val TEXT_FILL_STYLE: String = "#FFFFFF"

        //  HPゲージの高さ
        private const val HP_GAUGE_HEIGHT: Double = 12.0

        //  HPゲージのトラック部分の塗りスタイル
        private const val HP_GAUGE_TRACK_FILL_STYLE: String = "#333333"

        //  HPゲージの緑ゲージの塗りスタイル
        private const val HP_GAUGE_GREEN_FILL_STYLE: String = "#00FF00"

        //  HPゲージの黄ゲージの塗りスタイル
        private const val HP_GAUGE_YELLOW_FILL_STYLE: String = "#FFFF00"

        //  HPゲージの赤ゲージの塗りスタイル
        private const val HP_GAUGE_RED_FILL_STYLE: String = "#FF0000"

        //  HPゲージの残量の最小横幅
        //  NOTE: HP1の場合でも最低限の横幅を確保することで見やすくするため。
        private const val HP_GAUGE_REMAINING_MIN_WIDTH: Double = 5.0
    }

    //  ステータスのスナップショット値
    private var statusSnapshot: StatusSnapshot? = null

    //  配置アニメーションの進捗率
    private var positioningProgress: Float = 0.0f

    /**
     * 描画処理を行う。
     */
    fun render(context: CanvasRenderingContext2D) {
        val (name, currentHp, maxHp) = this.statusSnapshot ?: return

        //  入場アニメの進捗率に基づいて座標を決定して、フレームを描画する。
        val (initialX, initialY) = when (this.side) {
            CharacterSide.OWN -> OWN_POSITION.copy(first = 0 - WIDTH)
            CharacterSide.OPPONENT -> OPPONENT_POSITION.copy(first = Game.CANVAS_WIDTH)
        }
        val (destX, destY) = when (this.side) {
            CharacterSide.OWN -> OWN_POSITION
            CharacterSide.OPPONENT -> OPPONENT_POSITION
        }
        val frameX = initialX + (destX - initialX) * this.positioningProgress
        val frameY = initialY + (destY - initialY) * this.positioningProgress
        context.fillStyle = FRAME_FILL_STYLE
        context.fillRect(x = frameX, y = frameY, w = WIDTH, h = HEIGHT)

        //  キャラクタ名を描画する。
        val nameX = frameX + HORIZONTAL_PADDING
        val nameY = frameY + NAME_FONT_SIZE + VERTICAL_PADDING
        context.font = NAME_FONT
        context.fillStyle = TEXT_FILL_STYLE
        context.fillText(text = name, x = nameX, y = nameY)

        //  HP数値を描画する。
        val hpStr = "$currentHp/$maxHp"
        val hpX = frameX + WIDTH - HORIZONTAL_PADDING
        val hpY = frameY + HEIGHT - VERTICAL_PADDING
        if (this.side == CharacterSide.OWN) {
            context.font = HP_STR_FONT
            context.fillStyle = TEXT_FILL_STYLE
            context.textAlign = CanvasTextAlign.END
            context.fillText(hpStr, hpX, hpY)
            context.textAlign = CanvasTextAlign.START
        }

        //  HPゲージを描画する。
        val hpGaugeX = frameX + HORIZONTAL_PADDING
        val hpGaugeCy = (nameY + hpY - HP_STR_FONT_SIZE) / 2
        val hpGaugeY = hpGaugeCy - HP_GAUGE_HEIGHT / 2
        val hpGaugeWidth = WIDTH - HORIZONTAL_PADDING * 2
        context.fillStyle = HP_GAUGE_TRACK_FILL_STYLE
        context.fillRect(x = hpGaugeX, y = hpGaugeY, w = hpGaugeWidth, h = HP_GAUGE_HEIGHT)

        //  HP残量に応じて色分けする。
        val remainingRatio = (currentHp.toDouble() / maxHp).coerceIn(minimumValue = 0.0, maximumValue = 1.0)
        val remainingWidth =
            if (currentHp == 0) 0.0 else (hpGaugeWidth * remainingRatio).coerceAtLeast(HP_GAUGE_REMAINING_MIN_WIDTH)
        context.fillStyle = when {
            remainingRatio <= 0.25 -> HP_GAUGE_RED_FILL_STYLE
            remainingRatio <= 0.5 -> HP_GAUGE_YELLOW_FILL_STYLE
            else -> HP_GAUGE_GREEN_FILL_STYLE
        }
        context.fillRect(x = hpGaugeX, y = hpGaugeY, w = remainingWidth, h = HP_GAUGE_HEIGHT)
    }

    /**
     * ステータスバーを表示させる。
     */
    suspend fun showStatusBar(name: String, currentHp: Int, maxHp: Int, durationInMs: Long) {
        val snapshot = StatusSnapshot(name, currentHp, maxHp)
        this.statusSnapshot = snapshot

        if (durationInMs == 0L) this.positioningProgress = 1.0f
        else startAnimUpdaterAsync(durationInMs) { progress -> this.positioningProgress = progress }
    }

    /**
     * ステータスバーを非表示にさせる。
     */
    suspend fun hideStatusBar(durationInMs: Long) {
        if (durationInMs == 0L) this.positioningProgress = 0.0f
        else startAnimUpdaterAsync(durationInMs) { progress -> this.positioningProgress = 1.0f - progress }

        this.statusSnapshot = null
    }

    /**
     * 現在HPを更新する。
     */
    suspend fun updateCurrentHp(destHp: Int, durationInMs: Long) {
        val snapshot = this.statusSnapshot ?: return
        val srcHp = snapshot.currentHp
        val diff = destHp - srcHp

        //  アニメーションさせながらHPを更新していく。
        startAnimUpdaterAsync(durationInMs) { progress ->
            this.statusSnapshot = snapshot.copy(currentHp = srcHp + (diff * progress).toInt())
        }
        this.statusSnapshot = snapshot.copy(currentHp = destHp)
    }

    //  ステータスのスナップショット値
    private data class StatusSnapshot(val name: String, val currentHp: Int, val maxHp: Int)
}
