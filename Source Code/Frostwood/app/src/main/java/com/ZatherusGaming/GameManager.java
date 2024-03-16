package com.ZatherusGaming;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
/*
 * FROSTWOOD CHRONICLES GAME
 *
 * AUTHOR:   Keith Mitchell
 * SID:      3178513
 * DATE:     December 2023
 * COURSE:   COMP486 - Mobile and Internet Game Development (Athabasca University)
 *
 * GameManager
 * Description:
 * Main game renderer and game loop.
 *
 * Usage:
 * When GameManger is initialized the game will start. No main usage of this class needed as its
 * all self contained and executed. This current build of the game will require this class to be
 * used with the layout XML and associated ACtivity class as many of the classes this sets up make
 * calls to the Activity class.
 *
 * Future Updates/Refactor:
 * If I were to start from scratch I would gut this class to being JUST the onDraw() call of a view.
 * If done correctly all drawing can be handled on one thread and the rest of the game loop and logic
 * can be seperated. Care would need to be taken on some areas for MutEX lock etc. But the
 * SurfaceView should only be handling the drawing of its view. It should not have an understanding
 * on what that data means. That should be handled by a Game Loop/manager/or Game State. Minor gripe
 * but it still works in its current form.
 */
public class GameManager extends SurfaceView implements Runnable{
    //Game Management Classes
    private FrostwoodActivity activity;
    private BitmapHandler bitmapHandler;
    private InputController inputController;
    private CollisionDetector collisionDetector;
    private GameState gameState;

    private Context context;
    private int screenWidth;
    private int screenHeight;

    //Thread Control
    private volatile boolean running;
    private Thread gameThread = null;

    //Drawing Objects
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;

    //Timing Variables
    private long startFrameTime;
    private long timeThisFrame;
    private long fps;

    //Post Loading Launch
    private boolean isInitialized = false;

    //Set to true for debugging information
    private boolean debugging = false;

    GameManager(Context context, int screenWidth, int screenHeight, FrostwoodActivity activity){
        super(context);
        this.context = context;
        this.activity = activity;

        // Initialize our drawing objects
        ourHolder = getHolder();
        paint = new Paint();

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void initializeGame(){
        bitmapHandler = new BitmapHandler(context, screenWidth, screenHeight);
        collisionDetector = new CollisionDetector(activity);
        gameState = new GameState(context, this, bitmapHandler, activity, collisionDetector);
        inputController = new InputController(gameState);
    }

    @Override
    public void run() {
        if(!isInitialized){
            initializeGame();
            isInitialized = true;
        }

        while (running) {
            startFrameTime = System.currentTimeMillis();

            update();
            draw();

            // Calculate the fps this frame
            // We can then use the result to
            // time animations and more.

            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }
        }
    }

    private void collisionDetection(){
        //If in the tile state check the players position to update compass direction
        if(gameState.getState() == GameState.State.TILE) {
            collisionDetector.edgeDetection(gameState.getPlayer(), screenWidth, screenHeight);

            //Cycle through the visable objects and check for player collision
            //Currently just water and food
            for(GameObject element : gameState.getGameObjects()){
                if(element.isVisable()){
                    if(collisionDetector.checkObjects(gameState.getPlayer(), element)){
                        gameState.getPlayer().onOverlap(element, gameState);
                    };
                }
            }
        }
    }

    private void update() {
        collisionDetection();//Collision detection
        gameState.update(fps);//Where most of the game logic is
    }

    private void draw() {
        if (ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas(); //Lock the canvas while drawing
            paint.setColor(Color.argb(255, 0, 0, 0));
            canvas.drawColor(Color.argb(255, 0, 0, 0));

            //Draw the background
            Background currentBackground = gameState.getGameMap().getCurrentTile().getBackground();
            canvas.drawBitmap(currentBackground.getBitmap(), currentBackground.getXPosition(), currentBackground.getYPosition(), paint);

            //Draw the player
            Player temporary = gameState.getPlayer();
            if(temporary.getFacing() == Actor.Facing.LEFT){
                Bitmap flippedBitmap = bitmapHandler.flipBitmap(temporary.getCurrentSprite(System.currentTimeMillis()));
                canvas.drawBitmap(flippedBitmap, temporary.getXPosition(), temporary.getYPosition(), paint);
            }
            else
                canvas.drawBitmap(temporary.getCurrentSprite(System.currentTimeMillis()), temporary.getXPosition(), temporary.getYPosition(), paint);

            //Draw gameObjects by layer (This is overkill right now as multilayer is not being used and there are minimal sprites being drawn.
            for(int layer = -2; layer <= 2; layer++){
                for(GameObject index : gameState.getGameObjects()){
                    if(index.getZPosition() == layer){
                        if(index.isVisable()) {
                            canvas.drawBitmap(index.getCurrentSprite(System.currentTimeMillis()), index.getXPosition(), index.getYPosition(), paint);
                        }
                    }
                }
            }


            //Draw UI
            UserInterface ui = gameState.getUserInterface();
            for(UIElement element: ui.getUserInterfaceElements()){
                Bitmap rotatedBitmap;
                if(element.isVisable()){
                    rotatedBitmap = bitmapHandler.rotateBitmap(element.getSprite().getCurrentSprite(System.currentTimeMillis()), element.getRotation());
                    canvas.drawBitmap(rotatedBitmap, element.getXPosition(), element.getYPosition(), paint);
                }
            }

            //Draw debugging info
            if(debugging) {
                paint.setTextSize(50);
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(Color.argb(255, 255, 0, 0));
                canvas.drawText("Number of Sprites:" + bitmapHandler.numberOfLoadedBitmaps, 10, 150, paint);
                canvas.drawText("Sprite Loads Skipped:" + bitmapHandler.numberOfSkippedLoads, 10, 190, paint);
                canvas.drawText("FPS:" + fps, 10, 500, paint);

                canvas.drawText("Mana:" + gameState.getPlayer().getMana(), 10, 550, paint);
                canvas.drawText("MaxMana:" + gameState.getPlayer().getMaxMana(), 10, 600, paint);
                canvas.drawText("Stamina:" + gameState.getPlayer().getStamina(), 10, 650, paint);
                canvas.drawText("MaxStamina:" + gameState.getPlayer().getMaxStamina(), 10, 700, paint);
                canvas.drawText("Health:" + gameState.getPlayer().getHealth(), 10, 750, paint);
                canvas.drawText("MaxHealth:" + gameState.getPlayer().getMaxHealth(), 10, 800, paint);
            }

            // Unlock and draw the scene
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        //Player could tap before the inputController had properly loaded
        if(inputController != null) {
            inputController.handleInput(motionEvent);
        }
        return true;
    }

    // Clean up our thread if the game is interrupted or the player quits
    public void pause() {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("error", "failed to pause thread");
        }
    }

    // Make a new thread and start it
    // Execution moves to our run method
    public void resume() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    //Getters
    public InputController getInputController(){
        return inputController;
    }
    public FrostwoodActivity getActivity(){
        return activity;
    }
}
