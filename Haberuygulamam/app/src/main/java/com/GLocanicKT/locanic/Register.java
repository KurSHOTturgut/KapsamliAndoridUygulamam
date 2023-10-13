package com.GLocanicKT.locanic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class Register extends AppCompatActivity {

    TextView kayitli;
    EditText adEditText, soyadEditText, kullaniciAdiEditText, ePostaEditText, sifreEditText, telefono;
    Button kayitOlButton;

    private static final String API_URL = "https://laconicmedya.com/mobile/api/laconic-medya-app/platform/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        kayitli = findViewById(R.id.kayitli);
        adEditText = findViewById(R.id.adEditText);
        soyadEditText = findViewById(R.id.soyadEditText);
        kullaniciAdiEditText = findViewById(R.id.kullaniciAdiEditText);
        ePostaEditText = findViewById(R.id.ePostaEditText);
        sifreEditText = findViewById(R.id.sifreEditText);
        telefono = findViewById(R.id.telefonnumarasıEditText);
        kayitOlButton = findViewById(R.id.Kayitol);

        kayitli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });

        kayitOlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ad = adEditText.getText().toString().trim();
                String soyad = soyadEditText.getText().toString().trim();
                String kullaniciAdi = kullaniciAdiEditText.getText().toString().trim();
                String eposta = ePostaEditText.getText().toString().trim();
                String sifre = sifreEditText.getText().toString().trim();
                String telefon = telefono.getText().toString().trim();

                if (ad.isEmpty() || soyad.isEmpty() || kullaniciAdi.isEmpty() || eposta.isEmpty() || sifre.isEmpty() || telefon.isEmpty()) {
                    Toast.makeText(Register.this, "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show();
                } else {
                    String firebaseToken = generateFirebaseToken();
                    registerUser(ad, soyad, kullaniciAdi, eposta, sifre, telefon, firebaseToken);
                }
            }
        });
    }

    private String generateFirebaseToken() {
        // Firebase token üretme işlemini burada gerçekleştirin
        String token = UUID.randomUUID().toString();

        // Token'i SharedPreferences ile kaydedin
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("firebaseToken", token);
        editor.apply();

        return token;
    }

    private void registerUser(String ad, String soyad, String kullaniciAdi, String eposta, String sifre, String telefon, String firebaseToken) {
        String postData = "first_name=" + ad +
                "&last_name=" + soyad +
                "&username=" + kullaniciAdi +
                "&email=" + eposta +
                "&password=" + sifre +
                "&user_phone=" + telefon +
                "&user_firebase_token=" + firebaseToken;

        new RegisterUserTask().execute(API_URL, postData);
    }

    private class RegisterUserTask extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... params) {
            String apiUrl = params[0];
            String postData = params[1];
            int responseCode = 0;

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(postData.getBytes());
                outputStream.flush();
                outputStream.close();

                responseCode = conn.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return responseCode;
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            super.onPostExecute(responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                Toast.makeText(Register.this, "Kayıt işlemi başarılı.", Toast.LENGTH_SHORT).show();
                // Kayıt işlemi başarılı olduğunda yapılacak işlemleri burada gerçekleştirin
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish(); // Kayıt ekranını kapatmak için
            } else {
                Toast.makeText(Register.this, "Kayıt işlemi başarısız oldu.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
