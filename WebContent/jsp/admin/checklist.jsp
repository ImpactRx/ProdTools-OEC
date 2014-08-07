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
        <title>Check List</title>
        <link rel="stylesheet" type="text/css" href="/tools/css/oec.css">
        <link rel="stylesheet" type="text/css" href="/oec/css/<%= u.getRoleCode()%>.css">
        <link rel="stylesheet" type="text/css" href="/tools/css/layout.css">
        <script type='text/javascript' src='/tools/javascript/common.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/monthend.js'></script>
        <script type='text/javascript' src='/oec/dwr/engine.js'></script>
	<script type='text/javascript' src='/oec/dwr/util.js'></script>
        <script language="JavaScript">
            var ckTable = "<center><table class=\"checklist\"><tr><th>Fielding Period / Program Label</th><th>Checks Conducted</th></tr><tr><td>";
            var endTable = "</td></tr></table></center>";
            function buildPage()
            {
                monthend.getCompleteCheckList(displayCheckList);
            }
            function displayCheckList(data)
            {
                //alert(data.length);
                var start = 0;
                var temp = "";
                var results = "";
                if (data.length == 0) 
                { 
                    document.getElementById("chklist").innerHTML = "<b>No checks have been conducted on any ACTIVE Programs.</b>";
                    return false;
                    }
                for (var i = 0; i < data.length; i++)
                {
                    
                    result = data[i];
                    
                    var fp = result.fieldingPeriod;
                    var pl = result.programLabel;
                    var cc = result.checkName;
                    var cv = result.checkValue;
                    if (start == 0) 
                    { 
                        if (fp != null)
                        {
                            temp = fp;
                        } else
                        {
                            temp = pl
                        }
                        start = 1;
                        results = ckTable+temp+"</td><td>"+cc;
                    } else
                    {
                        
                        if (fp != null)
                        {
                        
                            if (fp == temp)
                            {
                                results = results+", "+cc;
                            } else
                            {
                                results = results+"</td></tr><tr><td>"+fp+"</td><td>"+cc;
                                temp = fp;
                            }
                         } else 
                         {
                            if (pl == temp)
                            {
                                results = results+", "+cc;
                            } else
                            {
                                results = results+"</td></tr><tr><td>"+pl+"</td><td>"+cc;
                                temp = pl;
                            }
                         }
                        
                    }
                    
                }
                results = results+endTable;
                //alert(results);
                document.getElementById("chklist").innerHTML = results;
            }
        </script>
    </head>
    <body style="background-color: #6699CC; padding: 5px 5px 5px 5px; font-size: 13px;" onload="buildPage();">
        <a href="#" class="close" onClick="window.close()">Close Window</a>
        <center><h3 style="font-size: 20px; font-weight: bold; color: #00529B; text-decoration: underline;">
        CheckList For Open Programs
        </h3></center>
        <p>Checks that have to be conducted on programs before they are posted are:
            <ul>
                <li>Change Report</li>
                <li>Discrepancy Report</li>
                <li>Check/Verify</li>
                <li>Disconnected</li>
                <li>End Event</li>
            </ul>
            <p>Below are the checks that you have conducted on ACTIVE programs minus the Change Report and 
            Discrepancy Report.</p>
        </p>
        <span id="chklist"><font class='load'>Loading <img src='/oec/images/progressBar.gif' border='0'/></font><span>
    </body>
<% } %>
</html>
