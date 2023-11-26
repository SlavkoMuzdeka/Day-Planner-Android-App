package org.unibl.etf.dayplanner.ui.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.aemerse.slider.ImageCarousel;
import com.aemerse.slider.model.CarouselItem;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import org.unibl.etf.dayplanner.BuildConfig;
import org.unibl.etf.dayplanner.R;
import org.unibl.etf.dayplanner.db.DayPlannerDB;
import org.unibl.etf.dayplanner.db.dao.ActivityImageJoinDAO;
import org.unibl.etf.dayplanner.db.enums.ActivityType;
import org.unibl.etf.dayplanner.db.model.ActivityImageJoin;
import org.unibl.etf.dayplanner.db.model.Image;
import org.unibl.etf.dayplanner.ui.fragments.ModalBottomSheetFragment;
import org.unibl.etf.dayplanner.utils.ImageSelectionListener;
import org.unibl.etf.dayplanner.views.CreateNewActivityViewModel;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class CreateNewActivity extends AppCompatActivity implements ImageSelectionListener {

    private Date date;
    private Integer hours, minutes;
    private ActivityType type = ActivityType.TRAVEL;
    private Place selectedLocation;
    private DayPlannerDB dayPlannerDB;
    private CreateNewActivityViewModel viewModel;

    private TextInputLayout tilTitle;
    private TextInputEditText titleText;
    private TextInputLayout tilDescription;
    private TextInputEditText descriptionText;
    private MaterialButtonToggleGroup btnToggleGroup;
    private TextInputLayout tilLocation;
    private TextInputEditText locationText;
    private TextInputLayout tilDate;
    private TextInputEditText dateText;
    private TextInputLayout tilTime;
    private TextInputEditText timeText;
    private ImageCarousel carousel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.title_activity_create_new));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dayPlannerDB = DayPlannerDB.getInstance(this);

        Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
        Places.createClient(this);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String inputTitle = String.valueOf(titleText.getText()).trim();
                String inputDescription = String.valueOf(descriptionText.getText()).trim();
                String inputLocation = String.valueOf(locationText.getText()).trim();
                String inputDate = String.valueOf(dateText.getText()).trim();
                String inputTime = String.valueOf(timeText.getText()).trim();

                if (!inputTitle.isEmpty())
                    tilTitle.setError(null);
                if (!inputDescription.isEmpty())
                    tilDescription.setError(null);
                if (!inputLocation.isEmpty())
                    tilLocation.setError(null);
                if (!inputDate.isEmpty())
                    tilDate.setError(null);
                if (!inputTime.isEmpty())
                    tilTime.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String inputTitle = String.valueOf(titleText.getText()).trim();
                String inputDescription = String.valueOf(descriptionText.getText()).trim();

                if (inputTitle.length() > 30)
                    tilTitle.setError(getResources().getString(R.string.title_length_error));
                if (inputDescription.length() > 500)
                    tilDescription.setError(
                            getResources().getString(R.string.description_length_error)
                    );
            }
        };

        tilTitle = findViewById(R.id.til_title);
        titleText = findViewById(R.id.title_text);
        titleText.addTextChangedListener(textWatcher);

        tilDescription = findViewById(R.id.til_description);
        descriptionText = findViewById(R.id.description_text);
        descriptionText.addTextChangedListener(textWatcher);

        tilLocation = findViewById(R.id.til_location);
        tilLocation.setStartIconOnClickListener((view) -> showMap());
        locationText = findViewById(R.id.location_text);
        locationText.addTextChangedListener(textWatcher);

        tilDate = findViewById(R.id.til_date);
        tilDate.setStartIconOnClickListener((view) -> showDataPicker());
        dateText = findViewById(R.id.date_text);
        dateText.addTextChangedListener(textWatcher);

        tilTime = findViewById(R.id.til_time);
        tilTime.setStartIconOnClickListener((view) -> showTimePicker());
        timeText = findViewById(R.id.time_text);
        timeText.addTextChangedListener(textWatcher);

        btnToggleGroup = findViewById(R.id.btn_toggle_group);
        btnToggleGroup.addOnButtonCheckedListener(this::setActivityType);

        viewModel = new ViewModelProvider(this).get(CreateNewActivityViewModel.class);

        carousel = findViewById(R.id.carousel);
        carousel.registerLifecycle(getLifecycle());

        if(viewModel.getImages().size() > 0) {
            carousel.setVisibility(View.VISIBLE);
            carousel.setData(viewModel.getImages());
        }
        findViewById(R.id.btn_add_images).setOnClickListener(this::handleClickOnAddImages);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home)
            this.finish();
        else if (menuItem.getItemId() == R.id.action_save)
            createActivity();
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_new_activity_menu, menu);
        return true;
    }

    @Override
    public void onImageSelected(String imagePath) {
        if (imagePath != null) {
            viewModel.addImage(new CarouselItem(imagePath));
            carousel.setData(viewModel.getImages());
        }
        if(viewModel.getImages().size() > 0)
            findViewById(R.id.carousel).setVisibility(View.VISIBLE);
    }

    private void showDataPicker() {
        MaterialDatePicker<Long> datePicker =
                MaterialDatePicker.Builder.datePicker().setTitleText(R.string.select_date).build();
        datePicker.show(getSupportFragmentManager(), "DatePicker");

        datePicker.addOnPositiveButtonClickListener(selection -> {
            this.date = new Date(selection);
            dateText.setText(datePicker.getHeaderText());
        });
    }

    private void showTimePicker() {
        MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H).setTitleText(R.string.select_time).build();
        timePicker.show(getSupportFragmentManager(), "TimePicker");

        timePicker.addOnPositiveButtonClickListener(selection -> {
            this.hours = timePicker.getHour();
            this.minutes = timePicker.getMinute();
            timeText.setText(String.format(Locale.getDefault(), "%02d:%02d", hours, minutes));
        });
    }

    private void showMap() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startAutocomplete.launch(intent);
    }

    private final ActivityResultLauncher<Intent> startAutocomplete = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        selectedLocation = Autocomplete.getPlaceFromIntent(intent);
                        locationText.setText(selectedLocation.getName());
                    }
                }
            }
    );

    private void setActivityType(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
        if (isChecked) {
            findViewById(R.id.error_message).setVisibility(View.GONE);
            Button addImages = findViewById(R.id.btn_add_images);
            if (checkedId == R.id.btn_travel) {
                type = ActivityType.TRAVEL;
                addImages.setVisibility(View.GONE);
                carousel.setVisibility(View.GONE);
                viewModel.getImages().clear();
            } else if (checkedId == R.id.btn_work) {
                type = ActivityType.WORK;
                addImages.setVisibility(View.GONE);
                carousel.setVisibility(View.GONE);
                viewModel.getImages().clear();
            } else if (checkedId == R.id.btn_free_time) {
                type = ActivityType.FREE_TIME;
                addImages.setVisibility(View.VISIBLE);
            }
        }
    }

    private void handleClickOnAddImages(View view) {
        ModalBottomSheetFragment modalBottomSheet = new ModalBottomSheetFragment();
        modalBottomSheet.show(getSupportFragmentManager(), ModalBottomSheetFragment.TAG);
    }

    private void createActivity() {
        if (validateFields()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, hours);
            calendar.set(Calendar.MINUTE, minutes);
            date = calendar.getTime();
            if (selectedLocation.getLatLng() != null) {
                org.unibl.etf.dayplanner.db.model.Activity activity =
                        new org.unibl.etf.dayplanner.db.model.Activity(
                                String.valueOf(titleText.getText()),
                                date,
                                String.valueOf(descriptionText.getText()),
                                selectedLocation.getName(),
                                selectedLocation.getLatLng().latitude,
                                selectedLocation.getLatLng().longitude,
                                type
                        );
                List<Image> imagesToInsert = viewModel.getImages().stream().map(im -> {
                    Image image = new Image();
                    image.setUri(im.getImageUrl());
                    return image;
                }).collect(Collectors.toList());
                insertActivity(activity, imagesToInsert);
            }
        }
    }

    private boolean validateFields() {
        boolean isValid = true;

        String inputTitle = (String.valueOf(titleText.getText())).trim();
        if (inputTitle.isEmpty()) {
            tilTitle.setError(getResources().getString(R.string.title_error));
            isValid = false;
        } else if (inputTitle.length() > 30) {
            tilTitle.setError(getResources().getString(R.string.title_length_error));
            isValid = false;
        }

        String inputDescription = (String.valueOf(descriptionText.getText())).trim();
        if (inputDescription.isEmpty()) {
            tilDescription.setError(getResources().getString(R.string.description_error));
            isValid = false;
        } else if (inputDescription.length() > 500) {
            tilDescription.setError(getResources().getString(R.string.description_length_error));
            isValid = false;
        }

        String inputLocation = (String.valueOf(locationText.getText())).trim();
        if (inputLocation.isEmpty()) {
            tilLocation.setError(getResources().getString(R.string.location_error));
            isValid = false;
        }

        String inputDate = (String.valueOf(dateText.getText())).trim();
        if (inputDate.isEmpty()) {
            tilDate.setError(getResources().getString(R.string.date_error));
            isValid = false;
        }

        String inputTime = (String.valueOf(timeText.getText())).trim();
        if (inputTime.isEmpty()) {
            tilTime.setError(getResources().getString(R.string.time_error));
            isValid = false;
        }

        if (btnToggleGroup.getCheckedButtonId() == View.NO_ID) {
            findViewById(R.id.error_message).setVisibility(View.VISIBLE);
            isValid = false;
        }

        return isValid;
    }



    private void insertActivity(org.unibl.etf.dayplanner.db.model.Activity activity,
                                List<Image> imagesToInsert) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            long activityId = dayPlannerDB.getActivityDAO().insert(activity);
            List<Long> insertedImages = dayPlannerDB.getImageDAO().insertImages(imagesToInsert);
            ActivityImageJoinDAO activityImageJoinDAO = dayPlannerDB.getActivityJoinDAO();

            for (Long imageId : insertedImages) {
                ActivityImageJoin join = new ActivityImageJoin();
                join.setActivityId(activityId);
                join.setImageId(imageId);
                activityImageJoinDAO.insert(join);
            }

            handler.post(() -> {
                Toast.makeText(this, R.string.activity_created, Toast.LENGTH_SHORT).show();
                finish();
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