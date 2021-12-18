package com.openclassrooms.realestatemanager.ui.propertylist.viewstate;

public class RowPropertyViewState {
    private long id;
    private String url;
    private String type;
    private String address;
    private String price;

    public RowPropertyViewState(long id, String url, String type, String address, String price) {
        this.id = id;
        this.url = url;
        this.type = type;
        this.address = address;
        this.price = price;
    }

    public long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public String getAddress() {
        return address;
    }

    public String getPrice() {
        return price;
    }
}
