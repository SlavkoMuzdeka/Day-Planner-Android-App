package org.unibl.etf.dayplanner.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import org.unibl.etf.dayplanner.db.dao.ActivityDAO;
import org.unibl.etf.dayplanner.db.dao.ActivityImageJoinDAO;
import org.unibl.etf.dayplanner.db.dao.ImageDAO;
import org.unibl.etf.dayplanner.db.model.Activity;
import org.unibl.etf.dayplanner.db.model.ActivityImageJoin;
import org.unibl.etf.dayplanner.db.model.Image;
import org.unibl.etf.dayplanner.utils.ActivityTypeRoomConverter;
import org.unibl.etf.dayplanner.utils.Constants;
import org.unibl.etf.dayplanner.utils.DateRoomConverter;

@Database(entities = {Activity.class, Image.class, ActivityImageJoin.class}, version = 1)
@TypeConverters({DateRoomConverter.class, ActivityTypeRoomConverter.class})
public abstract class DayPlannerDB extends RoomDatabase {

    public abstract ImageDAO getImageDAO();

    public abstract ActivityDAO getActivityDAO();

    public abstract ActivityImageJoinDAO getActivityJoinDAO();

    private static DayPlannerDB dayPlannerDB;

    public static DayPlannerDB getInstance(Context context) {
        if (null == dayPlannerDB)
            dayPlannerDB = buildDatabaseInstance(context);
        return dayPlannerDB;
    }

    @NonNull
    private static DayPlannerDB buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(
                context,
                DayPlannerDB.class,
                Constants.DATABASE_NAME
        ).allowMainThreadQueries().build();
    }
}
