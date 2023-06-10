package net.ruffpug.suckerreimu.battle

/**
 * 対戦シーン状態の結果
 */
internal sealed interface BattleSceneStateResult {
    val state: BattleSceneState

    /**
     * アニメーション再生中状態の結果
     */
    data class PlayingAnimations(
        override val state: BattleSceneState.PlayingAnimations
    ) : BattleSceneStateResult

    /**
     * 技選択中状態の結果
     */
    data class SelectingMoves(
        override val state: BattleSceneState.SelectingMoves,
        val selectedMove: MoveKind,
    ) : BattleSceneStateResult
}
