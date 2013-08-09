package items;

import java.util.Collection;

public interface ItemRepository {
	public void addItem(Item item);

	public void removeItem(Item item);

	public boolean contains(Item item);

	public Item getItem(String ID);

	public Collection<Item> getItems();
}
