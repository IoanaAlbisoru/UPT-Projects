package game.gameplay;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import game.dispatcher.Event;
import game.dispatcher.Filter;
import game.dispatcher.AbstractListener;

public class TimeManager extends AbstractListener {
  Executor notifyExecutor = Executors.newCachedThreadPool();

  @Override
  protected void processEvent(Event event) {

    switch (event.header) {
    case EventHeaders.ATTACK_ATTEMPT: {

      final int attackerID = Integer.parseInt(event.message[0]);
      final int tagetID = Integer.parseInt(event.message[1]);
      final int troopsUsed = Integer.parseInt(event.message[2]);

      if (World.get().checkIfCanAttack(attackerID)) {
        final int time = World.get().getDistance(attackerID, tagetID) * 100;
        // TODO
        Event notifyAttack = new Event(EventHeaders.ATTACK_NOTIFICATION, new String[] { event.message[0], event.message[1], event.message[2],
            String.valueOf(time) });
        World.get().submitEvent(notifyAttack);
        this.notifyExecutor.execute(new Runnable() {
          @Override
          public void run() {
            try {
              Thread.sleep(time);
              World.get().battle(attackerID, tagetID, troopsUsed);
            } catch (InterruptedException ignore) {
            }
          }
        });
      }
      break;
    }
    case EventHeaders.AID: {
      final int aiderID = Integer.parseInt(event.message[0]);
      final int targetID = Integer.parseInt(event.message[1]);
      final int troopsSent = Integer.parseInt(event.message[2]);

      final int time = World.get().getDistance(aiderID, targetID) * 100;
      this.notifyExecutor.execute(new Runnable() {

        @Override
        public void run() {
          try {
            Thread.sleep(time);
            World.get().aid(aiderID, targetID, troopsSent);
          } catch (InterruptedException e) {
          }
        }
      });
      break;
    }
    case EventHeaders.TROOP_TRAINING_ATTEMPT: {
      final int requestorID = Integer.parseInt(event.message[0]);
      final int numberOfTroops = Integer.parseInt(event.message[1]);
      if (World.get().requestTroops(requestorID, numberOfTroops)) {
        final int time = numberOfTroops * 100;
        World.get().submitEvent(
            new Event(EventHeaders.TROOP_TRAINING_RESPONSE, new String[] { event.message[0], event.message[1], "success", String.valueOf(time) }));

        this.notifyExecutor.execute(new Runnable() {
          @Override
          public void run() {
            for (int i = 0; i < numberOfTroops; i++)
              try {
                Thread.sleep(100);
                World.get().addUnit(requestorID);
              } catch (InterruptedException e) {
                break;
              }
          }
        });

      } else
        World.get().submitEvent(
            new Event(EventHeaders.TROOP_TRAINING_RESPONSE, new String[] { event.message[0], event.message[1], "not enough resources", String.valueOf(0) }));
      break;
    }

    default:
      break;
    }
  }

  @Override
  public boolean equals(Object obj) {
    return false;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  public class TimeManagerFilter implements Filter {

    private final String[] acceptableHeaders = new String[] { EventHeaders.ATTACK_ATTEMPT, EventHeaders.AID, EventHeaders.TROOP_TRAINING_ATTEMPT };

    @Override
    public boolean accept(Event m) {
      for (String s : this.acceptableHeaders)
        if (m.header == s)
          return true;
      return false;
    }

  }

}
