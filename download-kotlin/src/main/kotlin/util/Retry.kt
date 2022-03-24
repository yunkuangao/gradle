package util

import io.ktor.utils.io.errors.*
import kotlinx.coroutines.delay
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

/**
 * 重试机制
 *
 * @param times 重试次数
 * @param initialDelay 初始化延时
 * @param maxDelay 最大延时
 * @param factor 因子
 * @param block 执行代码
 */
suspend fun <T> retryIO(
    times: Int = Int.MAX_VALUE,
    initialDelay: Long = 100,
    maxDelay: Long = 1000,
    factor: Double = 2.0,
    block: suspend () -> T,
): T {
    var currentDelay = initialDelay
    repeat(times - 1) {
        try {
            return block()
        } catch (e: IOException) {
            // 这里可能超时 不管它
//            logger.error(e.message)
            logger.debug { e.printStackTrace() }
        }
        delay(currentDelay)
        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
    }
    return block()
}