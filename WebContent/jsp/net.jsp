<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@ page import="com.targetrx.project.oec.bo.User" %>
<%
   String cbId = request.getParameter("codebookId");
   String netId = request.getParameter("netId");
   if (netId == "") { netId = "0"; }
   String codeId = request.getParameter("codeId");
   String net2Id = request.getParameter("net2Id");
   if (net2Id == null) { net2Id = "0"; }
   User u = (User) session.getAttribute("user");
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
        <title>Edit Net</title>
       
        
        <link rel="stylesheet" type="text/css" href="/tools/css/oec.css">
        <link rel="stylesheet" type="text/css" href="/oec/css/<%= u.getRoleCode()%>.css">
        <script type='text/javascript' src='/oec/dwr/interface/codebooks.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codes.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/nets.js'></script>
        <script type='text/javascript' src='/oec/dwr/engine.js'></script>
	<script type='text/javascript' src='/oec/dwr/util.js'></script>
        <script language="Javascript">
            var _netid = '<%= netId %>';
            function buildPage()
            {
                codebooks.getCodeBookName(<%= cbId %>,buildCodeBook);
                codes.getCodeLabelById('<%= codeId %>','<%= cbId %>',displayCodeLabel);
                getNetCodes();
                
                var _netid = '<%= netId %>';
                DWRUtil.setValue('net1hidden','<%= netId %>');
                getSubNets(<%= cbId %>, "0", "1");
                if (_netid.length > 1)
                {                    
                    getSubNets(<%= cbId %>,_netid,"2");
                    var net2_temp = "<%= net2Id %>";
                    if (net2_temp != "")
                    {
                        nets.getSubNets(<%= cbId %>, net2_temp,displayNet3);
                    }
                }                
                codes.getCodeId(<%= cbId %>, <%= codeId %>,displayId);
            }
            function displayCodeLabel(data)
            {
                //docuement.getElementById("codel").innerHTML = data;
                document.getElementById("codel").innerHTML = "Code Label: <u><b>"+data+"</b></u>";
            }
            function displayId(data)
            {
                for (var i = 0; i < data.length; i++)
		{
                    var result = data[i];
//                    DWRUtil.setValue("codeId",result.codeId);	
                }
            }
            function buildCodeBook(data)
            {
                for (var i = 0; i < data.length; i++)
		{
                    var result = data[i];
                    document.getElementById("bookName").innerHTML = "Code Book Name: <b><u>"+result.codeBookName +"</b></u>";
                }
            }
            function getNetCodes()
            {
                nets.getNetsByCode(<%= cbId %>,<%= codeId %>,buildNets);
            }
            function buildNets(data)
            {
                for (var i = 0; i < data.length; i++)
		{
                    var result = data[i];
                    DWRUtil.setValue("net1",result.net1label);	 	
                    DWRUtil.setValue("net2",result.net2label);
                    DWRUtil.setValue("net2hidden",result.net2Id);
                    DWRUtil.setValue("net3",result.net3label);
                    DWRUtil.setValue("net3hidden",result.net3Id);
                    var temp = result.net2label;
                    if (temp != null)
                    {
                        getSubNets("3");
                    }
                }                  
            }
            function getSubNets(cbId, netId, net)
            {
                if (net == "1")
                {
                    nets.getCodeBookNet1(cbId,displayNet1);
                }
                if (net == "2")
                {
                    nets.getSubNets(cbId, netId,displayNet2);
                }
                if (net == "3")
                {
                    nets.getSubNets(cbId, netId,displayNet3);
                }
            }
            function displayNet1(data)
            {
                DWRUtil.addOptions("netList1", ["Please select a net."]);
                DWRUtil.addOptions("netList1", ["No net required."]);
                DWRUtil.addOptions("netList1", data);
            }
            function displayNet2(data)
            {
                DWRUtil.addOptions("netList2", ["Please select a net."]);
                DWRUtil.addOptions("netList2", data);                
            }
            function displayNet3(data)
            {
                DWRUtil.removeAllOptions("netList3");
                DWRUtil.addOptions("netList3", ["Please select a net."]);
                DWRUtil.addOptions("netList3", data);                
            }
            function setNetInfo(net)
            {
                if (net == "1")
                {
                    if ((DWRUtil.getValue("netList1") != "Please select a net.") && (DWRUtil.getValue("netList1") != "No net required."))
                    {
                        DWRUtil.setValue("net2","");
                        DWRUtil.setValue("net2hidden","");
                        DWRUtil.removeAllOptions("netList2");
                        DWRUtil.setValue("net3","");
                        DWRUtil.setValue("net3hidden","");
                        DWRUtil.removeAllOptions("netList3");
                        nets.getNetLabel(DWRUtil.getValue("netList1"),displayNetLabel1);                                             
                    } else 
                    {
                        DWRUtil.setValue("net1","");
                        DWRUtil.setValue("net1hidden","");
                        DWRUtil.setValue("net2","");
                        DWRUtil.setValue("net2hidden","");
                        DWRUtil.removeAllOptions("netList2");
                        DWRUtil.setValue("net3hidden","");
                        DWRUtil.setValue("net3","");
                        DWRUtil.removeAllOptions("netList3");
                    }
                }
                if (net == "2")
                {
                    if (DWRUtil.getValue("netList2") != "Please select a net.")
                    {
                        DWRUtil.setValue("net2","");
                        DWRUtil.setValue("net3hidden","");
                        DWRUtil.setValue("net3","");
                        DWRUtil.removeAllOptions("netList3");
                        nets.getNetLabel(DWRUtil.getValue("netList2"),displayNetLabel2);
                        nets.getSubNets(DWRUtil.getValue("codeBookId"),DWRUtil.getValue("netList2"),displayNetLabel2);
                       
                    } else
                    {
                        DWRUtil.setValue("net2","");
                        DWRUtil.setValue("net2hidden","");
                        DWRUtil.removeAllOptions("netList3");
                        DWRUtil.setValue("net3hidden","");
                        DWRUtil.setValue("net3","");
                    }
                }
                if (net == "3")
                {
                    if (DWRUtil.getValue("netList3") == "Please select a net.")
                    {
                        DWRUtil.setValue("net3hidden","");
                        DWRUtil.setValue("net3","");
                    } else
                    {
                        nets.getNetLabel(DWRUtil.getValue("netList3"),displayNetLabel3);                       
                    }
                }
            }
            function displayNetLabel1(data)
            {
                DWRUtil.setValue("net1hidden",DWRUtil.getValue("netList1"));
                DWRUtil.setValue("net1",data.net1label);
                DWRUtil.removeAllOptions("netList2");
                DWRUtil.removeAllOptions("netList3");
                DWRUtil.setValue("net2hidden","");
                DWRUtil.setValue("net2","");
                DWRUtil.setValue("net3hidden","");
                DWRUtil.setValue("net3","");
                getSubNets(DWRUtil.getValue("codeBookId"),DWRUtil.getValue("netList1"),"2");
            }
            function displayNetLabel2(data)
            {
                if ((data.net1Id == DWRUtil.getValue("net1hidden")) || (data.net1Id == DWRUtil.getValue("net3hidden")))
                {
                    alert("You cannt assign the same net as any other Net");
                    DWRUtil.setValue("net2","");
                    DWRUtil.setValue("net2hidden","");
                } else
                {
                    DWRUtil.setValue("net2",data.net1label);
                    DWRUtil.setValue("net2hidden",DWRUtil.getValue("netList2"));
                    DWRUtil.setValue("net3hidden","");
                    DWRUtil.setValue("net3","");
                    DWRUtil.removeAllOptions("netList3");
                    getSubNets(DWRUtil.getValue("codeBookId"),DWRUtil.getValue("net2hidden"),"3");                   
                }
               
            }
            function displayNetLabel3(data)
            {
                if (data.net1Id == DWRUtil.getValue("netList2"))
                {
                    alert("You cannot assign the same net as any other Net");
                    DWRUtil.setValue("net3","");
                    DWRUtil.setValue("net3hidden","");
                } else
                {
                    DWRUtil.setValue("net3",data.net1label);
                    DWRUtil.setValue("net3hidden",DWRUtil.getValue("netList3"));
                }
            }
            function saveNets(form)
            {
                var temp_net2 = form.net2hidden.value;
                var temp_net3 = form.net3hidden.value;
                var temp_net1 = form.net1In.value;
                if ((temp_net2 == "") || (temp_net3 == " ") || (temp_net2.length == 0))
                {
                   temp_net2 = "0";
                }
                if ((temp_net3 == "") || (temp_net3 == " ") || (temp_net3.length == 0))
                {
                   temp_net3= "0";
                }
                if ((temp_net1 == "") || (temp_net1 == " ") || (temp_net1.length == 0))
                {
                   temp_net1= "0";
                }
                temp_net1 = DWRUtil.getValue('netList1');
                if (DWRUtil.getValue('netList1') == "Please select a net.")
                {
                    temp_net1 = "0";
                }
                if (_netid == " ")
                {
                    _netid = "none";
                }
                if ((DWRUtil.getValue('netList2') != "Please select a net.") && (_netid != " ") && (DWRUtil.getValue('netList1') == "Please select a net."))
                {
                    alert("Please select a net 1 or click the cancel button");
                    return false;
                }
                if ((_netid != " ") && (DWRUtil.getValue('netList1') == "No net required."))
                {
                    var result = confirm("You are about to remove the Net Association to this code.");
                    if (!result)
                    {
                        return false;
                    }
                    temp_net1 = "0";
                }
                nets.updateCodesNetsXref(form.codeBookId.value, form.codeId.value,temp_net1,temp_net2, temp_net3, _netid,'<%= u.getUserName() %>');
                document.getElementById("message").innerHTML = "Nets have been updated.<br />"
                window.opener.location.reload();
            }
        </script>

    </head>
    
    <body onload="buildPage();">
    <a href="#" class="close" onClick="window.close()">Close Window</a>
    <p><div id="bookInfoNet"><div id="bookName"></div><div id="codel"></div></div></p>
    <div id="bookNet1"></div>
    <div id="contentNet">
        <form name="net">
        <table class="formnet">
            <tr>
                <td class="label">Net 1:</td>
                <td class="inputs"><input type="text" value="" id="net1" size="20" disabled/>
                <select onChange="setNetInfo('1')" id="netList1" style="width: 50mm"></select></td>
            </tr>
            <tr>
                <td class="label">Net 2:</td>
                <td class="inputs"><input type="text" value="" id="net2" size="20" disabled/> 
                <select onChange="setNetInfo('2')" id="netList2" style="width: 50mm"></select></td>
            </tr>
            <tr>
                <td class="label">Net 3:</td>
                <td class="inputs"><input type="text" value="" id="net3" size="20" disabled/> 
                <select onChange="setNetInfo('3')" id="netList3" style="width: 50mm"></select></td>
            </tr>
            <tr>
                <td class="buttons" colspan="2">
                <input type="hidden" value="<%= netId %>" id="net1hidden"/> 
                <input type="hidden" value="<%= netId %>" id="net1In"/> 
                <font id="message"></font>
                <a href="#" class="button"  id="saveCode" onClick="saveNets(document.net);"/>Save</a>    
                <a href="#" class="button"  id="cancelCod" onClick="window.close();"/>Cancel</a>
                </td>    
            </tr>
        </table>
        <input type="hidden" value="" id="net1hidden"/>
        <input type="hidden" value="" id="net2hidden"/>
        <input type="hidden" value="" id="net3hidden"/>
        <input type="hidden" value="<%= codeId %>" id="codeId" name="codeId"/>
        <input type="hidden" value="<%= cbId %>" id="codeBookId" name="codeBookId"/>
        </form>
    </div>
    </body>
<% } %>
</html>
