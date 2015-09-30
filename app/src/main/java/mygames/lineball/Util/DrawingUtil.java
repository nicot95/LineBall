package mygames.lineball.Util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.List;

import mygames.lineball.Activities.MainActivity;
import mygames.lineball.Balls.Ball;
import mygames.lineball.GameLogic.BallTracker;
import mygames.lineball.GameLogic.BorderColourer;
import mygames.lineball.R;

import static mygames.lineball.Balls.ColorBall.getBlue;
import static mygames.lineball.Balls.ColorBall.getGreen;
import static mygames.lineball.Balls.ColorBall.getLightBlue;
import static mygames.lineball.Balls.ColorBall.getLightGreen;
import static mygames.lineball.Balls.ColorBall.getLightRed;
import static mygames.lineball.Balls.ColorBall.getLightYellow;
import static mygames.lineball.Balls.ColorBall.getRed;
import static mygames.lineball.Balls.ColorBall.getYellow;


public class DrawingUtil {
    public static void drawLines(Canvas canvas, BallTracker ballTracker, float touchX, float touchY) {
        List<Ball> trackedBalls = ballTracker.getBallsTracked();

        Paint paint = new Paint();
        paint.setStrokeWidth(5); // Increase width of line

        Paint borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setStrokeWidth(10);
        boolean first = true;
        synchronized (trackedBalls) {
            for (int i = 0; i < trackedBalls.size(); i++) {
                //if shape is complete, border line to lighht blue, if not stay in white
                drawColoredBorder(first, ballTracker, borderPaint);
                first = false;

                //draw ball border
                Ball ball2 = trackedBalls.get(i);
                canvas.drawCircle(ball2.getX(), ball2.getY(), ball2.getBallRadius() + 4, borderPaint);

                //draw linked lines
                drawLinkedLines(canvas, ballTracker, trackedBalls, paint, borderPaint, i, ball2);

                //draw temporay line following finger touch on screen
                drawTemporaryLines(canvas, ballTracker, touchX, touchY, trackedBalls, paint, i, ball2);
            }
        }
    }

    private static void drawColoredBorder(boolean first, BallTracker ballTracker, Paint borderPaint) {
        if (first || ballTracker.isReadyToCalculateScore()) {
            borderPaint.setColor(Color.argb(255,0,204,204));
        } else if (ballTracker.isGameOver()) {
            borderPaint.setColor(Color.RED);
        } /*else if(ballTracker.isReadyToCalculateScore()) {
            /*List<Ball> balls = ballTracker.getBallsTracked();
            int chainColor = ballTracker.getColorChain();
            for(Ball b: balls) {
                if(!b.getClass().equals(RandomBall.class)) {
                    setBallSelectedColor(b, chainColor);
                }
            }
            borderPaint.setColor(Color.CYAN);*/
          else {
            borderPaint.setColor(Color.WHITE);
        }
    }


    private static void setBallSelectedColor(Ball b, int chainColor) {
        int newCol = -1;
        if(chainColor == getRed()) {
            newCol = getLightRed();
        } else if(chainColor == getBlue()) {
            newCol = getLightBlue();
        } else if(chainColor == getGreen()) {
            newCol = getLightGreen();
        } else if(chainColor == getYellow()) {
            newCol = getLightYellow();
        }

        b.setColor(newCol);

    }

    private static void drawLinkedLines(Canvas canvas, BallTracker ballTracker, List<Ball> trackedBalls, Paint paint, Paint borderPaint, int i, Ball ball2) {
        if (i > 0) {
            //draw border line
            Ball ball1 = trackedBalls.get(i - 1);
            canvas.drawLine(ball1.getX(), ball1.getY(), ball2.getX(),
                    ball2.getY(), borderPaint);
            //draw actual line
            paint.setColor(ballTracker.getColorChain());
            canvas.drawLine(ball1.getX(), ball1.getY(), ball2.getX(),
                    ball2.getY(), paint);
        }
    }

    private static void drawTemporaryLines(Canvas canvas, BallTracker ballTracker, float touchX, float touchY, List<Ball> trackedBalls, Paint paint, int i, Ball ball2) {
        if(i == trackedBalls.size()-1 && !ballTracker.isReadyToCalculateScore()) {
            paint.setStrokeWidth(7);
            paint.setColor(ballTracker.getColorChain());
            canvas.drawLine(ball2.getX(), ball2.getY(), touchX,
                    touchY, paint);
            paint.setStrokeWidth(5);
        }
    }

    public static void drawScreenBorder(Paint paint, Canvas canvas, BorderColourer borderColourer, int screenWidth, int screenHeight) {
        paint.setStrokeWidth(7);
        //Draw north border
        paint.setColor(borderColourer.getNorthBorderColour());
        canvas.drawLine(0, 3, screenWidth, 3, paint);

        //Draw west border
        paint.setColor(borderColourer.getWestBorderColour());
        canvas.drawLine(3, 0, 3, screenHeight, paint);

        //Draw south border
        paint.setColor(borderColourer.getSouthBorderColour());
        canvas.drawLine(0, screenHeight, screenWidth, screenHeight, paint);

        //Draw east border
        paint.setColor(borderColourer.getEastBorderColour());
        canvas.drawLine(screenWidth, 0, screenWidth, screenHeight+5, paint);
    }

    private static Button restartButton;

    public static Button getRestartButton(final Context context, RelativeLayout gameLayout) {
        if (restartButton == null) {
            restartButton = new Button(context);
            restartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                }
            });
            restartButton.setBackgroundResource(R.drawable.blueroundbutton);
            restartButton.setText("Restart");

            RelativeLayout.LayoutParams mutebuttonParams =  new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);

            gameLayout.addView(restartButton, mutebuttonParams);
        }
        return restartButton;
    }

}
