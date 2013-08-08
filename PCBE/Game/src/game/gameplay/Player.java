package game.gameplay;

import game.dispatcher.Event;
import game.dispatcher.Filter;
import game.dispatcher.AbstractListener;

public class Player extends AbstractListener {

  private final String name;

  public Player(String name) {
    this.name = name;
  }

  @Override
  protected void processEvent(Event event) {
    // TODO Auto-generated method stub
    System.out.println(event.toString());
  }

  public void atackPlayer(int villageId, int noTroops, int myVillageId) {
    String[] message = new String[3];
    message[0] = String.valueOf(myVillageId);
    message[1] = String.valueOf(villageId);
    message[2] = String.valueOf(noTroops);
    World.get().submitEvent(new Event(EventHeaders.ATTACK_ATTEMPT, message));
  }

  public void aidPlayer(int villageID, int noTroops, int myVillageID) {
    String[] message = new String[2];
    message[0] = String.valueOf(myVillageID);
    message[1] = String.valueOf(villageID);
    message[3] = String.valueOf(noTroops);
    World.get().submitEvent(new Event(EventHeaders.AID, message));
  }

  public void buildVillage(Location location) {

  }

  public void trainPeasant(int noPeasants, String type, int villageID) {
    String[] message = new String[2];
    message[0] = String.valueOf(villageID);
    message[1] = String.valueOf(noPeasants);
    World.get().submitEvent(new Event(EventHeaders.PEASENT_TRAIN, message));
  }

  public void trainTroops(int noTroops, int villageID) {
    String[] message = new String[2];
    message[0] = String.valueOf(villageID);
    message[1] = String.valueOf(noTroops);
    World.get().submitEvent(new Event(EventHeaders.TROOP_TRAINING_ATTEMPT, message));
  }

  public void buildBuilding(int villageId, String type) {
    String[] message = new String[2];
    message[0] = String.valueOf(villageId);
    message[1] = type;
    World.get().submitEvent(new Event(EventHeaders.TROOP_TRAINING_ATTEMPT, message));
  }

  @Override
  public boolean equals(Object obj) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public int hashCode() {
    // TODO Auto-generated method stub
    return 0;
  }

  public class PlayerFilter implements Filter {

    @Override
    public boolean accept(Event m) {
      if (m.header == EventHeaders.ATTACK_ATTEMPT)
        return false;
      PlayerData playerData = World.get().getPlayerData(Player.this.name);
      for (Village v : playerData.villages)
        for (String s : m.message)
          if (String.valueOf(v.ID).equals(s))
            return true;

      return false;
    }

  }

}
