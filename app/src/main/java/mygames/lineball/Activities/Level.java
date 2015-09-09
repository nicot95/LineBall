package mygames.lineball.Activities;

/**
 * Created by nico on 09/09/15.
 */
public abstract class Level {

    protected Level_State current_state;
    protected Level_State end_state;

    public Level() {
        current_state = Level_State.INITIAL;
        end_state = Level_State.SHAPE_COMPLETE;
    }

    public enum Level_State {
        INITIAL,
        FIRST_BALL,
        SECOND_BALL,
        THIRD_BALL,
        SHAPE_COMPLETE;

        public Level_State getNext() {
            return values()[(ordinal()+1) % values().length];
        }
    }

    protected void nextState() {
        if(current_state != end_state) {
            current_state = current_state.getNext();
        }
    }

    public void setInitialState() {
        current_state = Level_State.INITIAL;
    }

    public boolean isEndState() {
        return current_state == end_state;
    }

    abstract String[] getComments();

    abstract Level nextLevel();
}
