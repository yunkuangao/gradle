import java.util.*
import java.util.function.Consumer
import java.util.function.Function
import java.util.stream.Collectors

/**
 * @author yunkuangao a317526763@gmail.com
 * @apiNote String tools
 */
object StringHelper {

    /**
     * @param map  check map
     * @param keys key String array
     * @return boolean true: map have all keys of args; false: map have not any keys of args
     * @author yunkuangao a317526763@gmail.com
     * @apiNote if there is no value of args in the map,return no value's args
     */
    fun hasAnyEmpty(map: Map<String, Any>, vararg keys: String): Boolean {
        return Arrays.stream(keys).anyMatch { lt: String -> !map.containsKey(lt) }
    }

    /**
     * @param map  check map
     * @param keys key String array
     * @return java.lang.String returns keys that are not in args in the map
     * @author yunkuangao a317526763@gmail.com
     * @apiNote if all args are contained in the map, an empty string will be returned; otherwise, no args will be returned
     */
    fun getAnyEmpty(map: Map<String, Any>, vararg keys: String): String {
        return Arrays.stream(keys)
            .filter { lt: String -> !map.containsKey(lt) }
            .collect(Collectors.joining(","))
    }

    /**
     * @param string string
     * @param t      any type
     * @return boolean true: equal; false: not equal
     * @author yunkuangao a317526763@gmail.com
     * @apiNote two-value comparison
     */
    fun <T> equalsStringAndT(string: String, t: T): Boolean {
        return t != null && string == t.toString()
    }

    /**
     * @param e any type
     * @param t any type
     * @return boolean true: equal; false: not equal
     * @author yunkuangao a317526763@gmail.com
     * @apiNote two-value comparison
     */
    fun <E, T> equalsAnyAndAny(e: E, t: T): Boolean {
        return e != null && e == t
    }

    /**
     * @param t1 any type
     * @param t2 any type
     * @return boolean true: equal; false: not equal
     * @author yunkuangao a317526763@gmail.com
     * @apiNote two-value comparison
     */
    fun <T> equalsTAndT(t1: T, t2: T): Boolean {
        return t1 != null && t1 == t2
    }

    /**
     * @param changeMap origin map
     * @return java.util.Map return Map<T></T>,T>
     * @author yunkuangao a317526763@gmail.com
     * @apiNote change the type of MAP<T></T>,T>
     */
    fun <T> changeMapToKT(changeMap: Map<out Any, Any>, t: T): Map<*, *> {
        return changeMap.entries.stream()
            .collect(Collectors.toMap(
                { (key): Map.Entry<Any, Any> -> key as T },
                { (_, value): Map.Entry<Any, Any> -> value as T }))
    }

    /**
     * @param changeMap origin map
     * @return java.util.Map return Map<K></K>,V>
     * @author yunkuangao a317526763@gmail.com
     * @apiNote change the type of MAP<K></K>,V>
     */
    fun <K, V> changeMapToKT(changeMap: Map<out Any, Any>, t: K, v: V): Map<*, *> {
        return changeMap.entries.stream()
            .collect(Collectors.toMap(
                { (key): Map.Entry<Any, Any> -> key as K },
                { (_, value): Map.Entry<Any, Any> -> value as V }))
    }

    /**
     * @param t any type
     * @return boolean true: t is null; false: t have instance; exception: if t is type String, the value is "" will be return true
     * @author yunkuangao a317526763@gmail.com
     * @apiNote check t is null
     */
    fun <T> isEmpty(t: T): Boolean {
        if (t == null) return true
        return if (t is String) {
            "" == t
        } else false
    }

    /**
     * @param value value
     * @return return string
     * @author yunkuangao a317526763@gmail.com
     * @apiNote if type of value is string, and it's not null, return it, or return ""
     */
    fun getValue(value: Any): String {
        return value.toString()
    }
}