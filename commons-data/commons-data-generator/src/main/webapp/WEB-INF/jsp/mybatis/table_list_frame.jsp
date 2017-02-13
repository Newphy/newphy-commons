<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="../common.jsp"%>


	<div class="widget-box">
		<div class="widget-title">
			<span class="icon">
				<i class="icon-arrow-right"></i>
			</span>
			<h5>表</h5>
		</div>
		
		<div class="widget-content nopadding scrollbar">
			<table class="table table-bordered">
				<thead>
					<tr>
						<th style="width:20px; text-align: center"><input type="checkbox" id="allTable" checked/></th>
						<th>表名(table.name)</th>
						<th>对象名(entity.name)</th>
					</tr>
				</thead>
				<tbody id="tableList">
					<c:if test="${entities == null || fn:length(entities) == 0 }">
						<c:forEach begin="0" end="9">
						<tr>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
							<td>&nbsp;</td>
						</tr>
						</c:forEach>
					</c:if>
					<c:forEach var="entity" items="${entities}">
						<tr class="tableRow">
							<td  style="width:20px; text-align: center"><input type="checkbox"  data-table="${entity.table.name }" checked="true"></td>						
							<td title="${entity.table.comment }">${entity.table.name }</td>
							<td>${entity.name }</td>
						</tr>							
					</c:forEach>
				</tbody>
			</table>		
		</div>
	</div>
	
	<c:if test="${entities != null }">
	<div class="widget-box">
		<div class="widget-title">
			<span class="icon">
				<i class="icon-arrow-right"></i>
			</span>
			<h5>字段</h5>
		</div>
		<div class="widget-content nopadding scrollbar">
			<table class="table table-bordered table-striped">
				<thead>
					<tr>
						<th style="width:20px; text-align: center"><input type="checkbox" id="allColumn" checked/></th>					
						<th>列名(column.name)</th>
						<th>属性名(property.name)</th>
					</tr>
				</thead>
				<tbody id="columnList">
					<c:forEach var="entity" items="${entities}" varStatus="stat">
						<c:forEach var="property" items="${entity.properties }">
						<tr style="<c:choose><c:when test="${stat.index == 0}">display:</c:when><c:otherwise>display:none</c:otherwise></c:choose>" >
							<td  style="width:20px; text-align: center"><input type="checkbox"  data-table="${entity.table.name }" data-column="${property.column.name }"  checked="true"/></td>						
							<td title="${property.column.comment }">${property.column.name }</td>
							<td>${property.name}</td>
						</tr>
						</c:forEach>
					</c:forEach>
				</tbody>
			</table>		
		</div>
	</div>
	</c:if>

<script type="text/javascript">
	(function($) {
		$(".tableRow").click(function(){
			$(".tableRow").removeClass("alert-success");
			var tableName = $(this).addClass("alert-success").find(":checkbox").data("table");
			$("#columnList tr").hide().find(":checkbox[data-table='" + tableName + "']").closest("tr").show();
		});
		
		$("#allTable").click(function(){
			$("#tableList").find("tr:visible").find(":checkbox").attr("checked", $(this).is(":checked"));
		});
		
		$("#allColumn").click(function(){
			$("#columnList").find("tr:visible").find(":checkbox").attr("checked", $(this).is(":checked"));
		});

		filterTable();
	})(jQuery);
</script>

