package net.ruffpug.suckerreimu.result.components

import kotlinx.browser.window
import net.ruffpug.suckerreimu.Game
import net.ruffpug.suckerreimu.Logger
import net.ruffpug.suckerreimu.Resources
import net.ruffpug.suckerreimu.TouchEvent
import net.ruffpug.suckerreimu.result.ResultSceneArgs
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.Image
import org.w3c.dom.url.URLSearchParams

/**
 * 共有ボタン
 */
internal class ShareButton(private val args: ResultSceneArgs) {
    companion object {

        //  下部のパディング
        private const val BOTTOM_MARGIN: Double = 64.0

        //  フォーカス時のボタン不透明度比率
        private const val FOCUSED_ALPHA_RATIO: Double = 0.75
    }

    //  ボタンがフォーカスされているかどうか
    private var isFocused: Boolean = false

    /**
     * 描画処理を行う。
     */
    fun render(context: CanvasRenderingContext2D) {
        //  座標を計算する。
        val image = Resources.Images.shareButton
        val (x, y) = this.calculateUpperLeftPos(image)

        //  フォーカス有無に応じて不透明度を決定して描画する。
        val alpha = context.globalAlpha
        context.globalAlpha = if (this.isFocused) alpha * FOCUSED_ALPHA_RATIO else alpha
        context.drawImage(image = image, dx = x, dy = y)
        context.globalAlpha = alpha
    }

    /**
     * タッチイベントが発生したとき。
     */
    fun onTouchEventOccurred(event: TouchEvent) {
        val image = Resources.Images.shareButton

        when (event) {

            //  押下された場合
            is TouchEvent.PointerDown -> {
                if (this.isFocused) return

                //  フォーカス有無を更新する。
                this.isFocused = this.isFocused(image, event.x, event.y)
                Logger.d { "共有ボタン 押下: ${this.isFocused}, $event" }
            }

            //  移動された場合
            is TouchEvent.PointerMove -> {
                if (!this.isFocused) return

                //  フォーカスの継続を判定する。
                this.isFocused = this.isFocused(image, event.x, event.y)
                if (!this.isFocused) Logger.d { "共有ボタン フォーカスキャンセル: $event" }
            }

            //  押上された場合
            is TouchEvent.PointerUp -> {
                if (!this.isFocused) return

                Logger.d { "共有ボタン 押上: $event" }
                this.isFocused = false
                this.onButtonClicked()
            }

            //  キャンセルされた場合
            is TouchEvent.PointerCancel -> {
                this.isFocused = false
                Logger.d { "共有ボタン キャンセル" }
            }
        }
    }

    //  左上の座標を計算する。
    private fun calculateUpperLeftPos(image: Image): Pair<Double, Double> {
        val leftX = Game.CANVAS_WIDTH / 2 - image.width / 2
        val topY = Game.CANVAS_HEIGHT - BOTTOM_MARGIN - image.height

        return leftX to topY
    }

    //  タッチ座標がボタンの範囲に入っているかどうかを判定する。
    private fun isFocused(image: Image, x: Double, y: Double): Boolean {
        val (left, top) = this.calculateUpperLeftPos(image)
        val right = left + image.width
        val bottom = top + image.height

        return x in left..right && y in top..bottom
    }

    //  共有ボタンがクリックされたとき。
    private fun onButtonClicked() {
        Logger.i { "共有ボタン クリック" }

        //  Twitterでの共有URLを組み立てる。
        val twitterBaseUrl = "https://twitter.com/intent/tweet?"
        val text = when (this.args.win) {

            //  勝ち
            true -> "${this.args.turn}ターン目にまりさちゃんに勝利!"

            //  負け
            false -> "${this.args.turn}ターン目にまりさちゃんに敗北!"
        }
        val url = window.location.href
        val query = URLSearchParams().also {
            it.append("text", text)
            it.append("url", url)
        }
        val shareUrl = "$twitterBaseUrl$query"

        //  共有リンクを踏ませる。
        Logger.d { "共有ボタン リンク: $shareUrl" }
        window.open(shareUrl, "_blank")?.focus()
    }
}
