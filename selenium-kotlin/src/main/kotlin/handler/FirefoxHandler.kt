package handler

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions

/**
 * 获取firefox的驱动
 *
 * @param options 浏览器选项
 */
fun firefox(options: FirefoxOptions = firefoxOption(defaultFirefoxOptions)): WebDriver {
    WebDriverManager.firefoxdriver().setup()
    return FirefoxDriver(options)
}

val defaultFirefoxOptions = listOf("--headless")

/**
 * 设置firefox的选项
 *
 * @param options firefox选项
 */
fun firefoxOption(options: List<String>): FirefoxOptions {
    val firefoxOptions = FirefoxOptions()
    if (options.isEmpty()) firefoxOptions.addArguments(defaultFirefoxOptions)
    else firefoxOptions.addArguments(options)
    return firefoxOptions
}
