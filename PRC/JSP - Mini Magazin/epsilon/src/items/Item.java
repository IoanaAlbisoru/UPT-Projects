package items;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Item implements Serializable {
	private static final long serialVersionUID = -6238651002074291050L;
	private final String itemId;
	private final String name;
	private String desc;
	private double price;

	private AtomicInteger stockQuantity = null;

	private String imagePath;

	private final ConcurrentLinkedQueue<Comment> comments = new ConcurrentLinkedQueue<Comment>();

	public Item(String itemID, String name, String desc, String imagePath,
			double price, int quantity) {
		this.itemId = itemID;
		this.name = name;
		this.desc = desc;
		this.price = price;
		this.imagePath = imagePath;
		this.stockQuantity = new AtomicInteger(1);
	}

	public String getDesc() {
		return desc;
	}

	public void addComments(Comment newCom) {
		this.comments.add(newCom);
	}

	public Collection<Comment> getComments() {
		return this.comments;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public int getStockQuantity() {
		return this.stockQuantity.get();
	}

	public void setStockQuantity(int quantity) {
		this.stockQuantity.set(quantity);
	}

	public int decStockQuantity(int offset) {
		return this.stockQuantity.addAndGet(-1 * offset);
	}

	public int addStockQuantity(int offset) {
		return this.stockQuantity.addAndGet(offset);
	}

	public String getItemId() {
		return itemId;
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof String)
			return this.itemId.equals(arg0);
		else if (arg0 instanceof Item) {
			return this.itemId.equals(((Item) arg0).itemId);
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getImagePath() {
		return imagePath;
	}
}
