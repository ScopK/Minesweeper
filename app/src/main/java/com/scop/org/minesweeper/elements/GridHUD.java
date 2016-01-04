package com.scop.org.minesweeper.elements;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.scop.org.minesweeper.control.Settings;

public class GridHUD {
    private Grid grid;
    private View view;
    private float vHeight, vWidth, vTop;

    private Paint bg,textStyle;

    private Timer timer;
    protected String timeStr="";

    public GridHUD(Grid grid, View view) {
        this.grid = grid;
        this.view = view;

        bg = new Paint();

        textStyle = new Paint(Paint.FILTER_BITMAP_FLAG);
        textStyle.setColor(Color.WHITE);
        textStyle.setTextSize(40);
    }

    public void setDimensions(float vWidth, float vHeight){
        this.vWidth = vWidth;
        this.vHeight = vHeight;
        this.vTop = vHeight-100;
        startTimer();
    }

    public void draw(Canvas canvas) {
        switch(grid.getStatus()){
            case Grid.PLAYING: bg.setColor(0xA0000000); break;
            case Grid.GAMEOVER: bg.setColor(0xA0FF0000); break;
            case Grid.WIN: bg.setColor(0xA000FF00); break;
        }

        canvas.drawRect(0, vTop, vWidth, vHeight, bg);

        int tiles = grid.getTotalTiles();
        textStyle.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Open: "+(tiles-grid.getUndiscoveredTiles())+"/"+tiles, 30, vHeight-30, textStyle);

        textStyle.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(timeStr + "   " + grid.getFlaggedBombs() + "/" + grid.getTotalBombs(), vWidth - 30, vHeight - 30, textStyle);
    }

    public void startTimer(){
        timer = new Timer();
        timer.start();
    }

    public void resumeTimer(){
        timer.timeResume();
    }
    public void pauseTimer(){
        timer.timePause();
    }
    public void restartTimer() {
        if (timer==null){
            startTimer();
        } else {
            timer.restart();
        }
    }
    public void stopTimer() {
        timer.timerClose();
        timer=null;
    }
    public int getTime(){
        return timer.getTime();
    }
    public void timerNotify(int time){
        int s = time / 1000;
        int m = s / 60;
        int h = m / 60;
        s %= 60;
        m %= 60;
        timeStr = (h == 0 ? "" : (h + ":")) + m + ":" + (s < 10 ? "0" + s : s);
        view.postInvalidate(0, (int) vTop, (int) vWidth, (int) vHeight);
    }

    public class Timer extends Thread{
        long initTime,pausedTime;
        boolean running = true;
        boolean showTime = true;
        public int getTime(){
            return (int)((System.nanoTime()-initTime)/1000000);
        }
        public void restart(){
            running = true;
            initTime=System.nanoTime();
            pausedTime=0;
            showTime = Settings.getInstance().isShowTime();
        }
        public void timePause(){
            pausedTime=System.nanoTime();
        }
        public void timeResume(){
            initTime+= (System.nanoTime()-pausedTime);
            pausedTime=0;
            showTime = Settings.getInstance().isShowTime();
            GridHUD.this.timeStr = "";
        }
        public void timerClose(){
            running = false;
        }
        @Override
        public void run() {
            super.run();
            restart();
            while (running){
                if (showTime) {
                    GridHUD.this.timerNotify(getTime());
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
