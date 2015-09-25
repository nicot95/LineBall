package mygames.lineball.Util;

import android.graphics.Point;
import android.os.Debug;
import android.util.Log;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mygames.lineball.Balls.RandomBall;
import mygames.lineball.GameLogic.BallTracker;
import mygames.lineball.Balls.Ball;
import mygames.lineball.GameLogic.BorderColourer;


public class MathUtil {

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

    private static final double CONST_BALL_RADIUS = 0.03125 ;

    public static float calculateBallRadius(double screenY) {
        Log.i("radius", (CONST_BALL_RADIUS * screenY) + "");
        return (float) (0.03125  * screenY);
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

            List<Point> intersectPoints1 = MathUtil.getCircleLineIntersectionPoint(point1, point2, point1, ballRadius);
            List<Point> intersectPoints2 = MathUtil.getCircleLineIntersectionPoint(point1, point2, point2, ballRadius);

            Point point1A = intersectPoints1.get(0);
            Point point1B = intersectPoints1.get(1);
            Point point2A = intersectPoints2.get(0);
            Point point2B = intersectPoints2.get(1);

            Point intersectPoint1 = getDistance(point1A, point2A) < getDistance(point1B, point2A) ?
                                         point1A : point1B;
            Point intersectPoint2 = getDistance(point2A, point1A) < getDistance(point2B, point1A) ?
                                         point2A : point2B;
            if(!ball1.equals(b) && !ball2.equals(b)
                    && MathUtil.getDistanceToSegment(intersectPoint1, intersectPoint2, thisPoint) <= ballRadius + LINEWIDTH)

                return true;
        }
        return false;

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


    public static void checkWallCollision(Ball b, BorderColourer borderColourer, int screenX, int screenY) {
        if (b.getY() + b.getBallRadius() >= screenY && b.getyVelocity() > 0) {
            b.reverseYVelocity();
            b.clearObstacleY(2);
            if(!b.getClass().equals(RandomBall.class)) {
                borderColourer.setSouthBorderColour(b.getColor());
            }

        }
        if(b.getY() - b.getBallRadius() <= 0 && b.getyVelocity() < 0) {
            b.reverseYVelocity();
            b.clearObstacleY(-2);
            if(!b.getClass().equals(RandomBall.class)) {
                borderColourer.setNorthBorderColour(b.getColor());
            }
        }


        if (b.getX() + b.getBallRadius() >= screenX && b.getxVelocity() > 0) {
            b.reverseXVelocity();
            b.clearObstacleX(2);
            if(!b.getClass().equals(RandomBall.class)) {
                borderColourer.setEastBorderColour(b.getColor());
            }
        }

        if(b.getX() - b.getBallRadius() <= 0 && b.getxVelocity() < 0) {
            b.reverseXVelocity();
            b.clearObstacleX(-2);
            if(!b.getClass().equals(RandomBall.class)) {
                borderColourer.setWestBorderColour(b.getColor());
            }
        }
    }
}
