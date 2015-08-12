package mygames.lineball;

/**
 * Created by nico on 11/08/15.
 */

import android.graphics.Color;
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
    private int color;

    private boolean isbeingTracked;

    public Ball(int screenX, int screenY) {

        Random gen = new Random();

        // Start the ball moving at a random speed and direction
        this.xVelocity = gen.nextInt(100) - 50;
        this.yVelocity = gen.nextInt(100) - 50;

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
        if (y + getBallRadius() >= screenY || y - getBallRadius() <= 0) {
            reverseYVelocity();
        }

        if (x + getBallRadius() >= screenX || x - getBallRadius() <= 0) {
            reverseXVelocity();
        }
    }

    public int getColor() {
        int color = Color.BLUE;
        switch (this.color) {
            case 1: color = Color.RED;
            case 2: color = Color.YELLOW;
            case 3: color = Color.GREEN;
            case 4: color = Color.BLUE;


        }

        return color;
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