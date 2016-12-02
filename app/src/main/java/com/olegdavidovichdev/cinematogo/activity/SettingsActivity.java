package com.olegdavidovichdev.cinematogo.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;

import com.olegdavidovichdev.cinematogo.R;

public class SettingsActivity extends PreferenceActivity {

    private AppCompatDelegate appCompatDelegate;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);

        toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        getDelegate().setSupportActionBar(toolbar);

        onCreatePreferencesFragment();
    }

    private void onCreatePreferencesFragment() {
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref);
        }
    }


    private AppCompatDelegate getDelegate() {
        if (appCompatDelegate == null) {
            appCompatDelegate = AppCompatDelegate.create(this, null);
        }
        return appCompatDelegate;
    }
}
