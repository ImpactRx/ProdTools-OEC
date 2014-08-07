<%@ page import="com.targetrx.project.oec.bo.User" %>
<%
  String cbId = request.getParameter("codebookid");
  String cbname = request.getParameter("cbname");
  String clientid = request.getParameter("clientId");
  session.setAttribute("clientId",clientid);
  String customMapping = request.getParameter("customMap");
  session.setAttribute("customMap",customMapping);
  User u = (User)session.getAttribute("user");
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Dynamic CRAP Loader</title>
        <link rel="stylesheet" type="text/css" href="/oec/css/oec.css">
        <link rel="stylesheet" type="text/css" href="/oec/css/<%= u.getRoleCode()%>.css">
        <link rel="stylesheet" type="text/css" href="/oec/css/layout.css">
        <link rel="stylesheet" type="text/css" href="/oec/common/css/tools.css">
        <script type='text/javascript' src='/oec/javascript/event.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codebooks.js'></script>
	<script type='text/javascript' src='/oec/dwr/interface/client.js'></script>
	<script type='text/javascript' src='/oec/dwr/interface/custom.js'></script>
	<script type='text/javascript' src='/oec/dwr/interface/codes.js'></script>
	<script type='text/javascript' src='/oec/dwr/interface/nets.js'></script>
        <script src='/oec/javascript/upload.js'> </script>
        <script src='/oec/dwr/interface/UploadMonitor.js'> </script>
        <script src='/oec/dwr/engine.js'> </script>
        <script src='/oec/dwr/util.js'> </script>
        <style type="text/css">
            body { font: 11px Lucida Grande, Verdana, Arial, Helvetica, sans serif; }
            #progressBar { padding-top: 5px; }
            #progressBarBox { width: 350px; height: 20px; border: 1px inset; background: #eee;}
            #progressBarBoxContent { width: 0; height: 20px; border-right: 1px solid #444; background: #9ACB34; }
        </style>
        <script language="Javascript">
            function buildPage()
            {
                DWRUtil.setValue("bookName","CodeBook: <font style=\"color: green;\"><%= cbname %></font>");
                custom.getCustomMapName("<%= customMapping %>",displayCustomMapName);
                client.getClientName("<%= clientid %>",displayClientName);
            }
            // FUNCTION DISPLAYS CUSTOM MAP NAME
            function displayCustomMapName(data)
            {
                 DWRUtil.setValue("customMap","Custom Map: <font style=\"color: green;\">"+data+"</font>");
            }
            // FUNCTION DISPLAYS CLIENT NAME
            function displayClientName(data)
            {
                DWRUtil.setValue("clientName","Client Name: <font style=\"color: green;\">"+data+"</font>");
            }
        </script>
    </head>
    <body style="margin:0px 5px;" onload="buildPage();">
    <table width="800px" class="main">
    <tr>
        <td>
            <jsp:include page="top_nav.jsp"/>
	</td>
    </tr>
    <tr>
    	<td>
            <table border="0" width="800px"><tr height="35"><td><jsp:include page="custom_nav.jsp"/></td></tr></table>
    	</td>
    </tr>
    <tr height="15px"><td></td></tr>
    <td valign="top">
        <b><span id="clientName"></span></b><br />
    	<b><span id="bookName"></span></b><br />
        <b><span id="customMap"></span></b><br /><p></p>
    	</td>
    <tr>
    	<td>
    	<form name="listmatch" action="upload.jsp" enctype="multipart/form-data" method="post" onsubmit="startProgress()">
            <table class="tier1" valign="top" align="center" width="100%">
                <tr>
                    <td>
                        Select File you wish to upload:<br />
                        <input class="default" type="file" id="file1" name="file1" size="50"/>
                    </td>
                </tr>
                <tr>
                    <td align="center">
                        <input type="submit" value="begin upload" id="uploadbutton"/>
                    </td>
                </tr>
            </table>
            <br />
            
            <div id="progressBar" style="display: none;">
            
            <div id="theMeter">
                <div id="progressBarText"></div>
                <div id="progressBarBox">
                    <div id="progressBarBoxContent"></div>
                </div>
            </div>
        </form>
        </td>
    </tr>
    </table>
    </body>
</html>
