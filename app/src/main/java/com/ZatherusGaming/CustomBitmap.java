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
 * CustomBitmap
 * Description:
 * Holder class used to match a resID to a loaded bitmap in memory.
 *
 * Usage:
 * After instantiating with a Bitmap reference and its resource ID, the class can be used to check
 * if future resource ids are already loaded in memory. Works alongside the BitmapHandler which
 * compares a new Bitmap load request against the stored CustomBitmaps in memory. This structure
 * is primarily used to avoid storing multiple of the same Bitmap in memory.
 *
 * Future Updates/Refactor:
 * Originally this class was used far more frequently but became less relevent later when I built
 * a Sprite class for animations. The resource ID is now stored and handled inside the sprites and
 * the sprite array is returned. Even static objects use the sprite object now. I left this in for
 * use with backgrounds, primarily to show the development of the code. Backgrounds could be loaded
 * in as a sprite instead to simplify the system. One solution could be to do a parent child
 * structure where multi bitmap objects (animated sprites), use the sprite class, and static
 * 1 bitmap objects use a StaticBitmap (this class). Could also add a counter that tracks how many
 * objects are using the bitmap and then when it hits 0 call a deconstructor on this object.
 */

public class CustomBitmap {
    private Bitmap image;
    private int resID;

    CustomBitmap(Bitmap passedImage, int resID){
        image = passedImage;
        this.resID = resID;
    }

    public int getResID(){
        return resID;
    }
    public Bitmap getBitmapReference(){
        return image;
    }
}
