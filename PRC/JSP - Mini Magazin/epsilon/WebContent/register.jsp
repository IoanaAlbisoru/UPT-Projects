<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" import="user.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	String username = request.getParameter("username");
	String userpass = request.getParameter("userpass");
	String passconfirm = request.getParameter("passconfirm");

	String message = null;

	if (username != null && userpass != null) {

		if (!userpass.equals(passconfirm)) {
			message = "Passwords do not match";
		} else if (Users.getRepository().contains(username)) {
			message = String.format("User already taken: '%s'",
					username);
		} else {
			if (Users.getRepository().addUser(
					new UserDetails(username, userpass))) {
				message = String.format(
						"User '%s' succesfully registered", username);
			} else {
				message = String.format(
						"User '%s' could not be registered", username);
			}
		}
	}
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>User Registration</title>
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
	<%-- 
	<script type="text/javascript">
		function confirmpass(form) {
			if (form.userpass.value == "") {
				alert("Empty password field.");
				return false;
			} else if (form.userpass.value != form.confirmpass.value) {
				alert("Password fields do not match.");
				return false;
			}
			else return true;

		}
	</script> --%>

	<%
		if (message != null) {
	%><div style="padding: 5px; border: 1px solid #666;"><%=message%></div>
	<%
		}
	%>

	<form id="registeruser" action="register.jsp" method="post">
		<p>
			Username : <input type="text" name="username" />
		</p>
		<p>
			Password : <input type="password" name="userpass" />
		</p>
		<p>
			Confirm password : <input type="password" name="passconfirm" />
		</p>
		<p>
			<input type="submit" value="Register" name="registerformsubmit" />
		</p>
	</form>
	<a href=login.jsp>Back to login</a>

</body>
</html>