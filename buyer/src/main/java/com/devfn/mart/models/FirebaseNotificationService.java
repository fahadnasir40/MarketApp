package com.devfn.mart.models;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.devfn.mart.R;
import com.devfn.mart.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG ;

public class FirebaseNotificationService extends FirebaseMessagingService {


    private int Notification_Id =211;
    private String ChannelId = "1";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Token Received", "Refreshed token: " + refreshedToken);

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
                .setSmallIcon(R.drawable.rts_logo)
                .setContentTitle(payload.get("title"))
                .setContentText(payload.get("message"))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(false);

        notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        notificationManager.notify(Notification_Id++, notificationBuilder.build());
    }
}
