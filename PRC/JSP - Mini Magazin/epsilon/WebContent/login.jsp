<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="user.*"%>

<%
	if(session.getAttribute("username") != null){
		response.sendRedirect("Browse.jsp");
	}
	String username = request.getParameter("username");
	String password = request.getParameter("userpass");
	UserRepository users = Users.getRepository();
	if (username != null && password != null)
		if (users.validate(username, password)) {
			session.setAttribute("username", username);
			response.sendRedirect("Browse.jsp");
		} else {
			out.print(" Wrong username / password ");
		}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<style>
form {
	margin-left: auto;
	margin-right: auto;
	width: 400px;
	padding: 5px;
	border: 1px solid #666;
}

form input {
	padding: 3px;
	border: 1px solid #999;
}
</style>
</head>
<body>

	<form id="loginform" action="login.jsp" method="post">
		<p>
			Username : <input type="text" name="username" value="" />
		</p>
		<p>
			Password : <input type="password" name="userpass" value="" />
		</p>
		<p>
			<input type="submit" value="Login" class="btn" />
		</p>
		<p>
			If you are not registered <a href="register.jsp">click here.</a>
		</p>
	</form>

</body>
</html>