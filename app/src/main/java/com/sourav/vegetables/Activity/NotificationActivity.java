package com.sourav.vegetables.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sourav.vegetables.Adapter.NotificationAdapter;
import com.sourav.vegetables.Database.NotificationDatabase;
import com.sourav.vegetables.Model.Notification;
import com.sourav.vegetables.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private SwipeRefreshLayout swipe_layout;
    private LinearLayout empty_view;
    private RecyclerView recyclerViewNotification;
    public List<Notification> notificationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        // Change status bar color
        changeStatusBarColor("#" + Integer.toHexString(ContextCompat.getColor
                (getApplicationContext(), R.color.colorPrimary) & 0x00ffffff));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notification");

        //getting views
        empty_view = (LinearLayout) findViewById(R.id.empty_view);
        swipe_layout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipe_layout.setOnRefreshListener(this);
        swipe_layout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_dark));

        recyclerViewNotification = (RecyclerView) findViewById(R.id.notificationRecyclerView);
        recyclerViewNotification.setLayoutManager(new LinearLayoutManager(NotificationActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerViewNotification.setHasFixedSize(true);
        recyclerViewNotification.setItemAnimator(new DefaultItemAnimator());
        recyclerViewNotification.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));



        loadNotificationItems();
    }

    private void deleteAllNotifications() {
        new AlertDialog.Builder(NotificationActivity.this)
                .setTitle("Are you sure?")
                .setMessage("Do you really want to delete all notifications")
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        new NotificationDatabase(NotificationActivity.this).removeAllNotificationItems();
                        loadNotificationItems();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void loadNotificationItems() {
        swipe_layout.setRefreshing(false);
        notificationList = new NotificationDatabase(NotificationActivity.this).getNotification();
        if (notificationList.isEmpty()) {
            Toast.makeText(NotificationActivity.this, "Empty Data", Toast.LENGTH_SHORT).show();
            //linearLayoutVisible.setVisibility(View.VISIBLE);
            recyclerViewNotification.setVisibility(View.GONE);
            empty_view.setVisibility(View.VISIBLE);
            //startAnimation();
        } else {
            empty_view.setVisibility(View.GONE);
            recyclerViewNotification.setVisibility(View.VISIBLE);
            NotificationAdapter adapter = new NotificationAdapter(notificationList, NotificationActivity.this);
            recyclerViewNotification.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notification, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_all_read)
        {
            new NotificationDatabase(NotificationActivity.this).markAllReadNotification();
            onBackPressed();
        }
        else if (item.getItemId() == R.id.menu_del_all)
        {
            deleteAllNotifications();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void changeStatusBarColor(String color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

    @Override
    public void onRefresh() {
        loadNotificationItems();
    }
}
