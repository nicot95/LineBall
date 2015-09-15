package mygames.lineball.Activities;

import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.MotionEvent;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;

import android.support.v4.app.FragmentActivity;
import android.widget.MediaController;

import java.util.ArrayList;

import mygames.lineball.BallGenerators.SurvivalBallGenerator;
import mygames.lineball.GameLogic.BallTracker;
import mygames.lineball.Balls.Ball;
import mygames.lineball.GameLogic.BorderColourer;
import mygames.lineball.Music.MusicHandler;
import mygames.lineball.R;
import mygames.lineball.Util.DrawingUtil;
import mygames.lineball.Util.MathUtil;

public class MainActivity extends FragmentActivity implements ConnectionCallbacks,
        OnConnectionFailedListener {

    // gameView will be the view of the game
    // It will also hold the logic of the game
    // and respond to screen touches as well
    GameView survivalView;

    public static int RANDOM_COLOR = -1;

    private ArrayList<RectF> lines = new ArrayList<RectF>();
    private GoogleApiClient mGoogleApiClient;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    private static final String STATE_RESOLVING_ERROR = "resolving_error";
    final String LEADERBOARD_ID = "leaderboard";
    final int REQUEST_LEADERBOARD = 1;

    int NUM_BALLS = 10;
    int DIFFERENT_TYPE_OF_BALLS = 5;

    private MusicHandler musicHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.musicHandler = new MusicHandler(MainActivity.this);
        musicHandler.playBackgroundMusic();

        // Create a GoogleApiClient instance
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

        Display display = getWindowManager().getDefaultDisplay();
        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);
        // Initialize gameView and set it as the view
        survivalView = new SurvivalView(this, size.x, size.y, NUM_BALLS, DIFFERENT_TYPE_OF_BALLS, RANDOM_COLOR);
        setContentView(survivalView);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        if (mResolvingError) {
            // Already attempting to resolve an error.
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;

        }
    }

    // The rest of this code is all about building the error dialog

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((MainActivity) getActivity()).onDialogDismissed();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        return false;
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
        private String timeLeft = "";
        private CountDownTimer timer;

        int initialRoundTIme = 30000;

        public SurvivalView(Context context, int screenWidth, int screenHeight,
                            int numBalls, int different_type_of_balls, int color) {
            super(context, screenWidth, screenHeight, numBalls, different_type_of_balls, color);
            this.ballTracker = new BallTracker(numberOfBallsPerType);
            this.borderColourer = new BorderColourer();
            this.survivalBallGenerator =
                    new SurvivalBallGenerator(numBalls, different_type_of_balls, screenWidth,
                                                screenHeight, numBalls);

            createNewTimer(survivalBallGenerator.getRound());
        }


        @Override
        // Everything that needs to be updated goes in here
        // Movement, collision detection etc.
        public void update() {

            synchronized (balls) {
                for (Ball b : balls) {
                    if (MathUtil.ballHitLineGameOver(ballTracker, b)) {
                        ballTracker.setGameStateToLineContact();
                        playing = false;
                        break;      // We break because the game is already over (performance++)
                    } else if (ballTracker.isGameOver()) { //TimeOut!
                        playing = false;
                        break;
                    }
                    MathUtil.checkWallCollision(b, borderColourer, screenWidth, screenHeight);
                    b.update(fps);
                }
            }

            /*
                increases the score and removes used balles from ArrayList
                for performance issues. Also, increase score if all balls were cleared
             */
            if (ballTracker.isRoundFinished()) {
                timer.cancel();
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
                        timer =  new CountDownTimer(initialRoundTIme + (round * 5) * 1000, 1000) {
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
                canvas.drawText(timeLeft, screenWidth - 100, 70, paint);

                //Draw the game_overState
                if (ballTracker.isRoundFinished()) {
                    drawGameOverText(50, ballTracker.getGameState(), screenHeight / 2, paint);
                    //Games.Leaderboards.submitScore(mGoogleApiClient, LEADERBOARD_ID, score);
                    //startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
                    //        LEADERBOARD_ID), REQUEST_LEADERBOARD);

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


        private void drawGameOverText(int textSize, BallTracker.Game_State text, int y, Paint paint) {
            paint.setTextSize(textSize);
            String gameOverText = "";
            switch (text) {
                case BOARD_CLEARED:
                    int extraScore = 100 + 50 * survivalBallGenerator.getRound();
                    gameOverText = "All balls cleared\n+" + extraScore + " Bonus";
                    break;
                case NO_POSSIBLE_MOVE:
                    gameOverText = "No more moves";
                    break;
                case LINE_CONTACT:
                    gameOverText = "Line contact";
                    break;
                case TIME_OUT:
                    gameOverText = "Time out";
                default:
                    break;
            }
            canvas.drawText(gameOverText, (float) 30, y, paint);

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
                        goToMenu();
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
        private void goToMenu() {
            Intent intent = new Intent(this.getContext(), MainMenuActivity.class);
            updateHighScoreAndChain(intent);
            startActivity(intent);
        }

        private void updateHighScoreAndChain(Intent intent) {
            SharedPreferences highscorePreference = PreferenceManager.getDefaultSharedPreferences(this.getContext());
            SharedPreferences.Editor editor = highscorePreference.edit();
            if (highscorePreference.getInt("highscore", 0) < score) {
                editor.putInt("highscore", score).commit();
            }
            int longestChain = ballTracker.getLongestChain();
            if (highscorePreference.getInt("LongestChain", 0) < longestChain) {
                editor.putInt("LongestChain", longestChain).commit();
            }

            intent.putExtra("score", this.score);
            intent.putExtra("Longestchain", longestChain);
        }

    }

    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();
        musicHandler.playBackgroundMusic();
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
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            //mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        musicHandler.stopMusic();
    }

    @Override
    public void onBackPressed() { }

}