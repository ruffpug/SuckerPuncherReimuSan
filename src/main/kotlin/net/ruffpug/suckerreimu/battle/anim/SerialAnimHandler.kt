package net.ruffpug.suckerreimu.battle.anim

import net.ruffpug.suckerreimu.battle.components.BattleSceneComponents

/**
 * 直列アニメーションのアニメーションハンドラ
 */
internal object SerialAnimHandler : BattleAnimHandler {

    override suspend fun handle(anim: BattleAnim, components: BattleSceneComponents) {
        require(anim is BattleAnim.Serial)

        //  子アニメを直列で実行していく。
        for (child in anim.animations) {
            BattleAnimHandler.of(child).handle(child, components)
        }
    }
}
