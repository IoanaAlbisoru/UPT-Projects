<%@page import="user.ShoppingCart"%>
<%@ page errorPage="errorpage.jsp"
	import="items.Item,items.Items,items.ItemRepository"%>

<jsp:useBean id="cart" scope="session" class="user.ShoppingCart" />
<%
	if (session.getAttribute("username") == null) {
		response.sendRedirect("login.jsp");
	}
%>
<html>
<head>
<style>
a:visited {
	color: #0000FF;
}
</style>
<title>Gadget Catalog</title>
</head>
<body>
	<%
		String id = request.getParameter("id");
		String message = "";
		if (id != null) {
			//Double price = new Double(request.getParameter("price"));
			Item it = Items.getRepository().getItem(id);
			cart.addItem(it);
			message = String
					.format("Item '%s' added to cart", it.getName());
		}
	%>
	<a href="ShoppingCart.jsp">Shopping Cart Quantity:</a>
	<%=cart.getNumOfItems()%>
	<a href="logout.jsp">logout [<%=session.getAttribute("username")%>]
	</a>
	<hr>
	<center>
		<h3>Gadget Catalog</h3>
	</center>
	<%
		if (!message.equals("")) {
	%><div style="padding: 5px; border: 1px solid #666;"><%=message%></div>
	<%
		}
	%>
	<table border="1" width="300" cellspacing="0" cellpadding="2"
		align="center">
		<tr>
			<th>Name</th>
			<th>Price($)</th>
			<th>Photo</th>
		</tr>
		<%
			ItemRepository repo = Items.getRepository();

			for (Item it : repo.getItems()) {
		%>
		<tr>
			<form action="Browse.jsp" method="post">
				<td><%=it.getName()%></td>
				<td><%=it.getPrice()%></td>
				<td><a href="ItemDisplay.jsp?id=<%=it.getItemId()%>"><img
						src=<%=it.getImagePath()%> width="100" height="100"> </a>
				</td>
				<td><a href="Browse.jsp?id=<%=it.getItemId()%>">Add</a>
			</form>
		</tr>
		<%
			}
		%>
	</table>
</body>
</html>