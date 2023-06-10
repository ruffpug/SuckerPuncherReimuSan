package net.ruffpug.suckerreimu.battle

/**
 * 対戦のターン
 */
internal sealed interface BattleTurn {

    /**
     * 次のターン
     */
    val next: BattleTurn

    /**
     * 初期状態
     */
    object InitialState : BattleTurn {
        override val next: FirstTurn get() = FirstTurn
    }

    /**
     * 最初のターン (自動操作)
     */
    object FirstTurn : BattleTurn {
        override val next: PlayableTurn get() = PlayableTurn(turn = 2)
    }

    /**
     * 操作可能ターン
     */
    data class PlayableTurn(val turn: Int) : BattleTurn {

        override val next: PlayableTurn get() = this.copy(turn = this.turn + 1)

        init {
            require(this.turn >= 2)
        }
    }
}
