package com.scop.org.minesweeper.elements;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;

import com.scop.org.minesweeper.elements.visual.ColorFilterHue;

import java.util.Random;

/**
 * Created by Oscar on 27/11/2015.
 */
public class TileStyle {
    private static TileStyle tilestyle;
    private Bitmap[] bms,defBms;
    private Paint paint;
    private Random random;
    private int backgroundColor;

    private TileStyle(){
        random = new Random();

        paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        //paint.setAntiAlias(true);
        //paint.setFilterBitmap(true);
        //paint.setDither(true);
    }
    public static TileStyle getInstance(){
        if (tilestyle==null){
            tilestyle = new TileStyle();
        }
        return tilestyle;
    }

    public void setStyle(Context context, String setName, int numUndiscoveredTiles, float color, float brightness, int bgColor){
        this.backgroundColor = bgColor;
        int id;
        id = context.getResources().getIdentifier("tilemarks_"+setName, "drawable", context.getPackageName());
        Bitmap marks = BitmapFactory.decodeResource(context.getResources(), id);

        id = context.getResources().getIdentifier("tiles_"+setName, "drawable", context.getPackageName());
        Bitmap tiles = BitmapFactory.decodeResource(context.getResources(), id);

        this.setStyle(tiles,numUndiscoveredTiles,marks,color,brightness);
    }

    private void setStyle(Bitmap tiles, int numUndiscoveredTiles, Bitmap marks, float color, float brightness) {
        int markSize = marks.getHeight();
        bms = new Bitmap[13];
        bms[0] = Bitmap.createBitmap(marks, 0         , 0, markSize, markSize);
        bms[1] = Bitmap.createBitmap(marks,   markSize, 0, markSize, markSize);
        bms[2] = Bitmap.createBitmap(marks, 2*markSize, 0, markSize, markSize);
        bms[3] = Bitmap.createBitmap(marks, 3*markSize, 0, markSize, markSize);
        bms[4] = Bitmap.createBitmap(marks, 4*markSize, 0, markSize, markSize);
        bms[5] = Bitmap.createBitmap(marks, 5*markSize, 0, markSize, markSize);
        bms[6] = Bitmap.createBitmap(marks, 6*markSize, 0, markSize, markSize);
        bms[7] = Bitmap.createBitmap(marks, 7*markSize, 0, markSize, markSize);
        bms[8] = Bitmap.createBitmap(marks, 8*markSize, 0, markSize, markSize);
        bms[9] = Bitmap.createBitmap(marks, 9*markSize, 0, markSize, markSize);
        bms[10]= Bitmap.createBitmap(marks,10*markSize, 0, markSize, markSize);
        bms[11]= Bitmap.createBitmap(marks,11*markSize, 0, markSize, markSize);
        bms[12]= Bitmap.createBitmap(marks,12*markSize, 0, markSize, markSize);

        int tileH = tiles.getHeight();
        int tileW = tiles.getWidth()/numUndiscoveredTiles;
        defBms = new Bitmap[numUndiscoveredTiles];
        for (int i=0;i<numUndiscoveredTiles;i++){
            defBms[i] = Bitmap.createBitmap(tiles, i*tileW, 0, tileW, tileH);
            ColorFilterHue.adjustHue(defBms[i], color, brightness);
        }
        //paintColored.setColorFilter(ColorFilterHue.adjustHue(radius, brightness));
    }

    public Bitmap getBitmap(int i){
        if (i==Tile.UNDISCOVERED){
            int idx = random.nextInt(defBms.length);
            return defBms[idx];
        }
        return bms[i];
    }

    public Paint getPaint(){
        return this.paint;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }
}
