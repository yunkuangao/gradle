package constant

import environments
import models.FieldOption

/**
 * 展示列上下文环境
 *
 * @Author yunkuangao a317526763@gmail.com
 * @Version 1.0
 */
object Env {

    val option: FieldOption = FieldOption()

    val importWord: String = environments.config.propertyOrNull("ktor.word.import")?.getString().toString()

    val subModelWord: String = environments.config.propertyOrNull("ktor.word.sub_model")?.getString().toString()

    val exportWord: String = environments.config.propertyOrNull("ktor.word.export")?.getString().toString()
}