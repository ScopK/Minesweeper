package com.scop.org.minesweeper.elements;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.scop.org.minesweeper.elements.visual.ColorFilterHue;

/**
 * Created by Oscar on 27/11/2015.
 */
public class Grid {

    private Paint paint=null;
    private Paint paintColored=null;
    private Tile[][] tiles;
    private int border,X,Y;

    public Grid(int w, int h){
        this.border = 50;
        this.X=0;
        this.Y=0;

        tiles = new Tile[w][h];

        for (int i=0;i<w;i++) {
            for (int j = 0; j < h; j++) {
                tiles[i][j] = new Tile((i+j)%12-1);
            }
        }

        paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        //paint.setAntiAlias(true);
        //paint.setFilterBitmap(true);
        //paint.setDither(true);

        paintColored  = new Paint(Paint.FILTER_BITMAP_FLAG);
        paintColored.setColorFilter(ColorFilterHue.adjustHue(180f,1.0f));
    }

    public void update(){
    }

    public void draw(Canvas canvas){
        int tileSize = Tile.BITMAP_SIZE;
        int maxWidth = canvas.getWidth();
        int maxHeight = canvas.getHeight();

        int posX,posY;

        for (int i=0;i<tiles.length;i++){
            for (int j=0;j<tiles[0].length;j++){
                posX = X+border + tileSize*i;
                posY = Y+border + tileSize*j;

                if (posX<-tileSize || posY<-tileSize) continue;
                //if (posX>maxWidth || posY>maxHeight) continue;

                Tile t = tiles[i][j];
                int status = t.getStatus();
                if (status == Tile.FLAGGED){
                    drawTile(canvas, Tile.UNDISCOVERED, posX, posY, (i+1)*100+j);
                }
                drawTile(canvas, status, posX, posY, (i+1)*100+j);
            }
        }
    }

    public void drawTile(Canvas canvas, int status, int posX, int posY, int seed){
        TileStyle ts = TileStyle.getInstance();

        if (status==Tile.UNDISCOVERED) {
            canvas.drawBitmap(ts.getBitmap(status,seed), posX, posY, paintColored);
        } else {
            canvas.drawBitmap(ts.getBitmap(status), posX, posY, paint);
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
