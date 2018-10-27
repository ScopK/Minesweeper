package com.scop.org.minesweeper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.scop.org.minesweeper.generators.RandomGenerator;
import com.scop.org.minesweeper.elements.Grid;

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
	    Grid grid = new Grid(w, h, b, RandomGenerator.class);
	    Intent intent = new Intent(this, GameActivity.class);
	    intent.putExtra("g", grid);
	    startActivity(intent);
	    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	    finish();
    }

    public void loadGrid(){
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("l",true);
        startActivity(intent);
	    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    public void openSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
