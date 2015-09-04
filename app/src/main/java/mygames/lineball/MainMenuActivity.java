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
    private MenuView menuView;

    //View that will hold the add
   // private AdView addView;

    private static final String AD_UNIT_ID = "PLACE ID HERE";

    private ArrayList<RectF> lines = new ArrayList<RectF>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuView = new MenuView(this);
        final RelativeLayout menuLayout = new RelativeLayout(this);
        menuLayout.addView(menuView);

        Button survivalBut = new Button(this);
        survivalBut.setText("Survival");
        survivalBut.setBackgroundColor(Color.WHITE);
        survivalBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuView.startGame();
            }
        });

        RelativeLayout.LayoutParams buttonParams =
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);

        buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        buttonParams.addRule(RelativeLayout.CENTER_VERTICAL);

        menuLayout.addView(survivalBut, buttonParams);

        setContentView(menuLayout);

    }

    public class MenuView extends SurfaceView implements Runnable {

        Thread gameThread = null;

        SurfaceHolder ourHolder;

        volatile boolean playing = true;

        private int NUM_BALLS = 20;
        private int DIFFERENT_TYPES_OF_BALLS = 5;

        private int screenX, screenY;

        Canvas canvas;
        Paint paint;

        long fps;

        // This is used to help calculate the fps
        private long timeThisFrame;
        private List<Ball> balls;
        private boolean paused = true;


        public MenuView(Context context) {
            super(context);
            this.setBackgroundColor(0X0ff00000);

            ourHolder = getHolder();
            paint = new Paint();
            paint.setAntiAlias(true);

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenX = size.x;
            screenY = size.y;
            balls = new BallGenerator(NUM_BALLS, DIFFERENT_TYPES_OF_BALLS,
                    screenX, screenY).generateBalls();

        }


        @Override
        public void run() {
            while (playing) {

                long startFrameTime = System.currentTimeMillis();


                draw();

                if(!paused) {
                    update();
                }

                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame >= 1) {
                    fps = 1000 / timeThisFrame;
                }

            }

        }

        private void update() {

            for(Ball b : balls) {
                b.checkWallCollision(screenX, screenY);
                b.update(fps);
            }
        }


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


                paint.setTextSize(40);
                paint.setColor(Color.argb(255, 255, 255, 255));
                canvas.drawText("LineBall", 30, 70, paint);
                //int score = getIntent().getIntExtra("score", 0);
                //canvas.drawText("Score: " + score, 30, 150, paint);

                ourHolder.unlockCanvasAndPost(canvas);
            }

        }
        //Starts the game
        private void startGame() {
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                // Player has touched the screen
                case MotionEvent.ACTION_DOWN: paused = false;



            }
            return true;
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