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
        <title>JSP Page</title>
        <link rel="stylesheet" type="text/css" href="/tools/css/oec.css">
        <link rel="stylesheet" type="text/css" href="/oec/css/<%= u.getRoleCode()%>.css">
        <link rel="stylesheet" type="text/css" href="/tools/css/subnav.css">
        <script type='text/javascript' src='/oec/javascript/tabber.js'></script>
        <script type='text/javascript' src='/tools/javascript/common.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/autocode.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codes.js'></script>
        <script type='text/javascript' src='/oec/dwr/engine.js'></script>
	<script type='text/javascript' src='/oec/dwr/util.js'></script>
        <script language="Javascript">
            function buildPage()
            {
                document.getElementById('bar').innerHTML = "<font class='load'>Loading <img src='/images/progressBar.gif' border='0'/></font>";
                autocode.getAutoCodeDetailReport(displayDetail);
            }
            function displayDetail(data)
            {
                var display = "<table width=\"705\" class=\"tagtype\"><tr><th>Label</th><th>Program Event</th><th>Tag Type Description</th><th>Number</th></tr>";
                for (var i = 0; i < data.length; i++)
                {
                    var result = data[i];
                    display = display+"<tr><td nowrap>"+result.programLabel+"</td><td>"+result.programEventId+"</td><td>"+result.description+"</td><td>"+result.discrepNumber+"</td></tr>";
                }
                display = display+"</table>";
                document.getElementById('bar').innerHTML = "";
                document.getElementById('result').innerHTML = display;
            }
        </script>
    </head>
    <body onload="buildPage()">
        <div id="head">
            <center class="title">Admin Console (Tag Type Code Report)</center>
            <jsp:include page="admin_nav.jsp"/>
        </div>
        <div id="result">
             <p id="bar"></p>
        </div>
    
   
    
    </body>
 <% } %>
</html>
