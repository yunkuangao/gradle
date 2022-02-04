import org.junit.Assert
import org.junit.Test
import java.time.LocalDateTime

internal class DateKitTest {
    @Test
    fun getTimeLineTemplate() {
        val actual = DateKit.getTimeLineTemplate(
            LocalDateTime.of(2020, 3, 3, 12, 12),
            LocalDateTime.of(2021, 3, 3, 12, 12),
        )
        val expect = "{2021=0, 2020=0}"
        Assert.assertEquals(expect, actual.toString())
    }
}