package net.ruffpug.suckerreimu.result

import net.ruffpug.suckerreimu.TouchEvent
import net.ruffpug.suckerreimu.result.components.ResultBackground
import net.ruffpug.suckerreimu.result.components.ResultMessage
import net.ruffpug.suckerreimu.result.components.ShareButton
import net.ruffpug.suckerreimu.startAnimUpdater
import org.w3c.dom.CanvasRenderingContext2D

/**
 * 結果シーン
 */
internal class ResultScene {
    companion object {

        //  フェードエフェクトの期間
        private const val FADE_DURATION_IN_MS: Long = 1000L
    }

    //  引数
    private lateinit var args: ResultSceneArgs

    //  背景
    private lateinit var background: ResultBackground

    //  結果メッセージ
    private lateinit var message: ResultMessage

    //  共有ボタン
    private lateinit var shareButton: ShareButton

    //  フェードエフェクトの進捗率
    private var fadeProgress: Float = 0.0f

    /**
     * 初期化を行う。
     */
    fun init(args: ResultSceneArgs) {
        this.args = args

        //  コンポーネントを初期化する。
        this.background = ResultBackground(this.args)
        this.message = ResultMessage(this.args)
        this.shareButton = ShareButton(this.args)

        //  フェードエフェクトを開始する。
        startAnimUpdater(duration = FADE_DURATION_IN_MS, onUpdated = { this.fadeProgress = it }, onFinished = {})
    }

    /**
     * 描画処理を行う。
     */
    fun render(context: CanvasRenderingContext2D) {
        context.globalAlpha = this.fadeProgress.toDouble()

        this.background.render(context)
        this.message.render(context)
        this.shareButton.render(context)

        context.globalAlpha = 1.0
    }

    /**
     * タッチイベントが発生したとき。
     */
    fun onTouchEventOccurred(event: TouchEvent) {
        this.shareButton.onTouchEventOccurred(event)
    }
}
