package mygames.lineball;

import java.util.ArrayList;

/*
    The ball tracker keeps an account of what balls have been linked so far.

    BallTracker is notified each time a ball makes contact with the linking line. Adding it to the
    list of tracked balls. When a link is complete, the result score will be calculated here depending on the
    balls tracked.

    If a link is broken by a passing ball, this object will delete all its content
 */
public class BallTracker {


    private int[] numBallsPerType;
    private ArrayList<Ball> ballsTracked;
    private boolean readyToCalculateScore;
    private int shapeMultiplier;

    //Fields to prevent finishing a chain in the same ball or in the previous one.
    private Ball lastTrackedBall;
    private Ball currentTrackedBall;

    private int colorChain;
    private int colorComparison;
    private boolean isGameOver;

    public BallTracker(int[] numBallsPerType) {

        ballsTracked = new ArrayList<>();
        readyToCalculateScore = false;
        isGameOver = false;
        colorChain = -1;

        lastTrackedBall    = null;
        currentTrackedBall = null;

        this.numBallsPerType = numBallsPerType;

    }

    public void trackBall(Ball b) {

        //Generates the colour of the chain that is gonna link all balls
        getChainColor(b);

        //Stop tracking ball if tapped again
        boolean wantedToUntrack = checkForResumeMovement(b);
        if (wantedToUntrack) return;

        int ballColor = b.getColorSimple();
        if (ballColor == Ball.RANDOM_COLOR || ballColor == colorComparison ||
                colorComparison == Ball.RANDOM_COLOR) {
            checkForChainProperties(b);
        }

        gameOverCheck();
    }

    private void checkForChainProperties(Ball b) {
        //If the ball is already in the chain, check if it is closing a shape
        if (ballsTracked.contains(b)) {
            checkForShape(b);
        } else {
            //Ball is not being tracked, add to list
            ballsTracked.add(b);
            trackNewBall(b);
        }
    }

    private boolean checkForResumeMovement(Ball b) {
        if ( currentTrackedBall != null && currentTrackedBall.equals(b)) {
            for (Ball ball: ballsTracked) {
                ball.resumeMovement();
            }
            cleanUpBallsFields();
            return true;
        }
        return false;
    }

    private void getChainColor(Ball b) {
        if (ballsTracked.isEmpty() || colorComparison == Ball.RANDOM_COLOR) {
            colorChain = b.getColor();
            colorComparison = b.getColorSimple();
        }
    }

    private void trackNewBall(Ball b) {
        lastTrackedBall    = currentTrackedBall;
        currentTrackedBall = b;
        b.stop();
    }

    private void checkForShape(Ball b) {
        if (!(b.equals(currentTrackedBall) || (b.equals(lastTrackedBall) && ballsTracked.size() > 2))) {
            //Shape has been completed, prepare to calculate score
            shapeMultiplier = ballsTracked.size();
            this.readyToCalculateScore = true;
            for (Ball ball: ballsTracked) {
                numBallsPerType[ball.getColorSimple()]--;
            }

        }
    }

    /*
        Calculates if there is a minimum amount of balls required to make a link.
        Otherwise, the game is over.
     */
    private void gameOverCheck() {
        int MINIMUM_BALLS_FOR_LINK = 2;
        int randomBalls = numBallsPerType[Ball.RANDOM_COLOR] ;
        for (int i = 0; i < numBallsPerType.length; i++) {
            if (i == Ball.RANDOM_COLOR)
                randomBalls = 0;
            if (numBallsPerType[i] + randomBalls >= MINIMUM_BALLS_FOR_LINK) {
                return;
            }
        }
        isGameOver = true;
    }

    //Very basic algorithm to calculate score depending on the number of balls tracked
    // Will hold the final algorithm at some point
    public int calculateScore() {
        int score = 0;

        //This loop will handle special cases, such as balls with the same color, special shapes
        // special number of balls... etc
        for (Ball b: ballsTracked) {
            score += 10;
        }
        score *= shapeMultiplier;
        return score;
    }

    //Resets fields to initial state after a succesfull play has been made
    public void cleanUpBallsFields() {
        ballsTracked.clear();
        readyToCalculateScore = false;
        shapeMultiplier       = 0;

        colorChain = -1;

        lastTrackedBall    = null;
        currentTrackedBall = null;
    }

    public int getColorChain() {
        return colorChain;
    }

    public boolean isReadyToCalculateScore() {
        return readyToCalculateScore;
    }

    public void setReadyToCalculate() {
        this.readyToCalculateScore = true;
    }

    public ArrayList<Ball> getBallsTracked() {
        return ballsTracked;
    }


    public boolean isGameOver() {
        return isGameOver;
    }
}
