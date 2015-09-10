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
                comment =  "Very well, now hold the finger and select";
                comment2 = "another ball of the same color. Be careful with";
                comment3 = "with the third ball. It must not cross the line!";
                break;
            case SECOND_BALL:
                comment = "Well done! Now, keep holding the finger";
                comment2 = "and select the last moving ball";

                break;
            case THIRD_BALL:
                comment = "Perfect, now just select the first ball";
                comment2 = "to finish the triangle. This shape will";
                comment3 = "give you more points than the 2 ball line!";
                break;
            case SHAPE_COMPLETE:
                comment = "Shape is complete! The three balls vanish";
                comment2 = "and you will get more points than in the";
                comment3 = "previous exercise. The more balls the merrier!";
        }

        comments[0] = comment;
        comments[1] = comment2;
        comments[2] = comment3;

        return comments;
    }

    public Level nextLevel() {
        return this;
    }
}
