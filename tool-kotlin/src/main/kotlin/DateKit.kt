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
 * @apiNote
 */
object DateKit {
    /**
     * @apiNote get current date, ex: 2021-08-29
     */
    @JvmStatic
    fun nowStr(): String {
        return LocalDate.now().toString()
    }

    /**
     * @apiNote get date to String, ex: 2021-08-29
     */
    fun getStr(date: LocalDateTime): String {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    /**
     * @apiNote get date to String by dateType
     */
    @JvmStatic
    fun getStr(date: LocalDateTime, dateType: DateType): String {
        val dtf: DateTimeFormatter = when (dateType) {
            DateType.MONTH -> DateTimeFormatter.ofPattern("yyyy-MM")
            DateType.YEAR -> DateTimeFormatter.ofPattern("yyyy")
            DateType.HOUR -> DateTimeFormatter.ofPattern("yyyy-MM-dd HH")
            DateType.DAY -> DateTimeFormatter.ofPattern("yyyy-MM-dd")
        }
        return dtf.format(date)
    }

    /**
     * @apiNote get timeline template
     */
    fun getTimeLineTemplate(dateType: DateType, start: LocalDateTime, end: LocalDateTime, init: BigDecimal): Map<String, BigDecimal> {
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
        return getTimeLineTemplate(start, init, adder, maxSize, key)
    }

    /**
     * @apiNote get timeline template, core method
     */
    private fun getTimeLineTemplate(
        start: LocalDateTime,
        init: BigDecimal,
        adder: UnaryOperator<LocalDateTime>,
        maxSize: Long,
        key: Function<LocalDateTime, String>,
    ): Map<String, BigDecimal> {
        return Stream.iterate(start, adder)
            .limit(maxSize)
            .map(key)
            .collect(Collectors.toMap({ obj: String -> obj }, { init }))
    }
}