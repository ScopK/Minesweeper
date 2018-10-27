package com.scop.org.minesweeper.skins;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.scop.org.minesweeper.R;

public class NostalgiaSkin extends MainSkin {
	private static final int ALTERNATIVES_MAX = 1;
	private Bitmap empty;
	private Bitmap[] covers;

	@Override
	public void load(Context context) {
		bgColor = 0xFF3C3C3C;
		empty = getBitmap(context, R.drawable.tile_skin_win_empty);

		symbols[0] = getBitmap(context, R.drawable.tile_skin_win_bomb);
		symbols[1] = getBitmap(context, R.drawable.tile_skin_win_bomb_end);
		symbols[2] = getBitmap(context, R.drawable.tile_skin_win_flag);
		symbols[3] = getBitmap(context, R.drawable.tile_skin_win_flag_fail);

		for (int i=1; i<9; i++) {
			numbers.put(i+"_"+i,getBitmap(context, "tile_skin_win_"+i+"_"+i));
		}

		tileSize = symbols[0].getHeight();

		Bitmap mainCover = getBitmap(context, R.drawable.tiles_win);
		int coverH = mainCover.getHeight();
		int coverW = mainCover.getWidth()/ALTERNATIVES_MAX;
		covers = new Bitmap[ALTERNATIVES_MAX];
		for (int i = 0; i<ALTERNATIVES_MAX; i++) {
			covers[i] = Bitmap.createBitmap(mainCover, i*coverW, 0, coverW, coverH);
		}
	}

	@Override
	public void drawCovered(Canvas canvas, float x, float y) {
		int i = alternative % covers.length;
		canvas.drawBitmap(covers[i], x, y, defaultPaint);
	}

	@Override
	public void drawEmpty(Canvas canvas, float x, float y) {
		canvas.drawBitmap(empty, x, y, defaultPaint);
	}
}
