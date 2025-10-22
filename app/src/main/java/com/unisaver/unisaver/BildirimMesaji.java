package com.unisaver.unisaver;

import android.content.Context;
import android.content.SharedPreferences;

public class BildirimMesaji {
    public static void getRandomMessageForNotification() {

        Context context = MainActivity.getAppContext();

        double rand = Math.random();
        int choose;
        if (rand <= 0.6) choose = 1;
        else if (rand <= 0.85) choose = 2;
        else choose = 3;

        String head1 = context.getString(R.string.head1);
        String body1 = context.getString(choose == 1 ? R.string.pool1_1 : choose == 2 ? R.string.pool1_2 : R.string.pool1_3);

        rand = Math.random();
        if (rand <= 0.6) choose = 1;
        else if (rand <= 0.85) choose = 2;
        else choose = 3;
        String head2 = context.getString(R.string.head2);
        String body2 = context.getString(choose == 1 ? R.string.pool2_1 : choose == 2 ? R.string.pool2_2 : R.string.pool2_3);

        rand = Math.random();
        if (rand <= 0.6) choose = 1;
        else if (rand <= 0.85) choose = 2;
        else choose = 3;
        String head3 = context.getString(R.string.head3);
        String body3 = context.getString(choose == 1 ? R.string.pool3_1 : choose == 2 ? R.string.pool3_2 : R.string.pool3_3);

        rand = Math.random();
        if (rand <= 0.6) choose = 1;
        else if (rand <= 0.85) choose = 2;
        else choose = 3;
        String head4 = context.getString(R.string.head4);
        String body4 = context.getString(choose == 1 ? R.string.pool4_1 : choose == 2 ? R.string.pool4_2 : R.string.pool4_3);

        rand = Math.random();
        if (rand <= 0.6) choose = 1;
        else if (rand <= 0.85) choose = 2;
        else choose = 3;
        String head5 = context.getString(R.string.head5);
        String body5 = context.getString(choose == 1 ? R.string.pool5_1 : choose == 2 ? R.string.pool5_2 : R.string.pool5_3);

        rand = Math.random();
        if (rand <= 0.6) choose = 1;
        else if (rand <= 0.85) choose = 2;
        else choose = 3;
        String head6 = context.getString(R.string.head6);
        String body6 = context.getString(choose == 1 ? R.string.pool6_1 : choose == 2 ? R.string.pool6_2 : R.string.pool6_3);

        SharedPreferences prefs = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("title1", head1);
        editor.putString("message1", body1);
        editor.putString("title2", head2);
        editor.putString("message2", body2);
        editor.putString("title3", head3);
        editor.putString("message3", body3);
        editor.putString("title4", head4);
        editor.putString("message4", body4);
        editor.putString("title5", head5);
        editor.putString("message5", body5);
        editor.putString("title6", head6);
        editor.putString("message6", body6);

        editor.apply();
    }
}
