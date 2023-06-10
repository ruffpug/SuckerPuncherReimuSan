package net.ruffpug.suckerreimu.battle

/**
 * 戦況のスナップショット
 */
internal data class BattleStatusSnapshot(

    /**
     * れーむさんの技リスト
     */
    val reimuMoves: List<MoveSnapshot>,

    /**
     * まりさちゃんの特攻のランク
     */
    val marisaCRank: Int,

    /**
     * 対戦結果
     */
    val result: BattleResult,
) {
    /**
     * 技PPを消費させる。
     */
    fun consumeMovePp(kind: MoveKind): BattleStatusSnapshot {
        val updatedMoves = this.reimuMoves.map { move ->
            if (move.moveKind != kind) move
            else move.copy(remainingPp = move.remainingPp - 1)
        }

        return this.copy(reimuMoves = updatedMoves)
    }

    /**
     * まりさちゃんの特攻のランクをぐーんと上げる。
     */
    fun raiseMarisaCStatBy2(): BattleStatusSnapshot {
        val updatedRank = (this.marisaCRank + 2).coerceAtMost(MAX_RANK)
        return this.copy(marisaCRank = updatedRank)
    }

    /**
     * 対戦結果
     */
    enum class BattleResult {

        /**
         * 試合継続中
         */
        ONGOING,

        /**
         * 勝利
         */
        WIN,

        /**
         * 敗北
         */
        LOSE,
    }

    companion object {

        //  能力変化のランクの最高値
        private const val MAX_RANK: Int = 6

        /**
         * 初期状態
         */
        val INITIAL: BattleStatusSnapshot = BattleStatusSnapshot(
            reimuMoves = listOf(
                MoveSnapshot(moveKind = MoveKind.FANTASY_SEAL, remainingPp = 8, maxPp = 8),
                MoveSnapshot(moveKind = MoveKind.SUCKER_PUNCH, remainingPp = 8, maxPp = 8),
            ),
            marisaCRank = 0,
            result = BattleResult.ONGOING,
        )
    }
}
