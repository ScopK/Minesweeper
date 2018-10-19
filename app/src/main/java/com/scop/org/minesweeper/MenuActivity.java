package com.scop.org.minesweeper;

import android.app.Activity;
import android.content.ContextWrapper;
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
    }

    public void startGrid(int w, int h, int b){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("w", w);
        intent.putExtra("h", h);
        intent.putExtra("b",b);
        startActivity(intent);
        finish();
    }

    public void loadGrid(){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("l",true);
        startActivity(intent);
        finish();
    }

    public void openSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
