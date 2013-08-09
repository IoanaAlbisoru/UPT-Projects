<%@ page errorPage="errorpage.jsp"%>
<%@ page import="java.util.*,items.*"%>

<jsp:useBean id="cart" scope="session" class="user.ShoppingCart" />
<%
	if (null == session.getAttribute("username"))
		response.sendRedirect("login.jsp");

	String itemID = request.getParameter("id");
	if (itemID != null) {
		cart.removeItem(itemID);
		response.reset();
	}
%>
<html>
<head>
<title>Shopping Cart Contents</title>
</head>
<body>
	<center>
		<table width="300" border="1" cellspacing="0" cellpadding="2"
			border="0">
			<caption>
				<b>Shopping Cart Contents</b>
			</caption>
			<tr>
				<th>Name</th>
				<th>Price</th>
				<th>Quantity</th>

			</tr>
			<%
				Collection<Item> itemsInCart = cart.getItems();
				for (Item item : itemsInCart) {
			%>
			<tr>
				<form action="ShoppingCart.jsp" method="post">
					<td><%=item.getName()%></td>
					<td align="center">$<%=item.getPrice()%></td>
					<td align="center"><%=cart.getNumberOfItems(item.getItemId())%></td>
					<td><a href="ShoppingCart.jsp?id=<%=item.getItemId()%>">Remove</a>
				</form>
			</tr>
			<%
				}
			%>
			<tr>
				<td>Total sum</td>
				<td>
					<%
						out.print(cart.getCost());
					%>
				</td>
			</tr>
		</table>
		<a href="checkout.jsp">Checkout</a>
	</center>
	<a href="Browse.jsp">Back to Catalog</a>
</body>
</html>