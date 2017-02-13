<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="../layouts/common.jsp"%>



<div class="row">
	<!-- 第一列 -->
	<div class="col-md-3 grid_box1">
		<div class="panel panel-warning"
			data-widget="{&quot;draggable&quot;: &quot;false&quot;}"
			data-widget-static="">
			<div class="panel-heading">
				<h2>配置属性</h2>
				<div class="panel-ctrls" data-actions-container="" data-action-collapse="{&quot;target&quot;: &quot;.panel-body&quot;}">
					<span class="button-icon has-bg"><i class="ti ti-angle-down"></i></span>
				</div>
			</div>
			<div class="panel-body no-padding" style="display: block;">
				<!-- 新增表单 -->
				<div class="tab-content">
					<div class="tab-pane active" id="horizontal-form">
						<form id="form2" class="form-horizontal" action="${_bashPath}/mybatis/addDs" method="POST">
							<input name="id" type="hidden" value="${ds.id}" />

							<div class="form-group">
								<label for="focusedinput" class="col-sm-2 control-label"
									style="width: 17%">包路径</label>
								<div class="col-sm-9">
									<input type="text" class="form-control1" id="dsName"
										name="name" value="${ds.name }" />
								</div>
							</div>

							<div class="form-group">
								<label for="focusedinput" class="col-sm-2 control-label"
									style="width: 17%">Mapper路径</label>
								<div class="col-sm-9">
									<input type="text" class="form-control1" id="dsUrl" name="url"
										value="${ds.url }" />
								</div>
							</div>

							<div class="form-group">
								<label for="selector1" class="col-sm-2 control-label"
									style="width: 17%">目标地址</label>
								<div class="col-sm-9">
									<input type="text" class="form-control1" id="dsDriverClass"
										name="driverClass" value="${ds.driverClass }" />
								</div>
							</div>

							<div class="form-group">
								<label for="focusedinput" class="col-sm-2 control-label"
									style="width: 17%">表前缀</label>
								<div class="col-sm-9">
									<input type="text" class="form-control1" id="dsUser"
										name="author" value="${ds.user }" />
								</div>
							</div>

							<div class="form-group">
								<label for="focusedinput" class="col-sm-2 control-label"
									style="width: 17%">作者</label>
								<div class="col-sm-9">
									<input type="text" class="form-control1" id="dsUser"
										name="user" value="${ds.user }" />
								</div>
							</div>

						</form>
					</div>
				</div>
			</div>
		</div>

	</div>
	<!-- 第二列 -->
	<div class="col-md-3 grid_box1">
		<!-- table list -->
		<div class="panel panel-warning"
			data-widget="{&quot;draggable&quot;: &quot;false&quot;}"
			data-widget-static="">
			<div class="panel-heading">
				<h2>表</h2>
				<div class="panel-ctrls" data-actions-container=""
					data-action-collapse="{&quot;target&quot;: &quot;.panel-body&quot;}">
					<span class="button-icon has-bg"><i class="ti ti-angle-down"></i></span>
				</div>
			</div>
			<div class="panel-body no-padding" style="display: block;">
				<div class="stats-info stats-info1 scrollbar">
					<table class="table table-bordered small">
						<thead>
							<tr>
								<th>表名</th>
								<th>表注释</th>
								<th>#</th>
							</tr>
						</thead>
						<tbody id="tableList">
							<c:forEach var="table" items="${schema.tables}">
								<tr class="tableRow">
									<td>${table.name }</td>
									<td title="${table.comment }">
										<c:choose>
											<c:when test="${fn:length(table.comment)>13}">${fn:substring(table.comment, 0, 10)}...</c:when>
											<c:otherwise>${table.comment }</c:otherwise>
										</c:choose>
									</td>
									<td><div class="text-success pull-right"><input type="checkbox"  data-table="${table.name }" checked="true"></div></td>
								</tr>							
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</div>

		<!-- column list -->
		<div class="panel panel-warning" data-widget="{&quot;draggable&quot;: &quot;false&quot;}" 	data-widget-static="">
			<div class="panel-heading">
				<h2>列</h2>
				<div class="panel-ctrls" data-actions-container=""
					data-action-collapse="{&quot;target&quot;: &quot;.panel-body&quot;}">
					<span class="button-icon has-bg"><i class="ti ti-angle-down"></i></span>
				</div>
			</div>
			<div class="panel-body no-padding" style="display: block;">
				<div class="table-responsive scrollbar">
					<table class="table table-bordered small">
						<thead>
							<tr>
								<th>列名</th>
								<th>列注释</th>
								<th>#</th>
							</tr>
						</thead>
						<tbody id="columnList">
							<c:forEach var="table" items="${schema.tables }" varStatus="tableStat">
								<c:forEach var="column" items="${table.columns }">
								<tr style="<c:choose><c:when test="${tableStat.index == 0}">display:</c:when><c:otherwise>display:none</c:otherwise></c:choose>" >
									<td>${column.name }</td>
									<td title="${column.comment }">
										<c:choose>
											<c:when test="${fn:length(column.comment)>13}">${fn:substring(column.comment, 0, 10)}...</c:when>
											<c:otherwise>${column.comment }</c:otherwise>
										</c:choose>
									</td>
									<td><div class="text-success pull-right"><input type="checkbox"  data-table="${table.name }" data-column="${column.name }"  checked="true"/></div></td>
								</tr>
								</c:forEach>
							</c:forEach>
						</tbody>
					</table>
				</div>
				<!-- /.table-responsive -->
			</div>
		</div>
	</div>

	<div class="col-md-6">
		<div class="panel panel-warning"
			data-widget="{&quot;draggable&quot;: &quot;false&quot;}"
			data-widget-static="">
			<div class="panel-heading">
				<h2>数据源列表</h2>
				<div class="panel-ctrls" data-actions-container="" data-action-collapse="{&quot;target&quot;: &quot;.panel-body&quot;}">
					<span class="button-icon has-bg"><i class="ti ti-angle-down"></i></span>
				</div>
			</div>
			<div class="panel-body no-padding" style="display: block;">
				<div class="col-md-12" style="height: 1024px">

					<div class="clearfix"></div>
				</div>

			</div>
		</div>
	</div>
	<div class="clearfix"></div>
</div>

<script type="text/javascript">
	(function($) {
		$(".tableRow").click(function(){
			var tableName = $(this).find(":checkbox").data("table");
			$("#columnList tr").hide().find(":checkbox[data-table='" + tableName + "']").closest("tr").show();
		});
		
		filterTable();
	})(jQuery);
</script>

