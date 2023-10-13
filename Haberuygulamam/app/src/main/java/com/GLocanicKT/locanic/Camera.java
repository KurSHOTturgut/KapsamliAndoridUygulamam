package com.GLocanicKT.locanic;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Camera extends AppCompatActivity {

    // Girilen kullanıcı adı
    String userToken;

    // Kamera öğesi
    private static final int CAMERA_REQUEST_CODE = 1001;
    private static final int GALLERY_REQUEST_CODE = 1002;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 2001;
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 2002;
    ImageView kameraac, GstrlckAlan;
    Button galeri, dosyayiyukle;

    // Bottom menü imageView'leri
    ImageView home, location, kutu, camera, message, profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Kullanıcı adını almak için intent'i kontrol edin
        Intent intent = getIntent();
        userToken = intent.getStringExtra("username");

        Log.d(TAG, "onCreate: token"+userToken);

        // Camera öğeleri
        kameraac = findViewById(R.id.kamerac);
        galeri = findViewById(R.id.galeri);
        GstrlckAlan = findViewById(R.id.cekilenyuklenenfoto);
        dosyayiyukle = findViewById(R.id.dosyayiyukle);


        // Bottom menü itemleri
        home = findViewById(R.id.camerahome);
        location = findViewById(R.id.cameralocation);
        kutu = findViewById(R.id.camerakutu);
        camera = findViewById(R.id.cameracamera);
        message = findViewById(R.id.cameramessage);
        profile = findViewById(R.id.cameraproife);

        kameraac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCameraOrGallery();
            }
        });

        galeri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCameraOrGallery();
            }
        });

        dosyayiyukle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
            }
        });
    }

    private void openCameraOrGallery() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seçim Yapın");
        builder.setMessage("Kamera mı açmak istiyorsunuz, yoksa galeriden bir fotoğraf mı seçmek istiyorsunuz?");

        builder.setPositiveButton("Kamera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openCamera();
            }
        });

        builder.setNegativeButton("Galeri", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openGallery();
            }
        });

        builder.create().show();
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }

    private void openGallery() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMISSION_REQUEST_CODE);
        } else {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
        }
    }

    private void uploadFile() {
        BitmapDrawable drawable = (BitmapDrawable) GstrlckAlan.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        File file = saveBitmapToFile(bitmap);

        if (file != null) {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("multipart/form-data");
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("token", userToken)
                    .addFormDataPart("process_type", "upload")
                    .addFormDataPart("upload_file", "galeriya." + getFileExtension(file), RequestBody.create(MediaType.parse("application/octet-stream"), file))
                    .build();
            Request request = new Request.Builder()
                    .url("https://www.laconicmedya.com/mobile/api/laconic-medya-app/files/files.php")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Camera.this, "Dosya yükleme başarısız.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body().string();
                    Log.d(TAG, "onResponse: " + responseBody);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(responseBody);
                                String status = jsonObject.getString("status");
                                String message = jsonObject.getString("message");
                                String responseData = jsonObject.getString("response");

                                if (status.equals("1")) {
                                    Toast.makeText(Camera.this, message, Toast.LENGTH_SHORT).show();
                                    // Dosya yükleme başarılı olduğunda yapılacak işlemler
                                } else {
                                    Toast.makeText(Camera.this, "Dosya yükleme başarısız.", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(Camera.this, "Dosya yükleme başarısız.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        } else {
            Log.e(TAG, "uploadFile: File is null");
        }
    }

    private String getFileExtension(File file) {
        String extension = "";

        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = fileName.substring(dotIndex + 1);
        }

        return extension;
    }



    private File saveBitmapToFile(Bitmap bitmap) {
        File file = new File(getExternalCacheDir(), "image.jpg");
        OutputStream os;
        try {
            os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
            return file;
        } catch (Exception e) {
            Log.e(TAG, "saveBitmapToFile: ", e);
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Kamera izni reddedildi.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == GALLERY_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Galeri izni reddedildi.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                GstrlckAlan.setImageBitmap(image);
            } else if (requestCode == GALLERY_REQUEST_CODE) {
                Uri selectedImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    GstrlckAlan.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void camerahome(View view) {
        Intent intent = new Intent(Camera.this, MainActivity.class);
        intent.putExtra("username", userToken);
        startActivity(intent);
    }

    public void cameralocation(View view) {
        Intent intent = new Intent(Camera.this, LocationActivity.class);
        intent.putExtra("username", userToken);
        startActivity(intent);
    }

    public void camerakutu(View view) {
        Intent intent = new Intent(Camera.this, Kutu.class);
        intent.putExtra("username", userToken);
        startActivity(intent);
    }

    public void cameracamera(View view) {
        Intent intent = new Intent(Camera.this, Camera.class);
        intent.putExtra("username", userToken);
        startActivity(intent);
    }
    public void camerabildirim(View view){
        Intent intent = new Intent(Camera.this, BildirimGondermeActivity.class);
        intent.putExtra("username", userToken);
        startActivity(intent);
    }
    public void cameramessage(View view) {
        Intent intent = new Intent(Camera.this, Message.class);
        intent.putExtra("username", userToken);
        startActivity(intent);
    }

    public void cameraprofile(View view) {
        Intent intent = new Intent(Camera.this, Profile.class);
        intent.putExtra("username", userToken);
        startActivity(intent);
    }
}
