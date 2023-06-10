package net.ruffpug.suckerreimu

import org.w3c.dom.Image
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * リソース
 */
internal object Resources {

    /**
     * 画像リソース
     */
    object Images {

        /**
         * 背景
         */
        lateinit var background: Image; private set

        /**
         * れーむさんキャラクタ画像
         */
        lateinit var characterReimu: Image; private set

        /**
         * まりさちゃんキャラクタ画像
         */
        lateinit var characterMarisa: Image; private set

        /**
         * 能力上昇エフェクト画像
         */
        lateinit var statUpEffect: Image; private set

        /**
         * 能力下降エフェクト画像
         */
        lateinit var statDownEffect: Image; private set

        /**
         * 結果シーン画像 (勝ち)
         */
        lateinit var winResultScreen: Image; private set

        /**
         * 結果シーン画像 (負け)
         */
        lateinit var loseResultScreen: Image; private set

        /**
         * 共有ボタン画像
         */
        lateinit var shareButton: Image; private set

        suspend fun load() {
            Logger.v { "画像リソース読み込み 開始" }

            this.background = this.loadImage("img/background.png")
            this.characterReimu = this.loadImage("img/character_reimu.png")
            this.characterMarisa = this.loadImage("img/character_marisa.png")
            this.statUpEffect = this.loadImage("img/stat_up_effect.png")
            this.statDownEffect = this.loadImage("img/stat_down_effect.png")
            this.winResultScreen = this.loadImage("img/win_result_screen.png")
            this.loseResultScreen = this.loadImage("img/lose_result_screen.png")
            this.shareButton = this.loadImage("img/share_button.png")

            Logger.v { "画像リソース読み込み 終了" }
        }

        //  画像を読み込む。
        private suspend fun loadImage(path: String): Image = suspendCoroutine { continuation ->
            val image = Image()

            image.onload = { continuation.resume(image) }
            image.onerror = { _, _, _, _, _ ->
                Logger.e { "画像読み込みエラー: $path" }
                continuation.resumeWithException(Exception("画像読み込みエラー: $path"))
            }

            image.src = path
        }
    }

    /**
     * リソースの読み込みを行う。
     */
    suspend fun load() {
        //  画像リソースの読み込みを行う。
        Images.load()
    }
}
