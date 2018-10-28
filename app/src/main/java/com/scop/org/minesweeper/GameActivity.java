package com.scop.org.minesweeper;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.scop.org.minesweeper.control.ScreenProperties;
import com.scop.org.minesweeper.elements.Grid;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameActivity extends Activity {
    private GamePanel gamepanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Bundle extras = this.getIntent().getExtras();
        if (extras==null){
            try {
                this.finalize();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        } else {
            gamepanel = new GamePanel(this);
            setContentView(gamepanel);

	        if (extras.getBoolean("l")){
		        if (!gamepanel.loadState()){
			        this.finish();
		        }
	        } else {
		        Grid grid = (Grid) extras.getSerializable("g");
		        gamepanel.setNewGrid(grid);
		        gamepanel.postInvalidate();
	        }
        }
    }

    @Override
    protected void onPause() {
        gamepanel.saveState();
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_MENU){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
	    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
