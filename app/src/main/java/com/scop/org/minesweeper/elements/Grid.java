package com.scop.org.minesweeper.elements;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Oscar on 27/11/2015.
 */
public class Grid {

    private Bitmap[] bitmaps;
    private Paint paint=null;
    private Tile[][] tiles;

    public Grid(int w, int h){
        tiles = new Tile[w][h];

        for (int i=0;i<w;i++) {
            for (int j = 0; j < h; j++) {
                tiles[i][j] = new Tile((i+j)%12);
            }
        }

        //paint = new Paint(Color.RED);
        //ColorFilter filter = new PorterDuffColorFilter(0xFFFF0000, PorterDuff.Mode.SCREEN);
        //paint.setColorFilter(filter);
    }

    public void update(){
    }

    public void draw(Canvas canvas){
        canvas.drawARGB(255,60,60,60);

        int tileSize = Tile.BITMAP_SIZE;
        TileStyle ts = TileStyle.getInstance();

        for (int i=0;i<tiles.length;i++){
            for (int j=0;j<tiles[0].length;j++){
                Tile t = tiles[i][j];
                canvas.drawBitmap(ts.getBitmap(t.getStatus()), tileSize*i, tileSize*j, paint);
            }
        }
    }
}
