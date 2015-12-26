package com.scop.org.minesweeper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.scop.org.minesweeper.control.Settings;
import com.scop.org.minesweeper.elements.TileStyle;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameActivity extends Activity {
    private GamePanel gamepanel;
    private MenuPanel menupanel;

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
                gamepanel.loadState();
            } else {
                gamepanel.setAndStart(extras.getInt("w"),extras.getInt("h"),extras.getInt("b"));
            }
        }
    }

    @Override
    protected void onStop() {
        gamepanel.saveState();
        super.onStop();
    }

}
