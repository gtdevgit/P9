package com.openclassrooms.realestatemanager.ui.propertydetail.viewmodel;

import android.annotation.SuppressLint;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.openclassrooms.realestatemanager.data.location.LocationRepository;
import com.openclassrooms.realestatemanager.data.permission_checker.PermissionChecker;
import com.openclassrooms.realestatemanager.data.room.model.Agent;
import com.openclassrooms.realestatemanager.data.room.model.Photo;
import com.openclassrooms.realestatemanager.data.room.model.Property;
import com.openclassrooms.realestatemanager.data.room.model.PropertyCategory;
import com.openclassrooms.realestatemanager.data.room.model.PropertyDetailData;
import com.openclassrooms.realestatemanager.data.room.model.PropertyLocationData;
import com.openclassrooms.realestatemanager.data.room.model.PropertyType;
import com.openclassrooms.realestatemanager.data.room.repository.DatabaseRepository;
import com.openclassrooms.realestatemanager.tag.Tag;
import com.openclassrooms.realestatemanager.ui.constantes.PropertyConst;
import com.openclassrooms.realestatemanager.ui.propertydetail.viewstate.PropertyDetailViewState;
import com.openclassrooms.realestatemanager.utils.Utils;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class PropertyDetailViewModel extends ViewModel {

    private static final int CATEGORY_FOR_SAL_ID = 1;
    private static final int CATEGORY_FOR_RENT_ID = 2;

    private long propertyId;
    /**
     * repositories
     */
    private final DatabaseRepository databaseRepository;
    @NonNull
    private final PermissionChecker permissionChecker;
    @NonNull
    private final LocationRepository locationRepository;

    /**
     * Mediator expose PropertyListViewState
     */
    private final MediatorLiveData<PropertyDetailViewState> propertyDetailViewStateMediatorLiveData = new MediatorLiveData<>();
    public LiveData<PropertyDetailViewState> getViewState() { return propertyDetailViewStateMediatorLiveData; }

    public PropertyDetailViewModel(PermissionChecker permissionChecker,
                                   LocationRepository locationRepository,
                                   DatabaseRepository databaseRepository) {
        this.permissionChecker = permissionChecker;
        this.locationRepository = locationRepository;
        this.databaseRepository = databaseRepository;
    }

    private void configureMediatorLiveData(long propertyId) {
        Log.d(Tag.TAG, "PropertyDetailViewModel.configureMediatorLiveData(propertyId=" + propertyId + ")");
        // Property detail with agent information, and category and type
        LiveData<PropertyDetailData> propertyDetailDataLiveData = databaseRepository.getPropertyRepository().getPropertyDetailById(propertyId);
        // other properties location
        LiveData<List<PropertyLocationData>> propertyLocationDataLiveData = databaseRepository.getPropertyRepository().getOtherPropertiesLocationById(propertyId);
        // user location
        LiveData<Location> locationLiveData = locationRepository.getLocationLiveData();
        // photos
        LiveData<List<Photo>> photosLiveData = databaseRepository.getPhotoRepository().getPhotosByPropertyId(propertyId);

        propertyDetailViewStateMediatorLiveData.addSource(locationLiveData, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                if (location != null) {
                    combine(location,
                            propertyDetailDataLiveData.getValue(),
                            propertyLocationDataLiveData.getValue(),
                            photosLiveData.getValue());
                    // must remove source to avoid bug "This source was already added with the different observer"
                    propertyDetailViewStateMediatorLiveData.removeSource(locationLiveData);
                }
            }
        });

        propertyDetailViewStateMediatorLiveData.addSource(propertyDetailDataLiveData, new Observer<PropertyDetailData>() {
            @Override
            public void onChanged(PropertyDetailData propertyDetailData) {
                combine(locationLiveData.getValue(),
                        propertyDetailData,
                        propertyLocationDataLiveData.getValue(),
                        photosLiveData.getValue());
            }
        });

        propertyDetailViewStateMediatorLiveData.addSource(propertyLocationDataLiveData, new Observer<List<PropertyLocationData>>() {
            @Override
            public void onChanged(List<PropertyLocationData> propertyLocationData) {
                combine(locationLiveData.getValue(),
                        propertyDetailDataLiveData.getValue(),
                        propertyLocationData,
                        photosLiveData.getValue());
            }
        });

        propertyDetailViewStateMediatorLiveData.addSource(photosLiveData, new Observer<List<Photo>>() {
            @Override
            public void onChanged(List<Photo> photos) {
                combine(locationLiveData.getValue(),
                        propertyDetailDataLiveData.getValue(),
                        propertyLocationDataLiveData.getValue(),
                        photos);
            }
        });
    }

    public void load(long propertyId){
        Log.d(Tag.TAG, "PropertyDetailViewModel.load(" + propertyId + ")");
        refreshLocation();

        if (propertyId == PropertyConst.PROPERTY_ID_NOT_INITIALIZED) {
            // when don't know propertyId load first property
            try {
                long id = databaseRepository.getPropertyRepository().getFirstPropertyId();
                Log.d(Tag.TAG, "PropertyDetailViewModel.load() getFirstPropertyId=" + id);
                this.propertyId = id;
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else {
            this.propertyId = propertyId;
        }
        configureMediatorLiveData(this.propertyId);
    }

    @SuppressLint("MissingPermission")
    private void refreshLocation() {
        // No GPS permission
        if (!permissionChecker.hasLocationPermission()) {
            locationRepository.stopLocationRequest();
        } else {
            locationRepository.startLocationRequest();
        }
    }

    private void combine(@Nullable Location location,
                         @Nullable PropertyDetailData propertyDetailData,
                         @Nullable List<PropertyLocationData> otherPropertiesLocation,
                         @Nullable List<Photo> photos){

        if (location == null || propertyDetailData == null || photos == null) {
            return;
        }
        Log.d(Tag.TAG, "PropertyDetailViewModel.combine() called");

        String propertyState = "";

        if (propertyDetailData.getPropertyCategoryId() == CATEGORY_FOR_SAL_ID) {
            if (propertyDetailData.isAvailable()){
                propertyState = "For sal";
            } else
                propertyState = "Sold";
        } else {
            if (propertyDetailData.isAvailable()){
                propertyState = "For rent";
            } else
                propertyState = "Rented";
        }

        String entryDate = Utils.convertDateToString(propertyDetailData.getEntryDate());
        String saleDate = Utils.convertDateToString(propertyDetailData.getSaleDate());

        PropertyLocationData currentPropertyLocation = new PropertyLocationData(propertyDetailData.getId(),
                propertyDetailData.getPrice(),
                propertyDetailData.getAddressTitle(),
                propertyDetailData.getLatitude(),
                propertyDetailData.getLongitude());

        // ViewModel emit ViewState
        propertyDetailViewStateMediatorLiveData.setValue(new PropertyDetailViewState(location,
                propertyDetailData, currentPropertyLocation, otherPropertiesLocation, photos, propertyState, entryDate, saleDate));
    }
}

