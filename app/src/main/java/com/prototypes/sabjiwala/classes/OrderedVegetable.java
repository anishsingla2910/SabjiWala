package com.prototypes.sabjiwala.classes;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class OrderedVegetable {
    public String vegetable_category;
    public String vegetable_name;
    public String vegetable_price;
    public String vegetable_price_per_unit;
    public String vegetable_quantity;
    public String vegetable_type;
    public String vegetable_id;
    
    public OrderedVegetable() {
        //empty constructor needed
    }
    
    public OrderedVegetable(String vegetable_category, String vegetable_name, String vegetable_price,
                            String vegetable_price_per_unit, String  vegetable_quantity, String vegetable_type, String vegetable_id) {
        this.vegetable_category = vegetable_category;
        this.vegetable_name = vegetable_name;
        this.vegetable_price = vegetable_price;
        this.vegetable_price_per_unit = vegetable_price_per_unit;
        this.vegetable_quantity = vegetable_quantity;
        this.vegetable_type = vegetable_type;
        this.vegetable_id = vegetable_id;
    }
    
    public String getVegetable_category() {
        return vegetable_category;
    }
    
    public void setVegetable_category(String vegetable_category) {
        this.vegetable_category = vegetable_category;
    }
    
    public String getVegetable_name() {
        return vegetable_name;
    }
    
    public void setVegetable_name(String vegetable_name) {
        this.vegetable_name = vegetable_name;
    }
    
    public String getVegetable_price() {
        return vegetable_price;
    }
    
    public void setVegetable_price(String vegetable_price) {
        this.vegetable_price = vegetable_price;
    }
    
    public String getVegetable_price_per_unit() {
        return vegetable_price_per_unit;
    }
    
    public void setVegetable_price_per_unit(String vegetable_price_per_unit) {
        this.vegetable_price_per_unit = vegetable_price_per_unit;
    }
    
    public String getVegetable_quantity() {
        return vegetable_quantity;
    }
    
    public void setVegetable_quantity(String vegetable_quantity) {
        this.vegetable_quantity = vegetable_quantity;
    }
    
    public String getVegetable_type() {
        return vegetable_type;
    }
    
    public void setVegetable_type(String vegetable_type) {
        this.vegetable_type = vegetable_type;
    }
    
    public String getVegetable_id() {
        return vegetable_id;
    }
    
    public void setVegetable_id(String vegetable_id) {
        this.vegetable_id = vegetable_id;
    }
}