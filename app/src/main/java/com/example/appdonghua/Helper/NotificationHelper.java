package com.example.appdonghua.Helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.appdonghua.Activity.MainActivity;
import com.example.appdonghua.R;

public class NotificationHelper {
    private static String ID = "donghua_channel";
    private static String NAME = "Donghua Notifications";
    private static String DESC = "Thông báo từ ứng dụng Donghua";
    private Context context;
    private NotificationManager notificationManager;
    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(ID, NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(DESC);
            channel.enableLights(true);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public void sendNotification(String title, String message) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ID)
                .setSmallIcon(R.drawable.bg_splash)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(soundUri)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
    public void sendActionNotification(String title, String message) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        // Action button 1
        Intent actionIntent1 = new Intent(context, MainActivity.class);
        actionIntent1.putExtra("action", "view");
        PendingIntent actionPendingIntent1 = PendingIntent.getActivity(
                context,
                1,
                actionIntent1,
                PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_notification, "Xem ngay", actionPendingIntent1)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public void sendProgressNotification(int notificationId, String title, int progress, int maxProgress) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText("Đang tải...")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setProgress(maxProgress, progress, false);

        notificationManager.notify(notificationId, builder.build());
    }

    // Hủy thông báo theo ID
    public void cancelNotification(int notificationId) {
        notificationManager.cancel(notificationId);
    }

    // Hủy tất cả thông báo
    public void cancelAllNotifications() {
        notificationManager.cancelAll();
    }
}
