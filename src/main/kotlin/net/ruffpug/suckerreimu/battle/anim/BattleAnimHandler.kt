package net.ruffpug.suckerreimu.battle.anim

import net.ruffpug.suckerreimu.battle.components.BattleSceneComponents

/**
 * 対戦アニメーションのハンドラ
 */
internal interface BattleAnimHandler {

    /**
     * アニメーションをハンドリングする。
     */
    suspend fun handle(anim: BattleAnim, components: BattleSceneComponents)

    companion object {

        /**
         * 指定アニメーションのハンドラを取得する。
         */
        fun of(anim: BattleAnim): BattleAnimHandler {
            return when (anim) {
                is BattleAnim.Serial -> SerialAnimHandler
                is BattleAnim.Parallel -> ParallelAnimHandler
                is BattleAnim.Delay -> DelayAnimHandler
                is BattleAnim.Message -> MessageAnimHandler
                is BattleAnim.BackgroundFadeInAnim -> BackgroundFadeInAnimHandler
                is BattleAnim.CharacterEnteringAnim -> CharacterEnteringAnimHandler
                is BattleAnim.CharacterExitingAnim -> CharacterExitingAnimHandler
                is BattleAnim.CharacterDamageAnim -> CharacterDamageAnimHandler
                is BattleAnim.CharacterStatUpAnim -> CharacterStatUpAnimHandler
                is BattleAnim.CharacterStatDownAnim -> CharacterStatDownAnimHandler
                is BattleAnim.CharacterHeldItemAnim -> CharacterHeldItemAnimHandler
                is BattleAnim.CharacterStatusBarEnteringAnim -> CharacterStatusBarEnteringAnimHandler
                is BattleAnim.CharacterStatusBarExitingAnim -> CharacterStatusBarExitingAnimHandler
                is BattleAnim.CharacterStatusBarHpUpdatingAnim -> CharacterStatusBarHpUpdatingAnimHandler
                is BattleAnim.AbilityPopupAnim -> AbilityPopupAnimHandler
            }
        }
    }
}
