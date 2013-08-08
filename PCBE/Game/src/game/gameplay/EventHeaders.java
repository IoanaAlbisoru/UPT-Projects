package game.gameplay;

public class EventHeaders {
  /**
   * @note 0 = attacker Village
   * @note 1 = defender Village
   * @note 2 = number of troops used
   */
  public final static String ATTACK_ATTEMPT = "ATTACK ATEMPT";

  /**
   * @note 0 = attacker Village
   * @note 1 = defender Village
   * @note 2 = number of troops used
   * @note 3 = time till arrival
   */
  public final static String ATTACK_NOTIFICATION = "Attack";

  /**
   * @note 0 = aiding village
   * @note 1 = village to be aided
   * @note 2 = troops sent
   */
  public final static String AID = "Aid";

  /**
   * @note 0 = aiding village
   * @note 1 = village to be aided
   * @note 2 = troops sent
   * 
   */
  public final static String AID_NOTIFICATION = "Aid Notification";

  /**
   * @note 0 = attacker Village
   * @note 1 = defender Village
   * @note 2 = number of troops used
   * @note 3 = winner
   * @note 4 = number of troops attacker lost
   * @note 5 = number of troops defender lost
   * @note 6 = food stolen
   * @note 7 = iron stolen
   * @note 8 = wood stolen
   */
  public final static String ATACK_FINISHED = "Attack finished";

  /**
   * @note 0 = targetVillage
   * @note 1 = numberOfTroopsToTrain
   */
  public final static String TROOP_TRAINING_ATTEMPT = "Training Troops Attempt";

  /**
   * @note 0 = targetVillage
   * @note 1 = numberOfTroopsToTrain
   * @note 2 = success;
   * @note 3 = time;
   */
  public final static String TROOP_TRAINING_RESPONSE = "Trainer";

  /**
   * @note 0 = village trained in
   */
  public final static String TROOP_TRAINED = "Troop has been trained";

  /**
   * @note 0 = village ID
   * @note 1 = building Type
   */
  public final static String BUILD = "Building motherfucker, do you speak it!?";
  
  /**
   *  @note 1 = village ID
   *  @note 2 = number of peasant
   *  @note 3 = type of peasant 
   */
  public final static String PEASENT_TRAIN = "Ready to work";
  

  public final static String WOOD_CREATION = "Wood";
  public final static String FOOD_CREATION = "FOOD";
  public final static String IRON_CREATION = "IRON";
}
