package com.scop.org.minesweeper.elements;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Oscar on 27/11/2015.
 */
public class Tile {
    public static final int BITMAP_SIZE = 96;

    public static final byte UNDISCOVERED = -1;
    public static final byte NEAR1 = 0;
    public static final byte NEAR2 = 1;
    public static final byte NEAR3 = 2;
    public static final byte NEAR4 = 3;
    public static final byte NEAR5 = 4;
    public static final byte NEAR6 = 5;
    public static final byte NEAR7 = 6;
    public static final byte NEAR8 = 7;
    public static final byte EMPTY = 8;
    public static final byte FLAGGED = 9;
    public static final byte BOMB = 10;
    private int status;

    private Bitmap baseBitmap,base;
    private Paint paint,colored;
    private int x,y;

    public Tile(int status, int x, int y){
        this.x = x;
        this.y = y;
        baseBitmap = TileStyle.getInstance().getBitmap(UNDISCOVERED);
        paint = TileStyle.getInstance().getPaint();
        colored = TileStyle.getInstance().getColoredPaint();
        setStatus(status);
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
        if (status != UNDISCOVERED) {
            base = TileStyle.getInstance().getBitmap(status);
        } else {
            base = null;
        }
    }

    public void draw(Canvas canvas, int parentX, int parentY){
        if (status==FLAGGED || status==UNDISCOVERED){
            canvas.drawBitmap(baseBitmap,parentX+x,parentY+y, colored);
        }
        if (base!=null){
            canvas.drawBitmap(base,parentX+x,parentY+y, paint);
        }
    }
}
