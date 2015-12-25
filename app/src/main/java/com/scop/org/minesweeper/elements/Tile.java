package com.scop.org.minesweeper.elements;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.Serializable;

/**
 * Created by Oscar on 27/11/2015.
 */
public class Tile implements Serializable {
    public static final int BITMAP_SIZE = 95;

    public static final byte UNDISCOVERED = -1;
    public static final byte NEAR1 = 0;
    public static final byte NEAR2 = 1;
    public static final byte NEAR3 = 2;
    public static final byte NEAR4 = 3;
    public static final byte NEAR5 = 4;
    public static final byte NEAR6 = 5;
    public static final byte NEAR7 = 6;
    public static final byte NEAR8 = 7;
    public static final byte BOMB = 8;
    public static final byte FLAGGED = 9;
    public static final byte EMPTY = 10;
    private int status;
    private boolean hasBomb;
    public int bombsNear = 0;

    transient private Bitmap baseBitmap,base;
    transient private Paint paint;
    private float x,y;

    public Tile(int status, float x, float y){
        this.x = x;
        this.y = y;
        bombsNear = 0;
        baseBitmap = TileStyle.getInstance().getBitmap(UNDISCOVERED);
        paint = TileStyle.getInstance().getPaint();
        setStatus(status);
    }

    public int getStatus() {
        return this.status;
    }
    public boolean isStatus(int status){
        return status==this.status;
    }
    public void setStatus(int status) {
        this.status = status;
        if (status == UNDISCOVERED) {
            base = null;
        } else {
            base = TileStyle.getInstance().getBitmap(status);
        }
    }

    public void plantBomb(){
        this.hasBomb = true;
    }
    public boolean hasBomb(){
        return this.hasBomb;
    }
    public void hasBombNear() {
        bombsNear++;
    }
    public int getBombsNear() {
        return bombsNear;
    }

    public boolean reveal(){
        if (status==FLAGGED || status==UNDISCOVERED){
            if (hasBomb){
                setStatus(BOMB);
                return false;
            }
        }
        return true;
    }

    public void draw(Canvas canvas, float parentX, float parentY){
        if (status==FLAGGED || status==UNDISCOVERED){
            canvas.drawBitmap(baseBitmap,parentX+x,parentY+y, paint);
        }
        if (base!=null){
            canvas.drawBitmap(base,parentX+x,parentY+y, paint);
        }
    }

    public void loadGraphics() {
        baseBitmap = TileStyle.getInstance().getBitmap(UNDISCOVERED);
        setStatus(status);
        paint = TileStyle.getInstance().getPaint();
    }
}
