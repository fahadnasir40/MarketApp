package com.devfn.seller.model;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.devfn.seller.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FirebaseNotificationServiceSeller extends FirebaseMessagingService {


    private int Notification_Id =211;
    private String ChannelId = "2";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        String uid = FirebaseAuth.getInstance().getUid();
        reference.child(uid).child("device_token").setValue(refreshedToken);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String,String> payload = remoteMessage.getData();
        sendNotification(payload);
    }


    private void sendNotification(Map<String,String> payload) {

        NotificationManager notificationManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notificationManager = (NotificationManager) getApplicationContext().getSystemService(NotificationManager.class);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(ChannelId,
                    "General", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), ChannelId)
                .setSmallIcon(R.drawable.rts_seller_logo)
                .setContentTitle(payload.get("title"))
                .setContentText(payload.get("message"))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(false);

        notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        notificationManager.notify(Notification_Id++, notificationBuilder.build());
    }
}
