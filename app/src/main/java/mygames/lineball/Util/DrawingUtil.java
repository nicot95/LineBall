package mygames.lineball.Util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.List;

import mygames.lineball.GameLogic.BallTracker;
import mygames.lineball.Balls.Ball;


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
        if (first) {
            borderPaint.setColor(Color.MAGENTA);
        } else if (ballTracker.isGameOver()) {
            borderPaint.setColor(Color.RED);
        } else if(ballTracker.isReadyToCalculateScore()) {
            borderPaint.setColor(Color.CYAN);
        } else {
            borderPaint.setColor(Color.WHITE);
        }
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
}
