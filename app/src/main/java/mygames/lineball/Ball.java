package mygames.lineball;

/**
 * Created by nico on 11/08/15.
 */

import android.graphics.Color;

import java.util.Random;

public class Ball {
    float xVelocity;
    float yVelocity;

    private float ballRadius = 30;
    private float x;
    private float y;
    protected int color;

    public final static int RANDOM_COLOR = 4;

    private boolean isbeingTracked;

    protected static Random gen = new Random();


    public Ball(int screenX, int screenY) {

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
        double extraRadius = 2.5;
        return touchx > x - getBallRadius() - extraRadius &&
                touchx < x + getBallRadius() + extraRadius &&
                touchy < y + getBallRadius() + extraRadius &&
                touchy > y - getBallRadius() - extraRadius;

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



    public int getColor() {
        int retColor = -1;
        int ballColor = this.color;
        switch (ballColor) {
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

    public int getColorSimple() {
        return this.color;
    }


    public void update(long fps) {
        if (!isbeingTracked) {
            x = getX() + xVelocity / fps;
            y += yVelocity / fps;
        }
    }

    public void reverseYVelocity() {
        yVelocity = -yVelocity;
    }

    public void reverseXVelocity() {
        xVelocity = -xVelocity;
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
        isbeingTracked = true;
    }

    public void resumeMovement() {
        isbeingTracked = false;
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

    public boolean isBeingTracked() {
        return isbeingTracked;
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