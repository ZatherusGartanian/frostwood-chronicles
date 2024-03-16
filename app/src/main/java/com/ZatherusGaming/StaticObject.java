package com.ZatherusGaming;
/*
 * FROSTWOOD CHRONICLES GAME
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     December 2023
 * COURSE:   COMP486 - Mobile and Internet Game Development (Athabasca University)
 *
 * StaticObject
 * Description:
 * A way to represent gameObjects that do not have motion.
 *
 * Usage:
 * See GameObject class for exact methods. Strictly a child for future proofing right now.
 *
 * Future Updates/Refactor:
 * Rather then having GameObject not be abstract I decided to handle static objects this way for
 * now. The main reason is there should be a distinction logically. As mentioned elsewhere I want
 * to refactor the whole GameObject class structures to be more logical. It started out clean and
 * slowly lost the object oriented structure as time went on.
 */

public class StaticObject extends GameObject{
    StaticObject(Vector location, String spriteName, int spritesPerSheet, int animationFPS, BitmapHandler bitmapHandler) {
        super(location, spriteName, spritesPerSheet, animationFPS, bitmapHandler);
    }
}
