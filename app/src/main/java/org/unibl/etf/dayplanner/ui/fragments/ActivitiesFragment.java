package org.unibl.etf.dayplanner.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.unibl.etf.dayplanner.R;
import org.unibl.etf.dayplanner.db.DayPlannerDB;
import org.unibl.etf.dayplanner.db.model.Activity;
import org.unibl.etf.dayplanner.adapters.Adapter;
import org.unibl.etf.dayplanner.ui.activities.ShowDetailsActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActivitiesFragment extends Fragment {

    private RecyclerView recyclerView;
    private DayPlannerDB dayPlannerDB;
    private TextView textViewNoActivities;
    private String previousQuery = "";
    LinearProgressIndicator progressIndicator;
    private boolean temp = false;

    private static final String GET_ALL_ORDER_BY_DATE = "GET_ALL_ORDER_BY_DATE";
    private static final String GET_ALL_BY_TITLE = "GET_ALL_BY_TITLE";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_activities, container, false);
        textViewNoActivities = rootView.findViewById(R.id.tv_no_activities);
        recyclerView = rootView.findViewById(R.id.rv_activities);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dayPlannerDB = DayPlannerDB.getInstance(getContext());
        progressIndicator = rootView.findViewById(R.id.progress_indicator);
        getActivities(GET_ALL_ORDER_BY_DATE, "");

        SearchView searchView = rootView.findViewById(R.id.sv_activity);
        searchView.setOnQueryTextListener(handleSearchActivity);
        return rootView;
    }

    private void getActivities(String command, String query) {
        progressIndicator.setVisibility(View.VISIBLE);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            List<Activity> activities;
            if (GET_ALL_ORDER_BY_DATE.equals(command)) {
                activities = dayPlannerDB.getActivityDAO().getAllByDate(new Date());
            } else if (GET_ALL_BY_TITLE.equals(command)) {
                activities = dayPlannerDB.getActivityDAO().getAllByTitle(query, new Date());
            } else {
                activities = new ArrayList<>();
            }

            handler.post(() -> {
                Adapter adapter = new Adapter(requireContext(), activities, index -> {
                    Intent intent = new Intent(getContext(), ShowDetailsActivity.class);
                    intent.putExtra(getResources().getString(R.string.intent_activity_details), activities.get(index));
                    startActivity(intent);
                });
                recyclerView.setAdapter(adapter);
                progressIndicator.setVisibility(View.GONE);

                if (activities.size() > 0)
                    textViewNoActivities.setVisibility(View.GONE);
                else
                    textViewNoActivities.setVisibility(View.VISIBLE);
            });
        });
    }

    SearchView.OnQueryTextListener handleSearchActivity = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (!newText.equals(previousQuery)) {
                previousQuery = newText;
                getActivities(GET_ALL_BY_TITLE, newText);
            }
            return true;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if(!temp)
            temp = true;
        else {
            if(!Objects.equals(previousQuery, ""))
                getActivities(GET_ALL_BY_TITLE, previousQuery);
            else
                getActivities(GET_ALL_ORDER_BY_DATE, "");
        }
    }

}