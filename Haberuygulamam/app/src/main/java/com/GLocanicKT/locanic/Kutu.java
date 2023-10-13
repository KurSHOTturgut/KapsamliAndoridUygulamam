package com.GLocanicKT.locanic;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Kutu extends AppCompatActivity {
    // API endpoint
    private static final String API_URL = "https://www.laconicmedya.com/mobile/api/laconic-medya-app/files/files.php";

    private final int CHOOSE_PDF_FROM_DEVICE = 1001;
    private static final String TAG = "Kutu";
    private static final int FILE_REQUEST_CODE = 1001;

    TextView path;
    String filePath;

    ListView listView;
    Button dosyayukle;
    ImageView home, location, kutu, camera, message, profile;

    ArrayList<String> dosyaListesi;
    ArrayAdapter<String> adapter;

    // Girilen kullanıcı adı
    String userToken;
    int siraNo = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kutu);

        // Kullanıcı adını almak için intent'i kontrol edin
        Intent intent = getIntent();
        userToken = intent.getStringExtra("username");

        // Dosya yükleme öğeleri
        listView = findViewById(R.id.listview);
        dosyayukle = findViewById(R.id.dosyayukle);
        path = findViewById(R.id.path);

        Log.d(TAG, "onCreate: tokenim: " + userToken);

        dosyaListesi = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.file_path, dosyaListesi);
        listView.setAdapter(adapter);


        // Bottom menu
        home = findViewById(R.id.kutuhome);
        location = findViewById(R.id.kutulocation);
        kutu = findViewById(R.id.kutukutu);
        camera = findViewById(R.id.kutucamera);
        message = findViewById(R.id.kutumessage);
        profile = findViewById(R.id.kutuproife);

        fetchDocumentListFromAPI();

        dosyayukle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, FILE_REQUEST_CODE);

                callChooseFileFromDevice();
            }
        });


    }

    private void callChooseFileFromDevice() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        startActivityForResult(intent, CHOOSE_PDF_FROM_DEVICE);
    }

    private String getFilePathFromUri(Uri uri) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // Dosya veriyle birlikte olduğunda
            String documentId = DocumentsContract.getDocumentId(uri);
            if (isExternalStorageDocument(uri)) {
                String[] split = documentId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    filePath = Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(documentId));
                filePath = getDataColumn(contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                String[] split = documentId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = "_id=?";
                String[] selectionArgs = new String[]{split[1]};
                filePath = getDataColumn(contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Diğer içerik URI'leri
            filePath = getDataColumn(uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // Dosya URI'si
            filePath = uri.getPath();
        }
        return filePath;
    }

    private String getDataColumn(Uri uri, String selection, String[] selectionArgs) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            String filePath = getFilePathFromUri(data.getData());
            if (filePath != null) {
                // Dosya yolu null ise hata ele alın
                uploadFileToAPI(filePath);
                return;
            }
            else {
                Toast.makeText(this, "dosya yolu çözülemedi", Toast.LENGTH_SHORT).show();
            }

            // Dosya yolu geçerli ise upload işlemini gerçekleştirin

        }
    }


    private void uploadFileToAPI(String filePath) {
        if (filePath == null) {
            // Dosya yolu null ise hata ele alın
            Toast.makeText(this, "Dosya seçilmedi", Toast.LENGTH_SHORT).show();
            return;
        }

        // Dosya yolu geçerli ise devam edin
        File file = new File(filePath);

        OkHttpClient client = new OkHttpClient();

        // Dosyayı eklemek için MultipartBody oluşturun
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token", userToken)
                .addFormDataPart("process_type", "upload")
                .addFormDataPart("upload_file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file))
                .addFormDataPart("status", "1")
                .addFormDataPart("location_list", "kayıt edilecek konum bilgisi")  // Buraya kayıt edilecek konum bilgisini ekleyin
                .build();

        // POST isteğini oluşturun
        Request request = new Request.Builder()
                .url("https://www.laconicmedya.com/mobile/api/laconic-medya-app/files/files.php")
                .post(requestBody)
                .build();

        // İsteği gerçekleştirin ve yanıtı işleyin
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                handleUploadFailure("Dosya yükleme başarısız: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // İstek başarılı olduğunda yapılacak işlemler
                    String responseBody = response.body().string();
                    // Yanıtı işleme devam edin
                    handleUploadSuccess(filePath);
                } else {
                    // İstek başarısız olduğunda yapılacak işlemler
                    handleUploadFailure("Dosya yükleme başarısız: " + response.message());
                }
            }
        });
    }
    private void handleUploadSuccess(String response) {
        // Handle the success case here
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Kutu.this, "Dosya başaralı bir şekilde yüklendi :) ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleUploadFailure(String errorMessage) {
        // Handle the failure case here
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Kutu.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void kutuhome(View view) {
        Intent intent = new Intent(Kutu.this, MainActivity.class);
        intent.putExtra("username", userToken);
        startActivity(intent);
    }

    public void kutulocation(View view) {
        Intent intent = new Intent(Kutu.this, LocationActivity.class);
        intent.putExtra("username", userToken);
        startActivity(intent);
    }
    public void kutukutu(View view) {
        // Aynı aktiviteyi tekrar başlatmayı önlemek için bu işlemi yapmanız gerekmektedir.
        // Intent intent = new Intent(Kutu.this, Kutu.class);
        // intent.putExtra("username", userToken);
        // startActivity(intent);
    }

    public void kutucamera(View view) {
        Intent intent = new Intent(Kutu.this, Camera.class);
        intent.putExtra("username", userToken);
        startActivity(intent);
    }
    public void kutubildirim(View view){
        Intent intent = new Intent(Kutu.this, BildirimGondermeActivity.class);
        intent.putExtra("username", userToken);
        startActivity(intent);
    }
    public void kutumessage(View view) {
        Intent intent = new Intent(Kutu.this, Message.class);
        intent.putExtra("username", userToken);
        startActivity(intent);
    }

    public void kutuprofile(View view) {
        Intent intent = new Intent(Kutu.this, Profile.class);
        intent.putExtra("username", userToken);
        startActivity(intent);
    }





    private void fetchDocumentListFromAPI() {
        OkHttpClient client = new OkHttpClient();

        // MultipartBody oluşturarak isteği hazırlayın
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("token", userToken)
                .addFormDataPart("process_type", "list")
                .addFormDataPart("status","1")
                .build();

        // POST isteğini oluşturun
        Request request = new Request.Builder()
                .url("https://www.laconicmedya.com/mobile/api/laconic-medya-app/files/files.php")
                .post(requestBody)
                .build();

        // İsteği gerçekleştirin ve yanıtı işleyin
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                handleDocumentListFailure("Belge listeleme başarısız: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // İstek başarılı olduğunda yapılacak işlemler
                    String responseBody = response.body().string();
                    // Yanıtı işleme devam edin
                    handleDocumentListSuccess(responseBody);
                } else {
                    // İstek başarısız olduğunda yapılacak işlemler
                    handleDocumentListFailure("Belge listeleme başarısız: " + response.message());
                }
            }
        });
    }

    private void handleDocumentListSuccess(String response) {
        Log.d("Belge Listesi Yanıtı", response); // Hata ayıklama için log mesajı

        try {
            JSONObject jsonObject = new JSONObject(response);

            // JSON yanıtından belge listesini alın
            JSONArray locationList = jsonObject.getJSONArray("location_list");

            // Belge listesini kullanmak için gereken işlemleri burada gerçekleştirin
            for (int i = 0; i < locationList.length(); i++) {
                String documentUrl = locationList.getString(i);
                // Belge URL'sini kullanarak gerekli işlemleri yapabilirsiniz
                // Örneğin, belgeyi bir liste görünümüne ekleyebilirsiniz

                dosyaListesi.add(documentUrl); // Listeye belgeyi ekleyin
            }

            // Adapteri güncelleyin
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            handleDocumentListFailure("Belge listeleme hatası: JSON ayrıştırma hatası");
        }
    }



    private void handleDocumentListFailure(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("Belge Listeleme Hatası", error); // Hata ayıklama için log mesajı
                // Belge listeleme hatası burada ele alınabilir
                // error parametresi, hata mesajıdır
                // Hatanın kullanıcıya gösterilmesi veya başka bir işlem yapılması gerekiyorsa burada yapılabilir
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }















}
