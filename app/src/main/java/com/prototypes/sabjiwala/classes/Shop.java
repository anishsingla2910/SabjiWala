package com.prototypes.sabjiwala.classes;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Shop extends Model {
    public Timestamp createdOn;
    public GeoPoint location;
    public String ownerName;
    public String shopAddress;
    public boolean shopStatus;
    public String shopName;
    public int radiusSupplied;
    public String shopNumber;

    public Shop() {
        //empty constructor is needed
    }

    public Shop(Timestamp createdOn, GeoPoint location, String ownerName, String shopAddress, boolean shopStatus, String shopName, int radiusSupplied, String shopNumber) {
        this.createdOn = createdOn;
        this.location = location;
        this.ownerName = ownerName;
        this.shopAddress = shopAddress;
        this.shopStatus = shopStatus;
        this.shopName = shopName;
        this.radiusSupplied = radiusSupplied;
        this.shopNumber = shopNumber;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public boolean isShopStatus() {
        return shopStatus;
    }

    public void setShopStatus(boolean shopStatus) {
        this.shopStatus = shopStatus;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public int getRadiusSupplied() {
        return radiusSupplied;
    }

    public void setRadiusSupplied(int radiusSupplied) {
        this.radiusSupplied = radiusSupplied;
    }

    public String getShopNumber() {
        return shopNumber;
    }

    public void setShopNumber(String shopNumber) {
        this.shopNumber = shopNumber;
    }
}