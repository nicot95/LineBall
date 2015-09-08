package mygames.lineball;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by nico on 24/08/15.
 */


public class Util{

    /**
     * Returns distance to segment
     *
     * @param ss
     *            segment start point
     * @param se
     *            segment end point
     * @param p
     *            point to found closest point on segment
     * @return distance to segment
     */
    public static double getDistanceToSegment(Point ss, Point se, Point p)
    {
        return getDistanceToSegment(ss.x, ss.y, se.x, se.y, p.x, p.y);
    }

    /**
     * Returns distance to segment
     *
     * @param sx1
     *            segment x coord 1
     * @param sy1
     *            segment y coord 1
     * @param sx2
     *            segment x coord 2
     * @param sy2
     *            segment y coord 2
     * @param px
     *            point x coord
     * @param py
     *            point y coord
     * @return distance to segment
     */
    public static double getDistanceToSegment(int sx1, int sy1, int sx2, int sy2, int px, int py)
    {
        Point closestPoint = getClosestPointOnSegment(sx1, sy1, sx2, sy2, px, py);
        return getDistance(closestPoint.x, closestPoint.y, px, py);
    }
    /**
     * Returns distance between two 2D points
     *
     * @param point1
     *            first point
     * @param point2
     *            second point
     * @return distance between points
     */
    public static double getDistance(Point point1, Point point2)
    {
        return getDistance(point1.x, point1.y, point2.x, point2.y);
    }


    /**
     * Returns distance between two sets of coords
     *
     * @param x1
     *            first x coord
     * @param y1
     *            first y coord
     * @param x2
     *            second x coord
     * @param y2
     *            second y coord
     * @return distance between sets of coords
     */
    public static double getDistance(float x1, float y1, float x2, float y2)
    {
        // using long to avoid possible overflows when multiplying
        double dx = x2 - x1;
        double dy = y2 - y1;

        // return Math.hypot(x2 - x1, y2 - y1); // Extremely slow
        // return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)); // 20 times faster than hypot
        return Math.sqrt(dx * dx + dy * dy); // 10 times faster then previous line
    }

    // returns true if a ball has hit a line and therefore game is over
    public static  boolean ballHitLineGameOver(BallTracker ballTracker, Ball b) {
        final int LINEWIDTH = 4;

        List<Ball> ballsTracked = ballTracker.getBallsTracked();
        float x = b.getX(); float y = b.getY(); float ballRadius = b.getBallRadius();
        Point thisPoint = new Point((int) x, (int) y);

        for(int i = 1; i < ballsTracked.size(); i++) {
            Ball ball1 = ballsTracked.get(i-1);
            Ball ball2 = ballsTracked.get(i);
            Point point1 = new Point((int) ball1.getX(), (int) ball1.getY());
            Point point2 = new Point((int) ball2.getX(), (int) ball2.getY());

            List<Point> intersectPoints1 = Util.getCircleLineIntersectionPoint(point1, point2, point1, ballRadius);
            List<Point> intersectPoints2 = Util.getCircleLineIntersectionPoint(point1, point2, point2, ballRadius);

            Point point1A = intersectPoints1.get(0);
            Point point1B = intersectPoints1.get(1);
            Point point2A = intersectPoints2.get(0);
            Point point2B = intersectPoints2.get(1);

            Point intersectPoint1 = getDistance(point1A, point2A) < getDistance(point1B, point2A) ?
                                         point1A : point1B;
            Point intersectPoint2 = getDistance(point2A, point1A) < getDistance(point2B, point1A) ?
                                         point2A : point2B;
            if(!ball1.equals(b) && !ball2.equals(b)
                    && Util.getDistanceToSegment(intersectPoint1, intersectPoint2, thisPoint) <= ballRadius + LINEWIDTH)

                return true;
        }
        return false;

    }

    public static boolean matchingColor(Ball b1, Ball b2) {
        return b1.getColor() == b2.getColor() ||
                b1.getColor() == Ball.RANDOM_COLOR ||
                b2.getColor() == Ball.RANDOM_COLOR;
    }

    /**
     * Returns closest point on segment to point
     *
     * @param ss
     *            segment start point
     * @param se
     *            segment end point
     * @param p
     *            point to found closest point on segment
     * @return closest point on segment to p
     */
    public static Point getClosestPointOnSegment(Point ss, Point se, Point p)
    {
        return getClosestPointOnSegment(ss.x, ss.y, se.x, se.y, p.x, p.y);
    }

    /**
     * Returns closest point on segment to point
     *
     * @param sx1
     *            segment x coord 1
     * @param sy1
     *            segment y coord 1
     * @param sx2
     *            segment x coord 2
     * @param sy2
     *            segment y coord 2
     * @param px
     *            point x coord
     * @param py
     *            point y coord
     * @return closets point on segment to point
     */
    public static Point getClosestPointOnSegment(int sx1, int sy1, int sx2, int sy2, int px, int py)
    {
        double xDelta = sx2 - sx1;
        double yDelta = sy2 - sy1;

        if ((xDelta == 0) && (yDelta == 0))
        {
            return new Point(sx1, sx2);
        }

        double u = ((px - sx1) * xDelta + (py - sy1) * yDelta) / (xDelta * xDelta + yDelta * yDelta);

        final Point closestPoint;
        if (u < 0)
        {
            closestPoint = new Point(sx1, sy1);
        }
        else if (u > 1)
        {
            closestPoint = new Point(sx2, sy2);
        }
        else
        {
            closestPoint = new Point((int) Math.round(sx1 + u * xDelta), (int) Math.round(sy1 + u * yDelta));
        }

        return closestPoint;
    }


    //buggy, not working correctly
    public double pointToLineDistance(Point A, Point B, Point P) {
        double normalLength = Math.sqrt((B.x-A.x)*(B.x-A.x)+(B.y-A.y)*(B.y-A.y));
        return Math.abs((P.x-A.x)*(B.y-A.y)-(P.y-A.y)*(B.x-A.x))/normalLength;
    }

    public static List<Point> getCircleLineIntersectionPoint(Point pointA,
                                                             Point pointB, Point center, float radius) {
        float baX = pointB.x - pointA.x;
        float baY = pointB.y - pointA.y;
        float caX = center.x - pointA.x;
        float caY = center.y - pointA.y;

        float a = baX * baX + baY * baY;
        float bBy2 = baX * caX + baY * caY;
        float c = caX * caX + caY * caY - radius * radius;

        float pBy2 = bBy2 / a;
        float q = c / a;

        float disc = pBy2 * pBy2 - q;
        if (disc < 0) {
            return Collections.emptyList();
        }
        // if disc == 0 ... dealt with later
        float tmpSqrt = (float) Math.sqrt(disc);
        float abScalingFactor1 = -pBy2 + tmpSqrt;
        float abScalingFactor2 = -pBy2 - tmpSqrt;

        Point p1 = new Point((int) (pointA.x - baX * abScalingFactor1),(int) (pointA.y
                - baY * abScalingFactor1));
        if (disc == 0) { // abScalingFactor1 == abScalingFactor2
            return Collections.singletonList(p1);
        }
        Point p2 = new Point((int) (pointA.x - baX * abScalingFactor2), (int) (pointA.y
                - baY * abScalingFactor2));
        return Arrays.asList(p1, p2);
    }

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
