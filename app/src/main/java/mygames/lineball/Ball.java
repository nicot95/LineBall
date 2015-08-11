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

    private boolean isVisible;

    public Ball(int screenX, int screenY){

        Random gen = new Random();


        // Start the ball travelling straight up at 100 pixels per second
        xVelocity = gen.nextInt(100) -25;
        yVelocity = gen.nextInt(100) -25;

        // Place the ball in the centre of the screen at the bottom
        // Make it a 10 pixel x 10 pixel square

        x = screenX;
        y = screenY;

        isVisible = true;
    }

    //Checks if ball intersects with the given coordinates (the user's touch x and y)
    public boolean intersects(float touchx, float touchy) {
        return touchx > x - getBallRadius() && touchx < x + getBallRadius() &&
                touchy < y + getBallRadius() && touchy > y - getBallRadius();

    }

    public void update(long fps){
        x = getX() + xVelocity / fps;
        y += yVelocity / fps;
    }

    public void reverseYVelocity(){
        yVelocity = -yVelocity;
    }

    public void reverseXVelocity(){
        xVelocity = - xVelocity;
    }

    public void setRandomXVelocity(){
        Random generator = new Random();
        int answer = generator.nextInt(2);

        if(answer == 0){
            reverseXVelocity();
        }
    }

    public void clearObstacleY(float y){
        rect.bottom = y;
        rect.top = y - ballHeight;
    }

    public void clearObstacleX(float x){
        rect.left = x;
        rect.right = x + ballWidth;
    }

    public void reset(int x, int y){
        this.x = x / 2;
        this.y = y - 20;
    }

    public void stop() {
        xVelocity = 0;
        yVelocity = 0;
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

    public boolean isVisible() {
        return isVisible;
    }

    public void setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

}