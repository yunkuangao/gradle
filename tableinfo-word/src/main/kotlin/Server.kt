import freemarker.cache.ClassTemplateLoader
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging
import routes.build
import routes.option
import routes.table

lateinit var environments: ApplicationEnvironment

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    val logger = KotlinLogging.logger {}

    environments = environment
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    install(ContentNegotiation) {
        json()
    }

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, "Internal Server Error")
                .also { logger.error { cause.message } }
            throw cause
        }
    }

    routing {
        get("/") {
            call.respond(FreeMarkerContent("index.ftl", null))
        }

        static {
            resources("static")
        }

        option()
        table()
        build()
    }
}
