package com.yunkuangao.demo.welcome

import com.yunkuangao.Extension
import com.yunkuangao.Plugin
import com.yunkuangao.PluginWrapper
import com.yunkuangao.plugindemo.app.Greeting
import mu.KotlinLogging

open class WelcomePlugin(wrapper: PluginWrapper) : Plugin(wrapper) {

    private val logger = KotlinLogging.logger {}

    override fun start() {
        logger.info("WelcomePlugin.start()")
        logger.info("WelcomePlugin".uppercase())
    }

    override fun stop() {
        logger.info("WelcomePlugin.stop()")
    }

    @Extension
    class WelcomeGreeting : Greeting {
        override val greeting: String
            get() = "Welcome"

        override fun greetPerson(person: String): String {
            return "$greeting $person"
        }
    }

}


