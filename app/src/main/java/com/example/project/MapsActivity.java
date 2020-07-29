package com.example.project;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.textservice.TextInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.errorprone.annotations.Var;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    Polyline currentPolyline;
    MarkerOptions place1, place2, place3, place4, place5, place6, place7;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private static final String KEY_LOCATION = "location";
    private CameraPosition cameraPosition;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private FusedLocationProviderClient fusedLocationProviderClient;
    Marker mCurrLocationMarker;
    LocationListener mLocationListener;
    private static final String TAG = "ERR";
    LocationManager locationManager;
    private double posLat;
    private double posLng;

    private LatLng position;
    private Marker mPosition;

    Context mContext;

    String places1Info, places2Info, places3Info, places4Info, places5Info, places6Info, places7Info;

    Marker places1mark, places2mark, places3mark, places4mark, places5mark, places6mark, places7mark;
    Marker currentMarker;
    MarkerOptions omulet;
    String email;
    TextView tView;
    private long timeRemaining = 0;

    LocationRequest mLocationRequest;
    double userlatitude;
    double userlongitude;
    String userLocation;
    LatLng userLoc;
    FirebaseAuth user;
    String guid;
    DatabaseReference tempRef;
    Handler handler;
    int delay = 10000; //milliseconds

    private Location currentLocation;
    private Location previousLocation;
    LatLng uHull = new LatLng(53.7703, -0.3671);
    float distanceToStart = 201;
    static boolean tourStarted = false;
    static boolean o1,o2,o3,o4,o5,o6,o7 = false;
    final List<Marker> markerlist = new ArrayList<>();
    final List<String> placesInfoList = new ArrayList<>();
    long millisInFuture =0;

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }



    public void onLocationChanged(Location location) {


    }

   /* private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                getDeviceLocation();
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                getDeviceLocation();
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }

        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }*/

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
        //updateLocationUI();
    }

    /*private void getDeviceLocation() {

        final String error = "Sunt pe LASTKNONLOCATION";

        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                Log.i(TAG, "if in lastknownlocation");
                                *//*mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), 15));*//*
                                *//*mMap.moveCamera(CameraUpdateFactory
                                        .newLatLngZoom(place1.getPosition(), 17));*//*
                                mMap.moveCamera(CameraUpdateFactory
                                        .newLatLngZoom(place1.getPosition(), 17));
                                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                                mMap.setMyLocationEnabled(true);

                            } else {
                                Log.i(TAG, "if in lastknownlocation dar nu prea");
                            }
                        } else {
                            Log.i(TAG, "else in place1");
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(place1.getPosition(), 17));
                            mMap.getUiSettings().setMyLocationButtonEnabled(true);
                            mMap.setMyLocationEnabled(true);

                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusCheck();
        getLocationPermission();
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setContentView(R.layout.activity_maps);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        tView = (TextView) findViewById(R.id.timerDisplay);
        final Button startTour = (Button) findViewById(R.id.btnStartTour);
        Button logOut = (Button) findViewById(R.id.btnLogout);
        Button verifyMess = (Button) findViewById(R.id.verifyMessages);

        verifyMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapsActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        startTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                millisInFuture = markerlist.size() * 300000 ; //5 minutes per objective
                long countDownInterval = 1000; //1 second
                CountDownTimer timer;

                if(distanceToStart <=200){
                //Initialize a new CountDownTimer instance
                    tourStarted = true;
                timer = new CountDownTimer(millisInFuture,countDownInterval){
                    public void onTick(long millisUntilFinished){

                        tView.setText("Tour has started! Total time available is 5 minutes" +
                                " per objective. Time Left: "+String.format("%d min, %d sec",
                                TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                            //Put count down timer remaining time in a variable
                            timeRemaining = millisUntilFinished;
                        startTour.setEnabled(false);
                    }
                    public void onFinish(){
                        //Do something when count down finished
                        tView.setText("Tour has finished!");
                        startTour.setEnabled(true);
                        tourStarted=false;
                        o1 = false; o2 = false; o3 = false; o4 = false; o5 = false; o6 = false ; o7 =false;

                    }
                }.start();}else {
                TextView infoText = (TextView) findViewById(R.id.infoText);
                infoText.setText("You are "+ distanceToStart+" meters away from your starting point. Please move closer before starting the tour.");
            }}
        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginActivity.ref.child("latitude").removeValue();
                loginActivity.ref.child("longitude").removeValue();
                tempRef = FirebaseDatabase.getInstance().getReference().child("Users Logged");
                tempRef.child(loginActivity.username).removeValue();

                FirebaseAuth.getInstance().signOut();
                finish();
                Intent intent = new Intent(MapsActivity.this, loginActivity.class);
                startActivity(intent);

            }
        });

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        mMap.setOnMarkerClickListener(MapsActivity.this);
            int size = markerlist.size();
            final List<Boolean> beenVisited = new ArrayList<>();
            final List<Integer> timesToCheck = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
       /* user = FirebaseAuth.getInstance();
        guid = user.getUid();*/
        mContext = this;


        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);

        final android.location.LocationListener locationListenerGPS = new android.location.LocationListener() {
            Boolean once = true;
            @Override
            public void onLocationChanged(Location location) {
                currentLocation = location;


                if (currentMarker != null) {
                    currentMarker.remove();
                }


                userlatitude = location.getLatitude();
                userlongitude = location.getLongitude();
                Location start = new Location("");
                start.setLatitude(53.769894);
                start.setLongitude(-0.367887);
                distanceToStart = location.distanceTo(start);
                if (distanceToStart>200 && tourStarted == false){
                    TextView infoText = (TextView) findViewById(R.id.infoText);
                    infoText.setText("You are "+ distanceToStart+" meters away from your starting point. Please move closer before starting the tour.");
                }

                if (once){
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userlatitude,userlongitude), 17));
                once = false;
                }
                loginActivity.ref.child("latitude").setValue(userlatitude);
                loginActivity.ref.child("longitude").setValue(userlongitude);
                tempRef = FirebaseDatabase.getInstance().getReference().child("Users Logged");
                tempRef.child(loginActivity.username).removeValue();
                tempRef.child(loginActivity.username).setValue(userlatitude + ":"+userlongitude);

                if(tourStarted){
                    //If the tour just started, populate the lists depending on how many onjectives
                    if (beenVisited.size() == 0 && timesToCheck.size()==0){
                        //Add the initial value of time to be checked for 1st objective
                        timesToCheck.add(((int)millisInFuture- 240000)/60000);
                        //iterate through the list of objectives and populate the boolean and times list
                        for (Marker x: markerlist
                        ) {
                            beenVisited.add(false);
                            if (markerlist.indexOf(x) != 0){
                            timesToCheck.add(timesToCheck.get(markerlist.indexOf(x)-1) - 4);
                            }
                        }
                    }
                    //The actual check being done during the tour. Iterates through the locations, checks for distance between the guide and the objective, checks if the boolean is true or false
                    // If the boolean is false it means that the objective has not been fully visited yet. If the time left allocated for the objective is less than 1 minute
                    // alerts the guide and marks the objective as visited. Since the flag for the objective has been set it will only check the remaining objectives.
                    for (Marker x: markerlist
                         ) {
                        Location markLoc = new Location("");
                        markLoc.setLatitude(x.getPosition().latitude);
                        markLoc.setLongitude(x.getPosition().longitude);
                        float distanceToMarker = location.distanceTo(markLoc);
                        int index = markerlist.indexOf(x);

                        if ((distanceToMarker <= 20)&& beenVisited.get(index) == false){

                            if (TimeUnit.MILLISECONDS.toMinutes( timeRemaining) < timesToCheck.get(index)){

                                TextView infoText = (TextView) findViewById(R.id.infoText);
                                infoText.setText("You have used up the time for "+x.getTitle() +", please move onto the next objective");
                                beenVisited.set(index, true);
                                //x.remove();
                            }

                        }

                    }

                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, locationListenerGPS);

        final PolylineOptions polylineOptions = new PolylineOptions();
        DatabaseReference places1Ref = FirebaseDatabase.getInstance().getReference().child("LocationInfo");
        //Checks the database for objective entries
        places1Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() != null){
                    String values = snapshot.getValue().toString();
                    //Initialize needed variables
                    String objLat = null;
                    String objLong= null;
                    String objName= null;
                    String objInfo = null;
                    int objOrder;
                    Map<Integer,String> toOrder = new HashMap<>();
                    //Splits whatever was found in the database
                    String[] initialParts = values.split("\\}\\,");
                    //Iterate through the new created array and replaces unneeded characters with the needed ones. Preparing the array for further split
                   for (int i =0;i < initialParts.length; i++){
                       initialParts[i] = initialParts[i].replace("={Info=",",").replace("lat=","").replace("long=","").replace("{","").replace("}}","").replace("order=","");
                       //Further split each part of the initial array into more parts
                        String[] secondaryValues = initialParts[i].split(",");
                        objInfo = secondaryValues[1].trim();
                        objName=secondaryValues[0].trim();
                        objLat=secondaryValues[2].trim();
                        objLong=secondaryValues[3].trim();
                        //Prepare an int for adding as a key to the map to be able to order the objectives the way needed
                        objOrder= Integer.parseInt(secondaryValues[4].trim());
                        //Prepare a string for adding to a map
                        String rest = objName +"," +objInfo+"," +objLat+"," +objLong;
                        //Adding markers to the map, populating lists of information and the list of markers
                       Double lat = Double.parseDouble(objLat);
                       Double lng = Double.parseDouble(objLong);
                       LatLng markerLoc = new LatLng(lat, lng);

                       Marker marker = mMap.addMarker((new MarkerOptions().position(markerLoc)).title(objName));
                       mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 17));
                       markerlist.add(marker);
                       placesInfoList.add(objInfo);
                       //Populates the map in preparation for ordering
                       toOrder.put(objOrder,rest);
                   }
                   //Map sorting
                    Map<Integer, String> sortedMap = new TreeMap<Integer, String>(toOrder);
                    List<String> parts = new ArrayList<>();
                    //Map is sorted and each value of the map is added to a list in the correct order
                    for (Map.Entry<Integer, String> entry : sortedMap.entrySet()) {
                        parts.add(entry.getValue());
                    }
                    //Newly populated list is being split, and a polyoptions is being populated in order to draw
                    for (String x: parts
                         ) {
                        String[] secondaryParts = x.split(",");
                        objInfo = secondaryParts[1].trim();
                        objName=secondaryParts[0].trim();
                        objLat=secondaryParts[2].trim();
                        objLong=secondaryParts[3].trim();
                        Double lat = Double.parseDouble(objLat);
                        Double lng = Double.parseDouble(objLong);
                        polylineOptions.add(new LatLng(lat,lng));
                    }

                    mMap.addPolyline(polylineOptions);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }
    // populates the info window with  marker's info on click
    @Override
    public boolean onMarkerClick(Marker marker) {

        for (int i = 0;i <markerlist.size();i++){
            if (markerlist.contains(marker)){
                TextView infoText = (TextView) findViewById(R.id.infoText);
                int position = markerlist.indexOf(marker);
                String infoToSet= placesInfoList.get(position);
                infoText.setText(infoToSet);
                //marker.remove();
            }
        }
        return false;
    }
}