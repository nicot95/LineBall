package mygames.lineball.Activities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

import mygames.lineball.BallGenerators.InitialStateBallGenerator;
import mygames.lineball.Balls.Ball;
import mygames.lineball.GameLogic.BorderColourer;
import mygames.lineball.Util.MathUtil;


public class GameView extends SurfaceView implements Runnable{

    Thread gameThread = null;

    SurfaceHolder ourHolder;

    volatile boolean playing = true;

    protected int num_balls;
    protected int different_type_of_balls;
    protected int screenWidth, screenHeight;

    Canvas canvas;
    Paint paint;

    long fps;

    // This is used to help calculate the fps
    protected long timeThisFrame;
    protected List<Ball> balls;
    protected boolean paused = true;
    protected int[] numberOfBallsPerType;
    protected int color = MainActivity.RANDOM_COLOR;

    protected BorderColourer borderColourer;


    public GameView(Context context, int num_balls,
                    int different_type_of_balls, int color) {
        super(context);
        this.screenWidth = MathUtil.getScreenWidth();
        this.screenHeight = MathUtil.getScreenHeight();
        this.num_balls = num_balls;
        this.different_type_of_balls = different_type_of_balls;
        this.color = color;

        InitialStateBallGenerator ballGen = new InitialStateBallGenerator(num_balls, different_type_of_balls);
        balls = ballGen.generateBalls();
        this.numberOfBallsPerType = ballGen.getDifferentTypesOfBalls();

        this.setBackgroundColor(0X0ff00000);
        ourHolder = getHolder();
        paint = new Paint();
        paint.setAntiAlias(true);

        this.borderColourer = new BorderColourer();
    }


    @Override
    public void run() {
        while (playing) {

            long startFrameTime = System.currentTimeMillis();


            if(!paused) {
                update();
            }

            draw();


            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
                paused = false;
            }

        }

    }

    protected void update() {

        for(Ball b : balls) {
            MathUtil.checkWallCollision(b, borderColourer, screenWidth, screenHeight);
            b.update(fps);
        }
    }



    // Draw the newly updated scene
    protected void draw() {

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

