package com.yunkuangao

import java.io.FileReader
import javax.script.ScriptEngineManager

fun main() {
    val factory = ScriptEngineManager().getEngineByExtension("kts").factory
    assert(factory != null)
    println(factory)
//    FileReader("C:\\Users\\a3175\\IdeaProjects\\yunkuangao\\spi-kotlin\\src\\main\\kotlin\\com\\yunkuangao\\Test.kts").use {
//        engine.eval((it))
//    }
}
