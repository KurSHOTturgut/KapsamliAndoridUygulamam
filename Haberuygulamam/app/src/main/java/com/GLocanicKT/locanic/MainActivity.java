package com.GLocanicKT.locanic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ImageView home , location, kutu , camera , message , profile;
    ScrollView scrollView;
    TextView textView1 , textView2;

    LinearLayout bottomMenu;

    //girilen kullanıcı adı
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scrollView = findViewById(R.id.scrollView);
        textView1 = findViewById(R.id.textview1);
        textView2=findViewById(R.id.textview2);
        bottomMenu=findViewById(R.id.bottom_menu);

        // Kullanıcı adını almak için intent'i kontrol edin
        Intent intent = getIntent();
        username=intent.getStringExtra("username");

        //Bottom menü
        home=findViewById(R.id.mainhome);
        location =findViewById(R.id.mainlocation);
        kutu=findViewById(R.id.mainkutu);
        camera=findViewById(R.id.maincamera);
        message=findViewById(R.id.mainmessage);
        profile=findViewById(R.id.mainproife);

        Toast.makeText(this, username, Toast.LENGTH_SHORT).show();

        final int startAlpha = 0; // Başlangıç opaklık değeri
        final int endAlpha = 255; // Bitiş opaklık değeri

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                // ScrollView'nin dikey kaydırma değerini alabiliriz
                int scrollY = scrollView.getScrollY();

                // TextView'lerin opaklık değerini güncelleyebiliriz
                float alpha = calculateAlpha(scrollY);
                textView1.setAlpha(alpha);
                textView2.setAlpha(alpha);
                // Diğer TextView nesneleri için de aynı işlemi yapılabiliriz
            }

            private float calculateAlpha(int scrollY) {
                int viewHeight = scrollView.getHeight(); // ScrollView'nin görüntü yüksekliği
                int threshold = viewHeight / 2; // Eşik değeri: ScrollView yüksekliğinin yarısı

                if (scrollY >= threshold) {
                    // Eşik değerinin üzerinde ise maksimum opaklık değeri döndürüme kısmı
                    return (float) endAlpha / 255f;
                } else {
                    // Eşik değerinin altındaysa opaklık değerini hesaplayabiliriz
                    float ratio = (float) scrollY / (float) threshold;
                    return startAlpha / 255f + (ratio * (endAlpha - startAlpha) / 255f);
                }
            }
        });

    }


    public void mainhome(View view){
        Intent intent = new Intent(MainActivity.this,MainActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
    public void mainlocation(View view){
        Intent intent = new Intent(MainActivity.this,LocationActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
    public void mainkutu(View view){
        Intent intent = new Intent(MainActivity.this,Kutu.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
    public void maincamera(View view){
        Intent intent = new Intent(MainActivity.this,Camera.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
    public void mainBildirim(View view){
        Intent intent = new Intent(MainActivity.this,BildirimGondermeActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
    public void mainmessage(View view){
        Intent intent = new Intent(MainActivity.this,Message.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
    public void mainprofile(View view){
        Intent intent = new Intent(MainActivity.this,Profile.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }


}