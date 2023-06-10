package net.ruffpug.suckerreimu.battle

/**
 * 技の種類
 */
internal enum class MoveKind(val moveName: String) {

    /**
     * むそうふういん
     */
    FANTASY_SEAL(moveName = "むそうふういん"),

    /**
     * ふいうち
     */
    SUCKER_PUNCH(moveName = "ふいうち"),

    /**
     * マスタースパーク
     */
    MASTER_SPARK(moveName = "マスタースパーク"),

    /**
     * わるだくみ
     */
    NASTY_PLOT(moveName = "わるだくみ"),
}
