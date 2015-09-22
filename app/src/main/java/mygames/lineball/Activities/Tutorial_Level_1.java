package mygames.lineball.Activities;

/**
 * Created by nico on 09/09/15.
 */
public class Tutorial_Level_1 extends Level{

    public Tutorial_Level_1() {
        end_state = Level_State.SHAPE_COMPLETE;
    }

    @Override
    String[] getComments() {
        String comment = "";
        String comment2 = "";
        String comment3 = "";
        String[] comments = new String[3];

            switch (current_state) {
                case INITIAL:
                    comment = "TOUCH and HOLD any ball!";
                    break;
                case FIRST_BALL:
                    comment = "HOLD the finger and SWIPE through";
                    comment2 = "another ball of the SAME color.";
                    break;
                case SECOND_BALL:
                    comment = "Keep holding the finger and swipe";
                    comment2 = "through the INITIAL ball.";
                    break;
                case THIRD_BALL:
                    comment = "Figure completed! Release the finger";
                    comment2 = "and both balls will vanish.";
                    break;
                /*case SHAPE_COMPLETE:
                    comment = "In the next level, you will face with";
                    comment2 = "a more challenging figure: a triangle.";
                    comment3 = "Tap to continue.";*/
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
