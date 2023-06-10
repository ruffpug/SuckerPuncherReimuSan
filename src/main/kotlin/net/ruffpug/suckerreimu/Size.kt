package net.ruffpug.suckerreimu

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLCanvasElement

/**
 * 画面のリサイズが行われたとき。
 */
internal fun onResize() {
    //  画面のサイズを取得する。
    val containerWidth = window.innerWidth
    val containerHeight = window.innerHeight
    val isMobile = isMobile
    val canvas = document.getElementById("canvas") as HTMLCanvasElement
    Logger.i { "画面リサイズ: $containerWidth x $containerHeight, モバイル=$isMobile" }

    //  モバイル環境で縦長表示である場合
    val isPortrait = containerWidth < containerHeight
    if (isMobile && isPortrait) {
        Logger.v { "画面サイズ モバイル縦長: $containerWidth x $containerHeight" }

        //  90度回転させた際に画面に収まるようなCanvasサイズを設定する。
        val (canvasWidth, canvasHeight) = calculateCanvasSize(containerHeight, containerWidth)
        canvas.width = canvasWidth * Game.RENDERING_SCALE
        canvas.height = canvasHeight * Game.RENDERING_SCALE
        canvas.style.width = "${canvasWidth}px"
        canvas.style.height = "${canvasHeight}px"

        //  中央揃えを行う。
        canvas.style.position = "absolute"
        canvas.style.left = "${(containerWidth - canvasWidth) / 2}px"
        canvas.style.top = "${(containerHeight - canvasHeight) / 2}px"

        //  90度回転させる。
        canvas.style.transformOrigin = "center"
        canvas.style.transform = "rotate(90deg)"
    }

    //  モバイル環境以外、もしくは、横長表示である場合
    else {
        Logger.v { "画面サイズ 非モバイル縦長: $containerWidth x $containerHeight" }

        //  画面に収まるようなCanvasサイズを設定する。
        val (canvasWidth, canvasHeight) = calculateCanvasSize(containerWidth, containerHeight)
        canvas.width = canvasWidth * Game.RENDERING_SCALE
        canvas.height = canvasHeight * Game.RENDERING_SCALE
        canvas.style.width = "${canvasWidth}px"
        canvas.style.height = "${canvasHeight}px"

        //  中央揃えを行う。
        canvas.style.position = "absolute"
        canvas.style.left = "${(containerWidth - canvasWidth) / 2}px"
        canvas.style.top = "${(containerHeight - canvasHeight) / 2}px"

        //  回転をもとに戻す。
        canvas.style.transformOrigin = "center"
        canvas.style.transform = "rotate(0deg)"
    }
}

//  Canvasサイズを計算する。
//  (比率を保ったまま、最大サイズの制約に収まり、はみ出しが起きないようなCanvasのサイズを求める。)
private fun calculateCanvasSize(containerWidth: Int, containerHeight: Int): Pair<Int, Int> {
    //  領域がCanvas理想比率よりも横長である場合
    val ratio = containerWidth.toDouble() / containerHeight
    if (ratio >= CANVAS_RATIO) {
        Logger.v { "Canvasサイズ計算 理想比率よりも横長: $containerWidth x $containerHeight" }

        //  高さを基準にCanvasのサイズを決定する。
        val canvasHeight = containerHeight.coerceAtMost(maximumValue = CANVAS_MAX_HEIGHT)
        val canvasWidth = (canvasHeight * CANVAS_RATIO).toInt()
        Logger.v { "Canvasサイズ計算 理想比率よりも横長 Canvasサイズ: $canvasWidth x $canvasHeight" }

        return canvasWidth to canvasHeight
    }

    //  領域がCanvas理想比率よりも縦長である場合
    else {
        Logger.v { "Canvasサイズ計算 理想比率よりも縦長: $containerWidth x $containerHeight" }

        //  横幅を基準にCanvasのサイズを決定する。
        val canvasWidth = containerWidth.coerceAtMost(maximumValue = CANVAS_MAX_WIDTH)
        val canvasHeight = (canvasWidth / CANVAS_RATIO).toInt()
        Logger.v { "Canvasサイズ計算 理想比率よりも縦長 Canvasサイズ: $canvasWidth x $canvasHeight" }

        return canvasWidth to canvasHeight
    }
}

//  Canvasの最大横幅
private const val CANVAS_MAX_WIDTH: Int = 960

//  Canvasの最大高さ
private const val CANVAS_MAX_HEIGHT: Int = 540

//  Canvasの比率
private const val CANVAS_RATIO: Double = CANVAS_MAX_WIDTH.toDouble() / CANVAS_MAX_HEIGHT
