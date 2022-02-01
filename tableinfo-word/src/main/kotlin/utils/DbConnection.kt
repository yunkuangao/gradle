package utils

import mu.KotlinLogging
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

/**
 * JDBC连接
 * @author yunkuangao a317526763@gmail.com
 * @version 1.0
 */
object DbConnection {

    private val logger = KotlinLogging.logger {}

    /**
     * 获取连接
     * @param jdbcUrl         jdbc
     * @param userName        用户名
     * @param password        密码
     * @param driverClassName 驱动名
     * @return 返回一个连接实例
     * @author yunkuangao a317526763@gmail.com
     */
    @Throws(Exception::class)
    fun getConn(jdbcUrl: String, userName: String, password: String, driverClassName: String): Connection {
        Class.forName(driverClassName)
        return DriverManager.getConnection(jdbcUrl, userName, password)
    }

    /**
     * 关闭资源
     * @param resultSet 结果集实例
     * @author yunkuangao a317526763@gmail.com
     */
    @Throws(Exception::class)
    fun closeRs(resultSet: ResultSet?) {
        resultSet?.let {
            closeStatement(resultSet.statement)
            it.close()
            logger.debug("已关闭结果集连接")
        }
    }

    /**
     * 关闭语句
     * @param statement 语句实例
     * @author yunkuangao a317526763@gmail.com
     */
    @Throws(Exception::class)
    fun closeStatement(statement: Statement?) {
        statement?.let {
            closeConn(statement.connection)
            it.close()
            logger.debug("已关闭语句连接")
        }
    }

    /**
     * 关闭连接
     * @param connection 连接实例
     * @author yunkuangao a317526763@gmail.com
     */
    @Throws(Exception::class)
    fun closeConn(connection: Connection?) {
        connection?.let {
            it.close()
            logger.debug("已关闭数据库连接")
        }
    }
}