package com.sourav.vegetables.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.sourav.vegetables.Api.ApiClient;
import com.sourav.vegetables.Api.ApiService;
import com.sourav.vegetables.Database.Database;
import com.sourav.vegetables.Helper.NetWorkConfig;
import com.sourav.vegetables.Model.Result;
import com.sourav.vegetables.R;

public class ProfileActivity extends AppCompatActivity {

    public LinearLayout layOrderInfo, layLogOutInfo, layPersonalInfo, layChanePasswordInfo, layNotificationInfo, layReviewInfo;
    public TextView textViewUserName, textViewUserPhone, textViewUserEmail, textViewUserAddress, textViewContactEdit;
    private NetWorkConfig netWorkConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Change status bar color
        changeStatusBarColor("#" + Integer.toHexString(ContextCompat.getColor
                (getApplicationContext(), R.color.colorPrimary) & 0x00ffffff));

        netWorkConfig = new NetWorkConfig(this);

        /**
         *  Network Connection Check
         */
        if (!netWorkConfig.isNetworkAvailable()) {
            netWorkConfig.createNetErrorDialog();
            return;
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");


        textViewContactEdit = (TextView) findViewById(R.id.textViewContactEdit);
        textViewUserName = (TextView) findViewById(R.id.textViewUserName);
        textViewUserPhone = (TextView) findViewById(R.id.textViewUserPhone);
        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        textViewUserAddress = (TextView) findViewById(R.id.textViewUserAddress);
        layPersonalInfo = (LinearLayout) findViewById(R.id.layPersonalInfo);
        layOrderInfo = (LinearLayout) findViewById(R.id.layOrderInfo);
        layChanePasswordInfo = (LinearLayout) findViewById(R.id.layChanePasswordInfo);
        layLogOutInfo = (LinearLayout) findViewById(R.id.layLogOutInfo);
        layNotificationInfo = (LinearLayout) findViewById(R.id.layNotificationInfo);
        layReviewInfo = (LinearLayout) findViewById(R.id.layReviewInfo);

        textViewContactEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, EditCustomerInfoActivity.class));
            }
        });

        layOrderInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, AllOrderHistoryActivity.class));
            }
        });
        layChanePasswordInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class));
            }
        });
        layNotificationInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, NotificationActivity.class));
            }
        });
        layReviewInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, ShowAllReviewsActivity.class));
            }
        });


        layLogOutInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogOut();
            }
        });

        // get Customer Data
        getCustomerData();
    }

    private void getCustomerData() {
        final android.app.AlertDialog waitingDialog = new SpotsDialog(ProfileActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");

        String PHONE;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ProfileActivity.this);
        PHONE = sharedPreferences.getString("PHONE", null);

        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> call = service.getUser(PHONE);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                waitingDialog.dismiss();

                if (!response.body().getError()) {

                    layPersonalInfo.setVisibility(View.VISIBLE);
                    textViewUserName.setText(response.body().getUser().getUsername());
                    textViewUserEmail.setText(response.body().getUser().getEmail());
                    textViewUserPhone.setText(response.body().getUser().getPhone());
                    textViewUserAddress.setText(response.body().getUser().getAddress());
                } else {
                    Toast.makeText(ProfileActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getCustomerData();
    }

    private void changeStatusBarColor(String color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

    private void LogOut() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileActivity.this);
        alertDialog.setTitle("Log out Application");
        alertDialog.setMessage("Do you really want to log out from Vegetables?");
        alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.setNegativeButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ProfileActivity.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                // Clearing Data from Shared Preferences
                editor.clear();
                editor.commit();

                //Delete Remember user & password
                FirebaseAuth.getInstance().signOut();

                // Delete all cart items
                new Database(getApplicationContext()).removeAllCartItems();

                // Clear All Activity
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Toast.makeText(ProfileActivity.this, "Successfully logged out!", Toast.LENGTH_LONG).show();
                finish();
            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.show();
        Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (b != null) {
            b.setTextColor(Color.parseColor("#FF8A65"));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
