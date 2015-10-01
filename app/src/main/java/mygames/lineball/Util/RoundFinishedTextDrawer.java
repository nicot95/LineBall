/*
 * Copyright (c) 2015. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package mygames.lineball.Util;

import android.graphics.Canvas;
import android.graphics.Paint;

import mygames.lineball.GameLogic.BallTracker;

/*
    This class manages the printing of the text that will appear at the end of each round
    and also when line contact happens. The important bit is within drawGameOverText, which has
    a variable that manages the amount of time a the text will appear on screen.
 */
public class RoundFinishedTextDrawer {

    private int round;
    private Canvas canvas;
    private Paint paint;
    private final int screenWidth;
    private final int screenHeight;
    private BallTracker.Game_State text;

    private long startingMilliseconds;

    public RoundFinishedTextDrawer(int round,Canvas canvas, Paint paint, int screenWidth,
                                   int screenHeight, BallTracker.Game_State text) {
        //If there has been a line contact, print the actuall round, not the next one
        this.round = (text == BallTracker.Game_State.LINE_CONTACT ||
                text == BallTracker.Game_State.TIME_OUT) ? round : round + 1;
        this.canvas = canvas;
        this.paint = paint;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.text = text;
        this.startingMilliseconds = System.currentTimeMillis();
    }

    public void drawRoundOverText() {
        float textSize = paint.getTextSize();
        paint.setTextSize(60 * MathUtil.getScreenSizeFactor());
        canvas.drawText("Round " + round, screenWidth / 2 - 100, 300, paint);
        paint.setTextSize(40 * MathUtil.getScreenSizeFactor());
        String gameOverText = "";
        int extraScore = 0;
        int extraTime = 0;
        switch (text) {
            case BOARD_CLEARED:
                extraScore = 100 + 50 * round;
                extraTime = 5 + round;
                gameOverText = "All balls cleared!";
                break;
            case NO_POSSIBLE_MOVE:
                gameOverText = "No more moves";
                break;
            case LINE_CONTACT:
                gameOverText = "Line contact";
                break;
            case TIME_OUT:
                gameOverText = "Time out";
            default:
                break;
        }
        canvas.drawText(gameOverText, 100f, screenHeight / 2, paint);
        if (extraScore != 0) {
            canvas.drawText("+" + extraScore + " Bonus", 100f, (screenHeight / 2 ) + 70, paint);
            canvas.drawText("+" + extraTime + " secs" + " Bonus", 100f, (screenHeight / 2) + 140, paint);
        }
    }

    // If the statement returns true, it means that we still need to draw on the screen the text
    public boolean hasToDraw() {
        int MILLISECONDS_LIFESPAWN_GAMEOVER_TEXT = 4000;
        return System.currentTimeMillis() - startingMilliseconds < MILLISECONDS_LIFESPAWN_GAMEOVER_TEXT;
    }
}
