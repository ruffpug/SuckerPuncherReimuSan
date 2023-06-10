package net.ruffpug.suckerreimu.result.components

import net.ruffpug.suckerreimu.Game
import net.ruffpug.suckerreimu.result.ResultSceneArgs
import org.w3c.dom.CENTER
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.CanvasTextAlign
import org.w3c.dom.START

/**
 * 結果メッセージ
 */
internal class ResultMessage(private val args: ResultSceneArgs) {
    companion object {

        //  上部マージン
        private const val TOP_MARGIN: Double = 128.0

        //  塗りスタイル
        private const val FILL_STYLE: String = "#FFFFFF"

        //  フォントサイズ
        private const val FONT_SIZE: Int = 96

        //  フォントスタイル
        private const val FONT: String = "${FONT_SIZE}px Arial"
    }

    /**
     * 描画処理を行う。
     */
    fun render(context: CanvasRenderingContext2D) {
        //  勝敗によって文言を決定する。
        val text = when (this.args.win) {

            //  勝ち
            true -> "まりさちゃんに勝利!"

            //  負け
            false -> "まりさちゃんに敗北!"
        }
        val x = Game.CANVAS_WIDTH / 2
        val y = TOP_MARGIN + FONT_SIZE

        context.fillStyle = FILL_STYLE
        context.font = FONT
        context.textAlign = CanvasTextAlign.CENTER
        context.fillText(text = text, x = x, y = y)
        context.textAlign = CanvasTextAlign.START
    }
}
