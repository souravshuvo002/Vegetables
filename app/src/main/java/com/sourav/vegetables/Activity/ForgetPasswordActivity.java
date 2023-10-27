package com.sourav.vegetables.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.sourav.vegetables.Helper.NetWorkConfig;
import com.sourav.vegetables.R;

public class ForgetPasswordActivity extends AppCompatActivity {

    public Button btn_reset;
    private TextInputEditText editTextPhone;
    private NetWorkConfig netWorkConfig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forget_password);


        netWorkConfig = new NetWorkConfig(this);

        /**
         *  Network Connection Check
         */
        if (!netWorkConfig.isNetworkAvailable()) {
            netWorkConfig.createNetErrorDialog();
            return;
        }


        // getting views
        editTextPhone = (TextInputEditText) findViewById(R.id.input_phone);
        btn_reset = (Button) findViewById(R.id.btn_reset);


        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String number = editTextPhone.getText().toString().trim();

                if (number.isEmpty() || number.length() < 11) {
                    editTextPhone.setError("Valid number is required");
                    editTextPhone.requestFocus();
                    return;
                }

                final String phoneNumber = "+88" +  number;
                Intent intent = new Intent(ForgetPasswordActivity.this, VerifyPhoneActivity.class);
                intent.putExtra("phoneNumber", phoneNumber);
                startActivity(intent);
            }
        });

    }
}
