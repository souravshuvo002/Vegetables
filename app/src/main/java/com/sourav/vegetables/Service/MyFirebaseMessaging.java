package com.sourav.vegetables.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sourav.vegetables.Activity.AllOrderHistoryActivity;
import com.sourav.vegetables.Activity.NotificationActivity;
import com.sourav.vegetables.Api.ApiClient;
import com.sourav.vegetables.Api.ApiService;
import com.sourav.vegetables.Database.NotificationDatabase;
import com.sourav.vegetables.Helper.NotificationHelper;
import com.sourav.vegetables.Model.Result;
import com.sourav.vegetables.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Notification.DEFAULT_SOUND;
import static android.app.Notification.DEFAULT_VIBRATE;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        updateTokenToServer();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData() != null) {
            Map<String, String> data = remoteMessage.getData();
            String title = data.get("title");
            String message = data.get("message");
            DateFormat simpledateFormat = new SimpleDateFormat("MMMM dd yyyy");
            DateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm a");
            Date date = new Date(remoteMessage.getSentTime());
            Date date2 = new Date();
            String time = simpledateFormat.format(date2) + "," + simpleTimeFormat.format(date);

            createNotification(remoteMessage, time);
        }

    }

    private NotificationManager notifManager;
    public int NOTIFY_ID = 0;
    private final String GROUP_KEY = "GROUP_KEY_RANDOM_NAME";

    public void createNotification(RemoteMessage remoteMessage, String send_time) {
        //final int NOTIFY_ID = 1002;
        NOTIFY_ID = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);


        // Get Information From Message
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String message = data.get("message");
        String type = data.get("type");


        // There are hardcoding only for show it's just strings
        String name = "Vegetables User";
        String id = "com.sourav.vegetables"; // The user-visible name of the channel.
        String description = "my_package_first_channel"; // The user-visible description of the channel.

        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;

        if (notifManager == null) {
            notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, name, importance);
                mChannel.setDescription(description);
                mChannel.enableVibration(true);
                mChannel.setLightColor(Color.GREEN);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this, id);

            if (type.equalsIgnoreCase("Notification")) {
                intent = new Intent(this, NotificationActivity.class);
                //Saving to Room Database
                new Thread(() -> {
                    new NotificationDatabase(getApplicationContext()).addNotification(new com.sourav.vegetables.Model.Notification(
                            title,
                            message,
                            send_time,
                            "true"
                    ));
                }).start();
            } else {
                intent = new Intent(this, AllOrderHistoryActivity.class);
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            builder.setContentText(message)  // required
                    .setContentTitle(title)
                    .setSmallIcon(R.mipmap.ic_launcher) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(message))
                    .setAutoCancel(true)
                    .setShowWhen(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(message)
                    .setGroupSummary(true)
                    .setGroup(GROUP_KEY)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        } else {

            builder = new NotificationCompat.Builder(this);

            if (type.equalsIgnoreCase("Notification")) {
                intent = new Intent(this, NotificationActivity.class);
                //Saving to Room Database
                new Thread(() -> {
                    new NotificationDatabase(getApplicationContext()).addNotification(new com.sourav.vegetables.Model.Notification(
                            title,
                            message,
                            send_time,
                            "true"
                    ));
                }).start();
            } else {
                intent = new Intent(this, AllOrderHistoryActivity.class);
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            builder.setContentText(message)// required
                    .setContentTitle(title)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(message))
                    .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(message)
                    .setShowWhen(true)
                    .setGroupSummary(true)
                    .setGroup(GROUP_KEY)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH);
        } // else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        /*// Set the image for the notification
        if (remoteMessage.getNotification().getImageUrl() != null) {
            Bitmap bitmap = getBitmapFromUrl(remoteMessage.getData().get("image-url"));
            builder.setLargeIcon(bitmap);
        }*/

        Notification notification = builder.build();
        notifManager.notify(NOTIFY_ID, notification);

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
                        Toast.makeText(MyFirebaseMessaging.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

     /*private void sendNotification(RemoteMessage remoteMessage) {

        Log.d("Normal", "Normal");

        // Get Information From Message
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String message = data.get("message");

        Intent intent = new Intent(this, AllOrderHistoryActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // or NotificationCompat.PRIORITY_MAX
                .setContentIntent(pIntent)
                .setOngoing(true)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(new Random().nextInt(), builder.build());

    }

    private void sendNotificationAPI26(RemoteMessage remoteMessage) {

        Log.d("High", "High");


        // Get Information From Message
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String message = data.get("message");


        // From API level 26, we need implement Notification Channel
        NotificationHelper helper;
        NotificationCompat.Builder builder;

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        helper = new NotificationHelper(this);
        builder = helper.getNotification(title, message, defaultSoundUri);
        helper.getManager().notify(new Random().nextInt(), builder.build());
    }


    private void testNotificationAPI26(RemoteMessage remoteMessage) {

        // Get Information From Message
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String message = data.get("message");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String description = "Wow";
            int importance = NotificationManager.IMPORTANCE_HIGH; //Important for heads-up notification
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(description);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE) //Important for heads-up notification
                .setPriority(Notification.PRIORITY_MAX); //Important for heads-up notification

        Notification buildNotification = mBuilder.build();
        NotificationManager mNotifyMgr = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(001, buildNotification);
    }

    private void showNotification(String title, String body, String time) {
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
        builder.setAutoCancel(true)
                .setLights(Color.RED, 3000, 3000)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());

        //Saving to Room Database
        new Thread(() -> {
            new NotificationDatabase(getApplicationContext()).addNotification(new com.sourav.vegetables.Model.Notification(
                    title,
                    body,
                    time
            ));
        }).start();
    }*/

    public Bitmap getBitmapFromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            Log.e("awesome", "Error in getting notification image: " + e.getLocalizedMessage());
            return null;
        }
    }
}