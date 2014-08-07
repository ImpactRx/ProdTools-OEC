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
        <title>Admin Question Setup</title>
        <link rel="stylesheet" type="text/css" href="/tools/css/oec.css">
        <link rel="stylesheet" type="text/css" href="/oec/css/<%= u.getRoleCode()%>.css">
        <script type='text/javascript' src='/tools/javascript/common.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codebooks.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/cbg.js'></script>
        <script type='text/javascript' src='/oec/dwr/engine.js'></script>
	<script type='text/javascript' src='/oec/dwr/util.js'></script>
        <script language="JavaScript">
        function buildPage()
        {
            // GET ALL THE CODEBOOK GROUPS
            cbg.getAllCodebookGroup(displayGroups);
        }
        // DISPLAYS THE CODEBOOK GROUPS
        function displayGroups(data)
        {
            document.getElementById('message').innerHTML = "";
            DWRUtil.removeAllOptions("groups");
            DWRUtil.addOptions("groups",["Please select a Codebook Group."]);
            DWRUtil.addOptions("groups",data);
        }
        // Shows all the edit modules to the user
        function showEdit()
        {
            if (DWRUtil.getValue("groups") == "Please select a Codebook Group.")
            {
                alert("Please select a Codebook Group.");
                return false;
            }
            cbg.getCodebookGroup(DWRUtil.getValue("groups"),displayGroup);
            document.getElementById('editcb').style.visibility = 'visible';
            document.getElementById('eventlist').style.visibility = 'hidden';
            document.getElementById('linkevent').style.visibility = 'hidden';
            document.getElementById('linkcodes').style.visibility = 'hidden';
            document.getElementById('linkcode').style.visibility = 'hidden';
            DWRUtil.setValue("groupState","edit");
        }
        // When the user selects a new code group reset the GUI
        function checkGroup()
        {
            document.getElementById('editcb').style.visibility = 'hidden';
            document.getElementById('eventlist').style.visibility = 'hidden';
            document.getElementById('linkevent').style.visibility = 'hidden';
            document.getElementById('linkcodes').style.visibility = 'hidden';
            document.getElementById('linkcode').style.visibility = 'hidden';
        }
        function displayGroup(data)
        {
            for (var i=0; i < data.length; i++)
            {
                var result = data[i];
                DWRUtil.setValue("cbid",result.codebookGroupId);
                DWRUtil.setValue("grpname",result.groupName);
                DWRUtil.setValue("grpdesc",result.description);
            }
        }
        // Shows all the modules needed to add a Code Group
        function showAdd()
        {
            cbg.getNextCodebookGroupId(displayId);
            DWRUtil.setValue("grpname","");
            DWRUtil.setValue("grpdesc","");
            document.getElementById('editcb').style.visibility = 'visible';
            document.getElementById('eventlist').style.visibility = 'hidden';
            document.getElementById('linkevent').style.visibility = 'hidden';
            document.getElementById('linkcodes').style.visibility = 'hidden';
            document.getElementById('linkcode').style.visibility = 'hidden';
            DWRUtil.setValue("groupState","save");
        }
        // Display the Code Group Id
        function displayId(data)
        {
           DWRUtil.setValue("cbid",data); 
        }
        // Save the codebook group id
        function saveCodebookGroup()
        {
            if (DWRUtil.getValue("groupState") == "save")
            {
                cbg.addCodebookGroup(DWRUtil.getValue("cbid"),DWRUtil.getValue("grpname"),DWRUtil.getValue("grpdesc"),displaySave);
            } else 
            {
                cbg.updateCodebookGroup(DWRUtil.getValue("cbid"),DWRUtil.getValue("grpname"),DWRUtil.getValue("grpdesc"),displaySave);
            }
        }
        // Display message to the user after the save and then reload the Codebook Group drop down
        function displaySave()
        {
            buildPage();
            document.getElementById('message').innerHTML = "Codebook Group saved";
        }
        // Remove the group from the database. Before executing the command give
        // the user a chance to cancel that execution.
        function removeGroup()
        {
            if (DWRUtil.getValue("groups") == "Please select a Codebook Group.")
            {
                return false;
            }
            var result = confirm("Are you sure you wish to remove this Code Group");
            if (!result)
            {
                return false;
            }
            cbg.deleteCodebookGroup(DWRUtil.getValue("groups"),displayDelete);
        }
        // Display message to the user after a codebook group has been removed
        function displayDelete()
        {
            document.getElementById('message').innerHTML = "Codebook Group removed";
            buildPage();
        }
        // Show the modules needed to link a program event to a code group
        function linkEvent()
        {
            alert("Finish coding");
            if (DWRUtil.getValue("groups") == "Please select a Codebook Group.")
            {
                alert("Please select a Codebook Group.");
                return false;
            }
            document.getElementById('linkevent').style.visibility = 'visible';
            document.getElementById('eventlist').style.visibility = 'visible';
            document.getElementById('editcb').style.visibility = 'hidden';
            document.getElementById('linkcodes').style.visibility = 'hidden';
            document.getElementById('linkcode').style.visibility = 'hidden';
        }
        function assignCodes()
        {
            if (DWRUtil.getValue("groups") == "Please select a Codebook Group.")
            {
                alert("Please select a Codebook Group.");
                return false;
            }
            document.getElementById('linkcodes').style.visibility = 'visible';
            document.getElementById('linkcode').style.visibility = 'visible';
            document.getElementById('linkevent').style.visibility = 'hidden';
            document.getElementById('eventlist').style.visibility = 'hidden';
            document.getElementById('editcb').style.visibility = 'hidden';
            //codebooks.getAllCodeBooks(displayCodeBooks);
            codebooks.getEveryCodeBook(displayCodeBooks);
            codebooks.getCodebookFromGroups(DWRUtil.getValue("groups"),displayCodebookToGrps);
        }
        // Displays a list of Codebooks in a drop down
        function displayCodeBooks(data)
        {
            DWRUtil.removeAllOptions("codebooks");
            DWRUtil.addOptions("codebooks",["Please select a Codebook."]);
            DWRUtil.addOptions("codebooks",data);
        }
        // Display the Codebooks that are associated to the Codebook Group
        function displayCodebookToGrps(data)
        {
            DWRUtil.removeAllOptions("codelist");
            DWRUtil.addOptions("codelist",data);            
        }
        function saveAssign()
        {
            if (DWRUtil.getValue("codebooks") == "Please select a Codebook.")
            {
                alert("Please select a Codebook.");
                return false;
            }
            cbg.assignCodeBook(DWRUtil.getValue("groups"),DWRUtil.getValue("codebooks"),displayAssign);            
        }
        function displayAssign()
        {
            codebooks.getCodebookFromGroups(DWRUtil.getValue("groups"),displayCodebookToGrps);
        }
        </script>
   </head>
<body class="admin" onload="buildPage()">
    <div id="head">
        <center class="title">Admin Console</center>
        <jsp:include page="admin_nav.jsp"/>
    </div>
    <h1>Codebook Group Maintenance</h1>
    <div id="cbaction">
    <a class="adminLink" href="#" onclick="showAdd()">Add</a> | 
    <a class="adminLink" href="#" onclick="showEdit()">Edit</a> | 
    <a class="adminLink" href="#" onclick="removeGroup()">Remove</a> |
    <a class="adminLink" href="#" onclick="assignCodes()">Assign Codebooks</a>
    </div>
    <div id="cbsetup">
    Codebook Groups: <select id="groups" name="groups" style="width: 60mm" onchange="checkGroup()"></select>
    </div>
    <!-- ******************** MODULES FOR EDITING AND ADDING CODE GROUPS ********************  -->
    <input type="hidden" id="groupState" value=""/>
    <div id="editcb">
        <table class='groupResults' width="400">
        <tr>
            <th colspan="2">Codebook Group</th>
        </tr>
        <tr>
            <td colspan="2"><font style="color: green;" id="message"></font></td>
        </tr>
        <tr>
            <td>Codebook Group Id: </td>
            <td><input id="cbid" value="" size="8" disabled/></td>
        </tr>
        <tr>
            <td align="right">Group Name:</td>
            <td><input id="grpname" value="" size="25"/></td>
        </tr>
        <tr>
            <td align="right">Description:</td>
            <td><input id="grpdesc" value="" size="40"/></td>
        </tr>
        <tr>
            <td colspan="2"><center><a href="#" class="buttonU" onClick="saveCodebookGroup()">Save</a></center></td>
        </tr>
        </table>
    </div>
    <!-- ******************** END OF MODULES FOR EDITING AND ADDING CODE GROUPS ********************  -->
    <!-- ******************** MODULES FOR LINKING PROGRAM EVENTS ******************** -->
    <div id="linkevents">
        <select  id="eventlist" value="" style="width: 100mm" onchange="" multiple size="9"/></select>
    </div>
    <div id="linkevent">
        <table class='groupResults' width="340">
        <tr>
            <th colspan="2">Link a Program Event</th>
        </tr>
        <tr>
            <td>Program Event: </td>
            <td><select  id="proevent" value="" style="width: 60mm" onchange=""/></select></td>
        </tr>
        <tr>
            <td colspan="2"><center><a href="#" class="buttonU" onClick="saveCodebookGroup()">Save</a></center></td>
        </tr>
        </table>
    </div>
    <!-- ******************** END OF MODULES FOR LINKING PROGRAM EVENTS ******************** -->
    <!-- ******************** MODULES FOR LINKING CODEBOOKS TO GROUPCODES ******************** -->
    <div id="linkcodes">
        <select  id="codelist" value="" style="width: 100mm" onchange="" multiple size="9"/></select>
    </div>
    <div id="linkcode">
        <table class='groupResults' width="340">
        <tr>
            <th colspan="2">Link a Codebook</th>
        </tr>
        <tr>
            <td>Code Books: </td>
            <td><select  id="codebooks" value="" style="width: 60mm" onchange=""/></select></td>
        </tr>
        <tr>
            <td colspan="2"><center><a href="#" class="buttonU" onClick="saveAssign()">Assign</a></center></td>
        </tr>
        </table>
    </div>
    <!-- ******************** END OF MODULES FOR LINKING CODEBOOKS TO GROUPCODES ******************** -->
</body>
<% } %>
</html>