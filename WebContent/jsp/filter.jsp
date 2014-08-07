<%@ page import="com.targetrx.project.oec.bo.User" %>
<html>
<%
   String cbId = request.getParameter("codebookid");
   User u = (User)session.getAttribute("user");   
%>

        <link rel="stylesheet" type="text/css" href="/tools/css/oec.css">
        <link rel="stylesheet" type="text/css" href="/oec/css/<%= u.getRoleCode()%>.css">
        <link rel="stylesheet" type="text/css" href="/tools/css/layout.css">
        <script type='text/javascript' src='/tools/javascript/common.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codebooks.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codes.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/nets.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/programs.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/responses.js'></script>
        <script type='text/javascript' src='/oec/dwr/engine.js'></script>
        <script type='text/javascript' src='/oec/dwr/util.js'></script>

    <script language="Javascript">
        var resultGrp;
        function displayProgram(data)
        {
            DWRUtil.addOptions("plabel",["Program Labels."]);
            DWRUtil.addOptions("plabel", data);  
            document.getElementById('message').innerHTML = "";
        }
        function getEvents()
        {
            DWRUtil.removeAllOptions("peid");
            DWRUtil.removeAllOptions("group");
            DWRUtil.removeAllOptions("markets");
            programs.getProgramEvents(DWRUtil.getValue("plabel"),displayPeid);
            getGroups("label");
        }
        function displayPeid(data)
        {
            DWRUtil.addOptions("peid",["PEID"]);
            DWRUtil.addOptions("peid",data);
        }
        function getGroups(caller)
        {
            DWRUtil.removeAllOptions("group");
            DWRUtil.removeAllOptions("markets");
            programs.getGroupName(DWRUtil.getValue("plabel"),"0",DWRUtil.getValue("notag"),displayGroups);            
        }
        function displayGroups(data)
        {
            DWRUtil.addOptions("group",["Select a Group."]);
            DWRUtil.addOptions("group",data);
            document.getElementById('message').innerHTML = "";
        }
        function getMarkets()
        {
            document.getElementById('message').innerHTML = "";
            if (DWRUtil.getValue("group") != "Select a Group.")
            {
                DWRUtil.removeAllOptions("markets");
                programs.getMarkets(DWRUtil.getValue("plabel"),DWRUtil.getValue("group"),displayMarkets);                
            } else 
            {
                DWRUtil.removeAllOptions("markets");
                return false;
            }
        }
        function displayMarkets(data)
        {
            DWRUtil.addOptions("markets",["Please select a Parameter."]);
            DWRUtil.addOptions("markets",data);
            document.getElementById('message').innerHTML = "";
        }
        function getResponses()
        {
            // DO SOME INITIAL VALIDATION UPFRONT
      
            if (DWRUtil.getValue("markets") == "Please select a Parameter.")
            {
                //alert("Please select a Parameter.");
                //return false;
                
            }
            if (DWRUtil.getValue("group") == "Select a Group.")
            {
                alert("Please select a Group.");
                return false;
            }
            if (DWRUtil.getValue("plabel") == "Program Labels.")
            {
                alert("Please select a Program Label.");
                return false;
            }
            // CHECK THE TOTAL AMOUNT OF RESPONSES THE USER WISHES TO CHECKOUT
            var tag = DWRUtil.getValue("notag");
            programs.getGroupList(DWRUtil.getValue("plabel"),DWRUtil.getValue("markets"),DWRUtil.getValue("group"), tag,displayResponses);
        }
        function displayResponses(data)
        {
            resultGrp = data;
            var grp = "<table class='groupResults' width=\"550\" border=\"0\"><tr><th></th><th>Program</th><th>Question Label</th><th>Parameter</th><th>Count</th></tr>";
            var user = "<%= u.getUserName()%>"
            for (var i=0; i<data.length; i++)
            {
                var result = data[i];
                grp=grp+"<tr><td><input type='hidden' id='cnt"+i+"' value='"+result.count+"'/><input type='hidden' id='hid"+i+"' value='"+user+"~"+result.programEventId+"~"+result.questionId+"~"+result.parameterNo+"~"+result.parameterId+"'><input type='checkbox' id='grps"+i+"' name='grps"+i+"'></td>";
                grp=grp+"<td>"+result.programEventId+"</td><td>"+result.label+"</td><td>"+result.parameterValue+"</td><td>"+result.count+"</td></tr>";
            }
            grp=grp+"</table>";
            document.getElementById('chkValues').innerHTML = grp;
            document.getElementById('buttonsfilter').style.visibility = 'visible';
        }
        function checkOut()
        {
            var cnt = 0;
            var chk_array = new Array(resultGrp.length);
            var chk_cnt = 0;
            for (var i = 0; i < resultGrp.length; i++)
            {
               var obj = "grps"+i;
               
               if (DWRUtil.getValue(obj) == true)
               {
                var hidObj = "hid"+i;
                var cntObj = "cnt"+i;
                //alert(DWRUtil.getValue(hidObj));
                cnt = parseInt(DWRUtil.getValue(cntObj))+cnt;
                chk_array[chk_cnt] = DWRUtil.getValue(hidObj);
                chk_cnt++;
               }
            }
            
            // CHECK THE CNT OF HOW MANY RESPONSES HAVE BEEN CHECKED
            // USER TRIED TO CHECK OUT NO RESPONSES
            if (cnt == 0)
            {
                alert("Please select a response or respones you wish to check out.");
                return false;
            }
            // USER IS TRYING TO CHECK OUT TO MANY RESPONSES
            if (cnt > 1000)
            {
                alert("You have selected to many responses to checkout please uncheck some responses");
                return false;
            } else 
            {
                responses.getReponses(chk_array,"<%= u.getUserName()%>",DWRUtil.getValue("notag"),verifyCheckOut);
                document.getElementById('message').innerHTML = "<p><font style=\" color: blue;\">You have successfully checked out responses.</font></p>";                
            }
            
        }
        function verifyCheckOut(data)
        {
            if (data.length == 0)
            {
                var msg = "<p><font class=\"message\">You currently have responses checkout please finish coding those responses<br />";
                msg = msg + "or check in the responses you have checked out.</font></p>";
                document.getElementById('chkValues').innerHTML = msg;
                document.getElementById('message').innerHTML = "";
                document.getElementById('buttonsfilter').style.visibility = 'hidden';               
            }            
        }
        function reset()
        {
            for (var i=0; i<resultGrp.length; i++)
            {
                $("grps"+i).unchecked;
            }
        }
        function checkAll()
        {
            for (var i=0; i<resultGrp.length; i++)
            {
                $("grps"+i).checked = true;
            }
        }
        function resetParams()
        {
            DWRUtil.removeAllOptions("plabel");
            document.getElementById('chkValues').innerHTML = "";
            programs.getProgramFacts(displayProgram);
            document.getElementById('buttonsfilter').style.visibility = 'hidden';
            DWRUtil.removeAllOptions("markets");
            DWRUtil.removeAllOptions("group");
        }
    </script>
    <body>
    <font id="message"></font>
    <table align="center" border="1" cellpadding="0" cellspacing="0" class="nets" id="filters">
        <tr>
            <th>Tagged</th><th>Active Programs</th><th>Groups</th><th>Parameters</th><th></th>
        </tr>
        <tr>
            <td>
            <input type="radio" name="tag" id="notag" value="notag" checked onclick="resetParams()"/>No 
            <input type="radio" name="tag" id="tag" value="tag" onclick="resetParams()"/>Yes
            </td>
            <td height="10"><select id="plabel" name="plabel" style="width: 40mm" onchange="getGroups();"></select></td>
            <!--<td><select id="peid" name="peid" onChange="getGroups('event');"></select></td>-->
            <td><select id="group" name="group" onChange="getMarkets();"></select></td>
            <td><select id="markets" name="markets"></select></td>            
            <td><a href="#" class="buttonU"  id="getresp" onClick="getResponses();"/>Get Responses</a></td>
        </tr>
    </table>
    <form name="groups" id="groups">
    <div id="chkValues">
    </div>
    <div id="buttonsfilter">
    <table id="buttonsfilter" class="groupResults" width="550" border="0" align="center">
    <tr>
        <td class="buttons" colspan="2" align="center">
        <font id="message"></font>
        <a href="#" class="buttonU"  id="saveCode" onClick="checkOut();"/>Checkout</a>
        <a href="#" class="buttonU"  id="checkAll" onClick="checkAll();"/>CheckAll</a>
        <a href="#" class="buttonU"  id="clear" onClick="reset();"/>Reset</a>
        </td>
    </tr>
    </table>
    </div>
    </form>
    </body>
</html>