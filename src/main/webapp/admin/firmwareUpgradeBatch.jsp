<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>固件升级</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css"
          href="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/themes/icon.css">
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/jquery.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/jquery.easyui.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/static/jquery-easyui-1.3.3/locale/easyui-lang-zh_CN.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/static/layer/layer.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/static/jquery_form.js"></script>
    <script type="text/javascript">
    </script>
</head>
<body style="margin: 1px; font-family: microsoft yahei">
<table id="dg" title="设备管理" class="easyui-datagrid" fitcolumns="true" pagination="true" rownumbers="true"
       url="/firmwareManager/firmware/machineList" fit="true" toolbar="#tb">
    <thead>
    <tr>
        <th field="cb" checkbox="true" align="center"></th>
        <th field="machineId" width="20" align="center">编号</th>
        <th field="machineCode" width="200" align="center" >机器码</th>
        <th field="machineIp" width="50" align="center">IP</th>
        <th field="machinePort" width="50" align="center" >端口</th>
        <th field="hotelId" width="50" align="center" >酒店ID</th>
    </tr>
    </thead>
</table>
<div id="tb">
    <div>
        <a href="javascript:openModifyBlogTab()" class="easyui-linkbutton" iconCls="icon-edit" plain="true">修改</a>
        <a href="javascript:deleteBlog()" class="easyui-linkbutton" iconCls="icon-remove" plain="true">删除</a>
    </div>
    <div>
        &nbsp;根据机器码检索:&nbsp;<input type="text" id="searchByTitle" size="20" onkeydown="if(event.KeyCode=13) searchBlogByTitle()"/>
        <a href="javascript:searchBlogByTitle()" class="easyui-linkbutton" iconCls="icon-search">搜索</a>
    </div>
</div>
</body>
</html>