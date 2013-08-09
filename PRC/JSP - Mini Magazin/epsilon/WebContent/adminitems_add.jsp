<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="items.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	// if admin is not logged in redirect to login page

	String type = "edit";
	String id = (String) request.getAttribute("id");
	if (id == null)
		type = "add";
	else { /* get item */
	}

	// if page is submited 
	// daca am primit id => pagina era de tip editare,si tre sa faci update
	// daca nu ai id => pagina trebuie adaugata
	String name = (String) request.getAttribute("itemname");
	String priceStr = (String) request.getAttribute("itemprice");
	Double price = new Double(priceStr);
	String itemdesc = (String)request.getAttribute("itemdesc");

	// if page is not submited
	// daca am primit id => pagina e de tip editare
	//da ca nu ai id => pagina e de tip adaugare
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Item <%
	if (type == "add")
		out.println("add");
	else
		out.println("edit");
%>.</title>
</head>
<body>
	<form
		action="adminitems.jsp<%if (type == "edit")
				out.print("?id=" + id);%>"
		method="post">
		<input type="text" name="itemname"
			value="<%if (type == "edit")
				out.println( /* itemname */);%>" />
		<textarea name="itemdesc">
			<%
				if (type == "edit")
					out.println( /* item description */);
			%>
		</textarea>
		<input type="text" name="itemprice"
			value="<%if (type == "edit")
				out.println( /* itemprice */);%>" />
		<input type="file" name="itemimage"
			value="<%if (type == "edit")
				out.println( /* itemimagepath */);%>" />
		<input type="submit" name="addedit"
			value="<%if (type == "add")
				out.println("add");
			else
				out.println("edit");%>" />
	</form>
</body>
</html>