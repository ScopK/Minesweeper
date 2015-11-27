package com.scop.org.minesweeper.elements;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.scop.org.minesweeper.R;

/**
 * Created by Oscar on 27/11/2015.
 */
public class TileStyle {
    private static TileStyle tilestyle;
    private Bitmap[] bms;
    private TileStyle(){

    }
    public static TileStyle getInstance(){
        if (tilestyle==null){
            tilestyle = new TileStyle();
        }
        return tilestyle;
    }

    public void setStyle(Resources src){
        bms = new Bitmap[12];
        bms[0] = BitmapFactory.decodeResource(src, R.drawable.tile);
        bms[1] = BitmapFactory.decodeResource(src,R.drawable.tile1);
        bms[2] = BitmapFactory.decodeResource(src,R.drawable.tile2);
        bms[3] = BitmapFactory.decodeResource(src,R.drawable.tile3);
        bms[4] = BitmapFactory.decodeResource(src,R.drawable.tile4);
        bms[5] = BitmapFactory.decodeResource(src,R.drawable.tile5);
        bms[6] = BitmapFactory.decodeResource(src,R.drawable.tile6);
        bms[7] = BitmapFactory.decodeResource(src,R.drawable.tile7);
        bms[8] = BitmapFactory.decodeResource(src,R.drawable.tile8);
        bms[9] = BitmapFactory.decodeResource(src,R.drawable.tilewhite);
        bms[10] = BitmapFactory.decodeResource(src,R.drawable.tilemark);
        bms[11] = BitmapFactory.decodeResource(src,R.drawable.tilebomb);
    }

    public Bitmap getBitmap(int i){
        return bms[i];
    }
}
