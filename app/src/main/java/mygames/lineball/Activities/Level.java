/*
 * Copyright (c) 2015. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package mygames.lineball.Activities;


public abstract class Level {

    protected Level_State current_state;
    protected Level_State end_state;

    public Level() {
        current_state = Level_State.INITIAL;
        end_state = Level_State.NEXT_LEVEL;
    }



    public enum Level_State {
        INITIAL,
        FIRST_BALL,
        SECOND_BALL,
        THIRD_BALL,
        SHAPE_COMPLETE,
        NEXT_LEVEL,
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

    public boolean isLineContactState() {
        return current_state.equals(Level_State.LINE_CONTACT);
    }

    public boolean allBallsSelected() {
        return current_state.equals(Level_State.values()[end_state.ordinal() - 1]);
    }

    public boolean isNotAllBallsShapeState() {
        return current_state.equals(Level_State.NOT_ALL_BALLS_SHAPE);
    }

    abstract String[] getComments();

    abstract Level nextLevel();
}
