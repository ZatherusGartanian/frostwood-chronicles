package com.ZatherusGaming;
import java.util.Random;
/*
 * FROSTWOOD CHRONICLES GAME
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     December 2023
 * COURSE:   COMP486 - Mobile and Internet Game Development (Athabasca University)
 *
 * Tile
 * Description:
 * Represents the individual map locations that the player stands in. Tiles also know which exits
 * are viable from them. They also track their resource chance and if they have been visited.
 *
 * Usage:
 * Once a tile is constructed it must also have its exits set using setExits. Not using setExits will
 * cause crashes. This class should only be created by the Map where it always will set the exits
 * after instantiation. When a player forages run the spawnResourceAttempt to see if a resource
 * should spawn. Handle the spawned resource elsewhere. The gameState should use dailyGrowth every
 * day that passes to increase resources odds.
 *
 * Future Updates/Refactor:
 * May want to have tiles that run specific events and would want to update the Tile class in
 * someway to mark that. If say tile was a river background then event with river text should happen.
 * Tiles can also get more detailed in remembering whats on them. In future builds tiles may have
 * Tree sprites, cabins, or other interactibles on screen and that should be tracked and stored here,
 * not in the gameState. Tiles would then need a method to pass the objects needed to be drawn. This
 * would mean players could move between tiles and go back and have the tile remember everything
 * that was on the tile. Last idea is each event could have a set sprite associated with it and
 * when an event is randomized the tile the player is on could be updated with a sprite that represents
 * that event. This is a future future line of thinking though as a player stat/leveling system
 * is first.
 */

public class Tile {
    //Background visual of the tile
    private Background background;

    //Is the direction a valid exit from this tile
    boolean northExit;
    boolean eastExit;
    boolean southExit;
    boolean westExit;

    //Flag once tile has been visted.
    boolean visited;

    //Spawn chance of food and water on the tile when player forages
    private int resourceSpawnChance;


    Tile(String backgroundName, BitmapHandler bitmapHandler){
        background = new Background(backgroundName, bitmapHandler);
        visited = false;

        //Tweak this randomizer if the foraging rates are too low
        //Could update with final variables for easier adjusting later
        Random random = new Random();
        resourceSpawnChance = random.nextInt(31) + 10; //Random chance between 10 and 40
    }

    //Must be run after instantiation to set each exit. This is completed by the map class
    public void setExits(boolean northExit, boolean eastExit, boolean southExit, boolean westExit){
        this.northExit = northExit;
        this.eastExit = eastExit;
        this.southExit = southExit;
        this.westExit = westExit;
    }

    //Check if a cardinal direction is a valid exit from the tile
    public boolean viableExit(Compass location){
        boolean viable = false;
        switch(location){
            case NONE:
                break;
            case NORTH:
                if(northExit == true) {
                    viable = true;
                }
                break;
            case EAST:
                if(eastExit == true){
                    viable = true;
                }
                break;
            case SOUTH:
                if(southExit == true){
                    viable = true;
                }
                break;
            case WEST:
                if(westExit == true){
                    viable = true;
                }
                break;
        }
        return viable;
    }

    //Try and spawn a resource on the tile
    //Should be called when the player forages
    public boolean spawnResourceAttempt(){
        Random random = new Random();
        int randomRoll = random.nextInt(100) + 1;

        //If the randomRoll is under the spawn chance then the item should spawn
        if(randomRoll <= resourceSpawnChance){
            //If a resource spawns reduce chance on tile
            resourceSpawnChance -= 10;
            if (resourceSpawnChance < 5){
                resourceSpawnChance = 5;
            }
            return true;
        }
        return false;
    }

    //Increases resource chance on the tile up to 35%
    //Should be called by the game state when a day has passed
    public void dailyGrowth(){
        resourceSpawnChance += 5;
        if(resourceSpawnChance > 35){
            resourceSpawnChance = 35;
        }
    }

    //Getter
    public Background getBackground(){
        return background;
    }
}
