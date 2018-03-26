package com.scop.org.minesweeper;



import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;


import com.scop.org.minesweeper.control.Settings;
import com.scop.org.minesweeper.elements.TileStyle;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment(this)).commit();
    }


    public static class PrefsFragment extends PreferenceFragment {
        private Context context;
        public PrefsFragment(Context context){
            this.context = context;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            final Settings sets = Settings.getInstance();

            findPreference("option_time").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    boolean timeActivate = (boolean) value;
                    sets.setShowTime(timeActivate);
                    return true;
                }
            });
            findPreference("option_firstopen").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    boolean firstOpen = (boolean) value;
                    sets.setFirstOpen(firstOpen);
                    return true;
                }
            });
            findPreference("option_reveal").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    int revealValue = Integer.parseInt(value.toString());
                    sets.setDiscoveryMode(revealValue);
                    return true;
                }
            });
            findPreference("option_theme").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    switch (value.toString()){
                        case "0": TileStyle.getInstance().setStyle(context, "def", 4, 0f, 1.0f, 0xFF3C3C3C); break;
                        case "1": TileStyle.getInstance().setStyle(context, "dots", 4, 0f, 1.0f, 0xFF3C3C3C); break;
                        case "2": TileStyle.getInstance().setStyle(context, "dot", 4, 0f, 1.0f, 0xFF3C3C3C); break;
                        case "3": TileStyle.getInstance().setStyle(context, "win", 1, 0f, 1.0f, 0xFFC0C0C0); break;
                    }
                    return true;
                }
            });
        }
    }

}
