package user;

import items.Item;

import java.lang.String;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class ShoppingCart {
	private ConcurrentHashMap<String, Item> items = new ConcurrentHashMap<String, Item>();

	public void addItem(Item item) {
		items.putIfAbsent(item.getItemId(), item);
	}

	public void removeItem(String itemId) {
		items.remove(itemId);
	}

	public void updateQuantity(String itemId, int quantity) {
		if (items.contains(itemId)) {
			this.items.get(itemId).setStockQuantity(quantity);
		}
	}

	// public void addQuantity(String itemId, int offset) {
	// if (items.contains(itemId)) {
	// this.items.get(itemId).addOffset(offset);
	// }
	// }

	public synchronized Collection<Item> getItems() {
		return this.items.values();
	}

	public double getCost() {

		double price = 0.0;
		for (Item i : this.items.values()) {
			price += i.getPrice();
		}
		return price;
	}

	public int getNumOfItems() {
		return this.items.size();
	}

	public int getNumberOfItems(String ID) {
		Item temp = this.items.get(ID);
		if (temp == null)
			return 0;
		return 1;
	}

	public String toString() {
		String temp = "";
		for (Item it : this.items.values()) {
			temp += it.getItemId() + "  " + it.getName() + "\n";
		}
		return temp;
	}
}
