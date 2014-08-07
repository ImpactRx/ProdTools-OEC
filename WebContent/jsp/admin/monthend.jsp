<%@ page import="com.targetrx.project.oec.bo.User" %>
<%@ page import="com.targetrx.project.oec.service.CodeDaoImpl" %>
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
        <script type='text/javascript' src='/oec/dwr/interface/codebooks.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/programs.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/autocode.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codes.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/monthend.js'></script>
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
            var pFlag = "0";
            var bFlag = "0";
            var gotDates = "0";
            if (gotDates == "0")
            {
                programs.getActiveMonthAndYear(displayDates);
            }
            /**
            * Populate date elements
            */
            function displayDates(data)
            {
                DWRUtil.addOptions("crtDate",["Select a fielding period:"]);
                DWRUtil.addOptions("datesc",["Select a fielding period:"]);
                DWRUtil.addOptions("datedis",["Select a fielding period:"]);
                DWRUtil.addOptions("dateend",["Select a fielding period:"]);
                DWRUtil.addOptions("datesp",["Select a fielding period:"]);
                DWRUtil.addOptions("monthDate", ["Select a fielding period:"]);
                DWRUtil.addOptions("datecount",["Select a fielding period:"]);
                DWRUtil.addOptions("datemodule",["Select a fielding period:"]);

                DWRUtil.addOptions("datesc", data, 'fullDate', 'displayDate');
                DWRUtil.addOptions("datedis", data, 'fullDate', 'displayDate');
                DWRUtil.addOptions("dateend", data, 'fullDate', 'displayDate');
                DWRUtil.addOptions("datesp", data, 'fullDate', 'displayDate');
                DWRUtil.addOptions("datecount", data, 'fullDate', 'displayDate');
                DWRUtil.addOptions("datemodule", data, 'fullDate', 'displayDate');
                DWRUtil.addOptions("monthDate", data, 'fullDate', 'displayDate');
                DWRUtil.addOptions("crtDate", data, 'fullDate', 'displayDate');
            }
            /**
             * Call check in process
             */
             function checkIn(peid,field)
             {
                 codes.checkInPeid(peid);
                 document.getElementById(field).innerHTML = "<font class=\"checkedin\">Responses checked in.</font>"
             }
             /**
             * Display study types based on selected fielding period
             */
             function getStudyTypes(dateId, studyTypeId, programId, buttonId, resultTableId)
             {
                 var dateElement = document.getElementById(dateId);
                 var programElement = document.getElementById(programId);
                 var buttonElement = document.getElementById(buttonId);
 				var studyTypeElement = document.getElementById(studyTypeId);
             	var fieldingPeriod = dateElement.options[dateElement.options.selectedIndex].value;
                 var callbackProxy = function(data) { realCallback(data, fieldingPeriod); };
                 var realCallback = function(data, fieldingPeriod) 
 	                { 
 	                    DWRUtil.addOptions(studyTypeId, data, "studyTypeCode", "programLabel");
 						studyTypeElement.style.visibility = "visible";
 						studyTypeElement.selectedIndex = 0;
 	                }
 				;
                 DWRUtil.removeAllOptions(studyTypeId);
                 if (fieldingPeriod == "Select a fielding period:")
                 {
                     studyTypeElement.style.visibility = "hidden";
                     programElement.style.visibility = "hidden";
                     buttonElement.style.visibility = "hidden";
                     hideResultTable(resultTableId);
                 } else
                 {
                 	programElement.style.visibility = "hidden";
                 	buttonElement.style.visibility = "hidden";
                 	hideResultTable(resultTableId);
                 	programs.getStudyTypesFromPeriod(fieldingPeriod, { callback : callbackProxy });
                 }
             }
             /**
             * Display programs based on selected study type and fielding period
             */
             function getProgramsFromStudy(dateId, studyTypeId, programId, buttonId, resultTableId)
             {
                 var dateElement = document.getElementById(dateId);
                 var programElement = document.getElementById(programId);
                 var buttonElement = document.getElementById(buttonId);
 				var studyTypeElement = document.getElementById(studyTypeId);
             	var fieldingPeriod = dateElement.options[dateElement.options.selectedIndex].value;
             	var studyTypeValue = studyTypeElement.options[studyTypeElement.options.selectedIndex].value;
             	var callbackProxy = function(data) { realCallback(data, fieldingPeriod, studyTypeValue); };
                 var realCallback = function(data, fieldingPeriod, studyTypeValue) 
                 { 
                     DWRUtil.addOptions(programId, data, "eventId", "programLabel");
                     programElement.style.visibility = "visible";
                     buttonElement.style.visibility = "visible";
                 }
 				;
     	        DWRUtil.removeAllOptions(programId);
     	        hideResultTable(resultTableId);
         	   	programs.getProgramsFromPeriodAndStudyType(fieldingPeriod, studyTypeValue, { callback : callbackProxy });
             }
              /**
              * Hide result table if built
              */
              function hideResultTable(resultTableId)
              {
  				var tableElement = document.getElementById(resultTableId);
  				if (tableElement != null)
  				{
  					tableElement.style.visibility = "hidden";
  				}
  				if (document.getElementById("postIt") != null)
  				{
 	           	 	document.getElementById("postIt").style.visibility = "hidden";
  				}
  				if (document.getElementById("postResults") != null)
  				{
 	           	 	document.getElementById("postResults").style.visibility = "hidden";
  				}
  				if (document.getElementById("statusButton") != null)
  				{
 	                document.getElementById("statusButton").style.visibility = "hidden";
  				} 				
              }
            // -- CHANGE REPORT -- //
            /**
             * Show appropriate change report selections based on type
             */
             function getChangeReport(type)
             {
 				clearCRDisplayTable();
                 var report_date;
                 var byCodeBook = "false";
                 var codebookId = DWRUtil.getValue("cbook");
                 document.getElementById('bar').innerHTML = "<font class='load'>Loading <img src='/images/progressBar.gif' border='0'/></font>";
                 if (type == '<%= CodeDaoImpl.CHANGE_REPORT_ADHOC %>')
                 {
                     report_date = DWRUtil.getValue("months")+"/01/"+DWRUtil.getValue("years");
 					 document.getElementById("crButton").style.visibility = "hidden";
 	                 if (DWRUtil.getValue("checkChangeReport"))
 	                 {
 	                     byCodeBook = "true";
 	                 }
                 } else
                 {
 					report_date = DWRUtil.getValue("crtDate");
                 }
                 codes.getNewCodeHistory(type, report_date, '<%= u.getUserName() %>', byCodeBook, codebookId, displayMonthEnd);
             }
             /**
              * Display codebook drop down
              */
              function displayDropChangeReport()
              {
                  if (DWRUtil.getValue("checkChangeReport"))
                  {
                      if (bFlag == "0")
                      {
                          codebooks.getAllCodeBooks(buildList);
                          bFlag = "1";
                      }
                      document.getElementById('cbook').style.visibility = 'visible';                    
                  } else
                  {
                      document.getElementById('cbook').style.visibility = 'hidden';                    
                  }
              }
              /**
              * Populates the codebook dropdown from server
              */
              function buildList(data)
              {
                  DWRUtil.addOptions("cbook", data);
              }
              /**
              * Display the month end check report display data
              */
              function displayMonthEnd(data)
              {
                  var output = "";
                  output = "<table id=\"crDisplayTable\" class=\"monthend\" border=\"1\" cellpadding=\"2\" cellspacing=\"2\">";
                  output += "<tr>";
                  output += "<th>CodeBook</th>";
                  output += "<th>Code</th>";
                  output += "<th>Net</th>";
                  output += "<th>Net2</th>";
                  output += "<th>Net3</th>";
                  output += "<th>Action</th>";
                  output += "<th>Datetime</th>";
                  output += "</tr>";
                  if (data.length > 0)
                  {
                      for (var i = 0; i < data.length; i++)
                      {
                          var result = data[i];
                          output += "<tr>";
                          output += "<td align=\"center\">"+result.codebookLabel+"</td>";
                          output += "<td>"+result.codeLabel+"</td>";
                          output += "<td>"+result.net1Label+"</td>";
                          if (result.net2Label != null)
                          {
                              output += "<td>"+result.net2Label+"</td>";
                          } else
                          {
                              output += "<td>&nbsp;</td>";
                          }
                          if (result.net3Label != null)
                          {
                              output += "<td>"+result.net3Label+"</td>";
                          } else
                          {
                              output += "<td>&nbsp;</td>";
                          }
                          output += "<td align=\"center\">"+result.historyAction+"</td>";
                          output += "<td align=\"center\">"+(result.actionDatetime.getMonth()+1)+"/"+result.actionDatetime.getDay()+"/"+result.actionDatetime.getFullYear()+"</td>";
                          output += "</tr>";
                      }
                      output += "</table>";
                  } else
                  {
                      output += "<tr><td colspan=\"7\" align=\"center\" style=\"color: red; font-weight: bold\">No rows</td></tr>";
                  }
                  document.getElementById('changeReportDisplay').innerHTML = output;
                  document.getElementById('bar').innerHTML = "";
              }
              /**
              * Show table based on selected radio button
              */
  			function showCRForm(form)
  			{
  				clearCRDisplayTable();
  				displayDropChangeReport();
  				document.getElementById("crButton").style.visibility = "hidden";
  				var crtPick = form.crtPick;
  				for (var i = 0; i < crtPick.length; i++)
  				{
  					var radioElement = crtPick[i];
  					var tableElement = document.getElementById(radioElement.value);
  					if (radioElement.checked)
  					{
  						if (radioElement.value == "crtCheckTable")
  						{
  							showCRButton(form.crtDate.options[form.crtDate.selectedIndex].value);
  							document.getElementById("cbook").style.visibility = "hidden";
  						}
  						tableElement.style.visibility = "visible";
  					} else
  					{
  						tableElement.style.visibility = "hidden";
  					}
  				}	
  			}
  			/**
  			* Show button to execute appropriate change report
  			*/
  			function showCRButton(selectedValue)
  			{
  				clearCRDisplayTable();
  				if (selectedValue != "Select a fielding period:")
  				{
  					document.getElementById("crButton").style.visibility = "visible";
  				} else
  				{
  					document.getElementById("crButton").style.visibility = "hidden";
  				}					
  			}
  			/**
  			* Clear the change report display table
  			*/
  			function clearCRDisplayTable()
  			{
                  document.getElementById("changeReportDisplay").innerHTML = "";
                  var displayTable = document.getElementById("crDisplayTable")
                  if (displayTable != null)
                  {
                      displayTable.style.visibility = "hidden";
                  }
  			}
  			// -- DISCREPANCY REPORT -- //
  			/**
            * Run discrepancy report
            */
            function getDiscrepancyReport()
            {
                document.getElementById('bar').innerHTML = "<font class='load'>Loading <img src='/images/progressBar.gif' border='0'/></font>";
                autocode.exeAutoCodeDiscrepReport('<%= u.getUserName() %>',displayAutoCode);
            }
            /**
            * Show results of discrepancy report
            */
            function displayAutoCode()
            {
                var output = "<font style=\"color: white; font-weight: bold;\">An AutoCode Discrepancy Report has been queued.</font>"
                document.getElementById('tab2').innerHTML = output;
                document.getElementById('bar').innerHTML = "";
            }
  			// -- CHECK VERIFY -- //
            /**
              * Display results from check verify report
              */
              function getCheckVerifyReport()
              {
                  var program = DWRUtil.getValue("proglabel");
              	document.getElementById('bar').innerHTML = "<font class='load'>Loading <img src='/oec/images/progressBar.gif' border='0'/></font>";
                  codes.getCheckVerifyForProgram(program,'<%= u.getUserName() %>',displayCheckVerify);
              }
             /**
             * Show status for a program
             */
             function displayCheckVerify(data)
             {
                 var output = "<table id=\"checkVerifyTable\" class=\"monthend\" border=\"1\"><tr><th>Count</th><th>Status</th><th>Tag Type</th><th>Action</th></tr>"
                 for (var i = 0; i < data.length; i++)
                 {
                     var result = data[i];
                     var tagType = "";
                     var action = "";
                     var fontColor = "black";
                     var readyToPost = false;
                     if (result.statusCode == "cu")
                     {
                         readyToPost = false;
                         action = "<a href=\"#\" class=\"buttonU\" onClick=\"checkIn('"+result.programEventId+"','"+i+"Check')\">Check In</a>";
                     } else if (result.statusCode == "new")
                     {
                         readyToPost = false;
                         action = "Checkout and Code";
                         fontColor = "red";
                     } else if (result.statusCode == "review")
                     {
                         readyToPost = false;
                         action = "Review Required";
                         fontColor = "red";
                     } else
                     {
                         readyToPost = true;
                         action = "No Action Needed";
                     }
                     if (result.tagTypeCode != null)
                     {
                         tagType = result.tagTypeCode;
                     }
                     output += "<tr><td align=\"center\">"+result.count+"</td>";
                     output += "<td align=\"center\">"+result.statusCode+"</td>";
                     output += "<td align=\"center\">"+tagType+"</td>";
                     output += "<td align=\"center\" style=\"color: " + fontColor + ";\">" + action + "</td></tr>";
                 }
                 if (data.length == 0)
                 {
                     output += "<tr><td align=\"center\" colspan=\"4\" style=\"color: red;\">No responses exist for this program.</td></tr>";
                     output += "<tr><td align=\"center\" colspan=\"4\" style=\"background: yellow; color: black;  font-weight: bold;\">Check was not executed as no data exists.</td></tr>";
                 } else
                 {
                     if (readyToPost)
                     {
                         output += "<tr><td align=\"center\" colspan=\"4\" style=\"background: green; color: white; font-weight: bold;\">Check succeeded</td></tr>";
                     } else
                     {
                         output += "<tr><td align=\"center\" colspan=\"4\" style=\"background: red; color: white;  font-weight: bold;\">Check failed. See above for rows requiring action</td></tr>";
                     }
                 }
                 output += "</table>";
                 document.getElementById('tab3').innerHTML = output;
                 document.getElementById('bar').innerHTML = "";
             }
   			// -- DISCONNECTED -- //
             /**
              * Function does the checking to make sure all the program events
              * have been disconnected from production.
              */ 
              function checkDisconnected()
              {
                 var peid = DWRUtil.getValue("proglabeldis");
                 document.getElementById("disconnectedResults").innerHTML = "";
                 monthend.findOpenEvents(peid, '<%= u.getUserName() %>', displayDisconnectedResults);
              }
              /**
              * Show results from disconnected check
              */
              function displayDisconnectedResults(data)
              {
                  var output = "<table id=\"disconnectedTable\" class=\"monthend\" border=\"1\"><tr><th>Event Id</th><th>Event Label</th></tr>";
                  for (var i = 0; i < data.length; i++)
                  {
                      var result = data[i];
                      output += "<tr><td align=\"center\">"+result.eventId+"</td>";
                      output += "<td align=\"center\">"+result.programLabel+"</td>";
                  }
                  if (data.length == 0)
                  {
                      output += "<tr><td align=\"center\" colspan=\"2\" style=\"background: green; color: white; font-weight: bold;\">Check succeeded</td></tr>";
                  } else
                  {
                      output += "<tr><td align=\"center\" colspan=\"2\" style=\"background: red; color: white;  font-weight: bold;\">Check failed. See above for events that are still open</td></tr>";
                  }
                  output += "</table>";
                  document.getElementById('disconnectedResults').innerHTML = output;
                  document.getElementById('bar').innerHTML = "";
              }
   			// -- END EVENT -- //
			/**
			* Check to see if data_cutoff has been set
			*/
            function checkEndEvent()
            {
                document.getElementById("endEventResults").innerHTML = "";
				monthend.findDataDisconnectedDate(DWRUtil.getValue("proglabelend"), '<%= u.getUserName() %>', displayEnd);
            }
            /**
            * Display end event / data cutoff results
            */
            function displayEnd(data)
            {
                var output = "<table id=\"endeventTable\" class=\"monthend\" border=\"1\"><tr><th>Event Id</th><th>Event Label</th></tr>";
                for (var i = 0; i < data.length; i++)
                {
                    var result = data[i];
                    output += "<tr><td align=\"center\">"+result.eventId+"</td>";
                    output += "<td align=\"center\">"+result.programLabel+"</td>";
                }
                if (data.length == 0)
                {
                    output += "<tr><td align=\"center\" colspan=\"2\" style=\"background: green; color: white; font-weight: bold;\">Check succeeded</td></tr>";
                } else
                {
                    output += "<tr><td align=\"center\" colspan=\"2\" style=\"background: red; color: white;  font-weight: bold;\">Check failed. See above for events that do not have a data cutoff set</td></tr>";
                }
                output += "</table>";
                document.getElementById('endEventResults').innerHTML = output;
                document.getElementById('bar').innerHTML = "";
            }
   			// -- COUNT CHECK -- //
            /**
             * Execute count check
             */
             function checkCount()
             {
                 document.getElementById('bar').innerHTML = "<font class='load'>Loading <img src='/oec/images/progressBar.gif' border='0'/></font>";
                 var peid = DWRUtil.getValue("proglabelcount");
                 monthend.checkCodyCount(peid, '<%= u.getUserName() %>', displayCheckCount);
             }
             /**
             * Show check count results
             */
             function displayCheckCount(data)
             {
                 document.getElementById("displayCount").style.visibility = "visible";
                 document.getElementById("displayCount").innerHTML = data;
                 document.getElementById('bar').innerHTML = "";
             }
            // -- MODULE REVIEW -- //
            /**
             *
             */
            function checkReviewModule()
            {
                document.getElementById('bar').innerHTML = "<font class='load'>Loading <img src='/oec/images/progressBar.gif' border='0'/></font>";
                var peid = DWRUtil.getValue("proglabelmodule");
                monthend.checkModuleReview(peid, '<%= u.getUserName() %>', displayReviewModule);
            }
           /**
            *
            */
            function displayReviewModule(data)
            {
                 document.getElementById("displayModule").style.visibility = "visible";
                 document.getElementById("displayModule").innerHTML = data;
                 document.getElementById('bar').innerHTML = "";
            }
            /**
             * Get post check results
             */
             function getCheckResults()
             {
              	document.getElementById('bar').innerHTML = "<font class='load'>Loading <img src='/oec/images/progressBar.gif' border='0'/></font>";
                var peid = DWRUtil.getValue("proglabelpost");
             	monthend.getCheckStatus(peid, displayPostCodes);
             }
            // -- POST CODES -- //
            /**
             * Display post check results
             */
             function displayPostCodes(data)
             {
                 var output = "<table id=\"postTable\" class=\"monthend\" border=\"1\">";
                 var showPostButton = true;
                 for (var i = 0; i < data.length; i++)
                 {
                     var backgroundColor;
                     var resultString;
                     var result = data[i];
                     output += "<tr><th>"+result.description+"</th></tr>";
                     output += "<tr><td>";
                     output += "<table class=\"monthend\" border=\"1\">";
                     var checks = result.checkStatusList;
                     for (var x = 0; x < checks.length; x++)
                     {
                         if (checks[x].checkResult == null)
                         {
                             resultString = "Not Executed";
                             showPostButton = false;
                         } else
                         {
                             resultString = checks[x].checkResult;
                         }
                         if (resultString == "Success")
                         {
                             backgroundColor = "green";
                         } else
                         {
                             backgroundColor = "red";
                             showPostButton = false;
                         }
                         output += "<tr>";
                         output += "<td width=\"25%\" style=\"font-weight: bold; color: #006699; background: white;\">"+ checks[x].checkName + "</td>";
                         output += "<td width=\"75%\" style=\"font-weight: bold; color: white; background:"+backgroundColor+";\">"+ resultString + "</td>";
                         output += "</tr>";
                     }
                     output += "</table>";
                     output += "</td></tr>";
                 }
                 output += "</table>";
                 document.getElementById('postResults').innerHTML = output;
                 document.getElementById("postResults").style.visibility = "visible";
                 document.getElementById('bar').innerHTML = "";
                 if (showPostButton)
                 {
                	 document.getElementById("postIt").style.visibility = "visible";
                 }
             }
             /**
             * Check to see if program can be posted
             */
             function postProgram()
             {
                 var peid = DWRUtil.getValue("proglabelpost");
                 monthend.postProgram(peid, displayPostResults);
             }
             /**
             * Display results on whether a program can be posted
             */
             function displayPostResults(data)
             {
                 alert(data);
                 document.getElementById("postIt").style.visibility = "hidden";
            	 document.getElementById("postResults").innerHTML = data;
             }
   			 // -- MONTH STATUS -- //
             /**
              * Display button to click for month status
              */
              function showMonthStatusButton()
              {
                  document.getElementById("statusButton").style.visibility = "visible";
                  document.getElementById('monthStatusResults').style.visibility = "hidden";
              }
             /**
             * Show status for each program for the selected month
             */
             function getMonthStatus()
             {
                 var studyType = DWRUtil.getValue("mStudyTypes");
                 var fieldingPeriod = DWRUtil.getValue("monthDate");
                 monthend.getMonthStatus(studyType, fieldingPeriod, displayMonthStatus);
             }
             /**
             * Display results for each program
             */
             function displayMonthStatus(data)
             {
                 var background;
                 var output = "<table id=\"monthStatusTable\" class=\"monthend\" border=\"1\">";
                 output += "<tr>";
                 output += "<th>Event Id</th>";
                 output += "<th>Event Label</th>";
                 output += "<th>Supplement Code</th>";
                 output += "<th>Post Status</th>";
                 output += "</tr>";
                 for (var i = 0; i < data.length; i++)
                 {
                     var result = data[i];
                     if (result.statusCode == "Posted")
                     {
                         background = "green";
                     } else
                     {
                         background = "red";
                     }
                     output += "<tr>";
                     output += "<td align=\"center\">"+result.eventId+"</td>";
                     output += "<td align=\"center\">"+result.programLabel+"</td>";
                     output += "<td align=\"center\">"+result.supplementCode+"</td>";
                     output += "<td align=\"center\" style=\"background: " + background + "; color: white; font-weight: bold;\">"+result.statusCode+"</td>";
                     output += "</tr>";
                 }
                 output += "</table>";
                 document.getElementById('monthStatusResults').style.visibility = "visible";
                 document.getElementById('monthStatusResults').innerHTML = output;
             }
        </script>
    <body class="admin">
        <div id="head">
            <center class="title">Admin Console (Month End)</center>
            <jsp:include page="admin_nav.jsp"/>
        </div>
            <p id="bar"></p>
            <form name="checkv"/>
            <div class="tabber">
                 <!-- ***************************************************************************** -->
                 <!-- ******************************** CHANGE REPORT ****************************** -->
                 <!-- ***************************************************************************** -->
                 <div class="tabbertab">
                      <h2>Change Rpt</h2>
                      <table id="changeReportTable">
                       <tr>
                        <td>
                         <input name="crtPick" id="crtPick" type="radio" value="crtCheckTable" checked onclick="showCRForm(this.form);"><span class="white">View Month End</span>
                         <input name="crtPick" id="crtPick" type="radio" value="crtAdhocTable" onclick="showCRForm(this.form);"><span class="white">View Ad-hoc</span>
                        </td>
                       </tr>
                       <tr>
                        <td>
	                      <table id="crtCheckTable" style="visibility: visible">
	                       <tr>
	                        <td><span class="white">Date</span></td>
	                        <td><select name="crtDate" id="crtDate" onchange="showCRButton(this.form.crtDate.options[this.form.crtDate.selectedIndex].value);"></select></td>
	                        <td id="crButton" style="visibility: hidden"><a href="#" class="buttonU" onClick="getChangeReport('<%=CodeDaoImpl.CHANGE_REPORT_MONTHEND %>')">Get Change Report</a></td>
	                       </tr>
	                      </table>
                        </td>
                       </tr>
                       <tr>
                        <td>
	                     <table id="crtAdhocTable" style="visibility: hidden">
	                       <tr>
	                        <td><span class="white">Month:</span></td>
	                        <td>
	                         <select name="months" id="months">
	                            <option value="01"/>01</option>
	                            <option value="02"/>02</option>
	                            <option value="03"/>03</option>
	                            <option value="04"/>04</option>
	                            <option value="05"/>05</option>
	                            <option value="06"/>06</option>
	                            <option value="07"/>07</option>
	                            <option value="08"/>08</option>
	                            <option value="09"/>09</option>
	                            <option value="10"/>10</option>
	                            <option value="11"/>11</option>
	                            <option value="12"/>12</option>
	                         </select>
	                        </td>
	                        <td><span class="white">Year:</span></td>
	                        <td>
	                         <select name="years" id="years">
	                            <option value="2006">2006</option>
	                            <option value="2007">2007</option>
	                            <option value="2008">2008</option>
	                            <option value="2009">2009</option>
	                            <option value="2010">2010</option>
	                            <option value="2011">2011</option>
	                            <option value="2012">2012</option>
	                            <option value="2013">2013</option>
	                         </select>
	                        </td>
	                        <td><input type="checkbox" name="checkChangeReport" id="checkChangeReport" value="true" onclick="displayDropChangeReport()" /></td>
	                        <td><span class="white">By Codebook</span></td>
	                        <td><select id="cbook" name="cbook" style="visibility: hidden;"></select></td>
	                        <td><a href="#" class="buttonU" onClick="getChangeReport('<%=CodeDaoImpl.CHANGE_REPORT_ADHOC %>')">Get Change Report</a></td>
	                       </tr>
	                      </table>
                        </td>
                       </tr>
                      </table>
                      <p id="changeReportDisplay"></p>
                 </div>
                 <!-- Discrepancy Report  -->
                 <div class="tabbertab">
                      <h2>Discrep Rpt</h2>
                      <p>When you execute a discrepancy report you acutally queue up a job that will run in the
                      background of the application. An email will be sent to you with any discrepancies found. If
                      you do not recieve an email please check to make sure you are on the email list.</p>
                      <p>
                        <a href="#" class="buttonU" onClick="getDiscrepancyReport()">Queue up Discrepancy Report</a>
                      </p>
                      <p id="tab2"></p>
                 </div>
                 <!-- Check/Verify Report -->
                 <div class="tabbertab">
                      <h2>CV Rpt</h2>
                      <table>
                       <tr>
                        <td><span class="white">Date</span></td>
                        <td><select name="datesc" id="datesc" onchange="getStudyTypes('datesc', 'cvStudyTypes', 'proglabel', 'checkVerify', 'checkVerifyTable')"></select></td>
                        <td><select name="cvStudyTypes" id="cvStudyTypes" onchange="getProgramsFromStudy('datesc', 'cvStudyTypes', 'proglabel', 'checkVerify', 'checkVerifyTable')" onfocus="getProgramsFromStudy('datesc', 'cvStudyTypes', 'proglabel', 'checkVerify', 'checkVerifyTable')" style="visibility: hidden;"></select></td>
                        <td><select id="proglabel" name="proglabel" style="visibility: hidden;" onchange="hideResultTable('checkVerifyTable')"></select></td>
                        <td><a href="#" id="checkVerify" class="buttonU" onClick="getCheckVerifyReport()" style="visibility: hidden;">Get Check/Verify Report</a> </td>
                       </tr>
                      </table>
                      <p id="tab3"></p>
                 </div>
                 <!-- Disconnected -->
                 <div class="tabbertab">
                     <h2>Disconnected</h2>
                      <table>
                       <tr>
                        <td><span class="white">Date</span></td>
                        <td><select name="datedis" id="datedis" onchange="getStudyTypes('datedis', 'dStudyTypes', 'proglabeldis', 'disconnected', 'disconnectedTable')"></select></td>
                        <td><select name="dStudyTypes" id="dStudyTypes" onchange="getProgramsFromStudy('datedis', 'dStudyTypes', 'proglabeldis', 'disconnected', 'disconnectedTable')" onfocus="getProgramsFromStudy('datedis', 'dStudyTypes', 'proglabeldis', 'disconnected', 'disconnectedTable')" style="visibility: hidden;"></select></td>
                        <td><select id="proglabeldis" name="proglabeldis" style="visibility: hidden;" onchange="hideResultTable('disconnectedTable')"></select></td>
                        <td><a href="#" id="disconnected" class="buttonU" onClick="checkDisconnected()" style="visibility: hidden;">Check Disconnect</a> </td>
                       </tr>
                      </table>
                      <span id="message" style="color: green; font-size: 12pt; font-weight: bold;"></span> <br />
                      <p id="disconnectedResults" style="color: yellow; font-weight: bold;"></p>
                 </div>   
                 <!-- End Event -->
                 <div class="tabbertab">
                     <h2>End Event</h2>
                      <table>
                       <tr>
                        <td><span class="white">Date</span></td>
                        <td><select name="dateend" id="dateend" onchange="getStudyTypes('dateend', 'eeStudyTypes', 'proglabelend', 'endevent', 'endeventTable')"></select></td>
                        <td><select name="eeStudyTypes" id="eeStudyTypes" onchange="getProgramsFromStudy('dateend', 'eeStudyTypes', 'proglabelend', 'endevent', 'endeventTable')" onfocus="getProgramsFromStudy('dateend', 'eeStudyTypes', 'proglabelend', 'endevent', 'endeventTable')" style="visibility: hidden;"></select></td>
                        <td><select id="proglabelend" name="proglabelend" style="visibility: hidden;" onchange="hideResultTable('endeventTable')"></select></td>
                        <td><a href="#" id="endevent" class="buttonU" onClick="checkEndEvent()" style="visibility: hidden;">Check End Event</a> </td>
                       </tr>
                      </table>
                      <p id="endEventResults" style="color: green; font-size: 12pt; font-weight: bold;"></p>
                 </div>   
                 <!-- Check Count -->
                 <div class="tabbertab">
                     <h2>Check Count</h2>
                      <span id="message" style="color: green; font-size: 12pt; font-weight: bold;"></span> <br />
                      <table>
                       <tr>
                        <td><span class="white">Date</span></td>
                        <td><select name="datecount" id="datecount" onchange="getStudyTypes('datecount', 'cStudyTypes', 'proglabelcount', 'countButton', 'displayCount')"></select></td>
                        <td><select name="cStudyTypes" id="cStudyTypes" onchange="getProgramsFromStudy('datecount', 'cStudyTypes', 'proglabelcount', 'countButton', 'displayCount')" onfocus="getProgramsFromStudy('datecount', 'cStudyTypes', 'proglabelcount', 'countButton', 'displayCount')" style="visibility: hidden;"></select></td>
                        <td><select id="proglabelcount" name="proglabelcount" style="visibility: hidden;" onchange="hideResultTable('displayCount')"></select></td>
                        <td><a href="#" id="countButton" class="buttonU" onClick="checkCount()" style="visibility: hidden;">Check Count Against Response Facts</a> </td>
                       </tr>
                      </table>
                      <span id="displayCount" style="align: center; color: white; font-size: 12pt; font-weight: bold; visibility: hidden;"></span>
                 </div> 
                 <!-- Check Module Review -->
                 <div class="tabbertab">
                  <h2>Module Review</h2>
                      <table>
                       <tr>
                        <td><span class="white">Date</span></td>
                        <td><select name="datemodule" id="datemodule" onchange="getStudyTypes('datemodule', 'rmStudyTypes', 'proglabelmodule', 'moduleButton', 'displayModule')"></select></td>
                        <td><select name="rmStudyTypes" id="rmStudyTypes" onchange="getProgramsFromStudy('datemodule', 'rmStudyTypes', 'proglabelmodule', 'moduleButton', 'displayModule')" onfocus="getProgramsFromStudy('datemodule', 'rmStudyTypes', 'proglabelmodule', 'moduleButton', 'displayModule')" style="visibility: hidden;"></select></td>
                        <td><select id="proglabelmodule" name="proglabelmodule" style="visibility: hidden;" onchange="hideResultTable('displayModule')"></select></td>
                        <td><a href="#" id="moduleButton" class="buttonU" onClick="checkReviewModule()" style="visibility: hidden;">Check Module Review</a> </td>
                       </tr>
                      </table>
                      <span id="displayModule" style="align: center; color: white; font-size: 12pt; font-weight: bold; visibility: hidden;"></span>
                 </div>
                 <!-- Open End Posting -->
                 <div class="tabbertab">
                     <h2>Post Codes</h2>
                      <p style="color: yellow; font-size: 12pt; font-weight: bold;">Before Posting Codes you need to have NPD conduct their checks</p>
                      <table>
                       <tr>
                        <td><span class="white">Date</span></td>
                        <td><select name="datesp" id="datesp" onchange="getStudyTypes('datesp', 'pStudyTypes', 'proglabelpost', 'checkButton', 'postTable')"></select></td>
                        <td><select name="pStudyTypes" id="pStudyTypes" onchange="getProgramsFromStudy('datesp', 'pStudyTypes', 'proglabelpost', 'checkButton', 'postTable')" onfocus="getProgramsFromStudy('datesp', 'pStudyTypes', 'proglabelpost', 'checkButton', 'postTable')" style="visibility: hidden;"></select></td>
                        <td><select id="proglabelpost" name="proglabelpost" style="visibility: hidden;" onchange="hideResultTable('postTable')"></select></td>
                        <td><a href="#" id="checkButton" class="buttonU" onClick="getCheckResults()" style="visibility: hidden;">Check Results</a> </td>
                       </tr>
                      </table>
                      <span id="postResults"></span> <br/>
                      <a href="#" id="postIt" class="buttonU" onClick="postProgram()" style="visibility: hidden;">Post Program</a>
                 </div>
                 <div class="tabbertab">
                  <h2>Month Status</h2>
                      <table>
                       <tr>
                        <td><span class="white">Date</span></td>
                        <td><select name="monthDate" id="monthDate" onchange="getStudyTypes('monthDate', 'mStudyTypes', 'proglabelpost', 'checkButton', 'monthStatusTable')"></select></td>
                        <td><select name="mStudyTypes" id="mStudyTypes" style="visibility: hidden;" onchange="showMonthStatusButton()" onfocus="showMonthStatusButton()"></select></td>
                        <td><a href="#" id="statusButton" class="buttonU" onClick="getMonthStatus()" style="visibility: hidden;">Show Month Status</a> </td>
                       </tr>
                      </table>
                      <p id="monthStatusResults"></p>
                 </div>
            </div>    
            </form>
    </body>
 <% } %>
</html>

