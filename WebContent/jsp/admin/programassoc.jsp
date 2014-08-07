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
        <title>Program Assoc.</title>
        <link rel="stylesheet" type="text/css" href="/tools/css/oec.css">
        <link rel="stylesheet" type="text/css" href="/oec/css/<%= u.getRoleCode()%>.css">
        <script type='text/javascript' src='/tools/javascript/common.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codebooks.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/cbg.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/programs.js'></script>
        <script type='text/javascript' src='/oec/dwr/engine.js'></script>
	<script type='text/javascript' src='/oec/dwr/util.js'></script>
        <script language="JavaScript">
        function buildPage()
        {
            programs.getAllStudyTypes(displayStudyType);
        }
        function displayStudyType(data)
        {
        		document.getElementById("message").innerHTML = "";
            DWRUtil.addOptions("st",["Please select a Study Type."]);
            for (var i = 0; i < data.length; i++)
            {
                result = data[i];
                DWRUtil.addOptions("st",[result.studyTypeCode]);
            }
            DWRUtil.removeAllOptions("codegrp");
            DWRUtil.removeAllOptions("plabel");
            document.getElementById("assocgroups").innerHTML = "";
        }
        function getProgramEvents()
        {
            DWRUtil.removeAllOptions("plabel");
            DWRUtil.removeAllOptions("codegrp");
            document.getElementById("message").innerHTML = "";
			programs.getAllOpenProgramLabels(DWRUtil.getValue("st"), displayProgramLabel);
        }
        function displayProgramLabel(data)
        {
            DWRUtil.addOptions("plabel",["Please select a Program Label"]);
            document.getElementById("message").innerHTML = "";
            DWRUtil.addOptions("plabel",data);            
            document.getElementById("assocgroups").innerHTML = "";
        }
        function getCodeGroups()
        {
        		document.getElementById("message").innerHTML = "";
        	  cbg.getCodebookGroupAssoc(DWRUtil.getValue("plabel"),displayAssoc);
        		DWRUtil.removeAllOptions("codegrp");
        		cbg.getCodebookGroups(DWRUtil.getValue("plabel"),displayCodeGroups);
        }
        function getCodeGroupsClear()
        {
        	cbg.getCodebookGroupAssoc(DWRUtil.getValue("plabel"),displayAssoc);
					DWRUtil.removeAllOptions("codegrp");
        	cbg.getCodebookGroups(DWRUtil.getValue("plabel"),displayCodeGroups);
        }
        function displayAssoc(data)
        {
        	  var html = "";
        	  for (var i = 0; i < data.length; i++)
            {
                result = data[i];
                html  = html+"<b>"+result.groupName+"</b>";
                if (result.activeEvent == "A")
                {
                	html = html+"&nbsp;&nbsp;&nbsp;<a href=\"#\" onClick=\"removeAssoc('"+DWRUtil.getValue("plabel")+"')\">Remove</a><br />";
                	
                } else
                {
                	html = html+"<br />";
                }
            }    
            document.getElementById("assocgroups").innerHTML = html;
            
        }
        function displayCodeGroups(data)
        {
            DWRUtil.addOptions("codegrp",["Please select a Codebook Group."]);
            DWRUtil.addOptions("codegrp",data);                
        }
        function removeAssoc(value)
        {
        	cbg.deleteAssoc(value,getCodeGroups);
        }
        function saveAssoc()
        {
            cbg.addCodebookGroupsPrograms(DWRUtil.getValue("plabel"),DWRUtil.getValue("codegrp"),displayMessage);            
        }
        function displayMessage()
        {
            document.getElementById("message").innerHTML = "Program Assocation has been made.";
            getCodeGroupsClear();
        }
        </script>
    </head>
   <body class="admin" onload="buildPage();">
    <div id="head">
        <center class="title">Admin Console</center>
        <jsp:include page="admin_nav.jsp"/>
    </div>
    <h1>Program Event Assoc.</h1>
    
    <table class=tier1">
    <tr>
    <td>
    <!-- ********************* MODULES FOR STUDY TYPE CODE *********************** -->
    <div id="studytype">
        <table class='groupResults'>
        <tr>
            <td nowrap align="right">Study Type Code: </td>
            <td nowrap><select id="st" value="" style="width: 60mm" onchange="getProgramEvents()"/></select></td>
        </tr>
        </table>
    </div>
    <!-- ********************* END OF MODULES FOR STUDY TYPE CODE ********************** -->
	</td>
	</tr>
	<tr>
	<td>
    <!-- ******************** MODULES FOR LINKING PROGRAM EVENTS ******************** -->
    <div id="programlabel">
        <table class='groupResults'>
        <tr>
            <td nowrap align="right">Program Label: </td>
            <td nowrap><select id="plabel" value="" style="width: 60mm" onchange="getCodeGroups()"/></select></td>
        </tr>
        </table>
    </div>
	</td>
	</tr>   
	<tr>
		<td>
	    <!-- ******************** DISPLAY CODEBOOK GROUPS ASSOCIATED TO THIS PROJECT ******************** -->
	    <div id="assocgrps">
	        <table class='groupResults'>
	        <tr>
	            <td nowrap align="right">Associated Codebook Group: </td>
	            <td nowrap><span id="assocgroups"></span></td>
	        </tr>
	        </table>
	    </div>
		</td>
	</tr>  
	<tr>
	<td>
    <!-- ******************** END OF MODULES FOR LINKING PROGRAM EVENTS ******************** -->
    
    <!-- ******************** MODULES FOR CODEBOOK GROUPS ******************** -->
    <div id="codebookgroup1">
        <table class='groupResults'>
        <tr>
            <td nowrap align="right">Codebook Group: </td>
            <td nowrap><select id="codegrp" value="" style="width: 60mm" onchange=""/></select></td>
        </tr>
        </table>
    </div>
  <!-- ******************** END OF MODULES FOR CODEBOOK GROUPS ******************** -->
  </td>
  </tr>
	<tr>
	<td>
    <!-- ******************** MODULES FOR SAVING AND UPDATE ******************** -->
    <div id="assocsave">
        <table class='groupResults'>
        <tr>
            <td><a href="#" class="buttonU" onClick="saveAssoc()">Save</a></td>            
        </tr>
        </table>
    </div>
    </td>
    </tr>
    </table>
    <!-- ******************** END OF MODULES FOR SAVING AND UPDATE ******************** -->
    <p><font id="message" style="color: green; font-weight: bold;"></font></p>
    </body>
<% } %>
</html>
