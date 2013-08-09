<%@page import="user.ShoppingCart"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="items.*,user.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Checkout</title>
</head>
<body>

	<ul>
		<%
			if (null == session.getAttribute("username"))
				response.sendRedirect("login.jsp");

			ShoppingCart cart = (ShoppingCart) session.getAttribute("cart");
			double totalPrice = 0;
			for (Item it : cart.getItems()) {
				totalPrice += it.getPrice();
		%><li><%=it.getName()%> qt:<%=cart.getNumberOfItems(it.getItemId())%></li>
		<%
			}
		%><li>Total Price: <%=totalPrice%> <a href="confirmOrder.jsp">Confirm
				Order</a></li>
	</ul>

</body>
</html>