package net.ruffpug.suckerreimu.battle

/**
 * 技データのスナップショット
 */
internal data class MoveSnapshot(

    /**
     * 技の種類
     */
    val moveKind: MoveKind,

    /**
     * 残りPP
     */
    val remainingPp: Int,

    /**
     * 最大PP
     */
    val maxPp: Int
)
