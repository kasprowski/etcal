<%@ page isELIgnored="false" %> 

<html>
<head>
 <script src="ajax.js"></script>
 <script>
 function get(url,callback) {
		xmlHttp=GetXmlHttpObject();
		xmlHttp.onreadystatechange=callback;
		xmlHttp.open("GET", url, true);
		xmlHttp.send(null);
	}
 function post(url,body,callback) {
			xmlHttp=GetXmlHttpObject();
			xmlHttp.onreadystatechange=callback;
			xmlHttp.open("POST", url, true);
			xmlHttp.send(body);
		}

 
 function updateStatus() {
		if (xmlHttp.readyState==4) { 
			document.getElementById("status").innerHTML
	  				 	    =xmlHttp.responseText;
		}
	}

 function updateResults() {
		if (xmlHttp.readyState==4) { 
			document.getElementById("results").innerHTML
	  				 	    =xmlHttp.responseText;
		}
	}
 
 </script>
</head>
<body>
<h1>ETCAL</h1>
Status:
<span id="status">${etcal.statusInfoJSON}</span>
<button onclick="get('status',updateStatus)">Refresh (/status)</button>
<br/>
<button onclick="post('add',document.getElementById('data').value,updateStatus)">Send data (/add)</button>
<button onclick="get('reset',updateStatus)">Reset data (/reset)</button>
<button onclick="post('get',document.getElementById('data').value,updateResults)">Get calibrated data (/get)</button>
<form action="add" method="post">
Data<br/> <textarea id="data" name="data" cols="70" rows="5"></textarea>
</form>
<button onclick="post('build',document.getElementById('params').value,updateStatus)">Calibrate (/build)</button>
<button onclick="post('optimize',document.getElementById('params').value,updateStatus)">Optimize (/optimize)</button>
<form action="build" method="post">
Params<br/> <textarea id="params" name="params" cols="70" rows="2"></textarea>
</form>
<p id="results">
...
</p>

</body>
</html>
