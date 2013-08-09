package user;

import java.io.Serializable;

public class UserDetails implements Serializable {

	private static final long serialVersionUID = -2299343721721869971L;
	String parola;
	String username;

	public UserDetails(String username, String parola) {

		this.username = username;
		this.parola = parola;
	}

	public String getParola() {
		return parola;
	}

	public void setParola(String parola) {
		this.parola = parola.trim();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username.trim();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UserDetails) {
			UserDetails temp = (UserDetails) obj;
			return temp.parola.equals(this.parola)
					&& temp.username.equals(this.username);
		}
		return false;
	}
}