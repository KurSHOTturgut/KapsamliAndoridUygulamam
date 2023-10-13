package com.GLocanicKT.locanic;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Profile extends AppCompatActivity {

    EditText adi, soyadi, kullaniciadi, eposta, telno, sifre;
    Button bilgileriGuncelle;
    ImageView home, location, kutu, camera, message, profile;

    // Girilen kullanıcı adı
    String userToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Profile itemlerini XML ile bağlama
        adi = findViewById(R.id.ptofileadEditText);
        soyadi = findViewById(R.id.profilesoyadEditText);
        kullaniciadi = findViewById(R.id.profilekullaniciAdiEditText);
        eposta = findViewById(R.id.profileePostaEditText);
        telno = findViewById(R.id.profiletelnoEditText);
        sifre = findViewById(R.id.profilesifreEditText);
        bilgileriGuncelle = findViewById(R.id.bilgileriguncelle);

        // Kullanıcı adını almak için intent'i kontrol edin
        Intent intent = getIntent();
        userToken = intent.getStringExtra("username");
        Log.d(TAG, "onCreate:token " + userToken);

        home = findViewById(R.id.profilehome);
        location = findViewById(R.id.profilelocation);
        kutu = findViewById(R.id.profilekutu);
        camera = findViewById(R.id.profilecamera);
        message = findViewById(R.id.profilemessage);
        profile = findViewById(R.id.profileproife);

        // Bilgileri güncelle düğmesine tıklama olayı ekleme
        bilgileriGuncelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInfo();
            }
        });

        // Kullanıcı bilgilerini al ve EditText alanlarına doldur
        getUserInformation();
    }

    private void updateUserInfo() {
        // Güncellenecek kullanıcı bilgilerini alın
        String updatedFirstName = adi.getText().toString();
        String updatedLastName = soyadi.getText().toString();
        String updatedNickname = kullaniciadi.getText().toString();
        String updatedEmail = eposta.getText().toString();
        String updatedPhone = telno.getText().toString();

        // API isteği için gerekli URL'yi oluşturun
        String apiUrl = "https://www.laconicmedya.com/mobile/api/laconic-medya-app/platform/user-profile.php";
        String queryUrl = apiUrl + "?token=" + userToken + "&process_type=update";

        // Güncellenen kullanıcı bilgilerini bir JSON nesnesine ekleyin
        JSONObject updatedUserInfo = new JSONObject();
        try {
            updatedUserInfo.put("first_name", updatedFirstName);
            updatedUserInfo.put("last_name", updatedLastName);
            updatedUserInfo.put("user_nickname", updatedNickname);
            updatedUserInfo.put("user_email", updatedEmail);
            updatedUserInfo.put("user_phone", updatedPhone);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // OkHttpClient kullanarak POST isteği yapın
        OkHttpClient client = new OkHttpClient();
        okhttp3.RequestBody body = okhttp3.RequestBody.create(
                okhttp3.MediaType.parse("application/json; charset=utf-8"),
                updatedUserInfo.toString()
        );
        Request request = new Request.Builder()
                .url(queryUrl)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                showToast("API hatası: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();

                    if (responseData != null && !responseData.isEmpty()) {
                        try {
                            // Parse the JSON response
                            JSONObject json = new JSONObject(responseData);
                            int status = json.optInt("status");

                            if (status == 1) {
                                // Show success message
                                showToast("Profil bilgileriniz güncellendi");

                                // Update the UI on the main thread
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Update the EditText fields with the updated data
                                        adi.setText(updatedFirstName);
                                        soyadi.setText(updatedLastName);
                                        kullaniciadi.setText(updatedNickname);
                                        eposta.setText(updatedEmail);
                                        telno.setText(updatedPhone);

                                        kullaniciadi.setEnabled(false);
                                        eposta.setEnabled(false);
                                    }
                                });
                            } else {
                                // Show error message
                                showToast("API hatası: Geçersiz yanıt");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showToast("JSON hatası: " + e.toString());
                        }
                    } else {
                        // Show error message
                        showToast("API hatası: Boş yanıt");
                    }
                } else {
                    // Show error message
                    showToast("API hatası: " + response.message());
                }
            }
        });
    }

    private void getUserInformation() {
        // API'den kullanıcı bilgilerini almak için istek yapabilirsiniz
        // API isteği için gerekli URL'yi ve parametreleri kullanın
        String apiUrl = "https://www.laconicmedya.com/mobile/api/laconic-medya-app/platform/user-profile.php";
        String queryUrl = apiUrl + "?token=" + userToken + "&process_type=show";

        // OkHttpClient kullanarak API isteğini yapma
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(queryUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                showToast("API hatası: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();

                    if (responseData != null && !responseData.isEmpty()) {
                        try {
                            // Parse the JSON response
                            JSONObject json = new JSONObject(responseData);
                            int status = json.optInt("status");

                            if (status == 1) {
                                if (json.has("user_info")) {
                                    JSONObject userInfo = json.getJSONObject("user_info");

                                    // Extract the necessary data from the user_info object
                                    final String updatedFirstName = userInfo.optString("first_name");
                                    final String updatedLastName = userInfo.optString("last_name");
                                    final String updatedNickname = userInfo.optString("user_nickname");
                                    final String updatedEmail = userInfo.optString("user_email");
                                    final String updatedPhone = userInfo.optString("user_phone");

                                    // Update the UI on the main thread
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Update the EditText fields with the updated data
                                            adi.setText(updatedFirstName);
                                            soyadi.setText(updatedLastName);
                                            kullaniciadi.setText(updatedNickname);
                                            eposta.setText(updatedEmail);
                                            telno.setText(updatedPhone);

                                            kullaniciadi.setEnabled(false);
                                            eposta.setEnabled(false);
                                        }
                                    });
                                } else {
                                    // Show error message
                                    showToast("API hatası: Kullanıcı bilgileri bulunamadı");
                                }
                            } else {
                                // Show error message
                                showToast("API hatası: Geçersiz yanıt");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showToast("JSON hatası: " + e.toString());
                        }
                    } else {
                        // Show error message
                        showToast("API hatası: Boş yanıt");
                    }
                } else {
                    // Show error message
                    showToast("API hatası: " + response.message());
                }
            }
        });
    }

    private void showToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Profile.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Diğer aktivitelere geçiş metotları
    public void profilehome(View view) {
        Intent intent = new Intent(Profile.this, MainActivity.class);
        intent.putExtra("username", userToken);
        startActivity(intent);
    }

    public void profilelocation(View view) {
        Intent intent = new Intent(Profile.this, LocationActivity.class);
        intent.putExtra("username", userToken);
        startActivity(intent);
    }

    public void profilekutu(View view) {
        Intent intent = new Intent(Profile.this, Kutu.class);
        intent.putExtra("username", userToken);
        startActivity(intent);
    }

    public void profilecamera(View view) {
        Intent intent = new Intent(Profile.this, Camera.class);
        intent.putExtra("username", userToken);
        startActivity(intent);
    }

    public void profilebildirim(View view){
        Intent intent = new Intent(Profile.this, BildirimGondermeActivity.class);
        intent.putExtra("username", userToken);
        startActivity(intent);
    }

    public void profilemessage(View view) {
        Intent intent = new Intent(Profile.this, Message.class);
        intent.putExtra("username", userToken);
        startActivity(intent);
    }

    public void profileprofile(View view) {
        Intent intent = new Intent(Profile.this, Profile.class);
        intent.putExtra("username", userToken);
        startActivity(intent);
    }

}