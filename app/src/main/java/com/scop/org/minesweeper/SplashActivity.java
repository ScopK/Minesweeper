package com.scop.org.minesweeper;

import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.scop.org.minesweeper.control.ScreenProperties;
import com.scop.org.minesweeper.control.Settings;
import com.scop.org.minesweeper.elements.TileStyle;

public class SplashActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ScreenProperties.load(this);

		SharedPreferences preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
		Settings sets = Settings.getInstance();
		sets.setDiscoveryMode(Integer.parseInt(preferences.getString("option_reveal", "1")));
		sets.setShowTime(preferences.getBoolean("option_time", true));
		sets.setFirstOpen(preferences.getBoolean("option_firstopen", true));

		switch (preferences.getString("option_theme", "0")){
			case "0": TileStyle.getInstance().setStyle(this, "def", 4, 0f, 1.0f, 0xFF3C3C3C); break;
			case "1": TileStyle.getInstance().setStyle(this, "dots", 4, 0f, 1.0f, 0xFF3C3C3C); break;
			case "2": TileStyle.getInstance().setStyle(this, "dot", 4, 0f, 1.0f, 0xFF3C3C3C); break;
			case "3": TileStyle.getInstance().setStyle(this, "win", 1, 0f, 1.0f, 0xFFC0C0C0); break;
		}

		String fileSavePath = new ContextWrapper(SplashActivity.this).getFilesDir().getPath()+"/"+Settings.FILENAME;
		Intent intent;
		if (new java.io.File(fileSavePath).exists()){
			intent = new Intent(SplashActivity.this, GameActivity.class);
			intent.putExtra("l",true);

		} else {
			intent = new Intent(SplashActivity.this, MenuActivity.class);
		}

		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		finish();
	}
}