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


    private ArrayList<Ball> ballsTracked;
    private boolean readyToCalculateScore;
    private int shapeMultiplier;

    //Fields to prevent finishing a chain in the same ball or in the previous one.
    private Ball lastTrackedBall;
    private Ball currentTrackedBall;

    private int colorChain;
    private boolean sameColorPreserved;

    public BallTracker() {

        ballsTracked = new ArrayList<>();
        readyToCalculateScore = false;
        colorChain = -1;

        lastTrackedBall    = null;
        currentTrackedBall = null;
        sameColorPreserved = true;

    }

    public void trackBall(Ball b) {

        if (ballsTracked.isEmpty()) {
            colorChain = b.getColor();
        }

        /*
            If the ball is already in the chain, check if it is closing a shape
            or if it is joining a
         */
        if (ballsTracked.contains(b)) {
            if (!b.equals(currentTrackedBall)) {
                //It is not a hack, it's a shape
                ballsTracked.add(b);
                shapeMultiplier = ballsTracked.size();
                this.readyToCalculateScore = true;
                checkColor(b.getColor());
            }
        } else {
            //Ball is not being tracked, add to list
            ballsTracked.add(b);
            lastTrackedBall    = currentTrackedBall;
            currentTrackedBall = b;
            checkColor(b.getColor());
        }
    }

    private void checkColor(int color) {
        if (colorChain != color) {
            sameColorPreserved = false;
        }
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
        score *= sameColorPreserved ? 2 : 1;
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
        sameColorPreserved = true;
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


}
