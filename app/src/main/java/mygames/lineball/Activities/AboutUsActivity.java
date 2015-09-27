package mygames.lineball.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import mygames.lineball.Util.MathUtil;

public class AboutUsActivity extends Activity{

    private AboutUsView view;
    Thread gameThread = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = new AboutUsView(this, MathUtil.getScreenWidth(), MathUtil.getScreenHeight());
        setContentView(view);
    }

    private void addBackButton() {
        Button backButton = new Button(this);
        backButton.setText("Back");
        backButton.setTextColor(Color.WHITE);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                startActivity(intent);
            }
        });

        int diameter = 60;
        RelativeLayout.LayoutParams backButtonParams =
                new RelativeLayout.LayoutParams(diameter, diameter);
        backButtonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        backButtonParams.addRule(RelativeLayout.CENTER_VERTICAL);

    }

    @Override
    public void onPause() {
        super.onPause();
        view.pause();

    }

    // If SimpleGameEngine Activity is started then
    // start our thread.
    @Override
    public void onResume() {
        super.onResume();
        view.resume();
        //gameThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class AboutUsView extends SurfaceView implements Runnable {

        private boolean running = true;
        private final int screenWidth;
        private final int screenHeight;
        private Canvas canvas;
        SurfaceHolder ourHolder;
        private Paint paint;


        public AboutUsView(Context context, int screenWidth, int screenHeight) {
            super(context);
            this.setBackgroundColor(0X0ff00000);
            this.screenWidth = screenWidth;
            this.screenHeight = screenHeight;
            ourHolder = getHolder();
            this.canvas = new Canvas();
            this.paint = new Paint();
        }



        // Draw the newly updated scene
        public void draw() {
            if (ourHolder.getSurface().isValid()) {

                canvas = ourHolder.lockCanvas();

                // Draw the background color
                //canvas.drawColor(Color.BLACK);

                paint.setColor(Color.WHITE);
                paint.setTextSize(60);
                canvas.drawText("ABOUT US BIIIIIITCH", screenWidth / 2 - 100, screenHeight / 5, paint);
                paint.setTextSize(30);

                ourHolder.unlockCanvasAndPost(canvas);
            }

        }

        @Override
        public void run() {
            while(running) {
                draw();
            }
        }

        public void pause() {
            running = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }

        }

        // If SimpleGameEngine Activity is started then
        // start our thread.
        public void resume() {
            running = true;
            gameThread = new Thread(this);
            gameThread.start();
        }



    }



}
