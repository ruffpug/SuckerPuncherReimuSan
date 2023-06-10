package net.ruffpug.suckerreimu.battle

import kotlin.random.Random

/**
 * NPCの技選択ロジック
 */
internal class NpcMoveSelector(private val random: Random) {

    /**
     * まりさちゃんの技を選択する。
     */
    fun chooseMarisaMove(turn: BattleTurn.PlayableTurn, reimuMove: MoveKind): MoveKind {
        //  2ターン目の場合
        if (turn.turn == 2) {
            return when (reimuMove) {

                //  れーむさんがむそうふういんを選んでいた場合、まりさちゃんはマスタースパークを選んで勝利する。
                MoveKind.FANTASY_SEAL -> MoveKind.MASTER_SPARK

                //  れーむさんがふいうちを選んでいた場合、まりさちゃんはわるだくみを選んで対戦を続行させる。
                MoveKind.SUCKER_PUNCH -> MoveKind.NASTY_PLOT

                else -> throw IllegalStateException()
            }
        }

        //  10ターン目の場合
        if (turn.turn == 10) {
            //  仮にれーむさんがふいうちのPPを最大まで増やしていたとしても、このターンにはPPが切れていると考えられるため、
            //  絶対にまりさちゃんが勝てるであろうマスタースパークを選択する。
            return MoveKind.MASTER_SPARK
        }

        //  乱数で2択から選択させる。
        return when (this.random.nextBoolean()) {
            true -> MoveKind.MASTER_SPARK
            false -> MoveKind.NASTY_PLOT
        }
    }
}
