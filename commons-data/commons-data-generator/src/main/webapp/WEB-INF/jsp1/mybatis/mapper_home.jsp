<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="../layouts/common.jsp"%>
<!DOCTYPE HTML>
<html>
<head>
<title>数据源配置</title>
</head>
<body>
	<div class="xs">
		<h3>生成映射文件</h3>
		<div class="form-group">
			<form class="form-horizontal" action="${_basePath}/mybatis/mapperHome">
				<div class="panel panel-warning" data-widget="{&quot;draggable&quot;: &quot;false&quot;}" data-widget-static="">
					<div class="panel-heading">
						<h2>数据源列表</h2>
						<div class="panel-ctrls" data-actions-container="" data-action-collapse="{&quot;target&quot;: &quot;.panel-body&quot;}">
							<span class="button-icon has-bg"><i class="ti ti-angle-down"></i></span>
						</div>
					</div>
					<div class="panel-body no-padding" style="display: block;">
						<div class="row">
							<div class="col-md-12">
								<div class="col-md-3">
									<label for="selector1" class="control-label col-md-3">数据源</label>
									<div class="col-md-6">
										<select name="dsId" id="dslist" class="form-control1">
											<option value="">选择数据源</option>
											<c:forEach var="ds" items="${dses}" varStatus="stat">
												<option value="${ds.id }">${ds.name }</option>
											</c:forEach>
										</select>
									</div>
									<div class="col-md-3">
										<label class="label-has-error " id="connectStatus">connecting...</label>
									</div>
								</div>
								<div class="col-md-3">
									<label for="selector1" class="col-sm-1 control-label">Schema</label>
									<select name="schema" id="schemaList" class="form-control1">
										<option value=""></option>
									</select>
								</div>
								
								<div class="col-sm-3">
									<label for="focusedinput" class="col-sm-1 control-label">过滤表</label>
									<input type="text" class="form-control1" id="tableFilter"  />
								</div>
															
							</div>
							<div class="clearfix"></div>
						</div>
					</div>
				</div>
			</form>
		</div>

		<div class="form-group" id="mapperContent">

		</div>	

	</div>

<script type="text/javascript">
	(function($) {
		$("#dslist").change(function(){
			$("#schemaList").empty().append("<option value=''></option>" );
			var url = "${_basePath}/mybatis/getSchemas?dsId=" + $(this).val();
			$.ajax({
				type:"POST",
				url: url,
				beforeSend: function() {
					$("#connectStatus").html("connecting...");
					$("#dsList").attr("disabled", true);
					$("#schemaList").attr("disabled", true);
					$("#tableFilter").attr("disabled", true);
				},
				success: function(schemas) {
					$("#connectStatus").html("success");
					$.each(schemas, function(i, schema){
						$("#schemaList").append("<option value='" + schema.name + "'>" + schema.name + "</option>" );
					});
					$("#schemaList").change();
				},
				complete: function() {
					$("#dsList").removeAttr("disabled");
					$("#schemaList").removeAttr("disabled");
					$("#tableFilter").removeAttr("disabled");
				},
				error: function(){
					$("#connectStatus").html("failed");
				}
			});
			$.post(url, function(schemas){

			});
		});
		
		$("#schemaList").change(function(){
			var schema = $(this).val();
			var dsId = $("#dslist").val();
			var url = "${_basePath}/mybatis/mapperContent?dsId=" + dsId + "&schemaName=" + schema;
			$.post(url, function(data){
				$("#mapperContent").html(data);
			});
		});
		
		var t;
		$("#tableFilter").keypress(function(){
			if(t) {
				clearInterval(t);
			}
			t = setInterval(function(){
				filterTable();
			}, 500);
		});

	})(jQuery);
	
	
	function filterTable() {
		var match = $.trim($("#tableFilter").val());
		if(match != '') {
			match = "[data-table*='" + match + "']";
		}
		$(".tableRow").hide().attr("disabled", true).find(":checkbox" + match).attr("disabled", false).closest("tr").show();
	}
</script>
</body>

</html>
