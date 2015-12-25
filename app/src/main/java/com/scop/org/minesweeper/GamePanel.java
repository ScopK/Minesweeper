package com.scop.org.minesweeper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Parcelable;
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
        gridControl = new GridControl(this,context);
        restart();
        invalidate();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        switch(visibility){
            case GONE:      // minimizing & closing
                //saveState();
                break;
            case INVISIBLE: // resuming
                break;
            case VISIBLE:   // Start and resuming
                break;
        }
        super.onWindowVisibilityChanged(visibility);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        gridControl.setDimensions(w, h);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gridControl.onTouchEvent(event);
        return true;//super.onTouchEvent(event);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (canvas!=null){
            canvas.drawColor(TileStyle.getInstance().getBackgroundColor());
            //canvas.drawRGB(60, 60, 60);
            gridControl.draw(canvas);
        }
    }

    public void restart() {
        gridControl.end();
        gridControl.start(new Grid(50, 50, 503));
    }

    public void saveState(){
        System.err.println("Go SAVE");
        gridControl.savingState();
        System.err.println("SAVED");
    }

    public void loadState(){
        gridControl.loadingState();
        System.err.println("LOADED");
        invalidate();
    }
}
