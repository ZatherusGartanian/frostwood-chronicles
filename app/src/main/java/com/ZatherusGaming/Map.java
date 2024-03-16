package com.ZatherusGaming;
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
 * Map
 * Description:
 * Contains the layout of the game world as 2d array. Each array location represents a tile of the
 * game world. See Tile class for tile details. Map layout and design is prepared at launch.
 *
 * Usage:
 * Create a map object and you are all set. Daily resource growth updates all the tiles resource
 * chances. Use travel to move the player based on cardinal direction. Can customize the Map layout
 * as long as each line is the same length. Should be a m X n map layout.
 *
 * Future Updates/Refactor:
 * Multiple map layouts is easy to implement. Could also add a randomizer for starting location and
 * even the layout but that would take a bit more design thinking to set up to avoid really awkard
 * layouts.
 */
public class Map {
    private Tile[][] tiles;
    private int playerLocationX;
    private int playerLocationY;
    private ArrayList<String> mapAsString;
    private int mapWidth;
    private int mapHeight;

    Map(BitmapHandler bitmapHandler){
        testMap();
        mapWidth = mapAsString.get(0).length();
        mapHeight = mapAsString.size();
        tiles = new Tile[mapHeight][mapWidth];
        setStartingLocation(0, 0);
        buildMap(bitmapHandler);
    }

    //Build the map using the string array in the testMap() method.
    //Can update with other maps for variety later
    public void buildMap(BitmapHandler bitmapHandler){
        //Construct the Tiles with the correct background image
        String tempString;
        for(int mapHeightIndex = 0; mapHeightIndex < mapHeight; mapHeightIndex++) {
            tempString = mapAsString.get(mapHeightIndex);
            for(int mapWidthIndex = 0; mapWidthIndex < mapWidth; mapWidthIndex++){
                char backgroundType = tempString.charAt(mapWidthIndex);
                if(backgroundType == '1'){
                    tiles[mapHeightIndex][mapWidthIndex] = new Tile("test", bitmapHandler);
                }
                else if(backgroundType == '2'){
                    tiles[mapHeightIndex][mapWidthIndex] = new Tile("test2", bitmapHandler);
                }
                else if(backgroundType == '3'){
                    tiles[mapHeightIndex][mapWidthIndex] = new Tile("cliff_left", bitmapHandler);
                }
                else if(backgroundType == '4') {
                    tiles[mapHeightIndex][mapWidthIndex] = new Tile("cliff_right", bitmapHandler);
                }
                else if(backgroundType == '5') {
                    tiles[mapHeightIndex][mapWidthIndex] = new Tile("barren", bitmapHandler);
                }
                else if(backgroundType == '6') {
                    tiles[mapHeightIndex][mapWidthIndex] = new Tile("openarea", bitmapHandler);
                }
                else if(backgroundType == '7') {
                    tiles[mapHeightIndex][mapWidthIndex] = new Tile("openareahill", bitmapHandler);
                }
                else
                    tiles[mapHeightIndex][mapWidthIndex] = null;
            }
        }

        //Cycle through each tile location and assign traversal based on neighbouring tiles
        for(int mapHeightIndex = 0; mapHeightIndex < mapHeight; mapHeightIndex++) {
            for (int mapWidthIndex = 0; mapWidthIndex < mapWidth; mapWidthIndex++) {
                boolean northExit = false;
                boolean southExit = false;
                boolean eastExit = false;
                boolean westExit = false;
                Tile currentTile = tiles[mapHeightIndex][mapWidthIndex];

                if(currentTile == null){
                    continue;
                }

                Tile comparedTile;

                //Tile to the north
                if (mapHeightIndex - 1 >= 0) {
                    comparedTile = tiles[mapHeightIndex - 1][mapWidthIndex];
                    if (comparedTile != null) {
                        northExit = true;
                    }
                }

                //Tile to the East
                if (mapWidthIndex + 1 < mapWidth) {
                    comparedTile = tiles[mapHeightIndex][mapWidthIndex + 1];
                    if (comparedTile != null) {
                        eastExit = true;
                    }
                }

                //Tile to the south
                if (mapHeightIndex + 1 < mapHeight) {
                    comparedTile = tiles[mapHeightIndex + 1][mapWidthIndex];
                    if (comparedTile != null) {
                        southExit = true;
                    }
                }

                //Tile to the West
                if (mapWidthIndex - 1 >= 0) {
                    comparedTile = tiles[mapHeightIndex][mapWidthIndex - 1];
                    if (comparedTile != null) {
                        westExit = true;
                    }
                }

                currentTile.setExits(northExit, eastExit, southExit, westExit);
            }
        }
    }

    public Tile getCurrentTile(){
        return tiles[playerLocationY][playerLocationX];
    }

    public void setStartingLocation(int x, int y){
        // int max
        //Random random = new Random();
        //int randomNumber = random.nextInt(()
        playerLocationX = x;
        playerLocationY = y;
    }

    public void testMap() {
        //Use numbers 1-7 for different backgrounds and - as a null tile.
        mapAsString = new ArrayList<String>();
        mapAsString.add("3-----4");
        mapAsString.add("36--364");
        mapAsString.add("3765-74");
        mapAsString.add("-1-2514");
        mapAsString.add("2516---");

    }

    //Move the player in the compass direction provided if it is a viable direction
    //Returns true for gameState to know it was a valid direction and movement has started.
    public boolean travel(Compass direction){
        if(getCurrentTile().viableExit(direction)){
            switch(direction) {
                case NORTH:
                    playerLocationY--;
                    return true;
                case EAST:
                    playerLocationX++;
                    return true;
                case SOUTH:
                    playerLocationY++;
                    return true;
                case WEST:
                    playerLocationX--;
                    return true;
                case NONE:
                    return false;
            }
        }
        return false;
    }

    //Cycle through all the tiles and grow their resources
    public void dailyResourceGrowth(){
        for(int mapHeightIndex = 0; mapHeightIndex < mapHeight; mapHeightIndex++) {
            for (int mapWidthIndex = 0; mapWidthIndex < mapWidth; mapWidthIndex++) {
                Tile currentTile = tiles[mapHeightIndex][mapWidthIndex];
                if(currentTile == null){
                    continue;
                }
                currentTile.dailyGrowth();
            }
        }
    }
}
