package com.unisaver.unisaver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.google.android.gms.ads.AdView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;

import java.util.ArrayList;
import java.util.List;

public class NotOzellestir extends AppCompatActivity {

    private TableAdapterDizi adapter = null;
    private String diziIsim;
    private long diziId = -1;
    private boolean duzenlemeMi = false;
    private List<GradeMappingEntity> mappings = new ArrayList<>();
    private GradingSystemEntity system = null;
    private GradingSystemDao dao = AppDataBase.getInstance(MainActivity.getAppContext()).gradingSystemDao();
    private boolean isDefault = false;
    private AdView banner = null;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase, LocaleHelper.getLanguage(newBase)));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.harf_notu_olusturma);

        // XML'deki AdView'i bul
        banner = findViewById(R.id.banner);

        // Reklam isteği oluştur ve yükle
        AdRequest adRequest = new AdRequest.Builder().build();
        banner.loadAd(adRequest);


        Intent intent = getIntent();
        diziIsim = intent.getStringExtra("diziIsim");

        EditText isim = findViewById(R.id.notDizisiIsmi);

        RecyclerView tablo = findViewById(R.id.tablo);
        tablo.addItemDecoration(new SpaceItemDecoration(16));

        LinearLayout ekleme = findViewById(R.id.notEklemeKismi);

        if (diziIsim == null) {
            duzenlemeMi = true;
            diziIsim = intent.getStringExtra("diziIsimDuzenleme");
            diziId = intent.getIntExtra("systemId", -1);
            new Thread(() -> {
                List<GradingSystemEntity> systems = dao.getAllSystems();
                for (GradingSystemEntity ent : systems) {
                    if (ent.id == diziId) {
                        system = ent;
                        break;
                    }
                }
                mappings = dao.getMappingsForSystem((int) diziId);
                isDefault = system.isDefault;
                if (isDefault) {
                    isim.setFocusable(false);
                    isim.setClickable(false);
                    ekleme.setVisibility(View.GONE);
                }
                isim.setText(diziIsim);
                adapter = new TableAdapterDizi(isDefault, (int) diziId, this);
                adapter.setData(mappings);
                tablo.setAdapter(adapter);
                tablo.setLayoutManager(new LinearLayoutManager(this));
            }).start();
        } else {
            isDefault = false;
            duzenlemeMi = false;

            new Thread(() -> {
                system = new GradingSystemEntity();
                system.name = diziIsim;
                system.isDefault = false;
                diziId = dao.insertSystem(system);
                adapter = new TableAdapterDizi(false, (int) diziId, this);
                tablo.setAdapter(adapter);
                tablo.setLayoutManager(new LinearLayoutManager(this));
                isim.setText(diziIsim);
            }).start();
        }

        ImageButton anaSayfa = findViewById(R.id.anaSayfaDon3);

        anaSayfa.setOnClickListener(view -> {
            new Thread(()->{
                List<GradingSystemEntity> systems = dao.getAllSystems();
                for (GradingSystemEntity ent : systems) {
                    if (ent.id == diziId) {
                        system = ent;
                        break;
                    }
                }
                system.name = isim.getText().toString();
                dao.updateSystems(systems);
                finish();
            }).start();
        });

        tablo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                adapter.isPositionVisible(recyclerView);
            }
        });

        Button bitir = findViewById(R.id.harfDizisiOlustur);

        bitir.setOnClickListener(v -> {
            new Thread(()->{
                List<GradingSystemEntity> systems = dao.getAllSystems();
                for (GradingSystemEntity ent : systems) {
                    if (ent.id == diziId) {
                        system = ent;
                        break;
                    }
                }
                system.name = isim.getText().toString();
                dao.updateSystems(systems);
                finish();
            }).start();
        });

        EditText harfIsim = findViewById(R.id.notIsmi);
        EditText harfEtki = findViewById(R.id.notunEtkisi);

        Button notuEkle = findViewById(R.id.notEkle);

        notuEkle.setOnClickListener(v -> {
            if (harfIsim.getText().length() == 0 || harfEtki.getText().length() == 0) {
                Toast.makeText(this, getString(R.string.bos_alan), Toast.LENGTH_SHORT).show();
            } else {
                double not = Double.parseDouble(String.valueOf(harfEtki.getText()));
                String harf = harfIsim.getText().toString();
                boolean control = true;
                for (GradeMappingEntity mp : mappings) {
                    if (mp.grade.equals(harf)) {
                        control = false;
                        Toast.makeText(this, getString(R.string.same_grade), Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                if (control) {
                    GradeMappingEntity grade = new GradeMappingEntity((int) diziId, harf, not);
                    adapter.notEkle(grade);
                    new Thread(() -> {
                        dao.insertMapping(grade);
                    }).start();
                    mappings.add(grade);
                }
            }
        });
    }
}
