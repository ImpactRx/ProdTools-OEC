<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ page import="com.targetrx.project.oec.bo.User" %>
<%
   String cbId = request.getParameter("codebookId");
   User u = (User)session.getAttribute("user");
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Code</title>
        <link rel="stylesheet" type="text/css" href="/tools/css/oec.css">
        <link rel="stylesheet" type="text/css" href="/oec/css/<%= u.getRoleCode()%>.css">
        <script type='text/javascript' src='/tools/javascript/common.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codebooks.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codes.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/nets.js'></script>
        <script type='text/javascript' src='/oec/dwr/engine.js'></script>
		<script type='text/javascript' src='/oec/dwr/util.js'></script>
        <script language="Javascript">
        var code_error = "false";
        var num_error = false;
        function buildPage()
        {
            codebooks.getCodeBookName(<%= cbId %>,buildCodeBook);
            getNets();
            codes.getNextCodeId(displayCodeNum);
        }
        function displayCodeNum(data)
        {
            DWRUtil.setValue("codeNum",data);            
        }
        function buildCodeBook(data)
        {
            for (var i = 0; i < data.length; i++)
            {
                var result = data[i];
                DWRUtil.setValue("bookName","Code Book Name: <u>"+result.codeBookName+"</u>");	 	
            }
        }
        function getNets()
        {
            //nets.getEveryNet(displayNets);            
            nets.getCodeBookNet1('<%= cbId %>',displayNets);
        }
        function displayNets(data)
        {
            DWRUtil.addOptions("netList1", ["Please select a net."]);
            DWRUtil.addOptions("netList1", ["No net required."]);
            DWRUtil.addOptions("netList1", data);            
        }
        function saveCode(form)
        {
            //alert(form.codebookId.value);
            var net1 = form.net1.value;
            //alert(net1);
            if (form.codeNum.value == "")
            {
                alert("Please enter a Code Number.");
                return false;
            }
            if (!isInteger(form.codeNum.value))
            {
                alert("Please enter only numbers in Code Num.");
                return false;
            }
            if (form.codeLabel.value == "")
            {
                alert("Please enter a Code Label.");
                return false;
            }
            if (form.reportLabel.value == "")
            {
                alert("Please enter a Report Label.");
                return false;
            }
            if (form.hintCode.value == "")
            {
                alert("Please enter a Hint Code.");
                return false;
            }
            if (form.net1.value == "Please select a net.")
            {
               alert("You must select a net or select no net required.");
               return false;
            }
            if (form.net1.value == "No net required.")
            {
                net1 = "-55555";
            }
            var conf = "";
            if (form.codeLabel.value != form.reportLabel.value)
            {
                conf = confirm("Code Label and Repot Label do not match.");
                if (!conf)
                {
                    return false;
                }
            }
            codes.addCode(form.codebookId.value,form.codeNum.value,form.codeLabel.value,form.reportLabel.value,form.hintCode.value,net1,"0","0",'<%= u.getLastName() %>');
            document.getElementById('message').innerHTML = "Code has been created.<br />";
            DWRUtil.setValue("codeNum","");
            DWRUtil.setValue("codeLabel","");
            DWRUtil.setValue("reportLabel","");
            DWRUtil.setValue("hintCode","");
            codes.getNextCodeId(displayCodeNum);
            getNets();
             
        }
        function isInteger(candidate) {
        // Check to make sure a string is a valid integer
            if (!candidate) return false;
            var Chars = "0123456789";

            for (var i = 0; i < candidate.length; i++) {
               if (Chars.indexOf(candidate.charAt(i)) == -1)
                  return false;
            }
            return true;
        }
        function checkLabel()
        {
            codes.checkCodeLabel(DWRUtil.getValue("codeLabel"), displayLMessage);
        }
        function checkCodeNum()
        {
            codes.checkCodeNum(DWRUtil.getValue("codeNum"), displayNMessage);
        }
        function displayNMessage(data)
        {
            if (data.length > 0)
            {
                document.getElementById('saveCode').style.visibility = 'hidden';
                document.getElementById('message').innerHTML = "That Code Number already exists.<br />"                
                num_error = "true";
            } else
            {
                document.getElementById('saveCode').style.visibility = 'visible';
                document.getElementById('message').innerHTML = "";
                num_error = "false";
                if (code_error != "false")
                {
                    checkLabel();
                }
            }
        }
        function displayLMessage(data)
        {
            if (data.length > 0)
            {
                document.getElementById('saveCode').style.visibility = 'hidden';
                document.getElementById('message').innerHTML = "That Code Label already exists.<br />"
                code_error = "true";
            } else 
            {
                document.getElementById('saveCode').style.visibility = 'visible';
                document.getElementById('message').innerHTML = "";
                code_error = "false";
                DWRUtil.setValue("reportLabel",DWRUtil.getValue("codeLabel"));
                if (num_error != "false")
                {
                    checkCodeNum();
                }
            }
        }
        </script>
    </head>
    <body onload="buildPage();" onunload="refreshParent();">
     <a href="#" class="close" onClick="window.close()">Close Window</a>
    <div id="bookInfoCode"><div id="bookName"></div></div>
    <div id="codeContent">
    <form name="codes">
    <input type="hidden" name="codebookId" value="<%= cbId %>"/>
    <table border="1" class="form1">
        <tr>
            <th colspan="2">Create a New Code</th>
        </tr>
        <tr>
            <td class="label" valign="top">*Code Num</td>
            <td class="inputs" valign="top"><input readonly type="text" id="codeNum" name="codeNum" class="inputs" size="15" onBlur="checkCodeNum();"/></td>
        </tr>
        <tr>
            <td class="label" valign="top">*Code Label</td>
            <td class="inputs" valign="top"><input type="text" id="codeLabel" name="codeLabel" class="inputs" size="25" onBlur="checkLabel();"/></td>
        </tr>
        <tr>
            <td class="label" valign="top">*Report Label</td>
            <td class="inputs" valign="top"><input type="text" name="reportLabel" id="reportLabel" class="inputs" size="25"/></td>
        </tr>
        <tr>
            <td class="label" valign="top">*Hint Code</td>
            <td class="inputs" valign="top"><input type="text" name="hintCode" class="inputs" size="25"/></td>
        </tr>
        <tr>
            <td class="label" valign="top">Net 1</td>
            <td class="inputs" valign="top"><select id="netList1" name="net1"></select></td>
        </tr>
        <tr>
            <td class="buttons" colspan="2" align="center">
            <font id="message"></font>
            <a href="#" class="button"  id="saveCode" onClick="saveCode(document.codes);"/>Save</a>
            <a href="#" class="button"  id="cancelCod" onClick="window.close();"/>Cancel</a>
            </td>
        </tr>
    </table>
    </form>
    </div>
    
    </body>
</html>
