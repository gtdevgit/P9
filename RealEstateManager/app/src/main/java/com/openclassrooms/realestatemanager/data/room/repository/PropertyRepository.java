package com.openclassrooms.realestatemanager.data.room.repository;

import android.app.Application;
import android.database.Cursor;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.openclassrooms.realestatemanager.data.room.dao.PropertyDao;
import com.openclassrooms.realestatemanager.data.room.database.AppDatabase;
import com.openclassrooms.realestatemanager.data.room.model.Property;
import com.openclassrooms.realestatemanager.data.room.model.PropertyDetailData;
import com.openclassrooms.realestatemanager.data.room.model.PropertyLocationData;
import com.openclassrooms.realestatemanager.data.room.model.PropertyRange;
import com.openclassrooms.realestatemanager.tag.Tag;
import com.openclassrooms.realestatemanager.ui.constantes.PropertyConst;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class PropertyRepository {
    private final PropertyDao propertyDao;

    public PropertyRepository(Application application){
        this.propertyDao = AppDatabase.getInstance(application).propertyDao();
    }

    // return a long. This is the newly generated ID
    public long insert(Property property) throws ExecutionException, InterruptedException {
        Log.d(Tag.TAG, "insert() called with: property = [" + property + "]");
        Callable<Long> callable = new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return propertyDao.insert(property);
            }
        };

        Future<Long> future = AppDatabase.getExecutor().submit(callable);
        Long id = future.get();

        return  id;
    }

    // Return the number of updated rows
    public int update(Property property) throws ExecutionException, InterruptedException {
        Log.d(Tag.TAG, "update() called with: property = [" + property + "]");
        Callable<Integer> callable = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return propertyDao.update(property);
            }
        };

        Future<Integer> future = AppDatabase.getExecutor().submit(callable);
        int count = future.get();

        return count;
    }

    public void delete(long id) {
        AppDatabase.getExecutor().execute(() -> {
            propertyDao.delete(id);
        });
    }

    public LiveData<List<PropertyLocationData>> getOtherPropertiesLocationById(long id){
        return propertyDao.getOtherPropertiesLocationById(id);
    }

    public LiveData<PropertyDetailData> getPropertyDetailByIdLiveData(long id) {
        return propertyDao.getPropertyDetailById(id);
    }

    public LiveData<PropertyLocationData> getPropertyLocationById(long id){
        return propertyDao.getPropertyLocationById(id);
    }

    public LiveData<List<PropertyLocationData>> getPropertiesLocation(){
        return propertyDao.getPropertiesLocation();
    }

    public Cursor getPropertyByIdWithCursor(long id){
        return propertyDao.getPropertyByIdWithCursor(id);
    }

    public Cursor getPropertiesWithCursor(){
        return propertyDao.getPropertiesWithCursor();
    }

    public LiveData<List<Property>> getPropertiesWithFilterLiveData(SimpleSQLiteQuery query) {
        return propertyDao.getPropertiesWithFilterLiveData(query);
    };

    public List<Property> getPropertiesWithFilter(SimpleSQLiteQuery query){
        Callable<List<Property>> callable = new Callable<List<Property>>() {
            @Override
            public List<Property> call() throws Exception {
                return propertyDao.getPropertiesWithFilter(query);
            }
        };

        List<Property> properties = new ArrayList<>();
        Future<List<Property>> future = AppDatabase.getExecutor().submit(callable);
        try {
            List<Property> list = future.get();
            properties.addAll(list);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return properties;
    }

    private MutableLiveData<PropertySearchParameters> propertySearchParametersMutableLiveData = new MutableLiveData<>();
    public void setPropertySearchParameters(PropertySearchParameters propertySearchParameters) {
        propertySearchParametersMutableLiveData.setValue(propertySearchParameters);
    }

    public void resetSearch(){
        setPropertySearchParameters(new PropertySearchParameters());
    }

    public LiveData<List<Property>> getProperties() {
        return Transformations.switchMap(propertySearchParametersMutableLiveData,
            propertySearchParameters -> {return getPropertiesWithFilterLiveData(propertySearchParameters.getQuery());});
    }

    public LiveData<Long> getFirstOrValidIdLiveData(Long id) {
        return Transformations.map(getProperties(),
                properties -> {
                    long result = PropertyConst.PROPERTY_ID_NOT_INITIALIZED;
                    if (properties.size() > 0) {
                        result = properties.get(0).getId();
                        for (Property property : properties) {
                            if (property.getId() == id) {
                                result = id;
                                break;
                            }
                        }
                    }
                    return new Long(result);
                });
    }

    public LiveData<PropertyRange> getPropertiesMinMaxRanges(){
        return propertyDao.getPropertiesMinMaxRanges();
    }
}
