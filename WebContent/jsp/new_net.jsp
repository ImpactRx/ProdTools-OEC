<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ page import="com.targetrx.project.oec.bo.User" %>
<%
   String cbId = request.getParameter("codebookid");
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
        <title>Create New Net</title>
        <link rel="stylesheet" type="text/css" href="/tools/css/oec.css">
        <link rel="stylesheet" type="text/css" href="/oec/css/<%= u.getRoleCode()%>.css">
        <script type='text/javascript' src='/oec/dwr/interface/codebooks.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/nets.js'></script>
        <script type='text/javascript' src='/oec/dwr/engine.js'></script>
		<script type='text/javascript' src='/oec/dwr/util.js'></script>
    </head>
    <script language="Javascript">
        function checkNetLabel()
        {   
            nets.checkNetLabel(DWRUtil.getValue("netLabel"),displayStatus)
        }
        function displayStatus(data)
        {
            if (data.length >= 1)
            {
                document.getElementById('message').innerHTML = "That Net Label already exists.<br />";
                document.getElementById('saveNet').style.visibility = 'hidden';
                return false;
            }
            document.getElementById('message').innerHTML = "";
            document.getElementById('saveNet').style.visibility = 'visible';
        }
        function saveNet()
        {
            if (DWRUtil.getValue("netLabel") == "")
            {
                alert("Please enter a Net Label.");
                return false;
            }
            if (DWRUtil.getValue("netDesc") == "")
            {
                alert("Please enter a Net Description.");
                return false;
            }
            nets.addNet(DWRUtil.getValue("netLabel"),DWRUtil.getValue("netDesc"),'<%= u.getUserName() %>',displayMessage);
        }
        function displayMessage()
        {
            document.getElementById('message').innerHTML = "Net has been added to the database.<br />";
            document.getElementById('netLabel').innerHTML = "";
            document.getElementById('netDesc').innerHTML = "";
        }
    </script>
    <body>
    <a href="#" class="close" onClick="window.close()">Close Window</a>
    <div id="contentAddNet">
    <form name="net">
    <table class="formnet">
        <tr>
            <th colspan="2">Create a Net</th>
        </tr>
        <tr>
            <td class="label">Net Label:</td>
            <td class="inputs" valign="top"><input type="text" name="netLabel" value="" id="netLabel" size="35" onblur="checkNetLabel();"/></td>
        </tr>
        <tr>
            <td class="label">Net Description:</td>
            <td class="inputs" valign="top"><textarea rows="5" cols="40" id="netDesc" name="description" size="20"></textarea></td>
        </tr>
        <tr class="buttons">
            <td class="buttons" colspan="2">
            <font id="message" class="messsage"></font>
            <a href="#" class="button"  id="saveNet" onClick="saveNet();"/>Save</a>
            <a href="#" class="button"  id="cancel" onClick="window.close();"/>Cancel</a>
            </td>            
        </tr>
    </table>
    </form>
    </div>
    </body>
<% } %>
</html>
