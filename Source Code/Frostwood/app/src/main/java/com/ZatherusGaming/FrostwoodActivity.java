package com.ZatherusGaming;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
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
 * FrostwoodActivity
 * Description:
 * Activity class for the game. Handles much of the UI implementation due to the use of Views. Is
 * the launching point into the actual game.
 *
 * Usage:
 * Updates to views need to be run on the main UI thread and not in a seperate thread so calls are
 * sent to the activity to be handled here. See the specific methods for each unique UI case.
 * The use of this class is the launching point into the game and the intent must be passed to
 * this activity.
 *
 * Future Updates/Refactor:
 * Early on I followed the way the textbook approached the use of activities and I personally would
 * handle things differently now. onDraw() is a fantastic method to use. The surfaceView extension
 * should not be handling the game loop and should instead only care about what is being drawn
 * within the surface view. However, it was incredibly late into development that I started to
 * question the textbooks approach. Using Views for UI elements is incredibly powerful as they
 * also handle resolution differences built into the constraint layouts.
 */
public class FrostwoodActivity extends Activity {

    // Main GameManager that handles the core game loop including the view and drawing
    private GameManager gm;

    //UI ELEMENTS HAVE TO BE HANDLED BY THE MAIN THREAD AND NOT GAME LOOP.
    //TEXTBOOK MISSED THIS LEVEL OF UI DISCUSSION
    private View fadeEffect;
    private LinearLayout eventFrame;
    private TextView textView;
    private Button button1;
    private Button button2;
    private LinearLayout actionBar;
    private ImageView textDisplayBackground;
    private TextView foodCount;
    private TextView waterCount;
    private TextView time;
    private TextView day;

    private TextView levelUpStrength;
    private TextView levelUpPerception;
    private TextView levelUpEndurance;
    private TextView statsToAssign;

    private LinearLayout confirmStatsFrame;
    private ConstraintLayout statsFrame;

    private ConstraintLayout displayStats;
    private TextView playerStrength;
    private TextView playerEndurance;
    private TextView playerPerception;

    private ImageView presence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.triallayoutsheet);

        // Get a Display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();

        // Load the resolution into a Point object
        Point resolution = new Point();
        display.getSize(resolution);

        //Save references to all the necessary UI elements that will be manipulated during the game
        fadeEffect = findViewById(R.id.fadeEffect);
        day = findViewById(R.id.dayCount);
        time = findViewById(R.id.time);
        foodCount = findViewById(R.id.foodCount);
        waterCount = findViewById(R.id.waterCount);
        textDisplayBackground = findViewById(R.id.textDisplayBackground);
        actionBar = findViewById(R.id.actionBar);
        textView = findViewById(R.id.textDisplay);
        button1 = findViewById(R.id.response1);
        button2 = findViewById(R.id.response2);
        levelUpStrength = findViewById(R.id.strengthValue);
        levelUpPerception = findViewById(R.id.perceptionValue);
        levelUpEndurance = findViewById(R.id.enduranceValue);
        statsToAssign = findViewById(R.id.statsToAssign);
        confirmStatsFrame = findViewById(R.id.confirmStatsFrame);
        statsFrame = findViewById(R.id.levelUpFrame);
        displayStats = findViewById(R.id.displayStats);
        playerStrength = findViewById(R.id.strength);
        playerPerception = findViewById(R.id.perception);
        playerEndurance = findViewById(R.id.endurance);
        presence = findViewById(R.id.presence);

        //Build the customized surfaceView and add it to frame holder for it so that it will position
        //Underneath the custom UI elements. If just added last it would place on top so by using
        //a framelayout that was prepositioned at the bottom it will show up underneath the UI.
        gm = new GameManager(this, resolution.x, resolution.y, this);

        FrameLayout surfaceViewContainer = findViewById(R.id.surfaceViewHolder);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT);

        gm.setLayoutParams(layoutParams);

        // Add the SurfaceView to the container
        surfaceViewContainer.addView(gm);
    }

    //This solution was provided by chatGPT to solve for updates to textView crashing the game.
    //Through further research I was able to confirm that updates to View elements have to happen
    //on the main UI thread and not during the game loop. All the methods below update specific
    //layout view elements.

    //Updates the event UI text
    public void updateEventUI(String updateText, String button1Text, String button2Text){
        runOnUiThread(new Runnable() {
            public void run() {
                textView.setText(updateText);
                button1.setText(button1Text);
                button2.setText(button2Text);

                textDisplayBackground.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE); // or View.INVISIBLE or View.GONE
                button1.setVisibility(View.VISIBLE);
                button2.setVisibility(View.VISIBLE);
            }
        });
    }


    //Updates the outcome ui (Uses the eventUI but one less button)
    public void loadOutcomeUI(String updateText, String button1Text){
        runOnUiThread(new Runnable() {
            public void run() {
                textView.setText(updateText);
                button1.setText(button1Text);

                textDisplayBackground.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                button1.setVisibility(View.VISIBLE);
                button2.setVisibility(View.GONE);
            }
        });
    }

    //Hides the eventUI
    public void hideEventUI(){
        runOnUiThread(new Runnable() {
            public void run() {
                textDisplayBackground.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);
                button1.setVisibility(View.GONE);
                button2.setVisibility(View.GONE);
            }
        });
    }

    //Displays the action Bar
    public void displayActionBar(){
        runOnUiThread(new Runnable() {
            public void run() {
                actionBar.setVisibility(View.VISIBLE);
            }
        });
    }

    //Hides the action Bar
    public void hideActionBar(){
        runOnUiThread(new Runnable() {
            public void run() {
                actionBar.setVisibility(View.GONE);
            }
        });
    }

    //Updates the food string UI display
    public void updateFoodCount(String newValue){
        runOnUiThread(new Runnable() {
            public void run() {
                foodCount.setText(newValue);
            }
        });
    }

    //Updates the water string UI display
    public void updateWaterCount(String newValue){
        runOnUiThread(new Runnable() {
            public void run() {
                waterCount.setText(newValue);
            }
        });
    }
    //Updates the clock string UI display
    public void updateTime(String newTime, String newDay){
        runOnUiThread(new Runnable() {
            public void run() {
                time.setText(newTime);
                day.setText(newDay);
            }
        });
    }

    public void showLevelUp(){
        runOnUiThread(new Runnable() {
            public void run(){
                confirmStatsFrame.setVisibility(View.VISIBLE);
                statsFrame.setVisibility(View.VISIBLE);
            }
        });
    }

    public void updateStats(StatsScreen stats){
        runOnUiThread(new Runnable() {
            public void run() {
                if (stats.getStatsToAssign() <= 0){
                    confirmStatsFrame.setVisibility(View.VISIBLE);
                }
                else {
                    confirmStatsFrame.setVisibility(View.GONE);
                }
                statsToAssign.setText("STATS TO ASSIGN: " + stats.getStatsToAssign());
                levelUpStrength.setText(" " + stats.getPlayerStrength() + " (+" + stats.getStrength() + ")");
                levelUpEndurance.setText(" " + stats.getPlayerEndurance() + " (+" + stats.getEndurance() + ")");
                levelUpPerception.setText(" " + stats.getPlayerPerception() + " (+" + stats.getPerception() + ")");
            }
        });
    }

    public void hideLevelUp(){
        runOnUiThread(new Runnable() {
            public void run(){
                confirmStatsFrame.setVisibility(View.GONE);
                statsFrame.setVisibility(View.GONE);
            }
        });
    }

    public void showStats(Player player){
        runOnUiThread(new Runnable() {
            public void run(){
                displayStats.setVisibility(View.VISIBLE);
                playerStrength.setText(" " + player.getStrength());
                playerPerception.setText(" " + player.getPerception());
                playerEndurance.setText(" " + player.getEndurance());
            }
        });
    }

    public void hideStats(){
        runOnUiThread(new Runnable() {
            public void run(){
                displayStats.setVisibility(View.GONE);
            }
        });
    }


    //All fade effects below use the same game logic. Set the colour for the effect, then using
    //ObjectAninmator class, animate the view elements by changing the alpha channel of the layer.
    //Red damage effect when player takes damage.
    public void tookDamage(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fadeEffect.setBackgroundColor(Color.RED);
                //Fadeout (to red)
                ObjectAnimator fadeOut = ObjectAnimator.ofFloat(fadeEffect, "alpha", 0f, 0.5f);
                fadeOut.setDuration(100);

                //Fadein (back to game)
                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(fadeEffect, "alpha", 0.5f, 0f);
                fadeIn.setDuration(100);

                // Create an AnimatorSet to manage the sequence of animations
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playSequentially(fadeOut, fadeIn);

                // Start the animation
                animatorSet.start();
            }
        });

    }
    public void updatePresenceEffect(float presenceAlpha){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator updatePresence = ObjectAnimator.ofFloat(presence, "alpha",presence.getAlpha(), presenceAlpha);
                updatePresence.setDuration(3000);
                updatePresence.start();
            }
        });
    }
    //Fade in effect at the start of the game.
    public void fadeIn(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fadeEffect.setBackgroundColor(Color.BLACK);
                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(fadeEffect, "alpha", 1f, 0f);
                fadeIn.setDuration(1000);
                fadeIn.start();
            }
        });
    }
    //Fade out effect used during game over.
    public void fadeOut(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fadeEffect.setBackgroundColor(Color.BLACK);
                ObjectAnimator fadeOut = ObjectAnimator.ofFloat(fadeEffect, "alpha", 0f, 1f);
                fadeOut.setDuration(1000);
                fadeOut.start();
            }
        });
    }
    public void fadeInAndOut(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fadeEffect.setBackgroundColor(Color.BLACK);
                ObjectAnimator fadeOut = ObjectAnimator.ofFloat(fadeEffect, "alpha", 0f, 1f);
                fadeOut.setStartDelay(500);//
                fadeOut.setDuration(1000);

                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(fadeEffect, "alpha", 1f, 0f);
                fadeIn.setStartDelay(500);
                fadeIn.setDuration(1000);

                //Create an AnimatorSet to manage the sequence of animations
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playSequentially(fadeOut, fadeIn);

                // Start the animation
                animatorSet.start();
            }
        });
    }

    //Returns to the game menu activity(all game objects will be lost)
    public void returnToMainMenu(){
        Intent intent = new Intent(this, FrostwoodMenuActivity.class);
        startActivity(intent);
    }

    public boolean saveSurvivalTime(int daysSurvived, int hoursSurvived, int minutesSurvived) {
        SharedPreferences preferences = getSharedPreferences("HighScoreFile", MODE_PRIVATE);

        //Get prior record (0s if no record stored)
        int priorDays = preferences.getInt("DaysSurvived", 0); //Returns 0 if no file
        int priorHours = preferences.getInt("HoursSurvived", 0); //Returns 0 if no file
        int priorMinutes = preferences.getInt("MinutesSurvived", 0); //Returns 0 if no file

        //Translate times into minutes for easier comparing
        int priorSurvivalInMinutes = (priorDays * 24 * 60) + (priorHours * 60) + priorMinutes;
        int currentSurvivalInMinutes = (daysSurvived * 24 * 60) + (hoursSurvived *60) + minutesSurvived;

        //Check if new time is a new record and save new record
        if(currentSurvivalInMinutes > priorSurvivalInMinutes) {
            //New survival record
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("DaysSurvived", daysSurvived);
            editor.putInt("HoursSurvived", hoursSurvived);
            editor.putInt("MinutesSurvived", minutesSurvived);
            editor.apply();
            return true;
        }
        return false;
    }

    // If the Activity is paused make sure to pause our thread
    @Override
    protected void onPause() {
        super.onPause();
        gm.pause();
    }

    // If the Activity is resumed make sure to resume our thread
    @Override
    protected void onResume() {
        super.onResume();
        gm.resume();
    }
}