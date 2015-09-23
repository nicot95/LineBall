package mygames.lineball.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import mygames.lineball.Balls.Ball;
import mygames.lineball.R;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;



public class MainMenuActivity extends Activity {

    //View that will hold the menu and its logic
    private GameView menuView;

    private int highscore;
    private int longestChain;
    private int NUM_BALLS = 20;
    private int DIFFERENT_BALLS = 5;
    private RelativeLayout menuLayout;
    private RelativeLayout.LayoutParams buttonParams;

    //View that will hold the add
    private AdView addView;


    private static final String AD_UNIT_ID = "PLACE ID HERE";

    private ArrayList<RectF> lines = new ArrayList<RectF>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get a Display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();
        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);
        menuView = new MenuView(this, size.x, size.y, NUM_BALLS, DIFFERENT_BALLS, MainActivity.RANDOM_COLOR);
        menuLayout = new RelativeLayout(this);
        menuLayout.addView(menuView);


        addAllButtons();
        addAdView(menuLayout);

        setContentView(menuLayout);

        this.highscore = PreferenceManager.getDefaultSharedPreferences(this).getInt("highscore", 0);
        this.longestChain =
                PreferenceManager.getDefaultSharedPreferences(this).getInt("LongestChain", 0);
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

        /*Button levelsButt = new Button(this);
        setButton(levelsButt, "       Levels       ");
        */

        //Button achievButt = new Button(this);
        //setButton(achievButt, "Achievements");

        Button aboutUsButt = new Button(this);
        setButton(aboutUsButt, "About us");
        aboutUsButt.setBackgroundResource(R.drawable.greenroundbutton);
        aboutUsButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAboutUs();
            }
        });

        Button[] buttons = new Button[2];
        //buttons[0] = playButton;
        //buttons[1] = levelsButt;
        //buttons[1] = achievButt;
        buttons[0] = howToPlayButt;
        buttons[1] = aboutUsButt;

        displayPlayButton(playButton);
        displayRestButtons(buttons);

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

    private void displayPlayButton(Button butt) {
        //RelativeLayout.LayoutParams playButtParams = (RelativeLayout.LayoutParams) butt.getLayoutParams();
       // playButtParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        int diameter = menuView.screenHeight/6;
        RelativeLayout.LayoutParams playButtParams =
                new RelativeLayout.LayoutParams(diameter, diameter);
        playButtParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        playButtParams.addRule(RelativeLayout.CENTER_VERTICAL);


        //butt.setX(menuView.screenWidth/2);
        butt.setTextSize(27);
        menuLayout.addView(butt, playButtParams);

    }

    private void displayRestButtons(Button[] buttons) {

        int separation = 180;
        int diameter = menuView.screenHeight/9;

        for(int i = 0; i< buttons.length; i++) {
            buttons[i].setY((menuView.screenHeight / 2 + menuView.screenHeight/12 +
                    menuView.screenHeight/18) + i * separation);
            buttons[i].setWidth(diameter);
            buttons[i].setHeight(diameter);
            // buttonParams.addRule(RelativeLayout.BELOW, buttons[i-1].getId());
            menuLayout.addView(buttons[i], buttonParams);
        }
    }

    class MenuView extends GameView{
        public MenuView(Context context, int screenWidth, int screenHeight, int num_balls,
                        int different_type_of_balls, int color) {
            super(context, screenWidth, screenHeight, num_balls, different_type_of_balls, color);
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
                paint.setTextSize(60);
                canvas.drawText("LineBall", screenWidth / 2 - 100, 70, paint);
                paint.setTextSize(40);
                canvas.drawText("Highscore: " + highscore, 30, 140, paint);
                canvas.drawText("last score: " + getIntent().getIntExtra("score", 0), 30, 190, paint);
                canvas.drawText("Longest Chain: " + longestChain, 30, 240, paint);
                canvas.drawText("Last Longest Chain: " + getIntent().getIntExtra("Longestchain", 0), 30, 290, paint);
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
        highscore = PreferenceManager.getDefaultSharedPreferences(this).getInt("highscore", 0);
        longestChain = PreferenceManager.getDefaultSharedPreferences(this).getInt("LongestChain", 0);
        menuView.resume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        highscore = PreferenceManager.getDefaultSharedPreferences(this).getInt("highscore", 0);
        longestChain = PreferenceManager.getDefaultSharedPreferences(this).getInt("LongestChain", 0);
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();

        menuView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() { }

}