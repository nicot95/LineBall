package mygames.lineball;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;

public class MainMenuActivity extends Activity {

    //View that will hold the menu and its logic
    private MenuView menuView;

    //View that will hold the add
   // private AdView addView;

    private static final String AD_UNIT_ID = "PLACE ID HERE";

    private ArrayList<RectF> lines = new ArrayList<RectF>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuView = new MenuView(this);
        setContentView(menuView);

    }

    public class MenuView extends SurfaceView implements Runnable {

        Thread gameThread = null;

        SurfaceHolder ourHolder;

        volatile boolean playing = true;


        Canvas canvas;
        Paint paint;

        long fps;

        // This is used to help calculate the fps
        private long timeThisFrame;


        public MenuView(Context context) {
            super(context);
            this.setBackgroundColor(0X0ff00000);

            ourHolder = getHolder();
            paint = new Paint();

        }

        public MenuView(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.setBackgroundColor(0X0ff00000);

            ourHolder = getHolder();
            paint = new Paint();
        }

        @Override
        public void run() {
            while (playing) {

                long startFrameTime = System.currentTimeMillis();

                draw();

                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame >= 1) {
                    fps = 1000 / timeThisFrame;
                }

            }

        }

        // Draw the newly updated scene
        public void draw() {

            if (ourHolder.getSurface().isValid()) {
                canvas = ourHolder.lockCanvas();

                paint.setTextSize(40);
                paint.setColor(Color.argb(255, 255, 255, 255));
                canvas.drawText("LineBall", 30, 70, paint);
                int score = getIntent().getIntExtra("score", 0);
                canvas.drawText("Score: " + score, 30, 150, paint);

                ourHolder.unlockCanvasAndPost(canvas);
            }

        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    if (event.getY() < 500) {
                        startGame();
                    }
                    break;
            }
            return true;
        }

        //Starts the game
        private void startGame() {
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
        }

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


    }
    // This is the end of our BreakoutView inner class

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