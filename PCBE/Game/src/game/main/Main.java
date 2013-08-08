package game.main;

import game.gameplay.Player;
import game.gameplay.World;

public class Main {
  public static void main(String args[]) {
    World.init();
    final World world = World.get();
    final Player p1 = world.createPlayer("p1");
    final Player p2 = world.createPlayer("p2");

    Thread controllerThread1 = new Thread() {
      @Override
      public void run() {
        try {
          int[] playerVillageIDs = world.getPlayerVillageIDs("p2");
          int toAttack = playerVillageIDs[0];
          int[] myVillageIDS = world.getPlayerVillageIDs("p1");
          p1.atackPlayer(toAttack, 10, myVillageIDS[0]);
          Thread.sleep(5000);
        } catch (InterruptedException e) {
        }
      }
    };
    Thread controllerThread2 = new Thread() {
      @Override
      public void run() {
        try {
          Thread.sleep(3000);
          int[] playerVillageIDs = world.getPlayerVillageIDs("p1");
          int[] myVillageIDS = world.getPlayerVillageIDs("p2");
          p2.trainTroops(10, myVillageIDS[0]);
          Thread.sleep(1000);
          int toAttack = playerVillageIDs[0];
          p2.atackPlayer(toAttack, 10, myVillageIDS[0]);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
        }
      }
    };

    controllerThread1.start();
    controllerThread2.start();
    try {
      Thread.sleep(10000000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
