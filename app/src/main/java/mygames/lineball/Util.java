package mygames.lineball;

import android.graphics.Point;

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
            throw new IllegalArgumentException("Segment start equals segment end");
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


}
