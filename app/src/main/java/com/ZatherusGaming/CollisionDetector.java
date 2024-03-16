package com.ZatherusGaming;
import android.graphics.Rect;

/*
 * FROSTWOOD CHRONICLES GAME
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     December 2023
 * COURSE:   COMP486 - Mobile and Internet Game Development (Athabasca University)
 *
 * CollisionDetector
 * Description:
 * Main class used to monitor and handle collision detection in the game. Sets a collision edge
 * around the screen and detects if the players hitbox is touching an edge. If the player is
 * touching an edge they are updated to reference that edge. Also, you can check if two GameObjects
 * are intersecting and then prompt the first listed GameObject to handle the intersect.
 *
 * Usage:
 * Every cycle call edgeDetection to check if the player is touching an edge. Pass two objects to
 * checkObjects when necessary to detect if they are interesting.
 *
 * Future Updates/Refactors:
 * For optimization I could only run the edgeDetection if the player is currently moving and then
 * add a moving boolean flag to actors or check if velocity > 0. That way the detector will only
 * be running checks on cycles when the player character is moving. Could also create a collision
 * object that is created when a two objects are colliding and send that object to both classes.
 * It could then be flagged by the objects to indicate that the information was handled. As an
 * example, I could update the water and food objects to handle their own collisions to automatically
 * set themselves as no longer visible after being collided with. Lots of possible optimization
 * that could be done around this class.
 */

public class CollisionDetector {
    public final int BORDER_FORGIVENESS; //Set in constructor
    public final int TOP_UI; //Set in constructor
    public final int BOTTOM_UI; //Set in constructor

    CollisionDetector(FrostwoodActivity activity){
        TOP_UI = activity.findViewById(R.id.topUIFrame).getHeight();
        BOTTOM_UI = activity.findViewById(R.id.bottomUIFrame).getHeight();

        //Setting the border forgiveness to match the UI will get dynamic pixel distance for edge
        BORDER_FORGIVENESS = BOTTOM_UI;
    }

    public void edgeDetection(Player player, int screenWidth, int screenHeight){
        if(player.getHitBox().top <= (TOP_UI + BORDER_FORGIVENESS)){
            player.updateCardinalPosition(Compass.NORTH);
        }
        else if(player.getHitBox().right >= (screenWidth - BORDER_FORGIVENESS)){
            player.updateCardinalPosition(Compass.EAST);
        }
        else if(player.getHitBox().bottom >= (screenHeight - BOTTOM_UI - BORDER_FORGIVENESS)){
            player.updateCardinalPosition(Compass.SOUTH);
        }
        else if (player.getHitBox().left <= (BORDER_FORGIVENESS)){
            player.updateCardinalPosition(Compass.WEST);
        }
        else
            player.updateCardinalPosition(Compass.NONE);
    }

    public boolean checkObjects(GameObject gameObject1, GameObject gameObject2){
        Rect object1 = new Rect(gameObject1.getHitBox());
        Rect object2 = new Rect(gameObject2.getHitBox());
        return object1.intersect(object2);
    }

}
