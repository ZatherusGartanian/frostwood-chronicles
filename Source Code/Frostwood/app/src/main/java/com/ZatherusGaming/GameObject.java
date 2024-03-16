package com.ZatherusGaming;
import android.graphics.Bitmap;
import android.graphics.Rect;
/*
 * FROSTWOOD CHRONICLES GAME
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     December 2023
 * COURSE:   COMP486 - Mobile and Internet Game Development (Athabasca University)
 *
 * GameObject
 * Description:
 * Abstract Parent class for things that exist in the game world. Essentially a way to represent a
 * Sprite with a location and hitbox information. Must be extended by more specific GameObject types.
 *
 * Usage:
 * Must be extended by other classes. The methods in this class are primarily used to get and set
 * its variables.
 *
 * Future Updates/Refactor:
 * The entire GameObject class structure could use a bit of a rework. It was overengineered for this
 * project and when I stopped keeping it that way things got a bit messier then I would have liked.
 * All still very functional just want to refactor and move some things around to be more logical
 * and multi use.
 */

public abstract class GameObject {
    private Vector worldLocation;

    private Rect hitBox;
    private boolean isVisable;

    private Sprite sprite;
    private int rotation;
    private String name;

    GameObject(Vector location, String bitmapName, int spritesPerSheet, int animationFPS, BitmapHandler bitmapHandler){
        worldLocation = location;

        sprite = bitmapHandler.createSprite(bitmapName, spritesPerSheet, animationFPS);
        name = bitmapName;
        setHitBox();
        isVisable = true;
        rotation = 0;
    }

    public void update(){}

    public Rect getHitBox(){
        return hitBox;
    }
    public void updateHitBox(int x, int y){
        hitBox.offsetTo(x, y);
    }
    public void setHitBox(){
        int left = (int) worldLocation.getX();
        int top = (int) worldLocation.getY();
        hitBox = new Rect(left, top, left + sprite.getWidth(), top + sprite.getHeight());
    }

    public float getXPosition(){
        return worldLocation.getX();
    }
    public float getYPosition(){
        return worldLocation.getY();
    }
    public float getZPosition(){
        return worldLocation.getZ();
    }
    public Vector getWorldLocation(){
        return worldLocation;
    }
    public void setWorldLocation(Vector newLocation){
        worldLocation = newLocation;
        updateHitBox((int)newLocation.getX(), (int)newLocation.getY());
    }

    public void setVisability(boolean isVisable){
        this.isVisable = isVisable;
    }
    public boolean isVisable(){
        return isVisable;
    }

    public Sprite getSprite(){
        return sprite;
    }
    public Bitmap getCurrentSprite(long time){
        return sprite.getCurrentSprite(time);
    }
    public String getName(){return name;}

    public int getRotation(){
        return rotation;
    }
    public void setRotation(int degrees){
        rotation = degrees;
    }
}
