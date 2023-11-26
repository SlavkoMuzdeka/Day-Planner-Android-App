package org.unibl.etf.dayplanner.db.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.unibl.etf.dayplanner.db.enums.ActivityType;
import org.unibl.etf.dayplanner.utils.Constants;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity(tableName = Constants.TABLE_NAME_ACTIVITY)
public class Activity implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String title;
    private Date dateTime;
    private String description;
    @ColumnInfo(name = "location_name")
    private String locationName;
    @ColumnInfo(name = "location_latitude")
    private Double locationLatitude;
    @ColumnInfo(name = "location_longitude")
    private Double locationLongitude;
    private ActivityType activityType;
    public Activity() {
        super();
    }

    public Activity(String title, Date dateTime, String description, String locationName, Double locationLatitude, Double locationLongitude, ActivityType activityType) {
        this.title = title;
        this.dateTime = dateTime;
        this.description = description;
        this.locationName = locationName;
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
        this.activityType = activityType;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public void setLocationLatitude(Double locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public void setLocationLongitude(Double locationLongitude) {
        this.locationLongitude = locationLongitude;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public String getDescription() {
        return description;
    }

    public String getLocationName() {
        return locationName;
    }

    public Double getLocationLatitude() {
        return locationLatitude;
    }

    public Double getLocationLongitude() {
        return locationLongitude;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return id == activity.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
