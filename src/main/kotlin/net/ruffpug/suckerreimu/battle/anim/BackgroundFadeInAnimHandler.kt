package net.ruffpug.suckerreimu.battle.anim

import net.ruffpug.suckerreimu.battle.components.BattleSceneComponents

/**
 * 背景のフェードインアニメのアニメーションハンドラ
 */
internal object BackgroundFadeInAnimHandler : BattleAnimHandler {

    override suspend fun handle(anim: BattleAnim, components: BattleSceneComponents) {
        require(anim is BattleAnim.BackgroundFadeInAnim)

        //  背景のフェードインを行う。
        components.battleBackground.fadeIn(anim.durationInMs)
    }
}
