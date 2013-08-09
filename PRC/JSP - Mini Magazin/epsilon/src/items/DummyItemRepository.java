package items;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

class DummyItemRepository implements ItemRepository {

	private final ConcurrentHashMap<String, Item> items = new ConcurrentHashMap<String, Item>();

	DummyItemRepository() {
		// (String itemID, String name, String desc, String imagePath, double
		// price,
		// int quantity)
		Item temp = new Item("1", "Nintendo LALA", "Gaming stuff",
				"nintendo.jpg", 170, 1);
		temp.addComments(new Comment("ass", "zzzzzzzzzzzzzzzzzzzzzzzz"));
		temp.addComments(new Comment("ass", "aaaaaaaaaaaaaaaaaaaaaaaa"));
		this.items.put("1", temp);
		temp = new Item("2", "Sony PSP", "Gaming stuff2", "sony_psp.jpg", 190,
				1);
		temp.addComments(new Comment("ion", "lalal, load of crap"));
		this.items.put("2", temp);
		this.items.put("3", new Item("3", "Apple IPAD", "Multistuff",
				"ipad.jpg", 290, 1));
		this.items.put("4", new Item("4", "Laptop Sleeve", "sleeve",
				"laptop_sleeve.jpg", 100, 1));
		this.items.put("5", new Item("5", "Cup Warmer", "misc",
				"usb_cup_warmer.jpg", 120, 1));
	}

	public void writeToFile() {
		File file = new File("itemsByteFile.out");
		ObjectOutputStream output;
		try {
			output = new ObjectOutputStream(new FileOutputStream(file));

			for (Item it : this.items.values()) {
				try {
					output.writeObject(it);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			output.close();
		} catch (Exception e1) {
		}
	}

	public static void main() {
		DummyItemRepository temp = new DummyItemRepository();
		temp.writeToFile();
	}

	@Override
	public void addItem(Item item) {
		items.putIfAbsent(item.getItemId(), item);
	}

	@Override
	public void removeItem(Item item) {
		items.remove(item);
	}

	@Override
	public boolean contains(Item item) {
		return this.items.contains(item);
	}

	@Override
	public Item getItem(String ID) {
		return this.items.get(ID);
	}

	@Override
	public Collection<Item> getItems() {
		return this.items.values();
	}

}
