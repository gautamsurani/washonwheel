package com.washonwheel.android.Util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.washonwheel.android.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class FirebaseMessangingService extends FirebaseMessagingService {
    NotificationManager notificationManager;
    public static int count = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("CheckFCM", "Check_DONE");
        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String message = data.get("message");
        String image = data.get("image");
        String notificationType = data.get("display_image");

        if (notificationType.equals("Yes")) {
            final Intent offerIntent = new Intent();
            offerIntent.putExtra("message", message);
            offerIntent.putExtra("imageurl", image);
            if (Utils.getUserId(getApplicationContext()) != null) {
                offerIntent.setClassName(getApplicationContext(), "com.washonwheel.android.Activity.MainActivity");
            } else {
                offerIntent.setClassName(getApplicationContext(), "com.washonwheel.android.Activity.LoginActivity");
            }
            ShowCommonNotificationWithImage(image, offerIntent, title, message);
        } else {
            final Intent intAlert = new Intent();
            if (Utils.getUserId(getApplicationContext()) != null) {
                intAlert.setClassName(getApplicationContext(), "com.washonwheel.android.Activity.MainActivity");
            } else {
                intAlert.setClassName(getApplicationContext(), "com.washonwheel.android.Activity.LoginActivity");
            }
            sendNotificationBlank(intAlert, title, message);
        }
    }

    private void ShowCommonNotificationWithImage(String img, Intent gotoIntent, String StrTitle, String StrDescip) {
        Bitmap remote_picture;
        try {
            NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
            notiStyle.setSummaryText(StrDescip);
            remote_picture = getBitmapFromURL(img);
            assert remote_picture != null;
            int imageWidth = remote_picture.getWidth();
            int imageHeight = remote_picture.getHeight();
            DisplayMetrics metrics = this.getResources().getDisplayMetrics();
            int newWidth = metrics.widthPixels;
            float scaleFactor = (float) newWidth / (float) imageWidth;
            int newHeight = (int) (imageHeight * scaleFactor);
            remote_picture = Bitmap.createScaledBitmap(remote_picture, newWidth, newHeight, true);
            notiStyle.bigPicture(remote_picture);
            notificationManager = (NotificationManager) getApplicationContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent contentIntent;
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            contentIntent = PendingIntent.getActivity(getApplicationContext(),
                    (int) (Math.random() * 100), gotoIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
            android.app.Notification notification = mBuilder.setSmallIcon(R.drawable.ic_pushiconn).setWhen(0)
                    .setColor(getResources().getColor(R.color.colorPrimary)).setAutoCancel(true).setContentTitle(StrTitle)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(StrDescip))
                    .setContentIntent(contentIntent)
                    .setVibrate(new long[]{100, 250})
                    .setSound(defaultSoundUri)
                    .setContentText(StrDescip).setStyle(notiStyle).build();
            count++;
            notificationManager.notify(count, notification);

        } catch (Exception e) {
            Log.e("This", e.getMessage() + "  0");
        }
    }

    private void sendNotificationBlank(Intent gotoIntent, String title, String message) {
        int icon = R.drawable.ic_pushiconn;
        try {
            PendingIntent contentIntent;
            contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, gotoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder;
            notificationBuilder = new NotificationCompat.Builder(this);
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimary));
            notificationBuilder.setSmallIcon(icon);
            notificationBuilder.setContentTitle(title);
            notificationBuilder.setContentText(message);
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setSound(defaultSoundUri);
            notificationBuilder.setContentIntent(contentIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            count++;
            assert notificationManager != null;
            notificationManager.notify(count, notificationBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            return null;
        }
    }
}