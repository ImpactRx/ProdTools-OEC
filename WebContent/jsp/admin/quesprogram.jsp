<%@ page import="com.targetrx.project.oec.bo.User" %>
<%@ page import="com.targetrx.project.oec.bo.OecQuestionType" %>
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
        <title>Admin Question Setup</title>
        <link rel="stylesheet" type="text/css" href="/tools/css/oec.css">
        <link rel="stylesheet" type="text/css" href="/oec/css/<%= u.getRoleCode()%>.css">
        <link rel="stylesheet" type="text/css" href="/tools/css/subnav.css">
        <script type='text/javascript' src='/tools/javascript/common.js'></script>
        <script type='text/javascript' src='/oec/javascript/tabber.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codebooks.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/codes.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/nets.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/programs.js'></script>
        <script type='text/javascript' src='/oec/dwr/interface/pq.js'></script>
        <script type='text/javascript' src='/oec/dwr/engine.js'></script>
				<script type='text/javascript' src='/oec/dwr/util.js'></script>
        <script type="text/javascript">
            /* Optional: Temporarily hide the "tabber" class so it does not "flash"
               on the page as plain HTML. After tabber runs, the class is changed
               to "tabberlive" and it will appear. */
            document.write('<style type="text/css">.tabber{display:none;}<\/style>');
        </script>
        <script>
        function buildPage()
        {
            programs.getProgramFacts(displayProgram);
            codebooks.getAllCodeBooks(buildCodeBooks);
        }
        function displayProgram(data)
        {
            DWRUtil.addOptions("plabel",["Program Labels."]);
            DWRUtil.addOptions("plabel", data);              
        }
        function buildCodeBooks(data)
        {
            DWRUtil.addOptions("codebooks", ["Please select a Code Book"]);
            DWRUtil.addOptions("codebooks", data);
        }
        function getQuestions()
        {
            pq.getProgramOecQuestions(DWRUtil.getValue("plabel"), displayQuesGroup);
        }
        function displayQuesGroup(data)
        {
            var peid = "";
            var hold = "";
            // BUILD THE RESULTS TABLE AND DISPLAY IT ON THE WEBPAGE
            var re_table = "<table width='500' class='groupResults'>";
            re_table += "<tr>";
            re_table += "<th></th>";
            re_table += "<th>Program Id</th>";
            re_table += "<th>Survey QLabel</th>";
            re_table += "<th>Oec QType</th>";
            re_table += "</tr>";
            for (var i=0; i < data.length; i++)
            {
                var result = data[i];
                //hold = result.programEventId
                if (i == 0)
                {
                    hold = result.programEventId;
                    peid = hold;
                } else 
                {
                    if (hold != result.programEventId)
                    {
                        hold = result.programEventId;
                        peid = peid+"~"+hold;
                    }
                }
                re_table += "<tr><td><a class='buttonU' href='#' onClick=removeQues('"+result.programEventId+"','"+result.surveyQuestionLabel+"')>Delete</a></td> ";
                re_table += "<td align='center'>"+result.programEventId+"</td>";
                re_table += "<td align='center'>"+result.surveyQuestionLabel+"</td>";
                re_table += "<td align='center'>";
                re_table += "<select name='questionType'>";
                re_table += "<option value='None'>No Question Type Set";
                <%
                for (OecQuestionType type : OecQuestionType.values())
                {
                	out.println("if (\"" + type.getCode() + "\"==result.oecQuestionType)");
                	out.println("{");
					out.println("  re_table +=\"<option value='"+type.getCode()+"' selected>"+type.getCode()+"\";");
					out.println("} else {");
					out.println("  re_table +=\"<option value='"+type.getCode()+"'>"+type.getCode()+"\";");
					out.println("}");
                }
              	%>
              	re_table += "</select>";
              	re_table += "</td>";
                re_table += "</tr>";
            }   
            DWRUtil.setValue("peid",peid);
            re_table = re_table+"</table>";
            document.getElementById('groupResult').innerHTML = re_table;
            document.getElementById('groupResult').style.visibility = 'visible';
            document.getElementById('ques_add').style.visibility = 'visible';
            // SET THE DEFAULTS BACK TO AS IF THE PAGE WAS REFRESHED
            DWRUtil.setValue("studyTypeCode","");
            programs.getStudyType(DWRUtil.getValue("plabel"),displayStudyType);
            resetCloneValues();
        }
        // DISPLAYS THE STUDY TYPE CODE IN THE TEXT BOX
        function displayStudyType(data)
        {
            for (var i=0; i < data.length; i++)
            {
                var result = data[i];
                DWRUtil.setValue("studyTypeCode",result.studyTypeCode);
            }
        }

        function addSurveyQues()
        {
            if (DWRUtil.getValue("surveyQues") == "")
            {
                alert("Please enter a Survey Question.");
                return false;
            }
            pq.saveProgramOecQuestions(
                    DWRUtil.getValue("peid"),
                    DWRUtil.getValue("surveyQues"),
                    DWRUtil.getValue("studyTypeCode"),
                    DWRUtil.getValue("oecQuestionType"),
                    DWRUtil.getValue("plabel"), getQuestions);
        }        
        function removeQues(pid, qid)
        {
            var answer = confirm("Do you wish to proceed in deleting. PEID: "+pid+" Question Label: "+qid);
            if (answer)
            {
                pq.deleteProgramOecQuestion(pid,qid,getQuestions);            
            }
        }
        // GET THE QUESTIONGROUP LABELS FROM THE CODEBOOK THAT THE USER
        // SELECTED.
        function getSurveyLabels()
        {
            codebooks.getCodeBookQuestions(DWRUtil.getValue('codebooks'),displayCodeBookQues);
        }
        // DISPLAYS THE GROUPQUESTION LABELS TO THE USER
        function displayCodeBookQues(data)
        {
            var cq_table = "<table width='500' class='groupResults'><tr><th></th><th>Survey Question Label</th></tr>";
            for (var i=0; i < data.length; i++)
            {
                var result = data[i];
                cq_table = cq_table+"<tr><td><a class='buttonU' href='#' onClick=deleteSurveyLabel('"+result.codebookId+"','"+result.surveyQuestionLabel+"')>Delete</a></td>";
                cq_table = cq_table+"<td>"+result.surveyQuestionLabel+"</td></tr>";
            }
            cq_table = cq_table+"</table>";
            document.getElementById('codebookGroup').innerHTML = cq_table;
            document.getElementById('codebookGroup').style.visibility = 'visible';
            document.getElementById('cques_add').style.visibility = 'visible';
        }
        function addBookques()
        {
            if (DWRUtil.getValue('surveyQues1') == "")
            {
                alert("You must enter a Survey Question Label.");
                return false;
            }
            codebooks.saveCodebookQuestions(DWRUtil.getValue('codebooks'),DWRUtil.getValue('surveyQues1'),displaySave);
        }
        function displaySave(data)
        {
            getSurveyLabels();
        }
        function deleteSurveyLabel(cbid, gql)
        {
            codebooks.deleteCodebookQuestions(cbid,gql,displayDelete);
        }
        function displayDelete()
        {
            getSurveyLabels();
        }
        function searchForProgramsToClone()
        {
            var programLabel = DWRUtil.getValue("plabel");
            var filter = DWRUtil.getValue("cloneFilter");
            programs.getLikePrograms(programLabel, filter, displayProgramsToClone);
        }
        function displayProgramsToClone(data)
        {
        	DWRUtil.removeAllOptions("cloneProgram");
			DWRUtil.addOptions("cloneProgram", data, 'eventId', 'programLabel');
			document.getElementById("cloneProgram").style.visibility = "visible";
        }
        function cloneProgramOecQuestions()
        {
            var sourceProgramId = DWRUtil.getValue("cloneProgram");
            var targetProgramLabel = DWRUtil.getValue("plabel");
            if (document.questions.cloneProgram.options.length > 0)
            {
            	pq.cloneOecProgramQuestions(sourceProgramId, targetProgramLabel, displayAfterCloneOecQuestions);
            }
        }
        function displayAfterCloneOecQuestions(data)
        {
        	getQuestions();
        	alert("Clone is complete");
        }
        function resetCloneValues()
        {
        	DWRUtil.setValue("cloneFilter", "");
        	DWRUtil.removeAllOptions("cloneProgram");
        	document.getElementById("cloneProgram").style.visibility = "hidden";
        }
        </script>
				
    </head>
		
    <body class="admin">
    
    <div id="head">
        <center class="title">Admin Console</center>
        <jsp:include page="admin_nav.jsp"/>
    </div>
        <div class="tabber">
            
            <div class="tabbertab">
                <h2>Question Setup</h2>
                <form name="questions">  
                        <table class="groupResults">
                         <tr>
                          <th>Active Programs:</th>
                          <td><select id="plabel" name="plabel" style="width: 100mm" onchange="getQuestions();"></select></td>
                         </tr>
                        </table> 
                    <span id="ques_add">
                        <input type="hidden" id="peid" value=""/>
                        <table class="groupResults" width="250">
                            <tr><th colspan="2">Add a Survey Question to this Program</th></tr>
                            <tr>
                                <td>Survey Question Label:</td>
                                <td><input id="surveyQues" type="text" size="10" value=""/></td>
                            </tr>
                            <tr>
                                <td>Study Type Code:</td>
                                <td><input id="studyTypeCode" type="text" size="15" value="" disabled/></td>
                            </tr>
                            <tr>
                                <td>Oec Question Type:</td>
                                <td>
                                  <select name="oecQuestionType">
				                  <%
				                  for (OecQuestionType type : OecQuestionType.values())
				                  {
				                	  out.println("<option value='"+type.getCode()+"'>"+type.getCode());
				                  }
				              	  %>
				              	  </select>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2"><center><a href="#" class="buttonU" onClick="addSurveyQues()">Add</a></center></td>
                            </tr>
                        </table>
                        <table class="groupResults">
                         <tr>
                          <th colspan="2">Clone all questions from another study</th>
                         </tr>
                         <tr>
                          <td>Input Filter:</td>
                          <td>
                           <table>
                            <tr>
                             <td><input type="text" id="cloneFilter"></td>
                             <td><a href="#" class="buttonU" onClick="searchForProgramsToClone();">Search</a></td>
                            </tr>
                           </table>
                          </td>
                         </tr>
                         <tr>
                          <td>Filter Results</td>
                          <td><select name="cloneProgram" id="cloneProgram" style="visibility: hidden"></select></td>
                         </tr>
                         <tr>
                          <td colspan="2" align="center">
                           <a href="#" class="buttonU" onClick="cloneProgramOecQuestions();">Clone</a>
                          </td>
                         </tr>
                     </table>
                    </span>
                    <span id="groupResult"></span>
                </form>
                <p id="tab1"></p>
            </div>    
            
            <div class="tabbertab">
                <h2>Survey Question Label</h2>
                <span id="message1"></span>
                <span id="group_setup">Active Codebooks: <select id="codebooks" name="codebooks" style="width: 100mm" onchange="getSurveyLabels();"></select>
                    <br/>Add a Survey Question Label to Codebook. This has to be done in order to code a response.</span>
                <span id="codebookGroup"></span>
                <p id="tab2"></p>
                    <span id="cques_add" style="visibility: hidden">
                    <table class='groupResults' width="250">
                        <tr><th colspan="2">Add a Survey Question to this Book</th></tr>
                        <tr>
                            <td>Survey Question Label:</td>
                            <td><input id="surveyQues1" type="question" size="10" value=""/></td>
                        </tr>
                        <tr>
                            <td colspan="2"><center><a href="#" class="buttonU" onClick="addBookques()">Add</a></center></td>
                        </tr>
                    </table>
                    </span>
            </div>
        </div>        
    </body>
    <script>buildPage();</script>
<% } %>
</html>
