/*
 * Copyright (c) 2015. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package mygames.lineball.BallGenerators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mygames.lineball.Balls.Ball;


public class InitialStateBallGenerator extends BallGenerator {

    private List<Ball> balls;
    private int[] differentTypesOfBalls;

    public InitialStateBallGenerator(int numBalls, int differentTypesOfBalls, boolean isTutorial) {
        super(numBalls, differentTypesOfBalls, isTutorial);
        this.numBalls = numBalls;
        this.balls = Collections.synchronizedList(new ArrayList<Ball>());
        this.differentTypesOfBalls = new int[differentTypesOfBalls];

    }

    public List<Ball> generateBalls() {
        for (int i = 0; i < numBalls; i++) {
            Ball newBall = generateBall();
            differentTypesOfBalls[newBall.getColorSimple()]++;
            balls.add(newBall);
        }
        return balls;
    }



    public int[] getDifferentTypesOfBalls() {
        return differentTypesOfBalls;
    }
}
