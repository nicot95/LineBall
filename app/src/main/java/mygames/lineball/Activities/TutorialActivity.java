package mygames.lineball.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;

import mygames.lineball.BallGenerators.InitialStateBallGenerator;
import mygames.lineball.Balls.Ball;
import mygames.lineball.Balls.RandomBall;
import mygames.lineball.GameLogic.BallTracker;
import mygames.lineball.Util.DrawingUtil;
import mygames.lineball.Util.MathUtil;


public class TutorialActivity extends Activity {

    GameView tutorialView;

    private int NUM_BALLS = 2;
    private int DIFFERENT_BALLS = 1;
    private int RED = 0;
    private double game_screen_factor = 0.83;
    private final double MY_ASPECT_RATIO = 1.65;
    private final int FONT_WIDTH_RATIO = 18;
    private final int FONT_HEIGHT_RATIO = 30;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tutorialView = new TutorialView(this, NUM_BALLS, DIFFERENT_BALLS,
                 1, new Tutorial_Level_1());
        setContentView(tutorialView);

    }

    class TutorialView extends GameView {

        private BallTracker ballTracker;
        private float touchX, touchY;
        private double commentBoxHeight;
        private Level level;
        private int fullscreenHeight;

        Paint whitePaint = new Paint();


        public TutorialView(Context context, int num_balls,
                            int different_balls, int color, Level level) {
            super(context, num_balls, different_balls, color);
            this.screenHeight = (int) Math.round(game_screen_factor*MathUtil.getScreenHeight());
            this.ballTracker = new BallTracker(numberOfBallsPerType);
            this.fullscreenHeight = screenHeight;
            this.level = level;
            this.commentBoxHeight = (1-game_screen_factor)*screenHeight;
            whitePaint.setAntiAlias(true);
            whitePaint.setColor(Color.WHITE);
            setFontSize();
        }

        @Override
        public void update() {

            if (level.isLineContactState() || level.isNotAllBallsShapeState()) {
                return;
            }

            synchronized (balls) {
                for (Ball b : balls) {
                    if (MathUtil.ballHitLineGameOver(ballTracker, b)) {
                        level.setToLineContactState();
                        ballTracker.setGameStateToLineContact();
                        //playing = false;

                    }
                    MathUtil.checkWallCollision(b, borderColourer, screenWidth, screenHeight);
                    b.update(fps);
                }
            }

            if (level.isEndState()) {
                if (!level.getClass().equals(Tutorial_Level_3.class)) {

                    startNextLevel();
                    //tutorialView = new TutorialView(getContext(), screenWidth, screenHeight, 3, 1, 0, level);
                } else {
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
                DrawingUtil.drawLines(canvas, ballTracker, touchX, touchY);

                // Draw the balls
                synchronized (balls) {
                    for (Ball b : balls) {
                        b.draw(paint, canvas);
                    }
                }

                //Draw the game_overState

                drawComment();

                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        private void setFontSize() {
            float fontSize;
            if(fullscreenHeight/screenWidth < MY_ASPECT_RATIO) {
                fontSize = fullscreenHeight/FONT_HEIGHT_RATIO;
            } else {
                fontSize = screenWidth/FONT_WIDTH_RATIO;
            }
            whitePaint.setTextSize(fontSize);
        }

        public void drawComment() {
            String[] comments = level.getComments();
            int separation = screenHeight/20;
            int offset = screenHeight/16;
            //float fontSize = paint.getTextSize();
            for (String c : comments) {
                canvas.drawText(c, fullscreenHeight/120, screenHeight + separation, whitePaint);
                separation += offset;
            }

        }

        public void drawCommentsBox() {
            whitePaint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(0, screenHeight, screenWidth, (int) Math.round(screenHeight+ commentBoxHeight), whitePaint);
            whitePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        }

        private void startNextLevel() {
            level = level.nextLevel();
            startLevel();

        }

        private void restartLevel() {
            level.setInitialState();
            startLevel();
        }

        private void startLevel() {
            if (level.getClass().equals(Tutorial_Level_2.class)) {
                //level 2
                InitialStateBallGenerator ballgen = new InitialStateBallGenerator(3, 1);
                balls = ballgen.generateBalls();
                ballTracker = new BallTracker(ballgen.getDifferentTypesOfBalls());
            } else {
                //level 3
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
                    //if (level.isStateBeforeEndState()) {
                    //    level.nextState();
                    if (level.isLineContactState()) {
                        paused = false;
                        restartLevel();
                    } else if (level.isNotAllBallsShapeState()) {
                        ballTracker.resumeMovement();
                        level.setInitialState();
                    }
                    if (!ballTracker.isGameOver()) {
                        //paused = false;
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
                        //paused = false;
                        touchX = motionEvent.getX();
                        touchY = motionEvent.getY();
                        synchronized (balls) {
                            for (Ball b : balls) {
                                if (b.intersects(touchX, touchY)) {
                                    int start_balls = ballTracker.getBallsTracked().size();
                                    ballTracker.trackBall(b);
                                    if (ballTracker.getBallsTracked().size() > start_balls) {
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
                    if (!ballTracker.isGameOver() && ballTracker.isReadyToCalculateScore() ) {
                        if (level.allBallsSelected()) {
                            ballTracker.clearShape();
                            balls.removeAll(ballTracker.getBallsTracked());
                            ballTracker.cleanUpBallsFields();
                            level.nextState();
                        } else {
                            level.setNotAllBallsState();
                        }

                    } else if(!level.isLineContactState()){
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);

    }
}
