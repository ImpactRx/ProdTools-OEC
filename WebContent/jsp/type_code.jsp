<%@ page import="com.targetrx.project.oec.bo.User" %>
<html>
<%
   String cbId = request.getParameter("codebookid");
   User u = (User)session.getAttribute("user");   
%>
<% if (u == null) { %>
    <script language="Javascript">
    window.location = "/oec/main";
    </script>
<% } else { %>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Open Ended Coding Tool</title>
        <link rel="stylesheet" type="text/css" href="/tools/css/oec.css">
        <link rel="stylesheet" type="text/css" href="/oec/css/<%= u.getRoleCode()%>.css">
        <link rel="stylesheet" type="text/css" href="/tools/css/layout.css">
        <script type='text/javascript' src='/oec/javascript/event.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/programs.js'></script>
        <script type='text/javascript' src='/oec/dwr/engine.js'></script>
	<script type='text/javascript' src='/oec/dwr/util.js'></script>
        <script language="Javascript">
            function buildPage()
            {
                programs.getOecTypeCodes(displayTypes);
            }
            function displayTypes(data)
            {
                DWRUtil.removeAllOptions("code");
                DWRUtil.addOptions("code",["Please select a Tag Type Code."]);
                DWRUtil.addOptions("code", data);
            }
            function saveTypes()
            {
                var result = DWRUtil.getValue("code");
                if ((result == "Please select a Tag Type Code.") || (result == ""))
                {
                    alert("Please select a Tag Type Code.");
                    return false;
                }
                window.opener.document.codes.tagtypecode.value = result;
                window.opener.skipTagResp();
                window.close();
            }
        </script>
     </head>
<body onload="buildPage();">
    <form name="tag">
    <table align="center" class="tier2" border="0" height="50">
        <tr height="25">
        <td class="label">Tag Type Code:</td><td><select id="code" name="code" value="" style="width: 80mm"></select></td>
        </tr>
        <tr height="25">
            <td class="buttons" colspan="2" align="center">
            <font id="message"></font>
            <a href="#" class="buttonU"  id="saveTag" onClick="saveTypes();"/>Save</a>
            <a href="#" class="buttonU"  id="cancelTag" onClick="window.close();"/>Cancel</a>
            </td>
        </tr>
    </form>
</body>
<% } %>
</html>