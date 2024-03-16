package com.ZatherusGaming;
import static android.graphics.Bitmap.createBitmap;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import java.util.ArrayList;

/*
 * FROSTWOOD CHRONICLES GAME
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     December 2023
 * COURSE:   COMP486 - Mobile and Internet Game Development (Athabasca University)
 *
 * BitmapHandler
 * Description:
 * Main class used to manage the loading of Bitmaps. Additionally, there are a variety of extra
 * methods for custom bitmap creation like scaling and rotation.
 *
 * Usage:
 * Most classes utilize the BitmapHandler. After instantiation call loadBackgroundBitmap or
 * createSprite in order to load the associated string. Backgrounds have a unique method as their
 * scaling is slightly different then generic sprites. Backgrounds are designed to fill the
 * full screen while maintaining the aspect ratio of the image. This is accomplished by cropping
 * into the center of the image. As a result, some background information will be lost depending on
 * the screen size while allowing maintaining the aspect ratio between backgrounds and sprites
 * without the use of black bars.
 *
 * Future Updates/Refactors:
 * One major consideration that I am aware of but did not need to solve in this build due to the
 * smaller scale, is bitmap removal from memory. Currently, once an object is created and the bitmap
 * is loaded, it will stay in memory forever. If multiple levels, enemies, or other ideas were to
 * grow the project, I would want to properly dereference bitmaps for the garbage collector as
 * even if no objects exist that use the bitmap, the storage arrays in this handler will always
 * exist until the BitmpHandler is gone. Not an issue for the current build at all, just something
 * I knew I needed to think about.
 */

public class BitmapHandler {
    private int screenWidth;
    private int screenHeight;
    private float scaleFactor;

    private Context context;

    private ArrayList<CustomBitmap> bitmaps;
    private ArrayList<Sprite> sprites;

    //Debug variables used to see if the loader is functioning correctly.
    public int numberOfLoadedBitmaps;
    public int numberOfSkippedLoads;

    //Used for loaded bitmaps to avoid garbage collection during build
    private Bitmap tempBitmap;
    private Bitmap scaledBitmap;

    BitmapHandler(Context context, int screenWidth, int screenHeight){
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.context = context;

        //Temporary load to get accurate scaling due to pixel density differences.
        //Was not able to use hardcoded reference sizes here for scaling.
        int resID = context.getResources().getIdentifier("test",
                "drawable", context.getPackageName());
        Bitmap quickTest = BitmapFactory.decodeResource(context.getResources(), resID);

        int bitmapWidth = quickTest.getWidth();
        int bitmapHeight = quickTest.getHeight();

        // Calculate the scale factors for width and height
        float scaleX = (float) screenWidth / bitmapWidth;
        float scaleY = (float) screenHeight / bitmapHeight;
        quickTest = null;

        scaleFactor = Math.max(scaleX, scaleY); // Use max to ensure the scaled image is larger than the screen

        numberOfLoadedBitmaps = 0;

        bitmaps = new ArrayList<CustomBitmap>();
        sprites = new ArrayList<Sprite>();
    }

    //Loads a background image.
    public Bitmap loadBackgroundBitmap(String bitmapName){
        int resID = getResID(bitmapName);
        //Check if the bitmap is already in memory and return a reference to it
        for(CustomBitmap index : bitmaps){
            if(index.getResID() == resID){
                numberOfSkippedLoads++;
                return index.getBitmapReference();
            }
        }

        //The bitmap was not found and so must be loaded into memory

        //Build the new bitmap and scale it to the necessary size
        tempBitmap = BitmapFactory.decodeResource(context.getResources(), resID);
        int scaledWidth = (int) (tempBitmap.getWidth() * scaleFactor);
        int scaledHeight = (int) (tempBitmap.getHeight() * scaleFactor);
        scaledBitmap = Bitmap.createScaledBitmap(tempBitmap, scaledWidth, scaledHeight, true);

        // Calculate the coordinates of the top-left corner of the cropped portion (the screen size)
        int cropX = (scaledWidth - screenWidth) / 2;
        int cropY = (scaledHeight - screenHeight) / 2;

        //Rebuild the final bitmap with the cropped values.
        tempBitmap = createBitmap(scaledBitmap, cropX, cropY, cropX + screenWidth, cropY + screenHeight);
        bitmaps.add(new CustomBitmap(tempBitmap, resID));

        numberOfLoadedBitmaps++;

        return tempBitmap;
    }

    //Creates a sprite from a bitmap name. Must include sprite count on sheet and animation frames per second
    public Sprite createSprite(String bitmapName, int spritesPerSheet, int animationFPS){
        int resID = getResID(bitmapName);
        Sprite newSprite;

        //Checks the sprite array to see if the resID is already loaded.
        //Returns a new sprite with a reference to the same Sprite array.
        for(Sprite sprite : sprites){
            if(sprite.getResID() == resID){
                numberOfSkippedLoads++;
                newSprite = new Sprite(sprite.getSpriteArray(), resID, animationFPS);
                return newSprite;
            }
        }

        //Otherwise, no sprite built already

        //Build the new bitmap and store it
        //First scale the sprite sheet by the scalefactor
        Bitmap spriteSheet = BitmapFactory.decodeResource(context.getResources(), resID);
        int scaledWidth = (int) (spriteSheet.getWidth() * scaleFactor);
        int scaledHeight = (int) (spriteSheet.getHeight() * scaleFactor);
        scaledBitmap = Bitmap.createScaledBitmap(spriteSheet, scaledWidth, scaledHeight, true);

        //Get the height and width of one sprite on the sheet
        int spriteWidth = scaledBitmap.getWidth()/spritesPerSheet;
        int spriteHeight = scaledBitmap.getHeight();
        Rect spriteRect;
        Bitmap[] spriteArray = new Bitmap[spritesPerSheet];

        //Cycle through the sprite sheet and cutout individual bitmaps and store them in an animation array
        for(int index = 0; index < spritesPerSheet; index++){
            spriteRect = new Rect(spriteWidth*index, 0, spriteWidth*(index + 1), spriteHeight);
            tempBitmap = createBitmap(scaledBitmap, spriteRect.left, spriteRect.top, spriteRect.width(), spriteRect.height());
            spriteArray[index] = tempBitmap;
        }
        //create a new sprite with the array.
        newSprite = new Sprite(spriteArray, resID, animationFPS);

        numberOfLoadedBitmaps++; //Only count once per full sprite sheet

        return newSprite;
    }

    //Rotates a bitmap the number of degress provided and returns a new bitmap
    public Bitmap rotateBitmap(Bitmap sourceBitmap, float rotateDegree){
        Matrix rotationMatrix = new Matrix();
        rotationMatrix.postRotate(rotateDegree);

        return Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), rotationMatrix, true);
    }

    //Flips the provided bitmap horizontally and returns a new bitmap
    public Bitmap flipBitmap(Bitmap sourceBitmap){
        Matrix flippingMatrix = new Matrix();
        flippingMatrix.preScale(-1,1);
        return Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), flippingMatrix, true);
    }

    //No longer used. Keeping in as I may use in next build
    public Bitmap rescaleBitmap(Bitmap sourceBitmap, int newWidth, int newHeight){
        return Bitmap.createScaledBitmap(sourceBitmap, newWidth, newHeight, true);
    }

    //Translates a string into the associated resID
    public int getResID(String bitmapName){
        return context.getResources().getIdentifier(bitmapName,
                "drawable", context.getPackageName());
    }

    //Getters
    public int getScreenWidth(){
        return screenWidth;
    }
    public int getScreenHeight(){
        return screenHeight;
    }
}
