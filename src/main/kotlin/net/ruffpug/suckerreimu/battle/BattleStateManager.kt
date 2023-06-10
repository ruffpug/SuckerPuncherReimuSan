package net.ruffpug.suckerreimu.battle

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.ruffpug.suckerreimu.Logger
import net.ruffpug.suckerreimu.result.ResultSceneArgs
import kotlin.random.Random

/**
 * 対戦シーンの状態管理ロジック
 */
internal class BattleStateManager(private val onBattleFinished: (args: ResultSceneArgs) -> Unit) {

    //  NPCの技選択ロジック
    private val selector: NpcMoveSelector

    //  対戦シーンの状態
    private val _state = MutableStateFlow(BattleScenario.createInitialState())

    /**
     * 対戦シーンの状態
     */
    val state: StateFlow<BattleSceneState> = this._state.asStateFlow()

    init {
        val random = Random.Default
        this.selector = NpcMoveSelector(random)
    }

    /**
     * 状態が終了したとき。
     */
    fun onStateEnded(result: BattleSceneStateResult) {
        Logger.d { "対戦シーン 状態終了: $result" }

        when (result) {

            //  アニメーション再生中状態が終了した場合
            is BattleSceneStateResult.PlayingAnimations -> {
                val state = result.state
                when (val turn = state.turn) {

                    //  初期状態だった場合
                    is BattleTurn.InitialState -> {
                        //  最初のターン (自動操作) のアニメーションを再生させる。
                        this._state.value = BattleScenario.createFirstTurnState()
                    }

                    //  最初のターンだった場合
                    is BattleTurn.FirstTurn -> {
                        //  次のターンの技選択を行わせる。
                        this._state.value = BattleSceneState.SelectingMoves(turn = turn.next, status = state.status)
                    }

                    //  それ以降のターンだった場合
                    is BattleTurn.PlayableTurn -> {
                        //  決着がついている場合
                        if (state.status.result != BattleStatusSnapshot.BattleResult.ONGOING) {
                            //  結果シーンに遷移させる。
                            val args =
                                ResultSceneArgs(state.status.result == BattleStatusSnapshot.BattleResult.WIN, turn.turn)
                            this.onBattleFinished.invoke(args)
                        }

                        //  決着がついていない場合
                        else {
                            //  次のターンの技選択を行わせる。
                            this._state.value = BattleSceneState.SelectingMoves(turn = turn.next, status = state.status)
                        }
                    }
                }
            }

            //  技選択中状態が終了した場合
            is BattleSceneStateResult.SelectingMoves -> {
                val state = result.state

                //  まりさちゃんの技を選択する。
                val reimuMove = result.selectedMove
                val marisaMove = this.selector.chooseMarisaMove(state.turn, reimuMove)
                Logger.v { "対戦シーン 技選択結果 れーむさん=$reimuMove, まりさちゃん=$marisaMove" }

                //  選択された技に応じてそのターンの戦況を更新する。
                val pattern = BattleJudgement.judge(state.status, reimuMove, marisaMove)
                this._state.value = BattleScenario.createBattleAnimState(state.turn, pattern)
            }
        }
    }
}
