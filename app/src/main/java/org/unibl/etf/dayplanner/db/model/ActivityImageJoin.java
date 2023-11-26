package org.unibl.etf.dayplanner.db.model;

import androidx.room.Entity;

import org.unibl.etf.dayplanner.utils.Constants;

@Entity(tableName = Constants.TABLE_NAME_ACTIVITY_IMAGE_JOIN, primaryKeys = {"activityId", "imageId"})
public class ActivityImageJoin {
    private long activityId;
    private long imageId;

    public long getActivityId() {
        return activityId;
    }

    public long getImageId() {
        return imageId;
    }

    public void setActivityId(long activityId) {
        this.activityId = activityId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }
}