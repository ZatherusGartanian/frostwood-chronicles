package com.ZatherusGaming;
import android.view.View;
/*
 * FROSTWOOD CHRONICLES GAME
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     December 2023
 * COURSE:   COMP486 - Mobile and Internet Game Development (Athabasca University)
 *
 * ButtonListener
 * Description:
 * Main ButtonListener for all buttons in the game. Does not handle the input and passing it
 * instead to the UserInterface class to handle the specifics. Main differentiation with this
 * listener, is that all buttons mapped to the same instance will not be pressable simultaneously.
 *
 * Usage:
 * On instantiation, set all buttons to use this listener. When a button is pressed it will prompt
 * the UserInterface class to handle the press.
 */

public class ButtonListener implements View.OnClickListener{

    UserInterface ui;
    private boolean actionInProgress = false;

    ButtonListener(UserInterface ui){
        this.ui = ui;
    }

    @Override
    public void onClick(View view) {
        // Check if an action is already in progress
        if (!actionInProgress) {
            // Set the flag to indicate that an action is in progress
            actionInProgress = true;

            ui.handleButtonPress(view);

            // Reset the flag after the action is complete
            actionInProgress = false;
        }
    }
}
