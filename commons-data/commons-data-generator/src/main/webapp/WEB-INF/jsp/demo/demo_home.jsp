<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="../common.jsp"%>
<!DOCTYPE HTML>
<html>
<head>
<title>模板编辑</title>


<link rel="stylesheet" href="${_basePath }/css/codemirror5/codemirror.css">
<script src="${_basePath }/js/codemirror5/codemirror.js"></script>
<script src="${_basePath }/js/codemirror5/selection-pointer.js"></script>
<script src="${_basePath }/js/codemirror5/xml.js"></script>
<script src="${_basePath }/js/codemirror5/javascript.js"></script>
<script src="${_basePath }/js/codemirror5/css.js"></script>
<script src="${_basePath }/js/codemirror5/htmlmixed.js"></script>

</head>
<body>
	<div id="content">
		<div id="content-header">
			<h1>模板配置</h1>
			<div class="btn-group">
				<a class="btn btn-large tip-bottom" title="Manage Files"><i class="icon-file"></i></a> <a class="btn btn-large tip-bottom" title="Manage Users"><i class="icon-user"></i></a> <a
					class="btn btn-large tip-bottom" title="Manage Comments"><i class="icon-comment"></i><span class="label label-important">5</span></a> <a class="btn btn-large tip-bottom"
					title="Manage Orders"><i class="icon-shopping-cart"></i></a>
			</div>
		</div>
		<div id="breadcrumb">
			<a href="${_basePath}/" title="Go to Home" class="tip-bottom"><i class="icon-home"></i> Home</a> <a href="#" class="current">模板编辑</a>
		</div>
		<div class="container-fluid">

			<div class="row-fluid">
				<div class="span12">
					<div class="widget-box">
						<div class="widget-title">
							<span class="icon"> <i class="icon-th"></i>
							</span>
							<h5>模板编辑</h5>
							<div class="buttons">
								<a href="#" id="btnAdd" data-toggle="modal" class="btn btn-primary btn-mini"><i class="icon-plus icon-white"></i> 新增</a>
							</div>
						</div>
						<div class="widget-content nopadding">
							<form id="form1" action="${_bashPath }/demo/preview" method="POST"  target="_blank">
								<div class="control-group">
									<textarea id="templateCode" name="content">
<div></div>
								</textarea>
								</div>
								<div class="form-actions">
									<button type="submit" class="btn btn-primary btn-preview">预览</button>
								</div>
							</form>
						</div>
					</div>
				</div>
			</div>

			<div id="dsFrame" class="modal hide">
				<div class="modal-header">
					<button data-dismiss="modal" class="close" type="button">×</button>
					<h3>数据源设置</h3>
				</div>
				<div class="modal-body"></div>
			</div>

		</div>
	</div>
	<script type="text/javascript">
		(function($) {

		})(jQuery);

		// Define an extended mixed-mode that understands vbscript and
		// leaves mustache/handlebars embedded templates in html mode
		var mixedMode = {
			name : "htmlmixed",
			scriptTypes : [ {
				matches : /\/x-handlebars-template|\/x-mustache/i,
				mode : null
			}, {
				matches : /(text|application)\/(x-)?vb(a|script)/i,
				mode : "vbscript"
			} ]
		};
		var editor = CodeMirror.fromTextArea(document
				.getElementById("templateCode"), {
			mode : mixedMode,
			selectionPointer : true,
			lineNumbers : true,
			lineWrapping: true
		});
	</script>
</body>

</html>
