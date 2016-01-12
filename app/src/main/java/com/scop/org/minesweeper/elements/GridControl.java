package com.scop.org.minesweeper.elements;

import android.content.Context;
import android.graphics.Canvas;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.scop.org.minesweeper.GamePanel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import com.scop.org.minesweeper.control.Settings;

/**
 * Created by Oscar on 24/12/2015.
 */
public class GridControl implements GridEventListener{
    public static final float MARGIN = 100;

    private Grid grid = null;
    private GridHUD hud = null;
    private float vWidth=-1, vHeight=-1, vWidthScaled = -1, vHeightScaled = -1;
    private float minScale=0.3f, scale=-1, maxScale=1f;
    private float dragXpos,dragYpos;

    private boolean isResizing = false;
    private boolean isMoving = false;

    private ScaleGestureDetector scaleDetector;
    private GestureDetector gestureDetector;

    private View view;
    private Context context;

    public GridControl(Context context, View view) {
        this.context = context;
        this.view = view;
        this.scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        this.scaleDetector.setQuickScaleEnabled(false);
        this.gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public void start(Grid grid) {
        this.grid = grid;
        this.grid.setListener(this);
        if (hud!=null){
            hud.stopTimer();
        }
        this.hud = new GridHUD(grid,view);
        move();
    }
    public void restart() {
        grid.start();
        hud.restartTimer();
        move();
    }

    public void end(){
        this.grid = null;
    }

    public void setDimensions(float w, float h){
        vWidth = w;
        vHeight = h;
        if (hud!=null) hud.setDimensions(w,h);
        if (this.scale==-1) {
            this.scale = w / (Tile.BITMAP_SIZE * 7f);
            this.minScale = w / (Tile.BITMAP_SIZE * 14f);
            this.maxScale = w / (Tile.BITMAP_SIZE * 4f);
            this.vWidthScaled = vWidth/scale;
            this.vHeightScaled = vHeight/scale;
            if (grid!=null) move();
        }
    }

    public void focus(){
        float tileSize = Tile.BITMAP_SIZE;
        grid.x = -(grid.x*tileSize+tileSize/2-vWidthScaled/2);
        grid.y = -(grid.y*tileSize+tileSize/2-vHeightScaled/2);
        limitMoving();
    }
    public void move(){
        float tileSize = Tile.BITMAP_SIZE;
        if (vWidth>0) {
            if (grid.x == Integer.MIN_VALUE){
                float gridW = grid.w * tileSize + MARGIN;
                float maxX = MARGIN;
                float minX = -(gridW - vWidthScaled);
                grid.x = (maxX + minX) / 2;
            } else {
                focus();
            }
        }

        if (vHeight>0) {
            if (grid.y == Integer.MIN_VALUE){
                float gridH = grid.h * tileSize + MARGIN;
                float maxY = MARGIN;
                float minY = -(gridH - vHeightScaled);
                grid.y = (maxY + minY) / 2;
            }
        }
    }
    public void move(float x, float y){
        grid.x+=x;
        grid.y+=y;
        limitMoving();
    }
    private void limitMoving(){
        float tileSize = Tile.BITMAP_SIZE;
        float gridW = grid.w*tileSize+MARGIN;
        float gridH = grid.h*tileSize+MARGIN;

        float maxX = MARGIN;
        float maxY = MARGIN;
        float minX = -(gridW-vWidthScaled);
        float minY = -(gridH-vHeightScaled);

        if (gridW+MARGIN < vWidthScaled){
            grid.x=(maxX+minX)/2;
        } else {
            if (grid.x > maxX) grid.x = maxX;
            else if (grid.x < minX) grid.x = minX;
        }

        if (gridH+MARGIN < vHeightScaled){
            grid.y = (maxY+minY)/2;
        } else {
            if (grid.y > maxY) grid.y = maxY;
            else if (grid.y < minY) grid.y = minY;
        }
    }
    public void zoom(float z){
        float iScale = scale;
        this.scale *= z;
        this.scale = Math.max(minScale, Math.min(scale, maxScale));
        this.vWidthScaled = vWidth/scale;
        this.vHeightScaled = vHeight/scale;

        float dd = (1/scale - 1/iScale);
        float dX = dd*vWidth/2;
        float dY = dd*vHeight/2;
        move(dX,dY);
    }

    public void draw(Canvas canvas){
        canvas.scale(scale, scale);
        if (grid!=null) grid.draw(canvas,vWidthScaled, vHeightScaled);
        canvas.scale(1/scale, 1/scale);
        if (hud!=null) hud.draw(canvas);
    }

    public void onTouchEvent(MotionEvent e){
        gestureDetector.onTouchEvent(e);
        scaleDetector.onTouchEvent(e);

        if (!scaleDetector.isInProgress()) {
            int action = e.getActionMasked();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    dragXpos = e.getX();
                    dragYpos = e.getY();
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (this.isResizing)
                        return;

                    float X = e.getX();
                    float Y = e.getY();

                    int dx = Math.round((X - dragXpos) / scale);
                    int dy = Math.round((Y - dragYpos) / scale);

                    if (isMoving || Math.abs(dx)+Math.abs(dy)>35){
                        dragXpos = X;
                        dragYpos = Y;

                        move(dx, dy);
                        this.isMoving = true;
                        view.postInvalidate();
                    }
                    break;

                case MotionEvent.ACTION_OUTSIDE:
                case MotionEvent.ACTION_UP:
                    this.isMoving = false;
                    this.isResizing = false;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    break;
            }
        }
    }

    public void onWindowVisibilityChanged(int visibility) {
        switch (visibility){
            case View.VISIBLE:
                if (hud!=null) hud.resumeTimer();
                break;
            case View.GONE:
            case View.INVISIBLE:
                if (hud!=null) hud.pauseTimer();
                break;
        }
    }

    @Override
    public void onFinish(boolean userWin) {
        hud.stopTimer();
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            isResizing = true;

            float ratio = detector.getScaleFactor();
            zoom(ratio);

            view.postInvalidate();
            return true;
        }
    }
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (grid.isGameOver()){
                restart();
                view.postInvalidate();
                return true;
            }
            float thisX = e.getX()/scale;
            float thisY = e.getY()/scale;
            grid.sTap(thisX, thisY);
            view.postInvalidate();
            return true;
        }
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e){
            /*if (grid.isGameOver()){
                gamePanel.restart();
                gamePanel.postInvalidate();
                return true;
            }
            grid.sTap(e.getX()/scale, e.getY()/scale);
            gamePanel.postInvalidate();*/
            return true;
        }
        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            if (e.getAction()!=MotionEvent.ACTION_UP) return false;
            if (isMoving) return false;

            float thisX = e.getX()/scale;
            float thisY = e.getY()/scale;
            grid.dTap(thisX, thisY);
            view.postInvalidate();
            return true;
        }
    }
    public void savingState(){
        if (grid== null || grid.isGameOver()){
            new File(Settings.SAVE_STATE_PATH).delete();
            return;
        }
        try {
            new File(Settings.SAVE_STATE_PATH.substring(0,Settings.SAVE_STATE_PATH.lastIndexOf("/"))).mkdirs();
            FileOutputStream fos = new FileOutputStream (new File(Settings.SAVE_STATE_PATH));
            DataOutputStream dos = new DataOutputStream(fos);

            char[] map = grid.getMap(vWidthScaled/2,vHeightScaled/2,hud.getTime());
            for (char c : map) {
                dos.writeChar(c);
            }
            dos.close();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadingState(){
        try {
            File f = new File(Settings.SAVE_STATE_PATH);
            FileInputStream fis = new FileInputStream (f);
            DataInputStream dis = new DataInputStream(fis);

            int w = dis.readChar();
            int h = dis.readChar();
            int fields = w*h+8;
            char[] map = new char[fields+10];
            map[0] = (char)w;
            map[1] = (char)h;
            for (int i=0;i<fields;i++)
                map[i+2] = dis.readChar();

            fis.close();
            fis.close();

            f.delete();

            end();
            start(Grid.getGridFromMap(map));
            hud.setTime(grid.getStartingTime());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
