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
 * Sprite
 * Description:
 * Holds the visual representation of an object. Stores animations as frames in an array.
 *
 * Usage:
 * Set up occurs through BitmapHandler createSprite(). Otherwise, simply call getCurrentSprite every
 * cycle to get the correct bitmap. If sprite should play forward an backward use setAsReversable.
 * Other methods are noted where necessary.
 *
 * Future Updates/Refactor:
 * See CustomBitmap for some future thoughts. Static vs Animated sprites. One play animations
 * is also awkward to use so I cut its use.
 */

public class Sprite {
    // Array to store individual frames of the sprite animation
    private Bitmap[] spriteArray;

    // Resource ID associated with the sprite
    private int resID;

    // Height and width of each frame in the sprite animation
    private int height;
    private int width;

    // Information related to animation timing and frames
    private int frameCount;
    private int currentFrame;
    private long lastFrameTime;
    private int framePeriod;

    // Flags to control animation behavior
    private boolean reversing = false;
    private boolean reversable = false;
    private boolean looping = true;
    private boolean isAnimationComplete = false;

    //Constructor to initialize the sprite with its load bitmap array
    //Called from the BitmapHandler class createSprite() method.
    Sprite(Bitmap[] spriteArray, int resID, int animationFPS) {
        this.spriteArray = spriteArray;
        this.resID = resID;

        //Set height and width for the Sprite
        height = spriteArray[0].getHeight();
        width = spriteArray[0].getWidth();

        //Initializing animation variables
        frameCount = spriteArray.length;
        framePeriod = 1000 / animationFPS;
        lastFrameTime = 0l;
        currentFrame = 0;
    }


    //Get the current frame of the sprite based on elapsed time
    public Bitmap getCurrentSprite(long time){
        if (time > lastFrameTime + framePeriod){
            lastFrameTime = time;
            if(reversable && reversing) {
                //If reversable and currently reversing, move to the previous frame
                currentFrame--;
                if (currentFrame < 0) {
                    currentFrame = 1;
                    reversing = false;
                }
            }
            else if (reversable){
                //If reversable and not reversing, move to the next frame
                currentFrame++;
                if (currentFrame >= frameCount) {
                    currentFrame = frameCount-2;
                    reversing = true;
                }

            }
            else{
                //If not reversable, move to the next frame
                currentFrame++;
                if (currentFrame >= frameCount) {
                    currentFrame = 0;
                    //If not looping, mark animation as complete
                    if(!looping){
                        isAnimationComplete = true;
                    }
                }
            }
        }

        return spriteArray[currentFrame];
    }

    //Set the sprite as reversable (able to play frames in reverse)
    public void setAsReversable(){
        reversable = true;
    }

    //Use to reset the animation (See compass arrows in game. Used to sync their animations)
    public void resetSpriteFrameCount(){
        lastFrameTime = 0l;
        currentFrame = 0;
    }

    //Make sure the new sprite array is the same frame size as the old one for animations.
    //Will likely be refactored out in a future build for a cleaner and safer solution.
    public void replaceSpriteArray(Bitmap[] spriteArray){
        if(spriteArray.length == this.spriteArray.length) {
            this.spriteArray = spriteArray;
            height = spriteArray[0].getHeight();
            width = spriteArray[0].getWidth();
        }
    }

    //Set and check states of an animation. Used if an animation should only run through once.
    public void startAnimation(){
        isAnimationComplete = false;
    }
    public boolean isAnimationComplete(){
        return isAnimationComplete;
    }
    public void setLooping(Boolean looping){
        this.looping = looping;
    }

    //Getters
    public int getHeight(){
        return height;
    }
    public int getWidth(){
        return width;
    }
    public int getResID(){
        return resID;
    }
    public Bitmap[] getSpriteArray(){
        return spriteArray;
    };
    public int getCurrentFrame(){
        return currentFrame;
    }
}
