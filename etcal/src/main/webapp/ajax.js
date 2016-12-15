function GetXmlHttpObject() {
  xmlHttp=null;
  try  {
	xmlHttp=new XMLHttpRequest(); // Firefox, Opera 8.0+, Safari
  }catch (e) {
	try { // Internet Explorer
		xmlHttp=new ActiveXObject("Msxml2.XMLHTTP");
     }catch (e) {
		      xmlHttp=new ActiveXObject("Microsoft.XMLHTTP");
		}
    }
  return xmlHttp;
}

function getData() {
//	alert("start!");
	xmlHttp=GetXmlHttpObject();
	if (xmlHttp==null)  {
		alert ("Twoja przegl¹darka nie obs³uguje AJAXa!");
		return;
		} 
	var url="getdata.jsp?v="+document.getElementById('pole').value;
	//alert(url);
	xmlHttp.onreadystatechange=stateChanged;
	xmlHttp.open("GET", url, true);
	xmlHttp.send(null);
}
function stateChanged() {
//	alert("response!"); 
	if (xmlHttp.readyState==4) { 
		//alert(xmlHttp.responseText);
		document.getElementById("info").innerHTML
  				 	    =xmlHttp.responseText;
	}
}
