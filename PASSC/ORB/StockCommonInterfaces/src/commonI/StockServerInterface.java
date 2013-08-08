package commonI;

import remoting.RemoteInterface;

public interface StockServerInterface extends RemoteInterface {
  int getPrice(String stockName);

  StockI getLowest();

  StockI getHighest();
}
