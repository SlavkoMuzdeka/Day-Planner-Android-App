package org.unibl.etf.dayplanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.snackbar.Snackbar;

import org.unibl.etf.dayplanner.databinding.ActivityMainBinding;
import org.unibl.etf.dayplanner.db.DayPlannerDB;
import org.unibl.etf.dayplanner.db.model.Activity;
import org.unibl.etf.dayplanner.enums.NotificationOption;
import org.unibl.etf.dayplanner.ui.activities.CreateNewActivity;
import org.unibl.etf.dayplanner.ui.activities.UpcomingActivity;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        org.unibl.etf.dayplanner.databinding.ActivityMainBinding binding =
                ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_calendar, R.id.navigation_activities, R.id.navigation_settings)
                .build();
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        Button createActivityBtn = findViewById(R.id.btn_create_activity);
        navController.addOnDestinationChangedListener((navController1, navDestination, bundle) -> {
            if (navDestination.getId() == R.id.navigation_settings) {
                createActivityBtn.setVisibility(View.GONE);
            } else {
                createActivityBtn.setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.btn_create_activity).setOnClickListener(view -> {
            Intent intent = new Intent(this, CreateNewActivity.class);
            startActivity(intent);
        });

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String notOption = sharedPreferences.getString(getResources().getString(R.string.notification_preference_key), getResources().getString(R.string.notification_preference_default));
        NotificationOption option = NotificationOption.values()[Integer.parseInt(notOption)];
        if (option != NotificationOption.OFF) {
            sendNotification(option);
        }
    }

    private void sendNotification(NotificationOption option) {
        Calendar calendar = Calendar.getInstance();
        Date from = calendar.getTime();
        Date to;
        if (option == NotificationOption.HOUR)
            calendar.add(Calendar.HOUR, 1);
        else if (option == NotificationOption.DAY)
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        else
            calendar.add(Calendar.DAY_OF_YEAR, 7);
        to = calendar.getTime();
        getUpcomingActivities(from, to);
    }

    private void getUpcomingActivities(Date from, Date to) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        DayPlannerDB dayPlannerDB = DayPlannerDB.getInstance(this);

        executorService.execute(() -> {
            List<Activity> activities = dayPlannerDB.getActivityDAO().getAllByDate(from, to);
            handler.post(() -> {
                if (activities.size() > 0) {
                    TypedValue typedValue = new TypedValue();
                    int primaryColor = 0;
                    int colorOnPrimary = 0;
                    try {
                        TypedArray a = obtainStyledAttributes(
                                typedValue.data,
                                new int[]{
                                        com.google.android.material.R.attr.colorPrimary,
                                        com.google.android.material.R.attr.colorOnPrimary
                                }
                        );
                        primaryColor = a.getColor(0, 0);
                        colorOnPrimary = a.getColor(1, 0);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    Snackbar.make(
                                    findViewById(R.id.coordinator),
                                    getResources().getString(
                                            R.string.notifications_text,
                                            activities.size()
                                    ),
                                    Snackbar.LENGTH_LONG
                            )
                            .setBackgroundTint(primaryColor)
                            .setActionTextColor(colorOnPrimary)
                            .setAction(getResources().getString(R.string.snack_bar_btn), view -> {
                                Intent intent = new Intent(this, UpcomingActivity.class);
                                intent.putExtra(getResources().getString(R.string.intent_extra_activities), (Serializable) activities);
                                startActivity(intent);
                            }).show();
                }
            });
        });
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