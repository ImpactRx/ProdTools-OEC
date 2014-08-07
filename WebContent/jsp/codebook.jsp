<%@ page import="java.util.List" %>
<%@ page import="com.targetrx.project.oec.bo.User" %>
<%@ page import="com.targetrx.project.oec.bo.CodeBook" %>
<%@ page import="com.targetrx.project.oec.service.CodeBookDao" %>
<%@ page import="org.apache.log4j.Logger" %>
<%
   String cbId = request.getParameter("codebookId");
   User u = (User)session.getAttribute("user");
   Logger log = Logger.getLogger("codebook.jsp");
%>
<% if (u == null) { %>
    <script language="Javascript">
    window.opener.location = "/oec/main";
    window.close();
    </script>
<% } else { %>
<%
	int codebookId = 0;
	String codebookName = "";
	String description = "";
	if (request.getParameter("codebook_id") != null)
	{
		try
		{
			codebookId = Integer.parseInt(request.getParameter("codebook_id"));
			CodeBookDao codebookDao = (CodeBookDao)application.getAttribute("codebook");
			List<CodeBook> list = codebookDao.getCodeBookName(String.valueOf(codebookId));
			log.debug("list="+list.toString());
			if (list != null && list.size() > 0)
			{
				CodeBook codebook = list.get(0);
				codebookName = codebook.getCodeBookName();
				description = codebook.getDescription();
			}
		} catch (NumberFormatException e)
		{
			log.error(e.getMessage(), e);
			codebookId = 0;
		}
  }
  log.debug("codebook id="+codebookId);
  %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Code Book</title>
        <link rel="stylesheet" type="text/css" href="/tools/css/oec.css">
        <link rel="stylesheet" type="text/css" href="/oec/css/<%= u.getRoleCode()%>.css">
        <script type='text/javascript' src='/oec/dwr/interface/codebooks.js'></script>
        <script type='text/javascript' src='/oec/dwr/engine.js'></script>
		<script type='text/javascript' src='/oec/dwr/util.js'></script>
        <script language="Javascript">
            var div_tag;
            var secs;
            var timerID = null;
            var timerRunning = false;
            var delay = 1000;
            DWREngine.setErrorHandler(errHandler);
            function errHandler()
            {
                document.getElementById('saveCode').style.visibility = 'visible';
                document.getElementById("messages").innerHTML = "";
            }
            function refreshParent()
            {
                window.opener.location.reload();
            }
            function checkBookName()
            {
                var s = DWRUtil.getValue("codeBookName");
                var filteredValues = "'";
                for  (i= 0; i < s.length; i++)  
                {  
                    var c = s.charAt(i);
                    if  (filteredValues.indexOf(c)  !=  -1) 
                    { 
                        document.getElementById("messages").innerHTML ="You cannot have single or double quotes in a CodeBook Name<br />";
                        document.getElementById("saveCode").style.visibility = 'hidden';
                        return false; 
                    }
                }
                codebooks.checkCodeBookName(DWRUtil.getValue("codeBookName"),displayCheck);
            }
            function displayCheck(data)
            {
                if (data)
                {
                    document.getElementById("messages").innerHTML = "Currently there is a Code Book with that Name.<br />";
                    document.getElementById("saveCode").style.visibility = 'hidden';
                }
            }
            function saveCodeBook(form,divtag)
            {
                div_tag = divtag;
                if (DWRUtil.getValue("codeBookName") == "")
                {
                    alert("Please enter a Code Book Name");
                    return false;
                }
                if (DWRUtil.getValue("description") == "")
                {
                    alert("Please enter a description for the CodeBook.");
                }
                codebooks.insertCodeBook(DWRUtil.getValue("codebook_id"), form.codeBookName.value, form.description.value, '<%= u.getUserName() %>');
                document.getElementById(div_tag).innerHTML = "Code Book "+form.codeBookName.value+" has been saved.";
                InitializeTimer();
            }
            
            function InitializeTimer()
            {
                // Set the length of the timer, in seconds
                secs = 10
                StopTheClock()
                StartTheTimer()
            }

            function StopTheClock()
            {
                if(timerRunning)
                    clearTimeout(timerID)
                    timerRunning = false
            }

            function StartTheTimer(div_tag)
            {
                if (secs==0)
                {
                    StopTheClock()
                    document.getElementById(div_tag).innerHTML = "";
                } else
                {
                    self.status = secs
                    secs = secs - 1
                    timerRunning = true
                    timerID = self.setTimeout("StartTheTimer()", delay)
                }
            }
        </script>
    </head>
    <body onUnload="refreshParent()">
    <a href="#" class="close" onClick="window.close()">Close Window</a>
    <div id="message"></div>
    <div id="contentAddBook">
    <form name="codebook">
    <input type="hidden" name="codebook_id" value="<%= codebookId %>"/>
    <table class="form1">
        <tr>
            <th colspan="2">Create a Code Book</th>
        </tr>
        <tr>
            <td class="label">Code Book Name:</td>
            <td class="inputs" valign="top"><input id="codeBookName" onblur="checkBookName();" type="text" name="codeBookName" value="<%= codebookName %>" class="inputs" size="50"/></td>
        </tr>
        <tr>
            <td class="label" valign="top">Code Book Description:</td>
            <td class="inputs" valign="top"><textarea id="description" rows="5" cols="40" name="description" size="20"><%= description %></textarea></td>
        </tr>
        <tr class="buttons">
            <td class="buttons" colspan="2">
            <font id="messages"></font>
            <a href="#" class="button"  id="saveCode" onClick="saveCodeBook(document.codebook,'message');"/>Save</a>
            <a href="#" class="button"  id="cancelCod" onClick="window.close();"/>Cancel</a>
            </td>            
        </tr>
    </table>
    </form>
    </div>
    </body>
</html>
<% } %>