package mygames.lineball;

/**
 * Created by nico on 11/08/15.
 */

import android.graphics.Color;
import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Ball {
    final int LINEWIDTH = 4;

    float xVelocity;
    float yVelocity;

    private float ballRadius = 30;
    private float x;
    private float y;
    private int color;

    private boolean isbeingTracked;

    public Ball(int screenX, int screenY) {

        Random gen = new Random();

        // Start the ball moving at a random speed and direction
        this.xVelocity = gen.nextInt(250) - 125;
        this.yVelocity = gen.nextInt(250) - 125;

        // Place the ball in a random position within the screen. All the ball
        // must be inside the screen to avoid wallCollision bugs
        this.x = gen.nextInt(screenX - 2 * (int) getBallRadius()) + getBallRadius();
        this.y = gen.nextInt(screenY - 2 * (int) getBallRadius()) + getBallRadius();

        this.color = gen.nextInt(4);

        this.isbeingTracked = false;
    }

    //Checks if ball intersects with the given coordinates (the user's touch x and y)
    public boolean intersects(float touchx, float touchy) {
        return touchx > x - getBallRadius() && touchx < x + getBallRadius() &&
                touchy < y + getBallRadius() && touchy > y - getBallRadius();

    }

    // Checks if ball is colliding with a wall, and if so, changes velocity appropriately
    public void checkWallCollision(int screenX, int screenY) {
        if (y + getBallRadius() >= screenY) {
            reverseYVelocity();
            clearObstacleY(2);
        }
        if(y - getBallRadius() <= 0) {
            reverseYVelocity();
            clearObstacleY(-2);
        }


        if (x + getBallRadius() >= screenX) {
            reverseXVelocity();
            clearObstacleX(2);
        }

        if(x - getBallRadius() <= 0) {
            reverseXVelocity();
            clearObstacleX(-2);
        }
    }

    // returns true if a ball has hit a line and therefore game is over
    public boolean ballHitLineGameOver(BallTracker ballTracker) {
        ArrayList<Ball> ballsTracked = ballTracker.getBallsTracked();
        Point thisPoint = new Point((int) x, (int) y);

        for(int i = 1; i < ballsTracked.size(); i++) {
            Ball ball1 = ballsTracked.get(i-1);
            Ball ball2 = ballsTracked.get(i);
            Point point1 = new Point((int) ball1.getX(), (int) ball1.getY());
            Point point2 = new Point((int) ball2.getX(), (int) ball2.getY());

            List<Point> intersectPoint1 = Util.getCircleLineIntersectionPoint(point1, point2, point1, ballRadius);
            List<Point> intersectPoint2 = Util.getCircleLineIntersectionPoint(point1, point2, point2, ballRadius);

            if(!ball1.equals(this) && !ball2.equals(this)
                    && Util.getDistanceToSegment(intersectPoint1.get(0), intersectPoint2.get(0), thisPoint) <= ballRadius+ LINEWIDTH)

                   return true;
            }
        return false;

    }

    public int getColor() {
        int retColor = -1;
        switch (this.color) {
            case 0: retColor = Color.RED;
                break;
            case 1: retColor = Color.YELLOW;
                break;
            case 2: retColor = Color.GREEN;
                break;
            case 3: retColor = Color.BLUE;
                break;
        }

        return retColor;
    }


    public void update(long fps) {
        x = getX() + xVelocity / fps;
        y += yVelocity / fps;
    }

    public void reverseYVelocity() {
        yVelocity = -yVelocity;
    }

    public void reverseXVelocity() {
        xVelocity = -xVelocity;
    }

    public void setRandomXVelocity() {
        Random generator = new Random();
        int answer = generator.nextInt(2);

        if (answer == 0) {
            reverseXVelocity();
        }
    }

    public void clearObstacleY(int diff) {
        this.y -= diff;
    }

    public void clearObstacleX(int diff) {
        this.x -= diff;
    }

    public void reset(int x, int y) {
        this.x = x / 2;
        this.y = y - 20;
    }

    //Slows ball when it is being tracked
    public void stop() {
        if (!isbeingTracked) {
            xVelocity = 0;
            yVelocity = 0;
            isbeingTracked = true;
        }
        //erase me
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getBallRadius() {
        return ballRadius;

    }

    public void setIsbeingTracked(boolean isbeingTracked) {
        this.isbeingTracked = isbeingTracked;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof  Ball) {
            Ball ball2 = (Ball) o;
            return this.x == ball2.x && this.y == ball2.y && this.color == ball2.color;
        }
        return false;
    }



}