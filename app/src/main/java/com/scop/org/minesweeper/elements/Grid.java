package com.scop.org.minesweeper.elements;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.scop.org.minesweeper.elements.visual.ColorFilterHue;

/**
 * Created by Oscar on 27/11/2015.
 */
public class Grid {
    private Tile[][] tiles;
    private int border,X,Y;

    public Grid(int w, int h){
        this.border = 50;
        this.X=0;
        this.Y=0;

        int tileSize = Tile.BITMAP_SIZE;

        tiles = new Tile[w][h];

        for (int i=0;i<w;i++) {
            for (int j = 0; j < h; j++) {
                tiles[i][j] = new Tile((i+j)%12-1,i*tileSize,j*tileSize);
            }
        }
    }

    public void update(){
    }

    public void draw(Canvas canvas){
        for (int i=0;i<tiles.length;i++){
            for (int j=0;j<tiles[0].length;j++){
                tiles[i][j].draw(canvas,X,Y);
            }
        }
    }

    public void move(float x, float y){
        move((int)x,(int)y);
    }
    public void move(int x, int y){
        this.X += x;
        this.Y += y;
    }
}
