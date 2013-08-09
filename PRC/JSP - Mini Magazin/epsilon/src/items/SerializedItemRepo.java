package items;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class SerializedItemRepo implements ItemRepository {

	private static SerializedItemRepo instance = null;

	public static SerializedItemRepo getInstance() {

		if (instance == null) {
			try {
				instance = new SerializedItemRepo();
			} catch (Exception e) {
			}
			return instance;
		}

		return instance;
	}

	private final String FILE_NAME = "d:/Workspace/Dropbox/Workspace/School/PRC/PRC_JSP/Prc/WebContent/itemsByteFile.out";
	// private final String FILE_NAME = "WebContent/itemsByteFile.out";
	// private final ObjectOutputStream output;
	// private final ObjectInputStream input;
	private ConcurrentHashMap<String, Item> items = new ConcurrentHashMap<String, Item>();

	private SerializedItemRepo() throws Exception {
		loadFromFile();
	}

	@Override
	public void addItem(Item item) {
		this.items.putIfAbsent(item.getItemId(), item);
	}

	@Override
	public void removeItem(Item item) {
		this.items.remove(item);
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

	private void loadFromFile() {

		try {
			File file = new File(FILE_NAME);
			ObjectInputStream input = new ObjectInputStream(
					new FileInputStream(file));

			while (true) {
				try {
					Item toAdd = (Item) input.readObject();
					this.items.putIfAbsent(toAdd.getItemId(), toAdd);
				} catch (EOFException e) {
					input.close();
					return;
				} catch (Exception ignore) {
				}
			}
		} catch (Exception e) {
		}
	}

	private void writeToFile() {
		File file = new File(FILE_NAME);
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

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		writeToFile();
	}

}
