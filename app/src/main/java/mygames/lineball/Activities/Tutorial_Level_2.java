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
                comment =  "Very well, now hold the finger and try selecting";
                comment2 = "another ball of the same color. But careful with";
                comment3 = "the third ball. It must not cross the line!";
                break;
            case SECOND_BALL:
                comment = "Well done! Now, keep holding the finger";
                comment2 = "and select the last ball ";
                comment3 = "the shape.";
                break;
            case SHAPE_COMPLETE:
                comment = "Shape is complete! Both balls vanish";
                comment2 = "and the first step of tutorial is";
                comment3 = "complete!";
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
