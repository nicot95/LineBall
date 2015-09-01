package mygames.lineball;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Daniel on 01/09/2015.
 */
public class BallGenerator {

    private ArrayList<Ball> balls;
    private int[] differentTypesOfBalls;
    private int numBalls;

    private int screenX;
    private int screenY;

    public BallGenerator(int numBalls, int differentTypesOfBalls, int screenX, int screenY) {
        this.numBalls = numBalls;
        this.balls = new ArrayList<>();
        this.differentTypesOfBalls = new int[differentTypesOfBalls];

        this.screenX = screenX;
        this.screenY = screenY;
    }

    public ArrayList<Ball> generateBalls() {
        Random gen = new Random();
        for (int i = 0; i < numBalls; i++) {
            int randomBallCheck = gen.nextInt(5);
            Ball newBall;
            if (randomBallCheck == 4) {
                newBall = new RandomBall(screenX, screenY);
            } else {
                newBall = new Ball(screenX, screenY);
            }
            getDifferentTypesOfBalls()[newBall.getColorSimple()]++;
            balls.add(newBall);
        }
        return balls;
    }


    public int[] getDifferentTypesOfBalls() {
        return differentTypesOfBalls;
    }
}
