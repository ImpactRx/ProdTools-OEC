<%@ page import="com.targetrx.project.oec.bo.User" %>
<%
   String cbId = request.getParameter("codebookid");
   User u = (User)session.getAttribute("user");   
%>
<% if (u == null) { %>
    <script language="Javascript">
    window.location = "/oec/main";
    </script>
<% } else { %>
<%
  //String cbId = request.getParameter("codebookid");
  if ((cbId == null) || (cbId.equalsIgnoreCase("null")))
  {
    cbId = (String) session.getAttribute("codebookId");
  }
  session.setAttribute("codebookId",cbId);
  
%>
<html>
    <head>
      <title>Cody Custom Reports</title>
      <link rel="stylesheet" type="text/css" href="/tools/css/oec.css">
      <link rel="stylesheet" type="text/css" href="/oec/css/<%= u.getRoleCode()%>.css">
      <link rel="stylesheet" type="text/css" href="/tools/css/layout.css">
      <link rel="stylesheet" type="text/css" href="/tools/css/tools.css">
      <script type='text/javascript' src='/oec/javascript/event.js'></script>
			<script type='text/javascript' src='/oec/dwr/interface/codebooks.js'></script>
			<script type='text/javascript' src='/oec/dwr/interface/client.js'></script>
			<script type='text/javascript' src='/oec/dwr/interface/programs.js'></script>
			<script type='text/javascript' src='/oec/dwr/interface/pq.js'></script>
			<script type='text/javascript' src='/oec/dwr/interface/codes.js'></script>
			<script type='text/javascript' src='/oec/dwr/interface/nets.js'></script>
      <script type='text/javascript' src='/oec/dwr/interface/responses.js'></script>
      <script type='text/javascript' src='/oec/dwr/engine.js'></script>
			<script type='text/javascript' src='/oec/dwr/util.js'></script>
    </head>
    <script type="text/javascript">
    // Declaring valid date character, minimum year and maximum year
		var dtCh= "/";
		var minYear=1900;
		var maxYear=2100;
		function getTodaysDate()
		{
		 	var d = new Date();
		
			return (((1+d.getMonth())<10) ? "0" + (d.getMonth()+1): d.getMonth()+ 1) + "/01/" + d.getFullYear();
		}
		function isInteger(s){
			var i;
		    for (i = 0; i < s.length; i++){   
		        // Check that current character is number.
		        var c = s.charAt(i);
		        if (((c < "0") || (c > "9"))) return false;
		    }
		    // All characters are numbers.
		    return true;
		}
		
		function stripCharsInBag(s, bag){
			var i;
		    var returnString = "";
		    // Search through string's characters one by one.
		    // If character is not in bag, append to returnString.
		    for (i = 0; i < s.length; i++){   
		        var c = s.charAt(i);
		        if (bag.indexOf(c) == -1) returnString += c;
		    }
		    return returnString;
		}
		
		function daysInFebruary (year){
			// February has 29 days in any year evenly divisible by four,
		    // EXCEPT for centurial years which are not also divisible by 400.
		    return (((year % 4 == 0) && ( (!(year % 100 == 0)) || (year % 400 == 0))) ? 29 : 28 );
		}
		function DaysArray(n) {
			for (var i = 1; i <= n; i++) {
				this[i] = 31
				if (i==4 || i==6 || i==9 || i==11) {this[i] = 30}
				if (i==2) {this[i] = 29}
		   } 
		   return this
		}
		
		function isDate(dtStr){
			var daysInMonth = DaysArray(12);
			var pos1=dtStr.indexOf(dtCh);
			var pos2=dtStr.indexOf(dtCh,pos1+1);
			var strMonth=dtStr.substring(0,pos1);
			var strDay=dtStr.substring(pos1+1,pos2);
			var strYear=dtStr.substring(pos2+1);
			strYr=strYear;
			if (strDay.charAt(0)=="0" && strDay.length>1) strDay=strDay.substring(1)
			if (strMonth.charAt(0)=="0" && strMonth.length>1) strMonth=strMonth.substring(1)
			for (var i = 1; i <= 3; i++) {
				if (strYr.charAt(0)=="0" && strYr.length>1) strYr=strYr.substring(1)
			}
			month=parseInt(strMonth);
			day=parseInt(strDay);
			year=parseInt(strYr);
			if (pos1==-1 || pos2==-1){
				alert("The date format should be : mm/dd/yyyy");
				return false;
			}
			if (strMonth.length<1 || month<1 || month>12){
				alert("Please enter a valid month");
				return false;
			}
			if (strDay.length<1 || day<1 || day>31 || (month==2 && day>daysInFebruary(year)) || day > daysInMonth[month]){
				alert("Please enter a valid day");
				return false;
			}
			if (strYear.length != 4 || year==0 || year<minYear || year>maxYear){
				alert("Please enter a valid 4 digit year between "+minYear+" and "+maxYear);
				return false;
			}
			if (dtStr.indexOf(dtCh,pos2+1)!=-1 || isInteger(stripCharsInBag(dtStr, dtCh))==false){
				alert("Please enter a valid date");
				return false;
			}
		return true;
		}
		
		function ValidateDate(obj){
			var dt=obj
			if (isDate(dt.value)==false){
				dt.focus();
				return false;
			}
		    return true;
		 }

    </script>
    <script type="text/javascript">
    	
    	var codebook_name;
    	var peids = "";
    	var mids;
    	var productIds;
    	var qids;
    	var cells;
    	// FUNCTION GETS EXECUTED WHEN USER COMES TO THIS PAGE AND ON REFRESH
    	  function selectSdate(iStart, iLength, obj) {
					if (obj.createTextRange) {
						var oRange = obj.createTextRange();
						oRange.moveStart("character", iStart);
						oRange.moveEnd("character", iLength - obj.value.length);
						oRange.select();
					} else if (obj.setSelectionRange) {
						obj.setSelectionRange(iStart, iLength);
					}

						obj.focus();
        }
        function buildPage()
				{
					displayLoading("process");
					programs.getAllStudyTypes(displayStudyTypes);
					//programs.getAllProgramLabels("AUDIT",displayProgram);   
					var todaysDate = getTodaysDate();
					DWRUtil.setValue("sdate",todaysDate);
					DWRUtil.setValue("tdate",todaysDate);
					selectSdate(0, 2, document.rpts.sdate);
					hideLoading("load");				  
				}
				// LOADS THE STUDY TYPE SLECT BOX
				function displayStudyTypes(data)
				{
					DWRUtil.removeAllOptions("study");
					DWRUtil.addOptions("study",["Select a Study Type"]);
					for (var i=0; i<data.length; i++)
					{
						var result = data[i];
						
						if (result.studyTypeCode != "null")
						{
							DWRUtil.addOptions("study",[result.studyTypeCode]);
						}
					}
				}
				// MAKES THE CALL TO GET THE PROGRAMS FOR A SPECIFIC STUDY AND AFTER A CERTAIN DATE
				function getProgramLabels(ob, obC)
				{
					displayLoading("process");
					DWRUtil.removeAllOptions("programs");
        	DWRUtil.addOptions("programs",["Loading Programs....."]);
					if (DWRUtil.getValue("sdate") == "")
					{
						buildPage();
						alert("Please enter a date. mm/dd/yyyy");						
						return false;
					}
					var proceed = ValidateDate(document.rpts.sdate);
					if (!proceed)
					{
						hideLoading("process");
						return false;
					}
					proceed = ValidateDate(document.rpts.tdate);
					if (!proceed)
					{
						hideLoading("process");
						return false;
        	}
					qids = "";
					for(var i = 0; i < ob.options.length; ++i)
					{
						if (ob.options[i].selected)
					 	{
							if (ob.options[i].value != "Select one or many Questions.")
							{
								qids = qids+ob.options[i].value +","; 
							}
						}
					}
					cells = "";
					for(var i = 0; i < obC.options.length; ++i)
					{
						if (obC.options[i].selected)
						{
							if (obC.options[i].value != "Select one or many Cells.")
							{
								cells = cells+obC.options[i].value +","; 
							}
						}
					}
					if (mids.indexOf("Select one or many markets.") > -1)
					{
						alert("Please select one or more Markets.");
						hideLoading("process");
						return false;
					}
					if (productIds == "")
					{
						alert("Please select one or more Products.");
						hideLoading("process");
						return false;
					}
					if (qids == "")
					{
						alert("Please select one or many Survey Question Labels.");
						hideLoading("process");
						return false;
					}
					if (cells == "")
					{
						alert("Please select one or many Cells.");
						hideLoading("process");
						return false;
					}
					
					//programs.getAllProgramLabelsByType(DWRUtil.getValue("study"),DWRUtil.getValue("sdate"),displayProgram);
					programs.getAllProgramLabelsByTypeAnDate(cells.substring(0,cells.length-1),DWRUtil.getValue("study"), mids.substring(0,mids.length-1), productIds.substring(0,productIds.length-1), DWRUtil.getValue("sdate"), DWRUtil.getValue("tdate"),displayProgram);
					document.getElementById("step4").style.visibility = 'visible';
						
				}
				// LOADS THE PROGRAMS SELECT BOX
				function displayProgram(data)
				{
					DWRUtil.removeAllOptions("programs");
					DWRUtil.addOptions("programs",["Select one or many Program Labels."]);
				  DWRUtil.addOptions("programs", data);                  
				  //DWRUtil.removeAllOptions("markets");
				  //document.getElementById("step3").style.visibility = 'hidden';
				  //DWRUtil.removeAllOptions("products");
				  // document.getElementById("step3a").style.visibility = 'hidden';
				  //DWRUtil.removeAllOptions("questions");
				  //document.getElementById("step4").style.visibility = 'hidden';
				  document.getElementById("getreport").style.visibility = 'visible';
				  hideLoading("process");
        }
        // GETS THE MARKETS FOR SPECIFIC PROGRAMS
        function getMarkets(ob)
        {
        	displayLoading("process");
        	if (DWRUtil.getValue("study") == "Select a Study Type")
        	{
        		hideLoading("process");
        		document.getElementById("step2").style.visibility = 'hidden';
        		return false;
        	}
        	DWRUtil.removeAllOptions("markets");
        	DWRUtil.addOptions("markets",["Loading Markets....."]);        	
        	peids = "";
        	if (DWRUtil.getValue("sdate") == "")
        	{
        		alert("Please enter a FROM date.");
        		hideLoading("process");
        		return false;
        	}
        	
        	if (DWRUtil.getValue("tdate") == "")
					{
						alert("Please enter a TO date.");
						hideLoading("process");
						return false;
        	}
        	var proceed = ValidateDate(document.rpts.sdate);
					if (!proceed)
					{
						hideLoading("process");
						return false;
					}
					proceed = ValidateDate(document.rpts.tdate);
					if (!proceed)
					{
						hideLoading("process");
						return false;
        	}
        	//alert(peids);
        	//programs.getMarketsByPeid(DWRUtil.getValue("study"), peids.substring(0,peids.length-1), displayMarkets);
        	programs.getMarketsByDateAndType(DWRUtil.getValue("study"), DWRUtil.getValue("sdate"), DWRUtil.getValue("tdate"), displayMarkets)
        	document.getElementById("step2").style.visibility = 'visible';
        	//document.getElementById("step2a").style.visibility = 'visible';
        	hideLoading("process");	
        }
        // LOADS THE MARKETS SELECT BOX
        function displayMarkets(data)
        {
        	DWRUtil.removeAllOptions("markets");
        	DWRUtil.addOptions("markets",["Select one or many markets."]);
				  DWRUtil.addOptions("markets", data);				  
        }
        // GETS THE SPECIFIC PRODUCTS FOR THE USER SELECTED MARKETS
        function getProducts(ob)
        {
        	displayLoading("process");
        	var proceed = ValidateDate(document.rpts.sdate);
					if (!proceed)
					{
						hideLoading("process");
						return false;
					}
					proceed = ValidateDate(document.rpts.tdate);
					if (!proceed)
					{
						hideLoading("process");
						return false;
        	}
        	DWRUtil.removeAllOptions("products");
        	DWRUtil.addOptions("products",["Loading Products....."]);
        	mids = "";
					for(var i = 0; i < ob.options.length; ++i)
					{
						if (ob.options[i].selected)
					 	{
					 		if (ob.options[i].value != "Select one or many products.")
					 		{
					 			mids = mids+ob.options[i].value +","; 
					 		}
					  }
					}
					//programs.getProductsByPeidMarket(peids.substring(0,peids.length-1), mids.substring(0,mids.length-1), displayProducts);
					programs.getProductsByMarketAndDate(mids.substring(0,mids.length-1), DWRUtil.getValue("sdate"), DWRUtil.getValue("tdate"), DWRUtil.getValue("study"), displayProducts);
					document.getElementById("step2a").style.visibility = 'visible';
        	hideLoading("process");	
        }
        // LOADS THE PRODUCTS SELECT BOX
        function displayProducts(data)
        {
        	DWRUtil.removeAllOptions("products");
					DWRUtil.addOptions("products",["Select one or many Products."]);
				  DWRUtil.addOptions("products", data);
        }
        // GETS THE SPECIFIC OEN END QUESTIONS FOR THE PROGRAMS THE USER SELECTED
        function getQuestions(ob)
        {
        	displayLoading("process");
        	var proceed = ValidateDate(document.rpts.sdate);
					if (!proceed)
					{
						hideLoading("process");
						return false;
					}
					proceed = ValidateDate(document.rpts.tdate);
					if (!proceed)
					{
						hideLoading("process");
						return false;
        	}
        	document.getElementById("step3").style.visibility = 'visible';
        	DWRUtil.removeAllOptions("questions");
        	DWRUtil.addOptions("questions",["Loading Questions....."]);
        	productIds = "";
        	for(var i = 0; i < ob.options.length; ++i)
					{
						if (ob.options[i].selected)
					 	{
							if (ob.options[i].value != "Select one or many Products.")
							{
									productIds = productIds+ob.options[i].value +","; 
							}
					  }
					}
					if (productIds == "")
					{
						alert("Please select one or many products.");
						hideLoading("process");
						document.getElementById("step3").style.visibility = 'hidden';
						return false;
					}
        	//pq.getOecQuestions(peids.substring(0,peids.length-1),displayQuestions);
        	pq.getOecQuestionsBYMarketProduct(mids.substring(0,mids.length-1),productIds.substring(0,productIds.length-1),DWRUtil.getValue("sdate"), DWRUtil.getValue("tdate"),displayQuestions)
        	hideLoading("process");
        	//getProgramLabels();
        	getCells();
        }
        // LOADS THE QUESTION SELECT BOX
        function displayQuestions(data)
        {
        	DWRUtil.removeAllOptions("questions");
        	document.getElementById("step3").style.visibility = 'visible';
					DWRUtil.addOptions("questions",["Select one or many Questions."]);
				  DWRUtil.addOptions("questions", data);
				  document.getElementById("step3").style.visibility = 'visible';
        }
        function getCells()
        {
          displayLoading("process");
          var proceed = ValidateDate(document.rpts.sdate);
					if (!proceed)
					{
						hideLoading("process");
						return false;
					}
					proceed = ValidateDate(document.rpts.tdate);
					if (!proceed)
					{
						hideLoading("process");
						return false;
        	}
          document.getElementById("step3a").style.visibility = 'visible';
        	DWRUtil.removeAllOptions("cells");
        	DWRUtil.addOptions("cells",["Loading Cells....."]);
        	programs.getCellByProductMarket(DWRUtil.getValue("study"), mids.substring(0,mids.length-1), productIds.substring(0,productIds.length-1), DWRUtil.getValue("sdate"), DWRUtil.getValue("tdate"), displayCell);
        }
        function displayCell(data)
        {
        	DWRUtil.removeAllOptions("cells");
					DWRUtil.addOptions("cells",["Select one or many Cells."]);
					DWRUtil.addOptions("cells", data);
				  document.getElementById("step3a").style.visibility = 'visible';
				  hideLoading("process");
        }
        // HIDES SPECIFIC AREAS OF THE SCREEN DEPENDING ON WHICH PART OF 
        // THE SCREEN THE USER IS INTERACTING WITH.
        function changeScreen(item)
        {
          if ((item == "from") || (item == "to"))
					{
					  programs.getAllStudyTypes(displayStudyTypes);
						DWRUtil.removeAllOptions("markets");
						DWRUtil.removeAllOptions("questions");
						DWRUtil.removeAllOptions("products");
						DWRUtil.removeAllOptions("cells");
						DWRUtil.removeAllOptions("programs");
						document.getElementById("step2").style.visibility = 'hidden';
						document.getElementById("step2a").style.visibility = 'hidden';
						document.getElementById("step3").style.visibility = 'hidden';
						document.getElementById("step3a").style.visibility = 'hidden';
						document.getElementById("step4").style.visibility = 'hidden';        	
						document.getElementById("getreport").style.visibility = 'hidden';
        	}
        	if (item == "study")
        	{
        		DWRUtil.removeAllOptions("markets");
        		DWRUtil.removeAllOptions("questions");
        		DWRUtil.removeAllOptions("products");
        		DWRUtil.removeAllOptions("cells");
        		DWRUtil.removeAllOptions("programs");
        		document.getElementById("step2a").style.visibility = 'hidden';
        		document.getElementById("step3").style.visibility = 'hidden';
        		document.getElementById("step3a").style.visibility = 'hidden';
        		document.getElementById("step4").style.visibility = 'hidden';        	
        		document.getElementById("getreport").style.visibility = 'hidden';
        	}
        	if (item == "markets")
					{
						DWRUtil.removeAllOptions("questions");
						DWRUtil.removeAllOptions("products");
						DWRUtil.removeAllOptions("cells");
						DWRUtil.removeAllOptions("programs");
						document.getElementById("step2a").style.visibility = 'hidden';
						document.getElementById("step3").style.visibility = 'hidden';
						document.getElementById("step3a").style.visibility = 'hidden';
						document.getElementById("step4").style.visibility = 'hidden';   
						document.getElementById("getreport").style.visibility = 'hidden';
        	}
        	if (item == "products")
					{
						DWRUtil.removeAllOptions("questions");
						DWRUtil.removeAllOptions("cells");
						document.getElementById("step3").style.visibility = 'hidden';	
						document.getElementById("step3a").style.visibility = 'hidden';
						document.getElementById("step4").style.visibility = 'hidden';  
						document.getElementById("getreport").style.visibility = 'hidden';
        	}
        	if ((item == "questions") || (item == "cells"))
					{
						DWRUtil.removeAllOptions("programs");
						document.getElementById("step4").style.visibility = 'hidden'; 
						document.getElementById("getreport").style.visibility = 'hidden';
					}
        }
        
        function getReport()
        {
        	displayLoading("process");
        	var proceed = ValidateDate(document.rpts.sdate);
        	if (!proceed)
        	{
        		hideLoading("process");
        		return false;
        	}
        	proceed = ValidateDate(document.rpts.tdate);
        	if (!proceed)
					{
						hideLoading("process");
						return false;
        	}
					productIds = "";
					var ob = document.rpts.products
					for(var i = 0; i < ob.options.length; ++i)
					{
						if (ob.options[i].selected)
					 	{
					 		if (ob.options[i].value != "Select one or many Products.")
					 		{
								productIds = productIds+ob.options[i].value +","; 
							}
						}
					}
					qids = "";
					var obj = document.rpts.questions
					for(var i = 0; i < obj.options.length; ++i)
					{
						if (obj.options[i].selected)
					 	{
					 		if (obj.options[i].value != "Select one or many Questions.")
					 		{
								qids = qids+obj.options[i].value +","; 
							}
						}
					}
					var obj1 = document.rpts.programs
					peids = "";
					for(var i = 0; i < obj1.options.length; ++i)
					{
						if (obj1.options[i].selected)
						{
					 		if (obj1.options[i].value != "Select one or many Program Labels.")
					 		{
								peids = peids+obj1.options[i].value +","; 
							}
						}
					}
					//alert(peids.substring(0,peids.length-1));
					//responses.getDynamicReport(peids.substring(0,peids.length-1), mids.substring(0,mids.length-1), productIds.substring(0,productIds.length-1), qids.substring(0,qids.length-1),displayReport);
					openWindowT('/oec/jsp/export.jsp?sDate='+DWRUtil.getValue("sdate")+'&cells='+cells.substring(0,cells.length-1)+'&peids='+peids.substring(0,peids.length-1)+'&mids='+mids.substring(0,mids.length-1)+'&pids='+productIds.substring(0,productIds.length-1)+'&qids='+qids.substring(0,qids.length-1)+'&caller=dynamic');
					hideLoading("process");
        }
        function openWindowT(url_string, url_title)
        {
        	//alert(url_string);
					WindowObjectReference = window.open(url_string, url_title,"width=600,height=300,menubar=no,location=yes,resizable=yes,scrollbars=yes,status=yes");
        }
        function displayReport(data)
        {
        	alert(data);
        }
        // DISPLAY LOADING IMAGE AND TEXT
        function displayLoading(value)
        {
        	document.getElementById("process").innerHTML = "<font class='load'>Getting Data <img src='/oec/images/progressBar.gif' border='0'/></font>";
        }
        // HIDE LOADING IMAGE AND TEXT
        function hideLoading(value)
        {
        	document.getElementById("process").innerHTML = "";
        }
    </script>
    <% if ( u.getRoleCode() == null) 
       {
    %>
        <body>
        You are not registered to utilize this tool. Please contact Mark Snyder at Extension 8853
    <% } else { %>
    <body style="margin:0px 5px;" onload="buildPage()">
    <table width="800px" class="main">
    <tr>
		    <td colspan="2">
		        <jsp:include page="top_nav.jsp"/>
		    </td>
		</tr>
    <tr height="15px"><td id="process"></td></tr>
    <tr>
    	<td colspan="2">
    	<div id="bldg">
    	<jsp:include page="reports.jsp" />
    	</div>
    	</td>
    </tr>
    <tr>
        <td>
	 			<div id="display"></div>
	  </td>
    </tr>
    </table>
		<% } %>
    </body>
<% } %>
</html>
