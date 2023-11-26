package org.unibl.etf.dayplanner.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import org.unibl.etf.dayplanner.db.model.Image;

import java.util.List;

@Dao
public interface ImageDAO {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertImages(List<Image> images);

    @Transaction
    @Query("SELECT i.* FROM image i INNER JOIN activity_image_join ai ON i.id = ai.imageId " +
            "WHERE ai.activityId = :activityId")
    List<Image> getImagesForActivity(long activityId);

    @Transaction
    @Delete
    void delete(Image image);
}
