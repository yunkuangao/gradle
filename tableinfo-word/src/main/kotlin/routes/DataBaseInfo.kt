package routes

import com.beust.klaxon.JsonArray
import io.ktor.application.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import models.DatabaseInfo
import mu.KotlinLogging
import service.DataOperator

/**
 * 获取数据库表名
 *
 * @author yunkuangao
 */
fun Route.table() {

    val logger = KotlinLogging.logger {}

    post("/tableList") {
        val info = call.receive<DatabaseInfo>()
        val tableNameList = DataOperator.getTableName(info)
        call.respond(JsonArray(tableNameList).toJsonString()).run {
            logger.debug { tableNameList.forEach { println("获取数据库表:${it.tableName}") } }
        }
    }

}
