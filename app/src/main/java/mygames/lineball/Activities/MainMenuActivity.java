package mygames.lineball.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import mygames.lineball.Balls.Ball;

public class MainMenuActivity extends Activity {

    //View that will hold the menu and its logic
    private GameView menuView;

    private int highscore;
    private int longestChain;
    private int NUM_BALLS = 20;
    private int DIFFERENT_BALLS = 5;

    //View that will hold the add
   // private AdView addView;

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
        final RelativeLayout menuLayout = new RelativeLayout(this);
        menuLayout.addView(menuView);

        addAllButtons(menuLayout);
        setContentView(menuLayout);

        this.highscore = PreferenceManager.getDefaultSharedPreferences(this).getInt("highscore", 0);
        this.longestChain =
                PreferenceManager.getDefaultSharedPreferences(this).getInt("LongestChain", 0);
    }


    private void addAllButtons(RelativeLayout menuLayout) {

        Button survivalButt = new Button(this);
        setButton(survivalButt, "      Survival      ");
        survivalButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
        });

        Button howToPlayButt = new Button(this);
        setButton(howToPlayButt, "    How to play    ");
        howToPlayButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTutorial();
            }
        });

        /*Button levelsButt = new Button(this);
        setButton(levelsButt, "       Levels       ");
        */

        Button achievButt = new Button(this);
        setButton(achievButt, "Achievements");

        Button aboutUsButt = new Button(this);
        setButton(aboutUsButt, "      About us      ");

        Button[] buttons = new Button[4];
        buttons[0] = survivalButt;
        //buttons[1] = levelsButt;
        buttons[1] = achievButt;
        buttons[2] = howToPlayButt;
        buttons[3] = aboutUsButt;

        displayButtons(buttons, menuLayout);

     }



    private void setButton(Button button, String name) {
        button.setText(name);
        button.setBackgroundColor(Color.WHITE);
    }

    private void displayButtons(Button[] buttons, RelativeLayout layout ) {

        int separation = 150;

        RelativeLayout.LayoutParams buttonParams =
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);

        buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        for(int i = 0; i< buttons.length; i++) {

            buttons[i].setY((menuView.screenHeight/2) + i * separation);
            // buttonParams.addRule(RelativeLayout.BELOW, buttons[i-1].getId());
            layout.addView(buttons[i], buttonParams);
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
    private void startGame() {
        Intent intent = new Intent(menuView.getContext(), MainActivity.class);
        startActivity(intent);
    }

    private void startTutorial() {
        Intent intent = new Intent(menuView.getContext(), TutorialActivity.class);
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