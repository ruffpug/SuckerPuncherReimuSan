package net.ruffpug.suckerreimu

import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.CoroutineContext

/**
 * Coroutineを起動する。
 */
internal fun launchCoroutine(context: CoroutineContext = Dispatchers.Main.immediate, block: suspend () -> Unit): Job {
    return CoroutineScope(context).launch { block.invoke() }
}

/**
 * アニメーション更新処理を開始する。
 *
 * @param duration アニメーション期間 (ミリ秒)
 * @param onUpdated 更新用コールバック (引数には進捗率 0.0 ~ 1.0 が指定される。)
 * @param onFinished アニメーション終了コールバック
 */
internal fun startAnimUpdater(duration: Long, onUpdated: (Float) -> Unit, onFinished: () -> Unit): AnimCancellation {
    //  アニメーション期間が0ミリ秒の場合
    if (duration == 0L) {
        onUpdated(1.0f)
        onFinished()

        return AnimCancellationImpl()
    }

    //  アニメーション開始段階のタイムスタンプ
    var startingTimestamp: Double? = null

    //  キャンセル用オブジェクト
    val cancellation = AnimCancellationImpl()

    //  アニメーション用のフレームが呼ばれたとき。
    fun onFrameCalled(timestamp: Double) {
        //  初回のタイムスタンプを記録しておく。
        if (startingTimestamp == null) startingTimestamp = timestamp

        //  アニメーションを開始してからの経過時間を求めて、進捗率を算出する。
        val deltaTimeInMs = timestamp.toLong() - startingTimestamp!!.toLong()
        val progress = (deltaTimeInMs.toFloat() / duration).coerceIn(minimumValue = 0.0f, maximumValue = 1.0f)
        val shouldContinue = deltaTimeInMs < duration

        //  更新用コールバックを呼び出して進捗率を通知する。
        onUpdated(progress)

        //  アニメーションが継続している場合
        if (shouldContinue) {
            //  次のアニメーションフレームを予約する。
            cancellation.handleId = window.requestAnimationFrame(::onFrameCalled)
        }

        //  アニメーションが終わった場合
        else {
            //  アニメーション完了を通知して、次のアニメーションフレームの予約を行わないようにする。
            onUpdated(1.0f)
            onFinished()
            cancellation.handleId = null
        }
    }

    //  最初のアニメーションフレームを予約する。
    cancellation.handleId = window.requestAnimationFrame(::onFrameCalled)

    return cancellation
}

/**
 * アニメーション更新処理のループを開始する。
 *
 * @param duration アニメーション期間 (ミリ秒)
 * @param onUpdated 更新用コールバック (引数には進捗率 0.0 ~ 1.0 が指定される。)
 */
internal fun startInfiniteAnimUpdater(duration: Long, onUpdated: (Float) -> Unit): AnimCancellation {
    val wrapper = AnimCancellationWrapper()

    fun callback() {
        val shouldContinue = !wrapper.isCancelled
        if (shouldContinue) wrapper.cancellation = startAnimUpdater(duration, onUpdated, ::callback)
    }
    callback()

    return wrapper
}

/**
 * アニメーション更新処理を開始する。
 *
 * @param duration アニメーション期間 (ミリ秒)
 * @param onUpdated 更新用コールバック (引数には進捗率 0.0 ~ 1.0 が指定される。)
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal suspend fun startAnimUpdaterAsync(duration: Long, onUpdated: (Float) -> Unit) = suspendCancellableCoroutine {
    var animCancellation: AnimCancellation? = null
    animCancellation = startAnimUpdater(
        duration = duration,
        onUpdated = onUpdated,
        onFinished = { it.resume(Unit) { animCancellation?.cancel() } },
    )

    it.invokeOnCancellation { animCancellation.cancel() }
}

/**
 * アニメーションのキャンセル用オブジェクト
 */
internal interface AnimCancellation {

    /**
     * アニメーションのキャンセルを行う。
     */
    fun cancel()
}

//  AnimCancellationの具象実装
private class AnimCancellationImpl : AnimCancellation {

    //  最新のハンドルID (キャンセルすべきハンドルIDがない場合はnull)
    var handleId: Int? = null

    override fun cancel() {
        //  キャンセルすべきハンドルIDがある場合、キャンセル要求をかける。
        val id = this.handleId ?: return
        window.cancelAnimationFrame(id)
        this.handleId = null
    }
}

//  AnimCancellationのラッパ
private class AnimCancellationWrapper : AnimCancellation {

    //  キャンセル済みかどうか
    var isCancelled: Boolean = false

    //  内部のCancellation
    var cancellation: AnimCancellation? = null
        set(value) {
            if (!this.isCancelled) field = value
        }

    override fun cancel() {
        this.isCancelled = true
        this.cancellation?.cancel()
        this.cancellation = null
    }
}
