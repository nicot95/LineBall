package mygames.lineball;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

public class MainMenuActivity extends Activity {

    //View that will hold the menu and its logic
    private GameView menuView;

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
        menuView = new GameView(this, size.x, size.y);
        final RelativeLayout menuLayout = new RelativeLayout(this);
        menuLayout.addView(menuView);

        //addSurvivalButton(menuLayout);
        //addAchievementsButton(menuLayout);
       // addAboutUsButton(menuLayout);
        addAllButtons(menuLayout);

        setContentView(menuLayout);

    }



    private void addSurvivalButton(RelativeLayout menuLayout) {
        Button survivalBut = new Button(this);
        survivalBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
        });

        RelativeLayout.LayoutParams buttonParams =
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);

        buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        buttonParams.addRule(RelativeLayout.CENTER_VERTICAL);

        //addButton(menuLayout, survivalBut, "Survival", buttonParams);
    }

    private void addAllButtons(RelativeLayout menuLayout) {

        Button survivalButt = new Button(this);
        setButton(survivalButt, "Survival");
        survivalButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
        });

        Button achievButt = new Button(this);
        setButton(achievButt, "Achievements");

        Button aboutUsButt = new Button(this);
        setButton(aboutUsButt, "About us");

        Button[] buttons = new Button[3];
        buttons[0] = survivalButt;
        buttons[1] = achievButt;
        buttons[2] = aboutUsButt;
        int separation = 150;

        for(int i = 0; i< buttons.length; i++) {
            RelativeLayout.LayoutParams buttonParams =
                    new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);

                buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

                buttons[i].setY((menuView.screenHeight/2) + i * separation);
                // buttonParams.addRule(RelativeLayout.BELOW, buttons[i-1].getId());
                menuLayout.addView(buttons[i], buttonParams);
        }
     }

    private void setButton(Button button, String name) {
        button.setText(name);
        button.setBackgroundColor(Color.WHITE);
    }


    //Starts the game
    private void startGame() {
        Intent intent = new Intent(menuView.getContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        menuView.resume();
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

}