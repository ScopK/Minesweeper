package org.oar.minesweeper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import org.oar.minesweeper.control.GridDrawer.setSkin
import org.oar.minesweeper.control.Settings
import org.oar.minesweeper.skins.DefaultDotSkin
import org.oar.minesweeper.skins.DefaultNumberSkin
import org.oar.minesweeper.skins.DotHelpSkin
import org.oar.minesweeper.skins.NostalgiaSkin

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

            findPreference<Preference>("option_theme")!!.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, value ->
                    when (value.toString()) {
                        "0" -> setSkin(requireContext(), DefaultNumberSkin::class)
                        "1" -> setSkin(requireContext(), DefaultDotSkin::class)
                        "2" -> setSkin(requireContext(), DotHelpSkin::class)
                        "3" -> setSkin(requireContext(), NostalgiaSkin::class)
                    }
                    true
                }
        }
    }
}