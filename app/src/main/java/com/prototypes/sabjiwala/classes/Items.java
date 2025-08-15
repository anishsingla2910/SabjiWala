package com.prototypes.sabjiwala.classes;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Items {

    public String category;
    public String code;
    public boolean isSelling;
    public String photo_url;
    public String name;
    public String price;
    public String type;
    public String unit;

    public Items(){
        //Empty constructor is necessary so please don't delete it
    }

    public Items(String category, String code, boolean isSelling, String photo_url, String name, String price, String type, String unit) {
        this.category = category;
        this.code = code;
        this.isSelling = isSelling;
        this.photo_url = photo_url;
        this.name = name;
        this.price = price;
        this.type = type;
        this.unit = unit;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isSelling() {
        return isSelling;
    }

    public void setSelling(boolean selling) {
        isSelling = selling;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}