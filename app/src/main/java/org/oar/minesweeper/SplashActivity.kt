package org.oar.minesweeper

import android.annotation.SuppressLint
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import org.oar.minesweeper.control.GridDrawer
import org.oar.minesweeper.control.ScreenProperties
import org.oar.minesweeper.control.Settings
import org.oar.minesweeper.skins.*
import org.oar.minesweeper.skins.DefaultSkin
import org.oar.minesweeper.skins.DotSkin
import org.oar.minesweeper.skins.WinSkin
import org.oar.minesweeper.utils.PreferencesUtils.loadInteger
import java.io.File

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

        ScreenProperties.load(this);

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        Settings.apply {
            discoveryMode = preferences.getString("option_reveal", "1")?.toInt() ?: 1
            showTime = preferences.getBoolean("option_time", true)
        }

        when (loadInteger("skin")){
            0 -> GridDrawer.setSkin(this, DefaultSkin::class)
            1 -> GridDrawer.setSkin(this, DotSkin::class)
            2 -> GridDrawer.setSkin(this, DotAltSkin::class)
            3 -> GridDrawer.setSkin(this, WinSkin::class)
        }

        val fileSavePath = ContextWrapper(this).filesDir.path +"/"+Settings.FILENAME

        val intent: Intent
        if (File(fileSavePath).exists()){
            intent = Intent(this@SplashActivity, GameActivity::class.java)
            intent.putExtra("l",true);

        } else {
            intent = Intent(this@SplashActivity, MenuActivity::class.java)
        }

        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
	}
}