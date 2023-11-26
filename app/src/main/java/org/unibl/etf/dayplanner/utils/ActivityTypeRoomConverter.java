package org.unibl.etf.dayplanner.utils;

import androidx.room.TypeConverter;

import org.unibl.etf.dayplanner.db.enums.ActivityType;

public class ActivityTypeRoomConverter {
    @TypeConverter
    public static ActivityType toActivityType(Byte value) {
        return value == null? null: ActivityType.values()[value];
    }

    @TypeConverter
    public static Byte fromActivityType(ActivityType activityType){
        return activityType == null? null: (byte) activityType.ordinal();
    }
}
