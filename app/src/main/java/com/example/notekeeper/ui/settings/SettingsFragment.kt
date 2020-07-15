package com.example.notekeeper.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.notekeeper.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_main, rootKey)
    }
}