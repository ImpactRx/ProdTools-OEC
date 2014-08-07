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
        <title>Net and Subnet Relationship</title>
        <link rel="stylesheet" type="text/css" href="/tools/css/oec.css">
        <link rel="stylesheet" type="text/css" href="/oec/css/<%= u.getRoleCode()%>.css">
        <script type='text/javascript' src='/tools/javascript/common.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codebooks.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codes.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/nets.js'></script>
        <script type='text/javascript' src='/oec/dwr/engine.js'></script>
	<script type='text/javascript' src='/oec/dwr/util.js'></script>
    </head>
    <script language="JavaScript">
        function buildPage()
        {
            document.getElementById('loadingMsg').innerHTML = "<font class='load'>Loading...</font>";
            document.getElementById('net1').style.visibility = 'hidden';
            document.getElementById('net2').style.visibility = 'hidden';
            document.getElementById('saveNet').style.visibility = 'hidden';
            nets.getEveryNet(<%= cbId %>,buildNet1);
        }
        function buildNet1(data)
        {
            DWRUtil.addOptions("net1",["Please select a Net."]);
            DWRUtil.addOptions("net1", data);
            document.getElementById('loadingMsg').innerHTML = "";
            document.getElementById('net1').style.visibility = 'visible';
        }
        // HANDLES MOST OF THE TOGGLING ON AND OFF OF THE SAVE BUTTON.
        function getNetInfo(value)
        {
            if (value == "2")
            {
                if (DWRUtil.getValue("net1") != "Please select a Net.")
                {
                    document.getElementById('loadingMsg1').innerHTML = "<font class='load'>Loading...</font>";
                    DWRUtil.setValue("net1_code",DWRUtil.getValue("net1"));
                    document.getElementById('net2').style.visibility = 'hidden';
                    DWRUtil.setValue("net2_code","");
                    DWRUtil.removeAllOptions("net2");
                    nets.getRelSubNets(DWRUtil.getValue("net1_code"),"0",buildNet2);
                    document.getElementById('saveNet').style.visibility = 'hidden';
                } else
                {
                    DWRUtil.removeAllOptions("net2");
                    DWRUtil.setValue("net2_code","");
                    document.getElementById('saveNet').style.visibility = 'hidden';
                }                
            }            
        }
        function buildNet2(data)
        {
            DWRUtil.addOptions("net2",["Please select a SubNet."]);
            DWRUtil.addOptions("net2", data);
            document.getElementById('loadingMsg1').innerHTML = "";
            document.getElementById('net2').style.visibility = 'visible';
        }
        function saveNet()
        {
            var val_net2 = DWRUtil.getValue("net2");
            if (DWRUtil.getValue("net1") == "Please select a Net.")
            {
                alert("Please select a Net.");
                return false;
            }
            if (val_net2 == "Please select a SubNet.")
            {
                alert("Please select a SubNet.");
                return false;
            }
            nets.saveRelNets(DWRUtil.getValue("codeBookId"),DWRUtil.getValue("net1"),val_net2,'<%= u.getUserName() %>',displayMessage);
        }
        function displayMessage()
        {
            DWRUtil.setValue("net1","");
            DWRUtil.setValue("net2","");
            document.getElementById('message').innerHTML = "A SubNet to Net has been added.<br />";
        }
        function displaySave()
        {
            if (DWRUtil.getValue("net2") == "Please select a SubNet.")
            {
                document.getElementById('saveNet').style.visibility = 'hidden';
                return false;
            }
            document.getElementById('saveNet').style.visibility = 'visible';
        }
    </script>
    <body onLoad="buildPage();" onUnload="refreshParent();">
    <a href="#" class="close" onClick="window.close()">Close Window</a> 
    <form id="net" name="net">
    <input type="hidden" value="<%= cbId %>" id="codeBookId" name="codeBookId"/>
    <input type="hidden" value="" id="net1_code" name="net1_code"/>
    <input type="hidden" value="" id="net2_code" name="net2_code"/>
    <table class="form1">
        <tr>
            <td class="label" width="110">Select a Net:</td>
            <td class="inputs"><font id="loadingMsg"></font><select onChange="getNetInfo('2')" id="net1" name="net1" style="width: 100mm"></select></td>
        </tr>
        <tr>
            <td class="label" width="110">Select a SubNet:</td>
            <td class="inputs"><font id="loadingMsg1"></font><select onChange="displaySave();" id="net2" name="net2" style="width: 100mm"></select></td>
        </tr>
        </tr>
            <td class="buttons" colspan="2" align="center" height="30">
            <font id="message"></font>
            <a href="#" class="button"  id="saveNet" onClick="saveNet();"/>Save</a>
            <a href="#" class="button"  id="cancelNet" onClick="window.close();"/>Cancel</a>
            </td>
        </tr>
    </table>
    </form>
    </body>
<% } %>
</html>
