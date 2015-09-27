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

import mygames.lineball.R;
import mygames.lineball.Util.MathUtil;

public class AboutUsActivity extends Activity{

    private AboutUsView view;
    Thread gameThread = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = new AboutUsView(this, MathUtil.getScreenWidth(), MathUtil.getScreenHeight());
        setContentView(R.layout.about_us);
    }

    public void goToMenu(View view) {
        Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
        startActivity(intent);

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
                String text1 = "Designed and programmed by";
                String name1 = "Daniel Hernandez";
                String name2 = "Nicolas Trama";
                String imperialLove = "Students at Imperial College London";

                int initialHeight = screenHeight / 6;
                canvas.drawText(text1, 100, initialHeight, paint);
                int nameYseparation = screenHeight / 7; // Separation from text1 and names Y axis
                int nameSeparation = screenWidth / 10; //Horizontal separation between names
                paint.setTextSize(30);
                canvas.drawText(name1, 20, initialHeight + nameYseparation, paint);
                canvas.drawText(name2, 20 + nameSeparation, initialHeight + nameYseparation, paint);
                int imperialLoveY = initialHeight + nameSeparation + (screenHeight / 5);
                canvas.drawText(imperialLove, 100, imperialLoveY, paint);



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
