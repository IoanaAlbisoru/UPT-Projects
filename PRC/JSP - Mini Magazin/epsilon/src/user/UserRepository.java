package user;

public interface UserRepository {

	public UserDetails getUser(String username);

	public boolean contains(String username);

	public boolean validate(String username, String password);
	
	public boolean addUser(UserDetails toAdd);
}
