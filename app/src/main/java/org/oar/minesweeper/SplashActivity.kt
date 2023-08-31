package org.oar.minesweeper

import android.annotation.SuppressLint
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.oar.minesweeper.grid.GridDrawer
import org.oar.minesweeper.utils.ScreenProperties
import org.oar.minesweeper.control.Settings
import org.oar.minesweeper.skins.*
import org.oar.minesweeper.utils.ActivityUtils.animateStartActivity
import org.oar.minesweeper.utils.PreferencesUtils.loadBoolean
import org.oar.minesweeper.utils.PreferencesUtils.loadInteger
import org.oar.minesweeper.utils.PreferencesUtils.loadString
import java.io.File

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

        ScreenProperties.load(this);

        Settings.apply {
            discoveryMode = loadInteger("revealMode", 1)
            showTime = loadBoolean("showTime", true)
            switchActions = loadBoolean("switchActions", false)
        }

        val lastCoverHue = loadInteger("lastCoverHue", 0)

        when (loadInteger("skin")){
            0 -> GridDrawer.setSkin(this, DefaultSkin::class, lastCoverHue)
            1 -> GridDrawer.setSkin(this, DotSkin::class, lastCoverHue)
            2 -> GridDrawer.setSkin(this, DotAltSkin::class, lastCoverHue)
            3 -> GridDrawer.setSkin(this, WinSkin::class, lastCoverHue)
        }

        val fileSavePath = ContextWrapper(this).filesDir.path +"/"+Settings.FILENAME

        val intent: Intent
        if (File(fileSavePath).exists()){
            intent = Intent(this, GameActivity::class.java)
            intent.putExtra("l",true);

        } else {
            intent = Intent(this, MenuActivity::class.java)
        }

        animateStartActivity(intent, true)
	}
}