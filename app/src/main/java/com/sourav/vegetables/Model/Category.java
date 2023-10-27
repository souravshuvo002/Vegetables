package com.sourav.vegetables.Model;

public class Category {

    private String id, name, image_url, total_food_items;

    public Category() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getTotal_food_items() {
        return total_food_items;
    }

    public void setTotal_food_items(String total_food_items) {
        this.total_food_items = total_food_items;
    }
}
