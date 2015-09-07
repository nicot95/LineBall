package mygames.lineball;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
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

    private int NUM_BALLS = 3;
    private int DIFFERENT_BALLS = 1;
    private int RED = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get a Display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();
        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);
        tutorialView = new tutorialView(this, size.x, size.y, NUM_BALLS, DIFFERENT_BALLS, RED);
        setContentView(tutorialView);

    }

    class tutorialView extends GameView {

        private BallTracker ballTracker;
        private float touchX, touchY;

        public tutorialView(Context context, int screenWidth, int screenHeight, int num_balls,
                            int different_balls, int color) {
            super(context, screenWidth, screenHeight, num_balls, different_balls, color);
            this.ballTracker = new BallTracker(numberOfBallsPerType);

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
                if (ballTracker.isGameOver()) {
                    //drawGameOverText(50, ballTracker.getGameState(), screenHeight / 2, paint);
                    //Games.Leaderboards.submitScore(mGoogleApiClient, LEADERBOARD_ID, score);
                    //startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
                    //        LEADERBOARD_ID), REQUEST_LEADERBOARD);

                }

                // Draw everything to the screen
                ourHolder.unlockCanvasAndPost(canvas);
            }
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
                        touchX = motionEvent.getX();
                        touchY = motionEvent.getY();
                        if (ballTracker.getBallsTracked().isEmpty()) {
                            synchronized (balls) {
                                for (Ball b : balls) {
                                    if (b.intersects(touchX, touchY)) {
                                        ballTracker.trackBall(b);
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
                                    ballTracker.trackBall(b);
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
                        //ballTracker.checkForShape();

                    } else if (!ballTracker.isGameOver()) {
                        ballTracker.resumeMovement();
                    }
                    break;
            }
            return true;
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
