package mygames.lineball;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import java.util.List;

/**
 * Created by nico on 07/09/15.
 */
public class TutorialActivity extends Activity {

    GameView tutorialView;

    private int NUM_BALLS = 2;
    private int DIFFERENT_BALLS = 1;
    private int RED = 0;
    public static int TEXTBOX_SIZE = 200;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get a Display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();
        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);
        tutorialView = new tutorialView(this, size.x, size.y - TEXTBOX_SIZE, NUM_BALLS, DIFFERENT_BALLS, RED);
        setContentView(tutorialView);

    }

    static class tutorialView extends GameView {

        private BallTracker ballTracker;
        private float touchX, touchY;

        private Tutorial_State tutorial_state;
        Paint whitePaint = new Paint();

        public enum Tutorial_State {
            INITIAL,
            FIRST_BALL,
            SECOND_BALL,
            SHAPE_COMPLETE;

            public Tutorial_State getNext() {
                return values()[(ordinal()+1) % values().length];
            }

        }

        public tutorialView(Context context, int screenWidth, int screenHeight, int num_balls,
                            int different_balls, int color) {
            super(context, screenWidth, screenHeight, num_balls, different_balls, color);
            this.ballTracker = new BallTracker(numberOfBallsPerType);
            this.tutorial_state = Tutorial_State.INITIAL;
            whitePaint.setAntiAlias(true);
            whitePaint.setColor(Color.WHITE);
            whitePaint.setTextSize(40);

        }

        @Override
        public void update() {

            synchronized (balls) {
                for (Ball b : balls) {
                    if (Util.ballHitLineGameOver(ballTracker, b)) {
                        ballTracker.setGameStateToLineContact();
                        playing = false;
                    }
                    b.checkWallCollision(screenWidth, screenHeight);
                    b.update(fps);
                }
            }
        }

        @Override
        public void draw() {

            // Make sure our drawing surface is valid or we crash
            if (ourHolder.getSurface().isValid()) {
                paint.setAntiAlias(true);
                // Lock the canvas ready to draw
                canvas = ourHolder.lockCanvas();

                // Draw the background color
                canvas.drawColor(Color.BLACK);

                drawCommentsBox();

                //Draw the lines connecting the already linked balls and a white border surrounding
                // the selected balls
                List<Ball> trackedBalls = ballTracker.getBallsTracked();
                Util.drawLines(canvas, ballTracker, touchX, touchY);

                // Draw the balls
                synchronized (balls) {
                    for (Ball b : balls) {
                        b.draw(paint, canvas);
                    }
                }

                //Draw the game_overState

                    drawComment();
                    //drawGameOverText(50, ballTracker.getGameState(), screenHeight / 2, paint);
                    //Games.Leaderboards.submitScore(mGoogleApiClient, LEADERBOARD_ID, score);
                    //startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
                    //        LEADERBOARD_ID), REQUEST_LEADERBOARD);



                // Draw everything to the screen
                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        public void drawComment() {
            String comment = "";
            String comment2 = "";
            String comment3 = "";
            switch (tutorial_state) {
                case INITIAL:        comment = "Touch and hold any ball!";
                                     break;
                case FIRST_BALL    : comment = "Very well, now that the ball is selected,";
                                     comment2 = "hold the finger and try selecting";
                                     comment3 = "another ball of the same color.";
                                     break;
                case SECOND_BALL   : comment = "Well done! Now, keep holding the finger";
                                     comment2 = "and touch the initial ball to finish";
                                     comment3 = "the shape.";
                                     break;
                case SHAPE_COMPLETE:
                                     comment = "Shape is complete! Both balls vanish";
                                     comment2 = "and the first step of tutorial is";
                                     comment3 = "complete!";
            }

            canvas.drawText(comment, 10, screenHeight + 50, whitePaint);
            canvas.drawText(comment2, 10, screenHeight + 110, whitePaint);
            canvas.drawText(comment3, 10, screenHeight + 170, whitePaint);

        }

        public void drawCommentsBox() {
            whitePaint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(0, screenHeight, screenWidth, screenHeight + TEXTBOX_SIZE, whitePaint);
            whitePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        }

        // The SurfaceView class implements onTouchListener
        // So we can override this method and detect screen touches.
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                // Player has touched the screen
                case MotionEvent.ACTION_DOWN:
                    if (!ballTracker.isGameOver()) {
                        paused = false;
                        //stop = false;
                        touchX = motionEvent.getX();
                        touchY = motionEvent.getY();
                        if (ballTracker.getBallsTracked().isEmpty()) {
                            synchronized (balls) {
                                for (Ball b : balls) {
                                    if (b.intersects(touchX, touchY)) {
                                        ballTracker.trackBall(b);
                                        nextState();
                                        break;
                                    }
                                }
                            }
                        }
                    } else {

                    }
                    break;

                //In case we want swiping instead of just clicking
                case MotionEvent.ACTION_MOVE:
                    if (!ballTracker.isGameOver()) {
                        paused = false;
                        touchX = motionEvent.getX();
                        touchY = motionEvent.getY();
                        synchronized (balls) {
                            for (Ball b : balls) {
                                if (b.intersects(touchX, touchY)) {
                                    int start_balls = ballTracker.getBallsTracked().size();
                                    ballTracker.trackBall(b);
                                    if(ballTracker.getBallsTracked().size() > start_balls) {
                                        tutorial_state = tutorial_state.getNext();
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    break;

                // Player has removed finger from screen, score is updated
                case MotionEvent.ACTION_UP:
                    if (!ballTracker.isGameOver() && ballTracker.isReadyToCalculateScore()) {
                        ballTracker.clearShape();
                        balls.removeAll(ballTracker.getBallsTracked());
                        ballTracker.cleanUpBallsFields();
                        nextState();
                        //ballTracker.checkForShape();

                    } else if (!ballTracker.isGameOver()) {
                        ballTracker.resumeMovement();
                        tutorial_state = Tutorial_State.INITIAL;

                    }
                    break;
            }
            return true;
        }

        public void nextState() {

            if(tutorial_state != Tutorial_State.SHAPE_COMPLETE) {
                tutorial_state = tutorial_state.getNext();
            }


        }
    }
        @Override
        protected void onResume() {
            super.onResume();

            tutorialView.resume();
        }

        // This method executes when the player quits the game
        @Override
        protected void onPause() {
            super.onPause();

            tutorialView.pause();
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
        }

}
