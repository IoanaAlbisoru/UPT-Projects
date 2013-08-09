package orders;

import java.util.concurrent.atomic.AtomicLong;

public class Order {
	private static AtomicLong ORDER_NUMBER = new AtomicLong(0);

	private final String username;
	private final long ordNumber;

	public Order(String userName, String order) {
		this.username = userName;
		ordNumber = ORDER_NUMBER.getAndIncrement();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Long) {
			return obj.equals(this.ordNumber);
		}
		if (obj instanceof Order)
			return this.ordNumber == ((Order) obj).ordNumber;
		return false;
	}

	public long getOrderNumber() {
		return this.ordNumber;
	}

	public String getUsername() {
		return username;
	}
}
