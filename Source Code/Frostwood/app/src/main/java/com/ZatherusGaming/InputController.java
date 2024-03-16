package com.ZatherusGaming;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
/*
 * FROSTWOOD CHRONICLES GAME
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     December 2023
 * COURSE:   COMP486 - Mobile and Internet Game Development (Athabasca University)
 *
 * InputController
 * Description:
 * Class used to process screen taps that are not UI buttons (See ButtonListener).
 *
 * Usage:
 * Only needs to link up to the gameState and UserInterface. The rest is directly handled by the
 * class. The only odd note here is the clearInputs method. This is used to cancel press and hold
 * events from triggering and must be called by the game if necessary. For example, if a player
 * taps the edge of the screen to get there character to move to the edge, and then they press and
 * hold before the character arrives but transitions before the press and hold timer finishes the
 * next tile would have interfering UI information between the Event Display and the press and hold
 * action display. The game state would also bug out.
 *
 * Future Updates/Refactor:
 * I wanted to try and create a radial menu for action selection but solving how to display that
 * and animate it became to much and so I scrapped it for the current display. I also am debating
 * just letting the user tap the relevent bars to eat/drink. Tap the mana bar to drink. Tap Stamina
 * to eat. Add a camp icon somewhere and tap that to camp. Maybe add a cooldown on the camping use
 * that makes it so you have to pass 5 hours before you can sleep again.
 */
public class InputController {
    private GameState gameState;
    private Runnable pressAndHoldRunnable;
    private Handler pressAndHoldHandler;

    InputController(GameState gameState){
        this.gameState = gameState;

        pressAndHoldHandler = new Handler(Looper.getMainLooper());

        pressAndHoldRunnable = new Runnable(){
            public void run(){
                gameState.getUserInterface().displayActionBar();
            }
        };
    }

    public void handleInput(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();

        //If on a tile or moving between tiles
        if (gameState.getState() == GameState.State.TILE || gameState.getState() == GameState.State.TRAVEL) {
            switch (motionEvent.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    //On screen press start press and hold event
                    pressAndHoldHandler.postDelayed(pressAndHoldRunnable, 500);
                    break;

                case MotionEvent.ACTION_UP:
                    //On release of screen attempt to interrupt the press and hold call
                    //This will cancel the call if the player releases the screen before the delay ends.
                    //Then move the player to the location
                    pressAndHoldHandler.removeCallbacks(pressAndHoldRunnable);
                    gameState.getPlayer().moveToCoordinate(x, y);
                    break;
            }
        }
        //If in the action menu screen a screen tap not on a button will hide the bar again.
        else if(gameState.getState() == GameState.State.ACTIONMENU){
            switch(motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    gameState.getUserInterface().hideActionBar();
                    break;
            }
        }
        else if(gameState.getState() == GameState.State.STATS){
            switch(motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    gameState.getUserInterface().hideStats();
                    break;
            }
        }
    }

    //Use to interrupt press and hold trigger
    public void clearInputs(){
        pressAndHoldHandler.removeCallbacks(pressAndHoldRunnable);
    }
}
