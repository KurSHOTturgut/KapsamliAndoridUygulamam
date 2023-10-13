package com.GLocanicKT.locanic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class PasswordReset extends AppCompatActivity {

    EditText kullaniciAdi;
    Button sifreSifirlaButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        kullaniciAdi = findViewById(R.id.kullaniciAdiEditTextreset);
        sifreSifirlaButton = findViewById(R.id.sifresifirlabutton);

        sifreSifirlaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = kullaniciAdi.getText().toString();

                if (username.isEmpty()) {
                    Toast.makeText(PasswordReset.this, "Kullanıcı adını giriniz.", Toast.LENGTH_SHORT).show();
                } else {
                    // API isteği oluşturma
                    String apiUrl = "https://laconicmedya.com/mobile/api/laconic-medya-app/platform/password-reset.php";
                    String requestUrl = apiUrl + "?username=" + username;

                    JsonObjectRequest request = new JsonObjectRequest(requestUrl, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        int status = response.getInt("status");

                                        if (status == 1) {
                                            // Kullanıcıya şifre sıfırlama eposta / sms gönderildi
                                            Toast.makeText(PasswordReset.this, "Kullanıcıya şifre sıfırlama eposta  gönderildi.", Toast.LENGTH_SHORT).show();

                                            // Login sayfasına yönlendirme
                                            Intent intent = new Intent(PasswordReset.this, Login.class);
                                            startActivity(intent);
                                            finish();
                                        } else if (status == 2) {
                                            // Bu kullanıcı adı sistemde bulunmuyor
                                            Toast.makeText(PasswordReset.this, "Bu kullanıcı adı sistemde bulunmuyor. Lütfen kontrol edin ve tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
                                        } else if (status == 3) {
                                            // Şifre sıfırlama işlemi için kullanıcı adınızı yazmanız gerekiyor
                                            Toast.makeText(PasswordReset.this, "Şifre sıfırlama işlemi için kullanıcı adınızı yazmanız gerekiyor.", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // API isteği sırasında hata oluştu
                                    Toast.makeText(PasswordReset.this, "Şifre sıfırlama işlemi başarısız.", Toast.LENGTH_SHORT).show();
                                }
                            });

                    // API isteğini kuyruğa ekleme
                    Volley.newRequestQueue(PasswordReset.this).add(request);
                }
            }
        });
    }
}
