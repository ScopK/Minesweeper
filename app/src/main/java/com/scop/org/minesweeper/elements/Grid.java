package com.scop.org.minesweeper.elements;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.scop.org.minesweeper.GamePanel;
import com.scop.org.minesweeper.elements.visual.ColorFilterHue;

/**
 * Created by Oscar on 27/11/2015.
 */
public class Grid {
    private Tile[][] tiles;
    private float border,x,y,w,h;
    private float initialX,initialY;
    private float scale = -1;
    private boolean lockMove = false;
    private GamePanel gamePanel;

    public Grid(GamePanel gamepanel, int w, int h){
        this.border = 50;
        this.x=0;
        this.y=0;
        this.gamePanel = gamepanel;

        int tileSize = Tile.BITMAP_SIZE;
        tiles = new Tile[w][h];
        for (int i=0;i<w;i++) {
            for (int j = 0; j < h; j++) {
                tiles[i][j] = new Tile(Tile.UNDISCOVERED,i*tileSize,j*tileSize);
            }
        }
    }

    public void update(){
    }

    public void draw(Canvas canvas){
        float tileSize = Tile.BITMAP_SIZE;
        float screenW = w/scale;
        float screenH = h/scale;

        canvas.scale(scale, scale);
        for (int i=0;i<tiles.length;i++){
            for (int j=0;j<tiles[0].length;j++){
                float startX = i*tileSize+x;
                float startY = j*tileSize+y;
                if ( startX<-tileSize+5 ||
                     startY<-tileSize+5 ||
                     startX>screenW ||
                     startY>screenH) continue;

                tiles[i][j].draw(canvas,x,y);
            }
        }
        canvas.scale(1/scale, 1/scale);
    }

    public void setScreenSize(int w, int h){
        this.w = w;
        this.h = h;
        if (this.scale==-1) {
            this.scale = w / (Tile.BITMAP_SIZE * 8f);
        }
    }
    public void move(float x, float y){
        this.x += x;
        this.y += y;

        float tileSize = Tile.BITMAP_SIZE;
        float gridW = tiles.length*tileSize+border;
        float gridH = tiles[0].length*tileSize+border;

        float screenW = this.w/scale;
        float screenH = this.h/scale;

        float maxX = border;
        float maxY = border;
        float minX = -(gridW-screenW);
        float minY = -(gridH-screenH);

        if (gridW+border<screenW){
            this.x=maxX+minX/2;
        } else {
            if (this.x>maxX) this.x=maxX;
            else if (this.x<minX) this.x = minX;
        }

        if (gridH+border<screenH){
            this.y=maxY+minY/2;
        } else {
            if (this.y>maxY) this.y=maxY;
            else if (this.y<minY) this.y = minY;
        }
    }
    public void zoom(float z){
        float iScale = scale;
        this.scale *= z;
        this.scale = Math.max(0.5f, Math.min(scale, 1.2f));

        float dd = (1/scale - 1/iScale);
        float dX = dd*this.w/2;
        float dY = dd*this.h/2;
        move(dX,dY);
    }

    public void onTouchEvent(MotionEvent event){
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                initialX = event.getX();
                initialY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                if (this.lockMove)
                    return;
                float X = event.getX();
                float Y = event.getY();

                int dx = Math.round((X - initialX) / scale);
                int dy = Math.round((Y - initialY) / scale);

                initialX = X;
                initialY = Y;

                move(dx, dy);
                gamePanel.invalidate();
                break;

            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_UP:
                this.lockMove = false;
                break;

            case MotionEvent.ACTION_CANCEL: break;

        }
    }
    public void onTouchDetector(ScaleGestureDetector detector){
        this.lockMove = true;

        float ratio = detector.getScaleFactor();
        zoom(ratio);

        gamePanel.invalidate();
    }

}
