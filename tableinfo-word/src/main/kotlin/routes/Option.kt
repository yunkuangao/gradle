package routes

import com.beust.klaxon.JsonArray
import constant.Env
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging

/**
 * 获取打印列
 */
fun Route.option() {

    val logger = KotlinLogging.logger {}

    get("/option") {
        val option = Env.option.getVariableNameList()
        logger.debug("获取可选项:$option")
        call.respond(JsonArray(option).toJsonString())
    }

}