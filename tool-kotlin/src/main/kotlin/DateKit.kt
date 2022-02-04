import TimeLineData.DateType
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.function.Function
import java.util.function.UnaryOperator
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * @author yunkuangao
 */
object DateKit {

    /**
     * get current date, ex: 2021-08-29
     */
    @JvmStatic
    fun nowStr(): String {
        return LocalDate.now().toString()
    }

    /**
     * get date to String, ex: 2021-08-29
     */
    fun getStr(date: LocalDateTime): String {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    /**
     * 获取时间类型对应的时间字符串
     *
     * @param date 时间
     * @param dateType 时间类型
     */
    fun getStr(date: LocalDateTime, dateType: DateType): String {
        val dtf: DateTimeFormatter = when (dateType) {
            DateType.MONTH -> DateTimeFormatter.ofPattern("yyyy-MM")
            DateType.YEAR -> DateTimeFormatter.ofPattern("yyyy")
            DateType.DAY -> DateTimeFormatter.ofPattern("yyyy-MM-dd")
            DateType.HOUR -> DateTimeFormatter.ofPattern("yyyy-MM-dd HH")
        }
        return dtf.format(date)
    }

    /**
     * get timeline template
     */
    fun getTimeLineTemplate(
        start: LocalDateTime,
        end: LocalDateTime,
        dateType: DateType = DateType.YEAR,
        init: BigDecimal = BigDecimal.ZERO,
    ): Map<String, BigDecimal> {
        val adder: UnaryOperator<LocalDateTime>
        val maxSize: Long
        val key = Function { it: LocalDateTime -> getStr(it, dateType) }
        when (dateType) {
            DateType.DAY -> {
                adder = UnaryOperator { LocalDateTime: LocalDateTime -> LocalDateTime.plusDays(1) }
                maxSize = ChronoUnit.DAYS.between(start, end) + 1
            }
            DateType.HOUR -> {
                adder = UnaryOperator { LocalDateTime: LocalDateTime -> LocalDateTime.plusHours(1) }
                maxSize = ChronoUnit.HOURS.between(start, end) + 1
            }
            DateType.YEAR -> {
                adder = UnaryOperator { LocalDateTime: LocalDateTime -> LocalDateTime.plusYears(1) }
                maxSize = ChronoUnit.YEARS.between(start, end) + 1
            }
            DateType.MONTH -> {
                adder = UnaryOperator { LocalDateTime: LocalDateTime -> LocalDateTime.plusMonths(1) }
                maxSize = ChronoUnit.MONTHS.between(start, end) + 1
            }
        }
        return getTimeLineTemplate(start, adder, maxSize, key, init)
    }

    /**
     * get timeline template, core method
     */
    private fun getTimeLineTemplate(
        start: LocalDateTime,
        adder: UnaryOperator<LocalDateTime>,
        maxSize: Long,
        key: Function<LocalDateTime, String>,
        init: BigDecimal = BigDecimal.ZERO,
    ): Map<String, BigDecimal> = Stream.iterate(start, adder)
        .limit(maxSize)
        .map(key)
        .collect(Collectors.toMap({ obj: String -> obj }, { init }))

}