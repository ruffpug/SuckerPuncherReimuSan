package net.ruffpug.suckerreimu.battle.components

import net.ruffpug.suckerreimu.TouchEvent
import net.ruffpug.suckerreimu.battle.CharacterSide
import org.w3c.dom.CanvasRenderingContext2D

/**
 * 対戦シーンのコンポーネント群
 */
internal class BattleSceneComponents {

    /**
     * 対戦背景
     */
    val battleBackground: BattleBackground = BattleBackground()

    /**
     * メッセージフレーム
     */
    val messageFrame: MessageFrame = MessageFrame()

    /**
     * 技メニュー
     */
    val moveMenu: MoveMenu = MoveMenu()

    /**
     * れーむさん (自分側)
     */
    val reimu: Character = Character(side = CharacterSide.OWN)

    /**
     * まりさちゃん (相手側)
     */
    val marisa: Character = Character(side = CharacterSide.OPPONENT)

    /**
     * れーむさんのステータスバー
     */
    val reimuStatusBar: CharacterStatusBar = CharacterStatusBar(side = CharacterSide.OWN)

    /**
     * まりさちゃんのステータスバー
     */
    val marisaStatusBar: CharacterStatusBar = CharacterStatusBar(side = CharacterSide.OPPONENT)

    /**
     * れーむさんの特性ポップアップ
     */
    val reimuAbilityPopup: AbilityPopup = AbilityPopup(side = CharacterSide.OWN)

    /**
     * まりささんの特性ポップアップ
     */
    val marisaAbilityPopup: AbilityPopup = AbilityPopup(side = CharacterSide.OPPONENT)

    /**
     * 描画処理を行う。
     */
    fun render(context: CanvasRenderingContext2D) {
        //  コンポーネントの描画処理を呼び出していく。
        this.battleBackground.render(context)
        this.reimu.render(context)
        this.marisa.render(context)
        this.reimuStatusBar.render(context)
        this.marisaStatusBar.render(context)
        this.reimuAbilityPopup.render(context)
        this.marisaAbilityPopup.render(context)
        this.messageFrame.render(context)
        this.moveMenu.render(context)
    }

    /**
     * タッチイベントが発生したとき。
     */
    fun onTouchEventOccurred(event: TouchEvent) {
        //  コンポーネントのイベントコールバックを呼び出していく。
        this.moveMenu.onTouchEventOccurred(event)
    }
}
