import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.freemarker.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
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
        exception<Throwable> { cause ->
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
