package com.scop.org.minesweeper.skins;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;

import java.util.HashMap;

public abstract class MainSkin {
	protected int bgColor = 0xFFFFFF00;
	protected boolean helpEnabled = false;
	protected Bitmap[] symbols = new Bitmap[4];
	protected HashMap<String,Bitmap> numbers = new HashMap<>();
	protected int tileSize = 128-1;
	protected Paint defaultPaint = new Paint();
	protected int alternative;

	public abstract void load(Context context);
	public abstract void drawCovered(Canvas canvas, float x, float y);
	public abstract void drawEmpty(Canvas canvas, float x, float y);

	public void drawCovered(Canvas canvas, float x, float y, int idx) {
		drawCovered(canvas, x, y);
		canvas.drawBitmap(symbols[idx], x, y, defaultPaint);
	}

	public void drawEmpty(Canvas canvas, float x, float y, int idx) {
		drawEmpty(canvas, x, y);
		canvas.drawBitmap(symbols[idx], x, y, defaultPaint);
	}

	public void drawEmpty(Canvas canvas, float x, float y, int numValue, int numMarked) {
		drawEmpty(canvas, x, y);
		if (helpEnabled) {
			if (numMarked > numValue+1) numMarked = numValue+1;
			canvas.drawBitmap(numbers.get(numValue+"_"+numMarked), x, y, defaultPaint);

		} else {
			canvas.drawBitmap(numbers.get(numValue+"_"+numValue), x, y, defaultPaint);
		}
	}


	protected Bitmap getBitmap(Context context, int id){
		return ((BitmapDrawable) context.getDrawable(id)).getBitmap();
	}

	protected Bitmap getBitmap(Context context, String name){
		int id = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
		return getBitmap(context, id);
	}






	public int getTileSize() {
		return tileSize;
	}

	public int getBgColor() {
		return bgColor;
	}

	public void setAlternative(int alternative) {
		this.alternative = alternative;
	}
}
