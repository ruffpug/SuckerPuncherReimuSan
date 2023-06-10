package net.ruffpug.suckerreimu

import kotlinx.browser.window

/**
 * モバイル環境かどうか
 */
internal val isMobile: Boolean
    get() {
        val agent = window.navigator.userAgent.lowercase()
        return mobileAgents.any { agent.contains(it) }
    }

//  モバイル環境のユーザエージェント名リスト
private val mobileAgents: List<String> =
    listOf("Android", "webOS", "iPhone", "iPad", "iPod", "BlackBerry", "IEMobile", "Opera Mini").map { it.lowercase() }
