package com.sourav.vegetables.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {

    @SerializedName("error")
    private Boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("user")
    private User user;

    @SerializedName("banner")
    private List<Banner> banners;

    @SerializedName("menu")
    private List<Category> menuList;

    @SerializedName("foods")
    private List<Foods> foodsList;

    @SerializedName("orders")
    private List<Order> orderList;

    @SerializedName("orderDetails")
    private List<Order> orderDetails;

    @SerializedName("orderItems")
    private List<Order> orderItems;

    @SerializedName("token")
    private Token token;

    @SerializedName("area")
    private List<Area> areaList;

    @SerializedName("coupon")
    private Coupon couponDetails;

    @SerializedName("slotssss")
    private List<DeliverySlot> slotList;

    @SerializedName("allreviews")
    private List<Review> allReviewList;

    @SerializedName("textScroll")
    private TextScroll textScroll;


    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Banner> getBanners() {
        return banners;
    }

    public void setBanners(List<Banner> banners) {
        this.banners = banners;
    }

    public List<Category> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<Category> menuList) {
        this.menuList = menuList;
    }

    public List<Foods> getFoodsList() {
        return foodsList;
    }

    public void setFoodsList(List<Foods> foodsList) {
        this.foodsList = foodsList;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    public List<Order> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<Order> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public List<Order> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<Order> orderItems) {
        this.orderItems = orderItems;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public List<Area> getAreaList() {
        return areaList;
    }

    public void setAreaList(List<Area> areaList) {
        this.areaList = areaList;
    }

    public Coupon getCouponDetails() {
        return couponDetails;
    }

    public void setCouponDetails(Coupon couponDetails) {
        this.couponDetails = couponDetails;
    }

    public List<DeliverySlot> getSlotList() {
        return slotList;
    }

    public void setSlotList(List<DeliverySlot> slotList) {
        this.slotList = slotList;
    }

    public List<Review> getAllReviewList() {
        return allReviewList;
    }

    public void setAllReviewList(List<Review> allReviewList) {
        this.allReviewList = allReviewList;
    }

    public TextScroll getTextScroll() {
        return textScroll;
    }

    public void setTextScroll(TextScroll textScroll) {
        this.textScroll = textScroll;
    }
}
