package net.ruffpug.suckerreimu

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement

private fun main() {
    //  ログの出力設定を行う。
    Logger.isEnabled = !isReleaseBuild
    Logger.i { "スクリプト処理開始" }

    //  画面のリサイズコールバックを登録する。
    //  (初回のコールバックは手動で呼び出す。)
    window.onresize = { onResize() }
    onResize()

    launchCoroutine {
        try {
            //  リソースの読み込みを行う。
            Resources.load()

            //  ゲームを開始する。
            startGame()
        } catch (e: Exception) {
            Logger.wtf(e) { "システムエラー" }
            window.alert("システムエラーが発生しました。")
        }
    }
}

//  ゲームの処理を開始する。
private fun startGame() {
    Logger.i { "ゲーム処理開始" }

    //  ゲームの初期化を行う。
    Game.INSTANCE.init()

    //  ゲームループを開始する。
    window.requestAnimationFrame { onAnimFrameCalled() }

    //  Canvasのタッチイベントを購読し、ゲーム処理に通知する。
    val canvas = findCanvas()
    canvas.onpointerdown = { Game.INSTANCE.onTouchEventOccurred(TouchEvent.PointerDown.of(canvas, it)) }
    canvas.onpointermove = { Game.INSTANCE.onTouchEventOccurred(TouchEvent.PointerMove.of(canvas, it)) }
    canvas.onpointerup = { Game.INSTANCE.onTouchEventOccurred(TouchEvent.PointerUp.of(canvas, it)) }
    canvas.onpointercancel = { Game.INSTANCE.onTouchEventOccurred(TouchEvent.PointerCancel.of(canvas, it)) }
}

//  アニメーション用フレームが呼び出されたとき。
private fun onAnimFrameCalled() {
    val canvas = findCanvas()
    val context = canvas.getContext("2d") as CanvasRenderingContext2D
    context.clearRect(x = 0.0, y = 0.0, w = canvas.width.toDouble(), h = canvas.height.toDouble())

    //  Canvasをスケーリングして固定サイズで描画できるようにする。
    val scaleX = canvas.width / Game.CANVAS_WIDTH
    val scaleY = canvas.height / Game.CANVAS_HEIGHT
    context.scale(scaleX, scaleY)

    //  ゲームの描画処理を行う。
    Game.INSTANCE.render(context)

    //  スケーリングを戻し、次のアニメーション用フレームの呼び出しを購読する。
    context.scale(1.0 / scaleX, 1.0 / scaleY)
    window.requestAnimationFrame { onAnimFrameCalled() }
}

//  DOMからCanvasを取得する。
private fun findCanvas(): HTMLCanvasElement = document.getElementById("canvas") as HTMLCanvasElement

//  リリースビルドで実行されているかどうかを判定する。
//  (NOTE: GitHub Pagesでホストするため、リリース環境では常にHTTPS化がされているものとする。)
private val isReleaseBuild: Boolean get() = document.location?.protocol?.lowercase()?.contains("https") ?: false
