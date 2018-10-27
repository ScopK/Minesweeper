package com.scop.org.minesweeper;

import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.scop.org.minesweeper.control.ScreenProperties;
import com.scop.org.minesweeper.control.Settings;
import com.scop.org.minesweeper.control.GridDrawer;
import com.scop.org.minesweeper.skins.DefaultDotSkin;
import com.scop.org.minesweeper.skins.DefaultNumberSkin;
import com.scop.org.minesweeper.skins.DotHelpSkin;
import com.scop.org.minesweeper.skins.NostalgiaSkin;

public class SplashActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ScreenProperties.load(this);
		GridDrawer.setSkin(this, DotHelpSkin.class);

		SharedPreferences preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
		Settings sets = Settings.getInstance();
		sets.setDiscoveryMode(Integer.parseInt(preferences.getString("option_reveal", "1")));
		sets.setShowTime(preferences.getBoolean("option_time", true));
		sets.setFirstOpen(preferences.getBoolean("option_firstopen", true));

		switch (preferences.getString("option_theme", "0")){
			case "0": GridDrawer.setSkin(this, DefaultNumberSkin.class); break;
			case "1": GridDrawer.setSkin(this, DefaultDotSkin.class); break;
			case "2": GridDrawer.setSkin(this, DotHelpSkin.class); break;
			case "3": GridDrawer.setSkin(this, NostalgiaSkin.class); break;
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