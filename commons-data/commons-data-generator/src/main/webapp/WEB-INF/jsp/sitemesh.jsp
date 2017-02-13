<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="./common.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>
<title><sitemesh:write property='title' /></title>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<link rel="stylesheet" href="${_basePath}/css/bootstrap.min.css" />
<link rel="stylesheet" href="${_basePath}/css/bootstrap-responsive.min.css" />
<link rel="stylesheet" href="${_basePath}/css/uniform.css" />
<link rel="stylesheet" href="${_basePath}/css/select2.css" />
<link rel="stylesheet" href="${_basePath}/css/unicorn.main.css" />
<link rel="stylesheet" href="${_basePath}/css/unicorn.grey.css" class="skin-color" />
<!-- link rel="stylesheet" href="${_basePath}/css/docs.css" /> -->
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />

<script src="${_basePath}/js/jquery.min.js"></script>
<script src="${_basePath}/js/jquery.ui.custom.js"></script>
<script src="${_basePath}/js/bootstrap.min.js"></script>
<script src="${_basePath}/js/bootstrap-colorpicker.js"></script>
<script src="${_basePath}/js/bootstrap-datepicker.js"></script>
<script src="${_basePath}/js/jquery.uniform.js"></script>
<script src="${_basePath}/js/select2.min.js"></script>
<script src="${_basePath}/js/unicorn.js"></script>
<script src="${_basePath}/js/unicorn.form_common.js"></script>

<sitemesh:write property='head' />
</head>
<body>


	<%@ include file="./layouts/header.jsp"%>
	<%@ include file="./layouts/sidebar.jsp"%>

	<sitemesh:write property='body' />


	<%@ include file="./layouts/footer.jsp"%>
</body>

</html>
