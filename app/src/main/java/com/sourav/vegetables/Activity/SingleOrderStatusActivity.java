package com.sourav.vegetables.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sourav.vegetables.Adapter.SingleOrderStatusAdapter;
import com.sourav.vegetables.Api.ApiClient;
import com.sourav.vegetables.Api.ApiService;
import com.sourav.vegetables.Api.ApiURL;
import com.sourav.vegetables.Common.Common;
import com.sourav.vegetables.Helper.NetWorkConfig;
import com.sourav.vegetables.Model.Result;
import com.sourav.vegetables.R;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.JELLY_BEAN;

public class SingleOrderStatusActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, RatingDialogListener {

    RelativeLayout relativeLayout;
    LinearLayout linearLayDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerViewOrderItems;
    TextView textViewOrderID, textViewOrderDate, textViewTotal_items, textViewItems_Price, textViewAddress;
    TextView textViewOrderStatus;
    TextView textViewDelDateTime;
    ImageView imageViewBackButton;
    private Button btn_cancelOrder, btn_review;
    public SingleOrderStatusAdapter adapter;
    public String order_id, order_status;
    public double test_total_price = 0.00, total_price = 0.00;
    android.app.AlertDialog waitingDialog;
    private NetWorkConfig netWorkConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_order_status);

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

        order_id = getIntent().getStringExtra("ID_ORDER");

        if (SDK_INT >= JELLY_BEAN) {
            enableChangingTransition();
        }

        // getting views
        linearLayDialog = (LinearLayout) findViewById(R.id.linearLayDialog);
        relativeLayout = (RelativeLayout) findViewById(R.id.LayMain);
        imageViewBackButton = (ImageView) findViewById(R.id.imageViewBack);
        textViewOrderID = (TextView) findViewById(R.id.textViewOrderID);
        textViewOrderDate = (TextView) findViewById(R.id.textViewOrderDate);
        textViewTotal_items = (TextView) findViewById(R.id.textViewTotalItems);
        textViewItems_Price = (TextView) findViewById(R.id.textViewItems_Price);
        textViewAddress = (TextView) findViewById(R.id.textViewAddress);
        textViewOrderStatus = (TextView) findViewById(R.id.textViewOrderStatus);
        textViewDelDateTime = (TextView) findViewById(R.id.textViewDelDateTime);
        btn_cancelOrder = (Button) findViewById(R.id.btn_cancelOrder);
        btn_review = (Button) findViewById(R.id.btn_review);
        recyclerViewOrderItems = (RecyclerView) findViewById(R.id.recycler_view_OrderItems);
        recyclerViewOrderItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewOrderItems.setHasFixedSize(true);
        recyclerViewOrderItems.setItemAnimator(new DefaultItemAnimator());
        recyclerViewOrderItems.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(SingleOrderStatusActivity.this);
        // show loader and fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        //load Order details
                        loadOrderDetails(order_id);
                    }
                }
        );

        imageViewBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Common.convertCodeToStatus(order_status).equalsIgnoreCase("Shipping")) {
                    showCancelDialog();
                } else {
                    showCancelReasonDialog();
                }
            }
        });

        btn_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRatingBar();
            }
        });

        linearLayDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showOrderDetailsDialog();
            }
        });

        //load Order details
        loadOrderDetails(order_id);

        waitingDialog = new SpotsDialog(SingleOrderStatusActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Pleas wait ...");
    }

    private void showRatingBar() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not Good", "Quite Ok", "Very Good", "Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate us about service")
                .setDescription("Please select some stars and give feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here....")
                .setHintTextColor(android.R.color.white)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(SingleOrderStatusActivity.this)
                .show();
    }

    @Override
    public void onPositiveButtonClicked(int rateValue, String comment) {

        String USER_ID, USERNAME;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SingleOrderStatusActivity.this);
        USERNAME = sharedPreferences.getString("USERNAME", null);
        USER_ID = sharedPreferences.getString("USER_ID", null);

        final android.app.AlertDialog waitingDialog = new SpotsDialog(this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");

        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> call = service.addReview(USER_ID,
                order_id,
                USERNAME,
                comment,
                String.valueOf(rateValue), "1",
                Common.getDateTime());

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                waitingDialog.dismiss();
                //linearLay.setVisibility(View.VISIBLE);
                //getProductReviews(Common.singleProduct.getProduct_id());
                btn_review.setVisibility(View.GONE);
                Toasty.success(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT, true).show();
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toasty.error(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT, true).show();
                waitingDialog.dismiss();
            }
        });
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

    private void checkReview() {
        swipeRefreshLayout.setRefreshing(true);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SingleOrderStatusActivity.this);
        String USER_ID = sharedPreferences.getString("USER_ID", null);

        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> call = service.checkReview(order_id, USER_ID);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                if(!response.body().getError())
                {
                    if(order_status.equalsIgnoreCase("5"))
                    {
                        btn_review.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        btn_review.setVisibility(View.GONE);
                    }
                }

                waitingDialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    private void showCancelReasonDialog() {


        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SingleOrderStatusActivity.this);
        alertDialog.setTitle("Cancellation Request");
        alertDialog.setMessage("Please select reason");

        View myView = LayoutInflater.from(SingleOrderStatusActivity.this).inflate(R.layout.custom_cancel_order_reason_dialog, null);

        final Spinner spinnerReason = (Spinner) myView.findViewById(R.id.spinner_reason);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(SingleOrderStatusActivity.this,
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

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SingleOrderStatusActivity.this);
                String USER_ID = sharedPreferences.getString("USER_ID", null);

                //Update Order Status
                //building retrofit object
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(ApiURL.SERVER_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                //Defining retrofit api service
                ApiService service = retrofit.create(ApiService.class);
                Call<Result> call = service.updateOrderCancel(order_id, USER_ID, "6", reason);

                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        Toast.makeText(getApplicationContext(), "Order has been cancelled successfully", Toast.LENGTH_LONG).show();
                        //load Order details
                        loadOrderDetails(order_id);

                        btn_cancelOrder.setVisibility(View.GONE);


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

    private void loadOrderDetails(final String order_id) {

        swipeRefreshLayout.setRefreshing(true);

        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> call = service.getOrderDetails(order_id);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                relativeLayout.setVisibility(View.VISIBLE);

                textViewOrderID.setText("Order #" + order_id);
                //Date
                String strCurrentDate = response.body().getOrderDetails().get(0).getOrder_date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date newDate = null;
                try {
                    newDate = format.parse(strCurrentDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                format = new SimpleDateFormat("MMM dd, yyyy hh:mm");
                String date = format.format(newDate);
                textViewOrderDate.setText("Order date: " + date);
                total_price = Double.parseDouble(response.body().getOrderDetails().get(0).getTotal_price());
                textViewItems_Price.setText(getResources().getString(R.string.currency_sign) + response.body().getOrderDetails().get(0).getTotal_price());
                textViewAddress.setText("Billing Address\n" + response.body().getOrderDetails().get(0).getUsername() +
                        "\n" + response.body().getOrderDetails().get(0).getAddress() +
                        "\n" + response.body().getOrderDetails().get(0).getEmail() +
                        "\n" + response.body().getOrderDetails().get(0).getPhone());
                textViewOrderStatus.setText("Status: " + Common.convertCodeToStatus(response.body().getOrderDetails().get(0).getOrder_status()));

                if(Common.convertCodeToStatus(response.body().getOrderDetails().get(0).getOrder_status()).equalsIgnoreCase("Completed"))
                {
                    String strCurrentDate3 = response.body().getOrderDetails().get(0).getFood_delivery_date();
                    SimpleDateFormat format3 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date newDate3 = null;
                    try {
                        newDate3 = format3.parse(strCurrentDate3);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    format3 = new SimpleDateFormat("MMM dd, yyyy hh:mm a");
                    String date3 = format3.format(newDate3);
                    textViewDelDateTime.setText("Delivered at: " + date3);
                }
                else if(Common.convertCodeToStatus(response.body().getOrderDetails().get(0).getOrder_status()).equalsIgnoreCase("Cancelled"))
                {
                    String reason = response.body().getOrderDetails().get(0).getReason();
                    textViewDelDateTime.setText("Cancel Reason: " + reason);
                }
                else
                {
                    String strCurrentDate2 = response.body().getOrderDetails().get(0).getDelivery_date();
                    SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
                    Date newDate2 = null;
                    try {
                        newDate2 = format2.parse(strCurrentDate2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    format2 = new SimpleDateFormat("MMM dd, yyyy");
                    String date2 = format2.format(newDate2);

                    textViewDelDateTime.setText("Expected delivery: " + date2 + ", " + response.body().getOrderDetails().get(0).getDelivery_time());
                }


                order_status = response.body().getOrderDetails().get(0).getOrder_status();

                if (Common.convertCodeToStatus(response.body().getOrderDetails().get(0).getOrder_status()).equalsIgnoreCase("Pending")) {
                    btn_cancelOrder.setVisibility(View.VISIBLE);
                }
                else
                {
                    btn_cancelOrder.setVisibility(View.GONE);
                }

                //load Order items
                loadOrderItems(order_id);
                // check Review
                checkReview();


            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadOrderItems(String order_id) {

        swipeRefreshLayout.setRefreshing(true);

        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> call = service.getOrderItems(order_id);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                if (response.body().getOrderItems().size() > 1) {
                    textViewTotal_items.setText(response.body().getOrderItems().size() + " food items");
                } else {
                    textViewTotal_items.setText(response.body().getOrderItems().size() + " food item");
                }

                adapter = new SingleOrderStatusAdapter(response.body().getOrderItems(), SingleOrderStatusActivity.this);
                recyclerViewOrderItems.setAdapter(adapter);

                waitingDialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);

            }
        });

    }

    private void showCancelDialog() {

        LayoutInflater layoutInflater = LayoutInflater.from(SingleOrderStatusActivity.this);
        final View view = layoutInflater.inflate(R.layout.custom_cancel_dialog, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SingleOrderStatusActivity.this);
        //alertDialogBuilder.setTitle("Select Payment Type");

        alertDialogBuilder.setView(view);

        alertDialogBuilder
                .setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getApplicationContext(), "Submitted successfully", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(getApplicationContext(), "Submitted successfully", Toast.LENGTH_SHORT).show();
            }
        });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private void showOrderDetailsDialog()
    {
        LayoutInflater layoutInflater = LayoutInflater.from(SingleOrderStatusActivity.this);
        final View view = layoutInflater.inflate(R.layout.custom_order_details_dialog, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SingleOrderStatusActivity.this);
        //alertDialogBuilder.setTitle("Select Payment Type");

        alertDialogBuilder.setView(view);
        final TextView textViewSubtotal = (TextView) view.findViewById(R.id.textViewSubtotal);
        final TextView textViewCouponAmount = (TextView) view.findViewById(R.id.textViewCouponAmount);
        final TextView textViewDeliveryCharge = (TextView) view.findViewById(R.id.textViewDeliveryCharge);
        final TextView textViewOrderTotal = (TextView) view.findViewById(R.id.textViewOrderTotal);
        final LinearLayout linearLayCoupon = (LinearLayout) view.findViewById(R.id.linearLayCoupon);

        textViewOrderID.setText(Common.id_order);
        textViewSubtotal.setText(Common.SUBTOTAL);
        textViewDeliveryCharge.setText(Common.DELIVERY);
        textViewOrderTotal.setText(Common.TOTAL);

        if(Common.isCouponApplied)
        {
            linearLayCoupon.setVisibility(View.VISIBLE);
            textViewCouponAmount.setText(Common.COUPON_TOTAL);
        }
        else
        {
            linearLayCoupon.setVisibility(View.GONE);
        }

        alertDialogBuilder
                /*.setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        })*/.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @TargetApi(JELLY_BEAN)
    private void enableChangingTransition() {
        ViewGroup animatedRoot = (ViewGroup) findViewById(R.id.animated_root);
        animatedRoot.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    }

    @Override
    public void onRefresh() {
        //load Order details
        loadOrderDetails(order_id);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //overridePendingTransition(0, 0);
    }

    private void changeStatusBarColor(String color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }
}
