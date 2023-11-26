package org.unibl.etf.dayplanner.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import org.unibl.etf.dayplanner.db.model.Activity;
import org.unibl.etf.dayplanner.utils.Constants;

import java.util.Date;
import java.util.List;

@Dao
public interface ActivityDAO {

    @Transaction
    @Insert
    long insert(Activity activity);

    @Transaction
    @Query("SELECT * FROM " + Constants.TABLE_NAME_ACTIVITY + " a WHERE a.dateTime >= :from ORDER BY a.dateTime")
    List<Activity> getAllByDate(Date from);

    @Transaction
    @Query("SELECT * FROM " + Constants.TABLE_NAME_ACTIVITY + " a WHERE a.dateTime >= :from AND a.dateTime < :to ORDER BY a.dateTime")
    List<Activity> getAllByDate(Date from, Date to);

    @Transaction
    @Query("SELECT * FROM " + Constants.TABLE_NAME_ACTIVITY + " a WHERE a.title LIKE '%' || :title || '%' AND a.dateTime >= :from ORDER BY a.dateTime")
    List<Activity> getAllByTitle(String title, Date from);

    @Transaction
    @Delete
    void delete(Activity activity);
}
