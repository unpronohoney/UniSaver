package com.unisaver.unisaver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class BootReceiver extends BroadcastReceiver {

    private final int[] months = {Calendar.JANUARY, Calendar.FEBRUARY, Calendar.APRIL, Calendar.MAY, Calendar.SEPTEMBER, Calendar.NOVEMBER};
    private final int[] days = {5, 13, 1, 15, 15, 20};

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) || Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction())) {
            for (int i = 0; i < months.length; i++) {
                startWorker(months[i], days[i], i + 1, context);
            }
        }
    }

    private void startWorker(int month, int day, int workerId, Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);


        long delayMillis = calendar.getTimeInMillis() - System.currentTimeMillis();
        if (delayMillis < 0) {
            // Eğer tarih geçmişse, bir sonraki yıl için ayarla
            calendar.add(Calendar.YEAR, 1);
            delayMillis = calendar.getTimeInMillis() - System.currentTimeMillis();
        }

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
        WorkManager.getInstance(context).enqueue(workerRequest);

        Log.d("MainActivity", "Worker " + workerId + " scheduled for " + calendar.getTime());
    }
}
