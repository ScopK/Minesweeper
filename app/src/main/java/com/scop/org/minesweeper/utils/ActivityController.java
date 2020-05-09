package com.scop.org.minesweeper.utils;

import android.app.Activity;
import android.content.Intent;

import com.scop.org.minesweeper.GameActivity;
import com.scop.org.minesweeper.LoadingActivity;
import com.scop.org.minesweeper.elements.Grid;

public class ActivityController {

	public static void loadGrid(Grid grid, Activity activity) {
		boolean showLoading = grid.getGeneratorClass().contains("CheckedGenerator");

		if (showLoading) {
			grid.generate(()->{
				Activity loadingAct = LoadingActivity.getActiveActivity();
				Intent intent = new Intent(loadingAct, GameActivity.class);
				intent.putExtra("g", grid);
				loadingAct.startActivity(intent);
				loadingAct.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				loadingAct.finish();
			});
			Intent intent = new Intent(activity, LoadingActivity.class);
			activity.startActivity(intent);
			activity.overridePendingTransition(0,0);
			activity.finish();

		} else {

			grid.generate(()->{
				Intent intent = new Intent(activity, GameActivity.class);
				intent.putExtra("g", grid);
				activity.startActivity(intent);
				activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				activity.finish();
			});
		}
	}

}
