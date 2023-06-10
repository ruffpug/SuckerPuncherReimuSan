package net.ruffpug.suckerreimu.battle.components

import kotlinx.browser.document
import net.ruffpug.suckerreimu.AnimCancellation
import net.ruffpug.suckerreimu.Game
import net.ruffpug.suckerreimu.Resources
import net.ruffpug.suckerreimu.battle.CharacterSide
import net.ruffpug.suckerreimu.startAnimUpdaterAsync
import net.ruffpug.suckerreimu.startInfiniteAnimUpdater
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.Image
import org.w3c.dom.Path2D
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.sin

/**
 * キャラクタ
 */
internal class Character(private val side: CharacterSide) {
    companion object {

        //  自分側キャラクタの中心座標
        private val OWN_CENTER_POSITION: Pair<Double, Double> = Game.CANVAS_WIDTH / 4 to Game.CANVAS_HEIGHT * 3 / 4

        //  相手側キャラクタの中心座標
        private val OPPONENT_CENTER_POSITION: Pair<Double, Double> = Game.CANVAS_WIDTH * 2 / 3 to Game.CANVAS_HEIGHT / 4

        //  待機アニメの期間
        private const val STANDBY_ANIM_DURATION_IN_MS: Long = 1000L

        //  待機アニメの拡大率
        private const val STANDBY_ANIM_SCALE: Double = 1.05

        //  持ち物エフェクトの最小半径
        private const val HELD_ITEM_EFFECT_MIN_RADIUS: Double = 15.0

        //  持ち物エフェクトの最大半径
        private const val HELD_ITEM_EFFECT_MAX_RADIUS: Double = 30.0

        //  持ち物波紋エフェクトの最小横幅
        private const val HELD_ITEM_RIPPLE_MIN_WIDTH: Double = 0.0

        //  持ち物波紋エフェクトの最大横幅
        private const val HELD_ITEM_RIPPLE_MAX_WIDTH: Double = 25.0
    }

    //  能力変化エフェクト描画用のCanvas
    private val statEffectCanvas: HTMLCanvasElement by lazy {
        document.createElement("canvas") as HTMLCanvasElement
    }

    //  入場アニメの進捗率
    private var enteringAnimProgress: Float = 0.0f

    //  退出アニメの進捗率
    private var exitingAnimProgress: Float = 0.0f

    //  待機アニメの進捗率
    private var standbyAnimProgress: Float = 0.0f

    //  待機アニメのキャンセルハンドラ
    private var standbyAnimCancellation: AnimCancellation? = null

    //  不透明度 (ダメージアニメーションで使用)
    private var damageAnimAlpha: Float = 1.0f

    //  能力上昇エフェクトの進捗率
    private var statUpEffectProgress: Float? = null

    //  能力下降エフェクトの進捗率
    private var statDownEffectProgress: Float? = null

    //  持ち物エフェクトの進捗率
    private var heldItemEffectProgress: Float? = null

    /**
     * 描画処理を行う。
     */
    fun render(context: CanvasRenderingContext2D) {
        val characterImage = when (this.side) {
            CharacterSide.OWN -> Resources.Images.characterReimu
            CharacterSide.OPPONENT -> Resources.Images.characterMarisa
        }

        //  キャラクタの座標・拡大率・クリッピング用パスを求める。
        val (cx, cy) = this.calculateCenterPos(characterImage)
        val currentScale = 1.0 + (STANDBY_ANIM_SCALE - 1.0) * (1 - abs(0.5 - this.standbyAnimProgress) * 2)
        val clipPath = this.calculateClippingPath(characterImage)
        val width = characterImage.width * currentScale
        val height = characterImage.height * currentScale
        val x = cx - width / 2
        val y = cy - height / 2

        //  キャラクタを描画する。
        context.globalAlpha = this.damageAnimAlpha.toDouble()
        context.save()
        if (this.exitingAnimProgress != 0.0f) context.clip(clipPath)
        context.drawImage(characterImage, x, y, width, height)
        context.restore()
        context.globalAlpha = 1.0

        //  エフェクトを描画する。
        this.renderStatUpEffect(context, characterImage, x, y)
        this.renderStatDownEffect(context, characterImage, x, y)
        this.renderHeldItemEffect(context, characterImage, cx, cy)
    }

    /**
     * 入場アニメーションを再生する。
     */
    suspend fun enter(durationInMs: Long) {
        startAnimUpdaterAsync(durationInMs) { this.enteringAnimProgress = it }
    }

    /**
     * 退出アニメーションを再生する。
     */
    suspend fun exit(durationInMs: Long) {
        startAnimUpdaterAsync(durationInMs) { this.exitingAnimProgress = it }
    }

    /**
     * 待機アニメーションを開始する。
     */
    fun startStandbyAnim() {
        this.standbyAnimCancellation?.cancel()
        this.standbyAnimCancellation =
            startInfiniteAnimUpdater(STANDBY_ANIM_DURATION_IN_MS) { this.standbyAnimProgress = it }
    }

    /**
     * 待機アニメーションを停止する。
     */
    fun stopStandbyAnim() {
        this.standbyAnimCancellation?.cancel()
        this.standbyAnimCancellation = null
        this.standbyAnimProgress = 0.0f
    }

    /**
     * ダメージアニメーションを再生する。
     */
    suspend fun playDamageAnim(blinkCount: Int, durationInMs: Long) {
        startAnimUpdaterAsync(durationInMs) { progress ->
            val alpha = abs(0.5 - (progress * blinkCount - floor(progress * blinkCount))) * 2
            this.damageAnimAlpha = alpha.toFloat()
        }
    }

    /**
     * 能力上昇エフェクトを再生する。
     */
    suspend fun playStatUpEffect(durationInMs: Long) {
        this.statUpEffectProgress = 0.0f
        startAnimUpdaterAsync(durationInMs) { this.statUpEffectProgress = it }
        this.statUpEffectProgress = null
    }

    /**
     * 能力下降エフェクトを再生する。
     */
    suspend fun playStatDownEffect(durationInMs: Long) {
        this.statDownEffectProgress = 0.0f
        startAnimUpdaterAsync(durationInMs) { this.statDownEffectProgress = it }
        this.statDownEffectProgress = null
    }

    /**
     * 持ち物エフェクトを再生する。
     */
    suspend fun playHeldItemEffect(durationInMs: Long) {
        this.heldItemEffectProgress = 0.0f
        startAnimUpdaterAsync(durationInMs) { this.heldItemEffectProgress = it }
        this.heldItemEffectProgress = null
    }

    //  進捗率に基づいた中央座標を計算する。
    private fun calculateCenterPos(characterImage: Image): Pair<Double, Double> {
        //  退出アニメーションが再生されていない場合、入場アニメーションに基づいて座標を計算する。
        if (this.exitingAnimProgress == 0.0f) {
            //  自分側は画面左端から、相手側は画面右端から入場してくる。
            val initialCx = when (this.side) {
                CharacterSide.OWN -> 0.0 - characterImage.width / 2
                CharacterSide.OPPONENT -> Game.CANVAS_WIDTH + characterImage.width / 2
            }
            val (destCx, cy) = when (this.side) {
                CharacterSide.OWN -> OWN_CENTER_POSITION
                CharacterSide.OPPONENT -> OPPONENT_CENTER_POSITION
            }
            val cx = initialCx + (destCx - initialCx) * this.enteringAnimProgress

            return cx to cy
        }

        //  退出アニメーションが再生されている場合、退出アニメーションに基づいて座標を計算する。
        else {
            val (cx, initialCy) = when (this.side) {
                CharacterSide.OWN -> OWN_CENTER_POSITION
                CharacterSide.OPPONENT -> OPPONENT_CENTER_POSITION
            }
            val destCy = initialCy + characterImage.height
            val cy = initialCy + (destCy - initialCy) * this.exitingAnimProgress

            return cx to cy
        }
    }

    //  クリッピング用Pathを計算する。
    //  (退出用アニメーション再生時にキャラクタ画像下端が途切れるようにするためにクリッピングが必要となる。)
    private fun calculateClippingPath(characterImage: Image): Path2D {
        val width = characterImage.width
        val height = characterImage.height

        val (cx, cy) = when (this.side) {
            CharacterSide.OWN -> OWN_CENTER_POSITION
            CharacterSide.OPPONENT -> OPPONENT_CENTER_POSITION
        }
        val left = cx - width / 2
        val right = left + width
        val top = cy - height / 2
        val bottom = top + height

        return Path2D().also {
            it.moveTo(left, top)
            it.lineTo(right, top)
            it.lineTo(right, bottom)
            it.lineTo(left, bottom)
            it.lineTo(left, top)
            it.closePath()
        }
    }

    //  能力上昇エフェクトを描画する。
    private fun renderStatUpEffect(context: CanvasRenderingContext2D, characterImage: Image, x: Double, y: Double) {
        val progress = this.statUpEffectProgress ?: return

        //  合成描画用のCanvasを取得する。
        val statEffectCanvas = this.statEffectCanvas.also {
            it.width = characterImage.width
            it.height = characterImage.height
        }
        val statEffectContext = statEffectCanvas.getContext("2d") as CanvasRenderingContext2D
        val effectImage = Resources.Images.statUpEffect

        //  エフェクト画像をキャラクタ画像でくり抜くようにして描画する。
        val effectOffsetY = effectImage.height / 2.0 * -1.0 * progress
        val alpha = ((0.5 - abs(progress - 0.5)) * 4).coerceIn(minimumValue = 0.0, maximumValue = 1.0)
        statEffectContext.globalAlpha = alpha

        //  キャラクタ画像の色をエフェクト画像の色にするための事前描画を行う。
        statEffectContext.drawImage(effectImage, 0.0, effectOffsetY)

        //  切り抜きの型にするためのキャラクタ画像を描画する。
        statEffectContext.globalCompositeOperation = "destination-in"
        statEffectContext.drawImage(characterImage, 0.0, 0.0)

        //  その上からエフェクト画像を描画する。
        statEffectContext.drawImage(effectImage, 0.0, effectOffsetY)
        statEffectContext.globalAlpha = 1.0

        //  元のCanvasに描画する。
        context.drawImage(statEffectCanvas, x, y)
    }

    //  能力下降エフェクトを描画する。
    private fun renderStatDownEffect(context: CanvasRenderingContext2D, characterImage: Image, x: Double, y: Double) {
        val progress = this.statDownEffectProgress ?: return

        //  合成描画用のCanvasを取得する。
        val statEffectCanvas = this.statEffectCanvas.also {
            it.width = characterImage.width
            it.height = characterImage.height
        }
        val statEffectContext = statEffectCanvas.getContext("2d") as CanvasRenderingContext2D
        val effectImage = Resources.Images.statDownEffect

        //  エフェクト画像をキャラクタ画像でくり抜くようにして描画する。
        val effectOffsetY = effectImage.height / 2.0 * (progress - 1.0)
        val alpha = ((0.5 - abs(progress - 0.5)) * 4).coerceIn(minimumValue = 0.0, maximumValue = 1.0)
        statEffectContext.globalAlpha = alpha

        //  キャラクタ画像の色をエフェクト画像の色にするための事前描画を行う。
        statEffectContext.drawImage(effectImage, 0.0, effectOffsetY)

        //  切り抜きの型にするためのキャラクタ画像を描画する。
        statEffectContext.globalCompositeOperation = "destination-in"
        statEffectContext.drawImage(characterImage, 0.0, 0.0)

        //  その上からエフェクト画像を描画する。
        statEffectContext.drawImage(effectImage, 0.0, effectOffsetY)
        statEffectContext.globalAlpha = 1.0

        //  元のCanvasに描画する。
        context.drawImage(statEffectCanvas, x, y)
    }

    //  持ち物エフェクトを描画する。
    private fun renderHeldItemEffect(context: CanvasRenderingContext2D, characterImage: Image, cx: Double, cy: Double) {
        //  キャラクタ画像頂点からキャラクタ画像中心に向かって、カーブを描きながら左右2つの白い光を描画させる。
        //  その後、キャラクタ画像中心から波紋のように外側に向かって円を走らせるように描画する。
        val progress = this.heldItemEffectProgress ?: return
        val particleEffectRatio = 0.8
        val particleProgress = (progress * (1.0 / particleEffectRatio)).coerceAtMost(maximumValue = 1.0)
        val rippleProgress = (progress - 0.8).coerceAtLeast(minimumValue = 0.0) / (1.0 - 0.8)

        //  X座標はsin関数を使って左右に広がるようにカーブを描くような動きにする。
        val offsetX = sin(particleProgress * PI) * characterImage.width / 2
        val leftX = cx - offsetX
        val rightX = cx + offsetX

        //  Y座標は等速で上から下へ移動させる動きにする。
        val initialY = cy - characterImage.height / 2
        val y = initialY + (cy - initialY) * particleProgress

        //  白い光の半径は徐々に大きくなり、アニメーション折り返し地点から徐々に小さくなるようにする。
        val radiusDiff = HELD_ITEM_EFFECT_MAX_RADIUS - HELD_ITEM_EFFECT_MIN_RADIUS
        val radius = HELD_ITEM_EFFECT_MIN_RADIUS + radiusDiff * (1.0 - abs(particleProgress * 2 - 1.0))

        //  2つの白い光を描画する。
        if (particleProgress != 1.0) {
            context.beginPath()
            context.fillStyle = "#FFFFFF"
            context.arc(leftX, y, radius, 0.0, PI * 2)
            context.arc(rightX, y, radius, 0.0, PI * 2)
            context.fill()
        }

        //  波紋を描画する。
        val widthDiff = HELD_ITEM_RIPPLE_MIN_WIDTH - HELD_ITEM_RIPPLE_MAX_WIDTH
        val rippleEffectWidth = HELD_ITEM_RIPPLE_MAX_WIDTH + widthDiff * rippleProgress
        val rippleRadius = characterImage.width / 2.0 * rippleProgress
        context.beginPath()
        context.strokeStyle = "#FFFFFF"
        context.lineWidth = rippleEffectWidth
        context.arc(cx, cy, rippleRadius, 0.0, PI * 2)
        context.stroke()
    }
}
