<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ page import="com.targetrx.project.oec.bo.User" %>
<%
   String cbId = request.getParameter("codebookId");
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
        <title>Checkout</title>
        <link rel="stylesheet" type="text/css" href="/tools/css/oec.css">
        <link rel="stylesheet" type="text/css" href="/oec/css/<%= u.getRoleCode()%>.css">
        <script type='text/javascript' src='/tools/javascript/common.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codebooks.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codes.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/nets.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/responses.js'></script>
        <script type='text/javascript' src='/oec/dwr/engine.js'></script>
	<script type='text/javascript' src='/oec/dwr/util.js'></script>
        <script langauge="Javascript">
            function buildPage()
            {
            	document.getElementById('checkInMsg').style.visibility = 'hidden';
            	responses.checkInStatus("<%= u.getUserName() %>",displayCheckInStatus);
              programs.getProgramFacts(displayProgram);     
              document.getElementById('buttonsfilter').style.visibility = 'hidden';
            }
            function displayCheckInStatus(data)
            {
            	
            	if (data == "NO DATA")
            	{
            		//alert("GO AHEAD AND CHECK OUT");
            		document.getElementById('checkInMsg').style.visibility = 'hidden';
            	} else
            	{
            		//alert(data);
            		document.getElementById('filters').style.visibility = 'hidden';
            		document.getElementById('buttonsfilter').style.visibility = 'hidden';
            		document.getElementById('checkInMsg').style.visibility = 'visible';
            		document.getElementById('checkInMsg').innerHTML = "You are still in the process of checking in since "+data+" this may take a little while longer. Please close the window and check back in a couple of minutes.";
            	}
            }
            function displayProgram(data)
            {
                DWRUtil.addOptions("plabel",["Program Labels."]);
                DWRUtil.addOptions("plabel", data);                  
            }
            function viewResponses()
            {
                if (DWRUtil.getValue("plabel") == "Program Labels.")
                {
                    alert("Please select a Program Label.");
                    return false;
                } else 
                {
                    var peid = DWRUtil.getValue("plabel");
                    openWindowT('/oec/jsp/export.jsp?programid='+peid+'&caller=response');
                }
            }
            function openWindowT(url_string, url_title)
            {
                WindowObjectReference = window.open(url_string, url_title,"width=400,height=300,menubar=no,location=yes,resizable=yes,scrollbars=yes,status=yes");
            }
        </script>
    </head>
    <body onload="buildPage()">
    <a href="#" class="close" onClick="window.close()">Close Window</a><br />
    <p class="note">NOTE: Currently you can only check out 1000 records at one time. <a href="#" class="button"  id="viewR" onClick="viewResponses();"/>View Responses</a></p>
    <p id="checkInMsg" style="color:red; weight: bold;"></p>
    <jsp:include page="filter.jsp"/>
    </body>
</html>
<% } %>