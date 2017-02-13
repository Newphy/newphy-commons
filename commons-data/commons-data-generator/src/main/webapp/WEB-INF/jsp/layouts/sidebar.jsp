<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="../common.jsp"%>
    
<div id="sidebar">
	<c:forEach var="menu" items="${menuList.menus }">
		<c:if test="${menu.code == activeMenu.code }">
			<a href="#" class="visible-phone"><i class="icon ${menu.icon}"></i> ${menu.name }</a>
		</c:if>
		<c:if test="${not empty menu.submenus }">
			<c:forEach var="submenu" items="${menu.submenus }">
					<c:if test="${submenu.code == activeMenu.code }">
						<a href="#" class="visible-phone"><i class="icon ${submenu.icon}"></i> ${submenu.name }</a>
					</c:if>
			</c:forEach>
		</c:if>
	</c:forEach>
	<ul>
		<c:forEach var="menu" items="${menuList.menus }">
			<c:choose>
				<c:when test="${not empty menu.submenus}">
					<li class="submenu <c:if test="${menu.code == activeMenu.parent.code }">active open</c:if>">
						<a href="#"><i class="icon ${menu.icon }"></i> <span>${menu.name }</span><span class="label">${fn:length(menu.submenus) }</span></a>
						<ul>
							<c:forEach var="submenu" items="${menu.submenus }">
								<li<c:if test="${submenu.code == activeMenu.code }"> class="active"</c:if>><a href="${submenu.url }">${submenu.name }</a></li>
							</c:forEach>
						</ul>
					</li>
				</c:when>
				<c:otherwise>
					<li<c:if test="${menu.code == activeMenu.code }"> class="active"</c:if>><a href="${_basePath}${menu.url}"><i class="icon ${menu.icon}"></i>${menu.name}</a></li>
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</ul>
</div>
        