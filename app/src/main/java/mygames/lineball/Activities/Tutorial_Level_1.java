package mygames.lineball.Activities;

/**
 * Created by nico on 09/09/15.
 */
public class Tutorial_Level_1 extends Level{

    public Tutorial_Level_1() {
        end_state = Level_State.NEXT_LEVEL;
    }

    @Override
    String[] getComments() {
        String comment = "";
        String comment2 = "";
        String comment3 = "";
        String[] comments = new String[3];

            switch (current_state) {
                case INITIAL:
                    comment = "Touch and hold any ball!";
                    break;
                case FIRST_BALL    :
                    comment = "Very well, now that the ball is selected,";
                    comment2 = "hold the finger and try selecting";
                    comment3 = "another ball of the same color.";
                    break;
                case SECOND_BALL   :
                    comment = "Now, keep holding the finger and touch";
                    comment2 = "the initial ball to close the figure.";
                    break;
                case THIRD_BALL:
                    comment = "Figure completed! Now release the";
                    comment2 = "finger and both balls will vanish.";
                    break;
                case SHAPE_COMPLETE:
                    comment = "In the next level, you will face with";
                    comment2 = "a more challenging figure: a triangle.";
                    comment3 = "Tap to continue.";
            }

        comments[0] = comment;
        comments[1] = comment2;
        comments[2] = comment3;

        return comments;
    }

    public Level nextLevel() {
        return new Tutorial_Level_2();
    }


}
