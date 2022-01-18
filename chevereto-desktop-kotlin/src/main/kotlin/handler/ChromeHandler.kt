package handler

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions

/**
 * 获取chrome的驱动
 *
 * @param options 浏览器选项
 */
fun chrome(options: ChromeOptions = chromeOption(defaultOptions)): WebDriver {
    WebDriverManager.chromedriver().setup()
    return ChromeDriver(options)
}

val defaultOptions = listOf("--headless")

/**
 * 设置chrome的选项
 *
 * @param options chrome选项
 */
fun chromeOption(options: List<String>): ChromeOptions {
    val chromeOptions = ChromeOptions()
    if (options.isEmpty()) chromeOptions.addArguments(defaultOptions)
    else chromeOptions.addArguments(options)
    return chromeOptions
}
