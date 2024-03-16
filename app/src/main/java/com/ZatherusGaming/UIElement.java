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
 * UIElement
 * Description:
 * Represents UI elements drawn in game. Current use is limited to status bars (health, Stamina,
 * mana) and directional arrows.
 *
 * Usage:
 * The main use of this class is to provide a differentiation with other game objects and solves an
 * issue of rotating sprites being rotated every cycle.
 *
 * Future Updates/Refactor:
 * See my other Game Object classes for more on this area. The issue being solved with the
 * sourceBitmap is one of how drawing is handled. I greatly dislike the way the textbook handled
 * drawing. So much processing time is wasted every cycle redrawing every bitmap. If a bitmap is
 * not changing across multiple seconds why make the CPU rebuild a new Bitmap for every object.
 * Because the arrows are rotated they need to be loaded in from a source bitmap then rotated and
 * updated. The main UI that uses this though is the status bars. I wanted to scale the stat totals
 * as a percentage and rather then rebuilding the ui EVERY cycle I decided to build a system that
 * only updates the sprite when it needs to. The sprite needs to remember its original bitmap
 * size so that it can scale off the 100% size. All of this could be solved in a cleaner way while
 * still maintaining the optimization off the processor. This is probably overkill, I just actually
 * disliked how much processing was being wasted each frame in the text on drawing. My animation
 * solution also stores each bitmap frame rather then redrawing every cycle using a Rect across a
 * sprite sheet. The added storage is minimal between the two methods (I have some added header size
 * for each bitmap in storage instead of one header for a full sprite sheet) but that difference
 * is minimal for saving every time every cycle.
 *
 * All said, this class will likely get shifted during a refactor of the GameObjects design.
 */

public class UIElement extends GameObject{
    private Bitmap sourceBitmap;

    UIElement(Vector location, String spriteName, int spritesPerSheet, int animationFPS, BitmapHandler bitmapHandler){
        super(location, spriteName, spritesPerSheet, animationFPS, bitmapHandler);
        sourceBitmap = getSprite().getCurrentSprite(System.currentTimeMillis());
    }

    public Bitmap getSourceBitmap(){
        return sourceBitmap;
    }
}

