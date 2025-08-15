package com.prototypes.sabjiwala.classes;

public class OrderVegetable {
    public String ID;
    public String CATEGORY;
    public String QUANTITY;
    
    public OrderVegetable() {
        //empty constructor needed
    }
    
    public OrderVegetable(String ID, String CATEGORY, String QUANTITY) {
        this.ID = ID;
        this.CATEGORY = CATEGORY;
        this.QUANTITY = QUANTITY;
    }
    
    public String getID() {
        return ID;
    }
    
    public void setID(String ID) {
        this.ID = ID;
    }
    
    public String getCATEGORY() {
        return CATEGORY;
    }
    
    public void setCATEGORY(String CATEGORY) {
        this.CATEGORY = CATEGORY;
    }
    
    public String getQUANTITY() {
        return QUANTITY;
    }
    
    public void setQUANTITY(String QUANTITY) {
        this.QUANTITY = QUANTITY;
    }
}