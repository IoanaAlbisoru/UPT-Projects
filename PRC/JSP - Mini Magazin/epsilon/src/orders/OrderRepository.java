package orders;

import java.util.Collection;

public interface OrderRepository {
	public void addOrder(Order order);
	public Collection<Order> userOrderHistory(String username);
}
