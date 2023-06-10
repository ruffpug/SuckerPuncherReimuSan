package net.ruffpug.suckerreimu.battle

import net.ruffpug.suckerreimu.battle.anim.BattleAnim

/**
 * 対戦シーン状態
 */
internal sealed interface BattleSceneState {

    /**
     * 対戦ターン
     */
    val turn: BattleTurn

    /**
     * 戦況のスナップショット
     */
    val status: BattleStatusSnapshot

    /**
     * アニメーション再生中
     */
    data class PlayingAnimations(
        override val turn: BattleTurn,
        override val status: BattleStatusSnapshot,
        val animQueue: List<BattleAnim>,
    ) : BattleSceneState

    /**
     * 技選択中
     */
    data class SelectingMoves(
        override val turn: BattleTurn.PlayableTurn,
        override val status: BattleStatusSnapshot,
    ) : BattleSceneState
}
