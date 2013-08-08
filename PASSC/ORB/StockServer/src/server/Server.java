package server;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import registry.Entry;
import remoting.SpanishCastleMagic;

import commonI.StockI;
import commonI.StockServerInterface;

public class Server implements StockServerInterface {

  private final ConcurrentHashMap<String, StockI> stocks;

  public Server() {
    this.stocks = new ConcurrentHashMap<String, StockI>();
    this.stocks.putIfAbsent("Stock1", new StockShare("Stock1"));
    this.stocks.putIfAbsent("Stock2", new StockShare("Stock2"));
    this.stocks.putIfAbsent("Stock3", new StockShare("Stock3"));
  }

  @Override
  public int getPrice(final String stockName) {
    final StockI stock = this.stocks.get(stockName);

    if (stock == null) {
      return -1;
    }

    return stock.price();
  }

  @Override
  public StockI getLowest() {
    StockI min = this.stocks.values().iterator().next();

    for (final StockI st : this.stocks.values()) {
      if (min.price() > st.price()) {
        min = st;
      }
    }

    return min;
  }

  @Override
  public StockI getHighest() {
    StockI max = this.stocks.values().iterator().next();

    for (final StockI st : this.stocks.values()) {
      if (max.price() < st.price()) {
        max = st;
      }
    }

    return max;
  }

  private static final Random rand = new Random();

  private void change() {
    for (final StockI st : this.stocks.values()) {
      // design fail;
      final StockShare share = (StockShare) st;
      share.changePrice(Server.rand);
    }
  }

  public static void main(final String args[]) {
    final Server server = new Server();
    SpanishCastleMagic.bind("Stock", server, new Entry("127.0.0.1", 2307));

    while (true) {
      try {
        Thread.sleep(500);
      } catch (final InterruptedException e) {
        return;
      }
      server.change();
    }
  }
}
