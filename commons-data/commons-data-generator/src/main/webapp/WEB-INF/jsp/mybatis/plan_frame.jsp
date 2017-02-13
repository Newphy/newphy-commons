<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="../common.jsp"%>


	<form id="planModalForm" class="form-horizontal" action="#" method="POST">
		<input name="id" type="hidden" value="${plan.id}" /> <input id="opt" type="hidden"
			value="<c:choose><c:when test="${plan.id == null }">add</c:when><c:otherwise>update</c:otherwise></c:choose>" /> <input type="hidden" name="group" value="mybatis" />
		<div class="control-group">
			<label class="control-label" style="width: 100px">名称</label>
			<div class="controls" style="margin-left: 120px">
				<input type="text" class="form-control1" name="name" value="${plan.name }" />
			</div>
		</div>

		<div class="control-group">
			<label class="control-label" style="width: 100px">输出路径 </label>
			<div class="controls" style="margin-left: 120px">
				<input type="text" class="form-control1" name="targetPath" value="${plan.targetPath }" />
			</div>
		</div>

		<div class="control-group">
			<label class="control-label" style="width: 100px">包路径</label>
			<div class="controls" style="margin-left: 120px">
				<input type="text" class="form-control1" name="pkgPath" value="${plan.pkgPath }" />
			</div>
		</div>

		<div class="control-group">
			<label class="control-label" style="width: 100px">模板内容</label>
			<div class="controls" style="margin-left: 120px">
				<textarea id="templateCode" name="content" cols="30" rows="30">${plan.content }</textarea>
			</div>
		</div>

		<div class="form-actions">
			<button class="btn btn-primary btn-save">保存</button>
		</div>
	</form>




<script type="text/javascript">
	(function($) {
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
		console.log("dddd");
		$("#planModalForm .btn-save").click(function() {
			var opt = $("#planModalForm #opt").val();
			refreshPlanListFrame(opt, $("#planModalForm").serialize());
			$('#planFrame').modal('hide');
			return false;
		});
	})(jQuery);
	

</script>