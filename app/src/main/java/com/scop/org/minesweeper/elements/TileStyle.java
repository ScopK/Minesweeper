package com.scop.org.minesweeper.elements;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.scop.org.minesweeper.R;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by Oscar on 27/11/2015.
 */
public class TileStyle {
    private static TileStyle tilestyle;
    private Bitmap[] bms,defBms;
    private HashMap<Integer,Integer> seeds;
    private Random random;

    private TileStyle(){
        random = new Random();
        seeds = new HashMap();
    }
    public static TileStyle getInstance(){
        if (tilestyle==null){
            tilestyle = new TileStyle();
        }
        return tilestyle;
    }

    public void setStyle(Resources src){
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
    }

    public Bitmap getBitmap(int i){
        if (i==Tile.UNDISCOVERED){
            int idx = new java.util.Random().nextInt(defBms.length);
            return defBms[idx];
        }
        return bms[i];
    }

    public Bitmap getBitmap(int i, int seed){
        if (i==Tile.UNDISCOVERED){
            if (!seeds.containsKey(seed)){
                seeds.put(seed,random.nextInt(defBms.length));
            }
            return defBms[seeds.get(seed)];
        }
        return bms[i];
    }
}
