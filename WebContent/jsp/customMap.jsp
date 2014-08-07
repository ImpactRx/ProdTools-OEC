<%@ page import="com.targetrx.project.oec.bo.User" %>
<%
  String cbId = request.getParameter("codebookid");
  User u = (User)session.getAttribute("user");
  String codebookname = (String) request.getParameter("cbname");
  String clientId = (String) request.getParameter("clientId");
%>
<html>
<head>
	<title>Custom Map</title>
	<link rel="stylesheet" type="text/css" href="/oec/css/oec.css">
	<link rel="stylesheet" type="text/css" href="/oec/css/<%= u.getRoleCode()%>.css">
	<link rel="stylesheet" type="text/css" href="/oec/css/layout.css">
        <link rel="stylesheet" type="text/css" href="/oec/common/css/tools.css">
  <script type='text/javascript' src='/oec/dwr/interface/codebooks.js'></script>
	<script type='text/javascript' src='/oec/dwr/interface/client.js'></script>
	<script type='text/javascript' src='/oec/dwr/interface/custom.js'></script>
	<script type='text/javascript' src='/oec/dwr/engine.js'></script>
	<script type='text/javascript' src='/oec/dwr/util.js'></script>
	<script language="Javascript">
		// BUILDS THE PAGE
		function buildPage()
		{
                    client.getClientName("<%= clientId %>",displayClientName);
                    document.getElementById("cbname").innerHTML = "Working in Codebook: <%= codebookname %>";
		}
		// DISPLAY CLIENT NAME ON THE PAGE
		function displayClientName(data)
		{
                    document.getElementById("clientName").innerHTML = "Client: "+data;
		}
		// CALLS THE PROPER JAVA FUNCTION TO INSERT NEW CUSTOM MAP
		function create()
		{
                    var value = document.getElementById("mappinglabel").value;
                    if (value != "")
                    {
                        displayLoading("load");
                        custom.insertCustomMap("<%= clientId %>", value, "<%= u.getUserName() %>",displayCreate);
                    } else
                    {
                        alert("Please enter a Mapping Label.");
                    }
		}
                function displayCreate(data)
                {
                    document.getElementById("load").innerHTML = data;
                    window.opener.getMaps();
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
</head>
<body onload="buildPage()">


    <center>
        <table class="tier2">
            <tr>
                <th>
                    <b><span id="clientName"></span></b><br />
                    <b><span id="cbname"></span></b><br />
                </th>
            </tr>
            <tr height="15px"><td id="load"></td></tr>
            <tr>
                <td>
                    Mapping Label: <input type="text" size="40" value="" id="mappinglabel"/>
                </td>
            </tr>
            <tr height="10px"><td></td></tr>
            <tr>
                <td align="center"><a href="#" class="respbutton" onClick="create()">Create</a></td>
            </tr>
        </table>
    </center>
</body>
</html>