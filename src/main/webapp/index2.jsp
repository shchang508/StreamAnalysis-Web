<!DOCTYPE html>

<%@ page import="com.jamie.streamAnalysis.action.SessionCounter"%>

<html>

<head>
<meta charset="utf-8" />
<title>Stream Analysis</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link href='http://fonts.googleapis.com/css?family=Lato:400,700' rel='stylesheet' type='text/css'>
<link rel="stylesheet" href="style.css" />
<link rel="icon" href="image/icon.png">

<style type="text/css">
.myloading {
	position: fixed;
	z-index: 1000;
	top: 0;
	left: 0;
	height: 100%;
	width: 100%;
	background: rgba(255, 255, 255, .8) url('image/loading.gif') 50% 50%
		no-repeat;
}

.divBar {
	position: fixed;
	z-index: 1000;
	top: 0;
	left: 0;
	height: 100%;
	width: 100%;
}
</style>

<script type="text/javascript" src="jquery-1.12.4.js"></script>
<script src="js/basic.js"></script>

</head>

<body>

	<div class="alert">
		<span class="closebtn"
			onclick="this.parentElement.style.display='none';">&times;</span> <strong>Heads
			up! </strong> Please check if there is someone else using this tool at the same time.
	</div>

	<h1>
		Stream Analysis <span class="span">with TSReader</span>
	</h1>

	<div class="block">
		<div class="box">
			<input class="textField" type="text" id="inputName"
				placeholder="Enter path" />


			<button class="submitBtn" onclick="doExport();">
				<svg version="1.1" xmlns="http://www.w3.org/2000/svg"
					xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px"
					width="24px" height="24px" viewBox="0 0 24 24"
					enable-background="new 0 0 24 24" xml:space="preserve">
				<g id="Bounding_Boxes">
					<path fill="none" d="M0,0h24v24H0V0z" />
				</g>
				<g id="Outline">
					<g id="ui_x5F_spec_x5F_header">
					</g>
					<g>
						<path
						d="M19.35,10.04C18.67,6.59,15.64,4,12,4C9.11,4,6.6,5.64,5.35,8.04C2.34,8.36,0,10.91,0,14c0,3.31,2.69,6,6,6h13
							c2.76,0,5-2.24,5-5C24,12.36,21.95,10.22,19.35,10.04z M19,18H6c-2.21,0-4-1.79-4-4c0-2.05,1.53-3.76,3.56-3.97l1.07-0.11
							l0.5-0.95C8.08,7.14,9.94,6,12,6c2.62,0,4.88,1.86,5.39,4.43l0.3,1.5l1.53,0.11C20.78,12.14,22,13.45,22,15
							C22,16.65,20.65,18,19,18z" />
						<polygon
						points="13.45,10 10.55,10 10.55,13 8,13 12,17 16,13 13.45,13 		" />
					</g>
				</g>
				</svg>
				Download
			</button>

		</div>

		<div class="counter">
			<%
				SessionCounter counter = (SessionCounter) session.getAttribute(SessionCounter.COUNTER);
			%>

			Number of online user(s):
			<%=counter.getActiveSessionNumber()%>
		</div>


	</div>


	<form id="exportForm" method="post" action="f1?action=export"
		onSubmit="return false;">
		<input type="hidden" id="filePath" name="filePath" />
	</form>
	<!-- 	<form id="exportForm" method="post" action="f1" target="exportFrame" -->
	<!-- 		onSubmit="return false;"> -->
	<!-- 		<input type="hidden" id="filePath" name="filePath"/> -->
	<!-- 	</form> -->
	<!-- 	<iframe id="exportFrame" name="exportFrame" src="" frameborder="0" -->
	<!-- 		marginwidth="0" marginheight="0" style="display: none;"></iframe> -->

	<div id="loadingDiv" class="divBar" style="display: none">
		<img id="loading" class="" />
	</div>




	<script>
		function wait(ms) {
			var start = new Date().getTime();
			var end = start;
			while (end < start + ms) {
				end = new Date().getTime();
			}
		}

		// 		function doDownloadFile() {
		// 			document.getElementById('filePath').value = document
		// 					.getElementById('inputName').value;
		// 			document.getElementById('exportForm').submit();
		// 		}

		function doValid() {
			var path = document.getElementById("inputName").value;
			console.log("File path : " + path);

			if (path == "") {
				alert("Please input a valid path!");
				return false;
			}

			return true;
		}

		function doExport() {
			if (doValid() == false) {
				return;
			}

			console.log("open loading...");
			document.getElementById("loading").className = "myloading";
			document.getElementById("loadingDiv").style.display = "block";

			var input = document.getElementById("inputName").value;
			document.getElementById("filePath").value = input;

			var xhr = createXHR2();
			xhr.open("GET", "/StreamAnalysis-Web/f1?action=generate&filePath="
					+ encodeURI(input), true);
			xhr.send();

			xhr.onreadystatechange = function(e) {
				if (xhr.readyState === 4 && xhr.status == 200) {
					console.log(xhr.reponseText)
					console.log(xhr.getAllResponseHeaders());
					console.log(xhr.getResponseHeader("Content-Type"));
					console.log(xhr.status);
					console.log(xhr.statusText);
					console
							.log(
									"onreadystatechange: readyState:%d, status:%d, responseText:%s",
									xhr.readyState, xhr.status,
									xhr.responseText);
					handleResult(xhr.responseText);
				}
			}
		}

		function handleResult(ret) {
			ret = JSON.parse(ret);
			if (ret.ERR_CDE == "00") {
				console.log("exporting excel...");
				document.getElementById("exportForm").submit();
			} else {
				alert(ret.ERR_MSG);
			}

			console.log("close loading...");
			document.getElementById("loading").className = "";
			document.getElementById("loadingDiv").style.display = "none";
		}
	</script>
</body>

</html>
