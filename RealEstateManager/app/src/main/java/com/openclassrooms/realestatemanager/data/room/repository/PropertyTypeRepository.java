package com.openclassrooms.realestatemanager.data.room.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.openclassrooms.realestatemanager.data.room.dao.PropertyTypeDao;
import com.openclassrooms.realestatemanager.data.room.database.AppDatabase;
import com.openclassrooms.realestatemanager.data.room.model.Agent;
import com.openclassrooms.realestatemanager.data.room.model.PropertyType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class PropertyTypeRepository {
    private final PropertyTypeDao propertyTypeDao;

    public PropertyTypeRepository(Application application){
        this.propertyTypeDao = AppDatabase.getInstance(application).propertyTypeDao();
    }

    public List<PropertyType> getPropertyTypes(){
        Callable<List<PropertyType>> callable = new Callable<List<PropertyType>>() {
            @Override
            public List<PropertyType> call() throws Exception {
                return propertyTypeDao.getPropertyTypes();
            }
        };

        List<PropertyType> propertyTypes = new ArrayList<>();
        Future<List<PropertyType>> future = AppDatabase.getExecutor().submit(callable);

        try {
            List<PropertyType> list = future.get();
            propertyTypes.addAll(list);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return propertyTypes;
    }

    public LiveData<List<PropertyType>> getPropertyTypesLiveData() {
        MutableLiveData<List<PropertyType>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(getPropertyTypes());
        return mutableLiveData;
    }
    public LiveData<PropertyType> getPropertyTypeById(long id) {return propertyTypeDao.getPropertyTypeById(id);}

    public void insert(PropertyType propertyType) {
        AppDatabase.getExecutor().execute(() -> {
            propertyTypeDao.insert(propertyType);
        });
    }

    public void update(PropertyType propertyType) {
        AppDatabase.getExecutor().execute(() -> {
            propertyTypeDao.update(propertyType);
        });
    }

    public void delete(long id) {
        AppDatabase.getExecutor().execute(() -> {
            propertyTypeDao.delete(id);
        });
    }
}
