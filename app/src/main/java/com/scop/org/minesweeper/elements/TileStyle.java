package com.scop.org.minesweeper.elements;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;

import com.scop.org.minesweeper.R;
import com.scop.org.minesweeper.elements.visual.ColorFilterHue;

import java.util.Random;

/**
 * Created by Oscar on 27/11/2015.
 */
public class TileStyle {
    private static TileStyle tilestyle;
    private Bitmap[] bms,defBms;
    private Paint paint,paintColored;
    private Random random;

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

    public void setStyle(Resources src, float radius, float brightness){
        bms = new Bitmap[11];
        bms[0] = BitmapFactory.decodeResource(src,R.drawable.tile1);
        bms[1] = BitmapFactory.decodeResource(src,R.drawable.tile2);
        bms[2] = BitmapFactory.decodeResource(src,R.drawable.tile3);
        bms[3] = BitmapFactory.decodeResource(src,R.drawable.tile4);
        bms[4] = BitmapFactory.decodeResource(src,R.drawable.tile5);
        bms[5] = BitmapFactory.decodeResource(src,R.drawable.tile6);
        bms[6] = BitmapFactory.decodeResource(src,R.drawable.tile7);
        bms[7] = BitmapFactory.decodeResource(src,R.drawable.tile8);
        bms[8] = BitmapFactory.decodeResource(src,R.drawable.tilewhite);
        bms[9] = BitmapFactory.decodeResource(src,R.drawable.tilemark);
        bms[10] = BitmapFactory.decodeResource(src,R.drawable.tilebomb);

        defBms = new Bitmap[4];
        defBms[0] = BitmapFactory.decodeResource(src,R.drawable.tile_a);
        defBms[1] = BitmapFactory.decodeResource(src,R.drawable.tile_b);
        defBms[2] = BitmapFactory.decodeResource(src,R.drawable.tile_c);
        defBms[3] = BitmapFactory.decodeResource(src,R.drawable.tile_d);

        paintColored  = new Paint(Paint.FILTER_BITMAP_FLAG);
        paintColored.setColorFilter(ColorFilterHue.adjustHue(radius, brightness));
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

    public Paint getColoredPaint(){
        return this.paintColored;
    }
}
