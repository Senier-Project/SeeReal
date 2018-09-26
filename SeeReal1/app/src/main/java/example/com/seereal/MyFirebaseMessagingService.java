package example.com.seereal;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RelativeLayout;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static android.support.constraint.Constraints.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            // 이거 추가 하면
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE );
            PowerManager.WakeLock wakeLock = pm.newWakeLock( PowerManager.SCREEN_DIM_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG" );
            wakeLock.acquire(3000);


            String title =remoteMessage.getData().get("title").toString();
            String text = remoteMessage.getData().get("text").toString();
            sendNotification(title,text);
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void sendNotification(String title, String text) {
        ReceivedSingleton.getInstance().onReceived();
        //PlayRTCMain.isReceived=true;
        Bundle bundle =new Bundle();
        bundle.putString("id",text);
        Intent intent = new Intent(this, PlayRTCMain.class);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_stat_ic_notification)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
