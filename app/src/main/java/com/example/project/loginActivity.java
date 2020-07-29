package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class loginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText mPassWord;
    private EditText mEmail;
    private static final String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{6,}$";
    Button newUserButton;
    Button loginbutton;
    String role;
    public static String email;
    public static DatabaseReference ref;
    public static String userAndLocationInfo;
    public static String username;
    private boolean locationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getLocationPermission();
        mPassWord = (EditText) findViewById(R.id.passwordInput);
        mEmail = (EditText) findViewById(R.id.emailInput);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        loginbutton = (Button)findViewById(R.id.loginButton);
        newUserButton = (Button)findViewById(R.id.newAccountButton);


        newUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(loginActivity.this,AccRegister.class);
                startActivity(intent);
                finish();

            }
        });
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mPasswordInput = mPassWord.getText().toString().trim();
                String mEmailInput = mEmail.getText().toString().trim();
                boolean valid = true;
                Pattern emailPattern = Pattern.compile(emailRegex);
                Matcher emailMatcher = emailPattern.matcher((mEmailInput));
                Pattern passwordPattern = Pattern.compile(passwordRegex);
                Matcher passwordMatcher = passwordPattern.matcher((mPasswordInput));
                if (!emailMatcher.matches()){
                    mEmail.setError("Please use a valid email");
                    return;
                }
                if (!passwordMatcher.matches()){
                    mPassWord.setError("Please use a valid password");
                    return;
                }
                mAuth.signInWithEmailAndPassword(mEmailInput, mPasswordInput)
                        .addOnCompleteListener(loginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    email = user.getEmail();
                                    String guid = user.getUid();
                                    ref=FirebaseDatabase.getInstance().getReference().child("Users").child(guid);
                                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                     @Override
                                     public void onDataChange(@NonNull DataSnapshot snapshot) {
                                         //role = snapshot.child("role").getValue(String.class);
                                         username = snapshot.child("username").getValue().toString();
                                         if (snapshot.child("role").getValue(String.class).equalsIgnoreCase("Admin")){
                                         Toast.makeText(loginActivity.this, "Admin Authentication Successful.",
                                                 Toast.LENGTH_SHORT).show();

                                         //Intent intent = new Intent(loginActivity.this,AdminActivity.class);
                                             Intent intent = new Intent(loginActivity.this,CheckUsersLocation.class);
                                         startActivity(intent);
                                         finish();
                                         }
                                         else if (snapshot.child("role").getValue(String.class).equalsIgnoreCase("User"))
                                         {
                                             //userAndLocationInfo = email+ " is at Latitude: "+ snapshot.child("latitude").getValue().toString() + " and Longitude: "+snapshot.child("longitude").getValue().toString();
                                             Toast.makeText(loginActivity.this, "User Authentication Successful.",
                                                     Toast.LENGTH_SHORT).show();
                                             Intent intent = new Intent(loginActivity.this,MapsActivity.class);
                                             startActivity(intent);
                                             finish();
                                         }else {
                                             Toast.makeText(loginActivity.this, "Authentication failed. User does not have a set role. Check DB.",
                                                     Toast.LENGTH_SHORT).show();
                                         }

                                     }

                                     @Override
                                     public void onCancelled(@NonNull DatabaseError error) {

                                     }
                                 });

                                } else {


                                    Toast.makeText(loginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }

                            }
                        });
            }
        });
    }
}
