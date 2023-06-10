package net.ruffpug.suckerreimu

import net.ruffpug.suckerreimu.battle.BattleScene
import net.ruffpug.suckerreimu.result.ResultScene
import net.ruffpug.suckerreimu.result.ResultSceneArgs
import org.w3c.dom.CanvasRenderingContext2D

/**
 * ゲーム
 */
internal class Game {
    companion object {

        /**
         * Canvasの描画横幅
         */
        const val CANVAS_WIDTH: Double = 1280.0

        /**
         * Canvasの描画高さ
         */
        const val CANVAS_HEIGHT: Double = 720.0

        /**
         * 描画時の拡大率
         * (モバイル環境でのぼやけ防止用)
         */
        const val RENDERING_SCALE: Int = 2

        /**
         * ゲームのシングルトンインスタンス
         */
        val INSTANCE: Game by lazy { Game() }
    }

    //  対戦シーン
    private lateinit var battleScene: BattleScene

    //  結果シーン
    private var resultScene: ResultScene? = null

    /**
     * 初期化処理を行う。
     */
    fun init() {
        this.battleScene = BattleScene(this::onBattleFinished)
        this.battleScene.init()
    }

    /**
     * 描画処理を行う。
     */
    fun render(context: CanvasRenderingContext2D) {
        this.battleScene.render(context)
        this.resultScene?.render(context)
    }

    /**
     * タッチイベントが発生したとき。
     */
    fun onTouchEventOccurred(event: TouchEvent) {
        this.battleScene.onTouchEventOccurred(event)
        this.resultScene?.onTouchEventOccurred(event)
    }

    //  対戦が終了したとき。
    private fun onBattleFinished(args: ResultSceneArgs) {
        Logger.d { "対戦終了: $args" }

        //  結果シーンの初期化を行う。
        val scene = ResultScene()
        scene.init(args)
        this.resultScene = scene
    }
}
