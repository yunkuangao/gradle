package models

import kotlinx.serialization.Serializable
import kotlin.reflect.KProperty1

@Serializable
enum class DatabaseType {
    MYSQL,
    ORACLE,
    MARIADB,
    SQLSERVER,
}

/**
 * 类说明:数据库基础信息
 *
 * @author yunkuangao a317526763@gmail.com
 * @version 1.0
 */
@Serializable
data class DatabaseInfo(
    val ip: String,
    val port: String,
    val userName: String,
    val password: String,
    val databaseType: DatabaseType,
    val schema: String,
) {
    var fieldOption: FieldOption = FieldOption()
    var tableList: List<TableInfo> = listOf()
    var choiceTableList: List<TableInfo> = listOf()
}

/**
 * 类说明:
 *
 * @author yunkuangao a317526763@gmail.com
 * @version 1.0
 */
@Serializable
data class TableInfo(
    val tableName: String,
) {
    var comments: String = ""
    var columnList: List<ColumnInfo> = listOf()
}

@Serializable
data class ColumnInfo(
    val columnName: FieldInfo.Name,
    val columnType: FieldInfo.Type,
    var columnNullable: FieldInfo.Nullable,
    var columnKey: FieldInfo.Key,
    var columnDefault: FieldInfo.Default,
    var columnExtra: FieldInfo.Extra,
)

@Serializable
data class FieldOption(
    val name: Boolean = true,
    val type: Boolean = true,
    val nullable: Boolean = true,
    val key: Boolean = true,
    val default: Boolean = true,
    val extra: Boolean = true,
) {
    fun getVariableNameList(): List<String> = this::class.members.filter {
        when (it) {
            is KProperty1<*, *> -> true
            else -> false
        }
    }.map { it.name }

}

@Serializable
sealed class FieldInfo {

    @Serializable
    class Name(
        val name: String,
        var hidden: Boolean = true,
    ) : FieldInfo()

    @Serializable
    class Type(
        val name: String,
        var hidden: Boolean = true,
    ) : FieldInfo()

    @Serializable
    class Nullable(
        val name: String,
        var hidden: Boolean = true,
    ) : FieldInfo()

    @Serializable
    class Key(
        val name: String,
        var hidden: Boolean = true,
    ) : FieldInfo()

    @Serializable
    class Default(
        val name: String,
        var hidden: Boolean = true,
    ) : FieldInfo()

    @Serializable
    class Extra(
        val name: String,
        var hidden: Boolean = true,
    ) : FieldInfo()
}
