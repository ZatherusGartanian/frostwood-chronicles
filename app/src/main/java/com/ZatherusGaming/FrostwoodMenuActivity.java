package com.ZatherusGaming;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

/*
 * FROSTWOOD CHRONICLES GAME
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     December 2023
 * COURSE:   COMP486 - Mobile and Internet Game Development (Athabasca University)
 *
 * FrostwoodMenuActivity
 * Description:
 * This is the launcher for the game. This activity handles the pre game UI. Settings and file
 * loading would be handled here in the future.
 *
 * Usage:
 * Launch the Activity. Super basic activity that has 3 methods for handling some input before
 * passing things off to the game activity.
 *
 * Future Updates/Refactor:
 * Adding functionality through more button. I debated having the character sprite in the background
 * just randomly roaming around but decided it was polish overkill right now when there are better
 * areas that are still lacking (like sound and music). Adding a "hard mode" start option would be
 * pretty easy to implemnent by just passing the normal or hard mode choice to the game and then
 * changing starting values. Start with less food and water, take more losses to stats per hour.
 * These are all set as variables right now so could easily just have the those set at launch.
 */
public class FrostwoodMenuActivity extends Activity {
    ImageView instructions;
    LinearLayout buttonFrame;
    TextView longestRun;
    ConstraintLayout longestRunFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.game_layout);
        longestRun = findViewById(R.id.longestRunText);
        longestRunFrame = findViewById(R.id.longestRunFrame);
        instructions = findViewById(R.id.instructionImage);
        buttonFrame = findViewById(R.id.buttonFrame);

        loadLongestRun();
    }

    public void onPlayButtonClick(View view){
        Intent intent = new Intent(this, FrostwoodActivity.class);
        startActivity(intent);
    }

    public void howToPlay(View view){
        buttonFrame.setVisibility(View.GONE);
        instructions.setVisibility(View.VISIBLE);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Get the X and Y coordinates of the touch event
        if(instructions.getVisibility() == View.VISIBLE){
            instructions.setVisibility(View.GONE);
            buttonFrame.setVisibility(View.VISIBLE);
        }
        return true;
    }

    //Get the longest run attempt and add display it
    private void loadLongestRun() {
        SharedPreferences preferences = getSharedPreferences("HighScoreFile", MODE_PRIVATE);
        //Get prior record (0s if no record stored)
        int days = preferences.getInt("DaysSurvived", 0); //Returns 0 if no file
        int hours = preferences.getInt("HoursSurvived", 0); //Returns 0 if no file
        int minutes = preferences.getInt("MinutesSurvived", 0); //Returns 0 if no file

        //Translate times into minutes for easier comparing
        int priorSurvivalInMinutes = (days * 24 * 60) + (hours * 60) + minutes;

        if(priorSurvivalInMinutes == 0){
            //No record on file
            longestRunFrame.setVisibility(View.GONE);
        }
        else{
            String longestRunAsString= "Longest Survival Time:\n";
            if(days != 0){
                longestRunAsString = longestRunAsString.concat("" + days);
                if(days == 1){
                    longestRunAsString = longestRunAsString.concat(" day ");
                }
                else
                    longestRunAsString = longestRunAsString.concat(" days ");
            }

            if(hours != 0){
                longestRunAsString = longestRunAsString.concat(" " + hours);
                if(hours == 1){
                    longestRunAsString = longestRunAsString.concat(" hour");
                }
                else{
                    longestRunAsString = longestRunAsString.concat(" hours");
                }
            }

            if(minutes != 0) {
                longestRunAsString = longestRunAsString.concat(" " + minutes + " minutes");
            }

            longestRun.setText(longestRunAsString);
            longestRunFrame.setVisibility(View.VISIBLE);
        }
    }
}
