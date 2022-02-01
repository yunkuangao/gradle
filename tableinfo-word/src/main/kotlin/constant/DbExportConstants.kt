package constant

import models.DatabaseType

/**
 * 得到数据库链接属性
 *
 * @author yunkuangao a317526763@gmail.com
 */
object DbExportConstants {
    /**
     * 得到连接地址
     *
     * @param type 数据库类型
     * @param ip     ip
     * @param port   端口
     * @param schema 数据库名
     * @return 返回相应的数据库链接
     */
    fun getJdbcUrl(type: DatabaseType, ip: String, port: String, schema: String): String = when (type) {
        DatabaseType.MYSQL -> "jdbc:mysql://$ip:$port/$schema"
        DatabaseType.ORACLE -> "jdbc:oracle:thin:@//$ip:$port/$schema"
        DatabaseType.SQLSERVER -> "jdbc:sqlserver://$ip:$port;databaseName=$schema"
        DatabaseType.MARIADB -> "jdbc:mariadb://$ip:$port/$schema"
    }

    /**
     * 得到驱动名称
     *
     * @param type 数据库类型
     * @return 返回相应的驱动名称
     */
    fun getDriverClassName(type: DatabaseType): String = when (type) {
        DatabaseType.MYSQL -> "com.mysql.cj.jdbc.Driver"
        DatabaseType.ORACLE -> "oracle.jdbc.driver.OracleDriver"
        DatabaseType.SQLSERVER -> "com.microsoft.sqlserver.jdbc.SQLServerDriver"
        DatabaseType.MARIADB -> "org.mariadb.jdbc.Driver"
    }

    /**
     * 获取得到所有表和表注释的sql语句
     *
     * @param type 数据库类型
     * @return 返回数据库用户名下的所有表名
     */
    fun getTableName(type: DatabaseType): String = when (type) {
        DatabaseType.MYSQL, DatabaseType.MARIADB -> """
                    show tables
                """.trimIndent()
        DatabaseType.ORACLE -> """
                    SELECT 
                        table_name
                    FROM
                        dba_tables;
                """.trimIndent()
        DatabaseType.SQLSERVER -> """
                    SELECT 
                        *
                    FROM
                        information_schema.tables;
                """.trimIndent()
    }

    /**
     * 得到查询表字段和参数的sql
     *
     * @param type    数据库类型
     * @param tableName 表名
     * @return 返回对应的表字段和参数
     */
    fun getColumnInfo(type: DatabaseType, tableName: String): String = when (type) {
        DatabaseType.MYSQL, DatabaseType.MARIADB, DatabaseType.ORACLE -> """
                 desc $tableName
            """.trimIndent()
        DatabaseType.SQLSERVER -> """
                    sp_help $tableName
                """.trimIndent()
    }
}