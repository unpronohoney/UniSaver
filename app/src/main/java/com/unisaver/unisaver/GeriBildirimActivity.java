package com.unisaver.unisaver;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GeriBildirimActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> pdfForGB;
    private File pdfFile = null;
    private AdView banner = null;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase, LocaleHelper.getLanguage(newBase)));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.geri_bildirim);

        banner = findViewById(R.id.banner);

        PDFBoxResourceLoader.init(this);

        // Reklam isteği oluştur ve yükle
        AdRequest adRequest = new AdRequest.Builder().build();
        banner.loadAd(adRequest);

        EditText gbKonu = findViewById(R.id.gbKonu);
        EditText gbAcik = findViewById(R.id.gbAciklama);
        TextView gbDos = findViewById(R.id.dosyaAdi);
        Button gbTrans = findViewById(R.id.gbTrans);
        Button gbGonder = findViewById(R.id.gbGonder);

        ImageButton anaMenu = findViewById(R.id.anaSayfaDon3);

        anaMenu.setOnClickListener(view -> finish());

        pdfForGB = registerForActivityResult(
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
                                        pdfFile = getFileFromUri(uri);
                                        String str = getFileName(uri);
                                        gbDos.setText(str);
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

        gbTrans.setOnClickListener(v -> openFileForGB());
        gbGonder.setOnClickListener(v -> {
            if (gbKonu.getText().length() != 0 && gbAcik.getText().length() != 0 && !gbGonder.getText().equals(getString(R.string.sending))) {
                gbGonder.setText(R.string.sending);
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    GeriBildirim geriBildirim = new GeriBildirim(gbKonu.getText().toString(), gbAcik.getText().toString(), pdfFile);
                    // UI thread'e geri dön ve mesaj göster
                    new Handler(Looper.getMainLooper()).post(() -> {
                        pdfFile = null;
                        gbDos.setText("");
                        gbGonder.setText(R.string.send);
                        Toast.makeText(this, geriBildirim.getMessage(), Toast.LENGTH_LONG).show();
                    });
                });
            }
        });


    }

    private File getFileFromUri(Uri uri) {
        File file = null;
        try {
            // getContext().getCacheDir() kullan çünkü Fragment içindesin
            file = File.createTempFile("temp_pdf", ".pdf", this.getCacheDir());

            // Dosya içeriğini kopyala
            InputStream inputStream = this.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int bytesRead;
            if (inputStream != null) {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
                outputStream.close();
            } else {
                outputStream.close();
                Toast.makeText(this, getString(R.string.belge_error), Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, getString(R.string.belge_error), Toast.LENGTH_SHORT).show();
        }
        return file;
    }

    private String getFileName(Uri uri) {
        String fileName = null;
        Cursor cursor = null;

        try {
            // ContentResolver ile dosyanın meta verilerini alın
            ContentResolver contentResolver = this.getContentResolver();
            cursor = contentResolver.query(uri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex); // Dosya adını alın
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.belge_error), Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close(); // Cursor'u kapatmayı unutmayın
            }
        }
        return fileName;
    }

    private void openFileForGB() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf"); // Sadece PDF dosyalarını gösterir
        pdfForGB.launch(intent);
    }
    public boolean control(String text) {
        String cont = "yükleyeceğiniz e-Devlet Kapısına ait Barkodlu Belge Doğrulama veya YÖK Mobil uygulaması vasıtası ile yandaki karekod";
        return !text.contains(cont);
    }
}
