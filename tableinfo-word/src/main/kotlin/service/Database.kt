package service

import constant.DbExportConstants
import models.ColumnInfo
import models.DatabaseInfo
import models.FieldInfo
import models.TableInfo
import utils.DbConnection
import java.sql.Connection

/**
 * 进行数据查询和处理
 *
 * @author yunkuangao
 */
object DataOperator {

    private fun getConnection(info: DatabaseInfo): Connection = DbConnection.getConn(
        DbExportConstants.getJdbcUrl(
            info.databaseType,
            info.ip,
            info.port,
            info.schema
        ),
        info.userName,
        info.password,
        DbExportConstants.getDriverClassName(info.databaseType)
    )

    fun getTableName(info: DatabaseInfo): List<TableInfo> {
        getConnection(info).use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery(DbExportConstants.getTableName(info.databaseType)).use { resultSet ->
                    return buildList {
                        while (resultSet.next()) {
                            add(
                                TableInfo(tableName = resultSet.getString("Tables_in_${info.schema}"))
                            )
                        }
                    }
                }
            }
        }
    }

    fun getTableAllColumn(info: DatabaseInfo): List<TableInfo> = getTableAllColumn(info, info.choiceTableList.map { it.tableName })

    fun getTableAllColumn(info: DatabaseInfo, tableNameList: List<String>): List<TableInfo> = tableNameList.map { getTableAllColumn(info, it) }

    fun getTableAllColumn(info: DatabaseInfo, tableName: String): TableInfo {
        getConnection(info).use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery(DbExportConstants.getColumnInfo(info.databaseType, tableName)).use { resultSet ->
                    return TableInfo(tableName).apply {
                        val columnList: MutableList<ColumnInfo> = mutableListOf()
                        while (resultSet.next()) {
                            columnList.add(ColumnInfo(
                                FieldInfo.Name(resultSet.getString("Field"), info.fieldOption.name),
                                FieldInfo.Type(resultSet.getString("Type"), info.fieldOption.type),
                                FieldInfo.Nullable(resultSet.getString("Null") ?: "", info.fieldOption.nullable),
                                FieldInfo.Key(resultSet.getString("Key") ?: "", info.fieldOption.key),
                                FieldInfo.Default(resultSet.getString("Default") ?: "", info.fieldOption.default),
                                FieldInfo.Extra(resultSet.getString("Extra") ?: "", info.fieldOption.extra),
                            ))
                        }
                        this.columnList = columnList
                    }
                }
            }
        }
    }

}
