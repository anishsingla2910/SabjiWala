package com.prototypes.sabjiwala.choose_package;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.prototypes.sabjiwala.R;
import com.prototypes.sabjiwala.classes.Shop;
import com.prototypes.sabjiwala.database_classes.LikedShopDatabaseHelper;
import com.prototypes.sabjiwala.shop_activity.ShopActivity;

import java.util.ArrayList;

public class ChooseSabjiWalaActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 3000;
    private GoogleMap mMap;
    Toolbar toolbar;
    ArrayList<Marker> sabjiWalaMarkers;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    ArrayList<Shop> shops;
    LikedShopDatabaseHelper databaseHelper;
    ArrayList<Circle> sabjiWalaCircles;
    private static final String TAG = "ChooseSabjiWalaActivity";
    GeoFire geoFire;
    DatabaseReference firebaseDatabase;
    String path;
    double latitude;
    double longitude;
    boolean inRange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_sabji_wala);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        toolbar = findViewById(R.id.toolbar);
        shops = new ArrayList<>();
        sabjiWalaMarkers = new ArrayList<>();
        sabjiWalaCircles = new ArrayList<>();
        databaseHelper = new LikedShopDatabaseHelper(this);
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        setActionBar(toolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (ActivityCompat.checkSelfPermission(ChooseSabjiWalaActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ChooseSabjiWalaActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            });
            firebaseDatabase = FirebaseDatabase.getInstance().getReference();
            fStore.collection("SabjiWale")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            for (QueryDocumentSnapshot documentSnapshot : value) {
                                Shop shop = documentSnapshot.toObject(Shop.class).withId(documentSnapshot.getId());
                                shops.add(shop);
                            }
                            for (Shop shop : shops) {
                                double latitude = shop.getLocation().getLatitude();
                                double longitude = shop.getLocation().getLongitude();
                                com.google.android.gms.maps.model.LatLng location =
                                        new com.google.android.gms.maps.model.LatLng(latitude, longitude);
                                CircleOptions circleOptions = new CircleOptions();
                                circleOptions.center(location);
                                circleOptions.radius(shop.getRadiusSupplied());
                                circleOptions.fillColor(Color.argb(100, 255, 0, 0));
                                circleOptions.strokeColor(Color.argb(60, 255, 0, 0));
                                Circle circle = mMap.addCircle(circleOptions);
                                sabjiWalaCircles.add(circle);
                                Marker marker = mMap.addMarker(new MarkerOptions()
                                        .position(location)
                                        .title(shop.getId()));
                                sabjiWalaMarkers.add(marker);
                            }
                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    for (Marker markers : sabjiWalaMarkers) {
                                        if (marker.equals(markers)) {
                                            /*boolean progress = databaseHelper.addData(marker.getTitle());
                                            Intent intent = new Intent(ChooseSabjiWalaActivity.this, ShopActivity.class);
                                            intent.putExtra("path", marker.getTitle());
                                            startActivity(intent);*/
                                            checkFilter(marker.getTitle());
                                        }
                                    }
                                    return false;
                                }
                            });
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(ChooseSabjiWalaActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    });
                    firebaseDatabase = FirebaseDatabase.getInstance().getReference();
                    fStore.collection("SabjiWale")
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    for (QueryDocumentSnapshot documentSnapshot : value) {
                                        Shop shop = documentSnapshot.toObject(Shop.class).withId(documentSnapshot.getId());
                                        shops.add(shop);
                                    }
                                    for (Shop shop : shops) {
                                        double latitude = shop.getLocation().getLatitude();
                                        double longitude = shop.getLocation().getLongitude();
                                        com.google.android.gms.maps.model.LatLng location =
                                                new com.google.android.gms.maps.model.LatLng(latitude, longitude);
                                        CircleOptions circleOptions = new CircleOptions();
                                        circleOptions.center(location);
                                        circleOptions.radius(shop.getRadiusSupplied());
                                        circleOptions.fillColor(Color.argb(100, 255, 0, 0));
                                        circleOptions.strokeColor(Color.argb(60, 255, 0, 0));
                                        Circle circle = mMap.addCircle(circleOptions);
                                        sabjiWalaCircles.add(circle);
                                        Marker marker = mMap.addMarker(new MarkerOptions()
                                                .position(location)
                                                .title(shop.getId()));
                                        sabjiWalaMarkers.add(marker);
                                    }
                                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                        @Override
                                        public boolean onMarkerClick(Marker marker) {
                                            for (Marker markers : sabjiWalaMarkers) {
                                                if (marker.equals(markers)) {
                                            /*boolean progress = databaseHelper.addData(marker.getTitle());
                                            Intent intent = new Intent(ChooseSabjiWalaActivity.this, ShopActivity.class);
                                            intent.putExtra("path", marker.getTitle());
                                            startActivity(intent);*/
                                                    checkFilter(marker.getTitle());
                                                }
                                            }
                                            return false;
                                        }
                                    });
                                }
                            });
                }else{
                    finish();
                    Toast.makeText(this, "Permission is required for the app", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void checkFilter(String sabjiWalaId) {
        fStore.collection("SabjiWale")
                .document(sabjiWalaId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Shop shop = task.getResult().toObject(Shop.class).withId(task.getResult().getId());
                        if (ActivityCompat.checkSelfPermission(ChooseSabjiWalaActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                            @Override
                            public void onLocationChanged(@NonNull Location location) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        });
                        GeoLocation geoLocation = new GeoLocation(latitude, longitude);
                        geoFire = new GeoFire(firebaseDatabase.child("sabjiWalaLocations"));
                        geoFire.setLocation(shop.getId(), new GeoLocation(shop.getLocation().getLatitude(), shop.getLocation().getLongitude()));
                        GeoQuery geoQuery = geoFire.queryAtLocation(geoLocation, shop.getRadiusSupplied() / 1000f);
                        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                            @Override
                            public void onKeyEntered(String key, GeoLocation location) {
                                inRange = true;
                                path = key;
                                fStore.collection("SabjiWale")
                                        .document(path)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (key.equals(sabjiWalaId)) {
                                                    Shop shop1 = task.getResult().toObject(Shop.class).withId(task.getResult().getId());
                                                    boolean availability = shop1.shopStatus;
                                                    if (availability) {
                                                        Intent intent = new Intent(ChooseSabjiWalaActivity.this, ShopActivity.class);
                                                        intent.putExtra("path", shop1.getId());
                                                        startActivity(intent);
                                                    } else {
                                                        Toast.makeText(ChooseSabjiWalaActivity.this, "Shop is not available now. Please try later.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        });
                            }

                            @Override
                            public void onKeyExited(String key) {

                            }

                            @Override
                            public void onKeyMoved(String key, GeoLocation location) {

                            }

                            @Override
                            public void onGeoQueryReady() {
                                if (!inRange){
                                    Toast.makeText(ChooseSabjiWalaActivity.this, "This shop does not supply your locations", Toast.LENGTH_SHORT).show();
                                }
                                inRange = false;
                            }

                            @Override
                            public void onGeoQueryError(DatabaseError error) {
                                Log.e(TAG, "onGeoQueryError: " + error);
                            }
                        });
                    }
                });

    }
}