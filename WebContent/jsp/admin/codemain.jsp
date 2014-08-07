<%@ page import="com.targetrx.project.oec.bo.User" %>
<%@ page import="com.targetrx.project.oec.servlet.ExportServlet" %>
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
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Month End</title>
       
        <link rel="stylesheet" type="text/css" href="/tools/css/oec.css">
        <link rel="stylesheet" type="text/css" href="/oec/css/<%= u.getRoleCode()%>.css">
        <link rel="stylesheet" type="text/css" href="/tools/css/subnav.css">
        <script type='text/javascript' src='/oec/javascript/tabber.js'></script>
        <script type='text/javascript' src='/tools/javascript/common.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/programs.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/autocode.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codes.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/responses.js'></script>
        <script type='text/javascript' src='/oec/dwr/engine.js'></script>
	<script type='text/javascript' src='/oec/dwr/util.js'></script>
        <script type="text/javascript">
            /* Optional: Temporarily hide the "tabber" class so it does not "flash"
               on the page as plain HTML. After tabber runs, the class is changed
               to "tabberlive" and it will appear. */
            document.write('<style type="text/css">.tabber{display:none;}<\/style>');
        </script>
        <script type="text/javascript">
        programs.getActiveMonthAndYear(displayDates);
        function displayDates(data)
        {
            DWRUtil.addOptions("909startdates",["Select a fielding period:"]);
            DWRUtil.addOptions("909enddates",["Select a fielding period:"]);
            getFieldingPeriods("909startdates", "12/01/2005");
        }
        /**
        *
        */
        function getFieldingPeriods(elementName, fieldingPeriod)
        {
        	var callbackProxy = function(data) { realCallback(data, fieldingPeriod); };
            var realCallback = function(data, fieldingPeriod)
            {
				DWRUtil.addOptions(elementName, data, 'fullDate', 'displayDate');
            }
            ;
            programs.getDistinctFieldingPeriods(fieldingPeriod, { callback : callbackProxy });
        }
            function displayProgram(data)
            {
                DWRUtil.addOptions("programl",["Program Labels."]);
                DWRUtil.addOptions("programl", data);      
                DWRUtil.addOptions("programla",["Program Labels."]);
                DWRUtil.addOptions("programla", data);
            }
            function resetCodes()
            {
                document.getElementById("message").innerHTML = "";
                if (DWRUtil.getValue("programl") == "Program Labels.")
                {
                    alert("Please select a program.");
                    return false;
                }
                // CHECK THE POSITION OF WHICH CODE NUMBER YOU WANT TO CHANGE
                if (DWRUtil.getValue("codenumber") != "")
                {
                    if ((parseInt(DWRUtil.getValue("codenumber")) < 1) || (parseInt(DWRUtil.getValue("codenumber")) > 6))
                    {
                        alert("Please select a number between 1 and 6.");
                        return false;
                    }
                } else
                {
                    alert("Please enter a value between 1 and 6");
                    return false;
                }
                // CHECK THE CODE NUM AREA
                if (DWRUtil.getValue("codenum") != "")
                {
                    if (!IsNumeric(DWRUtil.getValue("codenum")))
                    {
                        alert("Please enter only numbers for your Code Num.");
                        return false;
                    }
                } else
                {
                    alert("Please enter the codenum you want to reset.");
                    return false;
                }
                responses.getResetCount(DWRUtil.getValue("codenumber"),DWRUtil.getValue("codenum"),DWRUtil.getValue("programl"),displayCount);                
            }
            
            // DISLPAY A MESSAGE TO THE USER ON HOW MANY RESPONSES WILL BE SET BACK TO 
            // A NEW STATUS. USER CAN THEN DECIDE IS HE/SHE WANTS TO PROCEED.
            function displayCount(data)
            {
                var answer = confirm("Wish to continue with resetting the responses to \"NEW\" for "+DWRUtil.getValue('programl')+"? \n"+data+" responses will be reset.");
                if (answer)
                {
                    document.getElementById('message').innerHTML = "<font class='load'>Loading <img src='/images/progressBar.gif' border='0'/></font>";
                    responses.resetResponses(DWRUtil.getValue("codenumber"),DWRUtil.getValue("codenum"),DWRUtil.getValue("programl"),'<%= u.getUserName() %>',displayReset);
                }
            }
            function displayReset(data)
            {
                document.getElementById("message").innerHTML= data;
            }
            function IsNumeric(sText)
            {
               var ValidChars = "-0123456789.";
               var IsNumber=true;
               var Char;
               for (i = 0; i < sText.length && IsNumber == true; i++) 
               { 
                  Char = sText.charAt(i); 
                  if (ValidChars.indexOf(Char) == -1) 
                  {
                     IsNumber = false;
                  }
               }
               return IsNumber;
            }
            function popCodes()
            {
                autocode.populateDictionary(displayPopCodes);
            }
            function displayPopCodes(data)
            {
                document.getElementById("message1").innerHTML= data;
            }
            function autoCodes()
            {
                if (DWRUtil.getValue("programla") == "Program Labels.")
                {
                    alert("Please select a Program that you wish to AutoCode.");
                    return false;
                }
                var answer = confirm("Do you wish to continue with AutoCoding responses for "+DWRUtil.getValue('programla')+"?")
                if (answer)
                {
                    document.getElementById('message2').innerHTML = "<font class='load'>Processing Your Request. <img src='/images/progressBar.gif' border='0'/></br /></font>";
                    autocode.autoCodeResponses(DWRUtil.getValue("programla"),displayAutoCode);
                }
            }
            function displayAutoCode(data)
            {
                 document.getElementById("message2").innerHTML= data;
            }
            /**
            * Show end fielding periods
            */
            function showEndDate()
            {
                var fieldingPeriod = DWRUtil.getValue("909startdates");
                if (fieldingPeriod != "Select a fielding period:")
                {
                    DWRUtil.removeAllOptions("909enddates");
                    DWRUtil.addOptions("909enddates",["Select a fielding period:"]);
                	getFieldingPeriods("909enddates", fieldingPeriod);
                    document.getElementById("909enddates").style.visibility = "visible";
                } else
                {
                    DWRUtil.removeAllOptions("909enddates");
                    DWRUtil.addOptions("909enddates",["Select a fielding period:"]);
                 	document.getElementById("909button").style.visibility = "hidden";
                }
            }
            /**
            * Show button to export 909 frequency report
            */
            function showExportButton()
            {
             	var startPeriod = DWRUtil.getValue("909startdates");
             	var endPeriod = DWRUtil.getValue("909enddates");
             	var buttonElement = document.getElementById("909button");
             	if (endPeriod == "Select a fielding period:")
             	{
             		buttonElement.style.visibility = "hidden";
             	} else
             	{
             		buttonElement.style.visibility = "visible";
             	}
            }
            /**
            * Show Q909 frequency report
            */
            function exportFrequencyReport()
            {
             	var startPeriod = DWRUtil.getValue("909startdates");
             	var endPeriod = DWRUtil.getValue("909enddates");
                var url = "/oec/export?reportType=<%= ExportServlet.REPORT_TYPE_909 %>&startPeriod="+startPeriod+"&endPeriod="+endPeriod;
                WindowObjectReference = window.open(url, "title","width=650,height=500,menubar=yes,location=no,resizable=yes,scrollbars=yes,status=yes");                
                WindowObjectReference.focus();
            }
            /**
            * Export random responses
            */
            function exportRandomResponses()
            {
                var url = "/oec/export?reportType=<%= ExportServlet.REPORT_TYPE_RANDOM %>";
                WindowObjectReference = window.open(url, "title","width=650,height=500,menubar=yes,location=no,resizable=yes,scrollbars=yes,status=yes");                
                WindowObjectReference.focus();
            }
            </script>
    <body class="admin">
        <div id="head">
            <center class="title">Admin Console (Code Maintenance)</center>
            <jsp:include page="admin_nav.jsp"/>
        </div>
            <p id="bar"></p>
            <form name="checkv"/>
            <div class="tabber">
                 <div class="tabbertab">
                      <h2>Reset Codes</h2>
                      <span id="message"></span>
                      <p>
                          <table  class="groupResults" width="500">
                              <tr>
                                  <td>Select a Program:</td>
                                  <td><select id="programl" name="programl"></select></td>
                              </tr>
                              <tr>
                                  <td>Enter the position of code number you want to change (1-6.)</td>
                                  <td>
                                      <select id="codenumber"/>
                                        <option value="0">Select a Position
                                        <option value="1">1
                                        <option value="2">2
                                        <option value="3">3
                                        <option value="4">4
                                        <option value="5">5
                                        <option value="6">6
                                      </select>
                                  </td>
                              </tr>
                              <tr>
                                  <td>Enter the code num you wish to have the responses set back to "NEW".</td>
                                  <td><input type="text" value="" id="codenum" size="10"/></td>
                              </tr>
                              <tr>
                                  <td align="center" colspan="2"><a href="#" class="buttonU" onClick="resetCodes()">Reset Codes</a></td>
                              </tr>
                          </table>  
                      </p>
                      <p id="tab1"></p>
            </div>                 
            <div class="tabbertab">
                <h2>Populate AutoCode Dictionary</h2>
                <span id="message1"></span>
                <p>
                <table  class="groupResults" width="500">
                    <tr>
                        <td>Click the &quot;Populate Dictionary&quot; button to start the procedure. This may take sometime
                            so please be patient. The process will run in the background so you do not have to keep this screen
                        open.</td>
                    </tr>
                    <tr>
                        <td nowrap align="center"><a href="#" class="buttonU" onClick="popCodes()">Populate Dictionary</a></td>
                    </tr>                              
                </table>  
                </p>
                <p id="tab2"></p>
            </div>
            <div class="tabbertab">
                <h2>AutoCode Responses</h2>
                <span id="message2"></span>
                <p>
                <table  class="groupResults" width="500">
                    <tr>
                        <td colspan="2">Select a Program that you wish to Autocode and then click the &quot;AutoCode Responses&quot; 
                        button to start the procedure. This may take sometime so please be patient. The process will run 
                        in the background so you do not have to keep this screen open.</td>
                    </tr>
                    <tr>
                        <td><b>Program:</b></td>
                        <td><select id="programla" name="programla" style="width: 40mm"></select></td>
                    </tr>
                    <tr>
                        <td colspan="2" nowrap align="center"><a href="#" class="buttonU" onClick="autoCodes()">AutoCode Responses</a></td>
                    </tr>                              
                </table>  
                </p>
                <p id="tab3"></p>
            </div>
            <div class="tabbertab">
                <h2>Q909 Frequencies</h2>
                  <table id="909table" style="visibility: visible">
                   <tr>
                    <td><span class="white">Start Date</span></td>
                    <td><select name="909startdates" id="909startdates" onchange="showEndDate();"></select></td>
                    <td><span class="white">End Date</span></td>
                    <td><select name="909enddates" id="909enddates" onchange="showExportButton();" style="visibility: hidden;"></select></td>
                    <td><a href="#" id="909button" class="buttonU" onClick="exportFrequencyReport();" style="visibility: hidden;">Export Frequency</a></td>
                   </tr>
                  </table>
             </div>
            <div class="tabbertab">
                <h2>Export Random Responses</h2>
                 <table>
                  <tr>
                   <td><a href="#" id="responseButton" class="buttonU" onClick="exportRandomResponses();">Export Random Responses</a></td>
                  </tr>
                 </table>
             </div>
            </form>
    </body>
    <script>
        programs.getProgramFacts(displayProgram);
    </script>
 <% } %>
</html>

