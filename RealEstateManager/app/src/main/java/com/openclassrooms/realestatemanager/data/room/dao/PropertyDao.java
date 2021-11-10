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
import com.openclassrooms.realestatemanager.data.room.model.PropertyDetailData;
import com.openclassrooms.realestatemanager.data.room.model.PropertyLocationData;

import java.util.List;

@Dao
public interface PropertyDao {
    @Query("SELECT * FROM property ORDER BY property.id")
    LiveData<List<Property>> getProperties();

    @Query("SELECT * FROM property WHERE property.id = :id")
    LiveData<Property> getPropertyById(Long id);

    @Query("SELECT property.id FROM property ORDER BY property.id LIMIT 1")
    LiveData<Long> getFirstPropertyIdLiveData();

/*  use this query to retrieve first id or valid id.
    if is valid id
        return id
    else return
*/
    @Query("SELECT property.id FROM property " +
           "WHERE (property.id = :id) or " +
           "      (property.id = (SELECT MIN(property.id) FROM property)) " +
           "ORDER BY property.id DESC " +
           "LIMIT 1")
    LiveData<Long> getFirstOrValidIdLiveData(Long id);

    @Query("SELECT property.id FROM property WHERE property.id = :id")
    LiveData<Long> getIsIdExistLiveData(Long id);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    long insert(Property property);

    @Update
    int update(Property property);

    @Query("DELETE FROM property WHERE property.id = :id")
    int delete(long id);

    //For Search
    @RawQuery(observedEntities = Property.class)
    LiveData<List<Property>> getSearch(SupportSQLiteQuery query);

    @Query("select property.*, " +
           "agent.email as agent_email, agent.name as agent_name, agent.phone as agent_phone, " +
           "property_type.name as property_type_name "+
           "from property " +
           "left join agent on property.agent_id = agent.id " +
           "left join property_type on property.property_type_id = property_type.id " +
           "where property.id = :id")
    LiveData<PropertyDetailData> getPropertyDetailById(long id);

    @Query("select id, price, address_title, latitude, longitude " +
           "from property " +
           "where (not ((latitude = 0) and (longitude = 0))) and (id <> :id)")
    LiveData<List<PropertyLocationData>> getOtherPropertiesLocationById(long id);

    @Query("select id, price, address_title, latitude, longitude " +
            "from property " +
            "where (not ((latitude = 0) and (longitude = 0))) and (id = :id)")
    LiveData<PropertyLocationData> getPropertyLocationById(long id);

    @Query("select id, price, address_title, latitude, longitude " +
            "from property " +
            "where (not ((latitude = 0) and (longitude = 0)))")
    LiveData<List<PropertyLocationData>> getPropertiesLocation();
}
