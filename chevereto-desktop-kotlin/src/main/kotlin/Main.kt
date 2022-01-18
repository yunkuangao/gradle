import handler.chrome
import handler.chromeOption
import model.CheveretoInfo
import parse.CheveretoParse

const val host = "https://chevereto.yunkuangao.com/"

fun main() {
    val cheveretoParse = CheveretoParse(CheveretoInfo(host), chrome(chromeOption(listOf())))

    cheveretoParse.download()

    cheveretoParse.quit()
}