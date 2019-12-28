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
        //个人信息提交
        function submitData(){
            //取值
            var nickName = $("#nickName").val();
            var sign = $("#sign").val();
            var profile = UE.getEditor("editor").getContent();
            if(nickName==null || nickName==""){
                $.messager.alert("系统提示","请输入昵称");
            }else if(sign==null || sign==""){
                $.messager.alert("系统提示","请输入个性签名");
            }else if(profile==null || profile==""){
                $.messager.alert("系统提示","请输入个人简介");
            } else {
                $("#profile").val(profile);
                $("#form1").submit();
            }


        }


    </script>
</head>

<body style="margin: 10px; font-family: microsoft yahei">
<div id="p" class="easyui-panel" title="固件升级" style="padding: 10px">
    <form id="form1" action="${pageContext.request.contextPath}/firmware/upload" method="post" enctype="multipart/form-data">
        <input type="hidden" id="id" name="id" value="${currentUser.id}">
        <input type="hidden" id="profile" name="profile" value="${currentUser.profile}">
        <table cellspacing="20px">
            <tr>
                <td width="80px">请上传你要升级的固件</td>
                <td><input type="file" id="firmwareFile" name="firmwareFile" style="width:400px;"/></td>
            </tr>
            <tr>
                <td width="80px">机器码</td>
                <td><input type="text" id="machineCode" name="machineCode" style="width:200px;"/></td>
            </tr>
            <tr>
                <td width="80px">设备IP</td>
                <td><input type="text" id="machineIp" name="machineIp" style="width:200px;" value="${machineIp}" readonly="readonly"/></td>
            </tr>
            <tr>
                <td width="80px">设备端口</td>
                <td><input type="text" id="machinePort" name="machinePort" style="width:200px;" value="${machinePort}" readonly="readonly"/></td>
            </tr>
            <tr>
                <td></td>
                <td><a href="javascript:getIpAndPort()" class="easyui-linkbutton" data-options="iconCls:'icon-submit'">获取设备IP端口</a></td>
                <td id="getIpAndPortInfo"></td>
            </tr>
            <tr>
                <td></td>
                <td><a href="javascript:intoUpgradeMode()" class="easyui-linkbutton" data-options="iconCls:'icon-submit'">进入烧写模式</a></td>
                <td id="intoUpgradeModeInfo"></td>
            </tr>
            <tr>
                <td></td>
                <td><a href="javascript:intoWorkMode()" class="easyui-linkbutton" data-options="iconCls:'icon-submit'">进入应用模式</a></td>
                <td id="intoWorkModeInfo"></td>
            </tr>

            <tr>
                <td></td>
                <td><a href="javascript:submitBtn()" class="easyui-linkbutton" data-options="iconCls:'icon-submit'">固件升级开始</a></td>
                <td id="submitInfo"></td>
            </tr>

            <tr>
                <td></td>
                <td><div id="progressbar" class="easyui-progressbar" data-options="value:0" style="width:400px;"></div></td>
                <td id="progressbarInfo"></td>
            </tr>

        </table>
    </form>
</div>
<script type="text/javascript">
    function getIpAndPort(){
        var machineCode = $("#machineCode").val();
        var machineIp = $("#machineIp");
        var machinePort = $("#machinePort");
        if(validMachineCode()){
            $.post('/firmwareManager/firmware/getIpAndPort', {machineCode:machineCode}, function (result) {
                machineIp.val('');
                machinePort.val('');
                if (result && result.success) {
                    machineIp.val(result.data.machineIp);
                    machinePort.val(result.data.machinePort);
                    layer.msg("读取成功");
                }else{
                    layer.msg(result.errorInfo);
                }
            });
        }
    }
    function intoUpgradeMode(){
        var machineCode = $("#machineCode").val();
        if(validMachineCode()){
            $.post('/firmwareManager/firmware/intoUpgradeMode',{machineCode:machineCode},function(result){
                if(result && result.success){
                    layer.msg('机器码'+machineCode+'的设备已进入烧写模式');
                }else{
                    layer.msg(result.errorInfo);
                }
            })
        }
    }
    function intoWorkMode(){
        var machineCode = $("#machineCode").val();
        if(validMachineCode()){
            $.post('/firmwareManager/firmware/intoWorkMode',{machineCode:machineCode},function(result){
                if(result && result.success){
                    layer.msg('机器码'+machineCode+'的设备已进入应用模式');
                }else{
                    layer.msg(result.errorInfo);
                }
            })
        }
    }
    function firmwareUpgrade(){
        var machineCode = $("#machineCode").val();
    }
    //固件升级开始,并回调显示进度
    function submitBtn(){
        $('#progressbar').progressbar('setValue', 0);
        if(validMachineCode()){
            var form = $("#form1");
            var options  = {
                url:'${pageContext.request.contextPath}/firmware/upload',
                type:'post',
                success:function(result)
                {
                    if(result.success){
                        layer.msg('文件上传完成,马上开始固件升级');
                        progressbarChange(result.data.firmwareCount);
                    }else{
                        layer.msg(result.errorInfo)
                    }
                }
            };
            form.ajaxSubmit(options);
        }
    }
    //校验机器码
    function validMachineCode(){
        var machineCode = $("#machineCode").val();
        if(machineCode==null || machineCode.trim()==""){
            layer.msg("机器码不能为空");
            return false;
        }
        return true;
    }
    //进度条展示
    function progressbarChange(firmwareCount){
        var totalCount = firmwareCount;
        $('#progressbar').progressbar('setValue', 1);
        //请求当前发送进度
        var progressbarInterval = setInterval(function(){
            var progressbarValue = $('#progressbar').progressbar('getValue');
            $.post(
                '/firmwareManager/firmware/getProgressbar',
                {},
                function(result){
                    if(result.success){
                        $('#progressbar').progressbar('setValue', (result.data.progressbarValue/totalCount)*100);
                        if(progressbarValue>=100){
                            layer.msg('升级完成');
                            clearInterval(progressbarInterval);
                        }
                    }else{
                        layer.msg(result.errorInfo);
                    }
                }
            );
        }, 1000);
    }
</script>
</body>
</html>
