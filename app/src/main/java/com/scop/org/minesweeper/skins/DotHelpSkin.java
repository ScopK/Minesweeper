package com.scop.org.minesweeper.skins;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.scop.org.minesweeper.R;

public class DotHelpSkin extends MainSkin {
	private static final int ALTERNATIVES_MAX = 4;
	private Bitmap empty;
	private Bitmap[] covers;

	@Override
	public void load(Context context) {
		bgColor = 0xFF3C3C3C;
		empty = getBitmap(context, R.drawable.tile_skin_def_empty);

		symbols[0] = getBitmap(context, R.drawable.tile_skin_def_bomb);
		symbols[1] = getBitmap(context, R.drawable.tile_skin_def_bomb_end);
		symbols[2] = getBitmap(context, R.drawable.tile_skin_def_flag);
		symbols[3] = getBitmap(context, R.drawable.tile_skin_def_flag_fail);

		for (int i=1; i<9; i++) {
			for (int j=0; j<i+2; j++) {
				numbers.put(i+"_"+j,getBitmap(context, "tile_skin_dot_"+i+"_"+j));
			}
		}

		tileSize = symbols[0].getHeight();
		helpEnabled = true;

		Bitmap mainCover = getBitmap(context, R.drawable.tiles_def);
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
