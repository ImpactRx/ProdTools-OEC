<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ page import="com.targetrx.project.oec.bo.User" %>
<%
   String codeId = request.getParameter("codeId");
   String cbId = request.getParameter("cbId");
   User u = (User)session.getAttribute("user");
%>
<% if (u == null) { %>
    <script language="Javascript">
    window.opener.location = "/oec/main";
    window.close();
    </script>
<% } else { %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Code</title>
        <link rel="stylesheet" type="text/css" href="/tools/css/oec.css">
        <link rel="stylesheet" type="text/css" href="/oec/css/<%= u.getRoleCode()%>.css">
        <script type='text/javascript' src='/tools/javascript/common.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codebooks.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codes.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/nets.js'></script>
        <script type='text/javascript' src='/oec/dwr/engine.js'></script>
	<script type='text/javascript' src='/oec/dwr/util.js'></script>
        <script language="Javascript">
            function buildPage()
            {
                codes.getCode(<%= codeId %>,<%= cbId %>,displayCode);
                DWRUtil.setValue("codeId","<%= codeId %>");
            }   
            function displayCode(data)
            {
                //alert(data.length);
                for (var i = 0; i < data.length; i++)
		{
                    var result = data[i];
                    DWRUtil.setValue("codeNum",result.codeNum);
                    DWRUtil.setValue("codeId",result.codeId);
                    DWRUtil.setValue("codeLabel",result.codeLabel);
                    DWRUtil.setValue("codeLabelIn",result.codeLabel);
                    DWRUtil.setValue("reportLabel",result.codeReport);
                    DWRUtil.setValue("hintCode",result.codeHint);
                }
            }
            function saveCode(form)
            {
                if (DWRUtil.getValue("codeLabel") == "")
                {
                    alert("Please enter a Code Label");
                    return false;
                }
                var conf = "";
                if (DWRUtil.getValue("codeLabel") != DWRUtil.getValue("reportLabel"))
                {
                    conf = confirm("Code Label and Repot Label do not match.");
                    if (!conf)
                    {
                        return false;
                    }
                }
                change = "true";
                codes.updateCode(<%= cbId %>,DWRUtil.getValue("codeId"),DWRUtil.getValue("codeNum"),DWRUtil.getValue("codeLabel"),DWRUtil.getValue("reportLabel"),DWRUtil.getValue("hintCode"),'<%= u.getUserName() %>',displayMessage);
            }
            function displayMessage()
            {
                document.getElementById("message").innerHTML = "Code has been updated<br />";
            }
            function checkLabel()
            {
                if (DWRUtil.getValue("codeLabelIn") != DWRUtil.getValue("codeLabel"))
                {
                    codes.checkCodeLabel(DWRUtil.getValue("codeLabel"), displayCMessage);
                } else
                {
                    document.getElementById('saveCode').style.visibility = 'visible';
                    document.getElementById("message").innerHTML = "";
                }
            }
            function displayCMessage(data)
            {
                if (data.length > 0)
                {
                    document.getElementById("message").innerHTML = "That Code Label already exists.<br />";
                    document.getElementById('saveCode').style.visibility = 'hidden';
                } else 
                {
                    document.getElementById('saveCode').style.visibility = 'visible';
                    DWRUtil.setValue("reportLabel",DWRUtil.getValue("codeLabel"));
                }
            }
        </script>
    </head>
    <body onLoad="buildPage();" onUnload="refreshParent();">
    <a href="#" class="close" onClick="window.close()">Close Window</a>
    <form name="codes">
    <input type="hidden" id="codeId" value=""/>
    <input type="hidden" id="codeLabelIn" value="<%= codeId %>"/>
    <table border="1" class="form1">
        <tr>
            <th colspan="2">Edit a Code</th>
        </tr>
        <tr>
            <td class="label" valign="top">*Code Num</td>
            <td class="inputs" valign="top"><input type="text" id="codeNum" class="inputs" size="15" disabled/></td>
        </tr>
        <tr>
            <td class="label" valign="top">*Code Label</td>
            <td class="inputs" valign="top"><input type="text" id="codeLabel" class="inputs" size="55" onBlur="checkLabel();"/></td>
        </tr>
        <tr>
            <td class="label" valign="top">Report Label</td>
            <td class="inputs" valign="top"><input type="text" id="reportLabel"  class="inputs" size="55"/></td>
        </tr>
         <tr>
            <td class="label" valign="top">Hint Code</td>
            <td class="inputs" valign="top"><input type="text" id="hintCode" class="inputs" size="55"/></td>
        </tr>
        </tr>
            <td class="buttons" colspan="2" align="center">
            <font id="message"></font>
            <a href="#" class="button"  id="saveCode" onClick="saveCode(document.codes);"/>Save</a>
            <a href="#" class="button"  id="cancelCod" onClick="window.close();"/>Cancel</a>
            </td>
        </tr>
    </table>
    </form>
    
    </body>
<% } %>
</html>
