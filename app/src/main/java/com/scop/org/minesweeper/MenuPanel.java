package com.scop.org.minesweeper;

import android.graphics.Canvas;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.scop.org.minesweeper.elements.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oscar on 25/12/2015.
 */
public class MenuPanel extends View {
    private static final int ACTION_LOAD = 0;
    private static final int ACTION_SETTINGS = 1;
    private static final int ACTION_GAME1 = 2;
    private static final int ACTION_GAME2 = 3;
    private static final int ACTION_GAME3 = 4;
    private static final int ACTION_GAME4 = 5;
    private static final int ACTION_GAME5 = 6;
    private static final int ACTION_GAME6 = 7;
    private static final int ACTION_GAME7 = 8;


    private MenuActivity parent;
    private GestureDetector gestureDetector;
    private List<Button> buttons = new ArrayList<>();

    private float x=0;
    private float y=0;
    private float heightNextButton = 0;
    private float dragXpos,dragYpos;

    public MenuPanel(MenuActivity parent) {
        super(parent);
        this.parent = parent;
        this.gestureDetector = new GestureDetector(parent, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (buttons!=null){
                    float mX = e.getX();
                    float mY = e.getY();

                    for (Button b : buttons) if (b.isPressed(mX,mY,x,y)){
                        switch (b.getAction()){
                            case ACTION_LOAD:
                                MenuPanel.this.parent.loadGrid();
                                break;
                            case ACTION_SETTINGS:
                                MenuPanel.this.parent.openSettings();
                                break;
                            case ACTION_GAME1:
                                MenuPanel.this.parent.startGrid(4, 6, 5);
                                break;
                            case ACTION_GAME2:
                                MenuPanel.this.parent.startGrid(10, 10, 12);
                                break;
                            case ACTION_GAME3:
                                MenuPanel.this.parent.startGrid(10, 10, 25);
                                break;
                            case ACTION_GAME4:
                                MenuPanel.this.parent.startGrid(15, 15, 50);
                                break;
                            case ACTION_GAME5:
                                MenuPanel.this.parent.startGrid(25, 25, 100);
                                break;
                            case ACTION_GAME6:
                                MenuPanel.this.parent.startGrid(50, 50, 503);
                                break;
                            case ACTION_GAME7:
                                MenuPanel.this.parent.startGrid(6, 20, 40);
                                break;
                        }
                        break;
                    }
                }
                return true;
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        buttons.clear();
        heightNextButton = 40;
        addButton(ACTION_LOAD,      "Load", "",                               w, h, 0x99FF0000);
        addButton(ACTION_GAME1,     "Sandbox", "4x6 - 5 bombs",               w, h, 0x99FFFF00);
        addButton(ACTION_GAME2,     "Baby", "10x10 - 12 bombs",               w, h, 0x99FFFF00);
        addButton(ACTION_GAME3,     "Atomic Baby", "10x10 - 25 bombs",        w, h, 0x99FFFF00);
        addButton(ACTION_GAME4,     "Profi", "15x15 - 50 bombs",              w, h, 0x99FFFF00);
        addButton(ACTION_GAME5,     "Mom, take me back", "25x25 - 100 bombs", w, h, 0x99FFFF00);
        addButton(ACTION_GAME6,     "Inferno", "50x50 - 503 bombs",           w, h, 0x99FFFF00);
        addButton(ACTION_GAME7,     "Chuck Norris", "6x20 - 40 bombs",        w, h, 0x99FFFF00);
        addButton(ACTION_SETTINGS,  "Settings", "",                           w, h, 0x9900FFFF);

        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void addButton(int action, String text,String text2, float w, float h, int bgcolor){
        buttons.add(new Button(action,text,text2,w*.15f,heightNextButton,w*.7f  ,140, bgcolor));
        heightNextButton+=180;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        switch (e.getAction()){
            case MotionEvent.ACTION_DOWN:
                //dragXpos = e.getX();
                dragYpos = e.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //float X = e.getX();
                float Y = e.getY();

                //x+= X - dragXpos;
                y+= Y - dragYpos;

                float maxHeight = getHeight()-heightNextButton;
                if (y>0 || maxHeight>0) y = 0;
                else if (y<maxHeight) y=maxHeight;

                //dragXpos = X;
                dragYpos = Y;
                invalidate();
                break;
        }

        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(0xFF3C3C3C);
        for (Button b : buttons){
            b.draw(canvas,x,y);
        }
    }
}
