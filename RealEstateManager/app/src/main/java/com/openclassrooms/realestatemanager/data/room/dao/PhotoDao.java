package com.openclassrooms.realestatemanager.data.room.dao;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.openclassrooms.realestatemanager.data.room.model.Photo;

import java.util.List;

@Dao
public interface PhotoDao {
    @Query("SELECT * FROM photo ORDER BY id")
    LiveData<List<Photo>> getPhotos();

    @Query("SELECT * FROM photo WHERE property_id = :propertyId ORDER BY id")
    LiveData<List<Photo>> getPhotosByPropertyId(long propertyId);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    long insert(Photo photo);

    @Update
    int update(Photo photo);

    @Query("DELETE FROM photo WHERE id = :id")
    int delete(long id);

    @Query("SELECT * FROM photo WHERE property_id = :propertyId ORDER BY id")
    Cursor getPhotoByIdWithCursor(long propertyId);

    @Query("SELECT * FROM photo ORDER BY id")
    Cursor getPhotosWithCursor();
}
