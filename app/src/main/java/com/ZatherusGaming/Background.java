package com.ZatherusGaming;
import android.graphics.Bitmap;

/*
 * FROSTWOOD CHRONICLES GAME
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     December 2023
 * COURSE:   COMP486 - Mobile and Internet Game Development (Athabasca University)
 *
 * Background
 * Description:
 * A holder class representing background images. Simply matches a vector location with an image.
 *
 * Usage:
 * Pass in the sprite name and the image is stored. Currently the world location is hard coded
 * but could easily be updated to allow setting unique coordinates. Basic holder class with getters.
 *
 * Future Updates/Refactor:
 * In a future code refactor this could in theory be refactored into the Tile class as that is
 * the only location that this is currently used. Was originally built on its own in case I ever wanted
 * to layer backgrounds. They also don't need to be StaticObjects as the object class holds far more
 * info then what was necessary.
 */

public class Background {
    private Vector location;
    private Bitmap image;

    Background(String spriteName, BitmapHandler bitmapHandler){
        location = new Vector(0, 0, 0);
        image = bitmapHandler.loadBackgroundBitmap(spriteName);
    }

    public Bitmap getBitmap(){
        return image;
    }

    public float getXPosition(){
        return location.getX();
    }

    public float getYPosition(){
        return location.getX();
    }
}
