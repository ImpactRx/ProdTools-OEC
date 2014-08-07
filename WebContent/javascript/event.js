function addEvents(method,capture)
{

    // Support detection
    if ((method == 'listener' && !document.addEventListener) || (method == 'attach' && !document.attachEvent))
    {
        alert('Your browser doesn\'t support this event registration model');
				return;
    }

    var infostring;
    // If keyup: add to document, not to layers
    if (method == 'keyup')
    {
      document.onkeyup = hotKeys;
      infostring = 'keyup added to document';
    }
    if (method == 'keypress')
		{
		  document.onkeypress = hotKeys;
			infostring = 'keypress added to document';
    }

}

function hotKeys()
{
    var KeyID = (window.event) ? window.event.keyCode : null;
    //alert(KeyID);
    if (KeyID == "113") // THIS IS THE "F2" KEY FOCUS TO CODE LIST
    {
      document.codes.codeList.focus();
    }
    if (KeyID == "192") //THIS IS THE "`" KEY FOCUS TO THE SEARCH BOX
    {
  		document.codes.search.focus();
    }
    if (KeyID == 8)
    {
    	return false;
    }    
    //WHEN NUMLOCK IS NOT ACTIVATED WE NEED TO CAPTURE THE KEYS
    if ((KeyID == "35") || (KeyID == "40")|| (KeyID == "34")|| (KeyID == "37")|| (KeyID == "12")|| (KeyID == "39"))
    {
    	getKey(event);
    }
    
    if (KeyID == "36") // NUMLOCK 7 SKIP AND TAG
		{
			saveSkipTag();
    }
    if (KeyID == "38") // NUMLOCK 8 SAVE AND NEXT
		{
			saveResp();
    }
    if (KeyID == "18") // NUMLOCK 8 SAVE AND NEXT
				{
					saveResp();
    }
    if (KeyID == "33") // NUMLOCK 9 RESET
    {
    	resetResp();
    }
}
