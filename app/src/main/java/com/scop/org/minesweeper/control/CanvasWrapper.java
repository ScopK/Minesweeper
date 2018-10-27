package com.scop.org.minesweeper.control;

import android.graphics.Canvas;
import android.graphics.Rect;

import com.scop.org.minesweeper.elements.Tile;

public class CanvasWrapper {
	private static float posX = 0;
	private static float posY = 0;
	private static float scale = ScreenProperties.dpiValue(.2491268f);
	private static final float minScale = ScreenProperties.dpiValue(.05f);
	private static final float maxScale = ScreenProperties.dpiValue(.8f);
	private static float contentWidth;
	private static float contentHeight;

	private Canvas canvas;
	private Rect visibleSpace;

	public CanvasWrapper(Canvas canvas) {
		this.canvas = canvas;
		canvas.save();

		canvas.translate(posX,posY);
		canvas.scale(scale, scale);

		visibleSpace = new Rect();
		visibleSpace.left = (int) (-posX/scale);
		visibleSpace.right = (int) ((-posX+ScreenProperties.WIDTH) / scale);
		visibleSpace.top = (int) (-posY/scale);
		visibleSpace.bottom = (int) ((-posY+ScreenProperties.HEIGHT) / scale);
	}

	public void end(){
		canvas.restore();
	}

	public static void translate(float x, float y) {
		posX += x;
		posY += y;

		// Check limits:
		float widthHalf = ScreenProperties.WIDTH/2f;
		float heightHalf = ScreenProperties.HEIGHT/2f;

		if (posX > widthHalf) posX = widthHalf;
		else if (posX < (widthHalf-contentWidth*scale)) posX = widthHalf-contentWidth*scale;

		if (posY > heightHalf) posY = heightHalf;
		else if (posY < (heightHalf-contentHeight*scale)) posY = heightHalf-contentHeight*scale;
	}

	public static void zoom(float z){
		float newScale = scale*z;
		newScale = Math.max(minScale, Math.min(newScale, maxScale));

		z = newScale / scale;
		scale = newScale;

		// Use center screen as anchor:
		float widthHalf = ScreenProperties.WIDTH/2f;
		posX = widthHalf - (widthHalf - posX)*z;

		float heightHalf = ScreenProperties.HEIGHT/2f;
		posY = heightHalf - (heightHalf - posY)*z;
	}

	public static void set(float posX, float posY, float scale){
		CanvasWrapper.posX = posX;
		CanvasWrapper.posY = posY;
		CanvasWrapper.scale = scale;
	}

	public static void focus(int x, int y) {
		int tileSize = GridDrawer.getTileSize();

		posX = ScreenProperties.WIDTH/2f-(x+.5f)*tileSize*scale;
		posY = ScreenProperties.HEIGHT/2f-(y+.5f)*tileSize*scale;
	}

	public static void focus(Tile t) {
		focus(t.getX(), t.getY());
	}

	public static void setContentDimensions(float w, float h){
		contentWidth = w;
		contentHeight = h;
		translate(0,0);
	}

	public static float getPosX() {
		return posX;
	}

	public static float getPosY() {
		return posY;
	}

	public static float getScale() {
		return scale;
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public Rect getVisibleSpace() {
		return visibleSpace;
	}
}
