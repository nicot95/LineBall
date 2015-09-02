package mygames.lineball;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created by Daniel on 01/09/2015.
 */
public class RandomBall extends Ball {

    private int FRAMES_PER_COLOR = 3;
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
            ticks_left_with_current_color = FRAMES_PER_COLOR;
        } else {
            ticks_left_with_current_color--;
        }
        return current_color;
    }

    @Override
    public void draw(Paint paint, Canvas canvas) {
        RectF rect = new RectF(x - ballRadius, y + ballRadius, x + ballRadius, y - ballRadius);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GREEN);
        canvas.drawArc(rect, 0, 90, true, paint);
        paint.setColor(Color.RED);
        canvas.drawArc(rect, 90, 180, true, paint);
        paint.setColor(Color.YELLOW);
        canvas.drawArc(rect, 180, 270, true, paint);
        paint.setColor(Color.BLUE);
        canvas.drawArc(rect, 270, 360, true, paint);


    }
}
