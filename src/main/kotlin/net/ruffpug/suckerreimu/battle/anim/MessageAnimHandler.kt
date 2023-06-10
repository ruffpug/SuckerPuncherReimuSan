package net.ruffpug.suckerreimu.battle.anim

import net.ruffpug.suckerreimu.battle.components.BattleSceneComponents

/**
 * メッセージ表示のアニメーションハンドラ
 */
internal object MessageAnimHandler : BattleAnimHandler {

    override suspend fun handle(anim: BattleAnim, components: BattleSceneComponents) {
        require(anim is BattleAnim.Message)

        //  メッセージフレームにメッセージを指定期間表示させる。
        components.messageFrame.showMessage(anim.text, anim.durationInMs)
    }
}
