package com.openclassrooms.realestatemanager.data.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.room.Update;
import androidx.sqlite.db.SupportSQLiteQuery;

import com.openclassrooms.realestatemanager.data.room.model.Property;

import java.util.List;

@Dao
public interface PropertyDao {
    @Query("SELECT * FROM property ORDER BY property.id")
    LiveData<List<Property>> getProperties();

    @Query("SELECT * FROM property WHERE property.id = :id")
    LiveData<Property> getPropertyById(Long id);

    @Query("SELECT property.id FROM property ORDER BY property.id LIMIT 1")
    Long getFirstPropertyId();

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    long insert(Property property);

    @Update
    int update(Property property);

    @Query("DELETE FROM property WHERE property.id = :id")
    int delete(long id);

    //For Search
    @RawQuery(observedEntities = Property.class)
    LiveData<List<Property>> getSearch(SupportSQLiteQuery query);
}
