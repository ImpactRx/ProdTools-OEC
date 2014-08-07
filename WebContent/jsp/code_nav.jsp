<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%
   String cbId = request.getParameter("codebookid");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<table class="nav">
    <tr>
        <td id="navA" class="nav_link">
            <a href="/oec/main" class="nlink">View code Books</a>
        </td>
        <td id="navP" class="nav_link">
            <a href="#" onClick="openWindowT('new_code.jsp?codebookId=<%= cbId %>');" class="nlink">New Code</a>
        </td>
        <td id="navP" class="nav_link">
            <a href="#" class="nlink" onClick="openWindowT('cb_net.jsp?codebookId=<%= cbId %>');">Net to CodeBook</a>
        </td>
        <td id="navP" class="nav_link">
            <a href="#" class="nlink" onClick="openWindowT('net_rel.jsp?codebookId=<%= cbId %>');">Net Relation</a>
        </td>
        <td id="navP" class="nav_link">
            <a href="#" class="nlink" onClick="openWindowP('autocode.jsp',800, 570 ,0 ,0 ,0 ,0 ,0 ,1 ,10 ,10 );">ACR</a>            
        </td>
        <td id="navP" class="nav_link">
            <a href="#" class="nlink" onClick="openWindowP('discrep.jsp',800, 600 ,0 ,0 ,0 ,0 ,0 ,1 ,10 ,10 );">Discrep</a>            
        </td>
        <td id="navAdmin" class="nav_link">
            <a href="admin/admin.jsp" class="nlink">Console</a>
        </td>
    </tr>
</table>
