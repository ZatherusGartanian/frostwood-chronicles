package com.ZatherusGaming;
/*
 * FROSTWOOD CHRONICLES GAME
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     December 2023
 * COURSE:   COMP486 - Mobile and Internet Game Development (Athabasca University)
 *
 * Vector
 * Description:
 * Basic representation of a coordinate.
 *
 * Usage:
 * This is a simple vector class that is used to represent a 3D coordinate space. Set values at
 * instantiation and use getters to call. Primarily used to store worldLocations of gameObjects.
 */

public class Vector {
    private float x;
    private float y;
    private float z;

    Vector(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    //Getters
    public float getX(){
        return x;
    }
    public float getY(){
        return y;
    }
    public float getZ(){
        return z;
    }
}
