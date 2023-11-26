package org.unibl.etf.dayplanner.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import org.unibl.etf.dayplanner.R;
import org.unibl.etf.dayplanner.db.enums.ActivityType;
import org.unibl.etf.dayplanner.db.model.Activity;

import java.text.SimpleDateFormat;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    public interface OnActivityClickListener {
        void onActivityClick(Integer index);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public Chip chipType;
        public Chip chipDate;
        public Chip chipTime;

        public ViewHolder(@NonNull View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tv_title_activity);
            chipType = view.findViewById(R.id.chip_type);
            chipDate = view.findViewById(R.id.chip_date);
            chipTime = view.findViewById(R.id.chip_time);
        }

    }

    private final List<Activity> activities;
    private final OnActivityClickListener listener;
    private final Context context;

    private final SimpleDateFormat simpleDateFormat;
    private final SimpleDateFormat simpleTimeFormat;
    @SuppressLint("SimpleDateFormat")
    public Adapter(Context context, List<Activity> activities, OnActivityClickListener listener) {
        this.context = context;
        this.activities = activities;
        this.listener = listener;
        this.simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        this.simpleTimeFormat = new SimpleDateFormat("HH:mm");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.activity_item,
                parent,
                false
        );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Activity activity = activities.get(position);


        holder.tvTitle.setText(activity.getTitle());
        holder.chipDate.setChipIconResource(R.drawable.ic_calendar);
        holder.chipDate.setText(simpleDateFormat.format(activity.getDateTime()));
        holder.chipTime.setChipIconResource(R.drawable.ic_time);
        holder.chipTime.setText(simpleTimeFormat.format(activity.getDateTime()));
        setChipTextAndImage(holder.chipType, activity.getActivityType());

        holder.itemView.setOnClickListener(view -> listener.onActivityClick(position));
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public void setChipTextAndImage(Chip chip, ActivityType activityType) {
        switch (activityType) {
            case FREE_TIME:
                chip.setText(context.getString(R.string.activity_type_free_time));
                chip.setChipIconResource(R.drawable.ic_free_time);
                break;
            case TRAVEL:
                chip.setText(context.getString(R.string.activity_type_travel));
                chip.setChipIconResource(R.drawable.ic_travel);
                break;
            case WORK:
                chip.setText(context.getString(R.string.activity_type_work));
                chip.setChipIconResource(R.drawable.ic_work);
                break;
        }
    }

}
