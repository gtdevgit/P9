package com.openclassrooms.realestatemanager.ui.propertyedit.viewmodel;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.data.googlemaps.repository.GoogleGeocodeRepository;
import com.openclassrooms.realestatemanager.data.googlemaps.repository.GoogleStaticMapRepository;
import com.openclassrooms.realestatemanager.data.room.model.Agent;
import com.openclassrooms.realestatemanager.data.room.model.Photo;
import com.openclassrooms.realestatemanager.data.room.model.Property;
import com.openclassrooms.realestatemanager.data.room.model.PropertyDetailData;
import com.openclassrooms.realestatemanager.data.room.model.PropertyType;
import com.openclassrooms.realestatemanager.data.room.repository.DatabaseRepository;
import com.openclassrooms.realestatemanager.tag.Tag;
import com.openclassrooms.realestatemanager.ui.constantes.PropertyConst;
import com.openclassrooms.realestatemanager.ui.propertyedit.viewstate.DropdownItem;
import com.openclassrooms.realestatemanager.ui.propertyedit.viewstate.DropdownViewState;
import com.openclassrooms.realestatemanager.ui.propertyedit.viewstate.FieldState;
import com.openclassrooms.realestatemanager.ui.propertyedit.viewstate.PropertyEditViewState;
import com.openclassrooms.realestatemanager.ui.propertyedit.viewstate.StaticMapViewState;
import com.openclassrooms.realestatemanager.utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PropertyEditViewModel extends ViewModel {
    @NonNull
    private final DatabaseRepository databaseRepository;
    @NonNull
    private final GoogleGeocodeRepository googleGeocodeRepository;
    @NonNull
    private final GoogleStaticMapRepository googleStaticMapRepository;

    /**
     * cache
     */
    private final CachePropertyEditViewModel cache;

    public PropertyEditViewModel(@NonNull DatabaseRepository databaseRepository,
                                 @NonNull GoogleGeocodeRepository googleGeocodeRepository,
                                 @NonNull GoogleStaticMapRepository googleStaticMapRepository) {
        this.databaseRepository = databaseRepository;
        this.googleGeocodeRepository = googleGeocodeRepository;
        this.googleStaticMapRepository = googleStaticMapRepository;

        cache = new CachePropertyEditViewModel();
        cache.setAgents(databaseRepository.getAgentRepository().getAgents());
        cache.setPropertyTypes(databaseRepository.getPropertyTypeRepository().getPropertyTypes());

        // default control values
        onCheckAddressTitleValueMutableLiveData.setValue(new FieldState(getResIdError(true)));
        onCheckAddressValueMutableLiveData.setValue(new FieldState(getResIdError(true)));
        onCheckDescriptionValueMutableLiveData.setValue(new FieldState(getResIdError(true)));
        onCheckPointOfInterestValueMutableLiveData.setValue(new FieldState(getResIdError(true)));
        onCheckPriceValueMutableLiveData.setValue(new FieldState(getResIdError(true)));
        onCheckSurfaceValueMutableLiveData.setValue(new FieldState(getResIdError(true)));
        onCheckRoomsValueMutableLiveData.setValue(new FieldState(getResIdError(true)));
        onCheckEntryDateValueMutableLiveData.setValue(new FieldState(getResIdError(true)));
        onCheckSaleDateValueMutableLiveData.setValue(new FieldState(getResIdError(true)));
        onCheckAgentIdValueMutableLiveData.setValue(new FieldState(getResIdError(true)));
        onCheckPropertyTypeIdValueMutableLiveData.setValue(new FieldState(getResIdError(true)));

        initDropdownViewStateMediatorLiveData();
        configureGoogleStaticMapUrlLiveData();
    }

    private final MutableLiveData<String> addressMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<String> getAddressMutableLiveData() {
        return addressMutableLiveData;
    }

    private final MediatorLiveData<StaticMapViewState> googleStaticMapViewStateMediatorLiveData = new MediatorLiveData<>();
    public LiveData<StaticMapViewState> getGoogleStaticMapViewState() {
        return googleStaticMapViewStateMediatorLiveData;
    }

    public void configureGoogleStaticMapUrlLiveData(){
        LiveData<LatLng> latLngLiveData = Transformations.switchMap(addressMutableLiveData,
                googleGeocodeRepository::getLocationByAddressLiveData);

        googleStaticMapViewStateMediatorLiveData.addSource(addressMutableLiveData, s -> {
        });

        googleStaticMapViewStateMediatorLiveData.addSource(latLngLiveData, latLng -> {
            cache.setValue(FieldKey.LATITUDE, Double.toString(latLng.latitude));
            cache.setValue(FieldKey.LONGITUDE, Double.toString(latLng.longitude));
            String url = googleStaticMapRepository.getUrlImage(latLng.latitude, latLng.longitude);
            googleStaticMapViewStateMediatorLiveData.setValue(new StaticMapViewState(latLng, url));
        });
    }

    public LiveData<PropertyEditViewState> getViewStateLiveData(long propertyId) {
        Log.d(Tag.TAG, "PropertyEditViewModel.getViewStateLiveData.() called with: propertyId = [" + propertyId + "]");
        cache.setPropertyId(propertyId);
        LiveData<List<Photo>> pendingPhotosLiveData = cache.getPendingPhotosLiveData();
        LiveData<PropertyDetailData> propertyDetailDataLiveData = databaseRepository.getPropertyRepository().getPropertyDetailByIdLiveData(propertyId);
        LiveData<List<Photo>> databasePhotosLiveData = databaseRepository.getPhotoRepository().getPhotosByPropertyId(propertyId);

        MediatorLiveData<PropertyEditViewState> mediatorLiveData = new MediatorLiveData<>();
        mediatorLiveData.addSource(propertyDetailDataLiveData,
                propertyDetailData -> combine(mediatorLiveData,
                        propertyId,
                        propertyDetailData,
                        databasePhotosLiveData.getValue(),
                        pendingPhotosLiveData.getValue()));

        mediatorLiveData.addSource(databasePhotosLiveData,
                photos -> combine(mediatorLiveData,
                        propertyId,
                        propertyDetailDataLiveData.getValue(),
                        photos,
                        pendingPhotosLiveData.getValue()));

        mediatorLiveData.addSource(pendingPhotosLiveData,
                photos -> combine(mediatorLiveData,
                        propertyId,
                        propertyDetailDataLiveData.getValue(),
                        databasePhotosLiveData.getValue(),
                        photos));
        return mediatorLiveData;
    }

    private PropertyEditViewState createViewStateFromCacheAndDatabase(PropertyDetailData propertyDetailData,
                                             List<Photo> databasePhotos,
                                             List<Photo> pendingPhotos,
                                             String googleStaticMapUrl){
        // merge photo
        List<Photo> photos = new ArrayList<>();
        if (pendingPhotos != null) {
            photos.addAll(pendingPhotos);
        }
        if(databasePhotos != null) {
            photos.addAll(databasePhotos);
        }

        // convert values toString
        String databaseEntryDate = Utils.convertDateToLocalFormat(propertyDetailData.getEntryDate());
        String databaseSaleDate = Utils.convertDateToLocalFormat(propertyDetailData.getSaleDate());
        String databasePrice = Integer.toString(propertyDetailData.getPrice());
        String databaseSurface = Integer.toString(propertyDetailData.getSurface());
        String databaseRooms = Integer.toString(propertyDetailData.getRooms());

        // get values from cache or from database ?
        String addressTitle = cache.getValue(FieldKey.ADDRESS_TITLE, propertyDetailData.getAddressTitle());
        String address = cache.getValue(FieldKey.ADDRESS, propertyDetailData.getAddress());
        String description = cache.getValue(FieldKey.DESCRIPTION, propertyDetailData.getDescription());
        String pointOfInterest = cache.getValue(FieldKey.POINT_OF_INTEREST, propertyDetailData.getPointsOfInterest());
        String price = cache.getValue(FieldKey.PRICE, databasePrice);
        String surface = cache.getValue(FieldKey.SURFACE, databaseSurface);
        String rooms = cache.getValue(FieldKey.ROOMS, databaseRooms);
        String entryDate = cache.getValue(FieldKey.ENTRY_DATE, databaseEntryDate);
        String saleDate = cache.getValue(FieldKey.SALE_DATE, databaseSaleDate);

        long agentId = cache.getValue(FieldKey.AGENT_ID, propertyDetailData.getAgentId());
        String agentName = cache.getValue(FieldKey.AGENT_NAME, propertyDetailData.getAgentName());
        long propertyTypeId = cache.getValue(FieldKey.PROPERTY_TYPE_ID, propertyDetailData.getPropertyTypeId());
        String propertyTypeName = cache.getValue(FieldKey.PROPERTY_TYPE_NAME, propertyDetailData.getTypeName());

        return new PropertyEditViewState(
                addressTitle,
                address,
                description,
                pointOfInterest,
                price,
                surface,
                rooms,
                entryDate,
                saleDate,
                agentId,
                agentName,
                propertyTypeId,
                propertyTypeName,
                propertyDetailData.getLatitude(),
                propertyDetailData.getLongitude(),
                photos,
                googleStaticMapUrl);
    }

    private PropertyEditViewState createViewStateFromCache(List<Photo> pendingPhotos){
        Log.d(Tag.TAG, "PropertyEditViewModel.createViewStateFromCache() called with: pendingPhotos = [" + pendingPhotos + "]");
        // merge photo
        List<Photo> photos = new ArrayList<>();
        if (pendingPhotos != null) {
            photos.addAll(pendingPhotos);
        }
        // get values from cache ?
        String addressTitle = cache.getValue(FieldKey.ADDRESS_TITLE);
        String address = cache.getValue(FieldKey.ADDRESS);
        String description = cache.getValue(FieldKey.DESCRIPTION);
        String pointOfInterest = cache.getValue(FieldKey.POINT_OF_INTEREST);
        String price = cache.getValue(FieldKey.PRICE);
        String surface = cache.getValue(FieldKey.SURFACE);
        String rooms = cache.getValue(FieldKey.ROOMS);
        String entryDate = cache.getValue(FieldKey.ENTRY_DATE);
        String saleDate = cache.getValue(FieldKey.SALE_DATE);

        long agentId = cache.getValue(FieldKey.AGENT_ID, 0);
        String agentName = cache.getValue(FieldKey.AGENT_NAME);
        long propertyTypeId = cache.getValue(FieldKey.PROPERTY_TYPE_ID, 0);
        String propertyTypeName = cache.getValue(FieldKey.PROPERTY_TYPE_NAME);

        double latitude = cache.getValue(FieldKey.LATITUDE, 0f);
        double longitude = cache.getValue(FieldKey.LONGITUDE, 0f);
        String googleStaticMapUrl = googleStaticMapRepository.getUrlImage(latitude, longitude);

        return new PropertyEditViewState(
                addressTitle,
                address,
                description,
                pointOfInterest,
                price,
                surface,
                rooms,
                entryDate,
                saleDate,
                agentId,
                agentName,
                propertyTypeId,
                propertyTypeName,
                latitude,
                longitude,
                photos,
                googleStaticMapUrl);
    }

    private void combine(MediatorLiveData<PropertyEditViewState> mediatorLiveData,
                         long propertyId,
                         PropertyDetailData propertyDetailData,
                         List<Photo> databasePhotos,
                         List<Photo> pendingPhotos) {
        Log.d(Tag.TAG, "PropertyEditViewModel.combine() called with: propertyId = [" + propertyId + "], propertyDetailData = [" + propertyDetailData + "], databasePhotos = [" + databasePhotos + "], pendingPhotos = [" + pendingPhotos + "]");

        if (propertyId == PropertyConst.PROPERTY_ID_NOT_INITIALIZED) {
            PropertyEditViewState propertyEditViewState = createViewStateFromCache(pendingPhotos);
            //PropertyEditViewState propertyEditViewState = new PropertyEditViewState(pendingPhotos);
            // todo : get cache !
            mediatorLiveData.setValue(propertyEditViewState);
            return;
        }

        if (propertyDetailData == null) {
            return;
        }

        // googleStaticMapRepository is not async so we can call it in combine
        String googleStaticMapUrl = googleStaticMapRepository.getUrlImage(propertyDetailData.getLatitude(), propertyDetailData.getLongitude());

        PropertyEditViewState propertyEditViewState = createViewStateFromCacheAndDatabase(propertyDetailData, databasePhotos, pendingPhotos, googleStaticMapUrl);
        mediatorLiveData.setValue(propertyEditViewState);
    }

    private final MediatorLiveData<DropdownViewState> dropDownViewStateMediatorLiveData = new MediatorLiveData<>();
    public MediatorLiveData<DropdownViewState> getDropDownViewStateMediatorLiveData() {
        return dropDownViewStateMediatorLiveData;
    }

    private void initDropdownViewStateMediatorLiveData(){
        LiveData<List<DropdownItem>> agentItemsLiveData = Transformations.map(databaseRepository.getAgentRepository().getAgentsLiveData(),
                agents -> {
                    List<DropdownItem> items = new ArrayList<>();
                    for (Agent agent : agents) {
                        items.add(new DropdownItem(agent.getId(), agent.getName()));
                    }
                    return items;
                });

        LiveData<List<DropdownItem>> propertyTypeItemsLiveData = Transformations.map(databaseRepository.getPropertyTypeRepository().getPropertyTypesLiveData(),
                propertyTypes -> {
                    List<DropdownItem> items = new ArrayList<>();
                    for (PropertyType propertyType : propertyTypes) {
                        items.add(new DropdownItem(propertyType.getId(), propertyType.getName()));
                    }
                    return items;
                });

        dropDownViewStateMediatorLiveData.addSource(agentItemsLiveData, items -> combineDropDown(items, propertyTypeItemsLiveData.getValue()));
        dropDownViewStateMediatorLiveData.addSource(propertyTypeItemsLiveData, items -> combineDropDown(agentItemsLiveData.getValue(), items));
    }

    private void combineDropDown(@Nullable List<DropdownItem> agentItems,
                                 @Nullable List<DropdownItem> propertyTypeItems){

        if ((agentItems == null) || (propertyTypeItems == null)) {
            return;
        }
        dropDownViewStateMediatorLiveData.setValue(new DropdownViewState(agentItems, propertyTypeItems));
    }

    private PropertyType findPropertyTypeById(long id) {
        if ((cache != null) && (cache.getPropertyTypes() != null)){
            for (PropertyType propertyType : cache.getPropertyTypes()) {
                if (propertyType.getId() == id) {
                    return propertyType;
                }
            }
        }
        return null;
    }

    private static boolean noEmptyString(String value){
        return !TextUtils.isEmpty(value.trim());
    }

    public static boolean validDate(String text){
        return (Utils.convertStringInLocalFormatToDate(text) != null);
    }

    private static boolean validOrNullDate(String text) {
        return (TextUtils.isEmpty(text) || (Utils.convertStringInLocalFormatToDate(text) != null));
    }

    private Agent findAgentById(long id) {
        if ((cache != null) && (cache.getAgents() != null)){
            for (Agent agent : cache.getAgents()) {
                if (agent.getId() == id) {
                    return agent;
                }
            }
        }
        return null;
    }

    private int getResIdError(boolean error) {
        return  (error) ? R.string.value_required : PropertyConst.NO_STRING_ID;
    }

    private boolean checkIsInt(String value){
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private final MutableLiveData<FieldState> onCheckAddressTitleValueMutableLiveData = new MutableLiveData<>();
    public LiveData<FieldState> getOnCheckAddressTitleValueLiveData() { return onCheckAddressTitleValueMutableLiveData; }
    public boolean checkAddressTitleValue(String value){
        boolean valueOk = PropertyEditViewModel.noEmptyString(value);
        onCheckAddressTitleValueMutableLiveData.setValue(new FieldState(getResIdError(!valueOk)));
        return valueOk;
    }

    private final MutableLiveData<FieldState> onCheckAddressValueMutableLiveData = new MutableLiveData<>();
    public LiveData<FieldState> getOnCheckAddressValueLiveData() { return onCheckAddressValueMutableLiveData; }
    public boolean checkAddressValue(String value){
        boolean valueOk = PropertyEditViewModel.noEmptyString(value);
        onCheckAddressValueMutableLiveData.setValue(new FieldState(getResIdError(!valueOk)));
        return valueOk;
    }

    private final MutableLiveData<FieldState> onCheckDescriptionValueMutableLiveData = new MutableLiveData<>();
    public LiveData<FieldState> getOnCheckDescriptionValueLiveData() { return onCheckDescriptionValueMutableLiveData; }
    public boolean checkDescriptionValue(String value){
        boolean valueOk = PropertyEditViewModel.noEmptyString(value);
        onCheckDescriptionValueMutableLiveData.setValue(new FieldState(getResIdError(!valueOk)));
        return valueOk;
    }

    private final MutableLiveData<FieldState> onCheckPointOfInterestValueMutableLiveData = new MutableLiveData<>();
    public LiveData<FieldState> getOnCheckPointOfInterestValueLiveData() { return onCheckPointOfInterestValueMutableLiveData; }
    public boolean checkPointOfInterestValue(String value){
        boolean valueOk = PropertyEditViewModel.noEmptyString(value);
        onCheckPointOfInterestValueMutableLiveData.setValue(new FieldState(getResIdError(!valueOk)));
        return valueOk;
    }

    private final MutableLiveData<FieldState> onCheckPriceValueMutableLiveData = new MutableLiveData<>();
    public LiveData<FieldState> getOnCheckPriceValueLiveData() { return onCheckPriceValueMutableLiveData; }
    public boolean checkPriceValue(String value){
        boolean valueOk = checkIsInt(value);
        onCheckPriceValueMutableLiveData.setValue(new FieldState(getResIdError(!valueOk)));
        return valueOk;
    }

    private final MutableLiveData<FieldState> onCheckSurfaceValueMutableLiveData = new MutableLiveData<>();
    public LiveData<FieldState> getOnCheckSurfaceValueLiveData() { return onCheckSurfaceValueMutableLiveData; }
    public boolean checkSurfaceValue(String value){
        boolean valueOk = checkIsInt(value);
        onCheckSurfaceValueMutableLiveData.setValue(new FieldState(getResIdError(!valueOk)));
        return valueOk;
    }

    private final MutableLiveData<FieldState> onCheckRoomsValueMutableLiveData = new MutableLiveData<>();
    public LiveData<FieldState> getOnCheckRoomsValueLiveData() { return onCheckRoomsValueMutableLiveData; }
    public boolean checkRoomsValue(String value){
        boolean valueOk = checkIsInt(value);
        onCheckRoomsValueMutableLiveData.setValue(new FieldState(getResIdError(!valueOk)));
        return valueOk;
    }

    private final MutableLiveData<FieldState> onCheckEntryDateValueMutableLiveData = new MutableLiveData<>();
    public LiveData<FieldState> getOnCheckEntryDateValueLiveData() { return onCheckEntryDateValueMutableLiveData; }
    public boolean checkEntryDateValue(String value){
        boolean valueOk = PropertyEditViewModel.validDate(value);
        onCheckEntryDateValueMutableLiveData.setValue(new FieldState(getResIdError(!valueOk)));
        return valueOk;
    }

    private final MutableLiveData<FieldState> onCheckSaleDateValueMutableLiveData = new MutableLiveData<>();
    public LiveData<FieldState> getOnCheckSaleDateValueLiveData() { return onCheckSaleDateValueMutableLiveData; }
    public boolean checkSaleDateValue(String value){
        boolean valueOk = PropertyEditViewModel.validOrNullDate(value);
        onCheckSaleDateValueMutableLiveData.setValue(new FieldState(getResIdError(!valueOk)));
        return valueOk;
    }

    private final MutableLiveData<FieldState> onCheckAgentIdValueMutableLiveData = new MutableLiveData<>();
    public LiveData<FieldState> getOnCheckAgentIdValueLiveData() { return onCheckAgentIdValueMutableLiveData; }
    public boolean checkAgentIdValue(long id){
        boolean valueOk = (findAgentById(id) != null);
        onCheckAgentIdValueMutableLiveData.setValue(new FieldState(getResIdError(!valueOk)));
        return valueOk;
    }

    private final MutableLiveData<FieldState> onCheckPropertyTypeIdValueMutableLiveData = new MutableLiveData<>();
    public LiveData<FieldState> getOnCheckPropertyTypeIdValueLiveData() { return onCheckPropertyTypeIdValueMutableLiveData; }
    public boolean checkPropertyTypeIdValue(long id){
        boolean valueOk = (findPropertyTypeById(id) != null);
        onCheckPropertyTypeIdValueMutableLiveData.setValue(new FieldState(getResIdError(!valueOk)));
        return valueOk;
    }

    private boolean checkPendingPhoto(){
        return cache.isAllPhotoOk();
    }

    public interface AddPropertyInterface{
        void onPropertyAdded(long propertyId);
    }
     /**
     * check values,
     * if values are ok send data to database and emit ok to view
     */
    public void insertOrUpdateProperty(
                            long propertyId,
                            String price,
                            String surface,
                            String description,
                            String addressTitle,
                            String address,
                            String pointOfInterest,
                            String entryDate,
                            String saleDate,
                            long propertyTypeId,
                            long agentId,
                            String rooms,
                            LatLng latLng,
                            AddPropertyInterface addPropertyInterface){

        // check all values
        boolean valuesOk = checkAllValues(price, surface, description, addressTitle, address,
                pointOfInterest, entryDate, saleDate, propertyTypeId, agentId, rooms);

        if (valuesOk) {
            double latitude = (latLng == null) ? 0 : latLng.latitude;
            double longitude = (latLng == null) ? 0 : latLng.longitude;

            int intPrice = Integer.parseInt(price);
            int intSurface = Integer.parseInt(surface);
            int intRooms = Integer.parseInt(rooms);

            Date dateEntryDate = Utils.convertStringInLocalFormatToDate(entryDate);
            Date dateSaleDate = Utils.convertStringInLocalFormatToDate(saleDate);

            Property property = new Property(propertyId,
                intPrice,
                intSurface,
                description,
                addressTitle,
                address,
                pointOfInterest,
                dateEntryDate,
                dateSaleDate,
                propertyTypeId,
                agentId,
                intRooms,
                latitude,
                longitude);

            try {
                if (propertyId == PropertyConst.PROPERTY_ID_NOT_INITIALIZED) {
                    propertyId = databaseRepository.getPropertyRepository().insert(property);
                    // now we have new property id and we can send pending photos to database
                    List<Photo> photos = new ArrayList<>(cache.getPendingPhotos());
                    for (Photo photo : photos) {
                        // change property id with new property id
                        photo.setPropertyId(propertyId);
                        // clear cache and send to database
                        updatePhoto(photo);
                    }
                } else
                    databaseRepository.getPropertyRepository().update(property);
                // Callback to close windows
                addPropertyInterface.onPropertyAdded(propertyId);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private final MutableLiveData<Boolean> onCheckAllValuesMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> getOnCheckAllValuesLiveData() {return onCheckAllValuesMutableLiveData;}
    public boolean checkAllValues(String price,
                                  String surface,
                                  String description,
                                  String addressTitle,
                                  String address,
                                  String pointOfInterest,
                                  String entryDate,
                                  String saleDate,
                                  long propertyTypeId,
                                  long agentId,
                                  String rooms){

        // check all values
        boolean valuesOk = checkPriceValue(price) &
                checkSurfaceValue(surface) &
                checkDescriptionValue(description) &
                checkAddressTitleValue(addressTitle) &
                checkAddressValue(address) &
                checkPointOfInterestValue(pointOfInterest) &
                checkEntryDateValue(entryDate) &
                checkSaleDateValue(saleDate) &
                checkPropertyTypeIdValue(propertyTypeId) &
                checkAgentIdValue(agentId) &
                checkRoomsValue(rooms) &
                checkPendingPhoto();

        onCheckAllValuesMutableLiveData.setValue(valuesOk);
        return valuesOk;
    }


    public void addPhoto(Uri uri, String caption, long propertyId){
        Photo photo = new Photo(0, 0, uri.toString(), caption, propertyId);
        updatePhoto(photo);
    }

    /**
     * send photo to cache or to database
     * @param photo - photo
     */
    public void updatePhoto(Photo photo){
        if ((photo.getPropertyId() == PropertyConst.PROPERTY_ID_NOT_INITIALIZED) || (cache.isNotValidPhoto(photo))) {
            cache.update(photo);
        }
        else {
            cache.removePhoto(photo);
            if (photo.getId() == PropertyConst.PHOTO_ID_NOT_INITIALIZED) {
                databaseRepository.getPhotoRepository().insert(photo);
            }
            else {
                databaseRepository.getPhotoRepository().update(photo);
            }
        }
    }

    public void clearCache(){ cache.clear();
    }

    public void rememberValue(FieldKey key, String value){
        cache.setValue(key, value);
    }

    public void deletePhoto(Photo photo){
        cache.removePhoto(photo);
        if (photo.getId() != PropertyConst.PHOTO_ID_NOT_INITIALIZED) {
            databaseRepository.getPhotoRepository().delete(photo.getId());
        }
    }
}
