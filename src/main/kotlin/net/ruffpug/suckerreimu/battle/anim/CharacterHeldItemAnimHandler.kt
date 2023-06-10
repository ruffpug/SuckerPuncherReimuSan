package net.ruffpug.suckerreimu.battle.anim

import net.ruffpug.suckerreimu.battle.CharacterSide
import net.ruffpug.suckerreimu.battle.components.BattleSceneComponents

/**
 * キャラクタ持ち物アニメのアニメーションハンドラ
 */
internal object CharacterHeldItemAnimHandler : BattleAnimHandler {

    override suspend fun handle(anim: BattleAnim, components: BattleSceneComponents) {
        require(anim is BattleAnim.CharacterHeldItemAnim)

        //  指定されたキャラクタの持ち物アニメーションを再生する。
        when (anim.side) {
            CharacterSide.OWN -> components.reimu.playHeldItemEffect(anim.durationInMs)
            CharacterSide.OPPONENT -> components.marisa.playHeldItemEffect(anim.durationInMs)
        }
    }
}
