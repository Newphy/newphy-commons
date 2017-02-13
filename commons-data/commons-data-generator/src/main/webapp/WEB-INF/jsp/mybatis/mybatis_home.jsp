<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="../common.jsp"%>
<!DOCTYPE HTML>
<html>
<head>
<title>Mybatis代码生成</title>
</head>
<body>
		<div id="content">
			<div id="content-header">
				<h1>Mybatis代码生成</h1>
				<div class="btn-group">
					<a class="btn btn-large tip-bottom" title="Manage Files"><i class="icon-file"></i></a>
					<a class="btn btn-large tip-bottom" title="Manage Users"><i class="icon-user"></i></a>
					<a class="btn btn-large tip-bottom" title="Manage Comments"><i class="icon-comment"></i><span class="label label-important">5</span></a>
					<a class="btn btn-large tip-bottom" title="Manage Orders"><i class="icon-shopping-cart"></i></a>
				</div>
			</div>
			<div id="breadcrumb">
				<a href="${_basePath}/" title="Go to Home" class="tip-bottom"><i class="icon-home"></i> Home</a>
				<a href="#" class="tip-bottom" data-original-title="">DAO生成</a>
				<a href="#" class="current">Mybatis代码生成</a>
			</div>
			<div class="container-fluid">
				<div class="row-fluid">
					<!-- 配置 -->
					<div class="span4" style="width: 25%">
						<div class="widget-box">
							<div class="widget-title">
								<span class="icon">
									<i class="icon-eye-open"></i>
								</span>
								<h5>配置</h5>
							</div>
							<div class="widget-content nopadding">
								<form action="#" method="get" class="form-horizontal mini-form">
									<div class="control-group">
										<label class="control-label">数据源</label>
										<div class="controls">
											<select name="dsId" id="dslist" style="width:200px">
												<option>请选择数据源</option>
												<c:forEach var="ds" items="${dses}" varStatus="stat">
													<option value="${ds.id }">${ds.name }</option>
												</c:forEach>
											</select>
											<span id="connectStatus" ></span>
										</div>
									</div>
									<div class="control-group">
										<label class="control-label">Schema</label>
										<div class="controls">
											<select name="schema" id="schemaList"  style="width:200px">
												<option value=""></option>
											</select>
										</div>
									</div>									
									<div class="control-group">
										<label class="control-label">表过滤</label>
										<div class="controls">
											<input type="text" id="tableFilter"  />
										</div>
									</div>
									<div class="control-group">
										<label class="control-label">项目地址</label>
										<div class="controls">
											<input type="text" id="projectDir" placeholder="项目文件地址">
										</div>
									</div>									
									<div class="control-group">
										<label class="control-label">mapper路径</label>
										<div class="controls">
											<input type="text" value="/mybatis/mapper">
										</div>
									</div>
									<div class="control-group">
										<label class="control-label">包路径</label>
										<div class="controls">
											<input type="text" value="">
										</div>
									</div>
									<div class="control-group">
										<label class="control-label">作者</label>
										<div class="controls">
											<input type="text" value="">
										</div>
									</div>
									<div class="form-actions">
										<button type="submit" class="btn btn-primary">Save</button>
									</div>
								</form>

							</div>
						</div>
					</div>
					
					<!-- table -->
					<div  id="tableListFrame" class="span3" style="width:27%">
					</div>
					
					<!-- plan -->
					<div id="planListFrame"  class="span5" style="width:40%">

					</div>
				</div>
			</div>
		</div>
<script type="text/javascript">
	(function($) {
		$("#dslist").change(function(){
			$("#schemaList").empty().append("<option value=''></option>" );
			var url = "${_basePath}/dao/getSchemas?dsId=" + $(this).val();
			$.ajax({
				type:"POST",
				url: url,
				beforeSend: function() {
					$("#connectStatus").addClass("label label-info").html("connecting...");
					$("#dsList").attr("disabled", true);
					$("#schemaList").attr("disabled", true);
					$("#tableFilter").attr("disabled", true);
				},
				success: function(schemas) {
					$("#connectStatus").removeAttr("class").addClass("label label-success").html("success");
					$.each(schemas, function(i, schema){
						$("#schemaList").append("<option value='" + schema.name + "'>" + schema.name + "</option>");
					});
					//$("#schemaList").change();
				},
				complete: function() {
					$("#dsList").removeAttr("disabled");
					$("#schemaList").removeAttr("disabled");
					$("#tableFilter").removeAttr("disabled");
				},
				error: function(){
					$("#connectStatus").removeAttr("class").addClass("label label-important").html("failed");
				}
			});
		});
		
		$("#schemaList").change(function(){
			var schema = $(this).val();
			var dsId = $("#dslist").val();
			var url = "${_basePath}/dao/tableListFrame?dsId=" + dsId + "&schemaName=" + schema;
			$.post(url, function(data){
				$("#tableListFrame").html(data);
			});
		});
		
		var t;
		$("#tableFilter").keydown(function() {
			if(t) {
				clearTimeout(t);
			}
			t = setTimeout(function(){
				filterTable();
			}, 500);
		});

		$("#schemaList").change();
		refreshPlanListFrame();
	})(jQuery);
	
	
	function refreshPlanListFrame(opt, plan) {
		var url = "${_basePath}/dao/planListFrame?opt=" + opt;
		$.post(url, plan, function(data){
			$("#planListFrame").html(data);
		});	
	}
	
	function filterTable() {
		var match = $.trim($("#tableFilter").val());
		if(match != '') {
			match = "[data-table*='" + match + "']";
		}
		$(".tableRow").hide().attr("disabled", true).find(":checkbox" + match).attr("disabled", false).closest("tr").show();
		var $tables = $(".tableRow:visible");
		if($tables.size() == 0) {
			$("#columnList tr").hide();
		}
		else {
			$tables.get(0).click();
		}
	}
</script>
</body>

</html>
