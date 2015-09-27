package mygames.lineball.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.view.MotionEvent;

import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import mygames.lineball.BallGenerators.SurvivalBallGenerator;
import mygames.lineball.Balls.Ball;
import mygames.lineball.GameLogic.BallTracker;
import mygames.lineball.GameLogic.BorderColourer;
import mygames.lineball.Music.MusicHandler;
import mygames.lineball.Util.AdHandler;
import mygames.lineball.Util.DrawingUtil;
import mygames.lineball.Util.MathUtil;
import mygames.lineball.Util.RoundFinishedTextDrawer;

public class MainActivity extends Activity {

    // gameView will be the view of the game
    // It will also hold the logic of the game
    // and respond to screen touches as well
    GameView survivalView;

    public static int RANDOM_COLOR = -1;


    private ArrayList<RectF> lines = new ArrayList<RectF>();
    final String LEADERBOARD_HIGHSCORE_ID = "CgkIpt6w6v8GEAIQAQ";
    final String LEADERBOARD_LONGEST_CHAIN_ID = "CgkIpt6w6v8GEAIQCA";
    final String LEADERBOARD_ROUND_ID = "CgkIpt6w6v8GEAIQBw";
    final int REQUEST_LEADERBOARD = 1;

    int NUM_BALLS = 8;
    int DIFFERENT_TYPE_OF_BALLS = 5;

    private MusicHandler musicHandler;
    private AdHandler adHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.musicHandler = new MusicHandler(MainActivity.this);
        musicHandler.playGameBackgroundMusic();

        this.adHandler = new AdHandler(this);

        // Initialize gameView and set it as the view
        survivalView = new SurvivalView(this, NUM_BALLS, DIFFERENT_TYPE_OF_BALLS, RANDOM_COLOR);
        setContentView(survivalView);
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
        private CountDownTimer fiveSecsLessTimer;
        private RoundFinishedTextDrawer roundFinishedTextDrawer;

        public SurvivalView(Context context, int numBalls, int different_type_of_balls, int color) {
            super(context,  numBalls, different_type_of_balls, color);
            this.ballTracker = new BallTracker(numberOfBallsPerType);
            this.borderColourer = new BorderColourer();
            this.survivalBallGenerator =
                    new SurvivalBallGenerator(numBalls, different_type_of_balls, numBalls);

            createNewTimer(survivalBallGenerator.getRound());
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
            if (!adHandler.isAdOpen() && ballTracker.isGameOver()) {



                goToMenu();

                //startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(MainMenuActivity.mGoogleApiClient),
                //        REQUEST_LEADERBOARD);
            }
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
                        musicHandler.stopMusic();
                        musicHandler.stopTimer();
                        musicHandler.playGameOverMusic();
                        break;      // We break because the game is already over (performance++)
                    } else if (ballTracker.isGameOver()) { //TimeOut!
                        playing = false;
                        musicHandler.stopMusic();
                        musicHandler.stopTimer();
                        musicHandler.playGameOverMusic();
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
                timer.cancel();
                fiveSecsLessTimer.cancel();
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
                    int time = (Integer.parseInt(timeLeft) + survivalBallGenerator.getBallsInRound() * 5 / round) * 1000;
                    timer = new CountDownTimer(time, 1000) {



                        @Override
                        public void onTick(long millisUntilFinished) {
                            int remainingTime = (int) Math.floor(millisUntilFinished / 1000);
                            timeLeft = String.valueOf(remainingTime);
                        }

                        @Override
                        public void onFinish() {
                            timeLeft = "0";
                            ballTracker.timeOut();
                        }
                    }.start();
                    fiveSecsLessTimer = new CountDownTimer((Integer.parseInt(timeLeft) + survivalBallGenerator.getBallsInRound() * 5 / round) * 1000- 5000, 1000) {
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

                drawScreenBorder();

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
                paint.setTextSize(40);
                canvas.drawText("Score: " + score, 30, 70, paint);
                paint.setColor(Color.WHITE);
                canvas.drawText(timeLeft, screenWidth - 100, 70, paint);




                //Draw the game_overState
                if (roundFinishedTextDrawer != null && roundFinishedTextDrawer.hasToDraw()) {
                    roundFinishedTextDrawer.drawRoundOverText();

                }

                // Draw everything to the screen
                ourHolder.unlockCanvasAndPost(canvas);
            }

        }

        private void drawScreenBorder() {

            paint.setStrokeWidth(7);
            //Draw north border
            paint.setColor(borderColourer.getNorthBorderColour());
            canvas.drawLine(0, 0, screenWidth, 0, paint);

            //Draw west border
            paint.setColor(borderColourer.getWestBorderColour());
            canvas.drawLine(0, 0, 0, screenHeight, paint);

            //Draw south border
            paint.setColor(borderColourer.getSouthBorderColour());
            canvas.drawLine(0, screenHeight, screenWidth, screenHeight, paint);

            //Draw east border
            paint.setColor(borderColourer.getEastBorderColour());
            canvas.drawLine(screenWidth, 0, screenWidth, screenHeight, paint);
        }

        // The SurfaceView class implements onTouchListener
        // So we can override this method and detect screen touches.
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {

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
                    } else {
                        InterstitialAd intersitialAd = adHandler.getInterstitialAd();
                        if (intersitialAd.isLoaded() || intersitialAd.isLoading()) {
                            adHandler.openPossibleIntersitialAd();
                        } else {
                            goToMenu();
                        }
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
                    }
                    break;
            }
            return true;
        }

        /*
            Return to the main menu (MainMenuActivity) and saves highscore if needed.
         */
        public void goToMenu() {
            Intent intent = new Intent(this.getContext(), MainMenuActivity.class);
            updateHighScoreAndChain(intent);
            startActivity(intent);

        }

        private void updateHighScoreAndChain(Intent intent) {

            int longestChain = ballTracker.getLongestChain();

            popUpLeaderboardIfHighscore(LEADERBOARD_LONGEST_CHAIN_ID, longestChain);

            popUpLeaderboardIfHighscore(LEADERBOARD_ROUND_ID, survivalBallGenerator.getRound() -1);

            popUpLeaderboardIfHighscore(LEADERBOARD_HIGHSCORE_ID, score);
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
                if (!MainMenuActivity.mGoogleApiClient.isConnected() ) { // return if not connected
                    return;
                }
                Games.Leaderboards.submitScore(MainMenuActivity.mGoogleApiClient, leaderboardId, score);

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

                StrictMode.setThreadPolicy(policy);

                if(!hasActiveInternetConnection(context)) {
                    return;
                }
                LeaderboardScore leaderboard = loadPlayerScoreResult.getScore();
                if(leaderboard != null) {
                    highscore = leaderboard.getRawScore();
                }
                if(score > highscore) {
                    startActivityForResult(Games.Leaderboards.getLeaderboardIntent(MainMenuActivity.mGoogleApiClient, leaderboardId), REQUEST_LEADERBOARD);
                    ret[0] = true;
                }

            };

        });
        return ret[0];

    }

    private boolean hasActiveInternetConnection(Context context) {
        if (isNetworkAvailable(context)) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                //Log.e(LOG_TAG, "Error checking internet connection", e);
            }
        } else {
           // Log.d(LOG_TAG, "No network available!");
        }
        return false;
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
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
        musicHandler.stopMusic();
        // Tell the gameView pause method to execute
        survivalView.pause();
    }


    @Override
    public void onBackPressed() { }

}