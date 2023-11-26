package org.unibl.etf.dayplanner.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Transaction;

import org.unibl.etf.dayplanner.db.model.ActivityImageJoin;

@Dao
public interface ActivityImageJoinDAO {

    @Transaction
    @Insert
    void insert(ActivityImageJoin activityImageJoin);

    @Transaction
    @Delete
    void delete(ActivityImageJoin activityImageJoin);
}
