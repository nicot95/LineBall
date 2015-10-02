/*
 * Copyright (c) 2015. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package mygames.lineball.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;

import mygames.lineball.BallGenerators.SurvivalBallGenerator;
import mygames.lineball.Balls.Ball;
import mygames.lineball.GameLogic.BallTracker;
import mygames.lineball.GameLogic.BorderColourer;
import mygames.lineball.Music.MusicHandler;
import mygames.lineball.R;
import mygames.lineball.Util.AdHandler;
import mygames.lineball.Util.DrawingUtil;
import mygames.lineball.Util.MathUtil;
import mygames.lineball.Util.NetworkUtil;
import mygames.lineball.Util.RoundFinishedTextDrawer;

public class MainActivity extends Activity {

    // gameView will be the view of the game
    // It will also hold the logic of the game
    // and respond to screen touches as well
    SurvivalView survivalView;

    public static int RANDOM_COLOR = -1;
    Button restartBut;
    Button goToMenuBut;


    final String LEADERBOARD_HIGHSCORE_ID = "CgkIpt6w6v8GEAIQAQ";
    final String LEADERBOARD_LONGEST_CHAIN_ID = "CgkIpt6w6v8GEAIQCA";
    final String LEADERBOARD_ROUND_ID = "CgkIpt6w6v8GEAIQBw";
    final int REQUEST_LEADERBOARD = 1;

    int NUM_BALLS = 8;
    int DIFFERENT_TYPE_OF_BALLS = 5;

    private boolean soundPlayed;
    private MusicHandler musicHandler;
    private AdHandler adHandler;
    private float MIN_BALLS_PER_SEC = 0.5f;
    private RelativeLayout gameLayout;
    private Thread thread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        soundPlayed = false;

        this.musicHandler = musicHandler.getInstance(this);
        musicHandler.playGameBackgroundMusic();


        //this.adHandler = AdHandler.getInstance(this);
        //adHandler.requestNewInterstitial();

        // Initialize gameView and set it as the view
        survivalView = new SurvivalView(this, NUM_BALLS, DIFFERENT_TYPE_OF_BALLS, RANDOM_COLOR);
        gameLayout = new RelativeLayout(this);
        gameLayout.addView(survivalView);
        setRestartButton();
        setGoToMenuBut();
        setContentView(gameLayout);
    }


    private void setRestartButton() {


            restartBut = new Button(this);
            restartBut.setTextColor(Color.WHITE);
            restartBut.setTextSize(20 * MathUtil.getScreenSizeFactor());

            final Context context = this;

             restartBut.setOnClickListener(new View.OnClickListener() {
                    @Override
                 public void onClick(View view) {
                    Intent intent = new Intent(context, MainActivity.class);
                    startActivity(intent);

                }
            });
            restartBut.setBackgroundResource(R.drawable.redroundbutton);
            restartBut.setText("Restart");

            RelativeLayout.LayoutParams buttonParams =  new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);

            buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            buttonParams.addRule(RelativeLayout.CENTER_VERTICAL);

            int diameter = survivalView.screenHeight/6;
            restartBut.setWidth(diameter);
            restartBut.setHeight(diameter);

            restartBut.setVisibility(View.GONE);

        gameLayout.addView(restartBut, buttonParams);
        }

    private void setGoToMenuBut() {
        int separation = survivalView.screenHeight/25;
        int playDiameter = survivalView.screenHeight / 6;
        int playButHeight = survivalView.screenHeight / 2;
        int diameter = survivalView.screenHeight / 9;
        int initial_off = (int) (playButHeight + playDiameter - 1.5 * (double) diameter);

        goToMenuBut = new Button(this);
        goToMenuBut.setTextSize(diameter / 11);
        goToMenuBut.setTextColor(Color.WHITE);



        goToMenuBut.setY(initial_off + (separation + diameter));

        goToMenuBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                survivalView.goToMenu();
            }
        });
        goToMenuBut.setBackgroundResource(R.drawable.blueroundbutton);
        goToMenuBut.setText("Menu");

        RelativeLayout.LayoutParams buttonParams =  new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        goToMenuBut.setWidth(diameter);
        goToMenuBut.setHeight(diameter);

        goToMenuBut.setVisibility(View.GONE);

        gameLayout.addView(goToMenuBut, buttonParams);

    }


    class SurvivalView extends GameView {

        /*
            Fields used by the ball tracker in order to check for gameOver state
        */
        private float touchX, touchY;

        private SurvivalBallGenerator survivalBallGenerator;
        // The score
        private int score = 0;

        private BallTracker ballTracker;
        private BorderColourer borderColourer;
        private String timeLeft = "0";
        private CountDownTimer timer;
        private CountDownTimer tenSecsLessTimer;
        private RoundFinishedTextDrawer roundFinishedTextDrawer;
        private boolean wasPaused = false;

        public SurvivalView(Context context, int numBalls, int different_type_of_balls, int color) {
            super(context, numBalls, different_type_of_balls, color, false);
            this.ballTracker = new BallTracker(numberOfBallsPerType);
            this.borderColourer = new BorderColourer();
            this.survivalBallGenerator =
                    new SurvivalBallGenerator(numBalls, different_type_of_balls, numBalls);

        }


        @Override
        // Everything that needs to be updated goes in here
        // Movement, collision detection etc.
        public void update() {
            updateBalls();

            /*
                increases the score and removes used balles from ArrayList
                for performance issues. Also, increase score if all balls were cleared
             */
            checkStartNewRound();

            deleteRoundFinishedDrawerIfNecessary();
            /*if (!adHandler.isAdOpen() && ballTracker.isGameOver()) {
                goToMenu();
            }*/

        }

        private void deleteRoundFinishedDrawerIfNecessary() {
            if (roundFinishedTextDrawer != null && !roundFinishedTextDrawer.hasToDraw()) {
                roundFinishedTextDrawer = null; //Destroys the text drawer for this round.
            }
        }

        private void updateBalls() {


            synchronized (balls) {
                for (Ball b : balls) {
                    if (MathUtil.ballHitLineGameOver(ballTracker, b)) {
                        ballTracker.setGameStateToLineContact();
                        playing = false;
                        musicHandler.pauseGameMusic();
                        musicHandler.stopTimer();

                        if(!soundPlayed) {
                            musicHandler.playGameOverMusic();
                            soundPlayed = true;
                        }
                        submitScore(LEADERBOARD_HIGHSCORE_ID, score);
                        submitScore(LEADERBOARD_LONGEST_CHAIN_ID, ballTracker.getLongestChain());
                        submitScore(LEADERBOARD_ROUND_ID, survivalBallGenerator.getRound()-1);
                        thread = new Thread(){
                            @Override
                            public void run() {
                                            runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                restartBut.setVisibility(VISIBLE);
                                                goToMenuBut.setVisibility(VISIBLE);

                                            }
                                        });

                                    }

                        };
                        thread.start();


                        break;      // We break because the game is already over (performance++)
                    } else if (ballTracker.isGameOver()) { //TimeOut!
                        playing = false;
                        musicHandler.pauseGameMusic();
                        musicHandler.stopTimer();
                        if(!soundPlayed) {
                            musicHandler.playGameOverMusic();
                            soundPlayed = true;
                        }
                        submitScore(LEADERBOARD_HIGHSCORE_ID, score);
                        submitScore(LEADERBOARD_LONGEST_CHAIN_ID, ballTracker.getLongestChain());
                        submitScore(LEADERBOARD_ROUND_ID, survivalBallGenerator.getRound()-1);
                        thread = new Thread(){
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        restartBut.setVisibility(VISIBLE);
                                        goToMenuBut.setVisibility(VISIBLE);

                                    }
                                });

                            }

                        };
                        thread.start();


                        break;
                    }
                    MathUtil.checkWallCollision(b, borderColourer, screenWidth, screenHeight);
                    b.update(fps);
                }
            }
        }

        private void checkStartNewRound() {
            if (ballTracker.isRoundFinished()) {
                musicHandler.stopTimer();
                if(timer != null) {
                    timer.cancel();
                }
                if(tenSecsLessTimer != null) {
                    tenSecsLessTimer.cancel();
                }
                // Creates a new Round Finished Drawar to draw the text on the screen as long as we want.
                if (roundFinishedTextDrawer == null) {
                    roundFinishedTextDrawer =
                            new RoundFinishedTextDrawer(survivalBallGenerator.getRound()
                                    , canvas, paint, screenWidth, screenHeight,
                                    ballTracker.getGameState());

                }
                if (survivalBallGenerator.loadNewRound()) {
                    Ball newBall = survivalBallGenerator.generateSurvivalBall();
                    ballTracker.addToBallList(newBall);
                    balls.add(newBall);
                } else {
                    final int round = survivalBallGenerator.getRound();
                    if (ballTracker.getGameState() == BallTracker.Game_State.BOARD_CLEARED) {
                        score += 100 + 50 * round;
                        int time = Integer.parseInt(timeLeft);
                        timeLeft = String.valueOf(time + 5 + round);
                    }
                    ballTracker.newRoundStarted();
                    createNewTimer(round);
                }
            }
        }


        /*
            Creates the new Timer that will depend on the round number
         */
        private void createNewTimer(final int round) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() { // The 1000 represents one second
                    int time = (Integer.parseInt(timeLeft) * 1000);
                    if (!wasPaused) {
                        float ballsPerSec = round > 8 ? MIN_BALLS_PER_SEC :  (float) 4 /(float) round;
                        time += (survivalBallGenerator.getBallsInRound() * ballsPerSec) * 1000;
                    }
                    timer = new CountDownTimer(time, 1000) {


                        @Override
                        public void onTick(long millisUntilFinished) {
                            if (!wasPaused) {
                                int remainingTime = (int) Math.floor(millisUntilFinished / 1000);
                                timeLeft = String.valueOf(remainingTime);
                            }

                        }

                        @Override
                        public void onFinish() {
                            timeLeft = "0";
                            ballTracker.timeOut();
                        }

                    }.start();
                    int timeRemaining = Integer.parseInt(timeLeft) * 1000;
                    int tenSecsLessTime =  (timeRemaining > 10000 || round < 2) ? timeRemaining - 10000 : 1;
                    if (!wasPaused) {
                        float ballsPerSec = round > 8 ? MIN_BALLS_PER_SEC : (float) 4/ (float) round;
                        tenSecsLessTime += (survivalBallGenerator.getBallsInRound() * ballsPerSec) * 1000;
                    } else {
                        wasPaused = !wasPaused;
                    }
                    tenSecsLessTimer = new CountDownTimer(tenSecsLessTime, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            musicHandler.playFiveSecsLeftMusic();
                        }
                    }.start();
                }
            });

        }

        @Override
        // Draw the newly updated scene
        public void draw() {

            // Make sure our drawing surface is valid or we crash
            if (ourHolder.getSurface().isValid()) {
                paint.setAntiAlias(true);
                // Lock the canvas ready to draw
                canvas = ourHolder.lockCanvas();

                // Draw the background color
                canvas.drawColor(Color.BLACK);

                DrawingUtil.drawScreenBorder(paint, canvas, borderColourer, screenWidth, screenHeight);

                /*
                    This call handles the drawing of:
                        - The lines connecting the linked balls
                        - The temporary line from the current ball and the finger
                        - The borders surrounding the balls and lines
                 */
                DrawingUtil.drawLines(canvas, ballTracker, touchX, touchY);

                // Draw the balls
                synchronized (balls) {
                    for (Ball b : balls) {
                        b.draw(paint, canvas);
                    }
                }

                //Draw the Score and time left
                paint.setTextSize(40 * MathUtil.getScreenSizeFactor());
                canvas.drawText("Score: " + score, screenWidth/24, screenWidth/10, paint);
                paint.setColor(Color.WHITE);
                canvas.drawText(timeLeft, screenWidth - screenWidth/7, screenWidth/10, paint);

                //Draw the game_overState
                if (roundFinishedTextDrawer != null && roundFinishedTextDrawer.hasToDraw()) {
                    roundFinishedTextDrawer.drawRoundOverText();

                }

                // Draw everything to the screen
                ourHolder.unlockCanvasAndPost(canvas);
            }

        }

        // The SurfaceView class implements onTouchListener
        // So we can override this method and detect screen touches.
        @Override
        public boolean onTouchEvent(@NonNull MotionEvent motionEvent) {

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                // Player has touched the screen
                case MotionEvent.ACTION_DOWN:
                    if (!ballTracker.isGameOver()) {
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
                    /*} else {
                        if (adHandler.isLoaded()) {
                            if(!adHandler.openPossibleIntersitialAd()){
                                goToMenu();
                            }
                        } else {
                            goToMenu();
                        }*/
                    }
                    break;

                //In case we want swiping instead of just clicking
                case MotionEvent.ACTION_MOVE:
                    if (!ballTracker.isGameOver()) {
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
                        /* Shaped is cleared, balls are eliminated from screen and also from list
                           so that app can scalete as rounds go on, the score is calculated and
                           logic is carried out to see if round is finished. Balltracker is restarted
                           to track new set of balls.
                         */
                        int balls_removed = ballTracker.clearShape();
                        musicHandler.playShapeCompleted();
                        survivalBallGenerator.deduceBalls(balls_removed);
                        score += ballTracker.calculateScore();
                        balls.removeAll(ballTracker.getBallsTracked());
                        ballTracker.cleanUpBallsFields();

                    } else if (!ballTracker.isGameOver()) {
                        ballTracker.resumeMovement();
                    } else {

                    }
                    break;
            }
            return true;
        }

        private void submitScore(String leaderboardId, int score) {
            if (MainMenuActivity.mGoogleApiClient.isConnected()) { // return if not connected
                Games.Leaderboards.submitScore(MainMenuActivity.mGoogleApiClient, leaderboardId, score);
            }

        }

        /*
            Return to the main menu (MainMenuActivity) and saves highscore if needed.
         */
        public void goToMenu() {
            Intent intent = new Intent(this.getContext(), MainMenuActivity.class);
            updateHighScoreAndChain();
            startActivity(intent);

        }

        private void updateHighScoreAndChain() {

            restartBut.setVisibility(VISIBLE);
            goToMenuBut.setVisibility(VISIBLE);

            int longestChain = ballTracker.getLongestChain();

            popUpLeaderboardIfHighscore(LEADERBOARD_LONGEST_CHAIN_ID, longestChain);

            popUpLeaderboardIfHighscore(LEADERBOARD_ROUND_ID, survivalBallGenerator.getRound() - 1);

            popUpLeaderboardIfHighscore(LEADERBOARD_HIGHSCORE_ID, score);
        }

        @Override
        public void pause() {
            playing = false;
            wasPaused = true;
            if(timer!= null) {
                timer.cancel();
                timer = null;
            }
            if(tenSecsLessTimer != null) {
                tenSecsLessTimer.cancel();
            }
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }

        }

        @Override
        // If SimpleGameEngine Activity is started then
        // start our thread.
        public void resume() {

            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
            createNewTimer(survivalBallGenerator.getRound());

        }


}




    private boolean popUpLeaderboardIfHighscore(final String leaderboardId, final int score) {

        final boolean[] ret = {false};

        final Context context = this;

        Games.Leaderboards.loadCurrentPlayerLeaderboardScore(MainMenuActivity.mGoogleApiClient,
                leaderboardId,
                LeaderboardVariant.TIME_SPAN_ALL_TIME,
                LeaderboardVariant.COLLECTION_PUBLIC).setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
            long highscore = 0;


            @Override
            public void onResult(Leaderboards.LoadPlayerScoreResult loadPlayerScoreResult) {
                if (!MainMenuActivity.mGoogleApiClient.isConnected()) { // return if not connected
                    return;
                }
                Games.Leaderboards.submitScore(MainMenuActivity.mGoogleApiClient, leaderboardId, score);

                if (!NetworkUtil.hasActiveInternetConnection(context)) {
                    return;
                }
                LeaderboardScore leaderboard = loadPlayerScoreResult.getScore();
                if (leaderboard != null) {
                    highscore = leaderboard.getRawScore();
                }
                if (score > highscore) {
                    startActivityForResult(Games.Leaderboards.getLeaderboardIntent(MainMenuActivity.mGoogleApiClient, leaderboardId), REQUEST_LEADERBOARD);
                    ret[0] = true;
                }
                restartBut.setVisibility(View.VISIBLE);
                goToMenuBut.setVisibility(View.VISIBLE);

            }

        });
        return ret[0];

    }


    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();
        musicHandler.playGameBackgroundMusic();
        // Tell the gameView resume method to execute
        survivalView.resume();
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();
        musicHandler.pauseGameMusic();
                // Tell the gameView pause method to execute
        survivalView.pause();
    }

    @Override
    public void onBackPressed() {

    }


}