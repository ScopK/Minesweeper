package com.scop.org.minesweeper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.scop.org.minesweeper.elements.Grid;
import com.scop.org.minesweeper.elements.GridControl;
import com.scop.org.minesweeper.elements.Tile;
import com.scop.org.minesweeper.elements.TileStyle;

/**
 * Created by Oscar on 25/11/2015.
 */
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback{
    private MainThread thread;

    private GridControl gridControl;

    public GamePanel(Context context) {
        super(context);

        //make gamePanel focusable so it can handle events
        setFocusable(true);

        //add the callback to the surfaceholder to intercept events
        getHolder().addCallback(this);

        // init grid:
        Bitmap marks = BitmapFactory.decodeResource(getResources(), R.drawable.tilemarks_a);
        Bitmap tiles = BitmapFactory.decodeResource(getResources(), R.drawable.tiles_a);
        TileStyle.getInstance().setStyle(tiles,4,marks,180f,1f);
        gridControl = new GridControl(this,context);
        gridControl.start(new Grid(3,3));

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        gridControl.setDimensions(getWidth(), getHeight());

        if (thread==null) {
            thread = new MainThread(getHolder(), this);

            // we can safely start the game loop
            thread.setRunning(true);
            thread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        gridControl.setDimensions(getWidth(), getHeight());
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry){
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
                thread = null;
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        gridControl.onTouchEvent(event);
        return true;//super.onTouchEvent(event);
    }



    public void update() {
        gridControl.update();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas!=null){
            //canvas.drawARGB(255,102,119,135); //Original
            canvas.drawARGB(255, 60, 60, 60);

            gridControl.draw(canvas);
        }
    }


    public void refresh(){
        thread.refreshFrame();
    }
}
