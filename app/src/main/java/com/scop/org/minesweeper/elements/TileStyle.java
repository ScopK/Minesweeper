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


        Bitmap tiles = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("tiles_"+setName, "drawable", context.getPackageName()));

        int tileH = tiles.getHeight();
        int tileW = tiles.getWidth()/numUndiscoveredTiles;
        defBms = new Bitmap[numUndiscoveredTiles];
        for (int i=0;i<numUndiscoveredTiles;i++){
            defBms[i] = Bitmap.createBitmap(tiles, i*tileW, 0, tileW, tileH);
            ColorFilterHue.adjustHue(defBms[i], color, brightness);
        }


        bms = new Bitmap[13];
        bms[0] = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("tile_skin_"+setName+"_1_1", "drawable", context.getPackageName()));
        bms[1] = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("tile_skin_"+setName+"_2_2", "drawable", context.getPackageName()));
        bms[2] = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("tile_skin_"+setName+"_3_3", "drawable", context.getPackageName()));
        bms[3] = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("tile_skin_"+setName+"_4_4", "drawable", context.getPackageName()));
        bms[4] = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("tile_skin_"+setName+"_5_5", "drawable", context.getPackageName()));
        bms[5] = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("tile_skin_"+setName+"_6_6", "drawable", context.getPackageName()));
        bms[6] = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("tile_skin_"+setName+"_7_7", "drawable", context.getPackageName()));
        bms[7] = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("tile_skin_"+setName+"_8_8", "drawable", context.getPackageName()));
        bms[8] = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("tile_skin_"+setName+"_bomb", "drawable", context.getPackageName()));
        bms[9] = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("tile_skin_"+setName+"_bomb_end", "drawable", context.getPackageName()));
        bms[10]= BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("tile_skin_"+setName+"_flag", "drawable", context.getPackageName()));
        bms[11]= BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("tile_skin_"+setName+"_flag_fail", "drawable", context.getPackageName()));
        bms[12]= BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("tile_skin_"+setName+"_empty", "drawable", context.getPackageName()));
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
