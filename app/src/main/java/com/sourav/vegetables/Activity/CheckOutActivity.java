package com.sourav.vegetables.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sourav.vegetables.Adapter.CheckOutAdapter;
import com.sourav.vegetables.Api.ApiClient;
import com.sourav.vegetables.Api.ApiService;
import com.sourav.vegetables.Api.ApiURL;
import com.sourav.vegetables.Api.IFCMService;
import com.sourav.vegetables.Common.Common;
import com.sourav.vegetables.Database.Database;
import com.sourav.vegetables.Helper.NetWorkConfig;
import com.sourav.vegetables.Model.Area;
import com.sourav.vegetables.Model.Cart;
import com.sourav.vegetables.Model.Coupon;
import com.sourav.vegetables.Model.DataMessage;
import com.sourav.vegetables.Model.DeliverySlot;
import com.sourav.vegetables.Model.MyResponse;
import com.sourav.vegetables.Model.Result;
import com.sourav.vegetables.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class CheckOutActivity extends AppCompatActivity {

    private static final String TAG = "CheckOutActivity";
    TextView textViewContactEdit, textViewPaymentEdit, textViewItemEdit,
            textViewCouponEdit, textViewSubAmount, textViewOtherAddress, textViewPromoEnter;
    ImageView imageViewBack;
    public Button btn_confirm, btn_back;
    public TextView textViewDate, textViewBillTotal;
    List<Cart> cartCart = new ArrayList<>();
    CheckOutAdapter adapter;
    RecyclerView recyclerViewCart;
    private double SUB_TOTAL;
    private String USER_ID;
    private EditText editTextCustomerName, editTextComment, editTextCustomerEmail,
            editTextCustomerAddress, editTextCustomerPhone;
    private EditText editTextPromoCode;
    private Button buttonApplyCode;

    private Spinner spinner_country, spinner_state_region;

    private LinearLayout layCouponInfo2;
    private LinearLayout linearLayContactExpand, layoutPromo;
    private LinearLayout linearLayMainDate, linearLayCustomDate;
    private TextView textViewSlotEdit;
    private LinearLayout linearLayCODPayment, linearLayBkashPayment, linearLayRocketPayment, linearLayTrxID;
    private EditText editTextTrxID;
    private TextView textViewBkashNumber, textViewRocketNumber;

    private RadioGroup radioPaymentType;
    private LinearLayout linearLayCoupon, linearLayHomeDelivery;
    private TextView text_coupon_rate, text_Total, textViewCouponTitle, text_delivery_rate;
    private String payment = "Cash On Delivery";

    private int book_id;

    private int radioIndex = 0;
    double exactCatTotal = 0.00;
    double total = 0.00;
    android.app.AlertDialog waitingDialog;

    private String customerName, customerPhone, customerEmail, customerAddress,
            customerComment, areaName;
    private Spinner spinner_area;
    private List<Area> areaList;
    private Coupon coupon;
    private boolean isCodeApplied = false;
    private String discountPrice = "0.0000", CouponCode;
    public NetWorkConfig netWorkConfig;

    private Date cDate;
    private TextView textViewDelDate, textViewDelTime, textViewDelFreeAmount;
    private String DELIVERY_TIME = "", DELIVERY_DATE = "", ORDER_TIME = "",
            MAX_DAY = "", DELIVERY_CHARGE = "", DELIVERY_FREE_AMOUNT = "";
    private int DAY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

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

        initViews();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CheckOutActivity.this);
        USER_ID = sharedPreferences.getString("USER_ID", null);

        cartCart = new Database(this).getCarts();

        /*str = covertObjectToString(new Database(getApplicationContext()).getCarts());
        Log.e("Tag", "Response String + " + str);*/

        cDate = new Date();

        textViewContactEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearLayContactExpand.getVisibility() == View.VISIBLE) {
                    textViewContactEdit.setText("Show");
                    linearLayContactExpand.setVisibility(View.GONE);
                } else {
                    textViewContactEdit.setText("Hide");
                    linearLayContactExpand.setVisibility(View.VISIBLE);
                }
            }
        });

        textViewCouponEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutPromo.getVisibility() == View.VISIBLE) {
                    textViewCouponEdit.setText("Show");
                    layoutPromo.setVisibility(View.GONE);
                } else {
                    textViewCouponEdit.setText("Hide");
                    layoutPromo.setVisibility(View.VISIBLE);
                }
            }
        });

        textViewItemEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CheckOutActivity.this, CartActivity.class));
            }
        });


        textViewSlotEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog();
            }
        });


        buttonApplyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 *  Network Connection Check
                 */
                if (!netWorkConfig.isNetworkAvailable()) {
                    netWorkConfig.createNetErrorDialog();
                    return;
                }

                waitingDialog = new SpotsDialog(CheckOutActivity.this);
                waitingDialog.show();
                waitingDialog.setMessage("Please wait ...");

                exactCatTotal = 0.00;
                total = 0.00;
                discountPrice = "0.0000";
                isCodeApplied = false;

                String code = editTextPromoCode.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(getApplicationContext(), "Empty field.", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (Cart cart : cartCart) {
                    total += (Double.parseDouble(cart.getPrice())) * (Double.parseDouble(cart.getQuantity()));
                }

                checkCoupon(code, USER_ID);
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAreaDialog();
            }
        });

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(CheckOutActivity.this, HomeActivity.class);
                it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(it);
                finish();
            }
        });

        loadSlots();
    }

    private void loadSlots() {

        final android.app.AlertDialog waitingDialog = new SpotsDialog(CheckOutActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");
        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> resultCall = service.getSlots();

        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                ORDER_TIME = response.body().getSlotList().get(0).getOrder_time();
                //Toast.makeText(getApplicationContext(), ORDER_TIME, Toast.LENGTH_LONG).show();
                DELIVERY_TIME = response.body().getSlotList().get(0).getStart_time() + " - " + response.body().getSlotList().get(0).getEnd_time();
                textViewDelTime.setText(DELIVERY_TIME);
                MAX_DAY = response.body().getSlotList().get(0).getMax_day();
                DELIVERY_CHARGE = response.body().getSlotList().get(0).getDelivery_charge();

                DELIVERY_FREE_AMOUNT = response.body().getSlotList().get(0).getDelivery_free_amount();
                if (Double.parseDouble(DELIVERY_FREE_AMOUNT) > 0.00) {
                    textViewDelFreeAmount.setVisibility(View.VISIBLE);
                    textViewDelFreeAmount.setText("Free delivery for sub_total " + getResources().getString(R.string.currency_sign) + DELIVERY_FREE_AMOUNT + " or above");
                } else {
                    textViewDelFreeAmount.setVisibility(View.GONE);
                }

                checkTime();
                getDateAndTotalBill();
                loadData();
                waitingDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                waitingDialog.dismiss();
            }
        });
    }

    private void checkTime() {
        Date date1 = new Date();
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm");
        dateFormat1.format(date1);
        try {
            if (dateFormat1.parse(dateFormat1.format(date1)).after(dateFormat1.parse(ORDER_TIME))) {
                //Toast.makeText(getApplicationContext(), "Current time is greater than Server Time, So order will be place", Toast.LENGTH_LONG).show();

                String dt = new SimpleDateFormat("yyyy-MM-dd").format(cDate);  // Start date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar c = Calendar.getInstance();
                try {
                    c.setTime(sdf.parse(dt));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                c.add(Calendar.DATE, 2);  // number of days to add
                DELIVERY_DATE = sdf.format(c.getTime());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date date = null;
                try {
                    date = dateFormat.parse(DELIVERY_DATE);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                textViewDelDate.setText(new SimpleDateFormat("EEE, dd MMM yyyy").format(date));
                DAY = 2;
                showDialog();

            } else {
                //Toast.makeText(getApplicationContext(), "Current time is less than Server Time", Toast.LENGTH_LONG).show();

                String dt = new SimpleDateFormat("yyyy-MM-dd").format(cDate);  // Start date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar c = Calendar.getInstance();
                try {
                    c.setTime(sdf.parse(dt));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                c.add(Calendar.DATE, 1);  // number of days to add
                DELIVERY_DATE = sdf.format(c.getTime());
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date date = null;
                try {
                    date = dateFormat.parse(DELIVERY_DATE);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                textViewDelDate.setText(new SimpleDateFormat("EEE, dd MMM yyyy").format(date));
                DAY = 1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void showDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(CheckOutActivity.this);
        final View view = layoutInflater.inflate(R.layout.custom_dialog, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CheckOutActivity.this);
        //alertDialogBuilder.setTitle("Select Payment Type");

        alertDialogBuilder.setView(view);
        final TextView textViewMessage = (TextView) view.findViewById(R.id.textViewMessage);
        final TextView textViewDelDateTime = (TextView) view.findViewById(R.id.textViewDelDateTime);

        textViewMessage.setText("Current time is greater than Server Time(" + Common.covert24Time12(ORDER_TIME) + ").So your order will be delivered on :");
        textViewDelDateTime.setText(textViewDelDate.getText().toString() + "\nBetween: " + textViewDelTime.getText().toString());

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


    private void initAreaTypeDialogView(View view) {
        spinner_area = (Spinner) view.findViewById(R.id.spinner_area);
        loadAreaData();
    }

    private void loadAreaData() {

        final android.app.AlertDialog waitingDialog = new SpotsDialog(CheckOutActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");

        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> call = service.getArea();

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                waitingDialog.dismiss();
                areaList = response.body().getAreaList();
                final List<String> areaNameID = new ArrayList<>();
                final Map<String, String> areaMap = new HashMap<String, String>();

                for (int i = 0; i < response.body().getAreaList().size(); i++) {
                    areaMap.put(response.body().getAreaList().get(i).getArea_name(), response.body().getAreaList().get(i).getId());
                    areaNameID.add(response.body().getAreaList().get(i).getArea_name());
                }

                // Creating adapter for spinner
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(CheckOutActivity.this, android.R.layout.simple_spinner_item, areaNameID);
                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                spinner_area.setAdapter(dataAdapter);

                spinner_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String string = spinner_area.getSelectedItem().toString();
                        areaName = string;
                        //shipperID = areaMap.get(string);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                waitingDialog.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        // getting views
        textViewDate = (TextView) findViewById(R.id.textViewDate);
        textViewBillTotal = (TextView) findViewById(R.id.textViewTotalBill);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        btn_back = (Button) findViewById(R.id.btn_back);

        editTextCustomerName = (EditText) findViewById(R.id.editTextCustomerName);
        editTextComment = (EditText) findViewById(R.id.editTextCustomerComment);
        editTextCustomerEmail = (EditText) findViewById(R.id.editTextCustomerEmail);
        editTextCustomerPhone = (EditText) findViewById(R.id.editTextCustomerPhone);
        editTextCustomerAddress = (EditText) findViewById(R.id.editTextCustomerAddress);

        textViewPromoEnter = (TextView) findViewById(R.id.textViewPromoEnter);

        editTextPromoCode = (EditText) findViewById(R.id.editTextPromoCode);
        buttonApplyCode = (Button) findViewById(R.id.buttonApplyCode);

        textViewContactEdit = (TextView) findViewById(R.id.textViewContactEdit);
        textViewPaymentEdit = (TextView) findViewById(R.id.textViewPaymentEdit);
        textViewItemEdit = (TextView) findViewById(R.id.textViewItemEdit);
        textViewCouponEdit = (TextView) findViewById(R.id.textViewCouponEdit);
        textViewSubAmount = (TextView) findViewById(R.id.text_sub_amount);
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        layCouponInfo2 = (LinearLayout) findViewById(R.id.layCouponInfo2);

        linearLayContactExpand = (LinearLayout) findViewById(R.id.linearLayContactExpand);
        layoutPromo = (LinearLayout) findViewById(R.id.layoutPromo);

        textViewSlotEdit = (TextView) findViewById(R.id.textViewSlotEdit);
        linearLayMainDate = (LinearLayout) findViewById(R.id.linearLayMainDate);
        //linearLayCustomDate = (LinearLayout) findViewById(R.id.linearLayCustomDate);

        textViewDelDate = (TextView) findViewById(R.id.textViewDelDate);
        textViewDelTime = (TextView) findViewById(R.id.textViewDelTime);
        textViewDelFreeAmount = (TextView) findViewById(R.id.textViewDelFreeAmount);

        /*linearLayCODPayment = (LinearLayout) findViewById(R.id.linearLayCODPayment);
        linearLayBkashPayment = (LinearLayout) findViewById(R.id.linearLayBkashPayment);
        linearLayRocketPayment = (LinearLayout) findViewById(R.id.linearLayRocketPayment);
        linearLayTrxID = (LinearLayout) findViewById(R.id.linearLayTrxID);
        editTextTrxID = (EditText) findViewById(R.id.editTextTrxID);
        textViewBkashNumber = (TextView) findViewById(R.id.textViewBkashNumber);
        textViewRocketNumber = (TextView) findViewById(R.id.textViewRocketNumber);*/

        radioPaymentType = (RadioGroup) findViewById(R.id.radioPaymentType);

        linearLayCoupon = (LinearLayout) findViewById(R.id.linearLayCoupon);
        linearLayHomeDelivery = (LinearLayout) findViewById(R.id.linearLayHomeDelivery);
        text_coupon_rate = (TextView) findViewById(R.id.text_coupon_rate);
        text_delivery_rate = (TextView) findViewById(R.id.text_delivery_rate);
        text_Total = (TextView) findViewById(R.id.text_Total);
        textViewCouponTitle = (TextView) findViewById(R.id.textViewCouponTitle);

        recyclerViewCart = (RecyclerView) findViewById(R.id.recyclerViewCart);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewCart.setHasFixedSize(true);
        recyclerViewCart.setItemAnimator(new DefaultItemAnimator());
        recyclerViewCart.addItemDecoration(
                new DividerItemDecoration(CheckOutActivity.this, LinearLayoutManager.VERTICAL) {
                    @Override
                    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                        int position = parent.getChildAdapterPosition(view);
                        // hide the divider for the last child
                        if (position == parent.getAdapter().getItemCount() - 1) {
                            outRect.setEmpty();
                        } else {
                            super.getItemOffsets(outRect, view, parent, state);
                        }
                    }
                }
        );
    }

    private void placeOrder() {

        customerName = editTextCustomerName.getText().toString();
        customerComment = editTextComment.getText().toString();
        customerPhone = editTextCustomerPhone.getText().toString();
        customerAddress = editTextCustomerAddress.getText().toString();
        customerEmail = editTextCustomerEmail.getText().toString();


        if (customerComment.equalsIgnoreCase("")) {
            customerComment = "null";
        }

        if (TextUtils.isEmpty(editTextCustomerName.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Information field can't be empty!", Toast.LENGTH_SHORT).show();
            editTextCustomerName.setError("Filed is mandatory");
            return;
        }
        if (TextUtils.isEmpty(editTextCustomerEmail.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Information field can't be empty!", Toast.LENGTH_SHORT).show();
            editTextCustomerEmail.setError("Filed is mandatory");
            return;
        }
        if (TextUtils.isEmpty(editTextCustomerPhone.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Information field can't be empty!", Toast.LENGTH_SHORT).show();
            editTextCustomerPhone.setError("Filed is mandatory");
            return;
        }
        if (TextUtils.isEmpty(editTextCustomerAddress.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Information field can't be empty!", Toast.LENGTH_SHORT).show();
            editTextCustomerAddress.setError("Filed is mandatory");
            return;
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CheckOutActivity.this);
        USER_ID = sharedPreferences.getString("USER_ID", null);

        String id_order = String.valueOf(System.currentTimeMillis());
        // Calculate Total Price
        double sub_total = 0.0, total = 0.00;
        for (Cart cart : cartCart) {
            sub_total += (Double.parseDouble(cart.getPrice())) * (Double.parseDouble(cart.getQuantity()));
        }

        Common.SUBTOTAL = String.valueOf(new StringBuilder(CheckOutActivity.this.getResources().getString(R.string.currency_sign)).append(sub_total));

        if (Double.parseDouble(DELIVERY_FREE_AMOUNT) <= sub_total && Double.parseDouble(DELIVERY_FREE_AMOUNT) > 0.00) {
            DELIVERY_CHARGE = "0.00";
        }

        if (isCodeApplied) {
            total = sub_total + Double.parseDouble(DELIVERY_CHARGE) - Double.parseDouble(discountPrice);
        } else {
            total = sub_total + Double.parseDouble(DELIVERY_CHARGE);
        }

        Common.TOTAL = String.valueOf(new StringBuilder(CheckOutActivity.this.getResources().getString(R.string.currency_sign)).append(total));
        Common.DELIVERY = String.valueOf(new StringBuilder(CheckOutActivity.this.getResources().getString(R.string.currency_sign)).append(Double.parseDouble(DELIVERY_CHARGE)));

        final android.app.AlertDialog waitingDialog = new SpotsDialog(CheckOutActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Placing ...");

        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        //defining the call
        Call<Result> call = service.placeOrder(
                id_order,
                USER_ID,
                customerName,
                customerPhone,
                customerEmail,
                customerAddress,
                areaName,
                String.valueOf(total),
                Common.getDateTime(),
                customerComment,
                "COD",
                "Unpaid",
                DELIVERY_DATE,
                DELIVERY_TIME
        );
        //calling the api
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (!response.body().getError()) {
                    waitingDialog.dismiss();
                    Common.id_order = id_order;
                    Common.NAME = customerName;
                    Common.PHONE = customerPhone;
                    Common.ADDRESS = customerAddress + " (" + areaName + ")";
                    Common.PAYMETHOD = "Cash on delivery";
                    Common.PAYSTATUS = "Unpaid";
                    Common.DELIVERY_DATE = DELIVERY_DATE;
                    Common.DELIVERY_TIME = DELIVERY_TIME;
                    submitOrderItems(id_order);
                    if (isCodeApplied) {
                        Common.isCouponApplied = true;
                        Common.COUPON_TOTAL = String.valueOf(new StringBuilder(CheckOutActivity.this.getResources().getString(R.string.currency_sign)).append(discountPrice));
                        submitCouponHistory(id_order);
                    } else {
                        Common.isCouponApplied = false;
                        Common.COUPON_TOTAL = String.valueOf(new StringBuilder(CheckOutActivity.this.getResources().getString(R.string.currency_sign)).append("0.0"));
                    }
                    Toast.makeText(getApplicationContext(), response.body().getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                waitingDialog.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void submitCouponHistory(String id_order) {
        // push to coupon history table
        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> call = service.addCouponHistory(
                coupon.getCoupon_id(),
                String.valueOf(id_order),
                USER_ID,
                "-" + discountPrice,
                Common.getDateTime());

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitOrderItems(String id_order) {

        final android.app.AlertDialog waitingDialog = new SpotsDialog(CheckOutActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Placing ...");

        // Delaying action for 1 second
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int count = new Database(getApplicationContext()).getCarts().size();
                for (int i = 0; i < count; i++) {

                    double food_total_price = 0.00;
                    food_total_price += (Double.parseDouble(cartCart.get(i).getPrice())) * (Double.parseDouble(cartCart.get(i).getQuantity()));

                    ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
                    //defining the call
                    Call<Result> call = service.placeOrderItems(
                            id_order,
                            cartCart.get(i).getFood_id(),
                            cartCart.get(i).getName(),
                            cartCart.get(i).getPrice(),
                            cartCart.get(i).getQuantity(),
                            String.valueOf(food_total_price),
                            cartCart.get(i).getImage_url(),
                            cartCart.get(i).getMin_unit_amount(),
                            cartCart.get(i).getUnit(),
                            cartCart.get(i).getId_menu(),
                            cartCart.get(i).getMenu_name()
                    );
                    //calling the api
                    call.enqueue(new Callback<Result>() {
                        @Override
                        public void onResponse(Call<Result> call, Response<Result> response) {
                            waitingDialog.dismiss();
                            // Remove All cart items
                            new Database(getApplicationContext()).removeAllCartItems();
                            startActivity(new Intent(CheckOutActivity.this, ConfirmationActivity.class));
                        }

                        @Override
                        public void onFailure(Call<Result> call, Throwable t) {
                            waitingDialog.dismiss();
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                sentNotificationToServer(id_order);
            }
        }, 1000);


    }
/*
    private void sentNotificationToServer(final String book_id) {

        //building retrofit object
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);

        //defining the call
        Call<Result> call = service.getToken("server_app", "1");
        //calling the api
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                // When we have token, then we just sent notification to this token
                Map<String, String> contentSend = new HashMap<>();
                contentSend.put("title", "New Order");
                contentSend.put("message", "You have new Order: " + book_id);

                DataMessage dataMessage = new DataMessage();
                if (response.body().getToken().getToken() != null) {
                    dataMessage.setTo(response.body().getToken().getToken());
                }
                dataMessage.setData(contentSend);
                IFCMService ifcmService = Common.getFCMService();
                ifcmService.sendNotification(dataMessage)
                        .enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                if (response.code() == 200) {
                                    if (response.body().success == 1) {
                                        Toast.makeText(CheckOutActivity.this, "Order Submitted", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(CheckOutActivity.this, "Send Notification failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {

                            }
                        });
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.d("Msg: ", "CheckOut");
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
*/

    private void getDateAndTotalBill() {

        Date cDate = new Date();
        String currentDate = new SimpleDateFormat("EEE, dd MMM yyyy").format(cDate);

        // Calculate Total Price
        double sub_total = 0.0;
        for (Cart cart : cartCart) {
            sub_total += (Double.parseDouble(cart.getPrice())) * (Double.parseDouble(cart.getQuantity()));
        }

        if (Double.parseDouble(DELIVERY_FREE_AMOUNT) <= sub_total && Double.parseDouble(DELIVERY_FREE_AMOUNT) > 0.00) {
            DELIVERY_CHARGE = "0.00";
        }

        sub_total = sub_total + Double.parseDouble(DELIVERY_CHARGE);

        textViewBillTotal.setText(getResources().getString(R.string.currency_sign) + String.format("%.2f", sub_total));
        textViewDate.setText(currentDate);
    }

    private void loadData() {

        // load cart Items
        cartCart = new Database(this).getCarts();
        adapter = new CheckOutAdapter(cartCart, this);
        recyclerViewCart.setAdapter(adapter);

        // Calculate Total Price
        double sub_total = 0.0, total = 0.00;
        for (Cart cart : cartCart) {
            sub_total += (Double.parseDouble(cart.getPrice())) * (Double.parseDouble(cart.getQuantity()));
        }

        if (Double.parseDouble(DELIVERY_FREE_AMOUNT) <= sub_total && Double.parseDouble(DELIVERY_FREE_AMOUNT) > 0.00) {
            DELIVERY_CHARGE = "0.00";
        }

        linearLayHomeDelivery.setVisibility(View.VISIBLE);
        text_delivery_rate.setText(getResources().getString(R.string.currency_sign) + "+" + String.format("%.2f", Double.parseDouble(DELIVERY_CHARGE)));
        total = sub_total + Double.parseDouble(DELIVERY_CHARGE);

        textViewSubAmount.setText(getResources().getString(R.string.currency_sign) + String.format("%.2f", sub_total));
        text_Total.setText(getResources().getString(R.string.currency_sign) + String.format("%.2f", total));


        editTextCustomerAddress.setText(Common.currentUser.getAddress());
        editTextCustomerName.setText(Common.currentUser.getUsername());
        editTextCustomerPhone.setText(Common.currentUser.getPhone());
        editTextCustomerEmail.setText(Common.currentUser.getEmail());

    }

    private void changeStatusBarColor(String color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

    private void checkCoupon(final String code, String USER_ID) {

        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> call = service.checkCouponCode(code, USER_ID);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                if (!response.body().getError()) {
                    coupon = new Coupon();
                    coupon = response.body().getCouponDetails();

                    if (Integer.parseInt(coupon.getUses_total()) > Integer.parseInt(coupon.getCode_uses_total()) &&
                            Integer.parseInt(coupon.getUses_customer()) > Integer.parseInt(coupon.getCode_customer_uses_total())) {
                        if (total >= Double.parseDouble(coupon.getTotal())) {

                            //Toast.makeText(getApplicationContext(), "Code Applied", Toast.LENGTH_LONG).show();
                            isCodeApplied = true;
                            CouponCode = code;
                            if (coupon.getType().equals("F")) {
                                discountPrice = coupon.getDiscount();
                            } else {
                                discountPrice = String.valueOf(total / 100 * Double.parseDouble(coupon.getDiscount()));
                                if(Double.parseDouble(discountPrice) > Double.parseDouble(coupon.getDiscount_limit()))
                                {
                                    discountPrice = coupon.getDiscount_limit();
                                }
                            }

                            linearLayCoupon.setVisibility(View.VISIBLE);
                            textViewCouponTitle.setText("Coupon (" + code + ")");
                            text_coupon_rate.setText(getResources().getString(R.string.currency_sign) + "-" + String.format("%.2f", Double.parseDouble(discountPrice)));
                            textViewPromoEnter.setVisibility(View.VISIBLE);
                            layCouponInfo2.setVisibility(View.GONE);
                            layoutPromo.setVisibility(View.GONE);
                            textViewCouponEdit.setVisibility(View.GONE);

                            double subTotal = total;

                            if (Double.parseDouble(DELIVERY_FREE_AMOUNT) <= subTotal && Double.parseDouble(DELIVERY_FREE_AMOUNT) > 0.00) {
                                DELIVERY_CHARGE = "0.00";
                            }

                            total = total + Double.parseDouble(DELIVERY_CHARGE) - Double.parseDouble(discountPrice);

                            textViewBillTotal.setText(getResources().getString(R.string.currency_sign) + String.format("%.2f", total));
                            textViewSubAmount.setText(getResources().getString(R.string.currency_sign) + String.format("%.2f", subTotal));
                            text_Total.setText(getResources().getString(R.string.currency_sign) + String.format("%.2f", total));
                            waitingDialog.dismiss();

                        } else {
                            waitingDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Minimum Order price is below: " + coupon.getTotal(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        waitingDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "You have crossed this coupon limit ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    waitingDialog.dismiss();
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void datePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int yy = calendar.get(Calendar.YEAR);
        int mm = calendar.get(Calendar.MONTH);
        int dd = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePicker = new DatePickerDialog(CheckOutActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String date, day = null, month = null;

                if (dayOfMonth < 10) {
                    day = "0" + String.valueOf(dayOfMonth);

                } else {
                    day = String.valueOf(dayOfMonth);
                }
                if (monthOfYear + 1 < 10) {
                    month = "0" + String.valueOf(monthOfYear + 1);
                } else {
                    month = String.valueOf(monthOfYear + 1);
                }

                DELIVERY_DATE = year + "-" + month + "-" + day;
                //Common.sellDate = sell_date;
                DateFormat inputFormatter1 = new SimpleDateFormat("yyyy-MM-dd");
                Date date1 = null;
                try {
                    date1 = inputFormatter1.parse(DELIVERY_DATE);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                DateFormat outputFormatter1 = new SimpleDateFormat("EEE, dd MMM yyyy");
                String output1 = outputFormatter1.format(date1);
                textViewDelDate.setText(output1);
            }
        }, yy, mm, dd);
        datePicker.getDatePicker().setMinDate(calendar.getTimeInMillis() + 1000 * 60 * 60 * 24 * DAY);
        datePicker.getDatePicker().setMaxDate(calendar.getTimeInMillis() + (1000 * 60 * 60 * 24 * Integer.parseInt(MAX_DAY)));
        datePicker.show();
    }

    private void showAreaDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(CheckOutActivity.this);
        View view = layoutInflater.inflate(R.layout.custom_area_dialog, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CheckOutActivity.this);
        alertDialogBuilder.setTitle("Select Area");
        alertDialogBuilder.setView(view);

        initAreaTypeDialogView(view);

        alertDialogBuilder
                .setCancelable(true).setPositiveButton("Place", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                placeOrder();
            }
        }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private String covertObjectToString(Object object) {
        return new Gson().toJson(object);
    }

    private void sentNotificationToServer(String id_order) {

        //building retrofit object
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);

        //defining the call
        Call<Result> call = service.getToken("server_app", "1");
        //calling the api
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                // When we have token, then we just sent notification to this token
                Map<String, String> contentSend = new HashMap<>();
                contentSend.put("title", "New Order");
                contentSend.put("message", "You have new Order: " + id_order);

                DataMessage dataMessage = new DataMessage();
                if (response.body().getToken().getToken() != null) {
                    dataMessage.setTo(response.body().getToken().getToken());
                }
                dataMessage.setData(contentSend);
                IFCMService ifcmService = Common.getFCMService();
                ifcmService.sendNotification(dataMessage)
                        .enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                if (response.code() == 200) {
                                    if (response.body().success == 1) {
                                        Toast.makeText(CheckOutActivity.this, "Order Submitted", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(CheckOutActivity.this, "Send Notification failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<MyResponse> call, Throwable t) {

                            }
                        });
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.d("Msg: ", "CheckOut");
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
