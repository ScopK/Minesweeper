package com.scop.org.minesweeper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import com.scop.org.minesweeper.elements.Grid;
import com.scop.org.minesweeper.elements.GridControl;
import com.scop.org.minesweeper.elements.TileStyle;

/**
 * Created by Oscar on 25/11/2015.
 */
public class GamePanel extends View{

    private GridControl gridControl;

    public GamePanel(Context context) {
        super(context);

        //make gamePanel focusable so it can handle events
        setFocusable(true);

        // init grid:
        Bitmap marks = BitmapFactory.decodeResource(getResources(), R.drawable.tilemarks_a);
        Bitmap tiles = BitmapFactory.decodeResource(getResources(), R.drawable.tiles_a);
        TileStyle.getInstance().setStyle(tiles,4,marks,-140f,1.1f);
        gridControl = new GridControl(this,context);
        restart();
        invalidate();


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gridControl.onTouchEvent(event);
        return true;//super.onTouchEvent(event);
    }

    public void update() {
        gridControl.update();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        gridControl.setDimensions(getWidth(), getHeight());

        if (canvas!=null){
            canvas.drawRGB(102, 119, 136);
            //canvas.drawRGB(60, 60, 60);
            gridControl.draw(canvas);
        }
    }

    public void restart() {
        gridControl.end();
        gridControl.start(new Grid(25,25,100));
    }
}
