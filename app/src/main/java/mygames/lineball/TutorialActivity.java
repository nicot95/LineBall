package mygames.lineball;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.widget.RelativeLayout;

/**
 * Created by nico on 07/09/15.
 */
public class TutorialActivity extends Activity {

    GameView tutorialView;

    private int NUM_BALLS = 3;
    private int DIFFERENT_BALLS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get a Display object to access screen details
        Display display = getWindowManager().getDefaultDisplay();
        // Load the resolution into a Point object
        Point size = new Point();
        display.getSize(size);
        tutorialView = new GameView(this, size.x, size.y, NUM_BALLS, DIFFERENT_BALLS);
        setContentView(tutorialView);

    }

    class tutorialView extends GameView {

        public tutorialView(Context context, int screenWidth, int screenHeight, int num_balls,
                            int different_balls) {
            super(context, screenWidth, screenHeight, num_balls, different_balls);
            BallGenerator ballGen = new BallGenerator(3, 1, screenWidth, screenHeight);
            balls = ballGen.generateBalls();
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
