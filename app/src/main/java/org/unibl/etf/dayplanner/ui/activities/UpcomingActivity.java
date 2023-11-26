package org.unibl.etf.dayplanner.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import org.unibl.etf.dayplanner.R;
import org.unibl.etf.dayplanner.adapters.Adapter;
import org.unibl.etf.dayplanner.db.model.Activity;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class UpcomingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming);

        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.upcoming_activity));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = findViewById(R.id.rv_activities);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Activity> activities = (List<Activity>) getIntent().getSerializableExtra(getResources().getString(R.string.intent_extra_activities));

        Adapter adapter = new Adapter(this, activities, index -> {
            Intent intent = new Intent(this, ShowDetailsActivity.class);
            assert activities != null;
            intent.putExtra(getResources().getString(R.string.intent_activity_details), activities.get(index));
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home)
            this.finish();
        return super.onOptionsItemSelected(menuItem);
    }

    private void setLocale(Context context) {
        SharedPreferences shPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String lang = shPreferences.getString("language", context.getResources().getString(R.string.language_preference_default));

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        context.getResources().updateConfiguration(
                configuration,
                context.getResources().getDisplayMetrics()
        );
    }

    @Override
    protected void attachBaseContext(Context base) {
        setLocale(base);
        super.attachBaseContext(base);
    }
}