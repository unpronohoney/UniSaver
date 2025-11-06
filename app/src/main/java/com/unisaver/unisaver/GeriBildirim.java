package com.unisaver.unisaver;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class GeriBildirim {
    private final String gonderenMail = "SENDER_MAIL_ADDRESS";  // Kendi Gmail adresini yaz
    private final String sifre = "SENDER_PASS_CODE";  // Gmail uygulama şifreni buraya yaz
    private final String aliciMail = "RECEIVER_MAIL_ADDRESS";  // E-posta göndereceğin kişi
    private String message;

    public GeriBildirim(String konu, String mesaj, File dosyaYolu) {
        // SMTP sunucu ayarları (Gmail için)
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        // Kullanıcı adı ve şifre ile oturum açma
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(gonderenMail, sifre);  // Uygulama şifreni kullan
            }
        });

        try {
            // E-posta mesajı oluşturma
            Message emailMessage = new MimeMessage(session);
            emailMessage.setFrom(new InternetAddress(gonderenMail));  // Gönderen adresi
            emailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(aliciMail));  // Alıcı adresi
            emailMessage.setSubject(konu);  // Konu
            emailMessage.setText(mesaj);  // Mesaj içeriği

            // MimeMultipart nesnesi oluştur
            MimeMultipart multipart = new MimeMultipart();

            // E-posta içeriği
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(mesaj);
            multipart.addBodyPart(textPart);
            if (dosyaYolu != null) {
                MimeBodyPart pdfPart = new MimeBodyPart();
                try {
                    pdfPart.attachFile(dosyaYolu);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                multipart.addBodyPart(pdfPart);
            }


            // E-posta mesajına ekleri ekle
            emailMessage.setContent(multipart);
            // E-posta gönderme
            Transport.send(emailMessage);
            this.message = MainActivity.getAppContext().getString(R.string.feedback_info);

        } catch (MessagingException e) {
            this.message = MainActivity.getAppContext().getString(R.string.feedback_error);
        }
    }
    public String getMessage() {
        return message;
    }
}


