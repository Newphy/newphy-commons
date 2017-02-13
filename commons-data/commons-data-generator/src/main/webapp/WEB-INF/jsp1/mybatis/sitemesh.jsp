<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="../layouts/common.jsp"%>
<!DOCTYPE HTML>
<html>
<head>
<title><sitemesh:write property='title' /></title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="application/x-javascript">
	
	 addEventListener("load", function() { setTimeout(hideURLbar, 0); }, false); function hideURLbar(){ window.scrollTo(0,1); } 

</script>
<!-- Bootstrap Core CSS -->
<link href="${_basePath}/css/bootstrap.min.css" rel='stylesheet'
	type='text/css' />
<!-- Custom CSS -->
<link href="${_basePath}/css/style.css" rel='stylesheet' type='text/css' />
<link href="${_basePath}/css/font-awesome.css" rel="stylesheet">
<!-- jQuery -->
<script src="${_basePath}/js/jquery.min.js"></script>
<!----webfonts--->
<link href= '${_basePath}/css/useso.css' rel='stylesheet' type='text/css'>
<!---//webfonts--->
<!-- Bootstrap Core JavaScript -->
<script src="${_basePath}/js/bootstrap.min.js"></script>
</head>
<body>
	<div id="wrapper">
		<!-- Navigation -->
		<%@ include file="../layouts/header.jsp"%>

		<div id="page-wrapper">
			<div class="graphs">
			
				<sitemesh:write property='body' />
				<%@ include file="../layouts/copyright.jsp"%>
			</div>
		
		</div>
		<!-- /#page-wrapper -->
	</div>
	<!-- /#wrapper -->
	<!-- Nav CSS -->
	<link href="${_basePath}/css/custom.css" rel="stylesheet">
	<!-- Metis Menu Plugin JavaScript -->
	<script src="${_basePath}/js/metisMenu.min.js"></script>
	<script src="${_basePath}/js/custom.js"></script>
</body>
</html>
