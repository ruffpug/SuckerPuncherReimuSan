package net.ruffpug.suckerreimu.result.components

import net.ruffpug.suckerreimu.Resources
import net.ruffpug.suckerreimu.result.ResultSceneArgs
import org.w3c.dom.CanvasRenderingContext2D

/**
 * 背景
 */
internal class ResultBackground(private val args: ResultSceneArgs) {

    /**
     * 描画処理を行う。
     */
    fun render(context: CanvasRenderingContext2D) {
        //  背景画像を描画する。
        val image = if (this.args.win) Resources.Images.winResultScreen else Resources.Images.loseResultScreen
        context.drawImage(image = image, dx = 0.0, dy = 0.0)
    }
}
