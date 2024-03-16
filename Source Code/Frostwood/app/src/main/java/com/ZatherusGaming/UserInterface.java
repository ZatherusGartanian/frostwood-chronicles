package com.ZatherusGaming;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
/*
 * FROSTWOOD CHRONICLES GAME
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     December 2023
 * COURSE:   COMP486 - Mobile and Internet Game Development (Athabasca University)
 *
 * UserInterface
 * Description:
 * Handles the display of the UI elements in the game. Calls the FrostwoodActivity to update the
 * view UIs where necessary as those must be handled by the main UI thread.
 *
 * Usage:
 * After initialization the user interface will only update when calls are made directly to it.
 * Each method call updates or runs a specific part of the UI. See each method for more details on
 * each specific use case.
 *
 * Future Updates/Refactor:
 * The current UI layout is not ideal but I left it so that I could show me drawing some UI
 * elements in surfaceView. However, the way I want to handle them is through the view updates like
 * I do with all the other elements. I think the only UI element that I would still handle direct
 * drawing in surface view is the arrows as they are animated and even then I can run animations
 * on the UI elements.
 */
public class UserInterface{
    private ArrayList<UIElement> userInterfaceElements;
    private FrostwoodActivity activity;
    private GameState gameState;

    //Health Bar Elements
    private UIElement healthBar;
    private UIElement healthTotal;
    private UIElement maxHealth;
    private int lastKnownHealth;
    private int lastKnownMaxHealth;

    //Mana Bar Elements
    private UIElement manaBar;
    private UIElement manaTotal;
    private UIElement maxMana;
    private int lastKnownMana;
    private int lastKnownMaxMana;

    //Stamina Bar Elements
    private UIElement staminaBar;
    private UIElement staminaTotal;
    private UIElement maxStamina;
    private int lastKnownStamina;
    private int lastKnownMaxStamina;

    //UI Directional Arrows
    private UIElement northArrow;
    private UIElement eastArrow;
    private UIElement southArrow;
    private UIElement westArrow;

    //UI Game Event Variables
    private ButtonListener buttonListener;

    UserInterface(BitmapHandler bitmapHandler, FrostwoodActivity activity, GameState gameState) {
        this.activity = activity;
        this.gameState = gameState;

        //Event Buttons
        Button button1;
        Button button2;
        //Action Buttons
        Button campButton;
        Button forageButton;
        Button drinkButton;
        Button eatButton;
        //Stat Buttons
        Button strengthPlus;
        Button strengthMinus;
        Button perceptionPlus;
        Button perceptionMinus;
        Button endurancePlus;
        Button enduranceMinus;
        Button levelUpConfirm;
        Button showStats;



        //Get a reference to the UI buttons
        userInterfaceElements = new ArrayList<UIElement>();
        button1 = activity.findViewById(R.id.response1);
        button2 = activity.findViewById(R.id.response2);
        campButton = activity.findViewById(R.id.campButton);
        eatButton = activity.findViewById(R.id.eatButton);
        forageButton = activity.findViewById(R.id.forageButton);
        drinkButton = activity.findViewById(R.id.drinkButton);
        strengthPlus = activity.findViewById(R.id.plusStrength);
        strengthMinus = activity.findViewById(R.id.minusStrength);
        perceptionMinus = activity.findViewById(R.id.minusPerception);
        perceptionPlus = activity.findViewById(R.id.plusPerception);
        enduranceMinus = activity.findViewById(R.id.minusEndurance);
        endurancePlus = activity.findViewById(R.id.plusEndurance);
        levelUpConfirm = activity.findViewById(R.id.confirmStats);
        showStats = activity.findViewById(R.id.showStats);

        // Set listeners for each button
        buttonListener = new ButtonListener(this);
        button1.setOnClickListener(buttonListener);
        button2.setOnClickListener(buttonListener);
        campButton.setOnClickListener(buttonListener);
        forageButton.setOnClickListener(buttonListener);
        drinkButton.setOnClickListener(buttonListener);
        eatButton.setOnClickListener(buttonListener);
        strengthPlus.setOnClickListener(buttonListener);
        strengthMinus.setOnClickListener(buttonListener);
        perceptionPlus.setOnClickListener(buttonListener);
        perceptionMinus.setOnClickListener(buttonListener);
        endurancePlus.setOnClickListener(buttonListener);
        enduranceMinus.setOnClickListener(buttonListener);
        levelUpConfirm.setOnClickListener(buttonListener);
        showStats.setOnClickListener(buttonListener);

        initializeUI(bitmapHandler);
    }

    //Order objects are added to the array will determine the draw order
    //First object in will be the first drawn.
    private void initializeUI(BitmapHandler bitmapHandler) {
        int bitmapWidth;
        int bitmapHeight;
        float xPosition;
        float yPosition;

        //HEALTH BAR CREATION
        //Get y distance from the bottom of the UIFrame view to position by resolution.
        Vector location = new Vector(30, activity.findViewById(R.id.topUIFrame).getHeight() + 20, 0);
        userInterfaceElements.add(new UIElement(location, "dropshadow", 1, 1, bitmapHandler));
        healthBar = new UIElement(location, "barbackground", 1, 1, bitmapHandler);
        healthBar.setWorldLocation(location);
        userInterfaceElements.add(healthBar);

        //This is done after loading a sprite to get the scaled values.
        //Get bitmapwidth and height values for all the bars to use for positioning
        bitmapWidth = healthBar.getSprite().getWidth(); //Get Scaled width of background bars for UI
        bitmapHeight = healthBar.getSprite().getHeight(); //Get Scaled height of background bars for UI
        xPosition = healthBar.getXPosition(); //Position to offset from background bar
        yPosition = healthBar.getYPosition(); //Position to offset from background bar

        //Load a bar first like above to get the scaled values afterwards. Then set based on that.
        maxHealth = new UIElement(location, "maxhealth", 1, 1, bitmapHandler);

        //Then calculate the offset necessary for the placement of the bars on the bar background.
        //Offsets wont change between bars.
        //Offsets are half the difference in their sizes (Bar should be centered on the background)
        float xOffSet = ((float)(bitmapWidth - maxHealth.getSprite().getWidth())/2);
        float yOffSet = ((float)(bitmapHeight - maxHealth.getSprite().getHeight())/2);
        xPosition = xPosition + xOffSet;
        yPosition = yPosition + yOffSet;
        location = new Vector(xPosition, yPosition, 0);
        maxHealth.setWorldLocation(location);
        userInterfaceElements.add(maxHealth);

        //Load health Total with same values
        healthTotal = new UIElement(location, "healthtotal", 1, 1, bitmapHandler);
        userInterfaceElements.add(healthTotal);

        //STAMINA BAR CREATION
        //Calculate draw position of stamina bar based on health bar and edge of screen for positioning for resolution differences
        xPosition = (float)(bitmapHandler.getScreenWidth()-bitmapWidth-30);
        yPosition = healthBar.getYPosition();
        location = new Vector(xPosition, yPosition, 0);

        //Load shadow element then background
        userInterfaceElements.add(new UIElement(location, "dropshadow", 1, 1, bitmapHandler));
        staminaBar = new UIElement(location, "barbackground", 1, 1, bitmapHandler);
        userInterfaceElements.add(staminaBar);

        //Calculate offsets for Stamina bars
        xPosition = xPosition + xOffSet;
        yPosition = yPosition + yOffSet;
        location = new Vector(xPosition, yPosition, 0);
        maxStamina = new UIElement(location, "maxstamina", 1, 1, bitmapHandler);
        userInterfaceElements.add(maxStamina);
        staminaTotal = new UIElement(location, "staminatotal", 1, 1, bitmapHandler);
        userInterfaceElements.add(staminaTotal);

        //MANA BAR CREATION
        //Get new location first from Stamina bar position.
        yPosition = staminaBar.getYPosition() + bitmapHeight+ 30;
        xPosition = staminaBar.getXPosition();
        location = new Vector(xPosition, yPosition, 0);

        //With new location known first load in drop shadow and then the necessary elements on top.
        userInterfaceElements.add(new UIElement(location, "dropshadow", 1, 1, bitmapHandler));
        manaBar = new UIElement(location, "barbackground", 1, 1, bitmapHandler);
        userInterfaceElements.add(manaBar);

        //Calculate offsets for mana bars
        xPosition = xPosition + xOffSet;
        yPosition = yPosition + yOffSet;
        location = new Vector(xPosition, yPosition, 0);
        maxMana = new UIElement(location, "maxmana", 1, 1, bitmapHandler);
        userInterfaceElements.add(maxMana);
        manaTotal = new UIElement(location, "manatotal", 1, 1, bitmapHandler);
        userInterfaceElements.add(manaTotal);

        //ARROW CREATION
        //Load sprite then with scaled values set position in centre of screen orientated at each edge.
        //Top and Bottom sprites must also take into account the added UI elements.

        //East Arrow
        eastArrow = new UIElement(location, "direction", 4, 6, bitmapHandler);
        bitmapWidth = eastArrow.getSprite().getWidth();
        bitmapHeight = eastArrow.getSprite().getHeight();
        xPosition = ((float) bitmapHandler.getScreenWidth()) - bitmapWidth - 5;
        yPosition = ((float) (bitmapHandler.getScreenHeight() / 2)) - ((float) bitmapHeight / 2);
        eastArrow.setWorldLocation(new Vector(xPosition, yPosition, 0));
        eastArrow.getSprite().setAsReversable();
        userInterfaceElements.add(eastArrow);

        //South Arrow
        xPosition = ((float)(bitmapHandler.getScreenWidth()/2)) - ((float)(bitmapWidth/2));
        yPosition = ((float)bitmapHandler.getScreenHeight()) - bitmapHeight - 5 - activity.findViewById(R.id.bottomUIFrame).getHeight();;
        location = new Vector (xPosition, yPosition, 0);
        southArrow = new UIElement(location, "direction", 4, 6, bitmapHandler);
        southArrow.setRotation(90);
        southArrow.getSprite().setAsReversable();
        userInterfaceElements.add(southArrow);

        //West Arrow
        xPosition = 5f;
        yPosition = ((float)bitmapHandler.getScreenHeight()/2) - ((float)bitmapHeight/2);
        location = new Vector (xPosition, yPosition, 0);
        westArrow = new UIElement(location, "direction", 4, 6, bitmapHandler);
        westArrow.setRotation(180);
        westArrow.getSprite().setAsReversable();
        userInterfaceElements.add(westArrow);

        //North Arrow
        xPosition = ((float)(bitmapHandler.getScreenWidth()/2)) - ((float)bitmapWidth/2);
        yPosition = 5f + activity.findViewById(R.id.topUIFrame).getHeight();
        location = new Vector (xPosition, yPosition, 0);
        northArrow = new UIElement(location, "direction", 4, 6, bitmapHandler);
        northArrow.setRotation(270);
        northArrow.getSprite().setAsReversable();
        userInterfaceElements.add(northArrow);

        //Ensure UI starts with the correct values
        updateInventoryUI();
        updateClock();
        updateStatUI();
        updateArrowUI();
        activity.fadeIn(); //Fade into the game screen
    }

    //Set the visability of each arrow based on the current tiles values.
    //Update when arriving at a new tile
    public void updateArrowUI(){
        if(gameState.getGameMap().getCurrentTile().eastExit == false){
            eastArrow.setVisability(false);
        }
        else
            eastArrow.setVisability(true);

        if(gameState.getGameMap().getCurrentTile().southExit == false){
            southArrow.setVisability(false);
        }
        else
            southArrow.setVisability(true);

        if(gameState.getGameMap().getCurrentTile().westExit == false){
            westArrow.setVisability(false);
        }
        else
            westArrow.setVisability(true);

        if(gameState.getGameMap().getCurrentTile().northExit == false){
            northArrow.setVisability(false);
        }
        else
            northArrow.setVisability(true);
    }

    //Whenever a stat is changed run this update
    public void updateStatUI(){
        updateHealthUI();
        updateManaUI();
        updateStaminaUI();
    }

    //FOR ALL UPDATE UI ELEMENTS THE LOGIC IS THE SAME JUST EFFECTING DIFFERENT ELEMENTS
    //IN FUTURE REFACTOR TRY AND COLLAPSE CODE INTO ONE SCALING METHOD THAT DOES THE SCALING PARTS

    //Logic here is to rescale the bars to represent the actual values they represent
    private void updateHealthUI(){
        int playerHealth = gameState.getPlayer().getHealth();
        int playerMaxHealth = gameState.getPlayer().getMaxHealth();

        //Check if the an update is necessary
        if(playerHealth != lastKnownHealth) {
            //Get scale amount
            float scaleAmount = gameState.getPlayer().getHealthPercent();
            if(scaleAmount > 0.0f) {
                //Rescale the bitmap from the source and replace sprite array
                Bitmap tempBitmap = healthTotal.getSourceBitmap();
                int newWidth = (int) (tempBitmap.getWidth() * scaleAmount);
                int newHeight = tempBitmap.getHeight();
                Bitmap[] scaledArray = new Bitmap[1];
                scaledArray[0] = Bitmap.createScaledBitmap(tempBitmap, newWidth, newHeight, true);
                healthTotal.getSprite().replaceSpriteArray(scaledArray);
            }
            else{
                //If scale amount is O or not positive then make bar invisible
                healthTotal.setVisability(false);
            }
            //Update last known value to the new update
            lastKnownHealth = playerHealth;
        }

        //Repeat of above logic
        if(playerMaxHealth != lastKnownMaxHealth) {
            float scaleAmount = gameState.getPlayer().getMaxHealthPercent();
            if(scaleAmount > 0.0f) {
                Bitmap tempBitmap = maxHealth.getSourceBitmap();
                int newWidth = (int) (tempBitmap.getWidth() * scaleAmount);
                int newHeight = tempBitmap.getHeight();
                Bitmap[] scaledArray = new Bitmap[1];
                scaledArray[0] = Bitmap.createScaledBitmap(tempBitmap, newWidth, newHeight, true);
                maxHealth.getSprite().replaceSpriteArray(scaledArray);
            }
            else{
                maxHealth.setVisability(false);
            }
            lastKnownMaxHealth = playerMaxHealth;
        }

        //Not necessary as either of these states ends the game but in the offchance that I added lives or revives.
        /*
        if(playerHealth > 0){
            healthTotal.setVisability(true);
        }
        if(playerMaxHealth > 0){
            maxHealth.setVisability(true);
        }
         */
    }

    //See updateHealthUI
    private void updateManaUI(){
        int playerMana = gameState.getPlayer().getMana();
        int playerMaxMana = gameState.getPlayer().getMaxMana();

        if(playerMana != lastKnownMana) {
            float scaleAmount = gameState.getPlayer().getManaPercent();
            if(scaleAmount > 0.0f) {
                Bitmap tempBitmap = manaTotal.getSourceBitmap();
                int newWidth = (int) (tempBitmap.getWidth() * scaleAmount);
                int newHeight = tempBitmap.getHeight();
                Bitmap[] scaledArray = new Bitmap[1];
                scaledArray[0] = Bitmap.createScaledBitmap(tempBitmap, newWidth, newHeight, true);
                manaTotal.getSprite().replaceSpriteArray(scaledArray);
            }

            else{
                manaTotal.setVisability(false);
            }
            lastKnownMana = playerMana;
        }

        if(playerMaxMana != lastKnownMaxMana) {
            float scaleAmount = gameState.getPlayer().getMaxManaPercent();
            if(scaleAmount > 0.0f) {
                Bitmap tempBitmap = maxMana.getSourceBitmap();
                int newWidth = (int) (tempBitmap.getWidth() * scaleAmount);
                int newHeight = tempBitmap.getHeight();
                Bitmap[] scaledArray = new Bitmap[1];
                scaledArray[0] = Bitmap.createScaledBitmap(tempBitmap, newWidth, newHeight, true);
                maxMana.getSprite().replaceSpriteArray(scaledArray);
            }

            else{
                maxMana.setVisability(false);
            }
            lastKnownMaxMana = playerMaxMana;
        }

        //Update visability if values go back above 0
        if(playerMana > 0){
            manaTotal.setVisability(true);
        }
        if(playerMaxMana > 0){
            maxMana.setVisability(true);
        }
    }

    //See updateHealthUI
    private void updateStaminaUI(){
        int playerStamina = gameState.getPlayer().getStamina();
        int playerMaxStamina = gameState.getPlayer().getMaxStamina();


        if(playerStamina != lastKnownStamina) {
            float scaleAmount = gameState.getPlayer().getStaminaPercent();
            if(scaleAmount > 0.0f) {
                Bitmap tempBitmap = staminaTotal.getSourceBitmap();
                int newWidth = (int) (tempBitmap.getWidth() * scaleAmount);
                int newHeight = tempBitmap.getHeight();
                Bitmap[] scaledArray = new Bitmap[1];
                scaledArray[0] = Bitmap.createScaledBitmap(tempBitmap, newWidth, newHeight, true);
                staminaTotal.getSprite().replaceSpriteArray(scaledArray);
            }
            else{
                staminaTotal.setVisability(false);
            }
            lastKnownStamina = playerStamina;
        }

        if(playerMaxStamina != lastKnownMaxStamina) {
            float scaleAmount = gameState.getPlayer().getMaxStaminaPercent();
            if(scaleAmount > 0.0f) {
                Bitmap tempBitmap = maxStamina.getSourceBitmap();
                int newWidth = (int) (tempBitmap.getWidth() * scaleAmount);
                int newHeight = tempBitmap.getHeight();
                Bitmap[] scaledArray = new Bitmap[1];
                scaledArray[0] = Bitmap.createScaledBitmap(tempBitmap, newWidth, newHeight, true);
                maxStamina.getSprite().replaceSpriteArray(scaledArray);
            }

            else{
                maxStamina.setVisability(false);
            }
            lastKnownMaxStamina = playerMaxStamina;
        }

        //Update visability if values go back above 0
        if(playerStamina > 0){
            staminaTotal.setVisability(true);
        }
        if(playerMaxStamina > 0){
            maxStamina.setVisability(true);
        }
    }


    //Used to sync up the arrow animations
    //Called when player travels to a new screen
    public void resetSpriteFrameCounter(){
        for (UIElement element: getUserInterfaceElements()){
            element.getSprite().resetSpriteFrameCount();
        }
    }

    //Send the game event text to the event UI View element.
    public void loadEventUI(GameEvent event){
        activity.updateEventUI(event.getEventText(), event.getButton1Text(), event.getButton2Text());
    }

    public void loadPresenceUI(String eventText, String button1, String button2){
        activity.updateEventUI(eventText, button1, button2);
    }

    //Send the game event outcome text to the outcomeUI View element.
    public void loadOutcomeUI(String outcome){
        activity.loadOutcomeUI(outcome, "Confirm");
    }

    //Based on the current game state, pass the button press to the gameState
    //This should likely just be handled within the GameState in later refactors to avoid the UI
    //needing to understand the state of the game.
    public void handleButtonPress(View button){
        if(gameState.getState() == GameState.State.EVENT) {
            gameState.resolveEvent(button);
        }
        else if(gameState.getState() == GameState.State.EVENTOUTCOME){
            gameState.outcomeConfirmed();
            activity.hideEventUI();
        }
        else if (gameState.getState() == GameState.State.ACTIONMENU){
            gameState.actionBarEvent(button);
            activity.hideActionBar();
        }
        else if (gameState.getState() == GameState.State.GAMEOVER){
            returnToMainMenu();
        }
        else if (gameState.getState() == GameState.State.TILE){
            gameState.uiButtonPressed(button);
        }
        else if (gameState.getState() == GameState.State.LEVELUP){
            gameState.levelUpScreen(button);
        }
        else if (gameState.getState() == GameState.State.PRESENCE){
            gameState.presenceAction(button);
        }
    }
    public void showLevelUp(StatsScreen statsScreen){
        activity.showLevelUp();
        updateStats(statsScreen);
    }

    public void updateStats(StatsScreen statsScreen){
        activity.updateStats(statsScreen);
    }
    public void completeLevelUp(){
        activity.hideLevelUp();
    }

    public void showStats(Player player){
        activity.showStats(player);
    }

    public void hideStats(){
        gameState.statScreenGone();
        activity.hideStats();
    }

    //Call this method whenever the players inventory values change
    public void updateInventoryUI(){
        activity.updateFoodCount(String.valueOf(gameState.getPlayer().getFoodCount()));
        activity.updateWaterCount(String.valueOf(gameState.getPlayer().getWaterCount()));
    }

    //Display and Hide the Action Bar
    public void displayActionBar(){
        gameState.actionBarDisplayed();
        activity.displayActionBar();
    }
    public void hideActionBar(){
        gameState.actionBarGone();
        activity.hideActionBar();
    }

    //Update the string being used to represent the in game time
    public void updateClock(){
        String hours;
        String minutes;
        if(gameState.getHours() <10) {
            hours = "0" + gameState.getHours();
        }
        else
            hours = String.valueOf(gameState.getHours());
        if(gameState.getMinutes()<10){
            minutes = "0" + gameState.getMinutes();
        }
        else
            minutes = String.valueOf(gameState.getMinutes());

        String days = "Day " + gameState.getDays();
        activity.updateTime(hours + ":" + minutes, days);
    }

    public void updatePresenceEffect(float presenceValue){
        activity.updatePresenceEffect(presenceValue);
    }

    //Visual effects created by changing the alpha channel of screen wide view.
    public void campingFadeEffect(){activity.fadeInAndOut();} //Fades in and out
    public void damageEffect(){ activity.tookDamage();} //Red flash
    public void gameOverFade(){ activity.fadeOut(); } //Fade to black

    //End game and return to first menu screen
    public void returnToMainMenu(){
        activity.returnToMainMenu();
    }

    //Getters
    public ArrayList<UIElement> getUserInterfaceElements(){
        return userInterfaceElements;
    }
    public int getBottomOfStatUI(){return (int)manaBar.getYPosition() + manaBar.getSprite().getHeight();}
}

