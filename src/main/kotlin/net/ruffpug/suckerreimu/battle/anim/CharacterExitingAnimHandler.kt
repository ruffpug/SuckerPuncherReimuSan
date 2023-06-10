package net.ruffpug.suckerreimu.battle.anim

import net.ruffpug.suckerreimu.battle.CharacterSide
import net.ruffpug.suckerreimu.battle.components.BattleSceneComponents

/**
 * キャラクタ退出アニメのアニメーションハンドラ
 */
internal object CharacterExitingAnimHandler : BattleAnimHandler {

    override suspend fun handle(anim: BattleAnim, components: BattleSceneComponents) {
        require(anim is BattleAnim.CharacterExitingAnim)

        //  指定されたキャラクタの退出アニメーションを再生する。
        when (anim.side) {
            CharacterSide.OWN -> components.reimu.exit(anim.durationInMs)
            CharacterSide.OPPONENT -> components.marisa.exit(anim.durationInMs)
        }
    }
}
