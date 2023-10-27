package com.sourav.vegetables.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import com.sourav.vegetables.Model.Cart;
import com.sourav.vegetables.Model.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationDatabase extends SQLiteAssetHelper {

    private static final String DB_NAME = "notification.db";
    private static final int DB_VER = 1;

    public NotificationDatabase(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    // Notification from Firebase
    public List<Notification> getNotification() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"id", "title", "body", "send_time", "is_read"};
        String sqlTable = "tbl_notification";

        qb.setTables(sqlTable);

        Cursor c = qb.query(db, sqlSelect, null, null, null, null, "id DESC");

        final List<Notification> result = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                result.add(new Notification(
                        c.getInt(c.getColumnIndex("id")),
                        c.getString(c.getColumnIndex("title")),
                        c.getString(c.getColumnIndex("body")),
                        c.getString(c.getColumnIndex("send_time")),
                        c.getString(c.getColumnIndex("is_read"))));
            }
            while (c.moveToNext());
        }
        return result;
    }

    public void addNotification(Notification notification) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO tbl_notification(title, body, send_time, is_read) VALUES('%s','%s','%s', 'false');",
                notification.getTitle(), notification.getBody(), notification.getSend_time(), notification.getIs_read());

        db.execSQL(query);
    }

    public void markAllReadNotification() {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE tbl_notification SET is_read = 'true'");
        db.execSQL(query);
    }

    public int notificationUnreadItemCount() {

        int count = 0;

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM tbl_notification WHERE is_read = 'false'");
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                count = cursor.getInt(0);
            }
            while (cursor.moveToNext());
        }
        return count;
    }

    public int notificationItemCount() {


        int count = 0;

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM tbl_notification");
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                count = cursor.getInt(0);
            }
            while (cursor.moveToNext());
        }
        return count;
    }

    public void removeAllNotificationItems() {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM tbl_notification");
        db.execSQL(query);
    }

}