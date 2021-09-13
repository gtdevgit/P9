package com.openclassrooms.realestatemanager.data.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.openclassrooms.realestatemanager.data.room.model.PropertyCategory;
import com.openclassrooms.realestatemanager.data.room.model.PropertyType;

import java.util.List;

@Dao
public interface PropertyTypeDao {
    @Query("SELECT * FROM propertytype ORDER BY propertytype.id")
    LiveData<List<PropertyType>> getPropertyTypes();

    @Query("SELECT * FROM propertytype WHERE propertytype.id = :id")
    LiveData<PropertyType> getPropertyTypeById(long id);

    @Insert
    long insert(PropertyType propertyType);

    @Update
    int update(PropertyType propertyType);

    @Query("DELETE FROM propertytype WHERE propertytype.id = :id")
    int delete(long id);
}

