package com.scop.org.minesweeper.elements;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.scop.org.minesweeper.GamePanel;
import com.scop.org.minesweeper.elements.visual.ColorFilterHue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Oscar on 27/11/2015.
 */
public class Grid implements Serializable {
    private Tile[][] tiles;
    public float x,y,w,h;
    private boolean gameOver=true;

    private List<Tile> revealing;

    public Grid(int w, int h, int bombs){
        gameOver = false;
        revealing = new ArrayList<>();
        this.x=0;
        this.y=0;
        this.w=w;
        this.h=h;

        int tileSize = Tile.BITMAP_SIZE;
        tiles = new Tile[w][h];
        for (int i=0;i<w;i++) {
            for (int j = 0; j < h; j++) {
                tiles[i][j] = new Tile(Tile.UNDISCOVERED,i*tileSize,j*tileSize);
            }
        }
        Random random = new Random();
        int x,y,rnd,max = w*h;
        for (int i=0;i<bombs;i++) {
            rnd = random.nextInt(max);
            x = rnd%w;
            y = rnd/w;
            tiles[x][y].plantBomb();
        }
        /*
        boolean found = false;
        while (!found){
            rnd = random.nextInt(max);
            x = rnd%w;
            y = rnd/w;
            if (!tiles[x][y].hasBomb()){
                reveal(tiles[x][y],new int[]{x,y});
                found = true;
            }
        }
        */
    }

    private int[] getCoord(float px, float py){
        float dX = (-this.x+px)/Tile.BITMAP_SIZE;
        float dY = (-this.y+py)/Tile.BITMAP_SIZE;
        if (dX<0) dX = -1;
        if (dY<0) dY = -1;
        return new int[]{(int)dX,(int)dY};
    }
    private int[] getCoord(Tile t){
        for (int i=0;i<tiles.length;i++){
            for (int j=0;j<tiles[0].length;j++){
                if (tiles[i][j]==t)
                    return new int[]{i,j};
            }
        }
        return null;
    }
    private Tile getTile(int cx, int cy){
        if (cx<0 || cy<0 || tiles.length<=cx || tiles[0].length<=cy)
            return null;
        return tiles[cx][cy];
    }

    private List<Tile> getNeighbors(int[] c){
        List<Tile> ts = new ArrayList<>();
        Tile t;
        if ((t = getTile(c[0]-1,c[1]+1)) != null) ts.add(t);
        if ((t = getTile(c[0]-1,c[1])) != null) ts.add(t);
        if ((t = getTile(c[0]-1,c[1]-1)) != null) ts.add(t);

        if ((t = getTile(c[0]+1,c[1]+1)) != null) ts.add(t);
        if ((t = getTile(c[0]+1,c[1])) != null) ts.add(t);
        if ((t = getTile(c[0]+1,c[1]-1)) != null) ts.add(t);

        if ((t = getTile(c[0],c[1]+1)) != null) ts.add(t);
        if ((t = getTile(c[0],c[1]-1)) != null) ts.add(t);
        return ts;
    }

    // ACTIONS_
    public void sTap(float pointX, float pointY){
        if (gameOver) return;
        int[] c = getCoord(pointX, pointY);
        Tile t = getTile(c[0], c[1]);
        if (t==null) return;
        switch (t.getStatus()){
            case Tile.UNDISCOVERED:
                t.setStatus(Tile.FLAGGED);
                break;
            case Tile.FLAGGED:
                t.setStatus(Tile.UNDISCOVERED);
                break;
            case Tile.EMPTY:
            case Tile.BOMB:
                break;
            default:
                revealing.clear();
                if (!massReveal(t,c)){
                    gameOver();
                }
                break;
        }
    }

    public void dTap(float pointX, float pointY){
        if (gameOver) return;
        int[] c = getCoord(pointX, pointY);
        Tile t = getTile(c[0], c[1]);
        if (t==null) return;
        switch (t.getStatus()){
            case Tile.UNDISCOVERED:
            case Tile.FLAGGED:
                revealing.clear();
                revealAppend(t);
                if (!reveal()){
                    gameOver();
                }
                break;
            default:
                break;
        }
    }

    private boolean reveal(){
        boolean gameOver = false;
        while (revealing.size()!=0){
            Tile t = revealing.remove(0);
            if (!reveal(t,getCoord(t))){
                gameOver = true;
            }
        }
        return !gameOver;
    }

    private void revealAppend(Tile t){
        if (!revealing.contains(t))
            revealing.add(t);
    }

    private boolean reveal(Tile tile, int[] coord){
        if (!tile.reveal())
            return false;

        List<Tile> ts = getNeighbors(coord);
        int countBombs = 0;
        for (Tile t : ts){
            if (t.hasBomb()) countBombs++;
        }
        if (countBombs==0){
            tile.setStatus(Tile.EMPTY);
            for (Tile t : ts){
                if (t.isStatus(Tile.UNDISCOVERED)) {
                    revealAppend(t);
                }
            }
        } else {
            switch (countBombs){
                case 1: tile.setStatus(Tile.NEAR1); break;
                case 2: tile.setStatus(Tile.NEAR2); break;
                case 3: tile.setStatus(Tile.NEAR3); break;
                case 4: tile.setStatus(Tile.NEAR4); break;
                case 5: tile.setStatus(Tile.NEAR5); break;
                case 6: tile.setStatus(Tile.NEAR6); break;
                case 7: tile.setStatus(Tile.NEAR7); break;
                case 8: tile.setStatus(Tile.NEAR8); break;
            }
        }
        return true;
    }

    private boolean massReveal(Tile tile, int[] coord){
        List<Tile> ts = getNeighbors(coord);
        boolean harderMode = false;
        if (!harderMode) {
            int countFlagged = 0;
            for (Tile t : ts){
                if (t.getStatus()==Tile.FLAGGED) countFlagged++;
            }

            switch (tile.getStatus()) {
                case Tile.NEAR1:
                    if (countFlagged != 1) return true;
                    break;
                case Tile.NEAR2:
                    if (countFlagged != 2) return true;
                    break;
                case Tile.NEAR3:
                    if (countFlagged != 3) return true;
                    break;
                case Tile.NEAR4:
                    if (countFlagged != 4) return true;
                    break;
                case Tile.NEAR5:
                    if (countFlagged != 5) return true;
                    break;
                case Tile.NEAR6:
                    if (countFlagged != 6) return true;
                    break;
                case Tile.NEAR7:
                    if (countFlagged != 7) return true;
                    break;
                case Tile.NEAR8:
                    if (countFlagged != 8) return true;
                    break;
            }
        }
        for (Tile t : ts){
            if (t.isStatus(Tile.UNDISCOVERED)) {
                revealAppend(t);
            }
        }
        return reveal();
    }

    public void gameOver(){
        gameOver=true;
        for (Tile[] r : tiles) for (Tile t : r){
            if (t.hasBomb()) {
                t.setStatus(Tile.BOMB);
            }
        }
    }

    // UPDATE & DRAW_
    public void update(){}

    public void draw(Canvas canvas){
        for (int i=0;i<tiles.length;i++){
            for (int j=0;j<tiles[0].length;j++){
                tiles[i][j].draw(canvas,x,y);
            }
        }
    }

    public boolean isGameOver() {
        return this.gameOver;
    }
}
