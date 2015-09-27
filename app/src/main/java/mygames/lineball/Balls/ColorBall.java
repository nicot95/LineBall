package mygames.lineball.Balls;

import android.graphics.Color;

/**
 * Created by nico on 27/09/15.
 */
public class ColorBall {

    private static int red = Color.argb(255, 255, 50, 50);
    private static int lightRed = Color.argb(255, 255,90,90);

    private static int blue = Color.argb(255, 50, 50, 255);
    private static int lightBlue = Color.argb(255, 90,90,255);

    private static int green = Color.argb(255, 0,154,0);
    private static int lightGreen = Color.argb(255, 90,173,90);

    private static int yellow = Color.argb(255, 255, 255, 50);
    private static int lightYellow = Color.argb(255,255,255,102);

    public static int getRed() {
        return red;
    }

    public static int getLightRed() {
        return lightRed;
    }

    public static int getBlue() {
        return blue;
    }

    public static int getLightBlue() {
        return lightBlue;
    }

    public static int getGreen() {
        return green;
    }

    public static int getLightGreen() {
        return lightGreen;
    }

    public static int getYellow() {
        return yellow;
    }

    public static int getLightYellow() {
        return lightYellow;
    }

}
