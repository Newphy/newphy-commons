<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="../common.jsp"%>
<!DOCTYPE HTML>
<html>
<head>
<title>参数配置</title>
</head>
<body>
	<div id="content">
		<div id="content-header">
			<h1>参数配置</h1>
			<div class="btn-group">
				<a class="btn btn-large tip-bottom" title="Manage Files"><i class="icon-file"></i></a> <a class="btn btn-large tip-bottom" title="Manage Users"><i class="icon-user"></i></a> <a
					class="btn btn-large tip-bottom" title="Manage Comments"><i class="icon-comment"></i><span class="label label-important">5</span></a> <a class="btn btn-large tip-bottom"
					title="Manage Orders"><i class="icon-shopping-cart"></i></a>
			</div>
		</div>
		<div id="breadcrumb">
			<a href="${_basePath}/" title="Go to Home" class="tip-bottom"><i class="icon-home"></i> Home</a> <a href="#" class="current">参数配置</a>
		</div>
		<div class="container-fluid">

			<div class="row-fluid">
				<div class="span12">
					<%@include file="../alert.jsp" %>
					<div class="widget-box">
						<div class="widget-title">
							<span class="icon"> <i class="icon-th"></i>
							</span>
							<h5>参数列表</h5>
							<div class="buttons">
								<a href="#" id="btnAdd" data-toggle="modal"  class="btn btn-primary btn-mini"><i class="icon-plus icon-white"></i> 新增</a>
							</div>
						</div>
						<div class="widget-content nopadding">
							<form id="form1" action="${_bashPath }/arg/argHome" method="POST">
								<input id="formId" name="id" type="hidden" /> <input id="opt" name="opt" type="hidden" value="list" />
								<table class="table table-bordered table-striped">
									<thead>
										<tr class="warning">
											<th>#</th>
											<th>参数类型</th>
											<th>参数名称</th>
											<th>参数值</th>
											<th>描述</th>
											<th>操作</th>
										</tr>
									</thead>
									<tbody>
										<c:forEach var="arg" items="${args}" varStatus="stat">
											<tr>
												<td>${stat.index+1}</td>
												<td>
													<c:choose><c:when test="${arg.type == 0 }">用户参数</c:when><c:when test="${arg.type == 1}">固定参数</c:when></c:choose>
												</td>
												<td>${arg.name }</td>
												<td>${arg.value }</td>
												<td>${arg.description }</td>
												<td>
													<button data-id="${arg.id }" data-toggle="modal" class=" btn btn-mini btn-primary btnEdit">
														<i class="icon-pencil icon-white"></i>
													</button>
													<button data-id="${arg.id }" class="btn btn-mini btn-danger btnRemove">
														<i class="icon-remove icon-white"></i>
													</button>
												</td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</form>
						</div>
					</div>
				</div>
			</div>
			
			<div id="argFrame" class="modal hide">
				<div class="modal-header">
					<button data-dismiss="modal" class="close" type="button">×</button>
					<h3>参数设置</h3>
				</div>
				<div class="modal-body">
				</div>
			</div>

		</div>
	</div>
	<script type="text/javascript">
		(function($) {
			$(".btnRemove").click(function() {
				var id = $(this).attr("data-id");
				$("#formId").val(id);
				$("#opt").val("remove");
				$("#form1").submit();
			});

			$("#btnAdd, .btnEdit").click(function() {
				var $btn = $(this);
				var id = $btn.attr("data-id");
				var url = "${_basePath}/arg/argFrame?id=" + id; 
				$("#argFrame").modal({
					"backdrop" : false,
					"remote": url
				}).on('hidden',function(e){
					$(this).find("input").val("");
					$(this).removeData('modal');
				});
			});

		})(jQuery);
	</script>
</body>

</html>
