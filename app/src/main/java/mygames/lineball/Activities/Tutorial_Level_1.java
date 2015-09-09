package mygames.lineball.Activities;

/**
 * Created by nico on 09/09/15.
 */
public class Tutorial_Level_1 extends Level{

    public Tutorial_Level_1() {
        end_state = Level_State.THIRD_BALL;
    }

    @Override
    String[] getComments() {
        String comment = "";
        String comment2 = "";
        String comment3 = "";
        String[] comments = new String[3];

            switch (current_state) {
                case INITIAL:        comment = "Touch and hold any ball!";
                    break;
                case FIRST_BALL    : comment = "Very well, now that the ball is selected,";
                    comment2 = "hold the finger and try selecting";
                    comment3 = "another ball of the same color.";
                    break;
                case SECOND_BALL   : comment = "Well done! Now, keep holding the finger";
                    comment2 = "and touch the initial ball to finish";
                    comment3 = "the shape.";
                    break;
                case THIRD_BALL:
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
        return new Tutorial_Level_2();
    }


}
