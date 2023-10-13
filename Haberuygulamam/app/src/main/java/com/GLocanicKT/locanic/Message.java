package com.GLocanicKT.locanic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Message extends AppCompatActivity {

    ImageView home , location, kutu , camera , message , profile;


    //girilen kullanıcı adı
    String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        home = findViewById(R.id.messagehome);
        location =findViewById(R.id.messagelocation);
        kutu=findViewById(R.id.messagekutu);
        camera=findViewById(R.id.messagecamera);
        message=findViewById(R.id.messagemessage);
        profile=findViewById(R.id.messageproife);

        // Kullanıcı adını almak için intent'i kontrol edin
        Intent intent = getIntent();
        username=intent.getStringExtra("username");
    }

    public void messagehome(View view){
        Intent intent = new Intent(Message.this,MainActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
    public void messagelocation(View view){
        Intent intent = new Intent(Message.this,LocationActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
    public void messagekutu(View view){
        Intent intent = new Intent(Message.this,Kutu.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
    public void messagecamera(View view){
        Intent intent = new Intent(Message.this,Camera.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
    public void messagebildirim(View view){
        Intent intent = new Intent(Message.this,BildirimGondermeActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
    public void messagemessage(View view){
        Intent intent = new Intent(Message.this,Message.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
    public void messageprofile(View view){
        Intent intent = new Intent(Message.this,Profile.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
}
