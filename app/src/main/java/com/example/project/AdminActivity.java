package com.example.project;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminActivity extends AppCompatActivity {
    public static EditText textToUpdate;
    public static EditText locationName;
    public static EditText locationLat;
    public static EditText LocationLong;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Button logOut = (Button) findViewById(R.id.signOut);
        Button updateDB = (Button) findViewById(R.id.updateDB);
        textToUpdate = (EditText) findViewById(R.id.texttoUpdate);
        locationName= (EditText) findViewById(R.id.objectiveName);
        locationLat=(EditText)findViewById(R.id.objectiveLat);
        LocationLong = (EditText) findViewById(R.id.objectiveLong);

        ref = FirebaseDatabase.getInstance().getReference().child("LocationInfo");


        updateDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String plainText = textToUpdate.getText().toString().trim();
                String locName = locationName.getText().toString().trim();
                String locLat = locationLat.getText().toString().trim();
                String locLong = LocationLong.getText().toString().trim();
                if(locName.equals("")){
                    locationName.setError("Please insert an objective name!");
                    return;
                }
                if (locLat.equals("")){
                    locationLat.setError("Please insert latitude!");
                    return;
                }
                try{
                    double l = Double.parseDouble(locLat);
                }catch(NumberFormatException e) {
                    locationLat.setError("Latitude must be a number!");
                    return;
                }
                if (locLong.equals("") ){
                    LocationLong.setError("Please insert longitude!");
                    return;
                }
                try{
                    double l = Double.parseDouble(locLong);
                }catch(NumberFormatException e) {
                    LocationLong.setError("Longitude must be a number!");
                    return;
                }
                if (plainText.equals("")){
                    textToUpdate.setError("Please insert a description!");
                    return;
                }


              ref.child(locName).child("Info").setValue(plainText);
              ref.child(locName).child("lat").setValue(locLat);
              ref.child(locName).child("long").setValue(locLong);
              ref.child(locName).child("order").setValue("99");
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginActivity.ref.child("latitude").removeValue();
                loginActivity.ref.child("longitude").removeValue();
                DatabaseReference tempRef = FirebaseDatabase.getInstance().getReference().child("Users Logged");
                tempRef.child(loginActivity.username).removeValue();

                FirebaseAuth.getInstance().signOut();
                finish();
                Intent intent = new Intent(AdminActivity.this, loginActivity.class);
                startActivity(intent);
            }
        });

    }
}