<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ page import="com.targetrx.project.oec.bo.User" %>
<%
   User u = (User)session.getAttribute("user");
   String dyn_error = (String) session.getAttribute("DYNAMICERROR");
%>
<% if (u == null) { %>
    <script language="Javascript">
    window.opener.location = "/oec/main";
    window.close();
    </script>
<% } else { %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
  
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Export Codebook</title>
        <link rel="stylesheet" type="text/css" href="/tools/css/oec.css">
        <link rel="stylesheet" type="text/css" href="/oec/css/<%= u.getRoleCode()%>.css">
        <script language="JavaScript">
            function getXslFile()
            {
                var caller = '<%= request.getParameter("caller") %>';
                if (caller == 'response')
                {
                    location.href = '/oec/export?programid=<%= request.getParameter("programid") %>&caller=response';
                } else if (caller == 'dynamic')
                {
                	//alert("<%= request.getParameter("peids") %>");
                	location.href = '/oec/export?sDate=<%= request.getParameter("sDate") %>&cells=<%= request.getParameter("cells") %>&peids=<%= request.getParameter("peids") %>&mids=<%= request.getParameter("mids") %>&pids=<%= request.getParameter("pids") %>&qids=<%= request.getParameter("qids") %>&caller=dynamic';
                }else
                {
                    location.href = '/oec/export?codebookid=<%= request.getParameter("codebookid") %>&caller='+caller;
                    //window.close();
                }
            }
        </script>
    </head>
    <body onload="getXslFile()">
    <a href="#" class="close" onClick="window.close()">Close Window</a> 
        <p>
        <font style="font-size: 13pt; color: #00529B;">CODY Exporting Tool</font>
        </p>
        <p>
            <b>Your Excel file should be arriving shortly. Please be patient. Depending on how big your file is this 
            may take several minutes.</b>
        </p>
        <p>
            You can save the excel file to anywhere on your hardrive and rename it.
        </p>
        <% if ((dyn_error != null) && (dyn_error != "null"))
        { %>
        	<font style="color: red;"><%= dyn_error %></font>
        <% } %>
    </body>
</html>
<% } %>
