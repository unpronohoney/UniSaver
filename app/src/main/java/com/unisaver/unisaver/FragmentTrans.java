package com.unisaver.unisaver;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;
import java.io.InputStream;


public class FragmentTrans extends AppCompatActivity {

    //ca-app-pub-3940256099942544/1033173712        deneme tam ekran reklam
    //ca-app-pub-7577324739927592/7859309101        benimki gecis
    private static final String AD_UNIT_ID_2 = "ca-app-pub-7577324739927592/7859309101";
    private AdView banner = null;
    private InterstitialAd mInterstitialAd;
    private ActivityResultLauncher<Intent> pdfPickerLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.hesaplama_activity_3);

        Button transSecici = findViewById(R.id.transBelgesi);
        PDFBoxResourceLoader.init(this);

        ImageButton anaMenu = findViewById(R.id.anaSayfaDon3);

        loadInterstitialAd();

        banner = findViewById(R.id.banner);

        // Reklam isteği oluştur ve yükle
        AdRequest adRequest = new AdRequest.Builder().build();
        banner.loadAd(adRequest);

        anaMenu.setOnClickListener(view -> finish());

        pdfPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        try {
                            if (uri != null) {
                                InputStream inputStream = this.getContentResolver().openInputStream(uri);
                                if (inputStream != null) {
                                    PDDocument document = PDDocument.load(inputStream);
                                    PDFTextStripper stripper = new PDFTextStripper();
                                    String text = stripper.getText(document);
                                    document.close();
                                    inputStream.close();
                                    if (control(text)) {
                                        Toast.makeText(this, R.string.belge_error, Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(this, R.string.belge_info, Toast.LENGTH_SHORT).show();
                                        if (mInterstitialAd != null) {
                                            new Handler(Looper.getMainLooper()).post(() -> {
                                                mInterstitialAd.show(this); // Geçiş reklamını göster
                                                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                                    @Override
                                                    public void onAdDismissedFullScreenContent() {
                                                        super.onAdDismissedFullScreenContent();
                                                        // Reklam kapatıldığında yeni Activity'e geçiş
                                                        Intent intent = new Intent(FragmentTrans.this, TranskriptTable.class);
                                                        intent.putExtra("text", text);
                                                        startActivity(intent);
                                                    }

                                                    @Override
                                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                                        super.onAdFailedToShowFullScreenContent(adError);
                                                        // Eğer reklam gösterilemezse, hemen yeni Activity açılır
                                                        Intent intent = new Intent(FragmentTrans.this, TranskriptTable.class);
                                                        intent.putExtra("text", text);
                                                        startActivity(intent);
                                                    }
                                                });
                                            });
                                        } else {
                                            // Eğer reklam yüklenmemişse, doğrudan geçiş yap
                                            Intent intent = new Intent(FragmentTrans.this, TranskriptTable.class);
                                            intent.putExtra("text", text);
                                            startActivity(intent);
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(this, R.string.belge_error, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("PDF_Text", "text");
                        }

                    }
                }
        );

        transSecici.setOnClickListener(v -> openFilePicker());

        Button degerlendir = findViewById(R.id.degerlendirme);

        degerlendir.setOnClickListener(v -> {
            ReviewManager manager = ReviewManagerFactory.create(this);
            Task<ReviewInfo> request = manager.requestReviewFlow();
            request.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ReviewInfo reviewInfo = task.getResult();
                    manager.launchReviewFlow(this, reviewInfo);
                } else {
                    // Eğer başarısız olursa burada fallback yapabilirsin (örn. Play Store'a yönlendirme)
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        });


    }
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf"); // Sadece PDF dosyalarını gösterir
        pdfPickerLauncher.launch(intent);
    }
    public boolean control(String text) {
        String cont = "yükleyeceğiniz e-Devlet Kapısına ait Barkodlu Belge Doğrulama veya YÖK Mobil uygulaması vasıtası ile yandaki karekod";
        return !text.contains(cont);
    }

    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, AD_UNIT_ID_2, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(InterstitialAd ad) {
                mInterstitialAd = ad;
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                mInterstitialAd = null;
            }
        });
    }
}
