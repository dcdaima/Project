package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static int Splash_timeout = 2000;
    int data = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button newButton;


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(new Runnable(){
    @Override
    public void run(){
        Intent homeIntent = new Intent(MainActivity.this, loginActivity.class);
        startActivity(homeIntent);
        finish();
    }
}, Splash_timeout);
        /*newButton = (Button)findViewById(R.id.randomButton);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this,loginActivity.class);
               // intent.putExtra("sending " ,data);
                startActivity(intent);

            }
        });*/
    }
}
