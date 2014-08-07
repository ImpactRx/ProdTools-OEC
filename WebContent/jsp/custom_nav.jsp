<%
   String cbId = request.getParameter("codebookid");
   if ((cbId == null) || (cbId.equalsIgnoreCase("null")))
  {
    cbId = (String) session.getAttribute("codebookId");
  }
  session.setAttribute("codebookId",cbId);
%>

<table class="bodyBlue">
    <tr>
    <td id="navA" class="nav_link">
       <a href="/oec/main" class="nlink">View CodeBooks</a>
    </td>
    <td id="navA" class="nav_link">
       <a href="/oec/jsp/oec/custom.jsp?codebookid=<%= cbId %>" class="nlink">Map A Code</a>
    </td>
    <!-- <td id="navA" class="nav_link">
       <a href="/oec/jsp/oec/customnet.jsp?codebookid=<%= cbId %>" class="nlink">Map A Net</a>
    </td>-->        
    </tr>
</table>