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
        <title>Add a Code</title>
        <link rel="stylesheet" type="text/css" href="/tools/css/oec.css">
        <link rel="stylesheet" type="text/css" href="/tools/css/<%= u.getRoleCode()%>.css">
        
        <script type='text/javascript' src='/tools/javascript/common.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codebooks.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codes.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/nets.js'></script>
        <script type='text/javascript' src='/oec/dwr/engine.js'></script>
	<script type='text/javascript' src='/oec/dwr/util.js'></script>
        <script language="Javascript">
            var current_value = "";
            var my_responses;
            var codeData;
            nn=(document.layers)?true:false; ie=(document.all)?true:false; 
            var act_bs = "false";
            function keyDown(e) 
            { 
                var evt=(e)?e:(window.event)?window.event:null; 
                if(evt)
                { 
                    var key=(evt.charCode)?evt.charCode: ((evt.keyCode)?evt.keyCode:((evt.which)?evt.which:0)); 
                    if(key=="8")
                    {
                        //alert(act_bs);
                        if (act_bs == "false")
                        {
                            return false;
                        }
                    }
                } 
            } 
            document.onkeydown=keyDown; 
            if(nn) document.captureEvents(Event.KEYDOWN);
            
            function buildPage()
            {
                //document.getElementById('loadingMsg').innerHTML = "<font class='load'>Loading...</font>";;
                document.getElementById('loadingMsg1').innerHTML = "Loading...";
                document.getElementById('codeList').style.visibility = 'hidden';
                document.getElementById('net1').style.visibility = 'hidden';
                //codes.getAllMappedCodes(<%= cbId %>,buildCodes);
                nets.getCodeBookNet1(<%= cbId %>,buildNet1);
            }
            function buildCodes(data)
            {
               //DWRUtil.addOptions("codeList",["Please select a Code."]);
               DWRUtil.removeAllOptions("codeList");
               DWRUtil.addOptions("codeList", data);
               document.getElementById('loadingMsg').innerHTML = "";
               document.getElementById('codeList').style.visibility = 'visible';
            }
            function buildNet1(data)
            {
               DWRUtil.addOptions("net1",["Please select a Net."]);
               DWRUtil.addOptions("net1", data);
               document.getElementById('loadingMsg1').innerHTML = "";
               document.getElementById('net1').style.visibility = 'visible';
            }
            function setNetInfo(net)
            {
                setMessage();
                if (net == "1")
                {
                    DWRUtil.removeAllOptions("net2");
                    nets.getSubNets(DWRUtil.getValue("codeBookId"),DWRUtil.getValue("net1"),displayNetLabel2);
                }
                if (net == "2")
                {
                    if (DWRUtil.getValue("net2") != "Please select a net.")
                    {
                        DWRUtil.removeAllOptions("net3");
                        nets.getSubNets(DWRUtil.getValue("codeBookId"),DWRUtil.getValue("net2"),displayNetLabel3);
                    }
                }
            }
            function displayNetLabel2(data)
            {
                DWRUtil.addOptions("net2",["Please select a Net."]);
                DWRUtil.addOptions("net2",data);               
            }
            function displayNetLabel3(data)
            {
                DWRUtil.addOptions("net3",["Please select a Net."]);
                DWRUtil.addOptions("net3",data);               
            }
            function saveCode()
            {
                var cbId = DWRUtil.getValue("codeBookId");
                var codeId = DWRUtil.getValue("codeList");
                var net1 = DWRUtil.getValue("net1");
                var net2 = DWRUtil.getValue("net2");
                var net3 = DWRUtil.getValue("net3");
                if (codeId == "Please select a Code.")
                {
                    alert("Please select a Code.");
                    return false;
                }
                if (net1 == "Please select a Net.")
                {
                     net1 = "-55555";
                }
                if ((net2 == "Please select a Net.") || (net2 == ""))
                {
                    net2 = "0";
                    net3 = "0";
                }
                if ((net3 == "Please select a Net.") || (net2 == ""))
                {
                    net3 = "";
                }
                change = "true";
                codes.saveCode(cbId,codeId,net1,net2,net3,'<%= u.getUserName() %>',displayMessage);
            }
            function displayMessage()
            {
                document.getElementById("message").innerHTML = "Code has been added.<br />";
            }
            function setMessage()
            {
                document.getElementById("message").innerHTML = "";
            }
            //******************************************************************************
            // THIS HANDLES THE CODY SUGGEST PORTION OF THE PAGE. USER CAN TYPE IN WHAT HE
            // OR SHE WISHES TO SEATCH FOR. AFTER A SET TIME OF NO TIME THE EVENT WILL
            // FIRE OFF TO GET THE RESULTS.
            //******************************************************************************
            var stack = 0;
            function lookUp(form,formc,e)
            {
                var KeyID = (window.event) ? event.keyCode : e.keyCode;
                if (KeyID == "113") // THIS IS THE F2 KEY
                {
                    form.codeList.focus();
                }
                if (current_value != form.search.value) 
                {
                    current_value = form.search.value;
                    stack++;
				    setTimeout("doer()", 800);
                }
            }
            function doer()
			{
				if(stack == 1)
				{
					document.getElementById('loadingMsg').innerHTML = "<font class='load'>Loading <img src='/oec/images/progressBar.gif' border='0'/></font>";
					var search_rad = DWRUtil.getValue("sradio");
					var search_where = DWRUtil.getValue("filter");
					codes.searchMappedCodes(search_rad, search_where, document.code.search.value, buildCodes);
			    }
				stack--;
           }
        </script>
    </head>

    <body onload="buildPage()" onUnload="refreshParent();">
    <a href="#" class="close" onClick="window.close()">Close Window</a>  
    
    <form name="code">
    <input type="hidden" value="<%= cbId %>" id="codeBookId" name="codeBookId"/>
    <table class="form1">
        <tr>
            <th colspan="2">Add a Code</th>
        </tr>
        <tr>
                <td class="label" >
                    <input type="radio" name="sradio" value="label" checked>Label<br />
                    <input type="radio" name="sradio" value="num">Code Num                    
                </td>
                <td class="inputs">
                    Search Text: <input type="text" size="20" name="search" value="" onblur="javascript: act_bs='false';" onfocus="javascript: act_bs='true';" onkeyup="lookUp(document.code,document.page_params,event)"/>
                    <br /> <input type="radio" name="filter" value="begins">Begins  
                    <input type="radio" name="filter" value="contains" checked>Contains
                </td>
            </tr>
        <tr>
            <td class="label" width="110">Select a code:</td>
            <td class="inputs"><font id="loadingMsg">Enter some search text to view some codes.</font>
            <select id="codeList" name="codeList" style="width: 110mm" onchange="setMessage();" size="5"></select></td>
        </tr>
        <tr>
            <td class="label" width="100">Select a Net 1:</td>
            <td class="inputs"><font id="loadingMsg1"></font><select onChange="setNetInfo('1')" id="net1" name="net1" style="width: 100mm"></select></td>
        </tr>        
        <tr>
            <td class="label" width="100">Select a Net 2:</td>
            <td class="inputs"><select onChange="setNetInfo('2')" id="net2" name="net2"></select></td>
        </tr>
         <tr>
            <td class="label" width="100">Select a Net 3:</td>
            <td class="inputs"><select id="net3" name="net3"></select></td>
        </tr>
         <tr>
            <td class="buttons" colspan="2" align="center">
            <font id="message"></font>
            <a href="#" class="button"  id="saveCode" onClick="saveCode();"/>Save</a>
            <a href="#" class="button"  id="cancelCod" onClick="window.close();"/>Cancel</a>
            </td>
        </tr>
    </table>
    </form>
    </body>
<% } %>
</html>
