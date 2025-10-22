package com.unisaver.unisaver;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;

public class NotDizisiOlustur extends AppCompatActivity {

    private TableAdapterNotDizileri adapter = null;
    private AdView banner = null;

    @Override
    protected void onResume() {
        super.onResume();
        // Adapter güncelle
        adapter.restart(); // veya veriyi yeniden yükle
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase, LocaleHelper.getLanguage(newBase)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.harf_notlari_act);

        // XML'deki AdView'i bul
        banner = findViewById(R.id.banner);

        // Reklam isteği oluştur ve yükle
        AdRequest adRequest = new AdRequest.Builder().build();
        banner.loadAd(adRequest);

        adapter = new TableAdapterNotDizileri(this);

        ImageButton anaSayfa = findViewById(R.id.anaSayfaDon);

        anaSayfa.setOnClickListener(view -> {
            List<GradingSystemEntity> data = adapter.getData();
            new Thread(() -> {
                GradingSystemDao dao = AppDataBase.getInstance(MainActivity.getAppContext()).gradingSystemDao();
                dao.updateSystems(data);
                Ders.fillMap();
            }).start();
            finish();
        });

        RecyclerView tablo = findViewById(R.id.harfTablo);
        tablo.addItemDecoration(new SpaceItemDecoration(16));

        Button tamam = findViewById(R.id.tamam);

        tamam.setOnClickListener(v -> {
            List<GradingSystemEntity> data = adapter.getData();
            new Thread(() -> {
                GradingSystemDao dao = AppDataBase.getInstance(MainActivity.getAppContext()).gradingSystemDao();
                dao.updateSystems(data);
                Ders.fillMap();
            }).start();
            finish();
        });

        ImageButton diziEkle = findViewById(R.id.harfNotlariEkle);
        diziEkle.setOnClickListener(v -> {
            NameInputBottomSheet bottomSheet = new NameInputBottomSheet(this);
            bottomSheet.show(getSupportFragmentManager(), "NameInputBottomSheet");
        });

        tablo.setAdapter(adapter);
        tablo.setLayoutManager(new LinearLayoutManager(this));

        tablo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                adapter.isPositionVisible(recyclerView);
            }
        });
    }
}
