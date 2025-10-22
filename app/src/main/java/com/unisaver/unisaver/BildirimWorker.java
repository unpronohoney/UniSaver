package com.unisaver.unisaver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class BildirimWorker extends Worker {

    private static final String CHANNEL_ID = "app_notification_channel";
    private final int[] months = {Calendar.JANUARY, Calendar.FEBRUARY, Calendar.APRIL, Calendar.MAY, Calendar.SEPTEMBER, Calendar.NOVEMBER};
    private final int[] days = {5, 13, 1, 15, 15, 20};

    public BildirimWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        int workerId = getInputData().getInt("workerId", -1);
        switch (workerId) {
            case 1:
                runWorkerJan();
                break;
            case 2:
                runWorkerFeb();
                break;
            case 3:
                runWorkerMar();
                break;
            case 4:
                runWorkerMay();
                break;
            case 5:
                runWorkerAug();
                break;
            case 6:
                runWorkerNov();
                break;
            default:
                return Result.failure();
        }

        return Result.success();
    }

    private void runWorkerJan() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        String title = prefs.getString("title1", "Varsayılan Başlık");
        String message = prefs.getString("message1", "Varsayılan Mesaj");
        sendNotification(title, message, 1);
        restartWorker(months[0], days[0], 1);
    }

    private void runWorkerFeb() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        String title = prefs.getString("title2", "Varsayılan Başlık");
        String message = prefs.getString("message2", "Varsayılan Mesaj");
        sendNotification(title, message, 2);
        restartWorker(months[1], days[1], 2);
    }
    private void runWorkerMar() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        String title = prefs.getString("title3", "Varsayılan Başlık");
        String message = prefs.getString("message3", "Varsayılan Mesaj");
        sendNotification(title, message, 3);
        restartWorker(months[2], days[2], 3);
    }
    private void runWorkerMay() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        String title = prefs.getString("title4", "Varsayılan Başlık");
        String message = prefs.getString("message4", "Varsayılan Mesaj");
        sendNotification(title, message, 4);
        restartWorker(months[3], days[3], 4);
    }
    private void runWorkerAug() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        String title = prefs.getString("title5", "Varsayılan Başlık");
        String message = prefs.getString("message5", "Varsayılan Mesaj");
        sendNotification(title, message, 5);
        restartWorker(months[4], days[4], 5);
    }
    private void runWorkerNov() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        String title = prefs.getString("title6", "Varsayılan Başlık");
        String message = prefs.getString("message6", "Varsayılan Mesaj");
        sendNotification(title, message, 6);
        restartWorker(months[5], days[5], 6);
    }

    private void restartWorker(int month, int day, int workerId) {
        // Worker'ın başlatılacağı tarihi ayarlıyoruz
        Calendar calendar = Calendar.getInstance();

            calendar.add(Calendar.YEAR, 1);

        // Zamanı gecikme olarak hesapla
        long delayMillis = calendar.getTimeInMillis() - System.currentTimeMillis();

        // WorkData ile worker'a veri gönderme (workerId)
        Data data = new Data.Builder()
                .putInt("workerId", workerId)
                .build();

        // Worker'ı başlatma
        OneTimeWorkRequest workerRequest = new OneTimeWorkRequest.Builder(BildirimWorker.class)
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build();

        // WorkManager ile iş kuyruğa alınır
        WorkManager.getInstance(getApplicationContext()).enqueue(workerRequest);

        Log.d("BildirimWorker", "Worker " + workerId + " scheduled for " + calendar.getTime());
    }

    // Bildirim Gönderme
    private void sendNotification(String title, String message, int id) {
        Context context = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "App Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        notificationManager.createNotificationChannel(channel);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                new Intent(getApplicationContext(), MainActivity.class),
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        System.out.println(title);
        System.out.println(message);
        notificationManager.notify(id, notification);
    }
}
