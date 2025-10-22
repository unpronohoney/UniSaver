package com.unisaver.unisaver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

public class KombinasyonluHesap extends AppCompatActivity {

    private AdView banner = null;
    private IhtimallerDizisi tutucu2 = null;
    private GenelNot tutucu1 = null;
    ArrayAdapter<String> adapter4 = null;
    private ArrayList<Ders> dersler = new ArrayList<>();
    private int dersSayac = 0;
    private int dersKrediTop = 0;
    private double ilkAgno = -1;
    private int ilkKredi = -1;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase, LocaleHelper.getLanguage(newBase)));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.kombinasyonlu);

        MobileAds.initialize(this, initializationStatus -> {
        });


        // XML'deki AdView'i bul
        banner = findViewById(R.id.banner);

        // Reklam isteği oluştur ve yükle
        AdRequest adRequest = new AdRequest.Builder().build();
        banner.loadAd(adRequest);


        String[] harfs = Ders.getSortedLetters();
        String[] harfler = new String[harfs.length + 1];
        String[] minHarfler = new String[harfs.length];
        String[] maxHarfler = new String[harfs.length];

        harfler[0] = getString(R.string.yok);
        int i = 0;
        for (String harf : harfs) {
            harfler[i + 1] = harf;
            minHarfler[harfs.length - i - 1] = harf;
            maxHarfler[i] = harf;
            i++;
        }

        minHarfler[0] = getString(R.string.unlimited);
        maxHarfler[0] = getString(R.string.unlimited);

        Button dersGirisineGec = findViewById(R.id.gec1);
        Button yenile = findViewById(R.id.yenile1);
        Button dersEkle = findViewById(R.id.addDers1);
        Button dersBitti = findViewById(R.id.finishAdding);
        Button ksh = findViewById(R.id.ksh);
        Button komGor = findViewById(R.id.olasGoruntule);
        ImageButton mainMenu = findViewById(R.id.anaSayfaDon1);

        EditText agno = findViewById(R.id.agno1);
        EditText kredi = findViewById(R.id.kredis1);
        EditText dersKredi = findViewById(R.id.dersKredi1);
        EditText minAgno = findViewById(R.id.minAgno);
        EditText maxAgno = findViewById(R.id.maxAgno);
        EditText komSayi = findViewById(R.id.olasilikSayisi);
        EditText dersAdi = findViewById(R.id.dersAdi);

        TextView komBilgi = findViewById(R.id.olasBilgi);
        TextView dersSayaci = findViewById(R.id.dersSayaci);
        TextView derseGecis = findViewById(R.id.derseGecis);

        dersSayaci.setText(getString(R.string.derssayar, dersSayac));

        Spinner dersHarf = findViewById(R.id.harfNotu1);
        Spinner minHarf = findViewById(R.id.minHarf);
        Spinner maxHarf = findViewById(R.id.maxHarf);
        Spinner komlar = findViewById(R.id.olasiliklar);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(MainActivity.getAppContext(), R.layout.spinner_inside, harfler);
        adapter1.setDropDownViewResource(R.layout.spinner_layout_list);
        dersHarf.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(MainActivity.getAppContext(), R.layout.spinner_inside, minHarfler);
        adapter2.setDropDownViewResource(R.layout.spinner_layout_list);
        minHarf.setAdapter(adapter2);

        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(MainActivity.getAppContext(), R.layout.spinner_inside, maxHarfler);
        adapter3.setDropDownViewResource(R.layout.spinner_layout_list);
        maxHarf.setAdapter(adapter3);

        LinearLayout donemKismi = findViewById(R.id.donemKismi);
        LinearLayout dersKismi = findViewById(R.id.dersKismi);
        LinearLayout kisitlar = findViewById(R.id.kisitlar);
        LinearLayout olasiliklar = findViewById(R.id.olasilikKismi);

        dersKismi.setVisibility(View.GONE);
        kisitlar.setVisibility(View.GONE);
        olasiliklar.setVisibility(View.GONE);

        ImageButton share = findViewById(R.id.shareButton);

        TextView oncekiDonem = findViewById(R.id.oncekiBilgi);

        oncekiDonem.setVisibility(View.GONE);

        share.setOnClickListener(view -> {
            String send = getString(R.string.ilk_bilgi);
            send += getString(R.string.agno_cred, ilkAgno, ilkKredi);
            send += "\n";
            send += komBilgi.getText().toString();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, send);
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, getString(R.string.choose_app));
            startActivity(shareIntent);
        });

        mainMenu.setOnClickListener(view -> {
            ilkAgno = -1;
            ilkKredi = -1;
            komlar.setAdapter(null);
            dersSayac = 0;
            dersKrediTop = 0;
            tutucu1 = null;
            tutucu2 = null;
            dersler.clear();
            MainActivity.resetBuDonem(true);
            adapter4 = null;
            finish();
        });

        yenile.setOnClickListener(v -> {
            ilkAgno = -1;
            ilkKredi = -1;
            oncekiDonem.setVisibility(View.GONE);
            donemKismi.setVisibility(View.VISIBLE);
            dersKismi.setVisibility(View.GONE);
            kisitlar.setVisibility(View.GONE);
            olasiliklar.setVisibility(View.GONE);
            derseGecis.setText(getString(R.string.dersBekle));
            dersAdi.setText("");
            komlar.setAdapter(null);
            dersSayac = 0;
            dersKrediTop = 0;
            dersSayaci.setText(getString(R.string.derssayar, dersSayac));
            agno.setText("");
            kredi.setText("");
            dersKredi.setText("");
            minAgno.setText("");
            maxAgno.setText("");
            komSayi.setText("");
            komBilgi.setText("");
            tutucu1 = null;
            tutucu2 = null;
            dersler.clear();
            adapter4 = null;
            MainActivity.resetBuDonem(true);
        });

        OnBackPressedDispatcher onBackPressedDispatcher = new OnBackPressedDispatcher(() -> {
            ilkAgno = -1;
            ilkKredi = -1;
            komlar.setAdapter(null);
            dersSayac = 0;
            dersKrediTop = 0;
            tutucu1 = null;
            tutucu2 = null;
            dersler.clear();
            MainActivity.resetBuDonem(true);
            adapter4 = null;
            finish();
        });

        dersGirisineGec.setOnClickListener(v -> {
            if (agno.getText().length() == 0 || kredi.getText().length() == 0) {
                Toast.makeText(MainActivity.getAppContext(), getString(R.string.bos_alan), Toast.LENGTH_SHORT).show();
            } else if (Integer.parseInt(String.valueOf(kredi.getText())) <= 0) {
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
                        tutucu1 = new GenelNot(ilkKredi, ilkAgno);
                        Toast.makeText(MainActivity.getAppContext(), getString(R.string.ders_gir_info), Toast.LENGTH_SHORT).show();
                        derseGecis.setText(getString(R.string.dersEkleyebilirsin));
                        donemKismi.setVisibility(View.GONE);
                        dersKismi.setVisibility(View.VISIBLE);
                        oncekiDonem.setText(getString(R.string.agno_cred_info, ilkAgno, ilkKredi));
                        oncekiDonem.setVisibility(View.VISIBLE);
                    }
            }
        });

        dersEkle.setOnClickListener(v -> {
            if (tutucu1 == null) {
                Toast.makeText(MainActivity.getAppContext(), getString(R.string.first_basis), Toast.LENGTH_SHORT).show();
            } else if (dersKredi.getText().length() == 0 || Integer.parseInt(String.valueOf(dersKredi.getText())) <= 0) {
                Toast.makeText(MainActivity.getAppContext(), getString(R.string.ders_err), Toast.LENGTH_SHORT).show();
            } else {
                if (!dersHarf.getSelectedItem().toString().equals(getString(R.string.yok))) {
                    dersKrediTop += Integer.parseInt(String.valueOf(dersKredi.getText()));
                }
                boolean hata = dersKrediTop > tutucu1.getKrediSayisi();
                if (!hata) {
                    if (dersAdi.getText().length() > 0) {
                        dersler.add(new Ders(Integer.parseInt(String.valueOf(dersKredi.getText())),
                                dersHarf.getSelectedItem().toString(), String.valueOf(dersAdi.getText())));
                    } else {
                        dersler.add(new Ders(Integer.parseInt(String.valueOf(dersKredi.getText())), dersHarf.getSelectedItem().toString()));
                    }
                    dersSayac++;
                    dersSayaci.setText(getString(R.string.derssayar, dersSayac));
                } else {
                    Toast.makeText(MainActivity.getAppContext(), getString(R.string.enter_ders_error), Toast.LENGTH_LONG).show();
                }
            }
        });

        dersBitti.setOnClickListener(v -> {
            if (dersler.isEmpty()) {
                Toast.makeText(MainActivity.getAppContext(), getString(R.string.any_ders), Toast.LENGTH_SHORT).show();
            } else {
                dersSayac = 0;
                dersSayaci.setText(getString(R.string.tekrarDers));
                MainActivity.setBuDonem(true, new ThisTerm(tutucu1, true));
                MainActivity.getBuDonem(true).dersGirisiArr(dersler);
                MainActivity.getBuDonem(true).yuvarlamaPaylari();
                MainActivity.getBuDonem(true).ihtimallerDizisi();
                dersler.clear();
                dersKrediTop = 0;
                dersKismi.setVisibility(View.GONE);
                kisitlar.setVisibility(View.VISIBLE);
            }
        });

        ksh.setOnClickListener(v -> {
            if (MainActivity.getBuDonem(true) == null) {
                Toast.makeText(MainActivity.getAppContext(), getString(R.string.term_info_err), Toast.LENGTH_SHORT).show();
            } else if (minAgno.getText().length() == 0 || komSayi.getText().length() == 0) {
                Toast.makeText(MainActivity.getAppContext(), getString(R.string.bos_alan), Toast.LENGTH_SHORT).show();
            } else {
                String agnoInput = minAgno.getText().toString().trim();
                agnoInput = agnoInput.replace(",", ".");
                double minGno = Double.parseDouble(agnoInput);
                if (minHarf.getSelectedItemPosition() < maxHarf.getSelectedItemPosition()) {
                    Toast.makeText(MainActivity.getAppContext(), getString(R.string.min_cant_max), Toast.LENGTH_SHORT).show();
                } else if (minGno > Ders.getPoint(Ders.getMaxHarf()) || minGno < Ders.getPoint(Ders.getMinHarf())) {
                    Toast.makeText(MainActivity.getAppContext(), getString(R.string.gpa_err), Toast.LENGTH_LONG).show();
                } else {
                    double maxGno = -1;
                    boolean deneme = true;
                    if (maxAgno.getText().length() != 0) {
                        agnoInput = maxAgno.getText().toString().trim();
                        agnoInput = agnoInput.replace(",", ".");
                        maxGno = Double.parseDouble(agnoInput);
                        if (maxGno > Ders.getPoint(Ders.getMaxHarf()) || maxGno < Ders.getPoint(Ders.getMinHarf())) {
                            Toast.makeText(MainActivity.getAppContext(), getString(R.string.gpa_err), Toast.LENGTH_LONG).show();
                            deneme = false;
                        } else if (maxGno < minGno) {
                            Toast.makeText(MainActivity.getAppContext(), getString(R.string.min_agno_max), Toast.LENGTH_SHORT).show();
                            deneme = false;
                        }
                    }
                    if (deneme) {
                        int kombinasyonlar = Integer.parseInt(String.valueOf(komSayi.getText()));
                        boolean oldumu = MainActivity.getBuDonem(true).newKsh(minGno, maxGno,
                                (String) minHarf.getSelectedItem(), (String) maxHarf.getSelectedItem(), kombinasyonlar);
                        tutucu2 = MainActivity.getBuDonem(true).getPossibilities();
                        if (oldumu) {
                            tutucu2.listByAgno();
                            Toast.makeText(MainActivity.getAppContext(), getString(R.string.koms_info), Toast.LENGTH_SHORT).show();
                            adapter4 = new ArrayAdapter<>(MainActivity.getAppContext(), R.layout.spinner_inside, olasilik());
                            adapter4.setDropDownViewResource(R.layout.spinner_layout_list);
                            komlar.setAdapter(adapter4);
                            kisitlar.setVisibility(View.GONE);
                            olasiliklar.setVisibility(View.VISIBLE);
                        } else {
                            if (tutucu2.getCounter() == 0) {
                                Toast.makeText(MainActivity.getAppContext(), R.string.koms_err, Toast.LENGTH_SHORT).show();
                            } else {
                                tutucu2.listByAgno();
                                Toast.makeText(MainActivity.getAppContext(), getString(R.string.koms_num, tutucu2.getCounter()), Toast.LENGTH_SHORT).show();
                                adapter4 = new ArrayAdapter<>(MainActivity.getAppContext(), R.layout.spinner_inside, olasilik());
                                adapter4.setDropDownViewResource(R.layout.spinner_layout_list);
                                komlar.setAdapter(adapter4);
                                kisitlar.setVisibility(View.GONE);
                                olasiliklar.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }

            }
        });

        komGor.setOnClickListener(v -> {
            if (tutucu2 != null) {
                if (tutucu2.getCounter() > 0) {
                    komBilgi.setText(MainActivity.getBuDonem(true).getPossibilities().toStringOnePossibility(komlar.getSelectedItemPosition() + 1));
                }
            }
        });

    }

    public String[] olasilik() {
        String[] olasilikGor = new String[tutucu2.getEskiAgnolar().size()];
        int j = 0;
        for (Integer i : tutucu2.getEskiAgnolar().keySet()) {
            olasilikGor[j] = getString(R.string.spinPoss, i, tutucu2.getAgno(i));
            j++;
        }
        return olasilikGor;
    }
}
