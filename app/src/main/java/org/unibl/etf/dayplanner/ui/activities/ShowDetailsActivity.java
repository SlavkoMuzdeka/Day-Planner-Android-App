package org.unibl.etf.dayplanner.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.aemerse.slider.ImageCarousel;
import com.aemerse.slider.model.CarouselItem;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.unibl.etf.dayplanner.R;
import org.unibl.etf.dayplanner.db.DayPlannerDB;
import org.unibl.etf.dayplanner.db.dao.ActivityImageJoinDAO;
import org.unibl.etf.dayplanner.db.dao.ImageDAO;
import org.unibl.etf.dayplanner.db.enums.ActivityType;
import org.unibl.etf.dayplanner.db.model.Activity;
import org.unibl.etf.dayplanner.db.model.ActivityImageJoin;
import org.unibl.etf.dayplanner.db.model.Image;
import org.unibl.etf.dayplanner.views.CreateNewActivityViewModel;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.List;

public class ShowDetailsActivity extends AppCompatActivity {

    private Activity activity;
    private DayPlannerDB dayPlannerDB;
    private Chip chipType;
    private CreateNewActivityViewModel viewModel;
    private ImageCarousel carousel;
    private List<Image> images;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_details);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.title_show_details_activity));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.dayPlannerDB = DayPlannerDB.getInstance(this);
        this.activity = (Activity) getIntent().getSerializableExtra(getResources().getString(R.string.intent_activity_details));

        chipType = findViewById(R.id.chip_type);
        Chip chipDate = findViewById(R.id.chip_date);
        Chip chipTime = findViewById(R.id.chip_time);
        Chip chipLocation = findViewById(R.id.chip_location);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm");

        chipDate.setChipIconResource(R.drawable.ic_calendar);
        chipDate.setText(simpleDateFormat.format(activity.getDateTime()));

        chipTime.setChipIconResource(R.drawable.ic_time);
        chipTime.setText(simpleTimeFormat.format(activity.getDateTime()));

        chipLocation.setChipIconResource(R.drawable.ic_location);
        chipLocation.setText(activity.getLocationName());
        chipLocation.setOnClickListener((v) -> showActivityLocation());

        ((TextView) findViewById(R.id.tv_title)).setText(activity.getTitle());
        ((TextView) findViewById(R.id.tv_description)).setText(activity.getDescription());
        findViewById(R.id.btn_delete_activity).setOnClickListener((v) -> showMaterialDialog());

        viewModel = new ViewModelProvider(this).get(CreateNewActivityViewModel.class);
        carousel = findViewById(R.id.carousel);
        carousel.registerLifecycle(getLifecycle());

        getActivityImages();
        setActivityType();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home)
            this.finish();
        return super.onOptionsItemSelected(menuItem);
    }

    private void getActivityImages() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> {
            images = dayPlannerDB.getImageDAO().getImagesForActivity(activity.getId());
            viewModel.setImages(images.stream().map(image -> new CarouselItem(image.getUri()))
                            .collect(Collectors.toList()));

            handler.post(() -> {
                if (viewModel.getImages().size() > 0 && activity.getActivityType() == ActivityType.FREE_TIME) {
                    carousel.setVisibility(View.VISIBLE);
                    carousel.setData(viewModel.getImages());
                }
            });
        });
    }

    private void setActivityType() {
        ActivityType activityType = activity.getActivityType();
        if (activityType == ActivityType.FREE_TIME) {
            chipType.setChipIconResource(R.drawable.ic_free_time);
            chipType.setText(getResources().getString(R.string.activity_type_free_time));
        } else if (activityType == ActivityType.WORK) {
            chipType.setText(getResources().getString(R.string.activity_type_work));
            chipType.setChipIconResource(R.drawable.ic_work);
        } else if (activityType == ActivityType.TRAVEL) {
            chipType.setText(getResources().getString(R.string.activity_type_travel));
            chipType.setChipIconResource(R.drawable.ic_travel);
        }
    }

    private void showActivityLocation() {
        Intent intent = new Intent(this, MapViewActivity.class);
        intent.putExtra("latitude", activity.getLocationLatitude());
        intent.putExtra("longitude", activity.getLocationLongitude());
        intent.putExtra("locationName", activity.getLocationName());
        startActivity(intent);
    }

    private void showMaterialDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                .setTitle(getResources().getString(R.string.alert_delete_title))
                .setMessage(R.string.alert_delete_message)
                .setPositiveButton("OK", (dialog, which) -> delete())
                .setNegativeButton("Cancel", null);
        builder.show();
    }

    private void delete() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        ActivityImageJoinDAO activityImageJoinDAO = dayPlannerDB.getActivityJoinDAO();
        ImageDAO imageDAO = dayPlannerDB.getImageDAO();

        executorService.execute(() -> {
            for(Image image: images) {
                ActivityImageJoin imageJoinDAO = new ActivityImageJoin();
                imageJoinDAO.setActivityId(activity.getId());
                imageJoinDAO.setImageId(image.getId());
                activityImageJoinDAO.delete(imageJoinDAO);
                imageDAO.delete(image);
            }
            dayPlannerDB.getActivityDAO().delete(activity);
        });

        handler.post(() -> {
            setResult(RESULT_OK);
            finish();
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