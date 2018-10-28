package com.scop.org.minesweeper;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.scop.org.minesweeper.control.ScreenProperties;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LoadingActivity extends Activity {

	private static Activity activeActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_loading);
		activeActivity = this;
	}

	public static Activity getActiveActivity(){
		return activeActivity;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		activeActivity = null;
	}
}
