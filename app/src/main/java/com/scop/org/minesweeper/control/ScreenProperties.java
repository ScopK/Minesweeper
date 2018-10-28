package com.scop.org.minesweeper.control;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;


public class ScreenProperties {
	public static int WIDTH, HEIGHT, HEIGHT_BAR_EXCLUDED;
	public static float DPI_W, DPI_H, DPI;

	private static float fontSizeBase;

	public static void load(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

		if (wm != null) {
			Display display = wm.getDefaultDisplay();

			Point size = new Point();
			display.getSize(size);

			DisplayMetrics metrics = new DisplayMetrics();
			display.getMetrics(metrics);

			WIDTH = size.x;
			HEIGHT = HEIGHT_BAR_EXCLUDED = size.y;
			DPI_W = metrics.xdpi;
			DPI_H = metrics.ydpi;
			DPI = (DPI_W+DPI_H)/2f;

			fontSizeBase = .14f*DPI;


			// Status Bar Height
			int statusBarHeight = 0;
			int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
			if (resourceId > 0) {
				statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
			}
			HEIGHT_BAR_EXCLUDED -= statusBarHeight;
		}
	}

	public static float dpiValue(float v){
		return v*DPI/100f;
	}

	public static float fontSizeAdapted(float fontSize){
		return fontSizeBase*fontSize/12f;
	}
}
