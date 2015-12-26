package com.scop.org.minesweeper.elements;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Oscar on 26/12/2015.
 */
public class Button {
    private float width, height, x,y;
    private String text, text2;
    private int action;

    Paint bgStyle, textStyle, textStyle2;

    public Button(int action, String text, String text2, float x, float y, float width, float height, int bgcolor) {
        this.action = action;
        this.height = height;
        this.text = text;
        this.text2 = text2;
        this.width = width;
        this.x = x;
        this.y = y;

        bgStyle = new Paint();
        bgStyle.setColor(bgcolor);

        textStyle = new Paint(Paint.FILTER_BITMAP_FLAG);
        textStyle.setColor(Color.WHITE);
        textStyle.setTextAlign(Paint.Align.CENTER);
        textStyle.setTextSize(32);
        textStyle.setTextScaleX(1.05f);

        textStyle2 = new Paint(Paint.FILTER_BITMAP_FLAG);
        textStyle2.setColor(0xFFDDDDDD);
        textStyle2.setTextAlign(Paint.Align.CENTER);
        textStyle2.setTextSize(20);
        textStyle2.setTextScaleX(1.05f);
    }

    public boolean isPressed(float tx, float ty, float baseX, float baseY){
        float x = this.x+baseX;
        float y = this.y+baseY;
        return x<=tx && tx<=(x+width) && y<=ty && ty<=(y+height);
    }

    public void draw(Canvas canvas, float baseX, float baseY){
        float x = this.x+baseX;
        float y = this.y+baseY;
        float textSize = textStyle.getTextSize()/2;
        canvas.drawRect(x, y, x + width, y + height, bgStyle);
        if (text2.equals("")){
            canvas.drawText(text, x + width / 2, y +textSize+ height / 2, textStyle);
        } else {
            canvas.drawText(text, x + width / 2, y + height / 2, textStyle);
            canvas.drawText(text2, x + width / 2, y + 2 * textSize + height / 2, textStyle2);
        }
    }

    public int getAction() {
        return action;
    }
}
