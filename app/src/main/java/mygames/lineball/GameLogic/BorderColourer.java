/*
 * Copyright (c) 2015. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package mygames.lineball.GameLogic;

import android.graphics.Color;


/*
    THis class is used to record the colour of the borders of the screen. Whenever a ball
    hits a border, it will change its colour to match that of the ball
 */
public class BorderColourer {

    private int northBorderColour;
    private int westBorderColour;
    private int southBorderColour;
    private int eastBorderColour;

    public BorderColourer() {
        this.setNorthBorderColour(Color.WHITE);
        this.setWestBorderColour(Color.WHITE);
        this.setSouthBorderColour(Color.WHITE);
        this.setEastBorderColour(Color.WHITE);
    }


    public int getNorthBorderColour() {
        return northBorderColour;
    }

    public void setNorthBorderColour(int northBorderColour) {
        this.northBorderColour = northBorderColour;
    }

    public int getWestBorderColour() {
        return westBorderColour;
    }

    public void setWestBorderColour(int westBorderColour) {
        this.westBorderColour = westBorderColour;
    }

    public int getSouthBorderColour() {
        return southBorderColour;
    }

    public void setSouthBorderColour(int southBorderColour) {
        this.southBorderColour = southBorderColour;
    }

    public int getEastBorderColour() {
        return eastBorderColour;
    }

    public void setEastBorderColour(int eastBorderColour) {
        this.eastBorderColour = eastBorderColour;
    }
}
