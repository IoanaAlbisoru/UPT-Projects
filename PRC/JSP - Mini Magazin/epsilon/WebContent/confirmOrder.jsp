<%@page import="user.ShoppingCart"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="orders.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%
	if (null == session.getAttribute("username"))
		response.sendRedirect("login.jsp");

	ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
	Orders.getRepository().addOrder(
			new Order((String) session.getAttribute("username"), cart
					.toString()));
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	Order succesfully submited.
	<a href="Browse.jsp">Back To Browse</a>
</body>
</html>