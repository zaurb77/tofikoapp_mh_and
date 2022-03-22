package com.angaihouse.utils;



import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.angaihouse.R;
import com.angaihouse.activity.RestaurantListActivity;
import com.angaihouse.activity.SelectRestaurantActivity;
import com.angaihouse.activity.newmodel.Home_New;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;



public class MyFirebaseMessagingService extends FirebaseMessagingService {

    Intent _intent;
    static int notificationId = 0;
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        new StoreUserData(this).setString(Constants.USER_FCM, s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // TODO(developer): Handle FCM messages here.
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message_Data_Payload" + remoteMessage.getData().toString());
            Log.d(TAG, "Message data payload ==>" + remoteMessage.getData().toString());
            //sendNotification(remoteMessage.getData().get("message"), remoteMessage.getData().get("cmd"));
            sendNotification(remoteMessage.getData().get("message"), remoteMessage.getData().get("cmd"), remoteMessage.getData().get("type"));
        }
    }

    private void sendNotification(String messageBody, String cmd, String type) {
        Intent intent = new Intent();

//        Log.i("jhdfjsdjf==>", "main");
//        intent = new Intent(this, Home_New.class).putExtra("cmd", cmd);


        if (type.equalsIgnoreCase( "booking" )) {
            _intent = new Intent( this, SelectRestaurantActivity.class );
        }else {
            _intent = new Intent( this, Home_New.class );
            _intent.putExtra("cmd", cmd);
        }

        _intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, _intent,
                PendingIntent.FLAG_ONE_SHOT);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "signals";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        CharSequence channelName = "Secondhand soko";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(getNotificationIcon())
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(messageBody)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody))
                .setVibrate(new long[]{100, 200})
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        notificationId++;
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.ic_launcher : R.mipmap.ic_launcher;
    }

}



