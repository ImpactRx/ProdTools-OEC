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
        <title>Auto Code</title>
        <link rel="stylesheet" type="text/css" href="/tools/css/oec.css">
        <link rel="stylesheet" type="text/css" href="/oec/css/<%= u.getRoleCode()%>.css">
        <script type='text/javascript' src='/tools/javascript/common.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codebooks.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codes.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/nets.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/autocode.js'></script>
        <script type='text/javascript' src='/oec/dwr/engine.js'></script>
	<script type='text/javascript' src='/oec/dwr/util.js'></script>
        <script language="Javascript">
        var auto_codes;
        function getDicRows(obj)
        {
            clearGUI();
            if (obj == "select")
            {
                auto_codes = null;
            }
            if (DWRUtil.getValue("statusCode") == "exception")
            {
                document.getElementById('verifya').style.visibility = 'hidden';
                document.getElementById('exceptiona').style.visibility = 'visible';
                document.getElementById('overexception').style.visibility = 'hidden';

            } else
            {
                document.getElementById('verifya').style.visibility = 'visible';
                document.getElementById('exceptiona').style.visibility = 'hidden';
                document.getElementById('overexception').style.visibility = 'hidden';
            }
            document.getElementById("codebook").innerHTML = "";
            document.getElementById('waiting').innerHTML = "<font class='load'>Loading <img src='../../images/progressBar.gif' border='0'/></br /></font>";
            DWRUtil.removeAllOptions("code");
            if (auto_codes == null)
            {
                autocode.getAutoCodes(DWRUtil.getValue("statusCode"),displayAutoCodes);
            } else
            {
                displayAutoCodes(auto_codes);
            }
        }
        function displayAutoCodes(data)
        {
            auto_codes = data;
            var result;
            DWRUtil.removeAllOptions("code");
            for (var i = 0; i < data.length; i++)
            {
                result = data[i];
                if ((result.statusCode == "unverified") || (result.statusCode == "exception"))
                {
                    DWRUtil.addOptions("code",[result.autoCodeId]);
                }
            }
            document.getElementById('waiting').innerHTML = "";
            document.getElementById('updating').innerHTML = "";
        }
        function refreshDicRows(value)
        {
            
        }
        function getAutoCode()
        {
            autocode.getDictAutoCode(DWRUtil.getValue("code"),displayAutoCode)
        }
        function displayAutoCode(data)
        {
            var result;
            clearGUI();
            
            for (var i = 0; i < data.length; i++)
            {
                result = data[i];
                DWRUtil.setValue("dicoriginal",result.verbatimStr);
                DWRUtil.setValue("diccode1",result.code1Label);
                DWRUtil.setValue("diccode2",result.code2Label);
                DWRUtil.setValue("diccode3",result.code3Label);
                DWRUtil.setValue("diccode4",result.code4Label);
                DWRUtil.setValue("diccode5",result.code5Label);
                DWRUtil.setValue("diccode6",result.code6Label);
                DWRUtil.setValue("dicpar1",result.par1Value);
                DWRUtil.setValue("dicpar2",result.par2Value);
                DWRUtil.setValue("dicpar3",result.par3Value);
                DWRUtil.setValue("dicpar4",result.par4Value);
                document.getElementById("codebook").innerHTML = "<b>CodeBook:</b> "+result.codeBookName+"&nbsp;&nbsp;&nbsp; <b>Group Label:</b> "+result.groupQuestionLabel+"&nbsp;&nbsp;";
            }
            if (DWRUtil.getValue("statusCode") == "exception")
            {
                getAutoCodeException();
            }
        }
        // CLEAR ALL THE INPUTS ON THE SCREEN
        function clearGUI()
        {
            DWRUtil.setValue("dicoriginal","");
            DWRUtil.setValue("diccode1","");
            DWRUtil.setValue("diccode2","");
            DWRUtil.setValue("diccode3","");
            DWRUtil.setValue("diccode4","");
            DWRUtil.setValue("diccode5","");
            DWRUtil.setValue("diccode6","");
            DWRUtil.setValue("dicpar1","");
            DWRUtil.setValue("dicpar2","");
            DWRUtil.setValue("dicpar3","");
            DWRUtil.setValue("dicpar4","");
            DWRUtil.setValue("exoriginal","");
            DWRUtil.setValue("excode1","");
            DWRUtil.setValue("excode2","");
            DWRUtil.setValue("excode3","");
            DWRUtil.setValue("excode4","");
            DWRUtil.setValue("excode5","");
            DWRUtil.setValue("excode6","");
            DWRUtil.setValue("expar1","");
            DWRUtil.setValue("expar2","");
            DWRUtil.setValue("expar3","");
            DWRUtil.setValue("expar4","");
        }
        // SETS THE STATUS CODE TO VERIFIED
        function setVerify()
        {
            document.getElementById('updating').innerHTML = "<font class='load'>Updating <img src='../../images/progressBar.gif' border='0'/></br /></font>";
            autocode.updateStatusCode(DWRUtil.getValue("code"),"verified","<%= u.getUserName() %>",displayVerify);            
        }
        function displayVerify()
        {
            for (var i = 0; i< auto_codes.length; i++)
            {
                var result = auto_codes[i];
                if (result.autoCodeId == DWRUtil.getValue("code"))
                {
                    result.statusCode = "verified";
                }
                auto_codes[i] = result;
            }
            getDicRows("func");
        }
        function setDoNotUse()
        {
            document.getElementById('updating').innerHTML = "<font class='load'>Updating <img src='../../images/progressBar.gif' border='0'/></br /></font>";
            autocode.updateStatusCode(DWRUtil.getValue("code"),"do_not_use","<%= u.getUserName() %>",displayDoNotUse);
        }
        function displayDoNotUse()
        {
            for (var i = 0; i< auto_codes.length; i++)
            {
                var result = auto_codes[i];
                if (result.autoCodeId == DWRUtil.getValue("code"))
                {
                    result.statusCode = "do_not_use";
                }
                auto_codes[i] = result;
            }
            getDicRows("func");
        }
        function getAutoCodeException()
        {
            autocode.getAutoCodeException(DWRUtil.getValue("code"),displayExceptions);
        }
        function displayExceptions(data)
        {
            var result;
            for (var i = 0; i < data.length; i++)
            {
                result = data[i];
                DWRUtil.setValue("exoriginal",result.verbatimStr);
                DWRUtil.setValue("excode1",result.code1Label);
                DWRUtil.setValue("excode2",result.code2Label);
                DWRUtil.setValue("excode3",result.code3Label);
                DWRUtil.setValue("excode4",result.code4Label);
                DWRUtil.setValue("excode5",result.code5Label);
                DWRUtil.setValue("excode6",result.code6Label);
                DWRUtil.setValue("expar1",result.par1Value);
                DWRUtil.setValue("expar2",result.par2Value);
                DWRUtil.setValue("expar3",result.par3Value);
                DWRUtil.setValue("expar4",result.par4Value);
                document.getElementById("codebook").innerHTML = "<b>CodeBook:</b> "+result.codeBookName+"&nbsp;&nbsp;&nbsp; <b>Group Label:</b> "+result.groupQuestionLabel+"&nbsp;&nbsp;";
                
            }
        }
        // GETS ALL THE CODE BOOKS FROM THE SERVER
        function buildCodeBooks()
        {
            codebooks.getAllCodeBooks(buildList);
        }

        // POPULATES THE DROPDOWN WITH TEH RESULS FROM THE SERVER
        function buildList(data)
        {
        	//alert("here");
            DWRUtil.addOptions("cbook", ["No Filter"]);
            DWRUtil.addOptions("cbook", data);
        }
        function filterCodebook()
        {
            if (DWRUtil.getValue("statusCode") == "unverified")
            {
                if (DWRUtil.getValue("cbook") == "No Filter")
                {
                    getDicRows('select');
                } else 
                {
                    var cbookId = DWRUtil.getValue("cbook");
                    var st = DWRUtil.getValue("statusCode");
                    autocode.getAutoCodesByCodeBook(cbookId,st,displayAutoCodes);
                }
            } else
            {
                alert("You must select the AutoCode Status of UNVERIFIED.");
            }
        }
        </script>
    </head>
    <body style="background-color: #CCCCCC;" onload="buildCodeBooks()">

    <h1>Auto Code Review</h1>
    
    <div id="dicstatus">
        AutoCode Status: <select id="statusCode" onchange="getDicRows('select')">
            <option value="0"> Please select a Status.
            <option value="exception">exception
            <option value="unverified">unverified
        </select><br />
        Codebook: <select id="cbook" onchange="filterCodebook()"></select>
        
    </div>    
    <div id="autocode"><font id="waiting"></font>
        Auto Codes: <select id="code" multiple size="3" style="width: 60mm" onchange="getAutoCode()"></select>
    </div>
    <div id="dictionarya">
    <table border="1" width="795" class="nets">
        <tr>
            <th colspan="2">Dictionary</th>
        </tr>
        <tr>
            <td align="right" id=diclabel" colspan="2"><font id="codebook"></font></td>
            </td>
        </tr>
        <tr>    
            <td valign="top">Original String</td>
            <td><textarea id="dicoriginal" cols="70" rows="2"></textarea></td>
        </tr>
        <tr>
            <td colspan="2">
            Code 1: <input id="diccode1" size="50" value="">
            Code 2: <input id="diccode2" size="50" value=""><br />
            Code 3: <input id="diccode3" size="50" value="">
            Code 4: <input id="diccode4" size="50" value=""><br />
            Code 5: <input id="diccode5" size="50" value="">
            Code 6: <input id="diccode6" size="50" value="">
            </td>
        </tr>
        <tr height="10"><td></td></tr>
        <tr>
            <td colspan="2">
                Par 1: <input id="dicpar1" type="text" size="18" disabled> 
                Par 2: <input id="dicpar2" type="text" size="18" disabled> 
                Par 3: <input id="dicpar3" type="text" size="18" disabled> 
                Par 4: <input id="dicpar4" type="text" size="18" disabled>
            </td>
        </tr>
    </table>
    </div>

    <div id="exceptiona">
    <table border="1" width="795" class="nets">
        <tr>
            <th colspan="2">Exception</th>
        </tr>
        <tr>
            <td align="right" id=exlabel" colspan="2">Question Label:</td>
            </td>
        </tr>
        <tr>    
            <td valign="top">Original String</td>
            <td><textarea id="exoriginal" cols="70" rows="3"></textarea></td>
        </tr>
        <tr>
            <td colspan="2">
            Code 1: <input id="excode1" size="50" value="">
            Code 2: <input id="excode2" size="50" value=""><br />
            Code 3: <input id="excode3" size="50" value="">
            Code 4: <input id="excode4" size="50" value=""><br />
            Code 5: <input id="excode5" size="50" value="">
            Code 6: <input id="excode6" size="50" value="">
            </td>
        </tr>
        <tr height="10"><td></td></tr>
        <tr>
            <td colspan="2">
                Par 1: <input id="expar1" type="text" size="18" disabled> 
                Par 2: <input id="expar2" type="text" size="18" disabled> 
                Par 3: <input id="expar3" type="text" size="18" disabled> 
                Par 4: <input id="expar4" type="text" size="18" disabled>
            </td>
        </tr>
    </table>
    </div>

    <div id="verifya">
        <a href="#" class="buttonU" onclick="setVerify()">&nbsp;&nbsp;Verify&nbsp;&nbsp;</a>
        <a href="#" class="buttonU" onclick="setDoNotUse()">&nbsp;&nbsp;Do Not Use&nbsp;&nbsp;</a>
        <font id="updating"></font>
    </div>
    
    <div id="overexception">
        <a href="#" class="buttonU" onclick="">&nbsp;&nbsp;Override Exception&nbsp;&nbsp;</a>
    </div>
    </body>
<% } %>
</html>
