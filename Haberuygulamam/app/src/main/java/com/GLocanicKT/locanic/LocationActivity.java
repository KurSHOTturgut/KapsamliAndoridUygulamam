package com.GLocanicKT.locanic;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.security.auth.callback.Callback;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LocationActivity extends AppCompatActivity implements LocationListener {

    // User Token
    String userToken;

    // Harita itemleri
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap googleMap;

    Button konumBul;
    ImageView konumIsaretiresim;
    MapView haritagelecek;
    EditText Enlem, Boylam;

    // Bottom menü itemleri
    ImageView home, location, kutu, camera, message, profile;

    String HaritaEnlem, HaritaBoylam, CihazBilgisi;

    // Konum güncellemelerini dinlemek için LocationManager
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location2);

        // Kullanıcı adını almak için intent'i kontrol edin
        Intent intent = getIntent();
        userToken = intent.getStringExtra("username");
        Log.d(TAG, "onCreate: tokenim"+userToken);

        // Harita itemlerini XML ile bağlantıları
        konumBul = findViewById(R.id.KonumBulButton);
        Enlem = findViewById(R.id.enlemedittext);
        Boylam = findViewById(R.id.boylamedittext);
        konumIsaretiresim = findViewById(R.id.konumisaretiresim);
        haritagelecek = findViewById(R.id.haritagelecek);
        haritagelecek.onCreate(savedInstanceState);

        // Bottom menü XML ile bağlantıları
        home = findViewById(R.id.locationhome);
        location = findViewById(R.id.locationlocation);
        kutu = findViewById(R.id.locationkutu);
        camera = findViewById(R.id.locationcamera);
        message = findViewById(R.id.locationmessage);
        profile = findViewById(R.id.locationproife);



        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        haritagelecek.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;
                showLocationOnMap("Laconic Medya, Via Flat İş ve Yaşam Merkezi Nergis Sokak No:7/2 Kat:3 Daire:87, Söğütözü Cd., 06560 Yenimahalle, Ankara");
            }
        });

        konumBul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (googleMap != null) {
                    // Konum izni kontrolü
                    if (ContextCompat.checkSelfPermission(LocationActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        // Konum bilgisini alma işlemi
                        getLastKnownLocation();
                        // Konumu sunucuya kaydet
                    } else {
                        // Konum izni isteği
                        ActivityCompat.requestPermissions(LocationActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }
                }
            }
        });

        konumIsaretiresim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // LocationManager başlatma
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    private void showLocationOnMap(String address) {
        Geocoder geocoder = new Geocoder(LocationActivity.this);
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && addresses.size() > 0) {
                Address location = addresses.get(0);
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng latLng = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions().position(latLng).title(address));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15)); // Yakınlaştırma düzeyini ayarlayabilirsiniz
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getLastKnownLocation() {
        if (ContextCompat.checkSelfPermission(LocationActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(LocationActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        LatLng latLng = new LatLng(latitude, longitude);
                        googleMap.clear();
                        googleMap.addMarker(new MarkerOptions().position(latLng).title("Konumunuz"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                        // Enlem ve boylam değerlerini güncelle
                        HaritaEnlem = String.valueOf(latitude);
                        HaritaBoylam = String.valueOf(longitude);

                        // Cihaz bilgisini al
                        CihazBilgisi = android.os.Build.MODEL;

                        // Güncellenmiş bilgileri kullanabilirsiniz
                        Toast.makeText(LocationActivity.this, "Enlem: " + HaritaEnlem + ", Boylam: " + HaritaBoylam + ", Cihaz Bilgisi: " + CihazBilgisi, Toast.LENGTH_SHORT).show();

                        // Konumu sunucuya gönder
                        sendLocationToServer(latitude,longitude);
                        //enlem boylam cihaz bilgisinin log kayıdı
                        Log.d(TAG, "onCreate: Enlem : "+HaritaEnlem);
                        Log.d(TAG, "onCreate: Boylam : "+HaritaBoylam);
                        Log.d(TAG, "onCreate: Cihaz Biglsi : " + CihazBilgisi);


                        // Konumu sunucuya gönder


                    }

                }
            });
        } else {
            ActivityCompat.requestPermissions(LocationActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }


    private void sendLocationToServer(double latitude, double longitude) {
        // Cihaz bilgilerini al
        String deviceInformation = CihazBilgisi;

        // İstek gövdesini oluştur
        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("token", userToken);
            requestObject.put("process_type", "save");
            requestObject.put("user_location_latitude", latitude);
            requestObject.put("user_location_longitude", longitude);
            requestObject.put("user_device_information", deviceInformation);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // İstek gönder
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://laconicmedya.com/mobile/api/laconic-medya-app/location/location.php",
                response -> {
                    try {
                        JSONObject responseObject = new JSONObject(response);
                        int status = responseObject.getInt("status");
                        if (status == 1) {
                            // Kayıt başarıyla yapıldı
                            Log.d(TAG, "Konum kaydedildi.");
                            showToastMessage("Konum kaydedildi.");
                        } else {
                            // Kayıt işlemi başarısız oldu
                            Log.d(TAG, "Konum kaydetme başarısız.");
                            showToastMessage("Konum kaydetme başarısız.");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Hata durumunda işlemler
                    Log.e(TAG, "Hata: " + error.toString());
                    showToastMessage("İstek zaman aşımına uğradı.");
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", userToken);
                params.put("process_type", "save");
                params.put("user_location_latitude", String.valueOf(latitude));
                params.put("user_location_longitude", String.valueOf(longitude));
                params.put("user_device_information", deviceInformation);
                return params;
            }

            @Override
            public RetryPolicy getRetryPolicy() {
                // Süre aşımı süresini 10 saniye olarak ayarla
                int timeout = 10000;
                return new DefaultRetryPolicy(timeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            }
        };

        // İstek kuyruğuna ekle
        Volley.newRequestQueue(getApplicationContext()).add(stringRequest);
    }

    private void showToastMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Konum izni verildi
                getLastKnownLocation();
            } else {
                // Konum izni reddedildi
                Toast.makeText(this, "Konum izni reddedildi.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void locationhome(View view) {
        Intent intent = new Intent(LocationActivity.this, MainActivity.class);
        intent.putExtra("username", userToken);
        startActivity(intent);
    }

    public void locationlocation(View view) {
        Intent intent = new Intent(LocationActivity.this, LocationActivity.class);
        intent.putExtra("username", userToken);
        startActivity(intent);
    }

    public void locationkutu(View view) {
        Intent intent = new Intent(LocationActivity.this, Kutu.class);
        intent.putExtra("username", userToken);
        startActivity(intent);
    }

    public void locationcamera(View view) {
        Intent intent = new Intent(LocationActivity.this, Camera.class);
        intent.putExtra("username", userToken);
        startActivity(intent);
    }
    public void locationbildirim(View view){
        Intent intent = new Intent(LocationActivity.this, BildirimGondermeActivity.class);
        intent.putExtra("username", userToken);
        startActivity(intent);
    }
    public void locationmessage(View view) {
        Intent intent = new Intent(LocationActivity.this, Message.class);
        intent.putExtra("username", userToken);
        startActivity(intent);
    }

    public void locationprofie(View view) {
        Intent intent = new Intent(LocationActivity.this, Profile.class);
        intent.putExtra("username", userToken);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        haritagelecek.onResume();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        haritagelecek.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        haritagelecek.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        // Konum değiştiğinde çağrılır
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Konumunuz"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

            // Enlem ve boylam değerlerini güncelle
            HaritaEnlem = String.valueOf(latitude);
            HaritaBoylam = String.valueOf(longitude);

            // Cihaz bilgisini al
            CihazBilgisi = android.os.Build.MODEL;

            // Güncellenmiş bilgileri kullanabilirsiniz
            Toast.makeText(LocationActivity.this, "Enlem: " + HaritaEnlem + ", Boylam: " + HaritaBoylam + ", Cihaz Bilgisi: " + CihazBilgisi, Toast.LENGTH_SHORT).show();
        }
    }





    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Konum sağlayıcının durumu değiştiğinde çağrılır
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Konum sağlayıcı etkinleştirildiğinde çağrılır
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Konum sağlayıcı devre dışı bırakıldığında çağrılır
    }
}
