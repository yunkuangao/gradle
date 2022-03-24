import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import model.FunkwhaleInfo
import parse.FunkwhaleParse

fun main(args: Array<String>) = Funkwhale().main(args)

class Funkwhale : CliktCommand() {
    val funkwhaleUrl by argument()
    override fun run() {
        funkwhale(funkwhaleUrl)
    }
}

fun funkwhale(host: String) {
    val funkwhaleParse = FunkwhaleParse(
        FunkwhaleInfo(
            url = host,
            username = "yun",
            password = "namesake-chute-aardvark-omissible-icing"
        )
    )
    funkwhaleParse.download()
}
