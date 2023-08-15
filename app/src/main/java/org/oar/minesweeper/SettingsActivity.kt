package org.oar.minesweeper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.oar.minesweeper.control.Settings


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.settings_activity);

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(android.R.id.content, PrefsFragment())
                .commit()
        }

        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class PrefsFragment : PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.pref_general, rootKey)
            setHasOptionsMenu(true)

            val sets = Settings

            findPreference<Preference>("option_time")!!.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, value ->
                    val timeActivate = value as Boolean
                    sets.showTime = timeActivate
                    true
                }

            findPreference<Preference>("option_reveal")!!.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, value ->
                    val revealValue = value.toString().toInt()
                    sets.discoveryMode = revealValue
                    true
                }
        }
    }
}