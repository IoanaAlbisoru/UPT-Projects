package user;

public class Users {
	public static UserRepository repo = new DummyUserRepository();

	public static UserRepository getRepository() {
		return repo;
	}
}
