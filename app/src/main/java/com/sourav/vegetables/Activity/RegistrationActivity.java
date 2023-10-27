package com.sourav.vegetables.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.sourav.vegetables.Api.ApiClient;
import com.sourav.vegetables.Api.ApiService;
import com.sourav.vegetables.Common.Common;
import com.sourav.vegetables.Helper.NetWorkConfig;
import com.sourav.vegetables.Model.Result;
import com.sourav.vegetables.Model.User;
import com.sourav.vegetables.R;

public class RegistrationActivity extends AppCompatActivity {


    private TextInputEditText editTextName, editTextEmail, editTextPhone,
            editTextPassword, editTextConPassword, editTextAddress;
    private CheckBox checkboxTermAgreement;
    private LinearLayout laySignIn;
    private Button btn_reg;
    private String strName, strEmail, strPhone, strAddress, strPass, strConPass;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private NetWorkConfig netWorkConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_registration);

        netWorkConfig = new NetWorkConfig(this);

        /**
         *  Network Connection Check
         */
        if (!netWorkConfig.isNetworkAvailable()) {
            netWorkConfig.createNetErrorDialog();
            return;
        }


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RegistrationActivity.this);
        editor = sharedPreferences.edit();

        // getting views
        editTextName = (TextInputEditText) findViewById(R.id.editTextName);
        editTextEmail = (TextInputEditText) findViewById(R.id.editTextEmail);
        editTextPhone = (TextInputEditText) findViewById(R.id.editTextPhone);
        editTextAddress = (TextInputEditText) findViewById(R.id.editTextAddress);
        editTextPassword = (TextInputEditText) findViewById(R.id.editTextPassword);
        editTextConPassword = (TextInputEditText) findViewById(R.id.editTextConPassword);
        checkboxTermAgreement = (CheckBox) findViewById(R.id.checkboxTermAgreement);
        laySignIn = (LinearLayout) findViewById(R.id.laySignIn);
        btn_reg = (Button) findViewById(R.id.btn_reg);

        // setting phone number
        editTextPhone.setText(Common.User_phone);

        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });


        laySignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void registerUser() {
        if(checkboxTermAgreement.isChecked()){

            strName = editTextName.getText().toString().trim();
            strEmail = editTextEmail.getText().toString().trim();
            strPhone = editTextPhone.getText().toString().trim();
            strAddress = editTextAddress.getText().toString().trim();
            strPass = editTextPassword.getText().toString().trim();
            strConPass = editTextConPassword.getText().toString().trim();

            if(!strPass.equals(strConPass))
            {
                Toast.makeText(getApplicationContext(), "Password doesn't match", Toast.LENGTH_LONG).show();
                return;
            }
            else
            {
                final android.app.AlertDialog waitingDialog = new SpotsDialog(RegistrationActivity.this);
                waitingDialog.show();
                waitingDialog.setMessage("Please wait ...");

                //Defining retrofit api service
                ApiService service = ApiClient.getClientVegetables().create(ApiService.class);
                Call<Result> call = service.registerUser(strName, strPass, strPhone, strEmail, strAddress);

                call.enqueue(new Callback<Result>() {
                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        waitingDialog.dismiss();

                        if(!response.body().getError())
                        {
                            editor.putString("USERNAME", response.body().getUser().getUsername());
                            editor.putString("PHONE",editTextPhone.getText().toString());
                            editor.putString("PASSWORD", editTextPassword.getText().toString());
                            editor.putString("USER_ID", response.body().getUser().getId());
                            editor.apply();

                            Common.User_phone = strPhone;
                            Common.User_ID = response.body().getUser().getId();


                            addtoFirebase("+88" + strPhone, strName, strPass, strAddress, strEmail);

                            updateTokenToServer();

                            Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                            startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));
                            finish();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
        else
        {
            Toast.makeText(getApplicationContext(), "You must accept privacy policy and term & condition in order to sign up for Dhacai.", Toast.LENGTH_LONG).show();
        }
    }

    private void addtoFirebase(String phone, String name, String pass, String address, String email) {

        FirebaseDatabase database;
        DatabaseReference users;

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");


        //Check if exists on Firebase Users
        users.orderByKey().equalTo(phone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(phone).exists()) // If exists
                        {
                            //We will create new user and login
                            User newUser = new User();
                            newUser.setPhone(phone);
                            newUser.setUsername(name);
                            newUser.setPassword(pass);
                            newUser.setAddress(address);
                            newUser.setEmail(email);

                            //Add to Firebase
                            users.child(phone)
                                    .setValue(newUser)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful())
                                            //Toast.makeText(RegistrationActivity.this, "User register successful!", Toast.LENGTH_SHORT).show();
                                        Common.User_phone = phone;
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(RegistrationActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();

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
                        Toast.makeText(RegistrationActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }



    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
