<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="../common.jsp"%>


	<div class="widget-box">
		<div class="widget-title">
			<span class="icon">
				<i class="icon-file"></i>
			</span>
			<h5>生成计划</h5>
			<div class="buttons">
				<a href="#" id="btnPlanAdd" data-toggle="modal"  class="btn btn-primary btn-mini"><i class="icon-plus icon-white"></i> 新增</a>
			</div>			
		</div>
		<div class="widget-content nopadding">
			<table class="table table-bordered">
				<thead>
					<tr>
						<th style="width:20px"><input type="checkbox" /></th>										
						<th>名称</th>
						<th>输出路径</th>
						<th>包路径</th>
						<th>操作</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="plan" items="${plans }">
						<tr>
							<td  style="width:20px; text-align: center"><input type="checkbox"  data-plan="${plan.id}" checked="true"/></td>
							<td>${plan.name}</td>
							<td>${plan.targetPath}</td>
							<td>${plan.pkgPath}</td>
							<td>
									<button data-id="${plan.id }" data-toggle="modal" class=" btn btn-mini btn-primary btnPlanEdit">
										<i class="icon-pencil icon-white"></i>
									</button>
									<button data-id="${plan.id }" class="btn btn-mini btn-danger btnPlanRemove">
										<i class="icon-remove icon-white"></i>
									</button>
									<button data-id="${plan.id }" class="btn btn-mini btn-danger btnPlanPreview">
										<i class="icon-eye-open icon-white"></i>
									</button>									
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>

	<div id="planFrame" class="modal hide" style="width:1200px;">
		<div class="modal-header">
			<button data-dismiss="modal" class="close" type="button">×</button>
			<h3>生成计划设置</h3>
		</div>
		<div class="modal-body nopadding" style="max-height: 820px">
		</div>
	</div>
	<div id="planPreviewFrame" class="modal hide" style="width:1200px;">
		<div class="modal-header">
			<button data-dismiss="modal" class="close" type="button">×</button>
			<h3>生成计划预览</h3>
		</div>
		<div class="modal-body nopadding" style="max-height: 820px">
		</div>
	</div>	

<script type="text/javascript">
	(function($) {
		$(".btnPlanRemove").click(function(){
			var id = $(this).attr("data-id");
			refreshPlanListFrame("delete", {"id": id});
		});
		
		$("#btnPlanAdd, .btnPlanEdit").click(function() {
			var $btn = $(this);
			var id = $btn.attr("data-id");
			var url = "${_basePath}/dao/planFrame?planId=" + id; 
			$("#planFrame").modal({
				"backdrop" : 'static',
				"remote": url
			}).on('hidden',function(e){
				$(this).find("input").val("");
				$(this).removeData('modal');
			}).css({'margin-top' : -400, 'margin-left': -600});
		});
		
		$(".btnPlanPreview").click(function() {
			var url = "${_basePath}/dao/planPreviewFrame";
			url += "?planId=" + $(this).attr("data-id");
			url += "&dsId=" + $("#dslist").val();
			url += "&schemaName=" + $("#schemaList").val();
			var tableName = $("#tableList tr").filter(".alert-success").find(":checkbox").data("table");
			url += "&tableName=" + tableName;
			var chkColumns = $("#columnList").find(":checkbox[data-table='" + tableName + "']").filter(":checked");
			$.each(chkColumns, function(){
				url += "&columnNames=" + $(this).attr("data-column");
			});
			console.log(url);
			$("#planPreviewFrame").modal({
				"backdrop" : 'static',
				"remote": url
			}).css({'margin-top' : -400, 'margin-left': -600});
		});
		

		
	})(jQuery);
</script>

