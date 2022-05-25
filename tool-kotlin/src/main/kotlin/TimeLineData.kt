import DateKit.getTimeLineTemplate
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.function.Function

/**
 *
 * # use builder mode build timeline data
 *
 * ## example:
 * ``` kotlin
 * List<Pojo> dataList = new ArrayList() {{
 *     add(new Pojo() {{
 *         setDate(new Date());
 *         setValue("1");
 *     }});
 *     add(new Pojo() {{
 *         setDate(Date.from(Instant.now().minusSeconds(3600 * 24 * 1)));
 *         setValue("2");
 *     }});
 *     add(new Pojo() {{
 *         setDate(Date.from(Instant.now().minusSeconds(3600 * 24 * 1)));
 *         setValue("3");
 *     }});
 *     add(new Pojo() {{
 *         setDate(Date.from(Instant.now().minusSeconds(3600 * 24 * 2)));
 *         setValue("3");
 *     }});
 * }};
 *
 * Map<String></String>, BigDecimal> result = new TimeLineData.Builder<Pojo>()
 *     .dataList(dataList)
 *     .k(it -> DateKit.getStr(LocalDateTime.ofInstant(it.getDate().toInstant(), ZoneId.systemDefault()), DateType.MONTH))
 *     .v(it -> BigDecimal.valueOf(Double.parseDouble(it.getValue())))
 *     .start(LocalDateTime.of(2020, 8, 1, 1, 1))
 *     .end(LocalDateTime.of(2021, 8, 1, 1, 1))
 *     .build()
 *     .getData();
 * ```
 *
 * @author yunkuangao
 * @apiNote build time-line data
 */
open class TimeLineData<V> private constructor() {
    private var dataList: List<V> = listOf()
    private var perFilter: Function<V, Boolean> = Function { true }
    private var postFilter: Function<Map<String, BigDecimal>, Boolean> = Function { true }
    private var k: Function<V, String> = Function { "" }
    private var v: Function<V, BigDecimal> = Function { BigDecimal.ZERO }
    private var dateType: DateType = DateType.YEAR
    private var start: LocalDateTime = LocalDateTime.now()
    private var end: LocalDateTime = LocalDateTime.now()
    private var init: BigDecimal = BigDecimal.ZERO
    protected fun setDataList(dataList: List<V>) {
        this.dataList = dataList
    }

    protected fun setPerFilter(perFilter: Function<V, Boolean>) {
        this.perFilter = perFilter
    }

    protected fun setPostFilter(postFilter: Function<Map<String, BigDecimal>, Boolean>) {
        this.postFilter = postFilter
    }

    protected fun setK(k: Function<V, String>) {
        this.k = k
    }

    protected fun setV(v: Function<V, BigDecimal>) {
        this.v = v
    }

    protected fun setDateType(dateType: DateType) {
        this.dateType = dateType
    }

    protected fun setStart(start: LocalDateTime) {
        this.start = start
    }

    protected fun setEnd(end: LocalDateTime) {
        this.end = end
    }

    protected fun setInit(init: BigDecimal) {
        this.init = init
    }

    /**
     * @apiNote get timeline data
     * @return
     */
    val data: Map<String, BigDecimal>
        get() {
            val temp: MutableMap<String, BigDecimal> = getTimeLineTemplate(start, end, dateType, init).toMutableMap()
            temp.putAll(
                dataList
                    .filter { perFilter.apply(it) }
                    .map { mapOf(Pair(k.apply(it), v.apply(it))) }
                    .filter { postFilter.apply(it) }
                    .reduce { older, newer -> older.add(newer) }
            )
            return temp.toSortedMap()
        }

    private fun Map<String, BigDecimal>.add(other: Map<String, BigDecimal>): Map<String, BigDecimal> {
        val temp = this.toMutableMap()
        other.map { (k, v) -> temp.merge(k, v) { older: BigDecimal, newer: BigDecimal -> older.add(newer) } }
        return temp

    }

    enum class DateType {
        YEAR, MONTH, DAY, HOUR;
    }

    data class Pojo(
        var date: LocalDateTime,
        var value: String,
    )

    internal class Builder<V> {
        private var dataList: List<V> = listOf()
        private var perFilter = Function { _: V -> true }
        private var postFilter = Function { _: Map<String, BigDecimal> -> true }
        private var k: Function<V, String> = Function { "" }
        private var v: Function<V, BigDecimal> = Function { BigDecimal.ZERO }
        private var dateType = DateType.MONTH
        private var start = LocalDateTime.now().minusYears(1)
        private var end = LocalDateTime.now()
        private var init = BigDecimal.ZERO
        fun perFilter(perFilter: Function<V, Boolean>): Builder<V> {
            this.perFilter = perFilter
            return this
        }

        fun postFilter(postFilter: Function<Map<String, BigDecimal>, Boolean>): Builder<V> {
            this.postFilter = postFilter
            return this
        }

        fun dataList(dataList: List<V>): Builder<V> {
            this.dataList = dataList
            return this
        }

        fun k(k: Function<V, String>): Builder<V> {
            this.k = k
            return this
        }

        fun v(v: Function<V, BigDecimal>): Builder<V> {
            this.v = v
            return this
        }

        fun dateType(dateType: DateType): Builder<V> {
            this.dateType = dateType
            return this
        }

        fun start(start: LocalDateTime): Builder<V> {
            this.start = start
            return this
        }

        fun end(end: LocalDateTime): Builder<V> {
            this.end = end
            return this
        }

        fun init(init: BigDecimal): Builder<V> {
            this.init = init
            return this
        }

        fun build(): TimeLineData<V> {
            return object : TimeLineData<V>() {
                init {
                    setDataList(dataList)
                    setK(k)
                    setV(v)
                    setPerFilter(perFilter)
                    setPostFilter(postFilter)
                    setDateType(dateType)
                    setStart(start)
                    setEnd(end)
                    setInit(init)
                }
            }
        }
    }
}