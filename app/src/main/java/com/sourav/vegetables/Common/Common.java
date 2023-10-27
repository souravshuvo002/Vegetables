package com.sourav.vegetables.Common;

import com.sourav.vegetables.Api.FCMClient;
import com.sourav.vegetables.Api.IFCMService;
import com.sourav.vegetables.Model.TextScroll;
import com.sourav.vegetables.Model.User;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Common {

    public static String User_email = "";
    public static String User_phone = "";
    public static String LOGIN_PHONE = "";
    public static String User_ID = "";
    public static User currentUser;
    public static TextScroll textScroll;

    public static String menu_id = "";
    public static String menu_name = "";
    public static String id_order = "";

    // SHOW on CONFIRMATION ACTIVITY AFTER ORDER PLACED
    public static String NAME = "";
    public static String PHONE = "";
    public static String EMAIL = "";
    public static String ADDRESS = "";
    public static String SUBTOTAL = "";
    public static String TOTAL = "";
    public static String COUPON_TOTAL = "";
    public static boolean isCouponApplied = false;
    public static String DELIVERY = "";
    public static String PAYMETHOD = "";
    public static String PAYSTATUS = "";
    public static String DELIVERY_DATE = "";
    public static String DELIVERY_TIME = "";


    private static final String FCM_API = "https://fcm.googleapis.com/";

    public static IFCMService getFCMService(){
        return FCMClient.getClient(FCM_API).create(IFCMService.class);
    }

    public static String convertCodeToStatus(String code)
    {
        if(code.equals("1"))
            return "Pending";
        else if(code.equals("2"))
            return "Accepted";
        else if(code.equals("3"))
            return "Rejected";
        else if(code.equals("4"))
            return "Shipping";
        else if(code.equals("5"))
            return "Completed";
        else if(code.equals("6"))
            return "Cancelled";
        else
            return "No result";
    }

    public static String covert24Time12(String time)
    {
        DateFormat format1 = new SimpleDateFormat("HH:mm");
        try {
            Date date = format1.parse(time);
            SimpleDateFormat format2 = new SimpleDateFormat("hh:mm a");
            String result = format2.format(date);
            return result;
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
}
