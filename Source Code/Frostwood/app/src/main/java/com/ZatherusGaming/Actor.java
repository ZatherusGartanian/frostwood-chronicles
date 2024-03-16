package com.ZatherusGaming;

/*
 * FROSTWOOD CHRONICLES GAME
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     December 2023
 * COURSE:   COMP486 - Mobile and Internet Game Development (Athabasca University)
 *
 * Actor Class
 * Description:
 * Child class that inherits from the GameObject class. Actors are GameObjects that have velocity.
 * In the current build of the game, the player is the only GameObject that takes this into consideration,
 * though future enemies or sprites could instantiate this class to be movable objects.
 *
 * Usage:
 * Contains methods specific to movable GameObjects. On instantiation call setMaxVelocity to first
 * the velocity of the Actor. The main additions are the moveToCoordinate method which takes an X and Y
 * position and on update will move the actor at their max velocity towards the coordinate.
 */


//Actors are GameObjects that can move (Have Velocity)
public class Actor extends GameObject{

    public enum ActorState{
        IDLE, MOVING
    }
    public enum Facing{
        LEFT, RIGHT
    }

    private ActorState actorState = ActorState.IDLE;

    private int maxVelocity = 0;
    private int velocity = 0;
    private Vector destination;
    private Facing facing = Facing.RIGHT;

    Actor(Vector location, String spriteName, int spritesPerSheet, int animationFPS, BitmapHandler bitmapHandler){
        super(location, spriteName, spritesPerSheet, animationFPS, bitmapHandler);
        destination = location;
    }

    public void setMaxVelocity(int maxVelocity){
        this.maxVelocity = maxVelocity;
    }

    public void update(long fps){
        super.update();
        if(velocity != 0){
            move(fps);
        }
    }

    private void move(long fps){
        float dx = destination.getX() - getXPosition();
        float dy = destination.getY() - getYPosition();

        if(dx < 0){
            facing = Facing.LEFT;
        }
        else
            facing = Facing.RIGHT;

        double distance = Math.sqrt((dx * dx) + (dy * dy));

        double movementThisFrame = (double)(velocity / fps);

        if (distance > movementThisFrame) {
            double direction = Math.atan2(dy, dx);
            float newX = (((float)(movementThisFrame * Math.cos(direction))) + getXPosition());
            float newY = (((float)(movementThisFrame * Math.sin(direction))) + getYPosition());

            setWorldLocation(new Vector(newX, newY, getWorldLocation().getZ()));
            updateHitBox((int) newX, (int) newY);
        }
        else {
            actorState = ActorState.IDLE;
            destination = getWorldLocation();
            velocity = 0;
        }
    }

    public void moveToCoordinate(float x, float y){
        //Translate coordinate to top corner of bitmap
        float destinationX = x - (float)(getSprite().getWidth()/2);
        float destinationY = y - (float)(getSprite().getHeight()/2);
        destination = new Vector(destinationX, destinationY, getWorldLocation().getZ());
        velocity = maxVelocity; //Start the movement
        actorState = ActorState.MOVING;
    }

    public ActorState getActorState(){
        return actorState;
    }

    public Facing getFacing(){
        return facing;
    }
}
