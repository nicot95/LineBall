package mygames.lineball.Activities;


public class Tutorial_Level_2 extends Level {

    public String[] getComments() {
        String comment = "";
        String comment2 = "";
        String comment3 = "";
        String[] comments = new String[3];

        switch (current_state) {
            case INITIAL:
                comment = "Now lets try with 3 balls. Once again,";
                comment2 = "touch and hold any ball!";
                break;
            case FIRST_BALL:
                comment =  "Swipe through another ball of the same";
                comment2 = "color, but be careful with the third";
                comment3 = "ball, it must not hit the line!";
                break;
            case SECOND_BALL:
                comment = "Now, keep holding the finger and select";
                comment2 = "the last moving ball.";
                break;
            case THIRD_BALL:
                comment = "Perfect, now just select the first ball";
                comment2 = "to finish the triangle.";
                break;
            case SHAPE_COMPLETE:
                comment = "Figure completed! Release the finger";
                comment2 = "and the three balls will vanish.";
                break;
            case NEXT_LEVEL:
                /*comment = "The more balls, the more points you";
                comment2 = "will be awarded. Tap to start level 3";
                comment3 = "and learn how to use multicolor balls.";
                break;*/

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

    public Level nextLevel() {
        return new Tutorial_Level_3();
    }
}
