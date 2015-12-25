package com.scop.org.minesweeper.elements;

import android.content.Context;
import android.graphics.Canvas;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.scop.org.minesweeper.GamePanel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Oscar on 24/12/2015.
 */
public class GridControl {
    public static final float MARGIN = 50;

    private Grid grid = null;
    private float xPos, yPos, vWidth, vHeight, scale=-1;
    private float dragXpos,dragYpos;

    private boolean isResizing = false;
    private boolean isMoving = false;

    private ScaleGestureDetector scaleDetector;
    private GestureDetector gestureDetector;

    private GamePanel gamePanel;
    private Context context;

    public GridControl(GamePanel gamePanel, Context context) {
        this.xPos = this.yPos = 0;
        this.context = context;
        this.gamePanel = gamePanel;
        this.scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        this.scaleDetector.setQuickScaleEnabled(false);
        this.gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public void start(Grid grid) {
        this.grid = grid;
        moveCenter();
    }

    public void end(){
        this.grid = null;
    }

    public void setDimensions(float w, float h){
        vWidth = w;
        vHeight = h;
        if (this.scale==-1) {
            this.scale = w / (Tile.BITMAP_SIZE * 7f);
            if (grid!=null) moveCenter();
        }
    }

    public void moveCenter(){
        float tileSize = Tile.BITMAP_SIZE;
        float gridW = grid.w*tileSize+MARGIN;
        float gridH = grid.h*tileSize+MARGIN;

        float screenW = vWidth/scale;
        float screenH = vHeight/scale;

        float maxX = MARGIN;
        float maxY = MARGIN;
        float minX = -(gridW-screenW);
        float minY = -(gridH-screenH);

        grid.x=(maxX+minX)/2;
        grid.y = (maxY+minY)/2;
    }

    public void move(float x, float y){
        x+=grid.x;
        y+=grid.y;

        float tileSize = Tile.BITMAP_SIZE;
        float gridW = grid.w*tileSize+MARGIN;
        float gridH = grid.h*tileSize+MARGIN;

        float screenW = vWidth/scale;
        float screenH = vHeight/scale;

        float maxX = MARGIN;
        float maxY = MARGIN;
        float minX = -(gridW-screenW);
        float minY = -(gridH-screenH);

        if (gridW+MARGIN < screenW){
            grid.x=(maxX+minX)/2;
        } else {
            if (x > maxX) grid.x = maxX;
            else if (x < minX) grid.x = minX;
            else grid.x = x;
        }

        if (gridH+MARGIN < screenH){
            grid.y = (maxY+minY)/2;
        } else {
            if (y > maxY) grid.y = maxY;
            else if (y < minY) grid.y = minY;
            else grid.y = y;
        }
    }
    public void zoom(float z){
        float iScale = scale;
        this.scale *= z;
        this.scale = Math.max(0.4f, Math.min(scale, 1.1f));

        float dd = (1/scale - 1/iScale);
        float dX = dd*vWidth/2;
        float dY = dd*vHeight/2;
        move(dX,dY);
    }

    public void draw(Canvas canvas){
        canvas.scale(scale, scale);
        if (grid!=null) grid.draw(canvas,vWidth/scale, vHeight/scale);
        canvas.scale(1/scale, 1/scale);
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

                    dragXpos = X;
                    dragYpos = Y;

                    if (isMoving || Math.abs(dx)+Math.abs(dy)>3){
                        move(dx, dy);
                        this.isMoving = true;
                        gamePanel.postInvalidate();
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

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            isResizing = true;

            float ratio = detector.getScaleFactor();
            zoom(ratio);

            gamePanel.postInvalidate();
            return true;
        }
    }
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        float lastX, lastY;
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (grid.isGameOver()){
                gamePanel.restart();
                gamePanel.postInvalidate();
                return true;
            }
            lastX = e.getX()/scale;
            lastY = e.getY()/scale;
            grid.sTap(lastX, lastY);
            gamePanel.postInvalidate();
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

            int bSize = Tile.BITMAP_SIZE/3;
            float thisX = e.getX()/scale;
            float thisY = e.getY()/scale;

            if (Math.abs(thisX-lastX) < bSize && Math.abs(thisY-lastY) < bSize) {
                grid.dTap(thisX, thisY);
            } else {
                //grid.sTap(lastX, lastY);
                grid.sTap(thisX, thisY);
            }
            gamePanel.postInvalidate();
            return true;
        }
    }
    public void savingState(){
        if (grid==null || grid.isGameOver()){
            new File("/sdcard/Minesweeper/savesstate.save").delete();
            return;
        }
        try {
            new File("/sdcard/Minesweeper").mkdirs();
            FileOutputStream fos = new FileOutputStream (new File("/sdcard/Minesweeper/savesstate.save"));
            DataOutputStream dos = new DataOutputStream(fos);

            char[] map = grid.getMap();
            for (char c : map)
                dos.writeChar(c);
            dos.close();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadingState(){
        try {
            File f = new File("/sdcard/Minesweeper/savesstate.save");
            FileInputStream fis = new FileInputStream (f);
            DataInputStream dis = new DataInputStream(fis);

            int w = dis.readChar();
            int h = dis.readChar();
            int fields = w*h;
            char[] map = new char[fields+2];
            map[0] = (char)w;
            map[1] = (char)h;
            for (int i=0;i<fields;i++)
                map[i+2] = dis.readChar();

            fis.close();
            fis.close();

            f.delete();

            end();
            start(Grid.getGridFromMap(map));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
