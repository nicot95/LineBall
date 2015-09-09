package mygames.lineball;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mygames.lineball.Balls.Ball;

/*
    The ball tracker keeps an account of what balls have been linked so far.

    BallTracker is notified each time a ball makes contact with the linking line. Adding it to the
    list of tracked balls. When a link is complete, the result score will be calculated here depending on the
    balls tracked.

    If a link is broken by a passing ball, this object will delete all its content
 */
public class BallTracker {


    private int[] numBallsPerType;
    private List<Ball> ballsTracked;
    private boolean readyToCalculateScore;
    private int shapeMultiplier;

    //Fields to prevent finishing a chain in the same ball or in the previous one.
    private Ball lastTrackedBall;
    private Ball currentTrackedBall;

    private int colorChain;
    private int colorComparison;
    private Game_State game_state;



    public enum Game_State {
        NOT_OVER,
        LINE_CONTACT,
        NO_POSSIBLE_MOVE,
        BOARD_CLEARED
    }

    public BallTracker(int[] numBallsPerType) {

        ballsTracked = Collections.synchronizedList(new ArrayList<Ball>());
        readyToCalculateScore = false;
        game_state = Game_State.NOT_OVER;
        colorChain = -1;

        lastTrackedBall    = null;
        currentTrackedBall = null;

        this.numBallsPerType = numBallsPerType;

    }

    public void trackBall(Ball b) {


        //Generates the colour of the chain that is gonna link all balls
        getChainColor(b);

        int ballColor = b.getColorSimple();
        if (ballColor == Ball.RANDOM_COLOR || ballColor == colorComparison ||
                colorComparison == Ball.RANDOM_COLOR) {
            checkForChainProperties(b);
        }



    }

    private void checkForChainProperties(Ball b) {

        if(isReadyToCalculateScore()) {
            return;
        }
        //If the ball is already in the chain, check if it is closing a shape
        if (ballsTracked.contains(b)) {
            checkForShape(b);
        } else {
            //Ball is not being tracked, add to list
            ballsTracked.add(b);
            trackNewBall(b);
        }
    }

    /*private void checkForChainProperties(Ball b) {
        if (!ballsTracked.contains(b) || (ballsTracked.size() > 1 && b.equals(ballsTracked.get(0)))) {
            //Ball is not being tracked, add to list
            ballsTracked.add(b);
            trackNewBall(b);
        }
    }*/

    public void resumeMovement() {
        if ( currentTrackedBall != null) {
            for (Ball ball: ballsTracked) {
                ball.resumeMovement();
            }
            cleanUpBallsFields();
        }
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


    public void checkForShape(Ball b) {
        if (shapeIsComplete(b)) {
            //Shape has been completed, prepare to calculate score
            //ballsTracked.remove(ballsTracked.size()-1);
            shapeMultiplier = ballsTracked.size();
            this.readyToCalculateScore = true;
            ballsTracked.add(b);

        }
    }

    private boolean shapeIsComplete(Ball b) {
        return !(b.equals(currentTrackedBall) ||
                (b.equals(lastTrackedBall) && ballsTracked.size() > 2));
    }

    public int clearShape() {
        Ball lastBall = ballsTracked.get(ballsTracked.size()-1);
        ballsTracked.remove(lastBall);
        int balls_cleared = ballsTracked.size();
        for (Ball ball: ballsTracked) {
                numBallsPerType[ball.getColorSimple()]--;
        }
        gameOverCheck();
        return balls_cleared;
    }

    /*
        Calculates if there is a minimum amount of balls required to make a link.
        Otherwise, the game is over.
     */
    private void gameOverCheck() {
        int MINIMUM_BALLS_FOR_LINK = 2;
        int randomBalls = 0;
        if(numBallsPerType.length == 5) {
            randomBalls = numBallsPerType[Ball.RANDOM_COLOR];
        }
        boolean isBoardCleared = true;
        for (int i = 0; i < numBallsPerType.length; i++) {
            if (i == Ball.RANDOM_COLOR)
                randomBalls = 0;
            if (numBallsPerType[i] > 0) {
                isBoardCleared = false;
            }
            if (numBallsPerType[i] + randomBalls >= MINIMUM_BALLS_FOR_LINK) {
                return;
            }
        }
        if (isBoardCleared) {
            game_state = Game_State.BOARD_CLEARED;
        } else {
            game_state = Game_State.NO_POSSIBLE_MOVE;
        }
    }

    //A line has been touched by a ball, game should stop
    public void setGameStateToLineContact() {
        game_state = Game_State.LINE_CONTACT;
    }

    //Very basic algorithm to calculate score depending on the number of balls tracked
    // Will hold the final algorithm at some point
    public int calculateScore() {
        int score = 0;

        score += ballsTracked.size();
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

    public List<Ball> getBallsTracked() {
        return ballsTracked;
    }

    public boolean isGameOver() {
        return game_state != Game_State.NOT_OVER;
    }

    public Game_State getGameState() { return game_state; }
}
