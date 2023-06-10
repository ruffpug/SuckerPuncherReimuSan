package net.ruffpug.suckerreimu.battle.components

import net.ruffpug.suckerreimu.Game
import net.ruffpug.suckerreimu.Resources
import net.ruffpug.suckerreimu.startAnimUpdaterAsync
import org.w3c.dom.CanvasRenderingContext2D

/**
 * 対戦背景
 */
internal class BattleBackground {

    //  フェードエフェクトの進捗率
    private var fadeProgress: Float = 0.0f

    /**
     * 描画処理を行う。
     */
    fun render(context: CanvasRenderingContext2D) {
        //  背景を黒で塗りつぶす。
        context.fillStyle = "#000000"
        context.fillRect(x = 0.0, y = 0.0, w = Game.CANVAS_WIDTH, h = Game.CANVAS_HEIGHT)

        //  背景画像を描画する。
        val alpha = this.fadeProgress.toDouble()
        context.globalAlpha = alpha
        context.drawImage(image = Resources.Images.background, dx = 0.0, dy = 0.0)
        context.globalAlpha = 1.0
    }

    /**
     * フェードインを行う。
     */
    suspend fun fadeIn(durationInMs: Long) {
        this.fadeProgress = 0.0f
        startAnimUpdaterAsync(durationInMs) { progress -> this.fadeProgress = progress }
    }
}
