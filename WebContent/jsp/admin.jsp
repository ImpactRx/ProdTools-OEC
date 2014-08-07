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
        <title>Open Ended Coding Tool</title>
        <link rel="stylesheet" type="text/css" href="../style/oec.css">
        <link rel="stylesheet" type="text/css" href="../style/<%= u.getRoleCode()%>.css">
        <link rel="stylesheet" type="text/css" href="../style/layout.css">
        <script type='text/javascript' src='../javascript/event.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/programs.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/responses.js'></script>
        <script type='text/javascript' src='/oec/dwr/engine.js'></script>
	<script type='text/javascript' src='/oec/dwr/util.js'></script>
        <script language="Javascript">
        function buildPage()
        {
            responses.getLockedResponses(displayStatus);
            responses.getTaggedResponses(displayTagged);
            programs.getProgramFacts(displayProgram);
        }
        function displayStatus(data)
        {
            DWRUtil.removeAllOptions("statusCoder");
            DWRUtil.addOptions("statusCoder", data);
        }
        function displayTagged(data)
        {
            DWRUtil.removeAllOptions("reviewResp");	 	
            DWRUtil.addOptions("reviewResp", data);	 	
           
        }
        function displayProgram(data)
        {
            DWRUtil.addOptions("plabel",["Program Labels."]);
            DWRUtil.addOptions("plabel", data);              
        }
        function getGraph()
        {
            responses.getProgramQuesGraph(DWRUtil.getValue("plabel"),displayGraph);
        }
        function displayGraph(data)
        {
            var total_count = 0;
            var total_inprogress = 0;
            var total_uncoded = 0;
            var total_tagged = 0;
            var total_unverify = 0;
            var out = "<table class=\"prgGraph\" border=\"1\">";
            out = out+"<th>Group</th><th>Program Event Id</th><th>Total Questions</th><th>Unverifed</tv><th>Tagged</th><th>Verified</th><thnew</th>";
            for (var i = 0; i < data.length; i++)
            {
                var result = data[i];
                out = out+"<tr><td>"+result.groupQuestionLabel+"<td></td>"+result.programEventId+"</td><td>"+result.totalCount+"</td><td>"+result.countUnverify+"</td><td>"+result.countTag+"</td><td>"+result.countVerify+"</td><td>"+result.coutnNew+"</td></tr>";                            	 	
                total_count += parseInt(result.totalCount);
                total_inprogress += parseInt(result.countUnverify)+parseInt(result.countVerify)+parseInt(result.countTag);
                if (result.countTag != "0")
                {
                    total_tagged += parseInt(result.countTag);
                }
                if (result.countUnverify != "0")
                {
                    total_unverify += parseInt(result.countUnverify);
                }
            }
            document.getElementById('resultGraph').innerHTML = out+"</table>";
            total_uncoded = total_count - total_inprogress;
            total_tag_unverify = total_tagged + total_unverify;
            var per_uncoded = 0;
            var per_inprogess = 0;
            var per_tagged = 0;
            var per_unverify = 0;
            per_uncoded = 100 * fmtPercent(total_uncoded/total_count);
            per_inprogress = 100 * fmtPercent(total_inprogress/total_count);
            per_tagged =  100 * fmtPercent(total_tagged/total_tag_unverify);
            per_unverify =  100 * fmtPercent(total_unverify/total_tag_unverify);
            
            var graph_table = "<table align=\"left\" class=\"graph\" width=\"100\"><tr><th colspan=\"2\">Overall ("+total_count+")</th></tr>";
            graph_table = graph_table+"<tr><td><img src=\"../images/red_dot.gif\" height=\"50\" width=\""+per_uncoded+"\" alt=\""+total_uncoded+" uncoded.\"/></td></tr>";
            graph_table = graph_table+"<tr><td> <img src=\"../images/green_dot.gif\" height=\"50\" width=\""+per_inprogress+"\" alt=\""+total_inprogress+" have been coded.\"/></td></tr></table>";
            
            graph_table = graph_table+ "<table align=\"left\" class=\"graph\" width=\"100\"><tr><th colspan=\"2\">Tag & UV ("+total_tag_unverify+")</th></tr>";
            graph_table = graph_table+"<tr><td><img src=\"../images/red_dot.gif\" height=\"50\" width=\""+per_tagged+"\" alt=\""+total_tagged+" tagged.\"/></td></tr>";
            graph_table = graph_table+"<tr><td> <img src=\"../images/green_dot.gif\" height=\"50\" width=\""+per_unverify+"\" alt=\""+total_unverify+" unverified.\"/></td></tr></table>";
            //alert(graph_table);
            document.getElementById('resultGraphTable').innerHTML = graph_table;
        }
        function fmtPercent(value)   {
            result=Math.floor(value)+".";
            var cents=100*(value-Math.floor(value))+0.5;
            result += Math.floor(cents/10);
            result += Math.floor(cents%10);
            return result;
        }
        function refreshProgram()
        {
            if (DWRUtil.getValue("regraph") == true)
            {
                InitializeTimer();
            } else 
            {
                StopTheClock();
            }
        }
        var secs;
        var timerID = null;
        var timerRunning = false;
        var delay = 1000;

        function InitializeTimer()
        {
            // Set the length of the timer, in seconds
            secs = 30;
            StopTheClock();
            StartTheTimer();
        }

        function StopTheClock()
        {
            if(timerRunning)
                clearTimeout(timerID);
            timerRunning = false;
        }

        function StartTheTimer()
        {
            if (secs==0)
            {
                getGraph();
                InitializeTimer();                
            }
            else
            {
                self.status = secs
                secs = secs - 1
                timerRunning = true
                timerID = self.setTimeout("StartTheTimer()", delay)
            }
        }
        function unLock()
        {
            if (DWRUtil.getValue("statusCoder") == "")
            {
                alert("Please select a Coder.");
                return false;
            }
            responses.checkIn(DWRUtil.getValue("statusCoder"), displayLock);           
        }
        function displayLock()
        {
            responses.getLockedResponses(displayStatus);
        }

        </script>
        
</head>
<body class="admin" onload="buildPage();">
    <div id="head">
        <center class="title">Admin Console</center>
        <jsp:include page="admin_nav.jsp"/>
    </div>
    <div id="statusC">
        &nbsp;<font class="label">Coders and their locked responses.</font><br/>
        &nbsp;<select id="statusCoder" name="statusCoder" multiple size="9" style="width: 100mm" ></select><br />
        <center><a href="#" class="buttonU" onClick="unLock();">Unlock Responses</a>&nbsp;</center>
    </div>
    <div id="review">
        &nbsp;<font class="label">Tagged responses in the queue.</font><br/>
        &nbsp;<select id="reviewResp" name="reviewResp" multiple size="9" style="width: 100mm" ></select><br />
        <center><a href="#" class="buttonU" onClick="">Detail</a>&nbsp;<a href="#" class="buttonU" onClick="">Print Report</a></center>
    </div>
    <div id="adminProgramGraph">
        <input id="regraph" type="checkbox" onclick="refreshProgram();">Refresh every 30 Seconds<br/>
        Select a Program Label: <select id="plabel" name="plabel" style="width: 70mm" onchange="getGraph();"></select>
        <div id="resultGraph"></div>
        
    </div>
    <div id="realGraph">Response Graphs:<br />
        <div id="resultGraphTable"></div>
    </div>
    <!--<div id="piechart"> </div>-->
</body>
</html>
<% } %>
