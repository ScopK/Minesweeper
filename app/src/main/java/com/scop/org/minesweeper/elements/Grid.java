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

    public Grid(int w, int h){
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
        //canvas.drawARGB(255,102,119,135); //Original
        canvas.drawARGB(255,60,60,60);

        int tileSize = Tile.BITMAP_SIZE;
        TileStyle ts = TileStyle.getInstance();

        for (int i=0;i<tiles.length;i++){
            for (int j=0;j<tiles[0].length;j++){
                Tile t = tiles[i][j];
                int status = t.getStatus();
                if (status == Tile.FLAGGED){
                    canvas.drawBitmap(ts.getBitmap(Tile.UNDISCOVERED,(j+1)*100*(i+1)), tileSize*i, tileSize*j, paintColored);
                }
                if (status==Tile.UNDISCOVERED) {
                    canvas.drawBitmap(ts.getBitmap(status,(j+1)*100*(i+1)), tileSize * i, tileSize * j, paintColored);
                } else {
                    canvas.drawBitmap(ts.getBitmap(status), tileSize * i, tileSize * j, paint);
                }
            }
        }
    }
}
