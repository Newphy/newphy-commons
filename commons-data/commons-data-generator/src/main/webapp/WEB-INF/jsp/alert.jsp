<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="common.jsp"%>

<c:if test="${alert.msg != null }">
	<c:choose>
		<c:when test="${alert.type == 'success' }">
			<c:set var="alertClass" value="alert-success" />
			<c:set var="alertTip" value="成功" />
		</c:when>
		<c:when test="${alert.type == 'info' }">
			<c:set var="alertClass" value="alert-info" />
			<c:set var="alertTip" value="信息" />
		</c:when>
		<c:when test="${alert.type == 'warn' }">
			<c:set var="alertClass" value="" />
			<c:set var="alertTip" value="警告" />
		</c:when>
		<c:when test="${alert.type == 'error' }">
			<c:set var="alertClass" value="alert-error" />
			<c:set var="alertTip" value="错误" />
		</c:when>				
	</c:choose>


	<div id="alertDiv" class="alert ${alertClass}">
		<button class="close" data-dismiss="alert">×</button>
		<strong>${alertTip }!</strong> ${alert.msg}
	</div>


<script type="text/javascript">
	(function($) {
		<c:choose>
		<c:when test="${alert.type == 'success' }">
			$("#alertDiv").fadeOut(3000);
		</c:when>
		<c:when test="${alert.type == 'info' }">
			$("#alertDiv").fadeOut(3000);
		</c:when>
		<c:when test="${alert.type == 'warn' }">
			$("#alertDiv").fadeOut(5000);
		</c:when>			
	</c:choose>
	})(jQuery);
</script>

</c:if>