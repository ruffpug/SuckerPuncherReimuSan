package net.ruffpug.suckerreimu.result

/**
 * 結果シーンの引数
 */
internal data class ResultSceneArgs(

    /**
     * 勝ったかどうか
     */
    val win: Boolean,

    /**
     * ターン数
     */
    val turn: Int,
)
