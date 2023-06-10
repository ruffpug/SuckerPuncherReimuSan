package net.ruffpug.suckerreimu.battle

/**
 * 対戦の判定ロジック
 */
internal object BattleJudgement {

    /**
     * 試合状況を判定する。
     */
    fun judge(status: BattleStatusSnapshot, reimuMove: MoveKind, marisaMove: MoveKind): Pattern {
        return when {

            //  れーむさんがむそうふういんを選択し、まりさちゃんがマスタースパークを選択した場合 (れーむさんが負ける場合)
            reimuMove == MoveKind.FANTASY_SEAL && marisaMove == MoveKind.MASTER_SPARK -> {
                //  まりさちゃんが先にマスタースパークを打ち、れーむさんが負けるパターン
                val updatedStatus = status.copy(result = BattleStatusSnapshot.BattleResult.LOSE)
                Pattern.Lose(status, updatedStatus)
            }

            //  れーむさんがむそうふういんを選択し、まりさちゃんがわるだくみを選択した場合 (れーむさんが勝つ場合)
            reimuMove == MoveKind.FANTASY_SEAL && marisaMove == MoveKind.NASTY_PLOT -> {
                //  まりさちゃんがわるだくみを積むも、れーむさんがむそうふういんを打ち、れーむさんが勝つパターン
                val updatedStatus = status.copy(result = BattleStatusSnapshot.BattleResult.WIN)
                    .raiseMarisaCStatBy2()
                    .consumeMovePp(MoveKind.FANTASY_SEAL)
                Pattern.Win.FantasySealPassed(status, updatedStatus)
            }

            //  れーむさんがふいうちを選択し、まりさちゃんがマスタースパークを選択した場合 (れーむさんが勝つ場合)
            reimuMove == MoveKind.SUCKER_PUNCH && marisaMove == MoveKind.MASTER_SPARK -> {
                //  れーむさんがふいうちを打ち、れーむさんが勝つパターン
                val updatedStatus =
                    status.copy(result = BattleStatusSnapshot.BattleResult.WIN).consumeMovePp(MoveKind.SUCKER_PUNCH)
                Pattern.Win.SuckerPunchPassed(status, updatedStatus)
            }

            //  れーむさんがふいうちを選択し、まりさちゃんがわるだくみを選択した場合 (ふいうちが透かされて試合が続行する場合)
            reimuMove == MoveKind.SUCKER_PUNCH && marisaMove == MoveKind.NASTY_PLOT -> {
                //  れーむさんのふいうちが失敗し、まりさちゃんがわるだくみを積み、試合が継続するパターン
                val updatedStatus = status.consumeMovePp(MoveKind.SUCKER_PUNCH).raiseMarisaCStatBy2()
                Pattern.Continue(status, updatedStatus)
            }

            else -> throw IllegalStateException()
        }
    }

    /**
     * 試合状況のパターン
     */
    sealed interface Pattern {

        /**
         * 更新前の戦況
         */
        val beforeStatus: BattleStatusSnapshot

        /**
         * 更新後の戦況
         */
        val updatedStatus: BattleStatusSnapshot

        /**
         * れーむさんが勝つパターン
         */
        sealed interface Win : Pattern {

            /**
             * ふいうちが通って勝つパターン
             */
            data class SuckerPunchPassed(
                override val beforeStatus: BattleStatusSnapshot,
                override val updatedStatus: BattleStatusSnapshot,
            ) : Win

            /**
             * むそうふういんが通って勝つパターン
             */
            data class FantasySealPassed(
                override val beforeStatus: BattleStatusSnapshot,
                override val updatedStatus: BattleStatusSnapshot,
            ) : Win {

                /**
                 * まりさちゃんが特攻ランクを上げることができるかどうか
                 */
                val canMarisaRaiseCRank: Boolean get() = this.beforeStatus.marisaCRank != this.updatedStatus.marisaCRank
            }
        }

        /**
         * れーむさんが負けるパターン
         */
        data class Lose(
            override val beforeStatus: BattleStatusSnapshot,
            override val updatedStatus: BattleStatusSnapshot,
        ) : Pattern

        /**
         * 試合が継続するパターン
         */
        data class Continue(
            override val beforeStatus: BattleStatusSnapshot,
            override val updatedStatus: BattleStatusSnapshot,
        ) : Pattern {

            /**
             * まりさちゃんが特攻ランクを上げることができるかどうか
             */
            val canMarisaRaiseCRank: Boolean get() = this.beforeStatus.marisaCRank != this.updatedStatus.marisaCRank
        }
    }
}
