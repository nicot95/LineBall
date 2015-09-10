package mygames.lineball.Activities;

/**
 * Created by nico on 10/09/15.
 */
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
                comment =  "Very well, now hold the finger and select";
                comment2 = "another ball. Again remember: other";
                comment3 = "balls must not hit the line!";
                break;
            case SECOND_BALL:
                comment = "Well done! Now, keep holding the finger";
                comment2 = "and select the last moving ball.";
                break;
            case THIRD_BALL:
                comment = "Perfect, now just select the first ball";
                comment2 = "again to finish the triangle";
                break;
            case SHAPE_COMPLETE:
                comment = "Shape completed! Release the finger.";
                comment2 = "As you can see, multicolor balls are very";
                comment3 = "useful, so use them wisely";
                break;
            case NEXT_LEVEL:
                comment = "Use multicolor balls wisely as they";
                comment2 = "will be scarce during the game.";
                comment3 = "Tap to go to menu and start playing :)";

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
