package org.unibl.etf.dayplanner.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.unibl.etf.dayplanner.R;
import org.unibl.etf.dayplanner.adapters.Adapter;
import org.unibl.etf.dayplanner.db.DayPlannerDB;
import org.unibl.etf.dayplanner.db.model.Activity;
import org.unibl.etf.dayplanner.ui.activities.ShowDetailsActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CalendarFragment extends Fragment {

    private Date from, to;
    private RecyclerView recyclerView;
    private TextView tvNoActivities;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        CalendarView calendarView = view.findViewById(R.id.calendar_view);
        tvNoActivities = view.findViewById(R.id.tv_no_activities);

        Date currentDate = Calendar.getInstance().getTime();

        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(currentDate);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);
        from = fromCalendar.getTime();

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(from);
        toCalendar.add(Calendar.DAY_OF_YEAR, 1);
        to = toCalendar.getTime();
        calendarView.setOnDateChangeListener((calendarView1, year, month, day) ->
                getForSelectedDate(year, month, day));

        recyclerView = view.findViewById(R.id.rv_activities);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        getActivities(from, to);
        return view;
    }

    private void getForSelectedDate(int year, int month, int day) {
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(year, month, day, 0, 0, 0);
        selectedDate.set(Calendar.MILLISECOND, 0);
        from = selectedDate.getTime();

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(from);
        toCalendar.add(Calendar.DAY_OF_YEAR, 1);
        toCalendar.add(Calendar.MILLISECOND, -1);
        to = toCalendar.getTime();

        getActivities(from, to);
    }

    @SuppressLint("SimpleDateFormat")
    private void getActivities(Date from, Date to) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        DayPlannerDB dayPlannerDB = DayPlannerDB.getInstance(requireContext());

        executorService.execute(() -> {
            List<Activity> activities = dayPlannerDB.getActivityDAO().getAllByDate(from, to);

            handler.post(() -> {
                String noActivities = getResources().getString(R.string.no_activities_today);
                String formattedDate = new SimpleDateFormat("MMMM dd, yyyy").format(from);
                if (activities.size() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    tvNoActivities.setText(
                            String.format(
                                    noActivities,
                                    formattedDate,
                                    getResources().getString(R.string.tv_no_activities)
                            )
                    );
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    tvNoActivities.setText(
                            String.format(
                                    noActivities,
                                    formattedDate,
                                    getResources().getString(R.string.number_of_activities_label, activities.size())
                            )
                    );
                    Adapter adapter = new Adapter(requireContext(), activities, index -> {
                        Intent intent = new Intent(requireContext(), ShowDetailsActivity.class);
                        intent.putExtra(getResources().getString(R.string.intent_activity_details), activities.get(index));
                        startActivity(intent);
                    });
                    recyclerView.setAdapter(adapter);
                }
            });
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (from != null && to != null)
            getActivities(from, to);
    }

}