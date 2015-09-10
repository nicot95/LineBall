package mygames.lineball.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;

import java.util.List;

import mygames.lineball.BallGenerators.InitialStateBallGenerator;
import mygames.lineball.GameLogic.BallTracker;
import mygames.lineball.Balls.Ball;
import mygames.lineball.Balls.RandomBall;
import mygames.lineball.Util.DrawingUtil;
import mygames.lineball.Util.MathUtil;

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
        tutorialView = new TutorialView(this, size.x, size.y - TEXTBOX_SIZE, NUM_BALLS, DIFFERENT_BALLS,
                 1, new Tutorial_Level_1());
        setContentView(tutorialView);

    }

    class TutorialView extends GameView {

        private BallTracker ballTracker;
        private float touchX, touchY;
        private Level level;

        Paint whitePaint = new Paint();



        public TutorialView(Context context, int screenWidth, int screenHeight, int num_balls,
                            int different_balls, int color, Level level) {
            super(context, screenWidth, screenHeight, num_balls, different_balls, color);
            this.ballTracker = new BallTracker(numberOfBallsPerType);
            this.level = level;
            whitePaint.setAntiAlias(true);
            whitePaint.setColor(Color.WHITE);
            whitePaint.setTextSize(40);


            for (Ball b: balls) {
                b.setColor(0);
            }
        }

        @Override
        public void update() {

            synchronized (balls) {
                for (Ball b : balls) {
                    if (MathUtil.ballHitLineGameOver(ballTracker, b)) {
                        level.setToErrorState();
                    }
                    MathUtil.checkWallCollision(b, borderColourer, screenWidth, screenHeight);
                    b.update(fps);
                }
            }

            if(level.isEndState()) {
                if(!level.getClass().equals(Tutorial_Level_3.class)) {

                    goToNextLevel();
                      //tutorialView = new TutorialView(getContext(), screenWidth, screenHeight, 3, 1, 0, level);
                }
                else {
                    Intent intent = new Intent(this.getContext(), MainMenuActivity.class);
                    startActivity(intent);
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
                DrawingUtil.drawLines(canvas, ballTracker, touchX, touchY);

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
            String[] comments = level.getComments();
            int separation = 50;
            for(String c : comments) {
                canvas.drawText(c, 10, screenHeight + separation, whitePaint);
                separation += 60;
            }

            //canvas.drawText(comment, 10, screenHeight + 50, whitePaint);
            //canvas.drawText(comment2, 10, screenHeight + 110, whitePaint);
            //canvas.drawText(comment3, 10, screenHeight + 170, whitePaint);

        }

        public void drawCommentsBox() {
            whitePaint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(0, screenHeight, screenWidth, screenHeight + TEXTBOX_SIZE, whitePaint);
            whitePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        }

        private void goToNextLevel() {
            level = level.nextLevel();
            if(level.getClass().equals(Tutorial_Level_2.class)) {
                InitialStateBallGenerator ballgen = new InitialStateBallGenerator(3, 1, screenWidth, screenHeight);
                balls = ballgen.generateBalls();
                ballTracker = new BallTracker(ballgen.getDifferentTypesOfBalls());
            } else {
                Ball ball1 = new Ball(screenWidth, screenHeight, 1);
                Ball ball2 = new Ball(screenWidth, screenHeight, 1);
                RandomBall randBall = new RandomBall(screenWidth, screenHeight);
                balls.clear();
                balls.add(ball1);
                balls.add(ball2);
                balls.add(randBall);

                int[] differentTypesOfBalls = new int[5];
                differentTypesOfBalls[1] = 2;
                differentTypesOfBalls[4] = 1;
                ballTracker = new BallTracker(differentTypesOfBalls);
            }
        }

        // The SurfaceView class implements onTouchListener
        // So we can override this method and detect screen touches.
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                // Player has touched the screen
                case MotionEvent.ACTION_DOWN:
                    if(level.isStateBeforeEndState()) {
                        level.nextState();
                    }
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
                                        level.nextState();
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
                                        level.nextState();
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
                        level.nextState();
                        //ballTracker.checkForShape();

                    } else if (!ballTracker.isGameOver()) {
                        ballTracker.resumeMovement();
                        level.setInitialState();

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
