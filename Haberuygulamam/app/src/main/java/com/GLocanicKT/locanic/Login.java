package com.GLocanicKT.locanic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

public class Login extends AppCompatActivity {

    EditText kullaniciAdiEditText, sifreEditText;
    Button girisButton;
    String gelenkullaniciadi;

    private static final String LOGIN_API_URL = "https://laconicmedya.com/mobile/api/laconic-medya-app/platform/login.php";
    private static final String REGISTER_API_URL = "https://laconicmedya.com/mobile/api/laconic-medya-app/platform/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        kullaniciAdiEditText = findViewById(R.id.loginkullaniciAdiEditTextgiris);
        sifreEditText = findViewById(R.id.loginsifreEditTextgiris);
        girisButton = findViewById(R.id.Girisyap);

        girisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String kullaniciAdi = kullaniciAdiEditText.getText().toString().trim();
                String sifre = sifreEditText.getText().toString().trim();
                gelenkullaniciadi=kullaniciAdiEditText.getText().toString();
                loginUser(kullaniciAdi, sifre);
            }
        });
    }

    private void loginUser(String kullaniciAdi, String sifre) {
        if (TextUtils.isEmpty(kullaniciAdi) || TextUtils.isEmpty(sifre)) {
            Toast.makeText(Login.this, "Kullanıcı adı ve parola boş olamaz. Lütfen kontrol ediniz", Toast.LENGTH_SHORT).show();
            return;
        }

        new LoginUserTask().execute(kullaniciAdi, sifre);
    }

    private class LoginUserTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String kullaniciAdi = params[0];
            String sifre = params[1];
            String response = "";


            try {
                // Giriş isteği
                URL url = new URL(LOGIN_API_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // Kullanıcı adı ve şifre parametrelerini JSON formatında oluştur
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("username", kullaniciAdi);
                jsonParam.put("password", sifre);

                // JSON verilerini isteğe ekle
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQueryParams(jsonParam));
                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = conn.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                    }
                    bufferedReader.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                JSONObject jsonResponse = new JSONObject(response);

                if (jsonResponse.has("status")) {
                    String status = jsonResponse.getString("status");

                    if (status.equals("1")) {
                        // Başarılı giriş
                        if (jsonResponse.has("user_register_token")) {
                            String userToken = jsonResponse.getString("user_register_token");
                            // Kullanıcı token'ıyla yapmak istediğiniz işlemleri burada gerçekleştirebilirsiniz
                            Toast.makeText(Login.this, "Giriş başarılı", Toast.LENGTH_SHORT).show();
                            // İstenilen işlemleri buraya ekleyebilirsiniz
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            intent.putExtra("username", userToken);
                            startActivity(intent);
                            finish(); // Bu aktiviteyi kapatın, kullanıcı geri düğmesine basarsa tekrar buraya gelmez.
                        }
                    } else if (status.equals("2")) {
                        // Kullanıcı adı hatası
                        Toast.makeText(Login.this, "Kullanıcı adı hatası", Toast.LENGTH_SHORT).show();
                    } else if (status.equals("3")) {
                        // Parola hatası
                        Toast.makeText(Login.this, "Parola hatası", Toast.LENGTH_SHORT).show();
                    } else if (status.equals("4")) {
                        // Kullanıcı adı ve parola boş olamaz
                        Toast.makeText(Login.this, "Kullanıcı adı ve parola boş olamaz. Lütfen kontrol ediniz", Toast.LENGTH_SHORT).show();
                    } else {
                        // Diğer durumlar
                        Toast.makeText(Login.this, "Geçersiz API yanıtı", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // API yanıtında "status" değeri yok
                    Toast.makeText(Login.this, "Geçersiz API yanıtı", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }





            private String getQueryParams(JSONObject params) throws Exception {
            StringBuilder result = new StringBuilder();
            boolean first = true;

            Iterator<String> iterator = params.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = params.getString(key);

                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(key).append("=").append(value);
            }

            return result.toString();
        }
    }

    private void registerUser() {
        // Kayıt sayfasına yönlendirme yapabilirsiniz
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
    }


    public void sifremiunuttum(View view){
        //şifremi unuttum sayfasına yönlendiriyor
        Intent intent = new Intent(Login.this,PasswordReset.class);
        startActivity(intent);

    }
}
