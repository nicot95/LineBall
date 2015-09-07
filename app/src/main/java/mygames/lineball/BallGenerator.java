package mygames.lineball;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Daniel on 01/09/2015.
 */
public class BallGenerator {

    private List<Ball> balls;
    private int[] differentTypesOfBalls;
    private int numBalls;

    private int screenX;
    private int screenY;
    private int color = -1;

    public BallGenerator(int numBalls, int differentTypesOfBalls, int screenX, int screenY, int color) {
        this.numBalls = numBalls;
        this.balls = Collections.synchronizedList(new ArrayList<Ball>());
        this.differentTypesOfBalls = new int[differentTypesOfBalls];

        this.screenX = screenX;
        this.screenY = screenY;
        //this(numBalls, differentTypesOfBalls, screenX, screenY);
        this.color = color;
    }

    public List<Ball> generateBalls() {
        int randomColor = -1;
        Random gen = new Random();
        // if color is given
        if(color != -1) {
            randomColor = color;
        }
        for (int i = 0; i < numBalls; i++) {
            if(color == -1) {
                randomColor = gen.nextInt(5);
            }
            Ball newBall;
            if (randomColor == 4) {
                newBall = new RandomBall(screenX, screenY);
            } else {
                newBall = new Ball(screenX, screenY, randomColor);
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
