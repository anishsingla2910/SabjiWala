package com.prototypes.sabjiwala.classes;

public class OrderHistoryLayout {
    public String vegetable_names;
    public String vegetable_sizes;
    public Order orders;

    public OrderHistoryLayout(String vegetable_names, String vegetable_sizes, Order orders) {
        this.vegetable_names = vegetable_names;
        this.vegetable_sizes = vegetable_sizes;
        this.orders = orders;
    }

    public String getVegetable_names() {
        return vegetable_names;
    }

    public void setVegetable_names(String vegetable_names) {
        this.vegetable_names = vegetable_names;
    }

    public String getVegetable_sizes() {
        return vegetable_sizes;
    }

    public void setVegetable_sizes(String vegetable_sizes) {
        this.vegetable_sizes = vegetable_sizes;
    }

    public Order getOrders() {
        return orders;
    }

    public void setOrders(Order orders) {
        this.orders = orders;
    }
}