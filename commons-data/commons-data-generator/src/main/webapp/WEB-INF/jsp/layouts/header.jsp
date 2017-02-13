<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="../common.jsp"%>
    
<div id="header">
	<h1><a href="${_basePath}/">Newphy</a></h1>		
</div>

<div id="search">
	<input type="text" placeholder="Search here..." /><button type="submit" class="tip-right" title="Search"><i class="icon-search icon-white"></i></button>
</div>
<div id="user-nav" class="navbar navbar-inverse">
     <ul class="nav btn-group">
         <li class="btn btn-inverse"><a title="" href="#"><i class="icon icon-user"></i> <span class="text">Profile</span></a></li>
         <li class="btn btn-inverse dropdown" id="menu-messages"><a href="#" data-toggle="dropdown" data-target="#menu-messages" class="dropdown-toggle"><i class="icon icon-envelope"></i> <span class="text">Messages</span> <span class="label label-important">5</span> <b class="caret"></b></a>
             <ul class="dropdown-menu">
                 <li><a class="sAdd" title="" href="#">new message</a></li>
                 <li><a class="sInbox" title="" href="#">inbox</a></li>
                 <li><a class="sOutbox" title="" href="#">outbox</a></li>
                 <li><a class="sTrash" title="" href="#">trash</a></li>
             </ul>
         </li>
         <li class="btn btn-inverse"><a title="" href="#"><i class="icon icon-cog"></i> <span class="text">Settings</span></a></li>
         <li class="btn btn-inverse"><a title="" href="login.html"><i class="icon icon-share-alt"></i> <span class="text">Logout</span></a></li>
     </ul>
 </div>
        
        