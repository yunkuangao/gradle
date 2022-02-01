<!DOCTYPE html>
<html lang="zh-cn">
<head>
    <meta charset="UTF-8">
    <title>数据库表结构导出生成WORD文档</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="../bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="../bootstrap/css/bootstrap-select.css">
    <script src="../js/jquery-3.3.1.js"></script>
    <script src="../js/jquery-ajax-native.js"></script>
    <script src="../bootstrap/js/bootstrap.min.js"></script>
    <script src="../bootstrap/js/bootstrap-select.js"></script>
    <script type="text/javascript">
        $(function () {
            $.ajax({
                url: "/option",
                dataType: "json",
                success: function (data) {
                    const $div = $("#optional");
                    data.forEach(option => $div.append("<option value='" + option + "' selected>" + option + "</option>"));
                    $div.selectpicker('refresh');
                    $div.selectpicker('render');
                }
            });

            $("input").change(function () {
                $.ajax({
                    url: "/tableList",
                    dataType: "json",
                    type: "post",
                    contentType: "application/json;charset=utf-8",
                    data: JSON.stringify({
                        databaseType: $("#dbKind").val().toUpperCase(),
                        ip: $("#ip").val(),
                        port: $("#port").val(),
                        schema: $("#dbName").val(),
                        userName: $("#userName").val(),
                        password: $("#password").val(),
                    }),
                    success: function (data) {
                        const $div = $("#table");
                        $div.empty();

                        data.forEach(table => $div.append("<option value='" + table.tableName + "' selected>" + table.tableName + "</option>"));

                        $div.selectpicker('refresh');
                        $div.selectpicker('render');
                    }
                });
            });

            $("#dbKind").change(function () {
                const db = $(this).val();
                $("#ip,#port,#dbName,#userName,#password").val("");
                $("#table").html("").selectpicker('refresh').selectpicker('render');
                let ip, port, dbName, userName;
                switch (db) {
                    case 'ORACLE':
                        ip = "127.0.0.1";
                        port = "1521";
                        dbName = "oracle";
                        userName = "oracle";
                        break;
                    case 'MYSQL':
                        ip = "127.0.0.1";
                        port = "3306";
                        dbName = "test";
                        userName = "root";
                        break;
                    case 'SQLSERVER':
                        ip = "127.0.0.1";
                        port = "1433";
                        dbName = "";
                        userName = "sa";
                        break;
                    default:
                        ip = "127.0.0.1";
                        port = "";
                        dbName = "";
                        userName = "";
                }
                $("#ip").val(ip);
                $("#port").val(port);
                $("#dbName").val(dbName);
                $("#userName").val(userName);
            });
        });

        function execute() {
            $('#messageText').text("正在努力生成中......");
            $('#myModal').modal('show');

            let option = {};
            $("#optional > option").each(function() {
                option[this.text] = this.selected;
            });

            $.ajax({
                url: "/build",
                type: "post",
                contentType: "application/json;charset=utf-8",
                dataType: 'native',
                xhrFields: {
                    responseType: 'blob'
                },
                data: JSON.stringify({
                    ip: $("#ip").val(),
                    port: $("#port").val(),
                    userName: $("#userName").val(),
                    password: $("#password").val(),
                    databaseType: $("#dbKind").val(),
                    schema: $("#dbName").val(),
                    fieldOption: option,
                    choiceTableList: $("#table").val().map(value => {
                        return {tableName: value}
                    })
                }),
                success: function (blob) {
                    $('#messageText').text(blob.size > 0 ? "success" : "error");
                    const link = document.createElement('a');
                    link.href = window.URL.createObjectURL(blob);
                    link.download = "export.docx";
                    link.click();
                }, error: function (data) {
                    $('#messageText').text("未知错误" + data.message);
                }
            });
        }
    </script>
</head>
<body>
<div class="container">
    <h1>数据库表结构导出生成WORD文档</h1>
    <div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
        <div class="panel panel-default">
            <div class="panel-heading" role="tab" id="headingOne">
                <h4 class="panel-title">
                    <a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseOne"
                       aria-expanded="true" aria-controls="collapseOne">
                        database
                    </a>
                </h4>
            </div>
            <div id="collapseOne" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingOne">
                <div class="panel-body">
                    <form style="margin-top: 20px;margin-left: 100px;margin-right: 400px;" action="/makeWord"
                          method="post">
                        <div class="form-group">
                            <label for="dbKind">database</label>
                            <select class="selectpicker form-control" id="dbKind"
                                    data-live-search="true" title="Choose one of all">
                                <option value="ORACLE" selected>ORACLE</option>
                                <option value="MYSQL">MYSQL</option>
                                <option value="MARIADB">MARIADB</option>
                                <option value="SQLSERVER">SQLSERVER</option>
                            </select>
                        </div>
                        <div class="form-inline">
                            <div class="form-group">
                                <label class="sr-only" for="ip">ip</label>
                                <input type="text" class="form-control" id="ip" placeholder="127.0.0.1">
                            </div>
                            <div class="form-group">
                                <label class="sr-only" for="port">port</label>
                                <input type="text" class="form-control" id="port" placeholder="" value="1521">
                            </div>
                            <div class="form-group">
                                <label class="sr-only" for="dbName">dbName</label>
                                <input type="text" class="form-control" id="dbName" placeholder="" value="oracle">
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="userName">userName</label>
                            <input type="text" class="form-control" id="userName" placeholder="userName">
                        </div>
                        <div class="form-group">
                            <label for="password">password</label>
                            <input type="text" class="form-control" id="password" placeholder="password">
                        </div>
                        <div class="form-group">
                            <!-- 多选框参考链接 -->
                            <!-- https://blog.csdn.net/zxl_LangYa/article/details/79247307 -->
                            <label for="optional">optional</label>
                            <select class="selectpicker form-control" id="optional"
                                    data-selected-text-format="count > 5"
                                    multiple data-live-search="true"
                                    title="Choose options of all"
                                    data-actions-box="true">
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="table">table</label>
                            <select class="selectpicker form-control" id="table" data-selected-text-format="count > 5"
                                    multiple data-live-search="true"
                                    title="Choose options of all"
                                    data-actions-box="true">
                            </select>
                        </div>
                        <p><a class="btn btn-primary btn-lg" href="javascript:execute();"
                              role="button">生成文档</a></p>
                        <p style="color: red">生成的文档在D盘下的export.docx</p>
                    </form>

                </div>
            </div>
        </div>
    </div>

    <!-- 模态框（Modal） -->
    <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"
         data-keyboard="false">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    <h4 class="modal-title" id="myModalLabel">正在努力生成文档中，请稍后...</h4>
                </div>
                <div class="modal-body" id="messageText">正在努力生成中......</div>

            </div><!-- /.modal-content -->
        </div><!-- /.modal -->
    </div>

</div>
</body>
</html>