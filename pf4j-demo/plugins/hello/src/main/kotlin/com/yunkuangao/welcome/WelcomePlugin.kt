package com.yunkuangao.welcome

import com.yunkuangao.api.Greeting
import mu.KotlinLogging
import org.pf4j.Extension
import org.pf4j.Plugin
import org.pf4j.PluginWrapper

class WelcomePlugin(wrapper: PluginWrapper) : Plugin(wrapper) {
    override fun start() {
        logger.info("WelcomePlugin.start()")
        logger.info("WelcomePlugin".uppercase())
    }

    override fun stop() {
        logger.info("WelcomePlugin.stop()")
    }

    companion object {
        private val logger = KotlinLogging.logger {}
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


