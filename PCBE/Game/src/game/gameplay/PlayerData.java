package game.gameplay;

import java.util.ArrayList;
import java.util.Collection;

class PlayerData {
  final String name;
  final Collection<Village> villages;
  boolean canAttack;

  PlayerData(String name) {
    this.name = name;
    this.villages = new ArrayList<Village>();
    this.villages.add(new Village(null));
    this.canAttack = true;
  }

  int[] returnIDS() {
    int[] temp = new int[this.villages.size()];
    int i = 0;
    for (Village v : this.villages)
      temp[i++] = v.ID;
    return temp;
  }

}
