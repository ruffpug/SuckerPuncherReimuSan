package net.ruffpug.suckerreimu.battle.components

import kotlinx.coroutines.delay
import net.ruffpug.suckerreimu.Game
import net.ruffpug.suckerreimu.startAnimUpdaterAsync
import org.w3c.dom.CanvasRenderingContext2D

/**
 * メッセージフレーム
 */
internal class MessageFrame {
    companion object {

        //  メッセージフレームの横幅
        private const val WIDTH: Double = 1200.0

        //  メッセージフレームの高さ
        private const val HEIGHT: Double = 200.0

        //  画面下部の余白
        private const val MARGIN_BOTTOM: Double = 16.0

        //  フェードの期間
        private const val FADE_DURATION_IN_MS: Long = 250L

        //  フレームの塗りスタイル
        private const val FRAME_FILL_STYLE: String = "#333333"

        //  文言の塗りスタイル
        private const val TEXT_FILL_STYLE: String = "#FFFFFF"

        //  文言のフォントサイズ
        private const val FONT_SIZE: Int = 48

        //  文言のフォントスタイル
        private const val FONT: String = "${FONT_SIZE}px Arial"

        //  文言の横余白
        private const val TEXT_HORIZONTAL_PADDING: Double = 32.0

        //  文言の縦余白
        private const val TEXT_VERTICAL_PADDING: Double = 32.0
    }

    //  不透明度
    private var alpha: Double = 0.0

    //  表示するテキスト
    private var text: String? = null

    /**
     * 描画処理を行う。
     */
    fun render(context: CanvasRenderingContext2D) {
        context.globalAlpha = this.alpha

        //  フレームを描画する。
        val frameX = (Game.CANVAS_WIDTH - WIDTH) / 2
        val frameY = Game.CANVAS_HEIGHT - HEIGHT - MARGIN_BOTTOM
        context.fillStyle = FRAME_FILL_STYLE
        context.fillRect(x = frameX, y = frameY, w = WIDTH, h = HEIGHT)

        //  テキストが設定されている場合、テキストを描画する。
        val text = this.text
        if (text != null) {
            val textX = frameX + TEXT_HORIZONTAL_PADDING
            val textY = frameY + TEXT_VERTICAL_PADDING + FONT_SIZE
            context.fillStyle = TEXT_FILL_STYLE
            context.font = FONT
            context.fillText(text, textX, textY)
        }

        context.globalAlpha = 1.0
    }

    /**
     * メッセージを表示する。
     */
    suspend fun showMessage(text: String, durationInMs: Long) {
        //  フェードインでフレームを表示させる。
        this.text = text
        startAnimUpdaterAsync(duration = FADE_DURATION_IN_MS) { progress -> this.alpha = progress.toDouble() }

        //  指定期間だけメッセージが表示されるように待機を入れる。
        val waitTimeInMs = (durationInMs - FADE_DURATION_IN_MS * 2).coerceAtLeast(minimumValue = 0L)
        delay(waitTimeInMs)

        //  フェードアウトでフレームを非表示にする。
        startAnimUpdaterAsync(duration = FADE_DURATION_IN_MS) { progress -> this.alpha = 1.0 - progress.toDouble() }
        this.text = null
    }
}
