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
                    comment = "Well done! Now, keep holding the finger";
                    comment2 = "and touch the initial ball to finish";
                    comment3 = "the shape.";
                    break;
                case THIRD_BALL:
                    comment = "Shape completed! Now release the finger";
                    comment2 = "and both balls will vanish. This is";
                    comment3 = "the simplest shape you can make.";
                    break;
                case SHAPE_COMPLETE:
                    comment = "Tap to start level 2 of the tutorial,";
                    comment2 = "where you will make a more challenging";
                    comment3 = "shape: a triangle";
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
