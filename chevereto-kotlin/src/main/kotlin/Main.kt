import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import handler.chrome
import handler.chromeOption
import model.CheveretoInfo
import parse.CheveretoParse

fun main(args: Array<String>) = Chevereto().main(args)

class Chevereto : CliktCommand() {
    val cheveretoUrl by argument()

    override fun run() {
        chevereto(cheveretoUrl)
    }
}

fun chevereto(host: String) {
    val cheveretoParse = CheveretoParse(CheveretoInfo(host), chrome(chromeOption(listOf())))
    cheveretoParse.download()
    cheveretoParse.quit()
}