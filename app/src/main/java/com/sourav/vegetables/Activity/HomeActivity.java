package com.sourav.vegetables.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sourav.vegetables.Adapter.MenuAdapter;
import com.sourav.vegetables.Api.ApiClient;
import com.sourav.vegetables.Api.ApiService;
import com.sourav.vegetables.Api.ApiURL;
import com.sourav.vegetables.Common.Common;
import com.sourav.vegetables.Database.Database;
import com.sourav.vegetables.Database.NotificationDatabase;
import com.sourav.vegetables.Helper.CircleTransform;
import com.sourav.vegetables.Helper.GridSpacingItemDecoration;
import com.sourav.vegetables.Helper.NetWorkConfig;
import com.sourav.vegetables.Model.Banner;
import com.sourav.vegetables.Model.Category;
import com.sourav.vegetables.Model.Result;
import com.sourav.vegetables.R;

import java.util.HashMap;
import java.util.List;

import static com.smarteist.autoimageslider.IndicatorView.utils.DensityUtils.dpToPx;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BaseSliderView.OnSliderClickListener, SwipeRefreshLayout.OnRefreshListener {

    TextView txtFullName, txtPhone, notification, textViewScroll;
    private ImageView imgProfile;
    SliderLayout mSlider;
    CounterFab fab;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout contentView;
    private RecyclerView recycler_menu;
    List<Category> menuList;
    private MenuAdapter adapter;

    private NetWorkConfig netWorkConfig;


    private static final String urlNavHeaderBg = "https://api.androidhive.info/images/nav-menu-header-bg.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        subscribeToPushService();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open
                , R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        toggle.syncState();

        animateNavigationDrawer();

        textViewScroll = (TextView) findViewById(R.id.textViewScroll);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        notification = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().
                findItem(R.id.nav_notification));

        textViewScroll.setSelected(true);

        //Set Name for user
        View headerView = navigationView.getHeaderView(0);
        txtFullName = (TextView) headerView.findViewById(R.id.txtFullName);
        txtPhone = (TextView) headerView.findViewById(R.id.txtPhone);
        //imgNavHeaderBg = (ImageView) headerView.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) headerView.findViewById(R.id.img_profile);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        String USERNAME = sharedPreferences.getString("USERNAME", null);
        txtFullName.setText(USERNAME);


        // getting views
        contentView = (LinearLayout) findViewById(R.id.contentView);
        fab = (CounterFab) findViewById(R.id.fab);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().
                getColor(android.R.color.holo_blue_dark), getResources().
                getColor(android.R.color.holo_red_dark), getResources().
                getColor(android.R.color.holo_green_light), getResources().
                getColor(android.R.color.holo_orange_dark));
        recycler_menu = (RecyclerView) findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recycler_menu.setLayoutManager(mLayoutManager);
        recycler_menu.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(0), true));
        recycler_menu.setItemAnimator(new DefaultItemAnimator());


        fab.setOnClickListener(view -> {
            if (new Database(this).getCarts().size() <= 0) {
                //Toast.makeText(getApplicationContext(), "No Cart items.", Toast.LENGTH_SHORT).show();
                Toasty.error(getApplicationContext(), "No Cart items.", Toast.LENGTH_SHORT, true).show();
            } else {
                Intent cartIntent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(cartIntent);
            }
        });

        fab.setCount(new Database(this).getCarts().size());

        updateNotificationBadge();

        loadListMenu();
        //Setup Slider
        getBanner();
        getTextScrollData();
        getUserData();
        updateTokenToServer();
    }

    private void getTextScrollData() {
        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> call = service.getTextScroll();

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Common.textScroll = response.body().getTextScroll();
                if (Common.textScroll.getStatus().equalsIgnoreCase("1")) {
                    textViewScroll.setVisibility(View.VISIBLE);
                    textViewScroll.setText(Common.textScroll.getText());
                    textViewScroll.setSelected(true);
                } else {
                    textViewScroll.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserData() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        String PHONE = sharedPreferences.getString("PHONE", null);
        String PASSWORD = sharedPreferences.getString("PASSWORD", null);

        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> call = service.loginUser(PHONE, PASSWORD);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                if (!response.body().getError()) {
                    Common.currentUser = response.body().getUser();
                    txtPhone.setText(Common.currentUser.getPhone());
                    txtFullName.setText(Common.currentUser.getUsername());
                    // Loading profile image
                    Glide.with(HomeActivity.this)
                            .load(ApiURL.SERVER_URL + response.body().getUser().getImage_url())
                            .crossFade()
                            .error(R.drawable.profile_avatar_2)
                            .thumbnail(0.5f)
                            .bitmapTransform(new CircleTransform(HomeActivity.this))
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgProfile);
                } else {
                    Toast.makeText(HomeActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getBanner() {
        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> call = service.getBanners();

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                displayImages(response.body().getBanners());
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayImages(List<Banner> banners) {

        mSlider = (SliderLayout) findViewById(R.id.slider);

        HashMap<String, String> bannerMap = new HashMap<>();
        for (Banner item : banners) {
            bannerMap.put(item.getName(), ApiURL.SERVER_URL + item.getImage_url());
        }
        for (String name : bannerMap.keySet()) {
            TextSliderView textSliderView = new TextSliderView(this);
            textSliderView
                    .image(bannerMap.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(HomeActivity.this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);

            mSlider.addSlider(textSliderView);
        }

        // Slider Animation
        /*mSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSlider.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);
        mSlider.setCustomAnimation(new DescriptionAnimation());
        mSlider.setDuration(4000);
*/
        /*mSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mSlider.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);
        mSlider.setDuration(4000);
        mSlider.setCustomIndicator((PagerIndicator) v.findViewById(R.id.indicator));*/
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this, slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();
    }

    private void loadListMenu() {
        swipeRefreshLayout.setRefreshing(false);

        final android.app.AlertDialog waitingDialog = new SpotsDialog(HomeActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");
        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> resultCall = service.getMenu();

        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                menuList = response.body().getMenuList();

                Log.d("SIZE: ", String.valueOf(menuList.size()));

                if (menuList.size() <= 0) {
                    Toast.makeText(getApplicationContext(), "Empty Data", Toast.LENGTH_SHORT).show();
                    waitingDialog.dismiss();
                } else {
                    adapter = new MenuAdapter(menuList, HomeActivity.this);
                    recycler_menu.setAdapter(adapter);
                    waitingDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                waitingDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_search)
            startActivity(new Intent(HomeActivity.this, SearchActivity.class));

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
        } /*else if (id == R.id.nav_cart) {
            if (new Database(this).getCarts().size() <= 0) {
                onNavigationItemSelected(navigationView.getMenu().getItem(0).setChecked(true));
                //Toast.makeText(getApplicationContext(), "No Cart items.", Toast.LENGTH_SHORT).show();
                Toasty.error(getApplicationContext(), "No Cart items.", Toast.LENGTH_SHORT, true).show();
            } else {
                Intent cartIntent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(cartIntent);
            }

        }*/ else if (id == R.id.nav_orders) {
            Intent orderIntent = new Intent(HomeActivity.this, AllOrderHistoryActivity.class);
            startActivity(orderIntent);
        } else if (id == R.id.nav_notification) {
            Intent orderIntent = new Intent(HomeActivity.this, NotificationActivity.class);
            startActivity(orderIntent);
        } else if (id == R.id.nav_reviews) {
            Intent orderIntent = new Intent(HomeActivity.this, ShowAllReviewsActivity.class);
            startActivity(orderIntent);
        } else if (id == R.id.nav_contact_us) {
            Intent orderIntent = new Intent(HomeActivity.this, ContactUsActivity.class);
            startActivity(orderIntent);
        } else if (id == R.id.nav_log_out) {
            //Delete Remember user & password
            LogOut();
        } /*else if (id == R.id.nav_update_name) {
            //showUpdateNameDialog();
        }*/ else if (id == R.id.nav_home_address) {
            showHomeAddressDialog();
        } else if (id == R.id.nav_profile) {
            String USER_ID;
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
            USER_ID = sharedPreferences.getString("USER_ID", null);
            Common.User_ID = USER_ID;
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        } /*else if (id == R.id.nav_favorites) {
            //startActivity(new Intent(HomeActivity.this, FavoritesActivity.class));
        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showHomeAddressDialog() {

        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle("CHANGE HOME ADDRESS");
        alertDialog.setCancelable(false);
        alertDialog.setMessage("Please fill below information");

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout_home = inflater.inflate(R.layout.home_address_layout, null);

        final EditText edtHomeAddress = (EditText) layout_home.findViewById(R.id.edtHomeAddress);
        edtHomeAddress.setText(Common.currentUser.getAddress());
        alertDialog.setView(layout_home);

        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onNavigationItemSelected(navigationView.getMenu().getItem(0).setChecked(true));
                dialogInterface.dismiss();
                //Set new HomeActivity Address
                Common.currentUser.setAddress(edtHomeAddress.getText().toString());

                //Defining retrofit api service
                ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
                Call<Result> call = service.updateUserAddress(Common.currentUser.getId(), edtHomeAddress.getText().toString());

                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onNavigationItemSelected(navigationView.getMenu().getItem(0).setChecked(true));
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }

    private void LogOut() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
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

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                // Clearing Data from Shared Preferences
                editor.clear();
                editor.commit();

                //Delete Remember user & password
                FirebaseAuth.getInstance().signOut();

                // Delete all cart items
                new Database(getApplicationContext()).removeAllCartItems();


                // Clear All Activity
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Toast.makeText(HomeActivity.this, "Successfully logged out!", Toast.LENGTH_LONG).show();
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
    protected void onStart() {
        super.onStart();
        onNavigationItemSelected(navigationView.getMenu().getItem(0).setChecked(true));
        loadListMenu();
        getTextScrollData();
        getUserData();
        updateTokenToServer();
        updateNotificationBadge();
    }

    private void updateNotificationBadge() {
        //Gravity property aligns the text
        notification.setGravity(Gravity.CENTER_VERTICAL);
        notification.setTypeface(null, Typeface.BOLD);
        notification.setTextColor(getResources().getColor(R.color.discount_color));
        int count = new NotificationDatabase(HomeActivity.this).notificationUnreadItemCount();
        notification.setText(String.valueOf(count));
    }

    @Override
    protected void onResume() {
        super.onResume();
        onNavigationItemSelected(navigationView.getMenu().getItem(0).setChecked(true));
        fab.setCount(new Database(this).getCarts().size());
        loadListMenu();
        getUserData();
        getTextScrollData();
        updateTokenToServer();
        updateNotificationBadge();
    }

    @Override
    public void onRefresh() {
        getTextScrollData();
        loadListMenu();
        getUserData();
        updateTokenToServer();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerVisible(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }

    public void subscribeToPushService() {
        FirebaseMessaging.getInstance().subscribeToTopic("customer");
        FirebaseMessaging.getInstance().subscribeToTopic("all");
    }


    private void changeStatusBarColor(String color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

    static final float END_SCALE = 0.7f;

    private void animateNavigationDrawer() {

        //Add any color or remove it to use the default one!
        //To make it transparent use Color.Transparent in side setScrimColor();
        //drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });

    }

    private void updateTokenToServer() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String USER_ID = sharedPreferences.getString("USER_ID", null);

        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {

                        //building retrofit object
                        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);

                        //defining the call
                        Call<Result> call = service.updateUserToken(USER_ID, instanceIdResult.getToken());
                        //calling the api
                        call.enqueue(new Callback<Result>() {
                            @Override
                            public void onResponse(Call<Result> call, Response<Result> response) {
                                Log.e("MA_Debug: ", response.body().getMessage());
                            }

                            @Override
                            public void onFailure(Call<Result> call, Throwable t) {
                                Log.e("MA_Debug: ", t.getMessage());
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

}
