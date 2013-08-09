package user;

import java.util.concurrent.ConcurrentHashMap;

public class DummyUserRepository implements UserRepository {

	private ConcurrentHashMap<String, UserDetails> users = new ConcurrentHashMap<String, UserDetails>();

	public DummyUserRepository() {
		users.put("ion", new UserDetails("ion", "ion"));
		users.put("ass", new UserDetails("ass", "ass"));
	}

	@Override
	public UserDetails getUser(String username) {
		return this.users.get(username);
	}

	@Override
	public boolean contains(String username) {
		return this.users.containsKey(username);
	}

	@Override
	public boolean validate(String username, String password) {
		UserDetails temp = this.users.get(username);
		if (temp != null)
			return temp.getParola().equals(password);
		return false;

	}

	@Override
	public boolean addUser(UserDetails toAdd) {
		if (this.users.putIfAbsent(toAdd.getUsername(), toAdd) == null)
			return true;
		return false;
	}

}
