<html>
<head>
	<title></title>
	<script type='text/javascript' src='/oec/dwr/interface/codes.js'></script>
	<script type='text/javascript' src='/oec/dwr/engine.js'></script>
	<script type='text/javascript' src='/oec/dwr/util.js'></script>

</script>
	
</head>
<body onload="populate()">
Test
<input type="button" onclick="getCodes()" value="Get all products"/>
<input type="button" onclick="getLabel()" value="Get Code Label"/>
<input type="text" id="test" value="here"/>
<script type="text/javascript">
	function populate()
	{
		//codes.getAllCodes(buildList);
		codes.getMappedCodes(buildList);
	}
	function buildList(data)
	{
		var codeList = new  Array(data.length);
		//alert("Here");
		//DWRUtil.addOptions("codes", ["Please select ..."]);
		//for (var i = 0; i < data.length; i++)
		//{
		 	//var result = data[i];		 	
		 	//codeList[i] = result.codeId+":'"+result.codeLabel+"'";		 	

		//}
    //DWRUtil.addOptions("codes",codeList);
    DWRUtil.addOptions("codes", data);		
	}
	function getCodes()
	{
		var text = "test label";  			
   	codes.getAllCodes(printMessage);
   	DWRUtil.setValue("test", "Check");    
  }
  
  function getLabel()
  {
  	codes.getCodeLabel(printTest);   
  }
  function printMessage(message)
  {
  	var temp = "";
  	for (var i = 0; i < message.length; i++)
	  {
	  	var result = message[i];
	  	temp = temp + "<div>" + result.codeLabel + "</div>";
    }
  	document.getElementById('productlist').innerHTML = temp;
  }
  function printTest(label)
  {
  	var temp = "<b>"+label+"</b>";
  	document.getElementById('codelabel').innerHTML = temp;
  	
  }
  function showId(form)
  {
  	//alert(form.codes.value);
  }
</script>
<p></p>
<form name="oec">
<table>
	<tr>
		<td><select id="codes" multiple size="4" onClick="javascript:showId(document.oec);"></select></td>
	</tr>
</table>
<div id="codeEdits">
	<table>
		<tr>
			<td>Code Id</td><td><input type="text" name="codeId" value=""/></td>			
		</tr>
		<tr>
			<td>Code Label</td><td><input type="text" name="codeLabel" value=""/></td>			
		</tr>
		<tr>
			<td>Code</td><td><input type="text" name="codeDesc" value=""/></td>			
		</tr>
</table>
</div>
</form>
<span id="productlist">
<p></p>
<span id="codelabel">
</body>
</html>