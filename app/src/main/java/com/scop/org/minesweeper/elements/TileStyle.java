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
    private Bitmap[][] bms_ext;
    private Paint paint;
    private Random random;
    private int backgroundColor;
    private boolean skinExt = true;


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

        int id = context.getResources().getIdentifier("tiles_"+setName, "drawable", context.getPackageName());
        Bitmap tiles = BitmapFactory.decodeResource(context.getResources(), id);

        int tileH = tiles.getHeight();
        int tileW = tiles.getWidth()/numUndiscoveredTiles;
        defBms = new Bitmap[numUndiscoveredTiles];
        for (int i=0;i<numUndiscoveredTiles;i++){
            defBms[i] = Bitmap.createBitmap(tiles, i*tileW, 0, tileW, tileH);
            ColorFilterHue.adjustHue(defBms[i], color, brightness);
        }

        bms_ext = new Bitmap[8][];

        boolean skinExt = true;

        for (int i=0;i<8;i++){
            int num = i+1;
            bms_ext[i] = new Bitmap[num+2];

            for (int j=0;j<num+2;j++){
                id = context.getResources().getIdentifier("tile_skin_"+setName+"_"+num+"_"+j, "drawable", context.getPackageName());
                if (id == 0){
                    skinExt = false;
                }
                bms_ext[i][j] = BitmapFactory.decodeResource(context.getResources(), id);
            }
        }
        this.skinExt = skinExt;

        bms = new Bitmap[5];
        bms[0] = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("tile_skin_"+setName+"_bomb", "drawable", context.getPackageName()));
        bms[1] = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("tile_skin_"+setName+"_bomb_end", "drawable", context.getPackageName()));
        bms[2] = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("tile_skin_"+setName+"_flag", "drawable", context.getPackageName()));
        bms[3] = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("tile_skin_"+setName+"_flag_fail", "drawable", context.getPackageName()));
        bms[4] = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier("tile_skin_"+setName+"_empty", "drawable", context.getPackageName()));
    }

    public Bitmap getBitmap(int i){
        return getBitmap(i,-1);
    }
    public Bitmap getBitmap(int i, int fl){
        if (i==Tile.UNDISCOVERED){
            int idx = random.nextInt(defBms.length);
            return defBms[idx];
        }
        if (i >= Tile.NEAR1 && i <= Tile.NEAR8){
            i -= Tile.NEAR1;
            if (fl < 0){
                fl = i+1;
            } else if(fl>i+1){
                fl = i+2;
            }

            return skinExt? bms_ext[i][fl] : bms_ext[i][i+1];
        } else {
            return bms[i-Tile.NEAR8-1];
        }
    }

    public Paint getPaint(){
        return this.paint;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }
}
