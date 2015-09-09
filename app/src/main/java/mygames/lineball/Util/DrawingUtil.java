package mygames.lineball.Util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.List;

import mygames.lineball.BallTracker;
import mygames.lineball.Balls.Ball;


public class DrawingUtil {
    public static void drawLines(Canvas canvas, BallTracker ballTracker, float touchX, float touchY) {
        List<Ball> trackedBalls = ballTracker.getBallsTracked();
        synchronized (trackedBalls) {
            Paint paint = new Paint();
            paint.setStrokeWidth(5); // Increase width of line

            Paint borderPaint = new Paint();
            borderPaint.setAntiAlias(true);
            borderPaint.setStrokeWidth(10);
            for (int i = 0; i < trackedBalls.size(); i++) {
                //if shape is complete, border line to lighht blue, if not stay in white
                if (ballTracker.isGameOver()) {
                    borderPaint.setColor(Color.RED);
                }else if(ballTracker.isReadyToCalculateScore()) {
                    borderPaint.setColor(Color.CYAN);
                } else {
                    borderPaint.setColor(Color.WHITE);
                }
                //draw ball border
                Ball ball2 = trackedBalls.get(i);
                canvas.drawCircle(ball2.getX(), ball2.getY(), ball2.getBallRadius() + 4, borderPaint);

                //draw linked lines
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

                //draw temporay line following finger touch on screen
                if(i == trackedBalls.size()-1 && !ballTracker.isReadyToCalculateScore()) {
                    paint.setStrokeWidth(7);
                    paint.setColor(ballTracker.getColorChain());
                    canvas.drawLine(ball2.getX(), ball2.getY(), touchX,
                            touchY, paint);
                    paint.setStrokeWidth(5);
                }
            }
        }
    }
}
