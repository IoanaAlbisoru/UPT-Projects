package game.gameplay;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import game.dispatcher.Event;

public class World {
  private static World ref = null;

  public static synchronized void init() {
    if (World.ref == null) {
      World.ref = new World();
      DispatcherSingleton.init();
    }
  }

  public static World get() {
    return World.ref;
  }

  private ConcurrentHashMap<String, PlayerData> nameDataMap = new ConcurrentHashMap<>();

  public boolean checkIfCanAttack(int attackerID) {
    // TODO Auto-generated method stub
    return true;

  }

  public void submitEvent(Event event) {
    try {
      DispatcherSingleton.dispatcher.addEvent(event);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  Village getVillageForID(int id) {
    for (PlayerData player : this.nameDataMap.values())
      for (Village v : player.villages) {
        if (v.ID == id) {
          return v;
        }
      }
    return null;
  }

  public void battle(int attackerID, int targetID, int troopsUsed) {
    Village attackerVillage = null;
    Village targetVillage = null;

    attackerVillage = getVillageForID(attackerID);
    targetVillage = getVillageForID(targetID);

    if (attackerVillage == null || targetVillage == null)
      return;

    //this fixes lock ordering deadlocks;
    if (attackerID > targetID) {
      targetVillage.lock();
      attackerVillage.lock();
    } else {
      attackerVillage.lock();
      targetVillage.lock();
    }

    if (targetVillage.numberOfTroops < troopsUsed) {
      int initialNumberOfTroops = targetVillage.numberOfTroops;
      targetVillage.numberOfTroops = 0;
      int diff = troopsUsed - targetVillage.numberOfTroops;
      int troopsLost = (int) (diff * 0.70);
      attackerVillage.numberOfTroops += (troopsUsed - troopsLost);
      int resourcesToSteal = diff / 3;

      int foodStolen;

      if (targetVillage.food < resourcesToSteal) {
        foodStolen = targetVillage.food;
        targetVillage.food = 0;
      } else {
        foodStolen = resourcesToSteal;
        targetVillage.food -= resourcesToSteal;
      }

      int ironStolen;
      if (targetVillage.iron < resourcesToSteal) {
        ironStolen = targetVillage.iron;
        targetVillage.iron = 0;
      } else {
        ironStolen = resourcesToSteal;
        targetVillage.iron -= resourcesToSteal;
      }

      int woodStolen;
      if (targetVillage.wood < resourcesToSteal) {
        woodStolen = targetVillage.wood;
        targetVillage.wood = 0;
      } else {
        woodStolen = resourcesToSteal;
        targetVillage.wood -= resourcesToSteal;
      }

      String[] message = new String[] { String.valueOf(attackerID), String.valueOf(targetID), String.valueOf(troopsUsed), String.valueOf(attackerID),
          String.valueOf(troopsLost), String.valueOf(initialNumberOfTroops), String.valueOf(foodStolen), String.valueOf(ironStolen), String.valueOf(woodStolen) };
      Event newEvent = new Event(EventHeaders.ATACK_FINISHED, message);
      this.submitEvent(newEvent);

    } else {
      int diff = targetVillage.numberOfTroops - troopsUsed;
      int troopsLost = (int) (diff * 0.70);
      targetVillage.numberOfTroops -= troopsLost;
      String[] message = new String[] { String.valueOf(attackerID), String.valueOf(targetID), String.valueOf(troopsUsed), String.valueOf(targetID),
          String.valueOf(troopsUsed), String.valueOf(troopsLost), String.valueOf(0), String.valueOf(0), String.valueOf(0) };
      Event newEvent = new Event(EventHeaders.ATACK_FINISHED, message);
      this.submitEvent(newEvent);
    }

    attackerVillage.unlock();
    targetVillage.unlock();
  }

  public int getDistance(int attackerID, int tagetID) {
    // TODO Auto-generated method stub
    return 5;
  }

  public void createFood() {
    // TODO Auto-generated method stub

  }

  public void createIron() {
    // TODO Auto-generated method stub

  }

  public void createWood() {
    // TODO Auto-generated method stub

  }

  PlayerData getPlayerData(String name) {
    return this.nameDataMap.get(name);
  }

  public int[] getPlayerVillageIDs(String name) {
    PlayerData data = this.getPlayerData(name);
    return data.returnIDS();
  }

  Executor playerExecutor = Executors.newFixedThreadPool(4);

  public Player createPlayer(String name) {
    final Player p = new Player(name);
    this.playerExecutor.execute(p);
    this.nameDataMap.putIfAbsent(name, new PlayerData(name));
    DispatcherSingleton.dispatcher.register(p.new PlayerFilter(), p);
    return p;
  }

  public void aid(int aiderID, int targetID, int troopsSent) {
    Village aiderVillage = null;
    Village targetVillage = null;

    for (PlayerData player : this.nameDataMap.values())
      for (Village v : player.villages) {
        v.lock();
        if (v.ID == aiderID) {
          aiderVillage = v;
          break;
        } else if (v.ID == targetID) {
          targetVillage = v;
          break;
        }
        v.unlock();
      }

    aiderVillage.numberOfForeignTroops += troopsSent;
    aiderVillage.unlock();
    targetVillage.unlock();

    String[] message = new String[2];
    message[0] = String.valueOf(aiderID);
    message[1] = String.valueOf(targetID);
    message[3] = String.valueOf(troopsSent);
    World.get().submitEvent(new Event(EventHeaders.AID_NOTIFICATION, message));
  }

  public boolean requestTroops(int requestorID, int numberOfTroops) {
    Village vToTrain = null;
    for (PlayerData player : this.nameDataMap.values())
      for (Village v : player.villages) {
        v.lock();
        if (v.ID == requestorID) {
          vToTrain = v;
          break;
        }
        v.unlock();
      }

    if (vToTrain == null)
      return false;

    if (vToTrain.iron < numberOfTroops * 5 || vToTrain.food < numberOfTroops * 7) {
      vToTrain.unlock();
      return false;
    }

    vToTrain.iron -= numberOfTroops * 5;
    vToTrain.food -= numberOfTroops * 7;
    vToTrain.unlock();
    return true;
  }

  public void addUnit(int requestorID) {
    Village vToTrain = null;
    for (PlayerData player : this.nameDataMap.values())
      for (Village v : player.villages) {
        v.lock();
        if (v.ID == requestorID) {
          vToTrain = v;
          break;
        }
        v.unlock();
      }

    if (vToTrain == null)
      return;

    vToTrain.numberOfTroops++;
    vToTrain.unlock();
    try {
      DispatcherSingleton.dispatcher.addEvent(new Event(EventHeaders.TROOP_TRAINED, new String[] { String.valueOf(requestorID) }));
    } catch (InterruptedException e) {
    }
    return;
  }

}
