<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="items.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	String itemId = request.getParameter("id");
	Item it = Items.getRepository().getItem(itemId);
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><%=it.getName()%></title>
</head>
<body>
	<%=it.getName()%>
	<img src="<%=it.getImagePath()%>" />
	<%=it.getDesc()%>
	<textarea name="commentArea" rows="10" cols="20"></textarea>
	<ul>
		<%
			for (Comment com : it.getComments()) {
		%><li><%=com.getUserName()%>: <%=com.getComment()%></li>
		<%
			}
		%>
	</ul>

</body>
</html>