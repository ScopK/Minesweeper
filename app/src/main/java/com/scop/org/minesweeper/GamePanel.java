package com.scop.org.minesweeper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
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
    private float initialX,initialY;
    private float scaleFactor;

    public GamePanel(Context context) {
        super(context);

        //add the callback to the surfaceholder to intercept events
        getHolder().addCallback(this);

        TileStyle.getInstance().setStyle(getResources(),180f,1f);
        grid = new Grid(12,12);

        //make gamePanel focusable so it can handle events
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        scaleFactor = getWidth()/(Tile.BITMAP_SIZE*8f);

        if (thread==null) {
            thread = new MainThread(getHolder(), this);

            // we can safely start the game loop
            thread.setRunning(true);
            thread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

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
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                initialX = event.getX();
                initialY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:

                float X = event.getX();
                float Y = event.getY();

                int dx = Math.round((X - initialX) / scaleFactor);
                int dy = Math.round((Y - initialY) / scaleFactor);

                initialX = X;
                initialY = Y;

                grid.move(dx, dy);
                thread.refreshFrame();
                break;

            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_UP:
                break;

            case MotionEvent.ACTION_CANCEL: break;

        }

        return true;//super.onTouchEvent(event);
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
            canvas.scale(scaleFactor, scaleFactor);
            grid.draw(canvas);
        }
    }
}
