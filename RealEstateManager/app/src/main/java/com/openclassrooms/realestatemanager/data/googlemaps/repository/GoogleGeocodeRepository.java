package com.openclassrooms.realestatemanager.data.googlemaps.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.openclassrooms.realestatemanager.data.googlemaps.api.GoogleGeocodeClient;
import com.openclassrooms.realestatemanager.data.googlemaps.api.GoogleGeocodeInterface;
import com.openclassrooms.realestatemanager.data.googlemaps.model.geocode.Geocode;
import com.openclassrooms.realestatemanager.data.googlemaps.model.geocode.Geometry;
import com.openclassrooms.realestatemanager.data.googlemaps.model.geocode.Location;
import com.openclassrooms.realestatemanager.data.googlemaps.model.geocode.Result;
import com.openclassrooms.realestatemanager.tag.Tag;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoogleGeocodeRepository {

    private final GoogleGeocodeInterface api;
    private final String apiKey;

    public GoogleGeocodeRepository(String apiKey) {
        this.apiKey = apiKey;
        this.api = GoogleGeocodeClient.getClient();
    }

    private String getApiKey() { return apiKey; }

    private final MutableLiveData<String> errorMutableLiveData = new MutableLiveData<>();
    public LiveData<String> getErrorLiveData() { return errorMutableLiveData; }

    public Call<Geocode> getGeocode(String address) {
        Log.d(Tag.TAG, "getGeocode() called with: address = [" + address + "]");
        try {
            return api.getGeocode(address, getApiKey());
        } catch (Exception e) {
            Log.d(Tag.TAG, "getGeocode() Exception = [" + e.getMessage() + "]");
            return null;
        }
    }

    private final MutableLiveData<Geocode> geocodeMutableLiveData = new MutableLiveData<>();
    public LiveData<Geocode> getGeocodeLiveData() { return geocodeMutableLiveData; }

    public void loadGeocode(String address){
        Call<Geocode> call = getGeocode(address);
        call.enqueue(new Callback<Geocode>() {
            @Override
            public void onResponse(Call<Geocode> call, Response<Geocode> response) {
                if (response.isSuccessful()) {
                    Geocode geocode = response.body();
                    geocodeMutableLiveData.setValue(geocode);
                }
            }

            @Override
            public void onFailure(Call<Geocode> call, Throwable t) {
                errorMutableLiveData.postValue(t.getMessage());
            }
        });
    }

    private LatLng extractLocation(Geocode geocode){
        if (geocode != null) {
            List<Result> results = geocode.getResults();
            if ((results != null) && (results.size() > 0)){
                Geometry geometry = results.get(0).getGeometry();
                if (geometry != null) {
                    Location location = geometry.getLocation();
                    if (location != null) {
                        double latitude = location.getLat();
                        double longitude = location.getLng();
                        return new LatLng(latitude, longitude);
                    }
                }
            }
        }
        return null;
    }

    private final MutableLiveData<LatLng> locationByAddressMutableLiveData = new MutableLiveData<>();
    public LiveData<LatLng> getLocationByAddressLiveData() { return locationByAddressMutableLiveData; }

    // result with LiveData
    public void loadLocationByAddress(String address){
        Call<Geocode> call = getGeocode(address);
        call.enqueue(new Callback<Geocode>() {
            @Override
            public void onResponse(Call<Geocode> call, Response<Geocode> response) {
                if (response.isSuccessful()) {
                    Geocode geocode = response.body();
                    LatLng latLng = extractLocation(geocode);
                    locationByAddressMutableLiveData.setValue(latLng);
                }
            }

            @Override
            public void onFailure(Call<Geocode> call, Throwable t) {
                errorMutableLiveData.postValue(t.getMessage());
            }
        });
    }

    public interface LocationByAddressListener{
        void onLocationFound(LatLng latLng);
    }

    // result with listener
    public void getLocationByAddress(String address, LocationByAddressListener locationByAddressListener){
        Call<Geocode> call = getGeocode(address);
        call.enqueue(new Callback<Geocode>() {
            @Override
            public void onResponse(Call<Geocode> call, Response<Geocode> response) {
                if (response.isSuccessful()) {
                    Geocode geocode = response.body();
                    LatLng latLng = extractLocation(geocode);
                    locationByAddressListener.onLocationFound(latLng);
                }
            }

            @Override
            public void onFailure(Call<Geocode> call, Throwable t) {
                errorMutableLiveData.postValue(t.getMessage());
            }
        });
    }
}
