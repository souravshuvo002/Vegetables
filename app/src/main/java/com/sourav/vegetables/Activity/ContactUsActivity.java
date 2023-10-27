package com.sourav.vegetables.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sourav.vegetables.Helper.CheckPermission;
import com.sourav.vegetables.R;

import static android.Manifest.permission.CALL_PHONE;

public class ContactUsActivity extends AppCompatActivity {

    private ImageView imageViewCall, imageViewFb, imageViewWhatsApp, imageViewMessenger;
    private TextView textViewPhone, textViewEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        // Change status bar color
        changeStatusBarColor("#" + Integer.toHexString(ContextCompat.getColor
                (getApplicationContext(), R.color.colorPrimary) & 0x00ffffff));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Contact us");

        // getting views
        imageViewCall = (ImageView) findViewById(R.id.call);
        imageViewFb = (ImageView) findViewById(R.id.fb);
        imageViewWhatsApp = (ImageView) findViewById(R.id.whatsapp);
        //imageViewMessenger = (ImageView) findViewById(R.id.messenger);
        textViewEmail = (TextView) findViewById(R.id.textViewEmail);
        textViewPhone = (TextView) findViewById(R.id.textViewPhone);


        imageViewFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + "283419885541687"));
                startActivity(browser);
            }
        });

        imageViewWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWhatsApp();
            }
        });

        /*imageViewMessenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMessenger();
            }
        });
*/

        imageViewCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callNumber();
            }
        });

        textViewPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callNumber();
            }
        });

        textViewEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMail();
            }
        });


    }

    /*private void openMessenger() {
        Uri uri = Uri.parse("fb-messenger://user/");
        uri = ContentUris.withAppendedId(uri, Long.parseLong("140840707507091"));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }*/

    private void openWhatsApp() {
        String contact = this.getResources().getString(R.string.admin_phone); // use country code with your phone number
        String url = "https://api.whatsapp.com/send?phone=" + contact;
        try {
            PackageManager pm = this.getPackageManager();
            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(getApplicationContext(), "WhatsApp app not installed in your phone", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void openMail() {
        Intent intent = new Intent (Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{this.getResources().getString(R.string.admin_email)});
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        intent.setPackage("com.google.android.gm");
        if (intent.resolveActivity(getPackageManager())!=null)
            startActivity(intent);
        else
            Toast.makeText(this,"Gmail App is not installed",Toast.LENGTH_SHORT).show();
    }

    private void callNumber() {
        CheckPermission checkPermission = new CheckPermission(ContactUsActivity.this);

        if (checkPermission.checkSinglePermission(CALL_PHONE)) {
            Intent dialIntent = new Intent();
            dialIntent.setAction(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse("tel:" + this.getResources().getString(R.string.admin_phone)));
            startActivity(dialIntent);

        } else {
            checkPermission.requestForSinglePermission(CALL_PHONE);
        }
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
}
