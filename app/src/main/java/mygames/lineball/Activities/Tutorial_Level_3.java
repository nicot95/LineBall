package mygames.lineball.Activities;


public class Tutorial_Level_3 extends Level{


    @Override
    String[] getComments() {

        String comment = "";
        String comment2 = "";
        String comment3 = "";
        String[] comments = new String[3];

        switch (current_state) {
            case INITIAL:
                comment = "The Multicolor ball has all the colors,";
                comment2 = "so it can be joined with any ball.";
                comment3 = "Touch and hold any ball";
                break;
            case FIRST_BALL:
                comment =  "Swipe through another ball of the same";
                comment2 = "color.";
                break;
            case SECOND_BALL:
                comment = "Now, keep holding the finger and select";
                comment2 = "the last moving ball.";
                break;
            case THIRD_BALL:
                comment = "Perfect, now just select the first ball";
                comment2 = "again to finish the triangle.";
                break;
            case SHAPE_COMPLETE:
                comment = "Figure completed! Release the finger";
                comment2 = "and the three balls will vanish.";
                break;
            case NEXT_LEVEL:
                comment = "Figure completed! Release the finger";
                comment2 = "and the three balls will vanish.";
                break;
            case LINE_CONTACT:
                comment = "Line contact! Balls must not hit";
                comment2 = "the lines. Tap to restart.";
                break;

            case NOT_ALL_BALLS_SHAPE:
                comment = "Don't be a chicken! Try doing a triangle";
                comment2 = "with 3 balls, not 2. This will give you";
                comment3 = "more points. Tap to resume.";

        }

        comments[0] = comment;
        comments[1] = comment2;
        comments[2] = comment3;

        return comments;
    }


    @Override
    Level nextLevel() {
        return null;
    }
}
