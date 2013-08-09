package orders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class DummyOrderHistory implements OrderRepository {

	private ConcurrentHashMap<String, List<Order>> orders = new ConcurrentHashMap<String, List<Order>>();

	public DummyOrderHistory() {
		List<Order> temp = new ArrayList<Order>();
		temp.add(new Order("ion", "1 SONY PSP"));
		orders.put("ion", temp);
	}

	@Override
	public void addOrder(Order order) {
	}

	@Override
	public Collection<Order> userOrderHistory(String username) {
		return this.orders.get(username);
	}

}
