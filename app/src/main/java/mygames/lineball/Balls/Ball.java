/*
 * Copyright (c) 2015. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package mygames.lineball.Balls;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

import mygames.lineball.BallGenerators.SurvivalBallGenerator;
import mygames.lineball.Util.MathUtil;

public class Ball {
    //commit test iml shitty bug
    private float xVelocity;
    private float yVelocity;
    private final static int MAX_SPEED = Math.round(125 * MathUtil.getScreenSizeFactor());

    protected float ballRadius = (float) (30 * MathUtil.getScreenSizeFactor());
    protected float x;
    protected float y;
    protected int color;



    public final static int RANDOM_COLOR = 4;

    private boolean isbeingTracked;

    protected static Random gen = new Random();


    public Ball(int screenX, int screenY, int color) {

        // Start the ball moving at a random speed and direction
        this.xVelocity = gen.nextInt(MAX_SPEED *2) -  MAX_SPEED;
        this.yVelocity = gen.nextInt(MAX_SPEED *2) -  MAX_SPEED;

        // Place the ball in a random position within the screen. All the ball
        // must be inside the screen to avoid wallCollision bugs
        this.x = gen.nextInt(screenX - 2 * (int) getBallRadius()) + getBallRadius();
        this.y = gen.nextInt(screenY - 2 * (int) getBallRadius()) + getBallRadius();

        this.color = color;

        this.isbeingTracked = false;
    }

    //Checks if ball intersects with the given coordinates (the user's touch x and y)
    public boolean intersects(float touchx, float touchy) {
        double extraRadius = 8;
        return touchx > x - getBallRadius() - extraRadius &&
                touchx < x + getBallRadius() + extraRadius &&
                touchy < y + getBallRadius() + extraRadius &&
                touchy > y - getBallRadius() - extraRadius;

    }

    public int getColor() {
        int retColor = -1;
        int ballColor = this.color;
        switch (ballColor) {
            case 0: retColor = ColorBall.getRed();
                break;
            case 1: retColor = ColorBall.getBlue();
                break;
            case 2: retColor = ColorBall.getYellow();
                break;
            case 3: retColor = ColorBall.getGreen();
                break;
        }

        return retColor;
    }

    public int getColorSimple() {
        return this.color;
    }


    public void update(long fps) {
        if (!isbeingTracked) {
            x = getX() + getxVelocity() / fps;
            y += getyVelocity() / fps;
        }
    }

    public void reverseYVelocity() {
        yVelocity = -getyVelocity();
    }

    public void reverseXVelocity() {
        xVelocity = -getxVelocity();
    }

    public void clearObstacleY(int diff) {
        this.y -= diff;
    }

    public void clearObstacleX(int diff) {
        this.x -= diff;
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

    @Override
    public boolean equals(Object o) {
        if (o instanceof  Ball) {
            Ball ball2 = (Ball) o;
            return this.x == ball2.x && this.y == ball2.y && this.color == ball2.color;
        }
        return false;
    }


    public void draw(Paint paint, Canvas canvas) {
        paint.setColor(getColor());
        canvas.drawCircle(x, y, ballRadius, paint);
    }

    public float getxVelocity() {
        return xVelocity;
    }

    public float getyVelocity() {
        return yVelocity;
    }

    public void setVelocityAndPosition(int goodX, int goodY, int extraSpeedPercentage, SurvivalBallGenerator.Direction direction) {
        this.x = goodX;
        this.y = goodY;

        int extraSpeed = MAX_SPEED * extraSpeedPercentage /100;
        int constantExtraSpeed = MAX_SPEED/5;

        switch (direction) {

            case NORTH:
                this.yVelocity = - (gen.nextInt(MAX_SPEED) + constantExtraSpeed + extraSpeed);
                this.xVelocity = gen.nextInt((int) -yVelocity);
                break;
            case WEST:
                this.xVelocity = - (gen.nextInt(MAX_SPEED) + constantExtraSpeed + extraSpeed);
                this.yVelocity = gen.nextInt((int) -xVelocity);
                break;
            case SOUTH:
                this.yVelocity = gen.nextInt(MAX_SPEED)  + constantExtraSpeed + extraSpeed;
                this.xVelocity = gen.nextInt((int) yVelocity);
                break;
            case EAST:
                this.xVelocity = gen.nextInt(MAX_SPEED)  + constantExtraSpeed + extraSpeed;
                this.yVelocity = gen.nextInt((int) xVelocity);
                break;
        }

    }

    public void setColor(int color) {
        this.color = color;
    }
}