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
        <title>Month End</title>
       
        <link rel="stylesheet" type="text/css" href="/tools/css/oec.css">
        <link rel="stylesheet" type="text/css" href="/oec/css/<%= u.getRoleCode()%>.css">
        <link rel="stylesheet" type="text/css" href="/tools/css/subnav.css">
        <script type='text/javascript' src='/oec/javascript/tabber.js'></script>
        <script type='text/javascript' src='/tools/javascript/common.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/programs.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/autocode.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codes.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/responses.js'></script>
        <script type='text/javascript' src='/oec/dwr/engine.js'></script>
				<script type='text/javascript' src='/oec/dwr/util.js'></script>
        <script type="text/javascript">
            /* Optional: Temporarily hide the "tabber" class so it does not "flash"
               on the page as plain HTML. After tabber runs, the class is changed
               to "tabberlive" and it will appear. */
            document.write('<style type="text/css">.tabber{display:none;}<\/style>');
        </script>
        <script>
            var pFlag = "0";
            var gotDates = "0";
            // FUNCTION EXECUTES WHEN THE PAGE LOADS
            function getPrograms()
            {
                programs.getProgramFacts(displayProgram);                
            }
            // ADDS THE VALUES TO THE SELECT BOX
            function displayProgram(data)
            {
                DWRUtil.addOptions("proglabelpost",["Program Labels."]);
                DWRUtil.addOptions("proglabelpost", data);                
            }
            function displayDropPost()
            {
                // HIDE THE SELECT BOX
                if (DWRUtil.getValue("checkAuditPost"))
                {
                    document.getElementById('proglabelpost').style.visibility = 'hidden';
                    document.getElementById('datesp').style.visibility = 'visible';
                    document.getElementById('mlabelsp').style.visibility = 'visible';                    
                } else
                // SHOW THE SELECT BOX
                {
                    if (pFlag == "0")
                    {
                        getPrograms();
                        pFlag = "1";
                    }
                    document.getElementById('proglabelpost').style.visibility = 'visible';
                    document.getElementById('datesp').style.visibility = 'hidden';
                    document.getElementById('mlabelsp').style.visibility = 'hidden';                    
                }
            }
            function displayDates(data)
            {
                document.getElementById('barsp').innerHTML = "<font class='load'>Loading <img src='/oec/images/progressBar.gif' border='0'/></font>";
                DWRUtil.addOptions("datesp",["Please select a date."]);
                for (var i = 0; i < data.length; i++)
		{
                    var result = data[i];
                    DWRUtil.addOptions("datesp",[result.fullDate]);	                    
                }
                document.getElementById('barsp').innerHTML = "";
            }
            if (gotDates == "0")
            {
                programs.getActiveMonthAndYear(displayDates);
                gotDates = 1;
            }
            function loadOec()
            {
                if (DWRUtil.getValue("checkAuditPost"))
                {
                    // Check to make sure the user selected a Date.
                    if (DWRUtil.getValue("datesp") == "Please select a date.")
                    {
                        alert("Please select a date.");
                        return false;
                    }
                    responses.loadOec(DWRUtil.getValue("datesp"),"null",displayLoadOec);
                } else
                {
                    // Check to make sure the User selected a program label
                    if (DWRUtil.getValue("proglabelpost") == "Program Labels.")
                    {
                        alert("Please select a Program Label");
                        return false;
                    }
                    responses.loadOec("null",DWRUtil.getValue("proglabelpost"),displayLoadOec);
                }
            }
            function displayLoadOec()
            {
                document.getElementById("message").innerHTML = "Loading of OEC has been queued.";
            }
        </script>
        
    <body class="admin">
        <div id="head">
            <center class="title">Admin Console (Loads)</center>
            <jsp:include page="admin_nav.jsp"/>
        </div>
            <p id="barsp"></p>
            <form name="checkv"/>
            <div class="tabber">
                 <div class="tabbertab">
                      <h2>Load OEC</h2>
                      <span id="message"></span>
                      <p>
                          <p>
                        <span id="mlabelsp">Date: </span><select name="datesp" id="datesp">                            
                        </select>                          
                      
                        &nbsp;&nbsp; <input type="checkbox" name="checkAuditPost" id="checkAuditPost" value="true" onclick="displayDropPost()" checked/>Audit Only 
                        &nbsp;&nbsp; <select id="proglabelpost" name="proglabelpost" style="width: 40mm" style="visibility: hidden;"></select>
                        <a href="#" class="buttonU" onClick="loadOec()">Load Open Ends</a></p>
                        </p>         
                      </p>
                      <p id="tab1"></p>
            </div>                 
            </form>
    </body>
    <script>
        
    </script>
 <% } %>
</html>

