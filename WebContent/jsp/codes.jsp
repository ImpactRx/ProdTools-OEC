<!doctype html>
<html>
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
    <head>
		<meta content="IE=edge" http-equiv="X-UA-Compatible">
		<meta content="text/html; charset=UTF-8" http-equiv="content-type">

        <title>Open Ended Coding Tool</title>
        <link rel="stylesheet" type="text/css" href="/shared/css/layout.css">
        <link rel="stylesheet" type="text/css" href="/shared/css/oec.css">
        <link rel="stylesheet" type="text/css" href="/oec/css/<%= u.getRoleCode()%>.css">

        <script type='text/javascript' src='/oec/dwr/engine.js'></script>
        <script type='text/javascript' src='/oec/javascript/event.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codebooks.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codes.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/nets.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/responses.js'></script>
		<script type='text/javascript' src='/oec/dwr/util.js'></script>

        <script language="Javascript">
    		var spinner = '<div id="spinner" align="center"><img src="/oec/images/spinner-32x32-anim.gif"/></div>';
	    	var markButton = '<a href="#" class="buttonU" id="mark" onClick="markForReview();"/>Mark Response For Review</a>';
	    	var updateButtons = '<a href="#" class="buttonU" id="skip" onClick="saveSkipTag();"/>Skip &amp; Tag</a>';
	    	updateButtons += '<a href="#" class="buttonU" id="save" onClick="saveResp();"/>Save</a>';
	    	updateButtons += '<a href="#" class="respbutton" id="resetResp" onClick="resetResp();"/>Reset</a>';
	    	var responseUnderReview = 'Response is under review';
            var current_value = "";
            var my_responses;
            var codeData;
            nn=(document.layers)?true:false; ie=(document.all)?true:false; 
            var act_bs = "false";
        	
			function showSpinner()
        	{
        		document.getElementById("reviewResponse").innerHTML = spinner;
        	}
        	
			function hideSpinner()
        	{
        		document.getElementById("reviewResponse").innerHTML = "";
	       	}
        	function showMarkButton()
        	{
        		document.getElementById("reviewResponse").innerHTML = markButton;
        	}
        	function hideMarkButton()
        	{
        		document.getElementById("reviewResponse").innerHTML = "";
        	}
			function showReviewResult(result)
			{
				document.getElementById("reviewResult").innerHTML = result;
			}
			function hideReviewResult()
			{
        		document.getElementById("reviewResult").innerHTML = "";
			}
			function showUpdateButtons(statusCode)
			{
                if (statusCode != "review")
                {
            		document.getElementById("updateResponse").innerHTML = updateButtons;
                } else
                {
            		document.getElementById("updateResponse").innerHTML = responseUnderReview;
                }
			}
			function hideUpdateButtons()
			{
        		document.getElementById("updateResponse").innerHTML = "";
			}
            function keyDown(e) 
            { 
                var evt=(e)?e:(window.event)?window.event:null; 
                if(evt)
                { 
                    var key=(evt.charCode)?evt.charCode: ((evt.keyCode)?evt.keyCode:((evt.which)?evt.which:0)); 
                    if(key=="8")
                    {
                        //alert(act_bs);
                        if (act_bs == "false")
                        {
                            return false;
                        }
                    }
                } 
            } 
            document.onkeydown=keyDown; 
            if(nn) document.captureEvents(Event.KEYDOWN);
            // FUNCTION EXECUTES EVERYTIME THE PAGE IS REFRESHED
            function buildPage()
            {
                addEvents('keyup');
                document.getElementById('loadingMsg').innerHTML = "<font class='load'>Loading <img src='/oec/images/progressBar.gif' border='0'/></font>";
                document.getElementById('deleteNet').style.visibility = 'hidden';
                document.getElementById('editNet').style.visibility = 'hidden';
                document.getElementById('responseCodes').style.visibility = 'hidden';
                codebooks.getCodeBookName(<%= cbId %>,buildCodeBook);
//                codes.getMappedCodes(<%= cbId %>,buildCodes);   
				getCodesByDate();
                document.getElementById('oecResp').style.visibility = 'hidden';
            }
            
            function buildCodeBook(data)
            {
                for (var i = 0; i < data.length; i++)
				{
                    var result = data[i];
                    DWRUtil.setValue("bookName","Code Book Name: "+result.codeBookName);	 	
                }
            }

            function buildCodes(data)
            {
			   var options2 = [];
			   data = getSortedKeys(data);
               codeData = data;
			   for (var i=0; i<data.length; i++) {
				   options2.push("<option value='"+ data[i].num + "'>" + "CN: " + data[i].num + " : " + data[i].name +"</option>");
				}

			   document.getElementById('codeList').innerHTML = options2.join('');
               document.getElementById('loadingMsg').innerHTML = "";
            }


			function getSortedKeys(data) {

				var re = /^CN: (-?\d+) : (.*)$/;
				var uk = "";
				var slist = [];
				for(var prop in data) {
					var parts = re.exec(data[prop]);
					if (parts!=null) {
						var obj = {
							id: prop,
							num: parts[1],
							name: parts[2]
							};

						slist.push(obj);
					} else {
						uk += data[prop]+"\n";
					}
				}
	
				if (uk != "") alert("Unknown:\n"+uk);
				slist.sort(compare);
				return slist;
			}

			function compare(a,b) {
			  if (a.name < b.name)
			     return -1;
			  if (a.name > b.name)
			    return 1;
			  return 0;
			}



            // GETS THE NET CODES AND THEN THEN REPORT LABEL AND HINT CODE
            function getNetCodes(form)
            {
                // RESET THE NET VALUES TO NULL
                DWRUtil.setValue("net1"," ");	 	
                DWRUtil.setValue("net2"," ");
                DWRUtil.setValue("net3"," ");

				document.getElementById('hintCode').rows[1].innerHTML="";
				document.getElementById('reportLabel').rows[1].innerHTML="";

                document.getElementById('netTable').deleteRow(1);
                var w=document.getElementById('netTable').insertRow(1);
                var x=w.insertCell(0);
                var y=w.insertCell(1);
                var z=w.insertCell(2);
                x.innerHTML="";
                y.innerHTML="";
                z.innerHTML="";

                if (DWRUtil.getValue("codeList") != "")
                {
                    //var pattern = /CN:\s(\d+)\s:/;
                    var textString = document.forms.codes.codeList1[document.forms.codes.codeList1.selectedIndex].text;
					var parts = textString.split(":")
                    //var result = textString.match(pattern);
                    if (parts.length > 1)
                    {
                        //codeNum = result[1]; // [0] stores the entire matched string; [1] stores the first () value
						codeNum	= parts[1];
                    }

                	nets.getNetsByCode(<%= cbId %>, codeNum, buildNets);

                    codes.getCodeMain(form.codeList1.value, <%= cbId %>, buildCodeInfo);
                }
            }

            // BUILDS THE CODE REPOT LABEL AND HINT CODE TABLE
            function buildCodeInfo(data)
            {
                 for (var i = 0; i < data.length; i++)	{
                    document.getElementById('hintCode').deleteRow(1);
                    document.getElementById('reportLabel').deleteRow(1);
                    result = data[i];
                    var w=document.getElementById('hintCode').insertRow(1);
                    var x=w.insertCell(0);
                    if (result.codeHint != null)
                    {
                        x.innerHTML=result.codeHint; 
                    }
                    var a=document.getElementById('reportLabel').insertRow(1);
                    var b=a.insertCell(0);
                    if (result.codeReport != null)
                    {
                        b.innerHTML=result.codeReport; 
                    }
                }
            }

            // BUILDS THE NET1, NET2, AND NET3 TABLE
            function buildNets(data)
            {
                var result;
                for (var i = 0; i < data.length; i++)
                {
                    document.getElementById('netTable').deleteRow(1);
                    result = data[i];
                    DWRUtil.setValue("net1",result.net1Id);	 	
                    DWRUtil.setValue("net2",result.net2Id);
                    DWRUtil.setValue("net3",result.net3Id);
                    var w=document.getElementById('netTable').insertRow(1);
                    var x=w.insertCell(0);
                    var y=w.insertCell(1);
                    var z=w.insertCell(2);
                    x.innerHTML=result.net1label;
                    y.innerHTML=result.net2label;
                    z.innerHTML=result.net3label;
                }
                <% if (! u.getRoleCode().equalsIgnoreCase("OEC_USER")) { %>
                document.getElementById('deleteNet').style.visibility = 'visible';
                document.getElementById('editNet').style.visibility = 'visible';
                <% } %>
            }

            // HANDLES THE EDIT OF THE NETS
            function openEdit(form1,form2,form3)
            {
                var codeNum;
                var temp1 = DWRUtil.getValue("net1");
                var temp2 = DWRUtil.getValue("net2");
                if (temp1.length == 0) { temp1 = "0"; }
                var pattern = /CN:\s(\d+)\s:/;
                var textString = document.forms.codes.codeList1[document.forms.codes.codeList1.selectedIndex].text;
                var result = textString.match(pattern);
                if (result != null)
                {
                    codeNum = result[1]; // [0] stores the entire matched string; [1] stores the first () value
                }
                // easy fix to previous mistake of passing in code id but looking for codenum; codenum is passed in even though the parameter name is code id
                var url_string = "net.jsp?codebookId="+DWRUtil.getValue("codebookid")+"&netId="+temp1+"&codeId="+codeNum+"&net2Id="+temp2;
                openWin(url_string,"Edit");
            }

            // OPENS A NEW WINDOW TO A LOCATION PASSED IN
            function openWin(url_string, url_title)
            {
                var WindowObjectReference;
                if (url_title == "response")
                {
                    WindowObjectReference = window.open(url_string, url_title,"width=700,height=600,menubar=no,location=yes,resizable=no,scrollbars=yes,status=yes");
                } else if (url_title == "Tag")
                {
                    WindowObjectReference = window.open(url_string, url_title,"width=500,height=100,menubar=yes,location=no,resizable=yes,scrollbars=yes,status=yes");
                } else {
                    WindowObjectReference = window.open(url_string, url_title,"width=650,height=400,menubar=yes,location=no,resizable=yes,scrollbars=yes,status=yes");
                }
            }

            function addNewCode(formPage)
            {
                var url_string = "new_code.jsp?codebookId="+DWRUtil.getValue("codebookid");
                openWin(url_string,"Add");
            }

            function addCodes(formPage)
            {
                var url_string = "add_code.jsp?codebookId="+DWRUtil.getValue("codebookid");
                openWin(url_string,"Add");
            }

            //******************************************************************************
            // THIS HANDLES THE CODY SUGGEST PORTION OF THE PAGE. USER CAN TYPE IN WHAT HE
            // OR SHE WISHES TO SEATCH FOR. AFTER A SET TIME OF NO TIME THE EVENT WILL
            // FIRE OFF TO GET THE RESULTS.
            //******************************************************************************
            var stack = 0;
            function lookUp(form,formc,e)
            {
                var KeyID = (window.event) ? event.keyCode : e.keyCode;
                if (KeyID == "113") // THIS IS THE F2 KEY
                {
                    form.codeList.focus();
                }
                if (current_value != form.search.value) 
                {
                    current_value = form.search.value;
                    stack++;
									  setTimeout("doer()", 800);										
                }
            }
            function doer()
						{
							if(stack == 1)
							{
								var search_rad = DWRUtil.getValue("sradio");
								var search_where = DWRUtil.getValue("filter");
								var codebookId = DWRUtil.getValue("codebookid");
								var date = DWRUtil.getValue("dates");
								var isSelected = DWRUtil.getValue("csubnet");
								if (isSelected.length>0)
								{
									var subnetId = DWRUtil.getValue("subnets");
									codes.searchMappedCodesBySubnet(codebookId, subnetId, search_rad, search_where, document.codes.search.value, buildCodes);
								} else
								{
									codes.searchMappedCodesByDate(codebookId, date, search_rad, search_where, document.codes.search.value, buildCodes);
								}
						  }
							stack--;
						}
            //******************************************************************************
						// END OF CODY SUGGEST
						//******************************************************************************
//            function deleteCodes(form)
//            {
//                alert(DWRUtil.getValue("codeList")+" "+DWRUtil.getValue("codebookid"));
//                codes.deleteCode(DWRUtil.getValue("codebookid"),DWRUtil.getValue("codeList"),displayMessage("codeMsg"));
//            }
//            function displayMessage(value)
//            {
//                window.location.reload();
//                document.getElementById('message').innerHTML = "Code Deleted <br />";
//            }
            function editCodes()
            {
                var code = DWRUtil.getValue("codeList");
                if (code == "")
                {
                    alert("Please select a Code to edit.");
                    return false;
                } else 
                {
                    openWin("edit_code.jsp?codeId="+code+"&cbId="+DWRUtil.getValue("codebookid"),"Edit");
                }
            }
            function getCodeObj()
            {
                 alert(DWRUtil.getValue(codeList));
            }
            function popResponse(e)
            {
                getKey(e);
            }
            function getKey(e)
            {
                var KeyID = (window.event) ? event.keyCode : e.keyCode;
                var value = "You must select a key from 1 to 6";
                switch(KeyID)
                {
                    case 8:
                        return false;
                    case 9:
                        value = "";
                        break;
                    case 65: //(A)
                        e.cancelBubble = true;
                        e.returnValue = false;
                        value = "";
                        var idx = document.codes.codeList1.selectedIndex+1;
                        document.codes.codeList1.selectedIndex = idx;
                        getNetCodes(document.codes);
                        break;     
                    case 90: //(Z)
                        e.cancelBubble = true;
                        e.returnValue = false;
                        value = "";
                        var idx = document.codes.codeList1.selectedIndex;
                        if (idx != 0)
                        {
                            document.codes.codeList1.selectedIndex = idx-1;
                        }                        
                        getNetCodes(document.codes);
                        break; 
                    case 38: // (ALT)
                        e.cancelBubble = true;
                        e.returnValue = false;
                        value = "";
                        break;
                    case 35: // (END NUM)
                        e.cancelBubble = true;
                        e.returnValue = false;
                        value = "1";
                        DWRUtil.setValue("target1",DWRUtil.getValue("codeList"));
                        document.codes.codeList.focus();
                        break;
                    case 49: 
                        e.cancelBubble = true;
                        e.returnValue = false;
                        value = "1";
                        DWRUtil.setValue("target1",DWRUtil.getValue("codeList"));
                        document.codes.codeList.focus();
                        break;
                    case 40: // (DOWN ARROW NUM)
                        e.cancelBubble = true;
                        e.returnValue = false;
                        value = "2";
                        DWRUtil.setValue("target2",DWRUtil.getValue("codeList"));
                        document.codes.codeList.focus();
                        break;
                    case 50:
                        e.cancelBubble = true;
                        e.returnValue = false;
                        value = "2";
                        DWRUtil.setValue("target2",DWRUtil.getValue("codeList"));
                        document.codes.codeList.focus();
                        break;
                    case 34: // (PG DN NUM)
                        e.cancelBubble = true;
                        e.returnValue = false;
                        value = "3";
                        DWRUtil.setValue("target3",DWRUtil.getValue("codeList"));
                        document.codes.codeList.focus();
                        break;
                     case 51: 
                        e.cancelBubble = true;
                        e.returnValue = false;
                        value = "3";
                        DWRUtil.setValue("target3",DWRUtil.getValue("codeList"));
                        document.codes.codeList.focus();
                        break;
                    case 37: // (LEFT ARROW NUM)
                        e.cancelBubble = true;
                        e.returnValue = false;
                        value = "4";
                        DWRUtil.setValue("target4",DWRUtil.getValue("codeList"));
                        document.codes.codeList.focus();
                        break;
                    case 52:
                        e.cancelBubble = true;
                        e.returnValue = false;
                        value = "4";
                        DWRUtil.setValue("target4",DWRUtil.getValue("codeList"));
                        document.codes.codeList.focus();
                        break;
                    case 12: // (5 NUM)
                        e.cancelBubble = true;
                        e.returnValue = false;
                        value = "5";
                        DWRUtil.setValue("target5",DWRUtil.getValue("codeList"));
                        document.codes.codeList.focus();
                        break;
                    case 53:
                        e.cancelBubble = true;
                        e.returnValue = false;
                        value = "5";
                        DWRUtil.setValue("target5",DWRUtil.getValue("codeList"));
                        document.codes.codeList.focus();
                        break;
                    case 39: // (RIGHT ARROW NUM)
                        e.cancelBubble = true;
                        e.returnValue = false;
                        value = "6";
                        DWRUtil.setValue("target6",DWRUtil.getValue("codeList"));                       
                        document.codes.codeList.focus();
                        break;
                    case 54:
                        e.cancelBubble = true;
                        e.returnValue = false;
                        value = "6";
                        DWRUtil.setValue("target6",DWRUtil.getValue("codeList"));                       
                        document.codes.codeList.focus();
                        break;
                    case 18:
                        value = "";
                        break;
                    case 36:
                        value = "";
                        break;
                    case 192:
                        value = "";
                        break;
                    case 103:
                        value = "";
                        break;
                    case 104:
                        value = "";
                        break;
                    case 105:
                        value = "";
                        break;
                    case 113:
                        value = "";
                        break;
                    case 144:
                        value = "";
                        break;
                }
                if (value == "You must select a key from 1 to 6")
                {
                    alert(value);
                }
                return value;
            }
            function startDrag(info) {
                window.event.dataTransfer.setData("Text", DWRUtil.getValue("codeList"));
                window.event.dataTransfer.effectAllowed = "copy";                 
            }

            function drop(form) {
              window.event.returnValue = false;                           
              window.event.dataTransfer.dropEffect = "copy";
              form.value = window.event.dataTransfer.getData("Text");
              //DWRUtil.setValue("")
            }

            function showDragCursor() {
              window.event.returnValue = false;
              window.event.dataTransfer.dropEffect = "copy";
            }
            function showResponse(form)
            {
                
                if (form.showResp.checked)
                {   
                    responses.getCheckedOutResponses("<%= u.getUserName() %>",displayResponse);
                    document.getElementById('responseCodes').style.visibility = 'visible';
                    document.getElementById('oecResp').style.visibility = 'visible';
                } else 
                {
                    document.getElementById('responseCodes').style.visibility = 'hidden';
                    document.getElementById('oecResp').style.visibility = 'hidden';
                    DWRUtil.setValue("responseString","");
                    DWRUtil.setValue("par1","");
                    DWRUtil.setValue("par2","");
                    DWRUtil.setValue("par3","");
                    DWRUtil.setValue("par4","");
                    DWRUtil.setValue("targetrxid","");
                    DWRUtil.setValue("status","");
                    DWRUtil.setValue("tag","");
                    DWRUtil.setValue("target1","");
                    DWRUtil.setValue("target2","");
                    DWRUtil.setValue("target3","");
                    DWRUtil.setValue("target4","");
                    DWRUtil.setValue("target5","");
                    DWRUtil.setValue("target6","");
                    DWRUtil.setValue("respPosition","");
                    DWRUtil.setValue("goto","");
                }
                
            }
            function displayResponse(data)
            {
                my_responses = data; // store Response object data in my_responses
                var result;
                for (var i=0; i<data.length; i++)
                {
                    if (i==0)
                    {
                        result = data[i];
                        setValues(result, 0);
                        DWRUtil.setValue("respPosition","0");
                    }
                }
                if (document.codes.showResp.checked)
                { 
                    // ONLY DO THIS CHECK IF THE USER HAS ANY RESPONSES CHECKED OUT.
                    if (data.length > 0)
                    {
                        var resp = data[1];
                        if (resp.surveyQuestionLabel != null)
                        {
                            responses.checkCodebookQues(<%= cbId %>,resp.surveyQuestionLabel,dataCheck);
                        }
                    }
                }
            }
            function dataCheck(data)
            {
                // IF THERE ARE NO RESPONSES WE NEED TO MAKE SURE THAT THERE IS AN ASSOC. BETWEEN GROUP LABEL
                // AND CODE BOOK
                if (data == 0)
                {
                    alert("This Survey Question Label is not associated to this Code Book.\nPlease contact the Adminstrator");
                    document.codes.showResp.checked = false;
                    showResponse(document.codes);
                } 
            }
            function nextResp()
            {
                var cnt = parseInt(DWRUtil.getValue("respPosition"))+1;
                if (cnt <= my_responses.length-1)
                {
                    DWRUtil.setValue("respPosition",cnt);
                    var result = my_responses[cnt];
                    setValues(result, cnt);
                }
            }
            function prevResp()
            {
                
                var cnt = parseInt(DWRUtil.getValue("respPosition"))-1;
                if (cnt >= 0)
                {
                    DWRUtil.setValue("respPosition",cnt);
                    var result = my_responses[cnt];
                    setValues(result, cnt);
                }
            }
            function setValues(result, cnt)
            {
            	hideReviewResult();
                DWRUtil.setValue("responseString",result.responseOrigStr);
                DWRUtil.setValue("par1",result.p1Value);
                DWRUtil.setValue("par2",result.p2Value);
                DWRUtil.setValue("par3",result.p3Value);
                DWRUtil.setValue("par4",result.p4Value);
                DWRUtil.setValue("targetrxid",result.targetrxId);
                DWRUtil.setValue("status",result.statusCode);
                DWRUtil.setValue("tag",result.tagTypeCode);
                DWRUtil.setValue("target1",result.code1);
                DWRUtil.setValue("target2",result.code2);
                DWRUtil.setValue("target3",result.code3);
                DWRUtil.setValue("target4",result.code4);
                DWRUtil.setValue("target5",result.code5);
                DWRUtil.setValue("target6",result.code6);
                DWRUtil.setValue("goto","");
                document.getElementById('questionid').innerHTML = "<b>Question Label:</b> <u>"+result.groupQuestionLabel+"</u>&nbsp;&nbsp; <b>Survey Label:</b> <u>"+result.surveyQuestionLabel+"</u>";
                if (cnt == 0)
                {
                  document.getElementById('counter').innerHTML = (cnt+1) + " of " + my_responses.length+" <a href='#' onClick='nextResp()'>&gt;&gt;</a>";
                } else if (my_responses.length == (cnt+1))
                {
                  document.getElementById('counter').innerHTML = "<a href='#' onClick='prevResp()'>&lt;&lt;</a> "+(cnt+1)+" of "+my_responses.length;
                } else
                {
                  document.getElementById('counter').innerHTML = "<a href='#' onClick='prevResp()'>&lt;&lt;</a> "+(cnt+1)+" of "+my_responses.length+" <a href='#' onClick='nextResp()'>&gt;&gt;</a>";
                }
                showUpdateButtons(result.statusCode);
                showMarkButton();
                if (result.statusCode == "review")
                {
                    hideMarkButton();
                }
            }
            function saveResp()
            {
                var cnt = DWRUtil.getValue("respPosition");
                var result = my_responses[cnt];
                if (!IsNumeric(DWRUtil.getValue("target1")) || !IsNumeric(DWRUtil.getValue("target2")) || !IsNumeric(DWRUtil.getValue("target3")) || !IsNumeric(DWRUtil.getValue("target4")) || !IsNumeric(DWRUtil.getValue("target5")) || !IsNumeric(DWRUtil.getValue("target6")))
                {
                    alert("Codes can only contain these values [ -0123456789 ]");
                    return false;
                }
                
                result.code1 = DWRUtil.getValue("target1");
                result.code2 = DWRUtil.getValue("target2");
                result.code3 = DWRUtil.getValue("target3");
                result.code4 = DWRUtil.getValue("target4");
                result.code5 = DWRUtil.getValue("target5");
                result.code6 = DWRUtil.getValue("target6");
                result.tagTypeCode = DWRUtil.getValue("tagtypecode");
                if (result.statusCode != "review")
                {
                    result.statusCode = "cu"
                }
                my_responses[cnt] = result;
                responses.setCodedUnverified(result.responseExtractId,DWRUtil.getValue("target1"),DWRUtil.getValue("target2"),DWRUtil.getValue("target3"),DWRUtil.getValue("target4"),DWRUtil.getValue("target5"),DWRUtil.getValue("target6"),"<%= u.getUserName() %>");
                document.codes.target1.focus();
                nextResp();
            }
            // CHECK TO MAKE SURE VALUE CONTAINS ONLY NUMBERS AND NEGATIVE SYMBOL
            function IsNumeric(sText)
            {
               var ValidChars = "-0123456789";
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
              var nu_array = sText.split("-");
              if (nu_array.length > 2)
              {
                alert("Please enter only numbers.");
                IsNumber = false;
              }
              
              return IsNumber;
            }

            function resetResp()
            {
                DWRUtil.setValue("target1","");
                DWRUtil.setValue("target2","");
                DWRUtil.setValue("target3","");
                DWRUtil.setValue("target4","");
                DWRUtil.setValue("target5","");
                DWRUtil.setValue("target6","");
                document.codes.target1.focus();
            }
            function openResponse(user)
            {
                document.codes.showResp.checked = false;
                showResponse(document.codes);
                var url_string = "checkout.jsp?user="+user;
                openWin(url_string,"response");
            }
            function skipTagResp()
            {
                if ((DWRUtil.getValue("tagtypecode") == "") || (DWRUtil.getValue("tagtypecode") == "Please select a Tag Type Code."))
                {
                    alert("You must select a Tag Type Code");
                    return false;
                }
                var cnt = DWRUtil.getValue("respPosition");
                var result = my_responses[cnt];
                responses.setTagTypeCode(DWRUtil.getValue("tagtypecode"),"<%= u.getUserName() %>",result.responseExtractId,DWRUtil.getValue("target1"),DWRUtil.getValue("target2"),DWRUtil.getValue("target3"),DWRUtil.getValue("target4"),DWRUtil.getValue("target5"),DWRUtil.getValue("target6"));
                result.tagTypeCode = DWRUtil.getValue("tagtypecode");
                my_responses[cnt] = result;
                DWRUtil.setValue("tagtypecode","");
                document.codes.target1.focus();
                nextResp();
            }  
            function saveSkipTag()
            {
                openWin("type_code.jsp","Tag");
            }
            /*
             * 
             */
            function markForReview()
            {
                hideMarkButton();
                hideUpdateButtons();
                showSpinner();
                var cnt = DWRUtil.getValue("respPosition");
                var responseExtractId = my_responses[cnt].responseExtractId;
                responses.findResponseReview(responseExtractId, '<%= u.getUserName() %>', displayReviewResults);
            }
            /*
             * 
             */
            function displayReviewResults(data)
            {
                var cnt = DWRUtil.getValue("respPosition");
                var resultTable = '<table border="1" align="center" style="font-size: 12px;">';
                var addedUser;
                var reviewResult;
                var updatedUser;
				var response = data.response;
				var reviewRow = data.reviewRow;
				if (response != null)
				{
					addedUser = response.addedUser;
					reviewResult = response.reviewResult;
					updatedUser = response.updatedUser;
					if (reviewResult != "Valid")
					{
						my_responses[cnt].statusCode = 'review';
					}
				} else
				{
					addedUser = reviewRow.addedUser;
					reviewResult = reviewRow.reviewResult;
					updatedUser = reviewRow.reviewedUser;
				}
				if (updatedUser == null)
				{
					updatedUser = "Not Reviewed";
				}
				resultTable += "<tr><th>Added User</th><th>Result</th><th>Reviewed User</th></tr>";
				resultTable += "<tr><td>"+addedUser+"</td><td>"+reviewResult+"</td><td>"+updatedUser+"</td></tr>";
				resultTable += "</table>";
				hideSpinner();
				showUpdateButtons(my_responses[cnt].statusCode);
				showReviewResult(resultTable);
            }
            function goTo()
            {
                if (DWRUtil.getValue("goto") == "")
                {
                    alert("Please enter a value for Goto.");
                    return false;
                }
                if (parseInt(DWRUtil.getValue("goto")) < 0)
                {
                    alert("You cannot enter a number less then zero.");
                    return false;
                }
                if (parseInt(DWRUtil.getValue("goto")) >= my_responses.length)
                {
                    alert("You must enter a number between 0 and "+  (my_responses.length-1))
                    return false;
                }
                var cnt = parseInt(DWRUtil.getValue("goto"));
                DWRUtil.setValue("respPosition",cnt);
                var result = my_responses[cnt];
                setValues(result, cnt);
            }
            function checkIn()
            {
                responses.checkIn("<%= u.getUserName() %>");
                DWRUtil.setValue("responseString","");
                DWRUtil.setValue("par1","");
                DWRUtil.setValue("par2","");
                DWRUtil.setValue("par3","");
                DWRUtil.setValue("par4","");
                DWRUtil.setValue("targetrxid","");
                DWRUtil.setValue("status","");
                DWRUtil.setValue("tag","");
                DWRUtil.setValue("target1","");
                DWRUtil.setValue("target2","");
                DWRUtil.setValue("target3","");
                DWRUtil.setValue("target4","");
                DWRUtil.setValue("target5","");
                DWRUtil.setValue("target6","");
                DWRUtil.setValue("goto","");
                document.getElementById('questionid').innerHTML = "";
                document.getElementById('counter').innerHTML = "";
                document.getElementById("updateResponse").innerHTML = "";
                document.getElementById("reviewResponse").innerHTML = "";
                document.getElementById("reviewResult").innerHTML = "";
            }
            function checkKey()
            {
                var KeyID = (window.event) ? event.keyCode : e.keyCode;
                if ((KeyID == "35") || (KeyID == "40")|| (KeyID == "34")|| (KeyID == "37")|| (KeyID == "12")|| (KeyID == "39") || (KeyID == "36") || (KeyID == "38") || (KeyID == "33"))
                {
                    //document.codes.codeList.focus();
                }
            }

            function showFullText()
            {
	            var textString = document.forms.codes.codeList1[document.forms.codes.codeList1.selectedIndex].text;
				var parts = textString.split(":")
                alert(parts[2]);
            }

            function getPrevResponse()
            {
                var cnt = DWRUtil.getValue("respPosition");
                var result = my_responses[cnt];
                responses.getPrevResponse(result.programEventId, result.eventId,result.targetrxId,result.questionId,result.questionSeqNo,displayPrev);
            }
            function displayPrev(data)
            {
                if (data != "null")
                {
                    alert(data);
                } else {
                    alert("No previous response found.");
                }
           }
            /**
            * Show subnet dropdown menu
            */
            var previousDateValue = "365";
            function showSubnets()
            {
				var isSelected = DWRUtil.getValue("csubnet");
                var selectedRadio = DWRUtil.getValue("sradio");
                var codebookId = DWRUtil.getValue("codebookid");
                var selectElement = document.getElementById("subnets");
                var dateElement = document.getElementById("dates");
                var selectedDateValue = DWRUtil.getValue("dates");
				if (isSelected.length>0)
				{
                   DWRUtil.setValue("dates", "-1");
                   dateElement.disabled = true;
                   nets.getSubnetsByCodebookId(<%= cbId %>, displaySubNets);
                   selectElement.style.visibility = "visible";
				} else
				{
                   document.getElementById('loadingMsg').innerHTML = "<font class='load'>Loading <img src='/oec/images/progressBar.gif' border='0'/></font>";
                   var searchType = DWRUtil.getValue("filter");
            	   dateElement.disabled = false;
            	   DWRUtil.setValue("dates", previousDateValue);
                   DWRUtil.removeAllOptions("subnets");
                   selectElement.style.visibility = "hidden";
            	   codes.searchMappedCodesByDate(codebookId, previousDateValue, selectedRadio, searchType, document.codes.search.value, buildCodes);
				}
               previousDateValue = selectedDateValue;
            }
           /**
           * Display subnets
           */
           function displaySubNets(data)
           {
               if (data.length > 0)
               {
                   DWRUtil.addOptions("subnets", data, 'net2Id', 'net2label');
                   getCodesBySubnet();
               } else
               {
                   var selectElement = document.getElementById("subnets");
                   selectElement[0] = new Option("No Subnets Are Assigned To This Codebook", -1);
               }
           }
           /**
           * Get codes by subnet
           */
           function getCodesBySubnet()
           {
               document.getElementById('loadingMsg').innerHTML = "<font class='load'>Loading <img src='/oec/images/progressBar.gif' border='0'/></font>";
               var codebookId = DWRUtil.getValue("codebookid");
               var subnetId = DWRUtil.getValue("subnets");
               var selectedRadio = DWRUtil.getValue("sradio");
               var searchType = DWRUtil.getValue("filter");
               codes.searchMappedCodesBySubnet(codebookId, subnetId, selectedRadio, searchType, document.codes.search.value, buildCodes);
           }
           /**
           * 
           */
           function getCodesByDate()
           {
               document.getElementById('loadingMsg').innerHTML = "<font class='load'>Loading <img src='/oec/images/progressBar.gif' border='0'/></font>";
               var days = DWRUtil.getValue("dates");
               var codebookId = DWRUtil.getValue("codebookid");
               var selectedRadio = DWRUtil.getValue("sradio");
               var searchType = DWRUtil.getValue("filter");
			   var isSelected = DWRUtil.getValue("csubnet");
               if (isSelected.length>0)
               {
                   var subnetId = DWRUtil.getValue("subnets");
                   codes.searchMappedCodesBySubnet(codebookId, subnetId, selectedRadio, searchType, document.codes.search.value, buildCodes);
               } else
               {
                  document.getElementById("dates").disabled = false;
                  codes.searchMappedCodesByDate(codebookId, days, selectedRadio, searchType, document.codes.search.value, buildCodes);
               }
           }
    </script>
    </head>

    <body onload="buildPage();">
    <div id="nav">
     <table>
      <tr>
       <td><jsp:include page="general_nav.jsp"/></td>
       <td><jsp:include page="code_nav.jsp"/></td>
      </tr>
     </table>
    </div>

    <div id="loading">
     <font id="loadingMsg"></font>
    </div>
    <div id="bookInfo">
     <div id="bookName"></div>
    </div>
    
    <div id="content">
        <form name="codes">
        <input type="hidden" id="net1" value=""/>
        <input type="hidden" id="net2" value=""/>
        <input type="hidden" id="net3" value=""/>
        <input id="codebookid" type="hidden" name="codebookid" value="<%= cbId %>"/> 
     <table>
      <tr>
       <td>
        <table class="wrksite">
         <tr>
          <td class="code">
           <table class="code" border="1" width="100%">
            <tr>
             <td colspan="2">
              <table border="1" width="100%">
               <tr>
                <td>
                 <table width="100%" style="font-size: 12px;">
                  <tr>
                   <td width="5%" valign="top" align="right">
                    <input type="radio" name="sradio" value="label" checked>
                   </td>
				   <td width="10%" align="left">Label</td>
                   <td width="5%" valign="top" align="right">
                    <input type="radio" name="sradio" value="num">
                   </td>
				   <td width="10%" align="left">Num</td>
				   <td width="40%" align="right">Codes added in the last: </td>
                   <td width="30%">
                    <select id="dates" name="dates" style="font-size: 12px; visibility: visible" onchange="getCodesByDate();">
                     <option value="90">Last 3 months</option>
                     <option value="180">Last 6 months</option>
                     <option value="365">Last 12 months</option>
                     <option value="-1" selected>All codes</option>
                    </select>
                   </td>
				  </tr>
                 </table>
             </td>
            </tr>
            <tr>
             <td>
              <table style="font-size: 12px;">
               <tr>
                <td width="5%" valign="top" align="left">
                 <input type="checkbox" name="csubnet" value="subnet" onclick="showSubnets()">
                </td>
			    <td width="10%" align="left">Subnet</td>
                <td width="85%" align="left">
                 <select id="subnets" name="subnets" style="font-size: 12px; visibility: hidden;" onchange="getCodesBySubnet();">
                 </select>
                </td>
               </tr>
              </table>
             </td>
            </tr>
            <tr>
             <td valign="top" colspan="2">
              <table style="font-size: 12px;">
               <tr>
                <td>
                 Search Text:
                </td>
                <td>
                 <input type="text" size="20" name="search" value="" onblur="javascript: act_bs='false';" onfocus="javascript: act_bs='true';" onkeyup="lookUp(document.codes,document.page_params,event)"/>
			    </td>
			    <td>
                 <input type="radio" name="filter" value="begins" onclick="getCodesByDate();">
                </td>
                <td>Begins</td>  
                <td>
                 <input type="radio" name="filter" value="contains" onclick="getCodesByDate();" checked>
                </td>
                <td>Contains</td>
               </tr>
              </table>
                </td>
               </tr>
              </table>
             </td>
            </tr>
            <tr>
                <td colspan="2">
                <select id="codeList" ondblclick="showFullText()" name="codeList1" multiple size="9" style="font-size: 12px; width: 140mm" onkeydown="popResponse(event)" onchange="getNetCodes(document.codes);" onDrag="getCodeObj();"></select>
                </td>
            </tr>
            <tr>
                <td width="25%">
                 <img src="/oec/images/disk.gif" onDragStart="startDrag('Paul Kukk')">
                </td>
                <td width="75%" align="center">
                 <font id="message"></font>
                 <a href="#" class="button" onClick="addNewCode(document.page_params);">Create a Code</a>
                 <a href="#" class="button"  onClick="addCodes(document.page_params);"/>Add a Code</a>
                 <a href="#" class="button"  onClick="editCodes();"/>Edit</a>
                </td>
            </tr>
           </table>
        </td>
        <td id="nets" class="net" valign="top">
         <table>
          <tr>
           <td class="title" colspan="2" align="center">Code Information Panel</td>
          </tr>
          <tr>
           <td valign="top">
            <table class="nets" id="reportLabel" width="400">
             <tr>
              <th>Report Label</th>
             </tr>
             <tr>
              <td></td>
             </tr>
            </table>
           </td>
           </tr>
           <tr>
            <td valign="top">
             <table class="nets" id="hintCode" width="400">
              <tr>
               <th>Hint Code</th>
              </tr>
              <tr>
               <td></td> 
              </tr>
             </table>
            </td>
           </tr>
           <tr>
            <td>
             <table class="nets" border="1" id="netTable" width="400">
              <tr>
               <th>Net 1</th>
               <th>Net 2</th>
               <th>Net 3</th>
              </tr>
              <tr id="netresult" name="netresult">
              </tr>
              <tr class="buttons" height="30px">
               <td colspan="3" align="center">
                <a href="#" class="button"  id="editNet" onClick="openEdit(document.codes,document.page_params,document.codes);"/>Edit</a>
                <a href="#" class="button"  id="deleteNet" onClick=""/>Delete</a>
               </td>
              </tr>
             </table>
            </td>
           </tr>
          </table>
        </td>
       </tr>
      </table>
     </td>
    </tr>
    <tr>
     <td>
      <table style="background-color: #00529B; color: #FFFFFF; font-weight: bold;" width="100%;">
       <tr>
        <td>
        <table>
        <tr>
         <td width="3%"><input type="checkbox" id="showResp" name="showResp" onclick="showResponse(document.codes)" z-index="5"/></td>
         <td width="20%"><span class="whitesmall">Show Response Books</span></td>
         <td width="77%">
          <a href="#" class="respbutton"  id="saveCode" onClick="openResponse('<%= u.getLastName() %>');"/>Checkout Responses</a>
          <a href="#" class="buttonU"  id="checkin" onClick="checkIn();"/>Check In Responses</a>
         </td>
        </tr>
        <tr>
         <td colspan="3">
          <input type="hidden" id="respPosition" value=""/>
          <input type="hidden" id="tagtypecode" value="" onchange=""/>
          <table width="100%" id="oecResp" style="background-color: #0099D8; color: #000000; font-size: 12px; font-weight: bold;">
           <tr>
            <td id="counter" width="125"></td>
            <td align="center"><b>Go To:</b> <input type="text" value="" id="goto" size="2"/>
             <a href="#" class="buttonU"  id="gotobut" onClick="goTo();"/>Go</a>
            </td>
            <td align="right" id="questionid" colspan="2"></td>
           </tr>
           <tr>
            <td align="right">TargetRx Id:</td>
            <td colspan="3">
             <input id="targetrxid" type="text" size="10" disabled> 
             Status: <input id="status" type="text" size="10" disabled> 
             Tag Code: <input id="tag" type="text" size="30" disabled>
            </td>
           </tr>
           <tr>
            <td align="right">
             <a href="#" class="buttonU"  id="pre" onClick="getPrevResponse()"/>Prev</a> Response:
            </td>
            <td colspan="3">
             <textarea cols="63" rows="2" id="responseString"></textarea>
            </td>
           </tr>
           <tr>
            <td align="right">
             Par1:</td><td colspan="3"> <input id="par1" type="text" size="10" disabled> 
             Par2: <input id="par2" type="text" size="10" disabled> 
             Par3: <input id="par3" type="text" size="10" disabled> 
             Par4: <input id="par4" type="text" size="10" disabled>
            </td>
           </tr>
           <tr height="10">
            <td colspan="4"></td>
           </tr>
           <tr>
            <td class="buttons" colspan="4" align="center">
             <table width="100%">
              <tr>
               <td width="60%" align="right">
                <div id="updateResponse">
                </div>
               </td>
               <td width="40%" align="center">
                <div id="reviewResponse">
                </div>
               </td>
              </tr>
              <tr>
               <td colspan="2">
                <div id="reviewResult"></div>
               </td>
              </tr>
             </table>
            </td>
           </tr>
          </table>
         </td>
        </tr>
       </table>
      </td>
      <td>
       <table id="responseCodes" style="visibility: hidden">
        <tr>
         <td>Code 1: <input id="target1" size="5" value="" onkeydown="checkKey();" ondragenter="showDragCursor()" ondragover="showDragCursor()" ondrop="drop(this)" onblur="javascript: act_bs='false';" onfocus="javascript: act_bs='true';"></td>
        </tr>
        <tr>
         <td>Code 2: <input id="target2" size="5" value="" onkeydown="checkKey();" ondragenter="showDragCursor()" ondragover="showDragCursor()" ondrop="drop(this)" onblur="javascript: act_bs='false';" onfocus="javascript: act_bs='true';"></td>
        </tr>
        <tr>
         <td>Code 3: <input id="target3" size="5" value="" onkeydown="checkKey();" ondragenter="showDragCursor()" ondragover="showDragCursor()" ondrop="drop(this)" onblur="javascript: act_bs='false';" onfocus="javascript: act_bs='true';"></td>
        </tr>
        <tr>
         <td>Code 4: <input id="target4" size="5" value="" onkeydown="checkKey();" ondragenter="showDragCursor()" ondragover="showDragCursor()" ondrop="drop(this)" onblur="javascript: act_bs='false';" onfocus="javascript: act_bs='true';"></td>
        </tr>
        <tr>
         <td>Code 5: <input id="target5" size="5" value="" onkeydown="checkKey();" ondragenter="showDragCursor()" ondragover="showDragCursor()" ondrop="drop(this)" onblur="javascript: act_bs='false';" onfocus="javascript: act_bs='true';"></td>
        </tr>
        <tr>
         <td>Code 6: <input id="target6" size="5" value="" onkeydown="checkKey();" ondragenter="showDragCursor()" ondragover="showDragCursor()" ondrop="drop(this)" onblur="javascript: act_bs='false';" onfocus="javascript: act_bs='true';"></td>
        </tr>
       </table>
      </td>
     </tr>
    </table>
   </td>
  </tr>
 </table>
 </form>
    <%= u.getRoleCode() %>
    </div>
    </body>
<% } %>
</html>
