package net.ruffpug.suckerreimu

import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.pointerevents.PointerEvent

/**
 * タッチイベント
 */
internal sealed interface TouchEvent {

    /**
     * ポインタID
     */
    val pointerId: Int

    /**
     * X座標
     */
    val x: Double

    /**
     * Y座標
     */
    val y: Double

    /**
     * 押下イベント
     */
    data class PointerDown(override val pointerId: Int, override val x: Double, override val y: Double) : TouchEvent {
        companion object {

            /**
             * PointerEventから生成する。
             */
            fun of(canvas: HTMLCanvasElement, e: PointerEvent): PointerDown {
                return PointerDown(e.pointerId, toScaledX(canvas, e), toScaledY(canvas, e))
            }
        }
    }

    /**
     * 移動イベント
     */
    data class PointerMove(override val pointerId: Int, override val x: Double, override val y: Double) : TouchEvent {
        companion object {

            /**
             * PointerEventから生成する。
             */
            fun of(canvas: HTMLCanvasElement, e: PointerEvent): PointerMove {
                return PointerMove(e.pointerId, toScaledX(canvas, e), toScaledY(canvas, e))
            }
        }
    }

    /**
     * 押上イベント
     */
    data class PointerUp(override val pointerId: Int, override val x: Double, override val y: Double) : TouchEvent {
        companion object {

            /**
             * PointerEventから生成する。
             */
            fun of(canvas: HTMLCanvasElement, e: PointerEvent): PointerUp {
                return PointerUp(e.pointerId, toScaledX(canvas, e), toScaledY(canvas, e))
            }
        }
    }

    /**
     * キャンセルイベント
     */
    data class PointerCancel(override val pointerId: Int, override val x: Double, override val y: Double) : TouchEvent {
        companion object {

            /**
             * PointerEventから生成する。
             */
            fun of(canvas: HTMLCanvasElement, e: PointerEvent): PointerCancel {
                return PointerCancel(e.pointerId, toScaledX(canvas, e), toScaledY(canvas, e))
            }
        }
    }
}

//  スケーリングされたX座標に変換する。
private fun toScaledX(canvas: HTMLCanvasElement, e: PointerEvent): Double =
    e.offsetX * Game.CANVAS_WIDTH / (canvas.width / Game.RENDERING_SCALE)

//  スケーリングされたY座標に変換する。
private fun toScaledY(canvas: HTMLCanvasElement, e: PointerEvent): Double =
    e.offsetY * Game.CANVAS_HEIGHT / (canvas.height / Game.RENDERING_SCALE)
