package net.ruffpug.suckerreimu

/**
 * ログ
 */
@Suppress("unused")
internal object Logger {

    /**
     * ログ出力が有効かどうか
     */
    var isEnabled: Boolean = false

    /**
     * VERBOSEログを出力する。
     */
    inline fun v(throwable: Throwable? = null, crossinline messageProvider: () -> String) {
        if (this.isEnabled) {
            val message =
                if (throwable == null) "[VERBOSE] ${messageProvider()}"
                else "[VERBOSE] ${messageProvider()}\n$throwable\n${throwable.message}"
            console.log(message)
        }
    }

    /**
     * DEBUGログを出力する。
     */
    inline fun d(throwable: Throwable? = null, crossinline messageProvider: () -> String) {
        if (this.isEnabled) {
            val message =
                if (throwable == null) "[DEBUG] ${messageProvider()}"
                else "[DEBUG] ${messageProvider()}\n$throwable\n${throwable.message}"
            console.log(message)
        }
    }

    /**
     * INFOログを出力する。
     */
    inline fun i(throwable: Throwable? = null, crossinline messageProvider: () -> String) {
        if (this.isEnabled) {
            val message =
                if (throwable == null) "[INFO] ${messageProvider()}"
                else "[INFO] ${messageProvider()}\n$throwable\n${throwable.message}"
            console.info(message)
        }
    }

    /**
     * WARNINGログを出力する。
     */
    inline fun w(throwable: Throwable? = null, crossinline messageProvider: () -> String) {
        if (this.isEnabled) {
            val message =
                if (throwable == null) "[WARN] ${messageProvider()}"
                else "[WARN] ${messageProvider()}\n$throwable\n${throwable.message}"
            console.warn(message)
        }
    }

    /**
     * ERRORログを出力する。
     */
    inline fun e(throwable: Throwable? = null, crossinline messageProvider: () -> String) {
        if (this.isEnabled) {
            val message =
                if (throwable == null) "[ERROR] ${messageProvider()}"
                else "[ERROR] ${messageProvider()}\n$throwable\n${throwable.message}"
            console.error(message)
        }
    }

    /**
     * ASSERTログを出力する。
     */
    inline fun wtf(throwable: Throwable? = null, crossinline messageProvider: () -> String) {
        if (this.isEnabled) {
            val message =
                if (throwable == null) "[ASSERT] ${messageProvider()}"
                else "[ASSERT] ${messageProvider()}\n$throwable\n${throwable.message}"
            console.error(message)
        }
    }
}
