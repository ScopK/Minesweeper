package com.scop.org.minesweeper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.scop.org.minesweeper.control.Settings;
import com.scop.org.minesweeper.elements.TileStyle;

public class MenuActivity extends Activity {
    private MenuPanel menupanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        menupanel = new MenuPanel(this);
        setContentView(menupanel);

        SharedPreferences preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        Settings sets = Settings.getInstance();
        sets.setDiscoveryMode(Integer.parseInt(preferences.getString("option_reveal", "0")));
        sets.setShowTime(preferences.getBoolean("option_time", true));
        sets.setFirstOpen(preferences.getBoolean("option_firstopen", true));

        switch (preferences.getString("option_theme", "0")){
            case "0": TileStyle.getInstance().setStyle(this, "default", 4, 0f, 1.0f, 0xFF3C3C3C); break;
            case "1": TileStyle.getInstance().setStyle(this, "win", 1, 0f, 1.0f, 0xFFC0C0C0); break;
        }

        if (new java.io.File(Settings.SAVE_STATE_PATH).exists()){
            loadGrid();
        }
    }

    public void startGrid(int w, int h, int b){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("w", w);
        intent.putExtra("h", h);
        intent.putExtra("b",b);
        startActivity(intent);
    }

    public void loadGrid(){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("l",true);
        startActivity(intent);
    }

    public void openSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
