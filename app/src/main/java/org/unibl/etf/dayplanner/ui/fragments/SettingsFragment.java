package org.unibl.etf.dayplanner.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import org.unibl.etf.dayplanner.R;

import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat
        implements androidx.preference.Preference.OnPreferenceChangeListener {

    @Override
    public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
        if (preference.getKey().equals(requireContext().getResources().getString(R.string.language_preference_key))) {
            Intent intent = requireActivity().getIntent();
            requireActivity().finish();
            startActivity(intent);
        }
        return true;
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        findPreference(requireContext().getResources().getString(R.string.language_preference_key)).setOnPreferenceChangeListener(this);
    }

    private void setLocale(String language, Context context) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);

        context.getResources().updateConfiguration(
                configuration,
                context.getResources().getDisplayMetrics()
        );
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String language = sharedPreferences.getString("language", context.getResources().getString(R.string.language_preference_default));
        setLocale(language, context);
    }
}