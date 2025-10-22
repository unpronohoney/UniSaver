package com.unisaver.unisaver;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class InfoActivity extends AppCompatActivity {
    private AdView banner = null;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase, LocaleHelper.getLanguage(newBase)));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.application_info);

        banner = findViewById(R.id.banner);

        AdRequest adRequest = new AdRequest.Builder().build();
        banner.loadAd(adRequest);

        ImageButton backButton = findViewById(R.id.anaSayfaDonIn);
        backButton.setOnClickListener(view -> finish());
    }
}
