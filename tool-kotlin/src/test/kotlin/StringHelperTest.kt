import org.junit.Assert
import org.junit.Test

internal class StringHelperTest {
    @Test
    fun center() {
        Assert.assertEquals("   test   ", StringHelper.center("test", 10))
        Assert.assertEquals("111test111", StringHelper.center("test", 10, '1'))
    }
}