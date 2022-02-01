package poitl

import com.deepoove.poi.XWPFTemplate
import com.deepoove.poi.data.*
import constant.Env
import constant.Env.importWord
import constant.Env.subModelWord
import models.TableInfo
import java.io.File

/**
 * POI-TL 模板渲染
 *
 * @author yunkuangao a317526763@gmail.com
 * @since 17
 */
object Poitl {

    data class TableData(val tableName: String, val tableComments: String, val tableValue: TableRenderData)

    /**
     * 进行word参数拼凑
     *
     * @param tableList 表列表
     * @author yunkuangao a317526763@gmail.com
     */
    fun makeDoc(tableList: List<TableInfo>): XWPFTemplate {

        // 列别名配置
        val headerData: List<TextRenderData> = Env.option.getVariableNameList().map { TextRenderData(it) }

        // 将数据写入模板
        val tableDataList: List<TableData> = tableList.map { table: TableInfo ->
            TableData(
                table.tableName,
                table.comments,
                Tables.create(
                    *listOf(
                        // 首行配置
                        Rows.of(*headerData.toTypedArray())
                            .textColor("FFFFFF")
                            .bgColor("4472C4")
                            .center()
                            .create(),
                        // 数据填充
                        *table.columnList.map { column ->
                            Rows.create(
                                // 如果为false则传null
                                if (column.columnName.hidden) column.columnName.name else null,
                                if (column.columnType.hidden) column.columnType.name else null,
                                if (column.columnNullable.hidden) column.columnNullable.name else null,
                                if (column.columnKey.hidden) column.columnKey.name else null,
                                if (column.columnDefault.hidden) column.columnDefault.name else null,
                                if (column.columnExtra.hidden) column.columnExtra.name else null,
                            )
                        }.toTypedArray()
                    ).toTypedArray()
                )
            )
        }

        // 子模板拼接
        val fileInputStream = File(this::class.java.classLoader.getResource(subModelWord)!!.file).inputStream()

        // 缓存map
        val tempMap: Map<String, Any> = mapOf("sub" to Includes.ofStream(fileInputStream).setRenderModel(tableDataList).create())

        // 返回模板
        return XWPFTemplate.compile(File(this::class.java.classLoader.getResource(importWord)!!.file)).render(tempMap)
    }
}

