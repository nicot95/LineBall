package mygames.lineball;

import android.app.Activity;
import android.app.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Game;
import com.google.android.gms.games.Games;

import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends FragmentActivity implements ConnectionCallbacks,
        OnConnectionFailedListener {

    // gameView will be the view of the game
    // It will also hold the logic of the game
    // and respond to screen touches as well
    GameView gameView;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GoogleApiClient instance
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

        // Initialize gameView and set it as the view
        gameView = new GameView(this);
        setContentView(gameView);
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
            return;
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
        public ErrorDialogFragment() { }

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
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {  // more about this later
            //mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
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

        List<Ball> balls;
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

            synchronized (balls) {
                for (Ball b : balls) {
                    if (Util.ballHitLineGameOver(ballTracker, b)) {
                        paused = true;
                        ballTracker.setGameStateToLineContact();
                    }
                    b.checkWallCollision(screenX, screenY);
                    b.update(fps);
                }
            }

            /*
                increases the score and removes used balles from ArrayList
                for performance issues.
             */
            if (ballTracker.isReadyToCalculateScore()) {

            }

        }

        // Draw the newly updated scene
        public void draw() {

            // Make sure our drawing surface is valid or we crash
            if (ourHolder.getSurface().isValid()) {
                paint.setAntiAlias(true);
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
                    //draw white border
                    paint.setColor(Color.WHITE);
                    canvas.drawCircle(ball2.getX(), ball2.getY(), ball2.getBallRadius() + 4, paint);
                    if(i > 0) {
                        //draw lines
                        Ball ball1 = trackedBalls.get(i - 1);
                        //draw white border
                        Paint whitePaint = new Paint();
                        whitePaint.setAntiAlias(true);
                        whitePaint.setColor(Color.WHITE);
                        whitePaint.setStrokeWidth(10);
                        canvas.drawLine(ball1.getX(), ball1.getY(), ball2.getX(),
                                ball2.getY(), whitePaint);
                        //draw actual line
                        paint.setColor(ballTracker.getColorChain());
                        canvas.drawLine(ball1.getX(), ball1.getY(), ball2.getX(),
                                ball2.getY(), paint);
                    }
                }



                // Draw the balls
                synchronized (balls) {
                    for (Ball b: balls) {
                        b.draw(paint, canvas);
                    }
                }

                // TODO draw the Score
                paint.setTextSize(40);
                canvas.drawText("Score: " + score, 30, 70, paint);

                //Draw the game_overState
                if (ballTracker.isGameOver()) {
                    drawGameOverText(50, ballTracker.getGameState(), screenY / 2, paint);
                    //Games.Leaderboards.submitScore(mGoogleApiClient, LEADERBOARD_ID, score);
                    //startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
                    //        LEADERBOARD_ID), REQUEST_LEADERBOARD);

                }

                // Draw everything to the screen
                ourHolder.unlockCanvasAndPost(canvas);
            }

        }

        private void drawGameOverText(int textSize, BallTracker.Game_State text, int y, Paint paint) {
            paint.setTextSize(textSize);
            String gameOverText = "";
            switch (text) {
                case BOARD_CLEARED:
                    gameOverText = "All balls cleared";
                    break;
                case NO_POSSIBLE_MOVE:
                    gameOverText = "No more moves";
                    break;
                case LINE_CONTACT:
                    gameOverText = "Line contact";
                    break;
                default:
                    break;
            }
            canvas.drawText(gameOverText, (float) 30, y, paint);

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
                    if (!ballTracker.isGameOver()) {
                        paused = false;
                        if (ballTracker.getBallsTracked().isEmpty()) {
                            synchronized (balls) {
                                for (Ball b: balls) {
                                    if (b.intersects(motionEvent.getX(), motionEvent.getY())) {
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
                        paused = false;
                        synchronized (balls) {
                            for (Ball b: balls) {
                                if (b.intersects(motionEvent.getX(), motionEvent.getY())) {
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
                        score += ballTracker.calculateScore();
                        balls.removeAll(ballTracker.getBallsTracked());
                        ballTracker.cleanUpBallsFields();
                        //ballTracker.checkForShape();

                    } else if (!ballTracker.isGameOver()){
                        ballTracker.resumeMovement();
                    }
                    break;
            }
            return true;
        }

        public void createBallsAndRestart(int numBalls) {
            BallGenerator ballGenerator = new BallGenerator(numBalls, DIFFERENT_TYPES_OF_BALLS,
                                                            screenX, screenY);
            balls = ballGenerator.generateBalls();
            numberOfBallsPerType = ballGenerator.getDifferentTypesOfBalls();

            // The score
            int score = 0;

            // Moves
            int movesLeft = 7;
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