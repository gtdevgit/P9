package com.openclassrooms.realestatemanager.data.room.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity
public class PropertyDetailData {
    private long id;
    private int price;
    private int surface;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "address_title")
    private String addressTitle;
    private String address;
    @ColumnInfo(name = "points_of_interest")
    private String pointsOfInterest;
    private boolean available;
    @ColumnInfo(name = "entry_date")
    @TypeConverters(DateConverter.class)
    private Date entryDate;
    @ColumnInfo(name = "sale_date")
    @TypeConverters(DateConverter.class)
    private Date saleDate;
    @ColumnInfo(name = "property_type_id")
    private long propertyTypeId;
    @ColumnInfo(name = "property_category_id")
    private long propertyCategoryId;
    @ColumnInfo(name = "agent_id")
    private long agentId;
    private int rooms;
    private double latitude;
    private double longitude;
    @ColumnInfo(name = "agent_email")
    private String agentEmail;
    @ColumnInfo(name = "agent_name")
    private String agentName;
    @ColumnInfo(name = "agent_phone")
    private String agentPhone;
    @ColumnInfo(name = "property_category_name")
    private String categoryName;
    @ColumnInfo(name = "property_type_name")
    private String typeName;

    public PropertyDetailData(long id, int price, int surface, String description, String addressTitle, String address, String pointsOfInterest, boolean available, Date entryDate, Date saleDate, long propertyTypeId, long propertyCategoryId, long agentId, int rooms, double latitude, double longitude, String agentEmail, String agentName, String agentPhone, String categoryName, String typeName) {
        this.id = id;
        this.price = price;
        this.surface = surface;
        this.description = description;
        this.addressTitle = addressTitle;
        this.address = address;
        this.pointsOfInterest = pointsOfInterest;
        this.available = available;
        this.entryDate = entryDate;
        this.saleDate = saleDate;
        this.propertyTypeId = propertyTypeId;
        this.propertyCategoryId = propertyCategoryId;
        this.agentId = agentId;
        this.rooms = rooms;
        this.latitude = latitude;
        this.longitude = longitude;
        this.agentEmail = agentEmail;
        this.agentName = agentName;
        this.agentPhone = agentPhone;
        this.categoryName = categoryName;
        this.typeName = typeName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getSurface() {
        return surface;
    }

    public void setSurface(int surface) {
        this.surface = surface;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddressTitle() {
        return addressTitle;
    }

    public void setAddressTitle(String addressTitle) {
        this.addressTitle = addressTitle;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPointsOfInterest() {
        return pointsOfInterest;
    }

    public void setPointsOfInterest(String pointsOfInterest) {
        this.pointsOfInterest = pointsOfInterest;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public long getPropertyTypeId() {
        return propertyTypeId;
    }

    public void setPropertyTypeId(long propertyTypeId) {
        this.propertyTypeId = propertyTypeId;
    }

    public long getPropertyCategoryId() {
        return propertyCategoryId;
    }

    public void setPropertyCategoryId(long propertyCategoryId) {
        this.propertyCategoryId = propertyCategoryId;
    }

    public long getAgentId() {
        return agentId;
    }

    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAgentEmail() {
        return agentEmail;
    }

    public void setAgentEmail(String agentEmail) {
        this.agentEmail = agentEmail;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentPhone() {
        return agentPhone;
    }

    public void setAgentPhone(String agentPhone) {
        this.agentPhone = agentPhone;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
