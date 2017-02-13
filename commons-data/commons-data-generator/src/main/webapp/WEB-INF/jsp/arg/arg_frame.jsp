<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="../common.jsp"%>


<form id="modalForm" class="form-horizontal" action="${_bashPath}/arg/saveArg" method="POST">
	<input name="id" type="hidden" value="${arg.id}" />
	<div class="control-group">
		<label class="control-label" style="width: 100px">参数名称</label>
		<div class="controls" style="margin-left: 120px">
			<input type="text" class="form-control1"  name="name" value="${arg.name }" />
		</div>
	</div>

	<div class="control-group">
		<label class="control-label" style="width: 100px">参数值</label>
		<div class="controls" style="margin-left: 120px">
			<input type="text" class="form-control1" name="value" value="${arg.value }" />
		</div>
	</div>

	<div class="control-group">
		<label class="control-label" style="width: 100px">参数描述</label>
		<div class="controls" style="margin-left: 120px">
			<input type="text" class="form-control1" name="description" value="${arg.description }" />
		</div>
	</div>

	<div class="form-actions">
		<button type="submit" class="btn btn-primary btn-save">保存</button>
	</div>
</form>

<script type="text/javascript">
	(function($) {

		$("#modalForm .btn-save").click(function() {
			$(this).closest("form").submit();
		});

	})(jQuery);
</script>