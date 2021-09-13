package com.openclassrooms.realestatemanager.data.room.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.openclassrooms.realestatemanager.data.room.model.Agent;
import com.openclassrooms.realestatemanager.data.room.model.PropertyCategory;

import java.util.List;

@Dao
public interface PropertyCategoryDao {
    @Query("SELECT * FROM propertycategory ORDER BY propertycategory.id")
    LiveData<List<PropertyCategory>> getCategories();

    @Query("SELECT * FROM propertycategory WHERE propertycategory.id = :id")
    LiveData<PropertyCategory> getCategoryById(long id);

    @Insert
    long insert(PropertyCategory propertyCategory);

    @Update
    int update(PropertyCategory ropertyCategory);

    @Query("DELETE FROM propertycategory WHERE propertycategory.id = :id")
    int delete(long id);
}