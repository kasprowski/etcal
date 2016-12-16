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
<hr/>
<br/>
<div style="width: 100%">
<div style="float:left">
<button onclick="post('add',document.getElementById('data').value,updateStatus)">Send data (/add)</button>
<button onclick="get('reset',updateStatus)">Reset data (/reset)</button>
<button onclick="post('get',document.getElementById('data').value,updateResults)">Get calibrated data (/get)</button>
<br/>
Data<br/> <textarea id="data" name="data" cols="70" rows="5"></textarea>
</div>

<div style="float:left;text-align:left;padding-left:20px;font-size: 80%">
Example:<br/>{"dataUnits":[{"variables":[0.0,0.0,10.0,20.0],"targets":[{"x":260.0,"y":260.0,"w":1.0}]},<br/>
{"variables":[100.0,0.0,100.0,1.0],"targets":[{"x":250.0,"y":250.0,"w":1.0}]},<br/>
{"variables":[100.0,100.0,34.0,21.0],"targets":[{"x":180.0,"y":180.0,"w":1.0}]}]}
</div>

</div>

<div style="clear: left"> 
<hr/>
</div>
<div>
<div style="float:left">
<button onclick="post('build',document.getElementById('params').value,updateStatus)">Calibrate (/build)</button>
<button onclick="post('optimize',document.getElementById('params').value,updateStatus)">Optimize (/optimize)</button>
<button onclick="post('usefilter',document.getElementById('params').value,updateStatus)">Filter (/usefilter)</button>
<br/>
Params<br/> <textarea id="params" name="params" cols="70" rows="2"></textarea>
</div>
<div style="float:left;align: left;padding-left:20px;font-size: 80%">
Some examples:<br/>
<em>Calibrator:</em> {type="pl.kasprowski.etcal.calibration.CalibratorPolynomial",params={}}<br>
<em>Optimizer:</em> {type="pl.kasprowski.etcal.optimizer.polynomial.GeneticPolynomialOptimizer",params={}}<br/>
<em>Another Optimizer:</em> {type="pl.kasprowski.etcal.optimizer.svr.FullSvrOptimizer",params={cvFolds=2,cvType=1}}<br/>
<em>Filter:</em> {type="pl.kasprowski.etcal.filters.AoiMedianizeFilter",params={}}<br/>
</div>
</div>

<div style="clear: left"> 
<hr/>
</div>

<p id="results">
...
</p>

</body>
</html>
