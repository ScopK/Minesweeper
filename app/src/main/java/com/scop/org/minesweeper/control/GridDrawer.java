package com.scop.org.minesweeper.control;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.scop.org.minesweeper.skins.MainSkin;
import com.scop.org.minesweeper.elements.Grid;
import com.scop.org.minesweeper.elements.Tile;

public class GridDrawer {
	private static MainSkin skin;

	public static void setSkin(Context context, Class<? extends MainSkin> skinClass){
		try {
			skin = skinClass.newInstance();
			skin.load(context);

		} catch (IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
		}
	}


	public static void draw(CanvasWrapper canvasW, Tile t) {
		int tileSize = skin.getTileSize();
		Rect visibleSpace = canvasW.getVisibleSpace();

		Rect dim = new Rect();
		dim.left = t.getX() * tileSize;
		dim.right = dim.left + tileSize;
		dim.top = t.getY() * tileSize;
		dim.bottom = dim.top + tileSize;

		if (!visibleSpace.contains(dim) && !visibleSpace.intersects(dim.left, dim.top, dim.right, dim.bottom)){
			return;
		}

		Canvas canvas = canvasW.getCanvas();
		skin.setAlternative(t.hashCode());

		switch(t.getStatus()) {
			case COVERED:
				skin.drawCovered(canvas, t.getX() * tileSize, t.getY() * tileSize);
				break;
			case A0:
				skin.drawEmpty(canvas, t.getX() * tileSize, t.getY() * tileSize);
				break;
			case A1:
				skin.drawEmpty(canvas, t.getX() * tileSize, t.getY() * tileSize, 1, t.getFlaggedNear());
				break;
			case A2:
				skin.drawEmpty(canvas, t.getX() * tileSize, t.getY() * tileSize, 2, t.getFlaggedNear());
				break;
			case A3:
				skin.drawEmpty(canvas, t.getX() * tileSize, t.getY() * tileSize, 3, t.getFlaggedNear());
				break;
			case A4:
				skin.drawEmpty(canvas, t.getX() * tileSize, t.getY() * tileSize, 4, t.getFlaggedNear());
				break;
			case A5:
				skin.drawEmpty(canvas, t.getX() * tileSize, t.getY() * tileSize, 5, t.getFlaggedNear());
				break;
			case A6:
				skin.drawEmpty(canvas, t.getX() * tileSize, t.getY() * tileSize, 6, t.getFlaggedNear());
				break;
			case A7:
				skin.drawEmpty(canvas, t.getX() * tileSize, t.getY() * tileSize, 7, t.getFlaggedNear());
				break;
			case A8:
				skin.drawEmpty(canvas, t.getX() * tileSize, t.getY() * tileSize, 8, t.getFlaggedNear());
				break;
			case BOMB:
				skin.drawEmpty(canvas, t.getX() * tileSize, t.getY() * tileSize,0);
				break;
			case BOMB_FINAL:
				skin.drawEmpty(canvas, t.getX() * tileSize, t.getY() * tileSize,1);
				break;
			case FLAG:
				skin.drawCovered(canvas, t.getX() * tileSize, t.getY() * tileSize,2);
				break;
			case FLAG_FAIL:
				skin.drawCovered(canvas, t.getX() * tileSize, t.getY() * tileSize,3);
				break;
		}
	}


	public static void draw(CanvasWrapper canvasW, Grid g) {
		canvasW.getCanvas().drawColor(skin.getBgColor());
		for (Tile t : g.getGrid()) {
			draw(canvasW, t);
		}
	}

	public static int getTileSize(){
		return skin.getTileSize();
	}
}
