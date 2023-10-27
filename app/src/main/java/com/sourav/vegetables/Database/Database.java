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

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME = "Vegetables.db";
    private static final int DB_VER = 1;


    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }


    // Carts
    public void addToCart(Cart cart) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO CartItems(food_id, name, description, price, image_url, min_unit_amount, unit, quantity, id_menu, menu_name) VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s');",
                cart.getFood_id(), cart.getName(), cart.getDescription(), cart.getPrice(), cart.getImage_url(), cart.getMin_unit_amount(), cart.getUnit(), cart.getQuantity(), cart.getId_menu(),cart.getMenu_name());

        db.execSQL(query);
    }

    public List<Cart> getCarts() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"id", "food_id", "name", "description", "price", "image_url", "min_unit_amount", "unit", "quantity", "id_menu", "menu_name"};
        String sqlTable = "CartItems";

        qb.setTables(sqlTable);

        Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);

        final List<Cart> result = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                result.add(new Cart(
                        c.getInt(c.getColumnIndex("id")),
                        c.getString(c.getColumnIndex("food_id")),
                        c.getString(c.getColumnIndex("name")),
                        c.getString(c.getColumnIndex("description")),
                        c.getString(c.getColumnIndex("price")),
                        c.getString(c.getColumnIndex("image_url")),
                        c.getString(c.getColumnIndex("min_unit_amount")),
                        c.getString(c.getColumnIndex("unit")),
                        c.getString(c.getColumnIndex("quantity")),
                        c.getString(c.getColumnIndex("id_menu")),
                        c.getString(c.getColumnIndex("menu_name"))));
            }
            while (c.moveToNext());
        }
        return result;
    }

    public void removeCartITem(int id) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM CartItems WHERE id = '%d'", id);
        db.execSQL(query);
    }

    public void clearCartITemFromFood(String food_id, String id_menu) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM CartItems WHERE food_id = '%s' AND id_menu = '%s'", food_id, id_menu);
        db.execSQL(query);
    }

    public void removeAllCartItems() {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM CartItems");
        db.execSQL(query);
    }

    public boolean checkExistence(String food_id) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT * FROM CartItems WHERE food_id = '%s'", food_id);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            // record exists
            return true;
        } else {
            // record not found
            return false;
        }
    }

    public String countQuantity(String food_id, String id_menu) {
        SQLiteDatabase db = getReadableDatabase();
        String quantity = "";
        Cursor cursor = null;
        String query = String.format("SELECT quantity FROM CartItems WHERE food_id = '%s' AND id_menu = '%s'", food_id, id_menu);
        try {
            cursor = db.rawQuery(query, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                quantity = cursor.getString(cursor.getColumnIndex("quantity"));
            }
            return quantity;
        } finally {
            cursor.close();
        }
    }

    public int cartItemCount() {

        int count = 0;

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM CartItems");
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                count = cursor.getInt(0);
            }
            while (cursor.moveToNext());
        }
        return count;
    }

    public void updateCartItem(Cart cartItem) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE CartItems SET quantity = %s WHERE id = %d", cartItem.getQuantity(), cartItem.getId());
        db.execSQL(query);
    }

    public void updateCartItemReorder(String quantity, String food_id, String id_menu) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE CartItems SET quantity = %s WHERE food_id = %s AND id_menu = '%s'", quantity, food_id, id_menu);
        db.execSQL(query);
    }

    /*// Favorites
    public List<Favorites> getFavorites() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"id", "product_id", "cat_id", "product_name", "model", "image_link", "price", "quantity", "parent_cat_name", "sub_cat_name"};
        String sqlTable = "Favorites";

        qb.setTables(sqlTable);

        Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);

        final List<Favorites> result = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                result.add(new Favorites(
                        c.getInt(c.getColumnIndex("id")),
                        c.getString(c.getColumnIndex("product_id")),
                        c.getString(c.getColumnIndex("product_name")),
                        c.getString(c.getColumnIndex("quantity")),
                        c.getString(c.getColumnIndex("price")),
                        c.getString(c.getColumnIndex("image_link")),
                        c.getString(c.getColumnIndex("menu_id")),
                        c.getString(c.getColumnIndex("menu_name"))));
            }
            while (c.moveToNext());
        }
        return result;
    }

    public void addFavorites(Favorites favorites)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO Favorites(product_id, cat_id, product_name, model, image_link, price, quantity, parent_cat_name, sub_cat_name) VALUES('%s','%s','%s','%s','%s','%s','%s','%s','%s');",
                favorites.getProduct_id(), favorites.getCat_id(), favorites.getProduct_name(), favorites.getModel(), favorites.getImage_link(), favorites.getPrice(), favorites.getQuantity(), favorites.getParent_cat_name(), favorites.getSub_cat_name());

        db.execSQL(query);
    }

    public void removeFromFavorites(String product_id)
    {
        SQLiteDatabase database = getReadableDatabase();
        String query = String.format("DELETE FROM Favorites WHERE product_id = '%s';", product_id);
        database.execSQL(query);
    }

    public boolean isFavorites(String product_id)
    {
        SQLiteDatabase database = getReadableDatabase();
        String query = String.format("SELECT * FROM Favorites WHERE product_id = '%s';", product_id);
        Cursor cursor = database.rawQuery(query, null);
        if(cursor.getCount() <= 0)
        {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public int favoritesItemCount() {

        int count = 0;

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM Favorites");
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                count = cursor.getInt(0);
            }
            while (cursor.moveToNext());
        }
        return count;
    }

    public void updateCartItemFromFav(String quantity, String product_id, String cat_id) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE CartItems SET quantity = %s WHERE product_id = %s AND cat_id = '%s'", quantity, product_id, cat_id);
        db.execSQL(query);
    }

    public void removeAllFavItems() {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM Favorites");
        db.execSQL(query);
    }*/

}