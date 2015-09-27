package mygames.lineball.Balls;

import android.graphics.Color;

/**
 * Created by nico on 27/09/15.
 */
public class ColorBall {

    private static int red = Color.argb(255, 255, 50, 50);

    private static int blue = Color.argb(255, 50, 50, 255);

    private static int green = Color.argb(255, 50,153,50);

    private static int yellow = Color.argb(255, 255, 255, 50);

    public static int getYellow() {
        return yellow;
    }

    public void setYellow(int yellow) {
        this.yellow = yellow;
    }

    public static int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }



    public static int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public static int getBlue() {
        return blue;
    }
}
