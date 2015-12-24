package com.scop.org.minesweeper.elements;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.scop.org.minesweeper.GamePanel;
import com.scop.org.minesweeper.elements.visual.ColorFilterHue;

/**
 * Created by Oscar on 27/11/2015.
 */
public class Grid {
    private Tile[][] tiles;
    public float x,y,w,h;

    public Grid(int w, int h){
        this.x=0;
        this.y=0;
        this.w=w;
        this.h=h;

        int tileSize = Tile.BITMAP_SIZE;
        tiles = new Tile[w][h];
        for (int i=0;i<w;i++) {
            for (int j = 0; j < h; j++) {
                tiles[i][j] = new Tile(Tile.UNDISCOVERED,i*tileSize,j*tileSize);
            }
        }
    }

    private Tile getTile(float x, float y){
        int dX = (int) ((-this.x+x)/Tile.BITMAP_SIZE);
        int dY = (int) ((-this.y+y)/Tile.BITMAP_SIZE);
        return tiles[dX][dY];
    }

    // ACTIONS_
    public void sTap(float[] coords){
        getTile(coords[0], coords[1]).setStatus(Tile.FLAGGED);
    }

    public void dTap(float[] coords){
        getTile(coords[0], coords[1]).setStatus(Tile.EMPTY);
    }

    // UPDATE & DRAW_
    public void update(){}

    public void draw(Canvas canvas){
        for (int i=0;i<tiles.length;i++){
            for (int j=0;j<tiles[0].length;j++){
                tiles[i][j].draw(canvas,x,y);
            }
        }
    }
}
