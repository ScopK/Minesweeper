package org.oar.minesweeper

import android.os.Bundle
import android.view.WindowInsets
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import org.oar.minesweeper.control.Settings
import org.oar.minesweeper.utils.PreferencesUtils.loadBoolean
import org.oar.minesweeper.utils.PreferencesUtils.loadInteger
import org.oar.minesweeper.utils.PreferencesUtils.save


class PreferencesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_preferences)
        window.insetsController?.hide(WindowInsets.Type.statusBars())

        val showTime = findViewById<SwitchMaterial>(R.id.showTime)
        val switchActions = findViewById<SwitchMaterial>(R.id.switchActions)
        val revealDisabled = findViewById<RadioButton>(R.id.revealDisable)
        val revealEnabled = findViewById<RadioButton>(R.id.revealEnable)
        val revealAuto = findViewById<RadioButton>(R.id.revealAuto)

        findViewById<TextView>(R.id.showTimeLabel).setOnClickListener { showTime.performClick() }
        findViewById<TextView>(R.id.switchActionsLabel).setOnClickListener { switchActions.performClick() }
        findViewById<TextView>(R.id.revealDisableLabel).setOnClickListener { revealDisabled.performClick() }
        findViewById<TextView>(R.id.revealEnableLabel).setOnClickListener { revealEnabled.performClick() }
        findViewById<TextView>(R.id.revealAutoLabel).setOnClickListener { revealAuto.performClick() }

        val showTimeValue = loadBoolean("showTime", true)
        val switchActionsValue = loadBoolean("switchActions", false)
        val revealValue = loadInteger("revealMode", 1)

        showTime.isChecked = showTimeValue
        showTime.setOnCheckedChangeListener { _, isChecked ->
            Settings.showTime = isChecked
            save("showTime", isChecked)
        }

        switchActions.isChecked = switchActionsValue
        switchActions.setOnCheckedChangeListener { _, isChecked ->
            Settings.switchActions = isChecked
            save("switchActions", isChecked)
        }

        revealDisabled.isChecked = revealValue == 0
        revealEnabled.isChecked = revealValue == 1
        revealAuto.isChecked = revealValue == 2
        revealDisabled.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Settings.discoveryMode = 0
                save("revealMode", 0)
            }
        }
        revealEnabled.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Settings.discoveryMode = 1
                save("revealMode", 1)
            }
        }
        revealAuto.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Settings.discoveryMode = 2
                save("revealMode", 2)
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
