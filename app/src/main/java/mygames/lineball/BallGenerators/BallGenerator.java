package mygames.lineball.BallGenerators;

import java.util.Random;

import mygames.lineball.Balls.Ball;
import mygames.lineball.Balls.RandomBall;

/**
 * Created by Daniel on 08/09/2015.
 */
public abstract class BallGenerator {

    protected int differentTypesOfBalls;
    protected int numBalls;

    protected int screenX;
    protected int screenY;

    protected Random gen;

    public BallGenerator(int numBalls, int differentTypesOfBalls, int screenX, int screenY) {
        this.numBalls = numBalls;

        this.differentTypesOfBalls = differentTypesOfBalls;
        this.screenX = screenX;
        this.screenY = screenY;

        this.gen = new Random();
    }

    protected Ball generateBall() {
        int randomColor = gen.nextInt(differentTypesOfBalls);
        Ball newBall;
        if (randomColor == 4) {
            newBall = new RandomBall(screenX, screenY);
        } else {
            newBall = new Ball(screenX, screenY);
        }
        return newBall;
    }
}
