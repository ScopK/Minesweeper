package com.scop.org.minesweeper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.scop.org.minesweeper.elements.Grid;
import com.scop.org.minesweeper.elements.Tile;
import com.scop.org.minesweeper.elements.TileStyle;

/**
 * Created by Oscar on 25/11/2015.
 */
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback{
    private MainThread thread;
    private Grid grid;
    private int scrWidth, scrHeight;

    private ScaleGestureDetector scaleDetector;

    public GamePanel(Context context) {
        super(context);
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        //add the callback to the surfaceholder to intercept events
        getHolder().addCallback(this);

        Bitmap marks = BitmapFactory.decodeResource(getResources(), R.drawable.tilemarks_a);
        Bitmap tiles = BitmapFactory.decodeResource(getResources(), R.drawable.tiles_a);
        TileStyle.getInstance().setStyle(tiles,4,marks,180f,1f);
        grid = new Grid(this,30,20);

        //make gamePanel focusable so it can handle events
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (grid!=null) {
            grid.setScreenSize(getWidth(),getHeight());
            grid.move(0, 0);
        }

        if (thread==null) {
            thread = new MainThread(getHolder(), this);

            // we can safely start the game loop
            thread.setRunning(true);
            thread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (grid!=null) grid.setScreenSize(getWidth(),getHeight());
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
        scaleDetector.onTouchEvent(event);

        if (!scaleDetector.isInProgress()) {
            if (grid!=null) grid.onTouchEvent(event);
        }
        return true;//super.onTouchEvent(event);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (grid!=null) grid.onTouchDetector(detector);
            return true;
        }
    }

    public void update() {
        grid.update();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        //canvas.drawARGB(255,102,119,135); //Original
        canvas.drawARGB(255, 60, 60, 60);

        if (canvas!=null){
            grid.draw(canvas);
        }
    }


    public void invalidate(){
        thread.refreshFrame();
    }
}
