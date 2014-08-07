<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ page import="com.targetrx.project.oec.bo.User" %>
<%
   String cbId = request.getParameter("codebookid");
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
        <title>AutoCode Discrepancy Tool</title>
        <link rel="stylesheet" type="text/css" href="/tools/css/oec.css">
        <link rel="stylesheet" type="text/css" href="/oec/css/<%= u.getRoleCode()%>.css">
        <script type='text/javascript' src='/toolsjavascript/common.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codebooks.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codes.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/nets.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/autocode.js'></script>
        <script type='text/javascript' src='/oec/dwr/engine.js'></script>
	<script type='text/javascript' src='/oec/dwr/util.js'></script>
        <script language="Javascript">
            // Builds the all the selects boxes and forms on the page. When the page first loads
            function buildPage()
            {
                codebooks.getAllCodeBooks(buildList);
            }
            // Builds out the Select Box for Codebooks
            function buildList(data)
            {
                DWRUtil.addOptions("codebook", ["Please select a Code Book"]);
                DWRUtil.addOptions("codebook", data);
            }
            //Gets and Displays the next dicrepancy record
            function getDiscreps()
            {
                
                if (DWRUtil.getValue("codebook") != "Please select a Code Book")
                {
                    cleanPage();
                    autocode.getAutoCodeDicreps(DWRUtil.getValue("codebook"),displayRecord);
                }  else
                {
                    cleanPage();
                }
            }
            function displayRecord(data)
            {
                if (data.length == 0)
                {
                    document.getElementById("updating").innerHTML = "<p style=\"color: green;\">That Codebook has no discrepancies.</p>";
                    return false;
                }
                for (var i=0; i < data.length; i++)
                {
                    var result = data[i];
                    //alert(result.autocodeId);
                    DWRUtil.setValue("responseId",result.responseId);
                    DWRUtil.setValue("dictionaryId",result.autocodeId);
                    DWRUtil.setValue("codebookId",result.codebookId);
                    DWRUtil.setValue("dicoriginal",result.verbatimStr);
                    DWRUtil.setValue("diccode1",result.acode1);
                    DWRUtil.setValue("diccode2",result.acode2);
                    DWRUtil.setValue("diccode3",result.acode3);
                    DWRUtil.setValue("diccode4",result.acode4);
                    DWRUtil.setValue("diccode5",result.acode5);
                    DWRUtil.setValue("diccode6",result.acode6);
                    DWRUtil.setValue("oecoriginal",result.responseOrigStr);
                    DWRUtil.setValue("oeccode1",result.ocode1);
                    DWRUtil.setValue("oeccode2",result.ocode2);
                    DWRUtil.setValue("oeccode3",result.ocode3);
                    DWRUtil.setValue("oeccode4",result.ocode4);
                    DWRUtil.setValue("oeccode5",result.ocode5);
                    DWRUtil.setValue("oeccode6",result.ocode6);
                    DWRUtil.setValue("masoriginal",result.responseOrigStr);
                }
            }
            // SETS ALL THE INPUT FIELDS TO NULL
            function cleanPage()
            {
                document.acform.x[0].checked=false;
                document.acform.x[1].checked=false;
                document.acform.x[2].checked=false;
                document.getElementById("updating").innerHTML ="";
                DWRUtil.setValue("responseId","");
                DWRUtil.setValue("dictionaryId","");
                DWRUtil.setValue("codebookId","");
                DWRUtil.setValue("dicoriginal","");
                DWRUtil.setValue("diccode1","");
                DWRUtil.setValue("diccode2","");
                DWRUtil.setValue("diccode3","");
                DWRUtil.setValue("diccode4","");
                DWRUtil.setValue("diccode5","");
                DWRUtil.setValue("diccode6","");
                DWRUtil.setValue("oecoriginal","");
                DWRUtil.setValue("oeccode1","");
                DWRUtil.setValue("oeccode2","");
                DWRUtil.setValue("oeccode3","");
                DWRUtil.setValue("oeccode4","");
                DWRUtil.setValue("oeccode5","");
                DWRUtil.setValue("oeccode6","");
                DWRUtil.setValue("masoriginal","");
            }
            //Updates both the Autocode dictionary and oec response row
            function updateDiscrep()
            {
                var aid = DWRUtil.getValue("dictionaryId");
                var rid = DWRUtil.getValue("responseId");
                var cid = DWRUtil.getValue("codebookId");
                var c1 = "";
                var c2 = "";
                var c3 = "";
                var c4 = "";
                var c5 = "";
                var c6 = "";
                var origString = "";
                var a1 = "null";
                var a2 = "null";
                var a3 = "null";
                var a4 = "null";
                var a5 = "null";
                var a6 = "null";
                var u = "<%= u.getUserName() %>";
                
                if (DWRUtil.getValue("selection") == "")
                {
                    alert("Please select a button that has the correct values.");
                    return false;
                }
                // NEED TO GRAB ALL THE DICTIONARY VALUES
                if (DWRUtil.getValue("selection") == "dictionary")
                {
                    //alert("dic");
                    c1 = DWRUtil.getValue("diccode1");
                    c2 = DWRUtil.getValue("diccode2");
                    c3 = DWRUtil.getValue("diccode3");
                    c4 = DWRUtil.getValue("diccode4");
                    c5 = DWRUtil.getValue("diccode5");
                    c6 = DWRUtil.getValue("diccode6");
                    
                } else if (DWRUtil.getValue("selection") == "response")
                // NEED TO GRAB ALL THE RESPONSE VALUES
                {
                    //alert("resp");
                    c1 = DWRUtil.getValue("oeccode1");
                    c2 = DWRUtil.getValue("oeccode2");
                    c3 = DWRUtil.getValue("oeccode3");
                    c4 = DWRUtil.getValue("oeccode4");
                    c5 = DWRUtil.getValue("oeccode5");
                    c6 = DWRUtil.getValue("oeccode6");
                    
                } else
                // NEED TO GRAB ALL THE MASTER VALUES
                {
                    //alert("mas");
                    c1 = DWRUtil.getValue("mascode1");
                    c2 = DWRUtil.getValue("mascode2");
                    c3 = DWRUtil.getValue("mascode3");
                    c4 = DWRUtil.getValue("mascode4");
                    c5 = DWRUtil.getValue("mascode5");
                    c6 = DWRUtil.getValue("mascode6");
                    
                }
                // AUTOCODE VALUES MUST BE SET
                if (c1 == "") { c1 = "null"; }
                if (c2 == "") { c2 = "null"; }
                if (c3 == "") { c3 = "null"; }
                if (c4 == "") { c4 = "null"; }
                if (c5 == "") { c5 = "null"; }
                if (c6 == "") { c6 = "null"; }
                origString = DWRUtil.getValue("oecoriginal");
                if (DWRUtil.getValue("diccode1") != "")
                {
                    a1 = DWRUtil.getValue("diccode1");
                }
                if (DWRUtil.getValue("diccode2") != "")
                {
                    a2 = DWRUtil.getValue("diccode2");
                }
                if (DWRUtil.getValue("diccode3") != "")
                {
                    a3 = DWRUtil.getValue("diccode3");
                }
                if (DWRUtil.getValue("diccode4") != "")
                {
                    a4 = DWRUtil.getValue("diccode4");
                }
                if (DWRUtil.getValue("diccode4") != "")
                {
                    a4 = DWRUtil.getValue("diccode4");
                }
                if (DWRUtil.getValue("diccode5") != "")
                {
                    a5 = DWRUtil.getValue("diccode5");
                }
                if (DWRUtil.getValue("diccode6") != "")
                {
                    a6 = DWRUtil.getValue("diccode6");
                }
                //alert(aid+"::"+rid+"::"+cid+"::"+c1+"::"+c2+"::"+c3+"::"+c4+"::"+c5+"::"+c6+"::"+u+"::");
                autocode.updateDiscrepancy(aid,rid,cid,c1,c2,c3,c4,c5,c6,u,origString,displayUpdate);                
            }
            function displayUpdate()
            {
                document.getElementById("updating").innerHTML = "<p style=\"color: green;\">Discrepancy has been fixed.</p>";                    
                getDiscreps();
            }
            function closeWindow()
            {
                window.close();
            }
        </script>
<body onLoad="buildPage();" style="background-color: #CCCCCC;">
    <a href="#" class="close" onclick="closeWindow();">&lt; Close Window &gt;</a><br />
    <div id="filter">
        What Codebook do you wish to work within? <select id="codebook" name="codebook" value="" onchange="getDiscreps();"></select>
    </div>
    <form name="acform">
    <input type="hidden" id="responseId" value=""/>
    <input type="hidden" id="dictionaryId" value=""/>
    <input type="hidden" id="codebookId" value=""/>
    <div id="buttons">
        <table>
            <tr height="150">
                <td><input type="radio" id="selection" name="x" value="dictionary"/></td>
            </tr>
            <tr height="180">
                <td><input type="radio" id="selection" name="x" value="response"/></td>
            </tr>
            <tr height="150">
                <td><input type="radio" id="selection" name="x" value="master"/></td>
            </tr>
        </table>
    </div>
    <div id="dictionary">
    <table border="1" width="745" class="nets">
        <tr>
            <th colspan="2">AutoCode Dictionary Record</th>
        </tr>
        <tr>
            <td align="right" id=diclabel" colspan="2"><font id="codebook"></font></td>
            </td>
        </tr>
        <tr>    
            <td valign="top">Original String</td>
            <td><textarea id="dicoriginal" cols="70" rows="3" disabled></textarea></td>
        </tr>
        <tr>
            <td colspan="2">
            Code 1: <input id="diccode1" size="8" value="">
            Code 2: <input id="diccode2" size="8" value="">
            Code 3: <input id="diccode3" size="8" value="">
            Code 4: <input id="diccode4" size="8" value="">
            Code 5: <input id="diccode5" size="8" value="">
            Code 6: <input id="diccode6" size="8" value="">
            </td>
        </tr>        
    </table>
    </div>

    <div id="oec">
    <table border="1" width="745" class="nets">
        <tr>
            <th colspan="2">OEC Response Record</th>
        </tr>
        <tr>    
            <td valign="top">Original String</td>
            <td><textarea id="oecoriginal" cols="70" rows="3" disabled></textarea></td>
        </tr>
        <tr>
            <td colspan="2">
            Code 1: <input id="oeccode1" size="8" value="">
            Code 2: <input id="oeccode2" size="8" value="">
            Code 3: <input id="oeccode3" size="8" value="">
            Code 4: <input id="oeccode4" size="8" value="">
            Code 5: <input id="oeccode5" size="8" value="">
            Code 6: <input id="oeccode6" size="8" value="">
            </td>
        </tr>        
    </table>
    </div>
    
    <div id="master">
    <table border="1" width="745" class="nets">
        <tr>
            <th colspan="2">Master Record</th>
        </tr>
        <tr>    
            <td valign="top">Original String</td>
            <td><textarea id="masoriginal" cols="70" rows="3" disabled></textarea></td>
        </tr>
        <tr>
            <td colspan="2">
            Code 1: <input id="mascode1" size="8" value="">
            Code 2: <input id="mascode2" size="8" value="">
            Code 3: <input id="mascode3" size="8" value="">
            Code 4: <input id="mascode4" size="8" value="">
            Code 5: <input id="mascode5" size="8" value="">
            Code 6: <input id="mascode6" size="8" value="">
            </td>
        </tr>        
    </table>
    </div>
    
    <div id="verifydiscrep">
        <a href="#" class="buttonU" onclick="updateDiscrep();">&nbsp;&nbsp;Correct Discrepancy&nbsp;&nbsp;</a>
        <font id="updating"></font>
    </div>
    </form>
    </body>
<% } %>
</html>

