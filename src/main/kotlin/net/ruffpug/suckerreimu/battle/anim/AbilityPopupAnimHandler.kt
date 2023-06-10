package net.ruffpug.suckerreimu.battle.anim

import net.ruffpug.suckerreimu.battle.CharacterSide
import net.ruffpug.suckerreimu.battle.components.BattleSceneComponents

/**
 * 特性ポップアップ表示アニメのアニメーションハンドラ
 */
internal object AbilityPopupAnimHandler : BattleAnimHandler {

    override suspend fun handle(anim: BattleAnim, components: BattleSceneComponents) {
        require(anim is BattleAnim.AbilityPopupAnim)

        //  指定されたキャラクタの特性ポップアップ表示アニメーションを再生する。
        when (anim.side) {
            CharacterSide.OWN -> components.reimuAbilityPopup.showPopup(anim.abilityName, anim.durationInMs)
            CharacterSide.OPPONENT -> components.marisaAbilityPopup.showPopup(anim.abilityName, anim.durationInMs)
        }
    }
}
