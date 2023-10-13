package com.GLocanicKT.locanic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {
    ImageView Logo;
    TextView textBottom,name;

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE);
        setContentView(R.layout.activity_splash_screen);


        // ImageView ve TextViewleri xml ile javada bağladığım kısım
        Logo=findViewById(R.id.logoImageView);
        textBottom=findViewById(R.id.copyrightTextView);
        name=findViewById(R.id.name);


        // animasyon kısmı
        Animation logoAnimation = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.zoom_animation);
        Animation nameAnimation = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.zoom_animation);

        Logo.setVisibility(View.VISIBLE);
        Logo.startAnimation(logoAnimation);

        logoAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                name.setVisibility(View.VISIBLE);
                name.startAnimation(nameAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        nameAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                textBottom.setVisibility(View.VISIBLE);
                final String animateTxt = textBottom.getText().toString();
                textBottom.setText("");
                count = 0;

                new CountDownTimer(animateTxt.length() * 140, 140) {

                    @Override
                    public void onTick(long l) {
                        textBottom.setText(animateTxt.substring(0, count + 1));
                        count++;
                    }

                    @Override
                    public void onFinish() {
                        // Animasyon tamamlandığında Register aktivitesine geçiş yap
                        textBottom.setText(animateTxt);
                        Intent intent = new Intent(SplashScreen.this, Register.class);
                        startActivity(intent);
                        finish(); // Eğer bu ekranı geri dönmemek için kullanmayacaksanız, bu aktiviteyi sonlandırabilirsiniz.
                    }
                }.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }
}