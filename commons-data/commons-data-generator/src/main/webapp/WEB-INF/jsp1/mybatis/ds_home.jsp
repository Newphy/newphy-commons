<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="../layouts/common.jsp"%>
<!DOCTYPE HTML>
<html>
<head>
<title>数据源配置</title>
</head>
<body>
	<div class="xs">
		<h3>数据源</h3>
		<form id="form1" action="${_bashPath }/mybatis/dsHome" method="POST">
		<input id="formId" name="id" type="hidden" />
		<input id="opt" name="opt" type="hidden" value="list"/>
		<div class="panel panel-warning" data-widget="{&quot;draggable&quot;: &quot;false&quot;}" data-widget-static="">
			<div class="panel-heading">
				<h2>数据源列表</h2>
				<div class="panel-ctrls" data-actions-container=""
					data-action-collapse="{&quot;target&quot;: &quot;.panel-body&quot;}">
					<span class="button-icon has-bg"><i class="ti ti-angle-down"></i></span>
				</div>
			</div>
			<div class="panel-body no-padding" style="display: block;">
				<table class="table table-striped">
					<thead>
						<tr class="warning">
							<th>#</th>
							<th>数据源名称</th>
							<th>数据驱动</th>
							<th width="50%">URL</th>
							<th>用户名</th>
							<th>操作</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="ds" items="${dses}" varStatus="stat">
							<tr>
								<td>${stat.index+1}</td>
								<td>${ds.name }</td>
								<td>${ds.driverClass }</td>
								<td>${ds.url }</td>
								<td>${ds.user }</td>
								<td>
									<button data-id="${ds.id }" class=" btn btn-xs btn-info btnEdit" >修改</button>
									<button data-id="${ds.id }" class="btn btn-xs btn-success warning_4 btnRemove" >删除</button>									
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div>
		</div>
		</form>

		<!-- 新增表单 -->
		<div class="tab-content">
			<div class="tab-pane active" id="horizontal-form">
				<form id="form2" class="form-horizontal" action="${_bashPath}/mybatis/addDs" method="POST">
					<input name="id" type="hidden" value="${ds.id}" />

					<div class="form-group">
						<label for="focusedinput" class="col-sm-2 control-label">名称</label>
						<div class="col-sm-8">
							<input type="text" class="form-control1" id="dsName" name="name" value="${ds.name }" />
						</div>
					</div>

					<div class="form-group">
						<label for="focusedinput" class="col-sm-2 control-label">URL</label>
						<div class="col-sm-8">
							<input type="text" class="form-control1" id="dsUrl" name="url" value="${ds.url }" />
						</div>
					</div>
					
					<div class="form-group">
						<label for="selector1" class="col-sm-2 control-label">Driver Class</label>
						<div class="col-sm-8">
							<input type="text" class="form-control1" id="dsDriverClass" name="driverClass" value="${ds.driverClass }" />
						</div>
					</div>

					<div class="form-group">
						<label for="focusedinput" class="col-sm-2 control-label">User</label>
						<div class="col-sm-8">
							<input type="text" class="form-control1" id="dsUser" name="user" value="${ds.user }" />
						</div>
					</div>	
					
					<div class="form-group">
						<label for="focusedinput" class="col-sm-2 control-label">Password</label>
						<div class="col-sm-8">
							<input type="password" class="form-control1" id="dsPassword" name="passwd" value="${ds.passwd }" />
						</div>
					</div>					

					<div class="panel-footer">
						<div class="row">
							<div class="col-sm-8 col-sm-offset-2">
								<button id="submit" class="btn btn-sm btn-primary">Submit</button>
								<button id="checkBtn" class="btn btn-sm btn-info">Check</button>
							</div>
						</div>
					</div>
				</form>
			</div>
		</div>

	</div>
	
<script type="text/javascript">
	(function($) {
		
		$(".btnEdit").click(function(){
			var id = $(this).attr("data-id");
			$("#formId").val(id);
			$("#opt").val("edit");
			$("#form1").submit();
		});
		
		$(".btnRemove").click(function(){
			var id = $(this).attr("data-id");
			$("#formId").val(id);
			$("#opt").val("remove");
			$("#form1").submit();
		});
		
		$("#submit").click(function(){
			$("#form2").submit();
		});
		
		$("#form2 input").change(function() {
			$("#checkBtn").removeClass("btn-danger btn-primary btn-info").addClass("btn-info").text("Check");
		});
		
		$("#checkBtn").click(function(){
			var url = "${_bashPath}/mybatis/checkDs";
			$.ajax({
				type:"POST",
				url: url,
				data: $("#form2").serialize(),
				beforeSend: function() {
					$("#form2 input").attr("disabled",true);
					$("#checkBtn").html("Checking...");
				},
				success: function(data) {
					$("#checkBtn").html(data);
				},
				complete: function() {
					$("#form2 input").removeAttr("disabled");
				}
			});
			return false;
		});
			
		$("#dsUrl").blur(function(){
			var url = $("#dsUrl").val();
			if(url !== null && url !== '') {
				var getUrl = "${_bashPath}/mybatis/getDriverClass?url=" + url;
				$.post(getUrl, function(data){
					$("#dsDriverClass").val(data);
				});
			}
		});
		
	})(jQuery);
</script>
</body>

</html>
