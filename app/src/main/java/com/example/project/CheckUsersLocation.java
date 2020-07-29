package com.example.project;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CheckUsersLocation extends FragmentActivity implements OnMapReadyCallback {
    public static TextView userList;
    DatabaseReference ref;
    private GoogleMap mMap;
    long maxNum;
    int nums = 0;
    String[] initialParts = new String[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_users_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button logOut = (Button) findViewById(R.id.logOut);
        Button userCheck = (Button) findViewById(R.id.updateUsers);
        Button sendMessage= (Button) findViewById(R.id.messageUsers);
        Button toDB = (Button) findViewById(R.id.toDatabase);
        ref = FirebaseDatabase.getInstance().getReference().child("Users Logged");

        userCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mMap.clear();
                        if(snapshot.getValue() != null){
                            String values = snapshot.getValue().toString();

                            values = values.trim().replace("{","").replace("}","");
                            String[] initialParts = values.split(",");

                            for (int i = 0; i < initialParts.length; i++) {

                                String[] parts = initialParts[i].split("=");
                                String username = parts[0];
                                String[] coords = parts[1].split(":");

                                Double lat = Double.parseDouble(coords[0].trim());
                                Double lng = Double.parseDouble(coords[1].trim());
                                LatLng userloc = new LatLng(lat, lng);
                                maxNum = snapshot.getChildrenCount();
                                nums = (int) maxNum;
                                Marker marker = mMap.addMarker((new MarkerOptions().position(userloc)).title(username));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 17));
                            }
                        }else {
                            Toast.makeText(CheckUsersLocation.this, "No users are logged in!",
                                    Toast.LENGTH_SHORT).show();
                        }
                        //markers.add(marker);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        toDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckUsersLocation.this, AdminActivity.class);
                startActivity(intent);
            }
        });
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CheckUsersLocation.this, ChatActivity.class);
                startActivity(intent);
                //finish();
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
                Intent intent = new Intent(CheckUsersLocation.this, loginActivity.class);
                startActivity(intent);
            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


    }
}