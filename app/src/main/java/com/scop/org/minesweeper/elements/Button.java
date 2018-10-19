package com.scop.org.minesweeper.elements;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.scop.org.minesweeper.control.ScreenProperties;

public class Button {
    private static final float PADDING_H = ScreenProperties.dpiValue(10);
    private static final float MARGIN_H = ScreenProperties.dpiValue(6);
    private static final int FONTSIZE = 10;
    private static final int FONTSIZE_SUB = 6;
    private static final float PERCENT_MARGIN_W = 0.14f;

    private float height, y;
    private int action;

    Paint bgStyle;

    private Text text1, text2;
    private boolean doubleLine = false;

    public Button(int action, String text, String subText, float y, int bgcolor) {
        this.action = action;
        this.height = PADDING_H*2;
        this.y = y;

        bgStyle = new Paint();
        bgStyle.setColor(bgcolor);

	    text1 = new Text(ScreenProperties.WIDTH/2, (int)(y + PADDING_H), text, FONTSIZE)
			    .setAlign(Paint.Align.CENTER)
			    .setColor(Color.WHITE);

	    float y2Pos = y + PADDING_H + MARGIN_H + text1.getDimensions().height();
	    text2 = new Text(ScreenProperties.WIDTH/2, (int)y2Pos, subText, FONTSIZE_SUB)
	            .setAlign(Paint.Align.CENTER)
			    .setColor(0xDDFFFFFF);

        this.height += text1.getDimensions().height();

        if (subText != "") {
	        doubleLine = true;
            this.height += text2.getDimensions().height() + MARGIN_H;
        }
    }

    public boolean isPressed(float tx, float ty, float baseY){
        float x = ScreenProperties.WIDTH*PERCENT_MARGIN_W;
        float width = ScreenProperties.WIDTH-x*2;
        float y = this.y+baseY;
        return x<=tx && tx<=(x+width) && y<=ty && ty<=(y+height);
    }

    public void draw(Canvas canvas){
        float x = ScreenProperties.WIDTH*PERCENT_MARGIN_W;

        //canvas.drawRect(x, y, ScreenProperties.WIDTH-x-x, y + height, bgStyle);
	    canvas.drawRect(x, y, ScreenProperties.WIDTH-x, y + height, bgStyle);

        if (doubleLine){
        	text1.draw(canvas);
	        text2.draw(canvas);
        } else {
	        text1.draw(canvas);
	    }
    }

    public int getAction() {
        return action;
    }

    public float getHeight(){
    	return height;
    }
}
