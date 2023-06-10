package net.ruffpug.suckerreimu.battle.anim

import net.ruffpug.suckerreimu.battle.CharacterSide
import net.ruffpug.suckerreimu.battle.components.BattleSceneComponents

/**
 * キャラクタダメージアニメのアニメーションハンドラ
 */
internal object CharacterDamageAnimHandler : BattleAnimHandler {

    override suspend fun handle(anim: BattleAnim, components: BattleSceneComponents) {
        require(anim is BattleAnim.CharacterDamageAnim)

        //  指定されたキャラクタのダメージアニメーションを再生する。
        when (anim.side) {
            CharacterSide.OWN -> components.reimu.playDamageAnim(anim.blinkCount, anim.durationInMs)
            CharacterSide.OPPONENT -> components.marisa.playDamageAnim(anim.blinkCount, anim.durationInMs)
        }
    }
}
