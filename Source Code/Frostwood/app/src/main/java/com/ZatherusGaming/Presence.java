package com.ZatherusGaming;
import java.util.Random;

/*
 * FROSTWOOD CHRONICLES GAME
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     January 2024
 * COURSE:   COMP486 - Mobile and Internet Game Development (Athabasca University)
 *
 * Presence
 * Description:
 * Class used to contain the data representing the presence in the game.
 *
 * Usage:
 * Once loaded the class contains basic getters. Use attemptToSpawn and getCloser every hour or
 * as necessary.
 *
 * Future Refactors/Updates:
 * Add some randomization to the returned strings making for a more dynamic combat with the presence.
 */

public class Presence {
    //Health and spawn chance
    private int health = 100;
    private int spawnChance = 0;

    //Variables for controlling player damage
    final int HEALTH_DAMAGE = 25;
    final int MAX_HEALTH_DAMAGE = 5;

    Presence(){}

    //Deal damage to the presence
    public void damage(int amount){
        health -= amount;
        if(health < 0){
            health = 0;
        }
    }

    //Increase the spawn chance by a random amount between 1 and 5.
    public void getCloser(){
        Random random = new Random();
        int randomChance = random.nextInt(5) + 1;

        spawnChance += randomChance;
    }

    //Attempt to spawn the presence. Base spawn chance must be higher then 60%
    //To increase the tension for the player.
    public boolean attemptToSpawn(){
        Random random = new Random();
        int randomChance = random.nextInt(100) + 1;

        if (spawnChance > 60){
            if(randomChance < spawnChance){
                return true;
            }
        }

        return false;
    }

    //Resets the chance to 0.
    public void resetChance(){
        spawnChance = 0;
    }

    //Returns an alpha value to represent the encrouaching presence.
    //Pass the value to the activity to set the UI elements alpha.
    public float getAlphaValue(){
        if(spawnChance < 25){
            return 0f;
        }
        else if(spawnChance < 50){
            return 0.4f;
        }
        else if(spawnChance < 60){
            return 0.7f;
        }
        else
            return 1f;
    }

    //GETTERS
    public int getHealth() {return health;}
    public int getSpawnChance() {return spawnChance;}
    public String getRetreatText(){
        return "You close your eyes and begin to calm yourself. The presence lashes out at you once last time." +
            " Weakened, you look across the horizon knowing it will return.";
    }
    public String getEventText(){
        return "The presence materializes before you. Ready yourself.";
    }
    public String getDefeatedText(){
        return "YOU DEFEATED THE PRESENCE!!";
    }
    public String getPostAttackText(int damageDealt) {
        return "You dealt " + damageDealt + " to the presence. You feel yourself weaken as the presence eats away at your essence. " +
                "You can still feel it swirling around you. What now?";
    }
}
