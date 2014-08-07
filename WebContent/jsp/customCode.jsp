<script language="JavaScript">
    function mapCode()
    {
        var client = DWRUtil.getValue("client");
        var map = DWRUtil.getValue("map");
        if ((client == "") || (map == ""))
        {
            alert("You must select a Client and Map to build a code too.");
            return false;
        }
        codes.insertClientCode(pCodeId,pCodeLabel,pCodeBookId, pUser)
    }
    function displayCode(data)
    {
    
    }
</script>
<b><span id="clientName"></span></b><br />
<b><span id="mapname"></span></b><br />
<table class="tier2">
<tr>
    <td>
    Client Code: <select id="clientCode"></select>
    &nbsp;&nbsp;<a href="#" class="respbutton" onClick="createCode()">New Code</a>
    </td>
</tr>
<tr>
    <td><select id="codeList" ondblclick="showFullText()" name="codeList1" multiple size="15" style="width: 150mm"></select>		
    </td>
</tr>
<tr height="10px"><td></td></tr>
<tr>
    <td align="center"><a href="#" class="respbutton" onClick="mapCode()">Map Code</a></td>
</tr>
</table>