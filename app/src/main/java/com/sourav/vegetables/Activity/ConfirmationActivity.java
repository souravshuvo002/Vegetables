package com.sourav.vegetables.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sourav.vegetables.Api.ApiClient;
import com.sourav.vegetables.Api.ApiService;
import com.sourav.vegetables.Api.ApiURL;
import com.sourav.vegetables.Api.IFCMService;
import com.sourav.vegetables.Common.Common;
import com.sourav.vegetables.Database.Database;
import com.sourav.vegetables.Helper.NetWorkConfig;
import com.sourav.vegetables.Model.DataMessage;
import com.sourav.vegetables.Model.MyResponse;
import com.sourav.vegetables.Model.Result;
import com.sourav.vegetables.R;

import java.util.HashMap;
import java.util.Map;

public class ConfirmationActivity extends AppCompatActivity {

    private NetWorkConfig netWorkConfig;
    private LinearLayout linearLayCoupon;
    private TextView textViewOrderID, textViewUserName, textViewUserPhone, textViewUserAddress,
            textViewSubtotal, textViewCouponAmount, textViewDeliveryCharge, textViewOrderTotal, textViewPaymentMethod,
            textViewPaymentStatus;
    private TextView textViewDELDATE, textViewDELTIME;
    private Button btn_cancelOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        netWorkConfig = new NetWorkConfig(ConfirmationActivity.this);

        new Database(getApplicationContext()).removeAllCartItems();

        // Change status bar color
        changeStatusBarColor("#" + Integer.toHexString(ContextCompat.getColor
                (getApplicationContext(), R.color.colorPrimary) & 0x00ffffff));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Order #" + Common.id_order);

        //getting views
        linearLayCoupon = (LinearLayout) findViewById(R.id.linearLayCoupon);
        textViewOrderID = (TextView) findViewById(R.id.textViewOrderID);
        textViewUserName = (TextView) findViewById(R.id.textViewUserName);
        textViewUserPhone = (TextView) findViewById(R.id.textViewUserPhone);
        textViewUserAddress = (TextView) findViewById(R.id.textViewUserAddress);
        textViewSubtotal = (TextView) findViewById(R.id.textViewSubtotal);
        textViewCouponAmount = (TextView) findViewById(R.id.textViewCouponAmount);
        textViewDeliveryCharge = (TextView) findViewById(R.id.textViewDeliveryCharge);
        textViewOrderTotal = (TextView) findViewById(R.id.textViewOrderTotal);
        textViewPaymentMethod = (TextView) findViewById(R.id.textViewPaymentMethod);
        textViewPaymentStatus = (TextView) findViewById(R.id.textViewPaymentStatus);
        textViewDELDATE = (TextView) findViewById(R.id.textViewDELDATE);
        textViewDELTIME = (TextView) findViewById(R.id.textViewDELTIME);
        btn_cancelOrder = (Button) findViewById(R.id.btn_cancelOrder);

        btn_cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCancelReasonDialog();
            }
        });

        loadData();

    }

    private void loadData() {
        textViewOrderID.setText(Common.id_order);
        textViewUserName.setText(Common.NAME);
        textViewUserPhone.setText(Common.PHONE);
        textViewUserAddress.setText(Common.ADDRESS);
        textViewDELDATE.setText(Common.DELIVERY_DATE);
        textViewDELTIME.setText(Common.DELIVERY_TIME);
        textViewSubtotal.setText(Common.SUBTOTAL);
        textViewDeliveryCharge.setText(Common.DELIVERY);
        textViewOrderTotal.setText(Common.TOTAL);
        textViewPaymentMethod.setText(Common.PAYMETHOD);
        textViewPaymentStatus.setText(Common.PAYSTATUS);

        if(Common.isCouponApplied)
        {
            linearLayCoupon.setVisibility(View.VISIBLE);
            textViewCouponAmount.setText(Common.COUPON_TOTAL);
        }
        else
        {
            linearLayCoupon.setVisibility(View.GONE);
        }
    }


    private void showCancelReasonDialog() {


        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ConfirmationActivity.this);
        alertDialog.setTitle("Cancellation Request");
        alertDialog.setMessage("Please select reason");

        View myView = LayoutInflater.from(ConfirmationActivity.this).inflate(R.layout.custom_cancel_order_reason_dialog, null);

        final Spinner spinnerReason = (Spinner) myView.findViewById(R.id.spinner_reason);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ConfirmationActivity.this,
                R.array.cancel_reason, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerReason.setAdapter(adapter);
        spinnerReason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        alertDialog.setView(myView);
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String reason = String.valueOf(spinnerReason.getSelectedItem());

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ConfirmationActivity.this);
                String USER_ID = sharedPreferences.getString("USER_ID", null);

                //Update Order Status
                //building retrofit object
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(ApiURL.SERVER_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                //Defining retrofit api service
                ApiService service = retrofit.create(ApiService.class);
                Call<Result> call = service.updateOrderCancel(Common.id_order, USER_ID, "6", reason);

                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        Toast.makeText(getApplicationContext(), "Order has been cancelled successfully", Toast.LENGTH_LONG).show();
                        //load Order details
                        btn_cancelOrder.setVisibility(View.GONE);
                        Intent i = new Intent(ConfirmationActivity.this, AllOrderHistoryActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {

                    }
                });
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final android.app.AlertDialog dialog = alertDialog.create();
        alertDialog.show();
        Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (b != null) {
            b.setTextColor(Color.parseColor("#FF8A65"));
        }
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
    public boolean onSupportNavigateUp() {
        Intent i = new Intent(ConfirmationActivity.this, AllOrderHistoryActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ConfirmationActivity.this, AllOrderHistoryActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
