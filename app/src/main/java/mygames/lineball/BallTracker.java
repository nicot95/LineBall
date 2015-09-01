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


    public int[] numBallsPerType;
    private ArrayList<Ball> ballsTracked;
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

        ballsTracked = new ArrayList<>();
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
        //If the ball is already in the chain, check if it is closing a shape
        if (ballsTracked.contains(b)) {
            checkForShape(b);
        } else {
            //Ball is not being tracked, add to list
            ballsTracked.add(b);
            trackNewBall(b);
        }
    }

    public boolean checkForResumeMovement(Ball b) {
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
            gameOverCheck();
        }
    }

    /*
        Calculates if there is a minimum amount of balls required to make a link.
        Otherwise, the game is over.
     */
    private void gameOverCheck() {
        int MINIMUM_BALLS_FOR_LINK = 2;
        int randomBalls = numBallsPerType[Ball.RANDOM_COLOR];
        boolean isBoardCleared = true;
        for (int i = 0; i < numBallsPerType.length; i++) {
            if (i == Ball.RANDOM_COLOR)
                randomBalls = 0;
            if (numBallsPerType[i] > 0) {
                isBoardCleared = false;
            }
            if (numBallsPerType[i] + randomBalls > MINIMUM_BALLS_FOR_LINK) {
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

    public ArrayList<Ball> getBallsTracked() {
        return ballsTracked;
    }

    public boolean isGameOver() {
        return game_state != Game_State.NOT_OVER;
    }

    public Game_State getGameState() { return game_state; }
}
