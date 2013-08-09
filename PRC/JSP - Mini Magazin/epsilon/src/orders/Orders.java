package orders;

public class Orders {
	private static OrderRepository repo = new DummyOrderHistory();

	public static OrderRepository getRepository() {
		return Orders.repo;
	}
}
