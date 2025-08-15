package com.prototypes.sabjiwala.account;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.prototypes.sabjiwala.R;

import java.io.ByteArrayOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    CardView imageButton;
    ImageButton imageButton1;
    ImageView profile_image;
    StorageReference storageReference, profileImage;
    StorageReference reference;
    FirebaseAuth fAuth;
    String userId;
    RelativeLayout relativeLayout2;
    RelativeLayout relativeLayout3;
    double latitude,longitude;
    FirebaseFirestore fStore;
    Toolbar toolbar;
    ImageButton edit_image_button;
    TextView customer_name, customer_email, customer_address;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        edit_image_button = findViewById(R.id.edit_image_button);
        customer_address = findViewById(R.id.customer_address);
        storageReference = FirebaseStorage.getInstance().getReference();
        profile_image = findViewById(R.id.profile_image);
        imageButton = findViewById(R.id.card_view);
        imageButton1 = findViewById(R.id.image_button);
        imageButton.setOnClickListener(this);
        imageButton1.setOnClickListener(this);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        customer_name = findViewById(R.id.customer_name);
        relativeLayout2 = findViewById(R.id.relativeLayout2);
        relativeLayout3 = findViewById(R.id.relativeLayout3);
        customer_email = findViewById(R.id.customer_email);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Profile");
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        profileImage = storageReference.child("customer_pictures/" + userId);
        reference = storageReference.child("customer_pictures/" + userId);
        Glide.with(profile_image)
                .load(profileImage)
                .error(R.drawable.default_profile)
                .into(profile_image);

        fStore.collection("Customers")
                .document(userId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        customer_name.setText(value.get("name").toString());
                        customer_email.setText(value.get("email").toString());
                        customer_address.setText(value.get("address").toString());
                    }
                });

        edit_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(AccountActivity.this);
                alertDialog.setTitle("ADDRESS");
                alertDialog.setMessage("Enter your address");
                TextInputLayout textInputLayout = new TextInputLayout(AccountActivity.this);
                EditText input = new EditText(AccountActivity.this);
                LinearLayout.LayoutParams parameters = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                parameters.setMargins(20, 0, 20, 0);
                input.setLayoutParams(parameters);
                textInputLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                input.setMaxLines(20);
                input.setLines(2);
                textInputLayout.addView(input);
                alertDialog.setView(textInputLayout);
                alertDialog.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = input.getText().toString();
                                fStore.collection("Customers")
                                        .document(userId)
                                        .update("address", name);
                                if (ActivityCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(AccountActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                                }
                                if (ActivityCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AccountActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                                    Criteria criteria = new Criteria();
                                    String provider = locationManager.getBestProvider(criteria, true);
                                    Location location = locationManager.getLastKnownLocation(provider);

                                    if (location != null) {
                                        latitude = location.getLatitude();
                                        longitude = location.getLongitude();
                                        //LatLng latLng = new LatLng(latitude, longitude);
                                        LatLng myPosition = new LatLng(latitude, longitude);
                                        fStore.collection("Customers")
                                                .document(userId)
                                                .update("latitude", latitude);
                                        fStore.collection("Customers")
                                                .document(userId)
                                                .update("longitude", longitude);
                                    }
                                }
                            }
                        });
                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            }
        });

        relativeLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(AccountActivity.this);
                alertDialog.setTitle("NAME");
                alertDialog.setMessage("Enter your account's new name");
                TextInputLayout textInputLayout = new TextInputLayout(AccountActivity.this);
                EditText input = new EditText(AccountActivity.this);
                LinearLayout.LayoutParams parameters = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                parameters.setMargins(20, 0, 20, 0);
                input.setLayoutParams(parameters);
                textInputLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                textInputLayout.addView(input);
                alertDialog.setView(textInputLayout);
                alertDialog.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = input.getText().toString();
                                fStore.collection("Customers")
                                        .document(userId)
                                        .update("name", name);
                            }
                        });
                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            }
        });
        relativeLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(AccountActivity.this);
                alertDialog.setTitle("E-MAIL");
                alertDialog.setMessage("Enter your account's new e-mail address");
                TextInputLayout textInputLayout = new TextInputLayout(AccountActivity.this);
                EditText input = new EditText(AccountActivity.this);
                LinearLayout.LayoutParams parameters = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                parameters.setMargins(20, 0, 20, 0);
                input.setLayoutParams(parameters);
                textInputLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                textInputLayout.addView(input);
                alertDialog.setView(textInputLayout);
                alertDialog.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = input.getText().toString();
                                boolean validity = isEmailValid(name);
                                if (validity == true) {
                                    fStore.collection("Customers")
                                            .document(userId)
                                            .update("email", name);
                                }else{
                                    Toast.makeText(AccountActivity.this, "Entered Email is not valid", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                alertDialog.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        /*builder.setMessage("Click an Image or Select an Image").setTitle("Insert an Image");
        builder.setMessage("Click an Image or Select an Image")
                .setCancelable(true)
                .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        askCamerPermissions();
                    }
                })
                .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setTitle("Image");
        dialog.show();*/
        askCamerPermissions();
    }

    public void askCamerPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 101);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, 102);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required ot use camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            profile_image.setImageBitmap(image);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();
            UploadTask uploadTask = reference.putBytes(imageData);
        }
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}