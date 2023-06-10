package net.ruffpug.suckerreimu.battle.anim

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import net.ruffpug.suckerreimu.battle.components.BattleSceneComponents

/**
 * 並列アニメーションのアニメーションハンドラ
 */
internal object ParallelAnimHandler : BattleAnimHandler {

    override suspend fun handle(anim: BattleAnim, components: BattleSceneComponents) {
        require(anim is BattleAnim.Parallel)

        coroutineScope {
            val jobs = ArrayList<Deferred<Unit>>(anim.animations.size)

            //  子アニメを並列で実行していく。
            for (child in anim.animations) {
                val job = async { BattleAnimHandler.of(child).handle(child, components) }
                jobs.add(job)
            }

            //  全ての子アニメのハンドリングが終了するまで待機する。
            jobs.awaitAll()
        }
    }
}
