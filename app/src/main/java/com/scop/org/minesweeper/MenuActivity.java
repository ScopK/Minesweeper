package com.scop.org.minesweeper;

import android.app.Activity;
import android.content.Intent;
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

        //TileStyle.getInstance().setStyle(this,"default", 4, -140f, 1.1f, 0xFF667788); // DefaultBG
        TileStyle.getInstance().setStyle(this, "default", 4, 0f, 1.0f, 0xFF3C3C3C); //

        menupanel = new MenuPanel(this);
        setContentView(menupanel);

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
        System.err.println("Settings");
    }
}
