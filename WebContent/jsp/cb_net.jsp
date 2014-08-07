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
        <title>Net to Codebook</title>
        <link rel="stylesheet" type="text/css" href="/tools/css/oec.css">
        <link rel="stylesheet" type="text/css" href="/oec/css/<%= u.getRoleCode()%>.css">
        <script type='text/javascript' src='/tools/javascript/common.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codebooks.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codes.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/nets.js'></script>
        <script type='text/javascript' src='/oec/dwr/engine.js'></script>
	<script type='text/javascript' src='/oec/dwr/util.js'></script>        
    </head>
    <script language="Javascript">
        function buildPage()
        {
            codebooks.getCodeBookName(<%= cbId %>,buildCodeBook);
            nets.getCodeBookNets(<%= cbId %>,buildNetOptions);
        }
        function buildCodeBook(data)
        {
            for (var i = 0; i < data.length; i++)
            {
                var result = data[i];
                DWRUtil.setValue("bookName","Code Book Name: <u>"+result.codeBookName+"</u>");	 	
            }
        }
        function buildNetOptions(data)
        {
            DWRUtil.addOptions("netList",["Please select a net"]);
            DWRUtil.addOptions("netList",data);
        }
        function saveNetCodeBook()
        {
            if (DWRUtil.getValue("netList") == "Please select a net.")
            {
                alert("You must select a net first");
                return false;
            }
            nets.saveCodeBookNets(DWRUtil.getValue("netList"),'<%= cbId %>','<%= u.getUserName() %>',displayNet);
        }
        function displayNet(data)
        {
            document.getElementById("message").innerHTML= data;
        }
    </script>
    <body onload="buildPage();">
    <a href="#" class="close" onClick="window.close()">Close Window</a>
    <p><div id="bookInfoNet"><div id="bookName"></div></div></p>
    <div id="bookNet1"></div>
    <div id="contentNet">
        <span id="message"></span>
        <form name="net">
        <table class="formnet">
            <tr>
                <td class="label1">Select a Net: <select id="netList" style="width: 80mm"></select></td>
            </tr>
            <tr>
                <td class="buttons" colspan="2">
                <font id="message"></font>
                <a href="#" class="button"  id="saveCode" onClick="saveNetCodeBook();"/>Save</a>    
                <a href="#" class="button"  id="cancelCod" onClick="window.close();"/>Cancel</a>
                </td>    
            </tr>
       </table>
       </form>
     </div>
    </body>
</html>
<% } %>
