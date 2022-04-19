package routes

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import models.DatabaseInfo
import mu.KotlinLogging
import poitl.Poitl
import service.DataOperator
import java.io.ByteArrayOutputStream
import java.util.*

/**
 * 生成word文档
 *
 * @return 返回消息提示
 * @author yunkuangao a317526763@gmail.com
 */
fun Route.build() {

    val logger = KotlinLogging.logger {}

    post("/build") {
        val info = call.receive<DatabaseInfo>()
        with(DataOperator.getTableAllColumn(info)) {
            with(Poitl.makeDoc(this)) {
                call.respondBytes {
                    ByteArrayOutputStream().also { outputStream ->
                        Objects.requireNonNull(this).write(outputStream).also {
                            logger.debug("生成文档")
                        }
                    }.toByteArray()
                }
            }
        }
    }

}
