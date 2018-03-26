package com.scop.org.minesweeper.elements;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.Serializable;

/**
 * Created by Oscar on 27/11/2015.
 */
public class Tile implements Serializable {
    public static final int BITMAP_SIZE = 127;

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
    public static final byte BOMB_END = 9;
    public static final byte FLAGGED = 10;
    public static final byte FLAGGED_FAILED = 11;
    public static final byte EMPTY = 12;
    private int status;
    private boolean hasBomb;
    private int bombsNear = 0;
    private int flaggedNear = 0;

    transient private Bitmap baseBitmap,base;
    transient private Paint paint;
    private float dx,dy;
    private int x,y;

    public Tile(int status, int x, int y, float dx, float dy){
        this.dx = dx;
        this.dy = dy;
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
            base = TileStyle.getInstance().getBitmap(status,flaggedNear);
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

    public void addFlaggedNear(int i){
        this.flaggedNear += i;
        if (this.status >= NEAR1 && this.status <= NEAR8)
            base = TileStyle.getInstance().getBitmap(status,flaggedNear);
    }

    public boolean reveal(){
        if (status==FLAGGED || status==UNDISCOVERED){
            if (hasBomb){
                setStatus(BOMB_END);
                return false;
            }
        }
        return true;
    }

    public void draw(Canvas canvas, float parentX, float parentY){
        if (status==FLAGGED || status==UNDISCOVERED || status==FLAGGED_FAILED){
            canvas.drawBitmap(baseBitmap,parentX+dx,parentY+dy, paint);
        }
        if (base!=null){
            canvas.drawBitmap(base,parentX+dx,parentY+dy, paint);
        }
    }

    public void loadGraphics() {
        baseBitmap = TileStyle.getInstance().getBitmap(UNDISCOVERED,flaggedNear);
        setStatus(status);
        paint = TileStyle.getInstance().getPaint();
    }

    public int[] getCoords(){
        return new int[]{x,y};
    }
}
