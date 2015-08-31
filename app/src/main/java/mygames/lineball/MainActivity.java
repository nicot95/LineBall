package mygames.lineball;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class MainActivity extends Activity {

    // gameView will be the view of the game
    // It will also hold the logic of the game
    // and respond to screen touches as well
    GameView gameView;

    private ArrayList<RectF> lines = new ArrayList<RectF>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize gameView and set it as the view
        gameView = new GameView(this);
        setContentView(gameView);
    }

    // Here is our implementation of GameView
    // It is an inner class.
    // Note how the final closing curly brace }
    // is inside SimpleGameEngine

    // Notice we implement runnable so we have
    // A thread and can override the run method.
    class GameView extends SurfaceView implements Runnable {

        // This is our thread
        Thread gameThread = null;

        // This is new. We need a SurfaceHolder
        // When we use Paint and Canvas in a thread
        // We will see it in action in the draw method soon.
        SurfaceHolder ourHolder;

        // A boolean which we will set and unset
        // when the game is running- or not.
        volatile boolean playing;

        // Game is paused at the start
        boolean paused = true;

        // A Canvas and a Paint object
        Canvas canvas;
        Paint paint;

        // This variable tracks the game frame rate
        long fps;

        // This is used to help calculate the fps
        private long timeThisFrame;

        // The size of the screen in pixels
        int screenX;
        int screenY;

        /*
            Fields used by the ball tracker in order to check for gameOver state
        */
        private final int DIFFERENT_TYPES_OF_BALLS = 5;
        private int[] numberOfBallsPerType;

        ArrayList<Ball> balls = new ArrayList<>();
        int numBalls = 15; //TODO get rid of magic number

        // The score
        int score = 0;

        // Moves
        int movesLeft = 7;

        private BallTracker ballTracker;

        // When the we initialize (call new()) on gameView
        // This special constructor method runs
        public GameView(Context context) {
            // The next line of code asks the
            // SurfaceView class to set up our object.
            // How kind.
            super(context);
            this.setBackgroundColor(0X00000000);


            // Initialize ourHolder and paint objects
            ourHolder = getHolder();
            paint = new Paint();

            // Get a Display object to access screen details
            Display display = getWindowManager().getDefaultDisplay();
            // Load the resolution into a Point object
            Point size = new Point();
            display.getSize(size);

            screenX = size.x;
            screenY = size.y;

            numberOfBallsPerType = new int[DIFFERENT_TYPES_OF_BALLS];
            createBallsAndRestart(numBalls);
            this.ballTracker = new BallTracker(numberOfBallsPerType);

        }

        @Override
        public void run() {
            while (playing) {

                // Capture the current time in milliseconds in startFrameTime
                long startFrameTime = System.currentTimeMillis();

                // Update the frame
                if(!paused){
                    update();
                }

                // Draw the frame
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

        // Everything that needs to be updated goes in here
        // Movement, collision detection etc.
        public void update() {

            for(Ball b : balls) {
                if(Util.ballHitLineGameOver(ballTracker, b)) {
                    goToMenu();
                }
                b.checkWallCollision(screenX, screenY);
                b.update(fps);
            }

            /*
                increases the score and removes used balles from ArrayList
                for performance issues.
             */
            if (ballTracker.isReadyToCalculateScore()) {
                score += ballTracker.calculateScore();
                balls.removeAll(ballTracker.getBallsTracked());
                ballTracker.cleanUpBallsFields();
            }

            if (ballTracker.isGameOver()) {
                goToMenu();
            }

        }

        // Draw the newly updated scene
        public void draw() {

            // Make sure our drawing surface is valid or we crash
            if (ourHolder.getSurface().isValid()) {
                // Lock the canvas ready to draw
                canvas = ourHolder.lockCanvas();

                // Draw the background color
                canvas.drawColor(Color.argb(255, 0, 0, 0));

                // Choose the brush color for drawing
                //paint.setColor(Color.argb(255,  255, 255, 255));


                //Draw the lines connecting the already linked balls and a white border surrounding
                // the selected balls
                ArrayList<Ball> trackedBalls = ballTracker.getBallsTracked();
                paint.setStrokeWidth(5); // Increase width of line
                for (int i = 0; i < trackedBalls.size(); i++) {
                    Ball ball2 = trackedBalls.get(i);
                    if(i > 0) {
                        //draw lines
                        Ball ball1 = trackedBalls.get(i - 1);
                        paint.setColor(ballTracker.getColorChain());
                        canvas.drawLine(ball1.getX(), ball1.getY(), ball2.getX(),
                                ball2.getY(), paint);
                    }
                    //draw white border
                    paint.setColor(Color.WHITE);
                    canvas.drawCircle(ball2.getX(), ball2.getY(), ball2.getBallRadius()+2, paint);
                }



                // Draw the balls
                for(int i = 0; i < balls.size(); i++) {
                    Ball ball = balls.get(i);
                    paint.setColor(ball.getColor());
                    canvas.drawCircle(ball.getX(), ball.getY(), ball.getBallRadius(), paint);
                }


                // TODO draw the Score
                paint.setTextSize(40);
                canvas.drawText("Score: " + score, 30, 70, paint);

                // Draw everything to the screen
                ourHolder.unlockCanvasAndPost(canvas);
            }

        }

        // If SimpleGameEngine Activity is paused/stopped
        // shutdown our thread.
        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }

        }

        // If SimpleGameEngine Activity is started then
        // start our thread.
        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        // The SurfaceView class implements onTouchListener
        // So we can override this method and detect screen touches.
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                // Player has touched the screen
                case MotionEvent.ACTION_DOWN:
                    paused = false;
                    for(int i = balls.size()-1; i >= 0; i--) {
                        Ball b = balls.get(i);
                        if (b.intersects(motionEvent.getX(), motionEvent.getY())) {
                            ballTracker.trackBall(b);
                            break;
                        }
                    }
                    break;

                //In case we want swiping instead of just clicking
                case MotionEvent.ACTION_MOVE:

                    break;

                // Player has removed finger from screen, score is updated
                case MotionEvent.ACTION_UP:
                  // ballTracker.setReadyToCalculate();
                    break;
            }
            return true;
        }

        public void createBallsAndRestart(int numBalls) {
            for(int ballNum = 0; ballNum < numBalls; ballNum ++ ){
                   Ball b = new Ball(screenX, screenY);
                   balls.add(b);
                   addToNumBallsPerType(b.getColorSimple());
            }

            //ball.reset(screenX, screenY);

            // The score
            int score = 0;

            // Moves
            int movesLeft = 7;

        }

        private void addToNumBallsPerType(int ballColor) {
            numberOfBallsPerType[ballColor]++;
        }

        public void updateScore(int extraScore) {
            this.score += extraScore;
        }

    }

    private void goToMenu() {
        Intent intent = new Intent(gameView.getContext(), MainMenuActivity.class);
        intent.putExtra("score", gameView.score);
        startActivity(intent);
    }
    // This is the end of our BreakoutView inner class

    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();

        // Tell the gameView resume method to execute
        gameView.resume();
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();

        // Tell the gameView pause method to execute
        gameView.pause();
    }

}