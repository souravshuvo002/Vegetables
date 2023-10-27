package com.sourav.vegetables.Model;

public class Cart {

    private int id;
    private String food_id, name, description, price, discount_price, image_url, min_unit_amount, unit, id_menu, status, date_added;
    private String quantity, menu_name;

    public Cart() {
    }

    public Cart(int id, String food_id, String name, String description, String price, String image_url, String min_unit_amount, String unit, String quantity, String id_menu, String menu_name) {
        this.id = id;
        this.food_id = food_id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image_url = image_url;
        this.min_unit_amount = min_unit_amount;
        this.unit = unit;
        this.quantity = quantity;
        this.id_menu = id_menu;
        this.menu_name = menu_name;
    }

    public Cart(String food_id, String name, String description, String price, String image_url, String min_unit_amount, String unit, String quantity, String id_menu, String menu_name) {
        this.food_id = food_id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image_url = image_url;
        this.min_unit_amount = min_unit_amount;
        this.unit = unit;
        this.quantity = quantity;
        this.id_menu = id_menu;
        this.menu_name = menu_name;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFood_id() {
        return food_id;
    }

    public void setFood_id(String food_id) {
        this.food_id = food_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount_price() {
        return discount_price;
    }

    public void setDiscount_price(String discount_price) {
        this.discount_price = discount_price;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getMin_unit_amount() {
        return min_unit_amount;
    }

    public void setMin_unit_amount(String min_unit_amount) {
        this.min_unit_amount = min_unit_amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getId_menu() {
        return id_menu;
    }

    public void setId_menu(String id_menu) {
        this.id_menu = id_menu;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getMenu_name() {
        return menu_name;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }
}
