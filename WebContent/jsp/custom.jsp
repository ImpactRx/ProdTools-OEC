<%@ page import="com.targetrx.project.oec.bo.User" %>
<%
  String cbId = request.getParameter("codebookid");
  if ((cbId == null) || (cbId.equalsIgnoreCase("null")))
  {
    cbId = (String) session.getAttribute("codebookId");
  }
  session.setAttribute("codebookId",cbId);
  User u = (User)session.getAttribute("user");
%>
<html>
    <head>
        <title>Client Recoding Analysis Program</title>
        <link rel="stylesheet" type="text/css" href="/oec/css/oec.css">
        <link rel="stylesheet" type="text/css" href="/oec/css/<%= u.getRoleCode()%>.css">
        <link rel="stylesheet" type="text/css" href="/oec/css/layout.css">
        <link rel="stylesheet" type="text/css" href="/oec/common/css/tools.css">
        <script type='text/javascript' src='/oec/javascript/event.js'></script>
	<script type='text/javascript' src='/oec/dwr/interface/codebooks.js'></script>
	<script type='text/javascript' src='/oec/dwr/interface/client.js'></script>
	<script type='text/javascript' src='/oec/dwr/interface/custom.js'></script>
	<script type='text/javascript' src='/oec/dwr/interface/codes.js'></script>
	<script type='text/javascript' src='/oec/dwr/interface/nets.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/responses.js'></script>
        <script type='text/javascript' src='/oec/dwr/engine.js'></script>
	<script type='text/javascript' src='/oec/dwr/util.js'></script>
    </head>
    <script type="text/javascript">
    	// FUNCTION GETS EXECUTED WHEN USRE COMES TO THIS PAGE AND ON REFRESH
    	var codebook_name;
        function buildPage()
        {
            displayLoading("load");
            client.getClients(buildClient);
            codebooks.getCodeBookName(<%= cbId %>,buildCodeBook);
            //codes.getClientCodes(<%= cbId %>,buildClientCodes)
            //codes.getMappedCodes(<%= cbId %>,buildCodes); 
            hideLoading("load");
        }
        // PUTS CLIENT IN A SELECT BOX
        function buildClient(data)
        {
            DWRUtil.addOptions("client", ["Please select a Client"]);
            DWRUtil.addOptions("client", data);
        }
        // PUTS THE CLIENT CODES INTO A SELECT BOX
        function buildClientCodes(data)
        {
            DWRUtil.addOptions("clientCode", ["Please select a Client Code"]);
            DWRUtil.addOptions("clientCode", data);
        }
        // PUTS THE CODEBOOK NAME IN THE RIGHT ELEMENT
        function buildCodeBook(data)
	{
            for (var i = 0; i < data.length; i++)
            {
                var result = data[i];
		DWRUtil.setValue("bookName","Working in CodeBook: "+result.codeBookName);	 	
		codebook_name = result.codeBookName;
            }
        }
        // DISPLAYS THE CODES IN THE MULTI SELECT BOX
        function buildCodes(data)
	{
            codeData = data;
            DWRUtil.removeAllOptions("codeList");
            DWRUtil.addOptions("codeList", data);					
        }
        //GET MAPS FOR A PATICULAR CLIENT
        function getMaps()
        {
            var value = DWRUtil.getValue('client');
            if (value != "Please select a Client")
            {
                displayLoading("load");
                custom.getCustomMaps(value, displayCustomMaps);
                document.getElementById("loadmap").style.visibility = "visible";
            } else
            {
                document.getElementById("loadmap").style.visibility = "hidden";
            }
        }
        // INSERTS THE VALUES FOR CUSTOM MAPS SELECT BOX
        function displayCustomMaps(data)
        {
            DWRUtil.removeAllOptions("map");
            DWRUtil.addOptions("map", ["Please select a Custom Map"]);
            DWRUtil.addOptions("map", data);
            hideLoading("load");
        }
        // POPS OPEN A WINDOW TO ALLOW THE USER TO CREATE A NEW CUSTOM MAP
        function createCustom()
        {
        	var vClient = DWRUtil.getValue('client');
        	if (vClient == "Please select a Client")
        	{
        		alert("You must select a client");
        		return false;        		
        	} else
        	{
        		displayNew("/oec/jsp/oec/customMap.jsp?cbname="+codebook_name+"&codebookid=<%= cbId %>&clientId="+vClient);
        	}
        }
        // BRINGS USER TO LOAD MAP SECTION ON THE APP ONLY IF USER HAS SELECTED A CLIENT
        function loadMap()
        {
            var vClient = DWRUtil.getValue('client');
            var vMapping = DWRUtil.getValue('map');
            if (vClient == "Please select a Client")
            {
                alert("You must select a client");
        	return false;
            } else if (vMapping == "Please select a Custom Map")
            {
                alert("You must select a Custom Map");
        	return false;
            } else 
            {
                window.location = "/oec/jsp/oec/upexcel.jsp?cbname="+codebook_name+"&clientId="+vClient+"&customMap="+vMapping;
            }
        }
        // POPUP A NEW WINDOW
        function displayNew(pValue)
        {
        	//alert(pValue);
        	window.open(pValue,"CUSTOM","menubar=no,width=430,height=360,toolbar=no");
        }
        // DISPLAY LOADING IMAGE AND TEXT
        function displayLoading(value)
        {
        	document.getElementById(value).innerHTML = "<font class='load'>Loading <img src='/oec/images/progressBar.gif' border='0'/></font>";
        }
        // HIDE LOADING IMAGE AND TEXT
        function hideLoading(value)
        {
        	document.getElementById(value).innerHTML = "";
        }
    </script>
    <% if ( u.getRoleCode() == null) 
       {
    %>
        <body>
        You are not registered to utilize this tool. Please contact Mark Snyder at Extension 8853
    <% } else { %>
    <body style="margin:0px 5px;" onload="buildPage();">
    <table width="800px" class="main">
    <tr>
		    <td colspan="2">
		        <jsp:include page="top_nav.jsp"/>
		    </td>
		</tr>
    <tr>
    	<td colspan="2">
    		<table><tr><td><jsp:include page="custom_nav.jsp"/></td></tr></table>
    	</td>
    </tr>
    <tr height="15px" colspan="2"><td></td></tr>
     <tr>
    	<td valign="top">
    	<b><span id="bookName"></span></b><br />
    	Client: <select id="client" onchange="getMaps()";></select><br /><br />
        <a href="#" id="loadmap" class="respbutton" onClick="loadMap()" style="visibility: hidden;">Load Map</a>
    	</td>
    	<td valign="top">
    	<br />Custom Map: <select id="map" class="map"></select>&nbsp;&nbsp;<a href="#" id="createm" class="respbutton" onClick="createCustom()">New Map</a>
    	</td>
    </tr>
    <tr height="15px"><td id="load" colspan="2"></td></tr>
    <!-- <tr>
    	<td colspan="2">
    	<div id="bldg">
    	<jsp:include page="customCode.jsp" />
    	</div>
    	</td>
    </tr>
    <tr>
        <td colspan="2">
	 <div id="display"></div>
	 </td>
    </tr> -->
    </table>
		<% } %>
    </body>
</html>
