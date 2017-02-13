<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="../common.jsp"%>


	<form id="planModalForm" class="form-horizontal" action="#" method="POST">

		<div class="control-group">
			<label class="control-label" style="width: 100px">模板内容</label>
			<div class="controls" style="margin-left: 120px">
				<textarea id="templateCode" cols="30" rows="30">${plan.content }</textarea>
			</div>
		</div>

		<div class="form-actions">
			<button class="btn btn-primary btn-close">保存</button>
		</div>
	</form>



<script type="text/javascript">
	(function($) {
		$("#planModalForm .btn-close").click(function() {
			$('#planFrame').modal('hide');
			return false;
		});


	})(jQuery);
	
	  var editor = CodeMirror.fromTextArea('templateCode', {
		    height: "500px",
		    parserfile: "parsefreemarker.js",
		    stylesheet: "${_basePath}/css/codemirror/freemarkercolors.css",
		    path: "${_basePath}/js/codemirror/",
		    continuousScanning: 500,
		    autoMatchParens: true,
		    lineNumbers: true,
		    markParen: function(node, ok) { 
		        node.style.backgroundColor = ok ? "#CCF" : "#FCC#";
		        if(!ok) {
		            node.style.color = "red";
		        }
		    },
		    unmarkParen: function(node) { 
		         node.style.backgroundColor = "";
		         node.style.color = "";
		    }
		  });
</script>