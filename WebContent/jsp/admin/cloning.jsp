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
        <title>Cloning</title>
        <link rel="stylesheet" type="text/css" href="/tools/css/oec.css">
        <link rel="stylesheet" type="text/css" href="/oec/css/<%= u.getRoleCode()%>.css">
        <script type='text/javascript' src='/tools/javascript/common.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codebooks.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/cbg.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codes.js'></script>
        <script type='text/javascript' src='/oec/dwr/engine.js'></script>
	<script type='text/javascript' src='/oec/dwr/util.js'></script>
        
        <script language="Javascript">
            var resultClone = "";
            function buildPage()
            {
                codebooks.getClonableCodeBooks(displayCloneBooks);
                //cbg.getAllCodebookGroup(displayCloneGroups);
            }
            function displayCloneBooks(data)
            {
                DWRUtil.addOptions("codebook", ["Please select a Code Book"]);
                DWRUtil.addOptions("codebook", data);
            }
            function displayCloneGroups(data)
            {
                DWRUtil.addOptions("group", ["Please select a Group"]);
                DWRUtil.addOptions("group", data);
            }
            // FUNCTION CHECKS TO SEE IF BOOK HAS BEEN SELECTED TO CLONE. IF IT HAS
            // THEN IT CALLS THE JAVA METHOD CLONECODEBOOK
            function clone()
            {
                if (DWRUtil.getValue("codebook") == "Please select a Code Book")
                {
                    alert("Please select a Codebook to clone.");
                    return false;
                }
                document.getElementById('bar').innerHTML = "<font class='load'>Loading <img src='/images/progressBar.gif' border='0'/></font>";
                document.getElementById('cloneButton').innerHTML = "";
                if (DWRUtil.getValue("codebook") != "Please select a Code Book")
                {
                    //alert("Codebook "+DWRUtil.getValue("codebook"));
                    codebooks.cloneCodeBook(DWRUtil.getValue("codebook"),"temp name",displayCodeBookClone);
                }
                //if (DWRUtil.getValue("group") != "Please select a Group")
                //{
                //    alert("Group "+DWRUtil.getValue("group"));
                //    codebooks.cloneCodeBookGroup(DWRUtil.getValue("group"));
                //}
            }
            function displayCodeBookClone(data)
            {
                resultClone = data;
                codebooks.getCodeBookName(data,displayChangeName)                
            }
            function displayChangeName(data)
            {
                var message = "CodeBook Id : "+ resultClone +" has been setup<br />";
                message = message+"<p>";
                for (var i = 0; i < data.length; i++)
                {
                    var cb = data[i];
                    message = message+"Please Change the Name of the CodeBook: ";
                    message = message+"<input type=\"text\" id=\"cloneName\" value=\""+cb.codeBookName+"\"/>";
                    message = message+"<input type=\"hidden\" id=\"cloneId\" value=\""+resultClone+"\"/></p>";
                }
                document.getElementById('clone_setup').innerHTML = message;   
                document.getElementById('cloneButton').innerHTML = "<a href=\"#\" class=\"buttonU\" onClick=\"reName()\">ReName</a>";
                alert(message);
            }
            function reName()
            {
                //alert(DWRUtil.getValue("cloneId")+" "+DWRUtil.getValue("cloneName"));
                codebooks.updateCodeBookName(DWRUtil.getValue("cloneId"),DWRUtil.getValue("cloneName"),displayGoodChange);
            }
            function displayGoodChange()
            {
                document.getElementById('clone_setup').innerHTML = "CodeBook Name has been changed. Thank you.";
                document.getElementById('cloneButton').innerHTML = "";
            }
        </script>
    </head>
    <body class="admin" onload="buildPage()">
    <div id="head">
        <center class="title">Admin Console (Cloning)</center>
        <jsp:include page="admin_nav.jsp"/>
    </div>
    <div id="clone_setup">
        <p id="bar"></p>
        <p>
            <font style="color: yellow; font-weight: bold;">Instructions: </font>Select the name of the Codebook 
            that you wish to clone. Once you made your selection then press 
            the clone button. <br />After you clone the book you will be then asked to rename the cloned  book.
        </p>
       
        <font class="clone">Cloneable Codebooks: </font><select id="codebook" name="codebook" style="width: 70mm"></select> 
        <!--<font class="clone">Cloneable Groups: </font><select id="group" name="group" style="width: 70mm"></select> -->
    </div>
    <div id="cloneButton">
       <a href="#" class="buttonU" onClick="clone()">Clone</a>
    </div>
    
    </body>
 <% } %>
</html>
