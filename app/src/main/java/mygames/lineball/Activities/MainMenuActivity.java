package mygames.lineball.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;

import mygames.lineball.Balls.Ball;
import mygames.lineball.Music.MusicHandler;
import mygames.lineball.R;
import mygames.lineball.Util.MathUtil;


public class MainMenuActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    //View that will hold the menu and its logic
    private GameView menuView;

   // private int highscore;
    //private int longestChain;
    private int NUM_BALLS = 40;
    private int DIFFERENT_BALLS = 5;
    private RelativeLayout menuLayout;
    private RelativeLayout.LayoutParams buttonParams;
    public static GoogleApiClient mGoogleApiClient;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    private static final String STATE_RESOLVING_ERROR = "resolving_error";
    final int REQUEST_LEADERBOARD = 1;

    private MusicHandler musicHandler;

    //View that will hold the add
    private AdView addView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.musicHandler = MusicHandler.getInstance(this);
        musicHandler.updateContext(this);


        // Create a GoogleApiClient instance
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Games.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

        menuView = new MenuView(this,  NUM_BALLS, DIFFERENT_BALLS, MainActivity.RANDOM_COLOR);
        menuLayout = new RelativeLayout(this);
        menuLayout.addView(menuView);

        addAllButtons();
        addAdView(menuLayout);

        setContentView(menuLayout);

       // this.highscore = PreferenceManager.getDefaultSharedPreferences(this).getInt("highscore", 0);
       // this.longestChain =
      //          PreferenceManager.getDefaultSharedPreferences(this).getInt("LongestChain", 0);
    }

    private void addAdView(RelativeLayout menuLayout) {
        addView = new AdView(this);
        addView.setAdSize(AdSize.SMART_BANNER);
        addView.setAdUnitId("ca-app-pub-1685157087617386/5583998552");

        // Create an ad request.
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();

        // Optionally populate the ad request builder.
        AdRequest adRequest = adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("CD02B0FFDDC8BD4A04EFD592E7C83808").build();

        // Add the AdView to the view hierarchy.
        menuLayout.addView(addView);

        // Start loading the ad.
        addView.loadAd(adRequest);
    }


    private void addAllButtons() {

        setButtonParams();

        //Button playButton = (Button) findViewById(R.id.button1);
        Button playButton = new Button(this);
        setButton(playButton, "Play");
        playButton.setBackgroundResource(R.drawable.redroundbutton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
        });

        Button howToPlayButt = new Button(this);
        setButton(howToPlayButt, "How to play");
        howToPlayButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTutorial();
            }
        });
        howToPlayButt.setBackgroundResource(R.drawable.blueroundbutton);



        Button highscoresButt = new Button(this);
        setButton(highscoresButt, "Ranking");
        highscoresButt.setBackgroundResource(R.drawable.highscorebutton);
        highscoresButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mGoogleApiClient.isConnected()) {
                    startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(mGoogleApiClient), REQUEST_LEADERBOARD);
                } else {
                    mGoogleApiClient.reconnect();
                    /*if(mGoogleApiClient.isConnected()) {
                        startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(mGoogleApiClient), REQUEST_LEADERBOARD);
                    }*/
                }
            }
        });

        Button[] buttons = new Button[3];
        buttons[0] = playButton;
        buttons[1] = highscoresButt;
        buttons[2] = howToPlayButt;

        displayButtons(buttons);

        setAndDisplayAboutUsButt();
        setAndDisplayMuteButt();

    }

    public void setAndDisplayAboutUsButt() {

        Button aboutUsButt = new Button(this);
        setButton(aboutUsButt, "About");
        aboutUsButt.setBackgroundResource(R.drawable.greenroundbutton);
        aboutUsButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAboutUs();
            }
        });

        int diameter = menuView.screenHeight/12;
        aboutUsButt.setWidth(diameter);
        aboutUsButt.setHeight(diameter);
        aboutUsButt.setX(3 * diameter / 4);
        aboutUsButt.setY(menuView.screenHeight - diameter - 3 * diameter / 4);
        aboutUsButt.setTextSize(10 * MathUtil.getScreenSizeFactor());

        RelativeLayout.LayoutParams highscoreParams =  new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        menuLayout.addView(aboutUsButt, highscoreParams);
    }

    private void setAndDisplayMuteButt() {

        Button muteButt = new Button(this);
        setButton(muteButt, "Mute");
        muteButt.setBackgroundResource(R.drawable.mutebutton);
        muteButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicHandler.muteOrUnmute();
            }
        });

        int diameter = menuView.screenHeight/12;
        muteButt.setWidth(diameter);
        muteButt.setHeight(diameter);
        muteButt.setX(menuView.screenWidth - diameter - 3 * diameter / 4);
        muteButt.setY(menuView.screenHeight - diameter - 3 * diameter / 4);
        muteButt.setTextSize(10 * MathUtil.getScreenSizeFactor());

        RelativeLayout.LayoutParams mutebuttonParams =  new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        menuLayout.addView(muteButt, mutebuttonParams);
    }


    private void setButtonParams() {

        buttonParams =  new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);


    }

    private void setButton(Button button, String name) {
        button.setText(name);
        button.setTextColor(Color.WHITE);

        //button.setTextSize(15);
    }

    private void displayButtons(Button[] buttons) {

        int separation = menuView.screenHeight/25;
        int playDiameter = menuView.screenHeight/6;
        int playButHeight = menuView.screenHeight/3;
        int diameter = menuView.screenHeight/9;
        int initial_off = playButHeight + playDiameter - diameter;

        RelativeLayout.LayoutParams playButtParams =
                new RelativeLayout.LayoutParams(playDiameter, playDiameter);
        playButtParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        //playButtParams.addRule(RelativeLayout.CENTER_VERTICAL);


        for(int i = 0; i< buttons.length; i++) {
            if(i == 0) {
                buttons[i].setTextSize(27 * MathUtil.getScreenSizeFactor());
                buttons[i].setY(menuView.screenHeight/3);
                menuLayout.addView(buttons[i], playButtParams);
            } else {
                buttons[i].setWidth(diameter);
                buttons[i].setHeight(diameter);
                buttons[i].setTextSize(diameter/11);
                buttons[i].setY(initial_off + (separation + diameter) * i ); //;
                menuLayout.addView(buttons[i], buttonParams);
            }
        }

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

        if (result.hasResolution()) {
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

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((MainMenuActivity) getActivity()).onDialogDismissed();
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
        // check for "inconsistent state"
        if ( resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED && requestCode == 1 )  {
            // force a disconnect to sync up state, ensuring that mClient reports "not connected"
            mGoogleApiClient.disconnect();
        }
    }



    /*private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        return resultCode==0;
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
        //boolean works = checkPlayServices();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    class MenuView extends GameView{
        public MenuView(Context context, int num_balls,
                        int different_type_of_balls, int color) {
            super(context, num_balls, different_type_of_balls, color);
        }

        @Override
        // Draw the newly updated scene
        public void draw() {

            if (ourHolder.getSurface().isValid()) {
                canvas = ourHolder.lockCanvas();

                // Draw the background color
                canvas.drawColor(Color.BLACK);

                // Draw the balls
                synchronized (balls) {
                    for (Ball b: balls) {
                        b.draw(paint, canvas);
                    }
                }



                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(menuView.screenWidth/12);
                canvas.drawText("LineBall", screenWidth / 2 - menuView.screenWidth/8, screenHeight/5, paint);
                paint.setTextSize(30);
                //canvas.drawText("Highscore: " + highscore, 30, 210, paint);
               // canvas.drawText("last score: " + getIntent().getIntExtra("score", 0), 30, 260, paint);
                //canvas.drawText("Longest Chain: " + longestChain, 30, 310, paint);
                //canvas.drawText("Last Longest Chain: " + getIntent().getIntExtra("Longestchain", 0), 30, 360, paint);
                //int score = getIntent().getIntExtra("score", 0);
                //canvas.drawText("Score: " + score, 30, 150, paint);

                ourHolder.unlockCanvasAndPost(canvas);
            }

        }
    }

    //Starts the game
    public void startGame() {
        Intent intent = new Intent(menuView.getContext(), MainActivity.class);
        startActivity(intent);
    }

    private void startTutorial() {
        Intent intent = new Intent(menuView.getContext(), TutorialActivity.class);
        startActivity(intent);
    }

    private void startAboutUs() {
        Intent intent = new Intent(menuView.getContext(), AboutUsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        musicHandler.playMenuBackgroundMusic();
        //highscore = PreferenceManager.getDefaultSharedPreferences(this).getInt("highscore", 0);
        //longestChain = PreferenceManager.getDefaultSharedPreferences(this).getInt("LongestChain", 0);
        menuView.resume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        musicHandler.playMenuBackgroundMusic();
       // highscore = PreferenceManager.getDefaultSharedPreferences(this).getInt("highscore", 0);
        //longestChain = PreferenceManager.getDefaultSharedPreferences(this).getInt("LongestChain", 0);
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();
        musicHandler.stopMenuMusic();
        menuView.pause();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

   }