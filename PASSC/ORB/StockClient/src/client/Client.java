package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import registry.Entry;
import remoting.SpanishCastleMagic;

import commonI.StockI;
import commonI.StockServerInterface;

public class Client {

  public static void main(final String args[]) {
    final StockServerInterface server = (StockServerInterface) SpanishCastleMagic.lookup("Stock", new Entry(
        "127.0.0.1", 2307));

    if (server == null) {
      System.out.println("Could not connect to server, exiting.");
      System.exit(0);
    }

    BufferedReader input = null;
    try {
      input = new BufferedReader(new InputStreamReader(System.in));
      String temp = "";

      while (!temp.equalsIgnoreCase("q")) {
        temp = input.readLine();
        if (temp.equalsIgnoreCase("min")) {
          final StockI stock = server.getLowest();
          System.out.println(String.format("Lowest Priced stock is: %s", stock.toString()));
        } else if (temp.equalsIgnoreCase("max")) {
          final StockI stock = server.getHighest();
          System.out.println(String.format("Highest Priced stock is: %s", stock.toString()));
        } else if (!temp.equalsIgnoreCase("q")) {
          final int price = server.getPrice(temp);
          if (price == -1) {
            System.out.println(String.format("No such stock: %s", temp));
          } else {
            System.out.println(String.format("Price for stock '%s' is: %d", temp, price));
          }
        }
      }
    } catch (final Exception e) {
      System.out.println(e.getMessage());
      return;
    }
  }
}
