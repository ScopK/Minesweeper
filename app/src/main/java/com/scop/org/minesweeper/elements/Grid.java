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
    private int totalBombs;
    public float x = 0,y = 0;
    public int w,h;
    private boolean gameOver=false;

    private List<Tile> revealing = new ArrayList<>();

    private Grid(int w, int h, int totalBombs, Tile[][] tiles){
        this.w = w;
        this.h = h;
        this.totalBombs = totalBombs;
        this.tiles = tiles;
    }

    public Grid(int w, int h, int bombs){
        this.totalBombs = bombs;
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
            if (tiles[x][y].hasBomb()){
                i--;
                continue;
            }
            tiles[x][y].plantBomb();
            List<Tile> ns = getNeighbors(tiles,new int[]{x,y});
            for (Tile t : ns){
                t.hasBombNear();
            }
        }
        boolean found = false;
        while (!found){
            rnd = random.nextInt(max);
            x = rnd%w;
            y = rnd/w;
            if (!tiles[x][y].hasBomb() && tiles[x][y].getBombsNear()==0){
                revealAppend(tiles[x][y]);
                reveal();
                found = true;
            }
        }
    }

    public void loadGraphics(){
        for (Tile[] ts : tiles) for (Tile t : ts){
            t.loadGraphics();
        }
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
    private static Tile getTile(Tile[][] tilegrid,int cx, int cy){
        if (cx<0 || cy<0 || tilegrid.length<=cx || tilegrid[0].length<=cy)
            return null;
        return tilegrid[cx][cy];
    }
    private static List<Tile> getNeighbors(Tile[][] tilegrid, int[] c){
        List<Tile> ts = new ArrayList<>();
        Tile t;
        if ((t = getTile(tilegrid,c[0]-1,c[1]+1)) != null) ts.add(t);
        if ((t = getTile(tilegrid,c[0]-1,c[1])) != null) ts.add(t);
        if ((t = getTile(tilegrid,c[0]-1,c[1]-1)) != null) ts.add(t);

        if ((t = getTile(tilegrid,c[0]+1,c[1]+1)) != null) ts.add(t);
        if ((t = getTile(tilegrid,c[0]+1,c[1])) != null) ts.add(t);
        if ((t = getTile(tilegrid,c[0]+1,c[1]-1)) != null) ts.add(t);

        if ((t = getTile(tilegrid,c[0],c[1]+1)) != null) ts.add(t);
        if ((t = getTile(tilegrid,c[0],c[1]-1)) != null) ts.add(t);
        return ts;
    }

    // ACTIONS_
    public void sTap(float pointX, float pointY){
        if (gameOver) return;
        int[] c = getCoord(pointX, pointY);
        Tile t = getTile(tiles,c[0], c[1]);
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
        Tile t = getTile(tiles,c[0], c[1]);
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

        int bombsNear = tile.getBombsNear();
        if (bombsNear==0){
            tile.setStatus(Tile.EMPTY);
            List<Tile> ts = getNeighbors(tiles,coord);
            for (Tile t : ts){
                if (t.isStatus(Tile.UNDISCOVERED)) {
                    revealAppend(t);
                }
            }
        } else {
            tile.setStatus(Tile.NEAR1-1 + bombsNear);
        }
        return true;
    }

    private boolean massReveal(Tile tile, int[] coord){
        List<Tile> ts = getNeighbors(tiles,coord);
        boolean harderMode = false;
        if (!harderMode) {
            int countFlagged = 0;
            for (Tile t : ts){
                if (t.getStatus()==Tile.FLAGGED) countFlagged++;
            }
            if (tile.getStatus()+1-Tile.NEAR1 != countFlagged){
                return true;
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
        for (Tile[] ts : tiles) for (Tile t : ts){
            if (t.hasBomb()) {
                t.setStatus(Tile.BOMB);
            }
        }
    }

    public void gameWon(){
        gameOver=true;
    }

    //  DRAW_
    public void draw(Canvas canvas,float screenW, float screenH){
        int tileSize = Tile.BITMAP_SIZE;

        float posX = x-tileSize;
        float posY = y;
        for (int i=0;i<tiles.length;i++){
            posX+=tileSize;
            for (int j=0;j<tiles[0].length;j++){
                if (j==0) posY = y;
                else      posY+=tileSize;

                if (posX<-tileSize-5) continue;
                else if (posX>screenW+5) continue;

                if (posY<-tileSize-5) continue;
                else if (posY>screenH+5) continue;

                tiles[i][j].draw(canvas,x,y);
            }
        }
    }

    public boolean isGameOver() {
        return this.gameOver;
    }



    public char[] getMap(){
        char[] map = new char[w*h+2];
        char c = 'E';
        map[0] = (char)w;
        map[1] = (char)h;
        int count = 2;
        for (int j=0;j<h;j++){
            for (int i=0;i<w;i++){
                Tile t = tiles[i][j];
                switch(t.getStatus()){
                    case Tile.BOMB:  c = 'B'; break;
                    case Tile.EMPTY: c = ' '; break;
                    case Tile.NEAR1: c = '1'; break;
                    case Tile.NEAR2: c = '2'; break;
                    case Tile.NEAR3: c = '3'; break;
                    case Tile.NEAR4: c = '4'; break;
                    case Tile.NEAR5: c = '5'; break;
                    case Tile.NEAR6: c = '6'; break;
                    case Tile.NEAR7: c = '7'; break;
                    case Tile.NEAR8: c = '8'; break;
                    case Tile.FLAGGED:
                        c = t.hasBomb()?'F':'f';
                        break;
                    case Tile.UNDISCOVERED:
                        c = t.hasBomb()?'U':'u';
                        break;
                }
                map[count++] = c;
            }
        }
        return map;
    }

    public static Grid getGridFromMap(char[] map){
        int w = map[0];
        int h = map[1];
        int totalBombs = 0;
        int xx,yy;
        int tileSize = Tile.BITMAP_SIZE;

        int fields = w*h;

        Tile t;
        Tile[][] newTiles = new Tile[w][h];

        for (int i=0;i<fields;i++){
            char c = map[i+2];
            xx = i%w;
            yy = i/w;

            switch(c){
                case ' ': t = new Tile(Tile.EMPTY  ,xx*tileSize,yy*tileSize); break;
                case '1': t = new Tile(Tile.NEAR1  ,xx*tileSize,yy*tileSize); break;
                case '2': t = new Tile(Tile.NEAR2  ,xx*tileSize,yy*tileSize); break;
                case '3': t = new Tile(Tile.NEAR3  ,xx*tileSize,yy*tileSize); break;
                case '4': t = new Tile(Tile.NEAR4  ,xx*tileSize,yy*tileSize); break;
                case '5': t = new Tile(Tile.NEAR5  ,xx*tileSize,yy*tileSize); break;
                case '6': t = new Tile(Tile.NEAR6  ,xx*tileSize,yy*tileSize); break;
                case '7': t = new Tile(Tile.NEAR7  ,xx*tileSize,yy*tileSize); break;
                case '8': t = new Tile(Tile.NEAR8  ,xx*tileSize,yy*tileSize); break;
                case 'f': t = new Tile(Tile.FLAGGED,xx*tileSize,yy*tileSize); break;
                case 'F':
                    t = new Tile(Tile.FLAGGED,xx*tileSize,yy*tileSize);
                    t.plantBomb();
                    totalBombs++;
                    break;
                case 'B':
                    t = new Tile(Tile.BOMB   ,xx*tileSize,yy*tileSize);
                    totalBombs++;
                    break;
                case 'U':
                    t = new Tile(Tile.UNDISCOVERED,xx*tileSize,yy*tileSize);
                    t.plantBomb();
                    totalBombs++;
                    break;
                //case 'u':
                default:
                    t = new Tile(Tile.UNDISCOVERED,xx*tileSize,yy*tileSize);
                    break;
            }

            newTiles[xx][yy] = t;
        }
        for (int i=0;i<w;i++){
            for (int j=0;j<h;j++){
                if (newTiles[i][j].hasBomb()){
                    List<Tile> ns = getNeighbors(newTiles,new int[]{i,j});
                    for (Tile ti : ns){
                        ti.hasBombNear();
                    }
                }
            }
        }
        return new Grid(w,h,totalBombs,newTiles);
    }
}
