import DateKit.getStr
import TimeLineData.DateType
import TimeLineData.Pojo
import org.junit.Assert
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

internal class TimeLineDataTest {

    @Test
    fun getData() {
        val dataList: List<Pojo> = listOf(
            Pojo(date = LocalDateTime.of(2021, 8, 1, 1, 1), value = "1"),
            Pojo(date = LocalDateTime.of(2021, 8, 1, 1, 1), value = "2"),
            Pojo(date = LocalDateTime.of(2021, 8, 1, 1, 1), value = "3"),
            Pojo(date = LocalDateTime.of(2021, 8, 1, 1, 1), value = "3"),
        )

        val actual = TimeLineData.Builder<Pojo>()
            .dataList(dataList)
            .k {
                getStr(
                    LocalDateTime.ofInstant(it.date.toInstant(ZoneOffset.of("+8")), ZoneId.systemDefault()),
                    DateType.MONTH
                )
            }
            .v { BigDecimal.valueOf(it.value.toDouble()) }
            .start(LocalDateTime.of(2020, 8, 1, 1, 1))
            .end(LocalDateTime.of(2021, 8, 1, 1, 1))
            .build()
            .data

        val expect =
            "{2020-08=0, 2020-09=0, 2020-10=0, 2020-11=0, 2020-12=0, 2021-01=0, 2021-02=0, 2021-03=0, 2021-04=0, 2021-05=0, 2021-06=0, 2021-07=0, 2021-08=9.0}"
        Assert.assertEquals(expect, actual.toString())
    }
}