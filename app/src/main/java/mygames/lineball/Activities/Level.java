package mygames.lineball.Activities;

/**
 * Created by nico on 09/09/15.
 */
public abstract class Level {

    protected Level_State current_state;
    protected Level_State end_state;

    public Level() {
        current_state = Level_State.INITIAL;
        end_state = Level_State.NEXT_LEVEL2;
    }



    public enum Level_State {
        INITIAL,
        FIRST_BALL,
        SECOND_BALL,
        THIRD_BALL,
        SHAPE_COMPLETE,
        NEXT_LEVEL,
        NEXT_LEVEL2,
        NOT_ALL_BALLS_SHAPE,
        LINE_CONTACT;

        public Level_State getNext() {
            return values()[(ordinal()+1) % values().length];
        }
    }

    protected void nextState() {
        if(current_state != end_state) {
            current_state = current_state.getNext();
        }
    }

    protected Level_State getCurrent_state() {
        return current_state;
    }

    public void setInitialState() {
        current_state = Level_State.INITIAL;
    }

    public void setToLineContactState() {
        current_state = Level_State.LINE_CONTACT;
    }

    public void setNotAllBallsState() {
        current_state = Level_State.NOT_ALL_BALLS_SHAPE;
    }

    public boolean isEndState() {
        return current_state == end_state;
    }

    public boolean isStateBeforeEndState() {
        // returns true if current state is the state before end state
        return current_state.equals(Level_State.values()[end_state.ordinal() - 1]);
    }

    public boolean isLineContactState() {
        return current_state.equals(Level_State.LINE_CONTACT);
    }

    public boolean allBallsSelected() {
        return current_state.equals(Level_State.values()[end_state.ordinal() - 2]);
    }

    public boolean isNotAllBallsShapeState() {
        return current_state.equals(Level_State.NOT_ALL_BALLS_SHAPE);
    }

    abstract String[] getComments();

    abstract Level nextLevel();
}
