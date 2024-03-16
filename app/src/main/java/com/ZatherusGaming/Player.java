package com.ZatherusGaming;
import android.graphics.Bitmap;
import java.util.Random;
/*
 * FROSTWOOD CHRONICLES GAME
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     December 2023
 * COURSE:   COMP486 - Mobile and Internet Game Development (Athabasca University)
 *
 * Player
 * Description:
 * Extends the Actor class (GameObject) and represents the player character. Primarily adds
 * information for stats (Health, Stamina, Mana). Overrides the getCurrentSprite() method to
 * handle its animation states.
 *
 * Usage:
 * After creation use the associated methods to increase and decrease the players stats. The rest
 * is internally run. Player will switch between two idle animations when not moving for a bit of
 * polish flair. Unique methods are more specifically discussed.
 *
 * Future Updates/Refactor:
 * I would like to find a better solution for animation states as more objects could also have
 * multiple animations like a player.
 */

public class Player extends Actor{
    public enum AnimationState{
        IDLESPIN, NONE
    }

    private AnimationState animationState;

    //Stat values for player.
    public final int MAX_HEALTH = 100;
    public final int MAX_MANA = 100;
    public final int MAX_STAMINA = 100;
    private int maxHealth;
    private int maxStamina;
    private int maxMana;
    private int currentStamina;
    private int currentHealth;
    private int currentMana;

    //Resource Trackers
    private int foodCount;
    private int waterCount;

    //Player position on tile (Used to check if player is on a viable exit)
    private Compass cardinalPosition;

    //Sprites
    private Sprite walk;
    private Sprite idleSpin;
    private Sprite currentSpriteState; //Used to track the current Sprite to be drawing

    //Tracker for idle animation looping rather then only playing through once.
    final private int IDLE_SPIN_PLAY_COUNT = 4;
    private int currentIdleSpinCounter = 0;

    Player(Vector location, BitmapHandler bitmapHandler){
        //Basic idle animation is stored as standard sprite for gameObject
        super(location, "idle", 6, 10, bitmapHandler);

        //Initialize stats
        cardinalPosition = Compass.NONE;
        maxHealth = MAX_HEALTH;
        currentHealth = MAX_HEALTH;
        maxMana = MAX_MANA;
        currentMana = MAX_MANA;
        maxStamina = MAX_STAMINA;
        currentStamina = MAX_STAMINA;

        //Initialize state
        animationState = AnimationState.NONE;

        //Load additional sprites
        walk = bitmapHandler.createSprite("walk", 8, 10);
        idleSpin = bitmapHandler.createSprite("idlespin", 4, 10);
        idleSpin.setLooping(false); //IdleSpin is not a looping animation

        //Starting resources (Update to final variables for easier changes)
        foodCount = 2;
        waterCount = 3;
    }


    //Get the current sprite for the character based on the current state of the player
    public Bitmap getCurrentSprite(long time){
        //If the player is idle add flair with a randomized second idle sprite
        if(animationState == AnimationState.NONE && getActorState() == ActorState.IDLE){
            //Only swap if the current animation is completed
            if(getSprite().getCurrentFrame() == 0) {
                //Check for 5% chance to switch idle animation
                Random random = new Random();
                int randomNumber = random.nextInt(100);
                if (randomNumber < 5) {
                    //Start idlespin
                    animationState = AnimationState.IDLESPIN;
                    idleSpin.startAnimation();
                }
            }
        }

        //Plays the idlespin animation through a set number of plays (See final variable) and then
        //Switches back to normal idle animation.
        if(animationState == AnimationState.IDLESPIN && idleSpin.isAnimationComplete() ){
            if(currentIdleSpinCounter == IDLE_SPIN_PLAY_COUNT){
                animationState = AnimationState.NONE;
                currentIdleSpinCounter = 0;
            }
            else{
                currentIdleSpinCounter++;
                idleSpin.startAnimation();
            }
        }

        //Set the current sprite state for animating and return the current frame.
        switch(getActorState()){
            case IDLE:
                //If idle and not doing idle spin flair
                if(animationState == AnimationState.NONE){
                    //Get the basic idle sprite
                    currentSpriteState = getSprite();
                }
                //If Idle and spinning
                else if(animationState == AnimationState.IDLESPIN){
                    //Get the spinning sprite
                    currentSpriteState = idleSpin;
                }
                break;

            case MOVING:
                //Get the moving sprite when moving
                currentSpriteState = walk;
                break;

            default:
                //For safety return the idle sprite.
                currentSpriteState = getSprite();
                break;
        }

        //Whichever sprite was set get the currentsprites animation frame as usual
        return currentSpriteState.getCurrentSprite(time);
    }

    //METHODS FOR INCREASING AND DECREASING PLAYER STATS
    //Min 0 and max of their associated maxs.

    //Health Methods
    public void decreaseHealth(int amount) {
        if(amount < 0){
            return;
        }
        currentHealth -= amount;

        if (currentHealth < 0) {
            currentHealth = 0;
        }
    }
    public void heal(int amount){
        currentHealth += amount;
        if(currentHealth > maxHealth){
            currentHealth = maxHealth;
        }
    }

    //Mana Methods
    public void decreaseMana(int amount) {
        if(amount < 0){
            return;
        }

        currentMana -= amount;

        if (currentMana < 0) {
            int overflowAmount = currentMana * -1;
            currentMana = 0;
            decreaseMaxHealth(overflowAmount);
        }
    }
    public void increaseMana(int amount){
        currentMana += amount;
        if (currentMana > maxMana){
            currentMana = maxMana;
        }
    }

    //STAMINA METHODS
    public void decreaseStamina(int amount) {
        if(amount < 0){
            return;
        }
        currentStamina -= amount;

        if (currentStamina < 0) {
            int overflowAmount = currentStamina*-1;
            currentStamina = 0;
            decreaseHealth(overflowAmount);
        }
    }

    public void rest(int amount){
        currentStamina += amount;
        if(currentStamina > maxStamina){
            currentStamina = maxStamina;
        }
    }

    public void findFood(){
        foodCount++;
    }
    public void findWater() {waterCount++;}
    public void eat(){
        if(foodCount - 1 < 0){

        }
        else{
            foodCount--;
            increaseMaxStamina(25);
        }
    }
    public void drink(){
        if(waterCount - 1 < 0){

        }
        else{
            waterCount--;
            increaseMana(25);
        }
    }

    //FUTURE METHODS FOR CONTROLLING MAX MANA. CURRENTLY NOT RELEVENT TO THE GAME.
    //IN PLACE IN CASE EVENTS WANTED TO BE MADE TO EFFECT MAX MANA.
    public void decreaseMaxMana(int amount){
        if(amount < 0){
            return;
        }
        maxMana -= amount;
        if(maxMana < 0){
            maxMana = 0;
        }

        if(currentMana > maxMana){
            currentMana = maxMana;
        }
    }
    public void increaseMaxMana(int amount){
        maxMana += amount;

        if(maxMana > MAX_MANA){
            maxMana = MAX_MANA;
        }
    }

    //MaxStamina Methods
    public void decreaseMaxStamina(int amount){
        if(amount < 0){
            return;
        }

        maxStamina -= amount;

        if(maxStamina < 0){
            int overflowAmount = maxStamina * -1;
            maxStamina = 0;
            decreaseMaxHealth(overflowAmount);
        }
        if(currentStamina > maxStamina){
            currentStamina = maxStamina;
        }
    }
    public void increaseMaxStamina(int amount){
        maxStamina += amount;

        if (maxStamina > MAX_STAMINA){
            maxStamina = MAX_STAMINA;
        }
    }

    public void decreaseMaxHealth(int amount){
        if(amount < 0){
            return;
        }
        maxHealth -= amount;
        if(maxHealth < 0){
            maxHealth = 0;
        }

        if(currentHealth > maxHealth){
            currentHealth = maxHealth;
        }
    }
    public void increaseMaxHealth(int amount){
        maxHealth += amount;
        if(maxHealth > MAX_HEALTH){
            maxHealth = MAX_HEALTH;
        }
    }

    //FUTURE USE
    public void update(long fps){
        super.update(fps);
    }

    //Called by collision detector. Handle overlap based on the object type.
    //Add further interactions here with other sprites.
    public void onOverlap(GameObject overlapObject, GameState gameState){
        if(overlapObject.getName().equals("food")){
            findFood();
            overlapObject.setVisability(false);
            gameState.getUserInterface().updateInventoryUI();
        }
        else if(overlapObject.getName().equals("drink")){
            findWater();
            overlapObject.setVisability(false);
            gameState.getUserInterface().updateInventoryUI();
        }
    }


    //Getter and Setter for cardinal position
    public Compass getCardinalPosition(){ return cardinalPosition; }
    public void updateCardinalPosition(Compass newCardinalPosition){
        cardinalPosition = newCardinalPosition;
    }

    //GETTERS
    public int getFoodCount(){
        return foodCount;
    }
    public int getWaterCount(){
        return waterCount;
    }

    public int getHealth(){return currentHealth;}
    public int getMaxHealth() {return maxHealth;}
    public float getHealthPercent(){return (float) currentHealth/MAX_HEALTH;}
    public float getMaxHealthPercent() {return (float) maxHealth/MAX_HEALTH;}

    public int getMana() {return currentMana; }
    public int getMaxMana() {return maxMana;}
    public float getManaPercent(){
        return (float) currentMana/MAX_MANA;
    }
    public float getMaxManaPercent(){
        return (float) maxMana/MAX_MANA;
    }

    public int getStamina() {return currentStamina; }
    public int getMaxStamina() {return maxStamina;}
    public float getStaminaPercent(){
        return (float) currentStamina/MAX_STAMINA;
    }
    public float getMaxStaminaPercent(){
        return (float) maxStamina/MAX_STAMINA;
    }
}
