package com.scop.org.minesweeper.elements;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.scop.org.minesweeper.control.ScreenProperties;


public class Text {

	protected Rect rect;

	private String text;
	private float fontSize;
	private Align align;
	private int color = 0xFFFFFFFF;
	private boolean anchorTop = true;
	private boolean bold = false;
	private boolean italic = false;

	public Text(int x, int y, String text, int fontSize){
		rect = new Rect();
		rect.left = x;
		rect.top = y;

		setText(text);
		this.align = Align.LEFT;
		this.fontSize = ScreenProperties.fontSizeAdapted(fontSize);
		calc();
	}

	public Text setAlign(Align align){
		this.align = align;
		return this;
	}

	public Text setText(String text){
		if (!text.equals(this.text)) {
			this.text = text;
			calc();
		}
		return this;
	}

	public Text setColor(int color){
		this.color = color;
		return this;
	}

	public Text setBold(boolean bold) {
		this.bold = bold;
		return this;
	}

	public Text setItalic(boolean italic) {
		this.italic = italic;
		return this;
	}

	public Text anchorPointIsTop(boolean anchorTop){
		this.anchorTop = anchorTop;
		return this;
	}


	public Rect getDimensions(){
		return rect;
	}

	private void calc(){
		Rect dim = new Rect();
		Paint textPaint = new Paint();
		textPaint.setTextSize(fontSize);
		textPaint.getTextBounds(text,0,text.length(),dim);
		rect.right = rect.left+dim.width();

		textPaint.getTextBounds("W",0,1,dim);
		rect.bottom = rect.top+dim.height();
	}

	public void draw(Canvas canvas) {
		Paint paint = new Paint();

		paint.setColor(color);
		paint.setTextSize(fontSize);
		paint.setTextAlign(align);
		paint.setAntiAlias(true);

		if (bold) {
			if (italic)
				paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC));
			else
				paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
		} else if (italic)
			paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));

		canvas.drawText(this.text, rect.left, anchorTop? rect.bottom : rect.top, paint);
	}
}

