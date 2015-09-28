package mygames.lineball.BallGenerators;

import java.util.Random;

import mygames.lineball.Balls.Ball;
import mygames.lineball.Balls.RandomBall;
import mygames.lineball.Util.MathUtil;


public abstract class BallGenerator {

    protected int differentTypesOfBalls;
    protected int numBalls;

    protected int screenX;
    protected int screenY;

    protected Random gen;

    public BallGenerator(int numBalls, int differentTypesOfBalls, boolean isTutorial) {
        this.numBalls = numBalls;

        this.differentTypesOfBalls = differentTypesOfBalls;
        this.screenX = MathUtil.getScreenWidth();
        this.screenY = isTutorial ? (int) (0.83 * MathUtil.getScreenHeight()) : MathUtil.getScreenHeight();

        this.gen = new Random();
    }

    protected Ball generateBall() {
        int randomColor = gen.nextInt(differentTypesOfBalls);
        Ball newBall;
        if (randomColor == 4) {
            newBall = new RandomBall(screenX, screenY);
        } else {
            newBall = new Ball(screenX, screenY, randomColor);
        }
        return newBall;
    }


}
