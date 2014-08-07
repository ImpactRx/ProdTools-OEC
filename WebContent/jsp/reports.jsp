<script language="JavaScript">
    
</script>
<form name="rpts">
<table class="tier1">
<tr>
    <td colspan="2">
    <font class="step">STEP ONE</font>
    </td>
</tr>
<tr>
		<td nowrap>
			From: <input type="textbox" id="sdate" name="sdate" size="12" value="" onchange="changeScreen('from');"/> Format: mm/dd/yyyy <br />
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;To: <input type="textbox" id="tdate" name="tdate" size="12"  value="" onchange="changeScreen('to');"/> Format: mm/dd/yyyy 							
				
    </td>
    <td>
    	&nbsp;Select Study Type: <select id="study" name="study" onchange="changeScreen('study'); getMarkets()"></select>		
    </td>
    
</tr>
<tr>
    <td colspan="2">
    <font class="step">STEP TWO</font>
    </td>
</tr>
<tr>
    <td id="step2" style="visibility: hidden" align="center" width="50%">
    	Markets:<br />
    	<select id="markets" name="markets" multiple size="8" onchange="changeScreen('markets');"></select><br />
    	<center><a href="#" class="respbutton" onClick="getProducts(document.rpts.markets)">Get Products</a></center>
    </td>
    <td  id="step2a" style="visibility: hidden"  align="center"  width="50%">
			Products:<br />
		  <select id="products" name="products" multiple size="8" onchange="changeScreen('products');"></select>		
		  <center><a href="#" class="respbutton" onClick="getQuestions(document.rpts.products)">Get Questions / Cells</a></center>
    </td>
</tr>
<tr>
    <td colspan="2">
    <font class="step">STEP THREE</font>
    </td>
</tr>
<tr>
    <td id="step3" style="visibility: hidden" align="center" width="50%">
    <center>Questions:<br />
    <select id="questions" name="questions" multiple size="8" onchange="changeScreen('questions');">></select></center>
    </td>
    <td id="step3a" style="visibility: hidden" align="center" width="50%">
		    <center>Cells:<br />
		    <select id="cells" name="cells" multiple size="8" onchange="changeScreen('cells');"></select></center>
		    <center><a href="#" class="respbutton" onClick="getProgramLabels(document.rpts.questions, document.rpts.cells);">Get Programs</a></center>		    
    </td>
</tr>
<tr>
    <td colspan="2">
    <font class="step">STEP FOUR</font>
    </td>
</tr>
<tr>
    <td colspan="2" id="step4" style="visibility: hidden">
    	<center>Programs:<br />
		  <select id="programs" name="programs" multiple size="8"></select></center>
    </td>
</tr>
<tr height="10px"><td colspan="2"></td></tr>
<tr>
    <td align="center" colspan="2"><a href="#" class="respbutton" id="getreport" onClick="getReport()" style="visibility: hidden">Get My Report</a></td>
</tr>
</table>
</form>