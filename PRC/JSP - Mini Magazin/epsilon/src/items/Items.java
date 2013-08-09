package items;

public class Items {
	private static DummyItemRepository temp = new DummyItemRepository();

	public static ItemRepository getRepository() {
		return temp;
	}
}
