<%@ page import="com.targetrx.project.oec.bo.User" %>
<%
	User u = (User)session.getAttribute("user");
	u.setRoleCode("OEC_SUPERUSER");
	session.setAttribute("user",u);
	System.out.println("OEC_SUPERUSER");
%>
<html>
    <head>
        <title>Open Ended Code Maintenance Tool</title>
        <link rel="stylesheet" type="text/css" href="/shared/css/oec.css">
        <link rel="stylesheet" type="text/css" href="/oec/css/<%= u.getRoleCode()%>.css">
        <link rel="stylesheet" type="text/css" href="/shared/css/layout.css">
        <script type='text/javascript' src='/oec/dwr/interface/codebooks.js'></script>
        <script type='text/javascript' src='/oec/dwr/engine.js'></script>
	<script type='text/javascript' src='/oec/dwr/util.js'></script>
    </head>
    <script type="text/javascript">
        // GETS ALL THE CODE BOOKS FROM THE SERVER
        function buildCodeBooks()
        {
            codebooks.getCurrentCodeBooks(buildList);
        }

        // POPULATES THE DROPDOWN WITH TEH RESULS FROM THE SERVER
        function buildList(data)
        {
            DWRUtil.addOptions("cbook", ["Please select a Code Book"]);
            DWRUtil.addOptions("cbook", data);
        }
        // FORWARDS HE PAGE TO THE CODES PAGE WITH THE RIGHT BOOK
        function getCodeBook(form)
        {
            if (form.cbook.value != "Please select a Code Book")
            {
                window.location= "/oec/jsp/codes.jsp?codebookid="+form.cbook.value;
            } else
            {
                alert("Please select a Codebook to enter cody with.");
            }
        }
        
        //////////////////////////////////////////////////////////////////////////
        // FUNCTION THAT TAKES THE USER TO ON DEMAN REPORTS FOR OPEN ENDS
        //////////////////////////////////////////////////////////////////////////
        function codyReports(form)
        {
        	window.location= "/oec/jsp/codyReports.jsp?codebookid="+form.cbook.value;
					
				}
		function editCodebook()
		{
            var value = DWRUtil.getValue("cbook");
            if (value != "Please select a Code Book")
            {
                openWindowT('/oec/jsp/codebook.jsp?codebook_id='+value);
            } else
            {
                alert("Please select a Codebook.");
            }
		}		
        function viewBookXsl()
        {
            var value = DWRUtil.getValue("cbook");
            if (value != "Please select a Code Book")
            {
                openWindowT('/oec/jsp/export.jsp?codebookid='+value+"&caller=codebook");
            } else
            {
                alert("Please select a Codebook to view in Excel.");
            }
        }
        function openWindowT(url_string, url_title)
        {
            WindowObjectReference = window.open(url_string, url_title,"width=600,height=300,menubar=no,location=yes,resizable=yes,scrollbars=yes,status=yes");
        }
        function openWindowP(url,w,h,tb,stb,l,mb,sb,rs,x,y){
            var t=(document.layers)? ',screenX='+x+',screenY='+y: ',left='+x+',top='+y; //A LITTLE CROSS-BROWSER CODE FOR WINDOW POSITIONING
            tb=(tb)?'yes':'no'; stb=(stb)?'yes':'no'; l=(l)?'yes':'no'; mb=(mb)?'yes':'no'; sb=(sb)?'yes':'no'; rs=(rs)?'yes':'no';
            var x=window.open(url, 'newWin'+new Date().getTime(), 'scrollbars='+sb+',width='+w+',height='+h+',toolbar='+tb+',status='+stb+',menubar='+mb+',links='+l+',resizable='+rs+t);
            x.focus();
        }
        function viewCodeChangesXsl()
        {
            var cbvalue = DWRUtil.getValue("cbook");
            var mvalue = DWRUtil.getValue("month");
            var yvalue = DWRUtil.getValue("year");
            // Check to see if they have selected a codebook
            if (cbvalue == "Please select a Code Book")
            {
                alert("You must select a Codebook.");
                return false;
            }
            // Check to see if they have selected a month
            if (mvalue == "Select Month")
            {
                alert("You must select a Month");
                return false;
            }
            // Check to see if they have selected a year
            if (yvalue == "Select Year")
            {
                alert("You must select a Year");
                return false;
            }
            openWindowT('/oec/jsp/change.jsp?codebookid='+cbvalue+"&caller=codebook&month="+mvalue+"&year="+yvalue);
        }
        /**
        * Display appropriate codebooks
        */
        function showCodebooks()
        {
            var isSelected = DWRUtil.getValue("bookbox");
            DWRUtil.removeAllOptions("cbook");
            if (isSelected)
            {
            	codebooks.getAllCodeBooks(buildList);
            } else
            {
            	codebooks.getCurrentCodeBooks(buildList);
            }
        }
    </script>
    <% if ( u.getRoleCode() == null) 
       {
    %>
        <body>
        You are not registered to utilize this tool. Please contact Mark Snyder at Extension 8853
    <% } else { %>
    <body onload="buildCodeBooks();">
    
    <% if (!u.getRoleCode().equalsIgnoreCase("OEC_USER")) { %>
    <div id="nav">
    <jsp:include page="general_nav.jsp"/>
    </div>
    <% } %>
    <p></p>
    <div id="content">
    <form name="codebook">
    <table class="tier2" width="800" align="center">
        <tr>
         <th class="oecLabel" colspan="2">Code Books</th>
        </tr>
        <tr>
            <td align="left" valign="top">
             CodeBook: <select id="cbook" ></select><br/>
             Show All <input type="checkbox" name="bookbox" id="bookbox" onclick="showCodebooks();">
            </td>
            <td align="center" nowrap>
                <a href="#" class="button"  id="enter" onClick="getCodeBook(document.codebook);"/>Enter Cody</a>&nbsp; 
                <a href="#" class="buttonU"  id="view1" onClick="codyReports(document.codebook);"/>COAT</a>&nbsp;
                <a href="#" class="buttonU"  id="view" onClick="viewBookXsl();"/>View Codebook</a>
                <a href="#" class="buttonU" id="edit" onClick="editCodebook();"/>Edit Codebook</a><br />
        </td></tr>
        <tr><td colspan="2">
            <p>
            <table><tr>
                <td><b>To view changes in a CodeBook please select a CodeBook you wish to see the changes
                in and then select a month and year. Once those items have been selected click the 
                &quot;View Changes&quot; button.</b></td>
                </tr><tr>
                <td align="center">
                    Month: <select id="month">
                    <option value="">Select Month
                    <option value="1">1
                    <option value="2">2
                    <option value="3">3
                    <option value="4">4
                    <option value="5">5
                    <option value="6">6
                    <option value="7">7
                    <option value="8">8
                    <option value="9">9
                    <option value="10">10
                    <option value="11">11
                    <option value="12">12
                    </select>
                    Year: <select id="year">
                    <option value="">Select Year
                    <option value="2009">2009
                    <option value="2010">2010
                    <option value="2011">2011   
                    <option value="2012">2012
                    <option value="2013">2013
                    </select>
                    <a href="#" class="button"  id="view" onClick="viewCodeChangesXsl();"/>View Changes</a><br />
              </td></tr></table>
              </p>
            </td>
	</tr>
        <tr>
            <td colspan="2"><p class="title" align="center">Open Ended Coding Overview</p>
            <p>The most potentially valuable part of any survey questionnaire consists of the open-ended questions that 
            is, the questions to which the respondents can make any reply they want, rather than just ticking one of the 
            limited list of alternatives provided by the surveyor. It is usually the only part of the questionnaire where
            respondents are free to address the issues that most concern them, rather than the issues that most concern 
            the surveyor. Often these issues are the most important ones.</p>
            <p>The most frequent responses to open-ended questions are also valuable. For example, in evaluating customer 
            satisfaction with service, the open-ended questions often tell you what aspects of service are really of most 
            concern to customers. You might find that respondents tend to rate four or five aspects of service low on 
            the rating scales, but that the responses to the open-ended questions are dominated by strong opinions about 
            only one of these aspects. You then know where to start trying to improve service.</p>
            <p>Before summaries of the responses to open-ended questions can be prepared, they have to be combined into 
               categories reflecting general trends in the answers. This process is called coding.</p>
            <p>For example, if three people report &quot;I hate my boss&quot;, &quot;I detest my boss&quot;, and &quot;I loathe my boss&quot;, I 
            would probably combine them into a single category called something like Dislike of supervisor.</p> 
            </td>
        </tr>
    </table>
    </form>
    </div>
    <% } %>
    </body>
</html>
