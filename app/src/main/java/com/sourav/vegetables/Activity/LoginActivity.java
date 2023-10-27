package com.sourav.vegetables.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sourav.vegetables.Api.ApiClient;
import com.sourav.vegetables.Api.ApiService;
import com.sourav.vegetables.Common.Common;
import com.sourav.vegetables.Helper.NetWorkConfig;
import com.sourav.vegetables.Model.Result;
import com.sourav.vegetables.Model.User;
import com.sourav.vegetables.R;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    //public AppCompatButton btn_reg, btn_login;
    public Button btn_login;
    public Button btn_reg, btn_forgotPassword;
    private TextInputEditText editTextPhone, editTextPassword;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final int REQUEST_CODE = 7171;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    private List<AuthUI.IdpConfig> providers;
    private boolean isForgetPressed = false;
    private boolean isRegButtonPress = false;
    private boolean flag = false;
    FirebaseDatabase database;
    DatabaseReference users;
    private NetWorkConfig netWorkConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());
        listener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                checkUserFromFirebase(user);
            }
        };

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.login_layout);

        netWorkConfig = new NetWorkConfig(this);

        /**
         *  Network Connection Check
         */
        if (!netWorkConfig.isNetworkAvailable()) {
            netWorkConfig.createNetErrorDialog();
            return;
        }

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        editor = sharedPreferences.edit();

        // getting views
        editTextPhone = (TextInputEditText) findViewById(R.id.input_phone);
        editTextPassword = (TextInputEditText) findViewById(R.id.input_password);

        //btn_login = (Button) findViewById(R.id.btn_login);
        //btn_reg = (TextView) findViewById(R.id.btn_create_account);

        btn_reg = (Button) findViewById(R.id.btn_create_account);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_forgotPassword = (Button) findViewById(R.id.btn_forgotPassword);

        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRegButtonPress = true;
                startLoginSystem();
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editTextPhone.getText().toString()) || TextUtils.isEmpty(editTextPassword.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Input fields can't be empty", Toast.LENGTH_LONG).show();
                } else {
                    loginUser();
                }

            }
        });

        btn_forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isForgetPressed = true;
                startLoginSystem();
            }
        });
    }

    private void loginUser() {
        final android.app.AlertDialog waitingDialog = new SpotsDialog(LoginActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait ...");


        //Defining retrofit api service
        ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
        Call<Result> call = service.loginUser("+88" + editTextPhone.getText().toString(), editTextPassword.getText().toString());

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                waitingDialog.dismiss();

                if (!response.body().getError()) {
                    // Clearing shared preferences
                    editor.clear();
                    editor.commit();

                    editor.putString("USERNAME", response.body().getUser().getUsername());
                    editor.putString("PHONE", "+88" + editTextPhone.getText().toString());
                    editor.putString("PASSWORD", editTextPassword.getText().toString());
                    editor.putString("USER_ID", response.body().getUser().getId());
                    editor.apply();

                    Common.currentUser = response.body().getUser();

                    Common.User_phone = editTextPhone.getText().toString();
                    Common.User_ID = response.body().getUser().getId();

                    updateTokenToServer();

                    Toast.makeText(getApplicationContext(), "Welcome back " + response.body().getUser().getUsername(), Toast.LENGTH_LONG).show();

                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                waitingDialog.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        super.onBackPressed();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(listener);
        flag = false;

        String Phone;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        Phone = sharedPreferences.getString("PHONE", null);
        if (Phone != null) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        if (listener != null)
            firebaseAuth.removeAuthStateListener(listener);
        super.onStop();
    }

    private void checkUserFromFirebase(FirebaseUser user) {
        /*//Show dialog
        final android.app.AlertDialog waitingDialog = new SpotsDialog(LoginActivity.this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait");
        waitingDialog.setCancelable(false);*/

        //Check if exists on Firebase Users
        users.orderByKey().equalTo(user.getPhoneNumber())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.child(user.getPhoneNumber()).exists()) // If not exists
                        {
                            //We will create new user and login
                            User newUser = new User();
                            newUser.setPhone(user.getPhoneNumber());
                            newUser.setUsername("");

                            //Add to Firebase
                            users.child(user.getPhoneNumber())
                                    .setValue(newUser)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful())
                                            Toast.makeText(LoginActivity.this, "User register successful!", Toast.LENGTH_SHORT).show();

                                        Common.User_phone = user.getPhoneNumber();
                                        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));

                                    });

                        } else // If exists
                        {

                            String Phone, Password;
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                            Phone = sharedPreferences.getString("PHONE", null);
                            Password = sharedPreferences.getString("PASSWORD", null);
                            if (isRegButtonPress && !isForgetPressed) {
                                isRegButtonPress = false;
                                //waitingDialog.dismiss();
                                if (!flag) {
                                    Toast.makeText(LoginActivity.this, "This number is already registered. Please login!", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }
                            if (isForgetPressed && !isRegButtonPress) {
                                //waitingDialog.dismiss();
                                isForgetPressed = false;
                                if (flag) {
                                    Toast.makeText(LoginActivity.this, "Please enter a phone number to proceed.", Toast.LENGTH_SHORT).show();
                                    return;
                                } else {
                                    Intent intent = new Intent(LoginActivity.this, VerifyPhoneActivity.class);
                                    intent.putExtra("phoneNumber", user.getPhoneNumber());
                                    startActivity(intent);
                                }
                            }
                            /*else if (!isRegButtonPress) {
                                //Copy code from LoginActivity
                                Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(homeIntent);
                                //Dismiss dialog
                                waitingDialog.dismiss();
                                finish();
                            }*/
                            if (Phone == null && Password == null) {
                                //waitingDialog.dismiss();
                                return;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //waitingDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


    }

    private void startLoginSystem() {
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(), REQUEST_CODE);
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
                        Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            } else {
                Toast.makeText(this, "Failed to sign in", Toast.LENGTH_SHORT).show();
                flag = true;
            }
        }

    }

}
