package mygames.lineball;

import android.graphics.Color;

/**
 * Created by Daniel on 01/09/2015.
 */
public class RandomBall extends Ball {

    private int FRAMES_PER_COLOR = 10;
    private int ticks_left_with_current_color;
    private int current_color;

    public RandomBall(int screenX, int screenY) {
        super(screenX, screenY);
        this.color = Ball.RANDOM_COLOR;
        this.ticks_left_with_current_color = 0;
    }

    @Override
    public int getColor() {
        if (ticks_left_with_current_color == 0) {
            int ballColor = gen.nextInt(4);
            switch (ballColor) {
                case 0: current_color = Color.RED;
                    break;
                case 1: current_color = Color.YELLOW;
                    break;
                case 2: current_color = Color.GREEN;
                    break;
                case 3: current_color = Color.BLUE;
                    break;
            }
        }
        return current_color;
    }
}
