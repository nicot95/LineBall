package mygames.lineball.BallGenerators;

import mygames.lineball.Balls.Ball;
import mygames.lineball.Util.MathUtil;

/*
    This class holds the logic used in the survival mode regarding where should balls appear
    and when they should appear.
 */
public class SurvivalBallGenerator extends BallGenerator {


    private int maxSpeed; // Will increase with each round

    public enum Direction { NORTH, WEST, SOUTH, EAST}
    private int[] ballsPerDirection; // The number of balls that have been generated on each Direction
    private Direction[] directions;  // Array holding the values of Directon Enum {NORTH, WEST...}
    private int averageBallsPerDirection;

    private int desiredBallCount; //Max number of balls that are going to populate the screen

    private int round; //Current round
    private boolean loadingNewRound; // Flag to mark that new balls are being generated for new round
    private int newBallsAddedThisRound;

    public SurvivalBallGenerator(int numBalls, int differentTypesOfBalls,
                                 int desiredBallCount) {
        super(numBalls, differentTypesOfBalls, false);

        this.desiredBallCount        = desiredBallCount;
        this.maxSpeed                = (int) (125 * MathUtil.getScreenSizeFactor());

        ballsPerDirection            = new int[Direction.values().length];
        directions                   = Direction.values();

        this.round                   = 1;
        this.loadingNewRound         = false;

    }

    /*
        Main method of this class, returns a ball that will be smartly placed to get
        a positive experience. This method will be called when a set of balls are
     */
    public Ball generateSurvivalBall() {
        int candidateDirectionIndex = gen.nextInt(Direction.values().length);
        int candidateDirection = ballsPerDirection[candidateDirectionIndex];
        int difference = averageBallsPerDirection - candidateDirection;

        Ball newBall;
        if (difference > 0) {
        /*
            if the difference is positive, it means that such direction is below average.
            In order to smoothen the likelihood of this happening, a second try happens. The chances
             of this direction being tbe chosen one are proportional to the difference between the
             average and the direction's count.;
         */
            int random2 = gen.nextInt(averageBallsPerDirection);
            if (random2 < difference) {
                newBall = generateBallInDirection(directions[candidateDirectionIndex]);
            } else {
                newBall = generateSurvivalBall(); //Nico, hace cuanto que no usamos recursion??
            }
        } else if (difference == 0) { // choose at random
            Direction randomDirection = directions[gen.nextInt(Direction.values().length)];
            newBall = generateBallInDirection(randomDirection);
        } else {
        /*
            This direction is above average, do not create a ball here. Instead, even it out
            with the least populated direction.
        */
            newBall = generateBallInDirection(biggestDifferenceFromAverageDirection());
        }
        calculateAverage();
        return newBall;
    }

    //Creates a ball that will come from outside the view from the parameter direction
    private Ball generateBallInDirection(Direction candidateDirection) {
        // This two variables are used to improve graphically where the balls are going to start at
        int XdelimitedPixels = (int) (screenX * 0.1);
        int YdelimitedPixels = (int) (screenY * 0.3);

        int goodX, goodY, index = 0;
        switch (candidateDirection) {
            case NORTH:
                goodY = - 50;
                goodX = gen.nextInt(screenX - XdelimitedPixels) + XdelimitedPixels;
                index = 0;
                break;
            case WEST:
                goodX = - 50;
                goodY = gen.nextInt(screenY - YdelimitedPixels) + YdelimitedPixels;
                index = 1;
                break;
            case SOUTH:
                goodY = screenY + 50;
                goodX = gen.nextInt(screenX - XdelimitedPixels) + XdelimitedPixels;
                index = 2;
                break;
            case EAST:
                goodX = screenX + 50;
                goodY = gen.nextInt(screenY - YdelimitedPixels) + YdelimitedPixels;
                index = 3;
                break;
            default: goodX = goodY = 0;
                break;
        }
        Ball newBall = generateBall();
        newBall.setVelocityAndPosition(goodX, goodY, (int) (MathUtil.getScreenSizeFactor() * maxSpeed), candidateDirection);

        newBallsAddedThisRound++;
        ballsPerDirection[index]++;
        return newBall;
    }

    //Recalculates the average value of all the balls depending on the direction they came from.
    private void calculateAverage() {
        int sum = 0;
        for (int i: ballsPerDirection) {
            sum += i;
        }
        this.averageBallsPerDirection = sum / Direction.values().length;
    }

    private Direction biggestDifferenceFromAverageDirection() {
        int min = ballsPerDirection[0];
        int index = 0;
        for (int i = 1; i < ballsPerDirection.length; i++) {
            if (ballsPerDirection[i] < min) {
                min = ballsPerDirection[i];
                index = i;
            }
        }
        return directions[index];
    }

    public void deduceBalls(int ballsCleared) {
        this.numBalls -= ballsCleared;
    }

    //Called at the beginning of a new wave
    public boolean loadNewRound() {
        /* If condition is true, new balls will be generated, otherwise stop the ball production
           Ball leftovers don't affect the desired cound because they are not considered when
           adding them to the new round.
        */
        if (!loadingNewRound) {
            this.loadingNewRound = true;
            this.round++;
            int MAX_BALLS = 30;
            if (((round % 2) == 0) && (desiredBallCount < MAX_BALLS)){
                this.desiredBallCount      += round;
            }
            this.newBallsAddedThisRound = 0;
            return true;
        } else if (newBallsAddedThisRound == desiredBallCount) {
            this.loadingNewRound = false;
            return false;
        } else if (loadingNewRound) {
            return true;
        }
        return false;
    }

    public int getRound() {
        return round;
    }

    public int getBallsInRound() {
        return this.desiredBallCount;
    }


}
