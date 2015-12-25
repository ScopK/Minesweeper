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

import com.scop.org.minesweeper.elements.TileStyle;

import java.io.File;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MGameActivity extends Activity {
    private GamePanel gamepanel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //TileStyle.getInstance().setStyle(this,"default", 4, -140f, 1.1f, 0xFF667788); // DefaultBG
        TileStyle.getInstance().setStyle(this,"default", 4, 0f, 1.0f, 0xFF3C3C3C); //

        gamepanel = new GamePanel(this);
        setContentView(gamepanel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        MenuItem m =  menu.findItem(R.id.loadlaststate);
        m.setEnabled(new File("/sdcard/Minesweeper/savesstate.save").exists());

        return super.onMenuOpened(featureId, menu);
    }

    @Override
    protected void onStop() {
        gamepanel.saveState();
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.new_game:
                gamepanel.restart();
                gamepanel.postInvalidate();
                return true;
            case R.id.saveclose:
                gamepanel.saveState();
                finish();
                return true;
            case R.id.loadlaststate:
                gamepanel.loadState();
                return true;
            case R.id.test:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
