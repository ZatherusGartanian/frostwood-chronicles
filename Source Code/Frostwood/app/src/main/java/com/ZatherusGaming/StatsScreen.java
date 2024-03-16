package com.ZatherusGaming;

/*
 * FROSTWOOD CHRONICLES GAME
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     January 2024
 * COURSE:   COMP486 - Mobile and Internet Game Development (Athabasca University)
 *
 * StatsScreen
 * Description:
 * Class used during player level up
 *
 * Usage:
 * Once created use this class when the player levels up to control the level up process. Make sure to
 * reset the stats between level ups. Also make sure to set statsToAssign at each level up.
 *
 * Future Refactors/Updates:
 * Should be called LevelUpScreen.
 */

public class StatsScreen {
    private int strength;
    private int perception;
    private int endurance;
    private Player playerStats;

    private int statsToAssign = 7;

    StatsScreen(Player playerStats){
        this.playerStats = playerStats;
        resetStats();
    }

    //Resets the variables to base.
    public void resetStats(){
        strength = 0;
        perception = 0;
        endurance = 0;
    }

    //Subtracts from the current strength value and if successful, adds a point back to stats to assign.
    public void minusStrength(){
        if(strength - 1 < 0){
            strength = 0;
        }
        else {
            strength--;
            statsToAssign++;
        }
    }
    //Adds a point to strength if there is a stat available to assign.
    public void plusStrength(){
        if(statsToAssign <= 0){
            return;
        }
        statsToAssign--;

        strength++;
    }

    //Subtracts from the current endurance value and if successful, adds a point back to stats to assign.
    public void minusEndurance(){
        if(endurance - 1 < 0){
            endurance = 0;
        }
        else {
            endurance--;
            statsToAssign++;
        }
    }
    //Adds a point to endurance if there is a stat available to assign.
    public void plusEndurance(){
        if(statsToAssign <= 0){
            return;
        }
        statsToAssign--;

        endurance++;
    }

    //Subtracts from the current perception value and if successful, adds a point back to stats to assign.
    public void minusPerception(){
        if(perception - 1 < 0){
            perception = 0;
        }
        else {
            perception--;
            statsToAssign++;
        }
    }
    //Adds a point to perception if there is a stat available to assign.
    public void plusPerception(){
        if(statsToAssign <= 0){
            return;
        }
        statsToAssign--;

        perception++;
    }

    //Set this value at the start of a level up
    public void setStatsToAssign(int amount){
        statsToAssign = amount;
    }

    //Getters
    public int getStrength() {return strength;}
    public int getPerception() {return perception;}
    public int getEndurance() {return endurance;}
    public int getStatsToAssign() {return statsToAssign;}
    public int getPlayerStrength() {return playerStats.getStrength();}
    public int getPlayerPerception() {return playerStats.getPerception();}
    public int getPlayerEndurance() {return playerStats.getEndurance();}
}
