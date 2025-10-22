package com.unisaver.unisaver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class ManuelHesap extends AppCompatActivity {
    private AdView banner = null;
    private int dersNolar = 1;
    private TableAdapterManuel adapter = null;
    private double ilkAgno = -1;
    private int ilkKredi = -1;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase, LocaleHelper.getLanguage(newBase)));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.manuel);

        MobileAds.initialize(this, initializationStatus -> {});

        // XML'deki AdView'i bul
        banner = findViewById(R.id.banner);

        // Reklam isteği oluştur ve yükle
        AdRequest adRequest = new AdRequest.Builder().build();
        banner.loadAd(adRequest);

        String[] harfs = Ders.getSortedLetters();

        String[] eskiharfler = new String[harfs.length + 1];
        String[] yeniharfler = new String[harfs.length];

        eskiharfler[0] = getString(R.string.yok);
        int i = 0;
        for (String harf : harfs) {
            eskiharfler[i+1] = harf;
            yeniharfler[i] = harf;
            i++;
        }

        Button dersGirisineGec = findViewById(R.id.gec1);
        Button yenile = findViewById(R.id.yenile1);
        Button dersEkle = findViewById(R.id.addDers1);
        ImageButton share = findViewById(R.id.shareButton);
        ImageButton mainMenu = findViewById(R.id.anaSayfaDon1);

        EditText agno = findViewById(R.id.agno1);
        EditText kredi = findViewById(R.id.kredis1);
        EditText dersKredi = findViewById(R.id.dersKredi1);
        EditText dersAdi = findViewById(R.id.dersAdi);

        Spinner eskiHarf = findViewById(R.id.eskiHarf);
        Spinner yeniHarf = findViewById(R.id.yeniHarf);

        RecyclerView tablo = findViewById(R.id.tablo);
        tablo.addItemDecoration(new SpaceItemDecoration(16));

        TextView info = findViewById(R.id.hesaplananAgno1);
        TextView derseGecis = findViewById(R.id.derseGecis);
        TextView tabloIsim = findViewById(R.id.tabloIsim);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(MainActivity.getAppContext(), R.layout.spinner_inside, eskiharfler);
        adapter1.setDropDownViewResource(R.layout.spinner_layout_list);
        eskiHarf.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(MainActivity.getAppContext(), R.layout.spinner_inside, yeniharfler);
        adapter2.setDropDownViewResource(R.layout.spinner_layout_list);
        yeniHarf.setAdapter(adapter2);

        LinearLayout dersEkleme = findViewById(R.id.dersEkleKismi);
        LinearLayout donemKismi = findViewById(R.id.donemKismi);

        dersEkleme.setVisibility(View.GONE);

        share.setVisibility(View.GONE);
        tabloIsim.setVisibility(View.GONE);
        info.setVisibility(View.GONE);

        TextView oncekiDonem = findViewById(R.id.oncekiBilgi);
        oncekiDonem.setVisibility(View.GONE);

        share.setOnClickListener(view -> {
            if (!MainActivity.getBuDonem(false).getDersler().isEmpty()) {
                StringBuilder send = new StringBuilder();
                ArrayList<Ders> dersler = MainActivity.getBuDonem(false).getDersler();
                for (Ders d : dersler) {
                    send.append(d.toStringShare());
                }
                send.append("\n");
                send.append(getString(R.string.ilk_bilgi));
                send.append(getString(R.string.agno_cred, ilkAgno, ilkKredi));
                send.append("\n");
                send.append(getString(R.string.sonuc));
                send.append(MainActivity.getBuDonem(false).getAgnoInfo());

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, send.toString());
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, getString(R.string.choose_app));
                startActivity(shareIntent);
            }
        });

        mainMenu.setOnClickListener(view -> {
            ilkAgno = -1;
            ilkKredi = -1;
            adapter = null;
            dersNolar = 1;
            MainActivity.resetBuDonem(false);
            finish();
        });

        tablo.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                adapter.isPositionVisible(recyclerView);
            }
        });

        yenile.setOnClickListener(v -> {
            ilkAgno = -1;
            ilkKredi = -1;
            oncekiDonem.setVisibility(View.GONE);
            donemKismi.setVisibility(View.VISIBLE);
            derseGecis.setText(getString(R.string.dersBekle));
            share.setVisibility(View.GONE);
            tabloIsim.setVisibility(View.GONE);
            info.setVisibility(View.GONE);
            dersEkleme.setVisibility(View.GONE);
            if (adapter != null) adapter.reset();
            dersNolar = 1;
            dersAdi.setText("");
            agno.setText("");
            kredi.setText("");
            dersKredi.setText("");
            MainActivity.resetBuDonem(false);
        });

        OnBackPressedDispatcher onBackPressedDispatcher = new OnBackPressedDispatcher(() -> {
            ilkAgno = -1;
            ilkKredi = -1;
            adapter = null;
            dersNolar = 1;
            MainActivity.resetBuDonem(false);
            finish();
        });

        dersGirisineGec.setOnClickListener(v -> {
            if (agno.getText().length() == 0 || kredi.getText().length() == 0) {
                Toast.makeText(MainActivity.getAppContext(), getString(R.string.bos_alan), Toast.LENGTH_SHORT).show();
            } else if (Integer.parseInt(kredi.getText().toString()) <= 0) {
                Toast.makeText(MainActivity.getAppContext(), getString(R.string.cred_err), Toast.LENGTH_SHORT).show();
            } else {
                String agnoInput = agno.getText().toString().trim();
                agnoInput = agnoInput.replace(",", ".");
                double agnoValue = Double.parseDouble(agnoInput);

                if (agnoValue > Ders.getPoint(Ders.getMaxHarf()) || agnoValue < Ders.getPoint(Ders.getMinHarf())) {
                    Toast.makeText(MainActivity.getAppContext(), getString(R.string.gpa_err), Toast.LENGTH_LONG).show();
                } else {
                    ilkAgno = agnoValue;
                    ilkKredi = Integer.parseInt(String.valueOf(kredi.getText()));
                    MainActivity.setBuDonem(false, new ThisTerm(new GenelNot(ilkKredi, ilkAgno)));
                    MainActivity.getBuDonem(false).yuvarlamaPaylari();
                    if (adapter == null) {
                        adapter = new TableAdapterManuel(info, info, tabloIsim, share);
                    }
                    tablo.setLayoutManager(new LinearLayoutManager(this));
                    tablo.setAdapter(adapter);
                    dersNolar = 1;
                    Toast.makeText(MainActivity.getAppContext(), getString(R.string.ders_gir_info), Toast.LENGTH_SHORT).show();
                    derseGecis.setText(getString(R.string.dersEkleyebilirsin));
                    dersEkleme.setVisibility(View.VISIBLE);
                    donemKismi.setVisibility(View.GONE);
                    oncekiDonem.setText(getString(R.string.agno_cred_info, ilkAgno, ilkKredi));
                    oncekiDonem.setVisibility(View.VISIBLE);
                }
            }
        });

        dersEkle.setOnClickListener(v -> {
            if (MainActivity.getBuDonem(false) == null) {
                Toast.makeText(MainActivity.getAppContext(), getString(R.string.first_basis), Toast.LENGTH_SHORT).show();
            } else if (dersAdi.getText().length() == 0 || dersKredi.getText().length() == 0) {
                Toast.makeText(MainActivity.getAppContext(), getString(R.string.bos_alan), Toast.LENGTH_SHORT).show();
            } else if (Integer.parseInt(String.valueOf(dersKredi.getText())) <= 0) {
                Toast.makeText(MainActivity.getAppContext(), getString(R.string.cred_err), Toast.LENGTH_SHORT).show();
            } else {
                if (adapter.getData().isEmpty()) {
                    share.setVisibility(View.VISIBLE);
                    tabloIsim.setVisibility(View.VISIBLE);
                    info.setVisibility(View.VISIBLE);
                    dersNolar = 1;
                }
                Ders ders = new Ders(dersNolar, Integer.parseInt(String.valueOf(dersKredi.getText())),
                        eskiHarf.getSelectedItem().toString(), String.valueOf(dersAdi.getText()) , yeniHarf.getSelectedItem().toString());
                adapter.dersEkle(ders);
                dersNolar++;
                Toast.makeText(MainActivity.getAppContext(), getString(R.string.ders_eklendi), Toast.LENGTH_SHORT).show();
                info.setText(MainActivity.getBuDonem(false).getAgnoInfo());
            }
        });
    }
}
