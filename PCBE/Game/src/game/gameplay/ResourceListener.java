package game.gameplay;

import game.dispatcher.Event;
import game.dispatcher.Filter;
import game.dispatcher.AbstractListener;

public class ResourceListener extends AbstractListener {

  @Override
  protected void processEvent(Event event) {
    switch (event.header) {
    case EventHeaders.FOOD_CREATION:
      World.get().createFood();
      break;
    case EventHeaders.IRON_CREATION:
      World.get().createIron();
      break;
    case EventHeaders.WOOD_CREATION:
      World.get().createWood();
      break;
    default:
      break;
    }
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

  public class ResourceListenerFilter implements Filter {

    @Override
    public boolean accept(Event m) {
      switch (m.header) {
      case EventHeaders.FOOD_CREATION:
        return true;
      case EventHeaders.IRON_CREATION:
        return true;
      case EventHeaders.WOOD_CREATION:
        return true;
      default:
        return false;
      }
    }
  }

}
