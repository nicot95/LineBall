/*
 * Copyright (c) 2015. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package mygames.lineball.Balls;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class RandomBall extends Ball {


    private int rotation = 0;
    private int ticks_left_with_current_color;
    private int current_color;

    public RandomBall(int screenX, int screenY) {
        super(screenX, screenY, 4);
        this.ticks_left_with_current_color = 0;
    }

    @Override
    public int getColor() {
        int FRAMES_PER_COLOR = 3;
        if (ticks_left_with_current_color == 0) {
            int ballColor = gen.nextInt(4);
            switch (ballColor) {
                case 0: current_color = ColorBall.getRed();
                    break;
                case 1: current_color = ColorBall.getBlue();
                    break;
                case 2: current_color = ColorBall.getYellow();
                    break;
                case 3: current_color = ColorBall.getGreen();
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
        RectF rect = new RectF(x - ballRadius, y - ballRadius, x + ballRadius, y + ballRadius);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(ColorBall.getGreen());
        canvas.drawArc(rect, rotation, 90, true, paint);
        paint.setColor(ColorBall.getRed());
        canvas.drawArc(rect, 90 + rotation, 90, true, paint);
        paint.setColor(ColorBall.getYellow());
        canvas.drawArc(rect, 180 + rotation, 90, true, paint);
        paint.setColor(ColorBall.getBlue());
        canvas.drawArc(rect, 270 + rotation, 90, true, paint);

        rotation += 5;
        if (rotation > 360) {
            rotation = rotation%360;
        }


    }
}
