package mygames.lineball.Activities;

/**
 * Created by nico on 09/09/15.
 */
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
                comment =  "Very well, again keep holding and select";
                comment2 = "another ball. Keep an eye on the third";
                comment3 = "ball, as it must not hit the line!";
                break;
            case SECOND_BALL:
                comment = "Well done! Now, keep holding the finger";
                comment2 = "and select the last moving ball.";

                break;
            case THIRD_BALL:
                comment = "Perfect, now just select the first ball";
                comment2 = "to finish the triangle.";
                break;
            case SHAPE_COMPLETE:
                comment = "Shape completed! Release the finger";
                comment2 = "and the three balls will vanish.";
                break;
            case NEXT_LEVEL:
                comment = "The more balls, the more points you";
                comment2 = "will be awarded. Tap to start level 3";
                comment3 = "and learn how to use multicolor balls";
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
