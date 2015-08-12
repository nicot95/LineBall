package mygames.lineball;

/**
 * Created by nico on 11/08/15.
 */
import android.graphics.RectF;

import java.util.Random;

public class Ball {
    RectF rect;

    float xVelocity;
    float yVelocity;
    float ballWidth = 10;
    float ballHeight = 10;

    private float ballRadius = 60;
    private float x;
    private float y;

    private boolean isbeingTracked;

    public Ball(int screenX, int screenY) {

        Random gen = new Random();


        // Start the ball travelling straight up at 100 pixels per second
        xVelocity = gen.nextInt(100) - 25;
        yVelocity = gen.nextInt(100) - 25;

        // Place the ball in the centre of the screen at the bottom
        // Make it a 10 pixel x 10 pixel square

        x = screenX;
        y = screenY;

        isbeingTracked = false;
    }

    //Checks if ball intersects with the given coordinates (the user's touch x and y)
    public boolean intersects(float touchx, float touchy) {
        return touchx > x - getBallRadius() && touchx < x + getBallRadius() &&
                touchy < y + getBallRadius() && touchy > y - getBallRadius();

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

    public void clearObstacleY(float y) {
        rect.bottom = y;
        rect.top = y - ballHeight;
    }

    public void clearObstacleX(float x) {
        rect.left = x;
        rect.right = x + ballWidth;
    }

    public void reset(int x, int y) {
        this.x = x / 2;
        this.y = y - 20;
    }

    //Slows ball when it is being tracked
    public void stop() {
        if (!isbeingTracked) {
            xVelocity *= 0.3;
            yVelocity *= 0.3;
            isbeingTracked = true;
        }
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
            return this.x == ball2.x && this.y == ball2.y;
        }
        return false;
    }
}