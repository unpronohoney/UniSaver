package com.unisaver.unisaver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.IOException;

public class TranskriptTable extends AppCompatActivity {
    private AdView banner = null;
    private BelgeliHesap bh = null;
    private TableAdapter adapter = null;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase, LocaleHelper.getLanguage(newBase)));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trans_table_shower);

        RecyclerView recyclerView = findViewById(R.id.tablo);
        recyclerView.addItemDecoration(new SpaceItemDecoration(16));

        MobileAds.initialize(this, initializationStatus -> {});

        // XML'deki AdView'i bul
        banner = findViewById(R.id.banner);

        // Reklam isteği oluştur ve yükle
        AdRequest adRequest = new AdRequest.Builder().build();
        banner.loadAd(adRequest);

        ArrayAdapter<String> adap;

        TextView agno = findViewById(R.id.agnoBilgisi);
        TextView kredis = findViewById(R.id.krediBilgisi);
        Button buttonDersEkle = findViewById(R.id.dersEkle);
        Button showNotes = findViewById(R.id.notSystem);
        EditText editTextDersAdi = findViewById(R.id.addDersAdi), editTextKredi = findViewById(R.id.addDersCred);
        Spinner spinnerNotlar = findViewById(R.id.addDersSpinNot);
        Button sonSil = findViewById(R.id.sonSilme);
        ImageButton anaMenu = findViewById(R.id.anaSayfayaDon);
        Button basaAl = findViewById(R.id.basaAl);

        anaMenu.setOnClickListener(view -> finish());
        sonSil.setOnClickListener(v -> {
            if (adapter != null) {
                adapter.sonSilineniGeriAl();
            }
        });
        basaAl.setOnClickListener(v -> {
            if (adapter != null) {
                adapter.basaAl();
            }
        });

        try {
            String str = getIntent().getStringExtra("text");
            bh = new BelgeliHesap(str);
            adapter = new TableAdapter(bh, agno, kredis);
            String[] strArr = new String[bh.getMachine().getNotes().keySet().size()];
            int i = 0;
            for (String a : bh.getMachine().getNotes().keySet()) {
                strArr[i] = a;
                i++;
            }
            adap = new ArrayAdapter<>(MainActivity.getAppContext(), R.layout.spinner_inside, strArr);
            adap.setDropDownViewResource(R.layout.spinner_layout_list);
            spinnerNotlar.setAdapter(adap);

            String message = "";
            if (Math.round(bh.getMachine().getComputedAgno() * 100.0) / 100.0 != bh.getMachine().getAgno()) {
                message += getString(R.string.trans_agno_unmatch, bh.getMachine().getAgno(), bh.getMachine().getComputedAgno(), bh.getMachine().getComputedAgno());
            }
            if (!message.isEmpty()) {
                message += getString(R.string.and);
            }
            if (bh.getMachine().getComputedCred() != bh.getMachine().getCred()) {
                message += getString(R.string.trans_cred_unmatch, bh.getMachine().getCred(), bh.getMachine().getComputedCred(), bh.getMachine().getComputedCred());
            }

            if (!message.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.unmatch))
                        .setMessage(message)
                        .setPositiveButton(getString(R.string.tmm), (dialog, which) -> {
                        })
                        .show();
            }
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
            agno.setText(getString(R.string.gpa, Math.round(bh.getMachine().getComputedAgno() * 100.0) / 100.0));
            kredis.setText(getString(R.string.tot_cred, bh.getMachine().getComputedCred()));
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.trans_err), Toast.LENGTH_SHORT).show();
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                adapter.isPositionVisible(recyclerView);
            }
        });

        buttonDersEkle.setOnClickListener(v -> {
            if (editTextDersAdi.getText().length() != 0 && editTextKredi.getText().length() != 0) {
                String dersAdi = editTextDersAdi.getText().toString();
                int kredi = Integer.parseInt(editTextKredi.getText().toString());
                String dersNotu = spinnerNotlar.getSelectedItem().toString();
                boolean cont = true;
                if (kredi < 0) {
                    cont = false;
                    Toast.makeText(this, getString(R.string.cred_err), Toast.LENGTH_SHORT).show();
                }
                if (cont && adapter == null) {
                    Toast.makeText(this, getString(R.string.trans_err), Toast.LENGTH_SHORT).show();
                } else if (cont) {
                    adapter.addDers(dersAdi, kredi, dersNotu);
                }
            }
        });

        showNotes.setOnClickListener(v -> {
            if (bh != null) {
                StringBuilder message = new StringBuilder(getString(R.string.uni_grades));
                for (String not : bh.getMachine().getNotes().keySet()) {
                    message.append(not).append("  -->  ").append(bh.getMachine().getNotes().get(not)).append("\n");
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.uni_grades_head))
                        .setMessage(message.toString())
                        .setPositiveButton(getString(R.string.tmm), (dialog, which) -> {
                        })
                        .show();
            } else {
                Toast.makeText(MainActivity.getAppContext(), getString(R.string.trans_err), Toast.LENGTH_SHORT).show();
            }

        });

    }
}
