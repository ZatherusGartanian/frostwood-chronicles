package com.ZatherusGaming;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.view.View;
import java.util.ArrayList;
import java.util.Random;
/*
 * FROSTWOOD CHRONICLES GAME
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     December 2023
 * COURSE:   COMP486 - Mobile and Internet Game Development (Athabasca University)
 *
 * GameState
 * Description:
 * Main class used to manage the core game rules and mechanics. Keeps track of all the objects
 * and their status throughout the game, including the player character.
 *
 * Usage:
 * Once instantiated, the core logic will execute when calling update(). This should be called
 * every cycle. Specific methods that should be called from outside the class are more specifically
 * noted.
 *
 * Future Updates/Refactor:
 * Pretty happy with this class though I would want to clean up some of the methods. There is a
 * lot of back and forth handling data with the UserInterface. There is also just a lot of stuff
 * going on in this one class and I should consider if aspects of this class could be moved into
 * their own class. It is currently fun but I was starting to find it was getting a bit unwieldy
 * towards the end as I added new methods.
 */
public class GameState {
    public enum State{
        TILE, EVENT, EVENTOUTCOME, ACTIONMENU, TRAVEL, CAMPING, PRESENCE, GAMEOVER, STATS, LEVELUP
    }
    private State state;


    final private long TRAVEL_WAIT_TIME = 1000L; //Timer to prevent player from accidently returning to prior tile.
    final private int REST_HOURS = 5;
    final private int HOURLY_STAT_LOSS = 5;
    final private int TRAVEL_COST = 10; //Stamina Cost
    final private int TRAVEL_TIME = 30; //In minutes
    final private long CAMPING_WAIT_TIME = 3000L; //Will want to match the fade times to this.

    //Spawn variables for screen bounds to prevent resources from spawning on the map edge.
    //Set at launch based on screen sizes.
    final private int MAX_SPAWN_X;
    final private int MIN_SPAWN_X;
    final private int MAX_SPAWN_Y;
    final private int MIN_SPAWN_Y;

    //GameObject references
    private ArrayList<GameObject> gameObjects;
    private StaticObject fire;
    private StaticObject tent;
    private StaticObject food;
    private StaticObject water;
    private Player player;

    //Game Controllers
    private EventHandler events;
    private GameEvent currentEvent;
    private Map gameMap;
    private UserInterface userInterface;
    private BitmapHandler bitmapHandler;
    private GameManager manager;

    //In game time
    private int hours = 0;
    private int minutes = 0;
    private int days = 1; //Do not want to start on day 0. The first day is day 1.

    //Trackers
    private long travelStartTime = 0l;
    private int lastHourStatDecrease;
    private int lastDayUpdate = 1;
    private long campStartTime = 0l;
    private int campedHours;
    private int recoveredHealth;
    private int lastKnownHealth;
    private int nextLevelUpHour = 0;
    private int nextLevelUpDay = 1;
    private int lastPresenceHourCheck = 0;

    private StatsScreen statsScreen;

    private Presence presence;

    GameState(Context context, GameManager manager, BitmapHandler bitmapHandler, FrostwoodActivity activity, CollisionDetector collisionDetector){
        this.bitmapHandler = bitmapHandler;

        state = State.TILE;

        this.manager = manager;
        gameObjects = new ArrayList<GameObject>();
        gameMap = new Map(bitmapHandler);
        events = new EventHandler(context);

        initializeObjects();

        //MUST OCCUR AFTER OBJECT INSTANTIATION. Requires data from sprites to setup the spawn values
        this.userInterface = new UserInterface(bitmapHandler, activity, this);
        MIN_SPAWN_Y = userInterface.getBottomOfStatUI(); //Spawn below stat UI
        MIN_SPAWN_X = collisionDetector.BORDER_FORGIVENESS;

        MAX_SPAWN_X = bitmapHandler.getScreenWidth() - collisionDetector.BORDER_FORGIVENESS
                - water.getSprite().getWidth();

        MAX_SPAWN_Y = bitmapHandler.getScreenHeight() - collisionDetector.BORDER_FORGIVENESS
                - collisionDetector.BOTTOM_UI - water.getSprite().getHeight();
    }

    //Initialize the game objects
    private void initializeObjects(){
        Vector startingVector = new Vector(80, 80, 1);
        player = new Player(startingVector, bitmapHandler);
        player.setMaxVelocity(500);

        fire = new StaticObject(new Vector(0, 0, 0), "fire", 1,1, bitmapHandler);
        fire.setVisability(false);
        gameObjects.add(fire);
        tent = new StaticObject(new Vector(0, 0, 0), "tent", 1,1, bitmapHandler);
        tent.setVisability(false);
        gameObjects.add(tent);

        water = new StaticObject(new Vector(0, 0, 0), "drink", 1,1, bitmapHandler);
        water.setVisability(false);
        gameObjects.add(water);
        food = new StaticObject(new Vector(0, 0, 0), "food", 1,1, bitmapHandler);
        food.setVisability(false);
        gameObjects.add(food);

        presence = new Presence();
        currentEvent = events.getNewEvent(); //Not necessary but ensures non null

        //Initialize trackers
        lastHourStatDecrease = hours;
        lastKnownHealth = player.getHealth();
        statsScreen = new StatsScreen(player);
    }

    //Main update loop for the game
    public void update(long fps){
        //If game over skip the whole loop
        if(state == State.GAMEOVER){
            return;
        }
        //Update player position
        player.update(fps);

        //Check if an hour has passed to reduce stats. (Could maybe be moved to be called anytime time is updated).
        hourlyStatDecrease();

        //CheckPlayerHealth to update game state and trigger UI Health flash when health lost
        checkPlayerHealth();

        //If currently just on tile
        if(state == State.TILE) {
            if(checkForPresence()){
                return;
            }

            if(nextLevelUpHour <= hours && nextLevelUpDay == days){
                levelUp();
                return;
            }
            //Check if the player is currently standing on a viable exit from the tile.
            if (gameMap.travel(player.getCardinalPosition())) {
                //Start Travel state
                state = State.TRAVEL;

                //Remove not picked up foragables from the tile.
                water.setVisability(false);
                food.setVisability(false);

                //On travel clear all inputs (Press and Hold interrupt)
                manager.getInputController().clearInputs();

                //Decrease stamina and add travel time.
                player.decreaseStamina(TRAVEL_COST);
                userInterface.updateStatUI();
                addMinutes(TRAVEL_TIME);

                changeMaps();
                userInterface.updateArrowUI();

                //If the new tile has not been visited do event and display it
                if(gameMap.getCurrentTile().visited == false){
                    state = State.EVENT;

                    currentEvent = events.getNewEvent();
                    userInterface.loadEventUI(currentEvent);
                }
                //Otherwise randomize at 5% chance to spawn another event on return trip
                else{
                    Random random = new Random();
                    if(random.nextInt(100) < 5){
                        state = State.EVENT;

                        currentEvent = events.getNewEvent();
                        userInterface.loadEventUI(currentEvent);
                    }
                }
            }
        }

        //If the player is travelling check if timer is done to return to normal tile state.
        //Event outcomes always return to a travel state to make sure the travel time has passed.
        //Daily updates are ran here to avoid checking every loop since events (including camping)
        //and travel are the only way to advance time. May be over optimizing this call.
        else if(state == State.TRAVEL){
            if(System.currentTimeMillis() - travelStartTime > TRAVEL_WAIT_TIME){
               state = State.TILE;
            }
            dailyUpdates();
        }
        //If player is camping check for when camping timer is up and run camping outcome.
        else if(state == State.CAMPING){
            if(System.currentTimeMillis() - campStartTime > CAMPING_WAIT_TIME){
                campingOutcome();

                tent.setVisability(false);
                fire.setVisability(false);
            }
        }
    }

    //Check if the players health has changed and if so do the damage effect
    //Otherwise if players health is 0 end the game.
    public void checkPlayerHealth(){
        if(player.getHealth() != lastKnownHealth){
            if(player.getHealth() > lastKnownHealth){
                lastKnownHealth = player.getHealth();
            }
            else {
                userInterface.damageEffect();
                lastKnownHealth = player.getHealth();
            }
        }

        if(player.getHealth() <= 0){
            gameOver();
        }
    }

    //When the player moves between map tiles they need to flip to the other side of the screen
    //in order to walk in from the correct side. This flips the players XY coordinates.
    //Then sends the player walking to the center of the screen to avoid the player triggering
    //travel again without having moved. It also looks more natural.
    public void changeMaps(){
        travelStartTime = System.currentTimeMillis();

        //Flip character to the other side of the screen
        Compass currentSide = player.getCardinalPosition();
        player.updateCardinalPosition(Compass.NONE);

        float flippedY = player.getYPosition();
        float flippedX = player.getXPosition();

        if (currentSide == Compass.NORTH || currentSide == Compass.SOUTH) {
            flippedY = Math.abs(player.getYPosition() - bitmapHandler.getScreenHeight());
        } else if (currentSide == Compass.EAST || currentSide == Compass.WEST) {
            flippedX = Math.abs(player.getXPosition() - bitmapHandler.getScreenWidth());
        }

        Vector newLocation = new Vector(flippedX, flippedY, player.getZPosition());
        player.setWorldLocation(newLocation);

        float xPosition = (bitmapHandler.getScreenWidth() / 2);
        float yPosition = (bitmapHandler.getScreenHeight() / 2);

        player.moveToCoordinate(xPosition, yPosition);

        //After travelling reset the arrows to sync up
        userInterface.resetSpriteFrameCounter();
    }

    public boolean checkForPresence(){
        if(lastPresenceHourCheck == hours) {
            return false;
        }

        if(presence.attemptToSpawn()) {
            state = State.PRESENCE;
            userInterface.loadPresenceUI(presence.getEventText(), "Attack", "Retreat");
        }
        lastPresenceHourCheck = hours;

        return true;
    }

    public void presenceAction(View button){
        if (button.getId() == R.id.response1) {
            int damageDealt = 5 + (2 * player.getStrength());
            player.decreaseHealth(presence.HEALTH_DAMAGE);
            player.decreaseMaxHealth(presence.MAX_HEALTH_DAMAGE);

            presence.damage(damageDealt);
            if(presence.getHealth() <= 0){
                gameOver();
            }
            else{
                userInterface.loadPresenceUI(presence.getPostAttackText(damageDealt), "Attack", "Retreat");
            }

        } else if (button.getId() == R.id.response2) {
            player.decreaseHealth(presence.HEALTH_DAMAGE);
            player.decreaseMaxHealth(presence.MAX_HEALTH_DAMAGE);
            userInterface.loadOutcomeUI(presence.getRetreatText());
            presence.resetChance();
            userInterface.updatePresenceEffect(presence.getAlphaValue());
            state = State.EVENTOUTCOME;
        }

        userInterface.updateStatUI();
    }

    public void levelUp(){
        state = State.LEVELUP;
        statsScreen.resetStats();
        if(nextLevelUpHour == 12){
            statsScreen.setStatsToAssign(1);
            nextLevelUpHour = 0;
            nextLevelUpDay++;
        }
        else{
            statsScreen.setStatsToAssign(3);
            nextLevelUpHour = 12;
        }

        userInterface.showLevelUp(statsScreen);
    }

    public void levelUpScreen(View button){
        if (button.getId() == R.id.minusStrength){
            statsScreen.minusStrength();
        }
        else if (button.getId() == R.id.plusStrength){
            statsScreen.plusStrength();
        }
        else if (button.getId() == R.id.minusEndurance){
            statsScreen.minusEndurance();
        }
        else if (button.getId() == R.id.plusEndurance){
            statsScreen.plusEndurance();
        }
        else if (button.getId() == R.id.minusPerception){
            statsScreen.minusPerception();
        }
        else if (button.getId() == R.id.plusPerception){
            statsScreen.plusPerception();
        }
        else if (button.getId() == R.id.confirmStats){
            userInterface.completeLevelUp();

            player.increaseStrength(statsScreen.getStrength());
            player.increasePerception(statsScreen.getPerception());
            player.increaseEndurance(statsScreen.getEndurance());

            state = State.TILE;
            return;
        }

        userInterface.updateStats(statsScreen);
    }

    public void uiButtonPressed(View button){
        if(button.getId() == R.id.showStats){
            userInterface.showStats(player);
            state = State.STATS;
        }
    }

    public void statScreenGone(){
        state = State.TILE;
    }

    //When an event button is pressed resolve the response
    public void resolveEvent(View button){
        // Check which button was clicked based on view ID and send the outcome text to the UI.
        if (button.getId() == R.id.response1) {
            events.processEventOutcome(this, player, currentEvent.getButton1Outcome());
            userInterface.loadOutcomeUI(currentEvent.getOutcome1Text());
        } else if (button.getId() == R.id.response2) {
            // Handle deny button click
            events.processEventOutcome(this, player, currentEvent.getButton2Outcome());
            userInterface.loadOutcomeUI(currentEvent.getOutcome2Text());
        }
        //Update the Ui elements for stats and inventory since events can effect these
        userInterface.updateStatUI();
        userInterface.updateInventoryUI();

        //Switch to eventoutcome state and set the tile to visted
        state = State.EVENTOUTCOME;
        gameMap.getCurrentTile().visited = true;
    }

    //Change states after basic user input
    //Used by User Interface to prompt game state changes from the input controller
    public void outcomeConfirmed(){state = State.TILE;}
    public void actionBarDisplayed(){state = State.ACTIONMENU;}
    public void actionBarGone(){state = State.TILE;}

    //Trigger the appropriate action based on which button was pressed
    public void actionBarEvent(View button){
        if (button.getId() == R.id.campButton) {
            setUpCamp();
        }

        else if (button.getId() == R.id.eatButton) {
            eatFood();
        }

        else if(button.getId() == R.id.drinkButton){
            drinkWater();
        }

        else if (button.getId() == R.id.forageButton) {
            forage();
        }
    }

    //Set up camp action.
    public void setUpCamp(){
        //Prep variables
        campedHours = 0;
        recoveredHealth = 0;
        int preCampHealth = player.getHealth();

        //If the player has no mana jump to outcome and end. Player cannot camp while thirsty.
        if(player.getMana() == 0){
            campingOutcome();
            return;
        }
        //Show the tent and fire sprites for the player
        Vector newLocation = new Vector(player.getXPosition() + player.getSprite().getWidth(), player.getYPosition(), 0);
        tent.setWorldLocation(newLocation);
        tent.setVisability(true);

        newLocation = new Vector(newLocation.getX()-40, newLocation.getY() + tent.getSprite().getHeight() + 10, 0);
        fire.setWorldLocation(newLocation);
        fire.setVisability(true);

        //Start the timer and trigger the fading effect visual
        campStartTime = System.currentTimeMillis();
        userInterface.campingFadeEffect();

        //Every hour check if the player still has mana to lose. If players mana hits 0 end sleep early.
        for(campedHours = 1; campedHours <= REST_HOURS; campedHours++){
            if(player.getMana() == 0) {
                break;
            }
            //Decrease mana every hour at half rate (Doubled when awake)
            //(Max stamina is not lost while resting)
            player.decreaseMana(HOURLY_STAT_LOSS);
            player.heal(5);
            player.rest(10);
            presence.getCloser();
        }
        //Camped horus is used for outcome text. Decrement to remove breakpoint check.
        campedHours--;

        //Get the amount healed from resting
        int postCampHealth = player.getHealth();
        recoveredHealth = postCampHealth - preCampHealth;

        //Add 60 minutes per camped hour
        addMinutes(campedHours*60); //Send as minutes

        //Update the stat decrease to the current hour to avoid double hitting mana.
        lastHourStatDecrease = hours;

        //Set state to camping to lock controls until camping timer is up
        state = State.CAMPING;
    }

    //Build a string based on how camping went and send to UI to display the outcome
    public void campingOutcome(){
        state = State.EVENTOUTCOME;
        String campingDetails;
        if(campedHours == 0){
            campingDetails = "You are too thirsty to recover from sleep. Drink some water or go find some!";
        }
        else {
            if (campedHours == 1) {
                campingDetails = "You were able to sleep for " + campedHours + " hour and regain some stamina.";
            } else {
                campingDetails = "You were able to sleep for " + campedHours + " hours and recover some stamina.";
            }

            if (recoveredHealth != 0) {
                campingDetails = campingDetails.concat(" You were also able to recover " + recoveredHealth +
                        " health.");
            }
        }

        userInterface.updateStatUI();
        userInterface.loadOutcomeUI(campingDetails);
    }

    //Eat Food Action.
    public void eatFood(){
        //If player has no food to eat
        if(player.getFoodCount() == 0){
            eatingOutcome(0);
            return;
        }
        //If player is already at max stamina and would see no benefit
        if(player.getMaxStamina() == player.MAX_STAMINA){
            eatingOutcome(-1); //Using -1 for unique case check
            return;
        }
        //Get how much stamina was recovered for outcome display
        int recoveredMaxStamina = 0;
        int preEatingStamina = player.getMaxStamina();
        player.eat();
        recoveredMaxStamina = player.getMaxStamina() - preEatingStamina;
        userInterface.updateInventoryUI(); //Update inventory for food loss

        //Display the outcome
        eatingOutcome(recoveredMaxStamina);
    }
    //Build outcome string based on results of eating and send to display.
    public void eatingOutcome(int recoveredMaxStamina){
        state = State.EVENTOUTCOME;
        String eatingOutcome;
        if(recoveredMaxStamina == -1){
            eatingOutcome = "You are too full to eat. You don't want to waste your food now do you?";
        }
        else if(recoveredMaxStamina == 0){
            eatingOutcome = "Your belly grumbles looking at your empty backpack. You have no food to eat. Go find some food!";
        }
        else{
            eatingOutcome = "You take a moment to monch on some food. Your stomach thanks you. You recovered " +
                    recoveredMaxStamina + " fatigue.";
        }

        userInterface.updateStatUI();
        userInterface.loadOutcomeUI(eatingOutcome);
    }
    //Same as eating but water(See above)
    public void drinkWater(){
        if(player.getWaterCount() == 0){
            drinkingOutcome(0);
            return;
        }
        if(player.getMana() == player.getMaxMana()){
            drinkingOutcome(-1); //Using -1 for unique case check
            return;
        }

        int recoveredMana = 0;
        int preDrinkingMana = player.getMana();
        player.drink();
        recoveredMana = player.getMana() - preDrinkingMana;
        userInterface.updateInventoryUI();

        drinkingOutcome(recoveredMana);
    }
    public void drinkingOutcome(int recoveredMana){
        state = State.EVENTOUTCOME;
        String drinkingOutcome;

        if(recoveredMana == -1){
            drinkingOutcome = "You are not thirsty. You need to preserve every drop of water you have.";
        }
        else if(recoveredMana == 0){
            drinkingOutcome = "You frantically search through your bag looking for something to quench your thirst. " +
                    "You feel sandpaper against your throat as you realize you have no more water. " +
                    "You must find something to drink or you will die.";
        }
        else{
            drinkingOutcome = "The cool water revitalizes you with each sip. You take a moment to enjoy this. " +
                    "When the final drop hits your lips you notice you feel stronger. You recovered " + recoveredMana +
                    " hydration.";
        }

        userInterface.updateStatUI();
        userInterface.loadOutcomeUI(drinkingOutcome);
    }

    //Forage Player Action
    public void forage(){
        //Try and spawn resources. 0 = No spawn, 1 = water, 2 = food.
        //Used for the forage outcome string display
        int forageResult = spawnResources();

        //Add an hour for foraging.
        addMinutes(60);
        forageOutcome(forageResult);
    }

    //Display a string based on the foraging results.
    public void forageOutcome(int forageResult){
        state = State.EVENTOUTCOME;
        String forageOutcome;
        if (forageResult == 0) {
            forageOutcome = "You scrounge through the brush looking for any sign of food or water. " +
                    "An hour passes with no luck.";
        }
        else if(forageResult == 1){
            forageOutcome = "You start searching. An hour passes. You are about to give up hope when out of the corner of " +
                    "your eye you catch a glint of light reflecting off a bottle. What are you waiting for? " +
                    "Go get it!";
        }
        //forageResult == 2 Update to else if with added foragables.
        else {
            forageOutcome = "You spot some bunny tracks in the snow and set out to hunt your prey. After some time, " +
                    "you spot a bunny cleaning itself under a shrub. You take aim with your bow and fire. Dinner is served. " +
                    "Go pick up your reward. You earned it.";
        }

        userInterface.loadOutcomeUI(forageOutcome);
    }

    public int spawnResources(){
        int spawnResult = 0;
        if(gameMap.getCurrentTile().spawnResourceAttempt(player.getPerception())){
            Random random = new Random();
            int resourceType = random.nextInt(3);

            //Spawn the resource within the bounds of the tile taking its collision border into account
            int spawnX = random.nextInt(MAX_SPAWN_X - MIN_SPAWN_X) + MIN_SPAWN_X;
            int spawnY = random.nextInt(MAX_SPAWN_Y - MIN_SPAWN_Y) + MIN_SPAWN_Y;

            //Set the position of water to get the update hitbox location to check if the spawn location
            //intersects the player
            water.setWorldLocation(new Vector(spawnX, spawnY, 0));

            //Check if spawn was on top of the player
            Rect resourceHitbox = new Rect(water.getHitBox());
            Rect playerHitbox =  new Rect(player.getHitBox());
            if(playerHitbox.intersect(resourceHitbox)){
                //Adjust spawn coordinates to shift away from player based on their screen position.
                if(player.getXPosition() < bitmapHandler.getScreenWidth()/2){
                    spawnX = (int)player.getXPosition() + player.getSprite().getWidth()+30;
                }
                else
                    spawnX = (int)player.getXPosition() - water.getSprite().getWidth()-30;

                if(player.getYPosition() < bitmapHandler.getScreenHeight()/2){
                    spawnY = (int)player.getYPosition() + player.getSprite().getHeight()+30;
                }
                else
                    spawnY = (int)player.getYPosition() - water.getSprite().getHeight()-30;
            }

            if(resourceType < 2){
                //Spawn water
                water.setWorldLocation(new Vector(spawnX, spawnY, 0));
                water.setVisability(true);
                spawnResult = 1;
            }
            else{
                //Spawn food
                food.setWorldLocation(new Vector(spawnX, spawnY, 0));
                food.setVisability(true);
                spawnResult = 2;
            }

        }
        return spawnResult;
    }

    //Game Over Triggered
    //Fades screen to black and constructs a survival report to be displayed.
    public void gameOver(){
        state = State.GAMEOVER;
        days--;//Remove starting day at 1 to get time survived
        //Save data and check if data is a new record
        boolean isNewRecord = manager.getActivity().saveSurvivalTime(days, hours, minutes);
        userInterface.gameOverFade();
        String gameOverReport;

        if(presence.getHealth() <= 0){
            gameOverReport = "You defeated the mystical presence in the woods. You have overcome the " +
                    "Frostwood. Congratulations!";
        }
        else {
            gameOverReport = "You collapse to the snow, unable to go on. With one last strained breath " +
                    "your body goes limp.";
        }


        gameOverReport = gameOverReport.concat("\n\nSurvival Time:");
        if(days != 0){
            gameOverReport = gameOverReport.concat("\n" + days);
            if(days == 1){
                gameOverReport = gameOverReport.concat(" day ");
            }
            else
                gameOverReport = gameOverReport.concat(" days ");
        }

        if(hours != 0){
            gameOverReport = gameOverReport.concat("\n" + hours);
            if(hours == 1){
                gameOverReport = gameOverReport.concat(" hour");
            }
            else{
                gameOverReport = gameOverReport.concat(" hours");
            }
        }
        if(minutes != 0) {
            gameOverReport = gameOverReport.concat("\n" + minutes + " minutes");
        }
        if(isNewRecord){
            gameOverReport = gameOverReport.concat("\n\nThis is the longest you have survived. " +
                    "Congratulations!");
        }

        if(presence.getHealth() > 0){
            gameOverReport = gameOverReport.concat("\n\nThe presence had " + presence.getHealth() + " health remaining.");
        }

        userInterface.loadOutcomeUI(gameOverReport);
    }

    //Decrease stats every hour
    public void hourlyStatDecrease(){
        if(hours == lastHourStatDecrease){
            return;
        }
        int hoursPast = hours - lastHourStatDecrease;
        player.decreaseMana(hoursPast*HOURLY_STAT_LOSS*2);
        player.decreaseMaxStamina(hoursPast*HOURLY_STAT_LOSS);

        for(int i = 0; i < hoursPast; i++) {
            presence.getCloser();
        }

        lastHourStatDecrease = hours;
        userInterface.updateStatUI();
        userInterface.updatePresenceEffect(presence.getAlphaValue());
    }

    //Every day update the tile resources and display a screen prompt
    public void dailyUpdates(){
        if(lastDayUpdate == days){
            return;
        }
        gameMap.dailyResourceGrowth();
        lastDayUpdate = days;
        String newDay = "You have survived to see another day.";
        state = State.EVENTOUTCOME;
        userInterface.loadOutcomeUI(newDay);
    }

    //Time math to update the in game time. Adds minutes and shifts to hours and days where needed.
    public void addMinutes(int addedMinutes){
        minutes += addedMinutes;
        int shiftHours = minutes/60;
        minutes = minutes%60;

        hours += shiftHours;
        int shiftDay = hours/24;
        hours = hours%24;

        days += shiftDay;

        userInterface.updateClock();
    }

    //GETTERS
    public Map getGameMap() {
        return gameMap;
    }
    public ArrayList<GameObject> getGameObjects(){
        return gameObjects;
    }
    public Player getPlayer(){
        return player;
    }
    public UserInterface getUserInterface() {
        return userInterface;
    }
    public State getState(){
        return state;
    }

    public int getHours(){return hours;}
    public int getMinutes(){return minutes;}
    public int getDays(){return days;}
}
