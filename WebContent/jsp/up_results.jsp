<%@ page import="com.targetrx.project.oec.bo.User" %>
<%
  String cbId = request.getParameter("codebookid");
  User u = (User)session.getAttribute("user");
  String clientid = (String) session.getAttribute("clientId");
  String custommap = (String) session.getAttribute("customMap");
  String xlsFile = (String) session.getAttribute("uploadedFile");
  if ((cbId == null) || (cbId.equalsIgnoreCase("null")))
  {
    cbId = (String) session.getAttribute("codebookId");
  }
  session.setAttribute("codebookId",cbId);
%>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Dynamic CRAP Loader</title>
        <link rel="stylesheet" type="text/css" href="/oec/css/oec.css">
        <link rel="stylesheet" type="text/css" href="/oec/css/<%= u.getRoleCode()%>.css">
        <link rel="stylesheet" type="text/css" href="/oec/css/layout.css">
        <link rel="stylesheet" type="text/css" href="/oec/common/css/tools.css">
        <script type='text/javascript' src='/oec/dwr/interface/codebooks.js'></script>
	<script type='text/javascript' src='/oec/dwr/interface/client.js'></script>
	<script type='text/javascript' src='/oec/dwr/interface/custom.js'></script>
	<script type='text/javascript' src='/oec/dwr/interface/codes.js'></script>
	<script type='text/javascript' src='/oec/dwr/interface/nets.js'></script>
        <script type='text/javascript' src='/oec/javascript/event.js'></script>
	<script type='text/javascript' src='/oec/dwr/interface/eLoader.js'></script>
        <script type='text/javascript' src='/oec/dwr/engine.js'></script>
	<script type='text/javascript' src='/oec/dwr/util.js'></script>        
        <script language="JavaScript">
            function buildPage()
            {
                displayLoading("load");
                codebooks.getCodeBookName(<%= cbId %>,buildCodeBook);
                //custom.getCustomMapName("<%= custommap %>",displayCustomMapName);
                //displayCustomMapName("<%= custommap %>");
                //client.getClientName("<%= clientid %>",displayClientName);
                eLoader.loadExcel("<%= xlsFile %>","<%= cbId %>","<%= clientid %>","<%= custommap %>",displayLoad);
            }
            //FUNCTION DISPLAYS CODEBOOK NAME
            function buildCodeBook(data)
            {
                for (var i = 0; i < data.length; i++)
                {
                    var result = data[i];
                    DWRUtil.setValue("bookName","Working in CodeBook: "+result.codeBookName);	 	
                    codebook_name = result.codeBookName;
                    //alert(codebook_name);
                }
            }
            // FUNCTION DISPLAYS CUSTOM MAP NAME
            function displayCustomMapName(data)
            {
                //docuement.getElementById("custMapName").innerHTML = "Custom Map: <font style=\"color: green;\">"+data+"</font>");
            }
            // FUNCTION DISPLAYS CLIENT NAME
            function displayClientName(data)
            {
                DWRUtil.setValue("clientName","Client Name: <font style=\"color: green;\">"+data+"</font>");
            }
            function displayLoad(data)
            {
                hideLoading("load");
                document.getElementById("load").innerHTML = "<p><b>"+data+"</b></p>";
            }
             // DISPLAY LOADING IMAGE AND TEXT
            function displayLoading(value)
            {
                    document.getElementById(value).innerHTML = "<font class='load'>Loading <img src='/oec/images/progressBar.gif' border='0'/></font>";
            }
            // HIDE LOADING IMAGE AND TEXT
            function hideLoading(value)
            {
                document.getElementById(value).innerHTML = "";
            }
        </script>        
    </head>
    <body style="margin:0px 5px;" onload="buildPage();">
    <table width="800px" class="main">
    <tr>
        <td colspan="2">
	   <jsp:include page="top_nav.jsp"/>
	</td>
    </tr>
    <tr>
    	<td colspan="2">
            <table><tr><td><jsp:include page="custom_nav.jsp"/></td></tr></table>
    	</td>
    </tr>
    <tr height="15px"><td id="load" colspan="2"></td></tr>
    <tr>
        <td id="bookname">
        <b><span id="clientName"></span></b><br />
        <b><span id="bookName"></span></b><br />
        <b><span id="custMapName"></span></b><br /><p></p>
        </td>
    </tr>
    <tr>
    	<td colspan="2">
    	<div id="bldg">
    	
    	</div>
    	</td>
    </tr>
    <tr>
		   <td colspan="2">
		   <div id="display"></div>
		   </td>
    </tr>
    </table>

    </body>
    <head>
        <META HTTP-EQUIV="PRAGMA" CONTENT="NO-CACHE">
        <META HTTP-EQUIV="Expires" CONTENT="-1">
    </head>
</html>
