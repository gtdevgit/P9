package com.openclassrooms.realestatemanager.ui.propertyedit.viewstate;

import com.openclassrooms.realestatemanager.data.room.model.Photo;

import java.util.List;

public class PropertyEditViewState {
    private final String addressTitle;
    private final String address;
    private final String description;
    private final String pointOfInterest;
    private final String price;
    private final String surface;
    private final String rooms;
    private final String entryDate;
    private final String saleDate;
    private final long agentId;
    private final String agentName;
    private final long propertyTypeId;
    private final String propertyTypeName;
    private final double latitude;
    private final double longitude;
    private final List<Photo> photos;
    private final String googleStaticMapUrl;

    public PropertyEditViewState(String addressTitle, String address, String description, String pointOfInterest, String price, String surface, String rooms, String entryDate, String saleDate, long agentId, String agentName, long propertyTypeId, String propertyTypeName, double latitude, double longitude, List<Photo> photos, String googleStaticMapUrl) {
        this.addressTitle = addressTitle;
        this.address = address;
        this.description = description;
        this.pointOfInterest = pointOfInterest;
        this.price = price;
        this.surface = surface;
        this.rooms = rooms;
        this.entryDate = entryDate;
        this.saleDate = saleDate;
        this.agentId = agentId;
        this.agentName = agentName;
        this.propertyTypeId = propertyTypeId;
        this.propertyTypeName = propertyTypeName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.photos = photos;
        this.googleStaticMapUrl = googleStaticMapUrl;
    }

    public String getAddressTitle() {
        return addressTitle;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public String getPointOfInterest() {
        return pointOfInterest;
    }

    public String getPrice() {
        return price;
    }

    public String getSurface() {
        return surface;
    }

    public String getRooms() {
        return rooms;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public long getAgentId() {
        return agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public long getPropertyTypeId() {
        return propertyTypeId;
    }

    public String getPropertyTypeName() {
        return propertyTypeName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public String getGoogleStaticMapUrl() {
        return googleStaticMapUrl;
    }
}
