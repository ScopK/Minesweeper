package com.scop.org.minesweeper;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.view.KeyEvent;

import android.view.Window;
import android.view.WindowManager;



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
                gamepanel.setAndStart(extras.getInt("w"),extras.getInt("h"),extras.getInt("b"));
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
        finish();
    }
}
