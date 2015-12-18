package com.scop.org.minesweeper.elements;

/**
 * Created by Oscar on 27/11/2015.
 */
public class Tile {
    public static final int BITMAP_SIZE = 96;

    public static final byte UNDISCOVERED = 0;
    public static final byte NEAR1 = 1;
    public static final byte NEAR2 = 2;
    public static final byte NEAR3 = 3;
    public static final byte NEAR4 = 4;
    public static final byte NEAR5 = 5;
    public static final byte NEAR6 = 6;
    public static final byte NEAR7 = 7;
    public static final byte NEAR8 = 8;
    public static final byte EMPTY = 9;
    public static final byte FLAGGED = 10;
    public static final byte BOMB = 11;
    private int status;

    public Tile(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
