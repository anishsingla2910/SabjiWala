package com.prototypes.sabjiwala;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.BuildConfig;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prototypes.sabjiwala.account.AccountActivity;
import com.prototypes.sabjiwala.login.LoginActivity;
import com.prototypes.sabjiwala.ui.order_history_fragment.OrderHistoryFragment;
import com.prototypes.sabjiwala.ui.shopsfragment.ShopsFragment;
import com.prototypes.sabjiwala.update_activity.UpdateActivity;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    FirebaseAuth fAuth;
    ShopsFragment shopsFragment;
    OrderHistoryFragment orderHistoryFragment;
    FirebaseFirestore fStore;
    StorageReference imageRef;
    ImageView ivShopPicture;
    ImageView imageView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fStore = FirebaseFirestore.getInstance();

        fStore.collection("App")
                .document("app-details-my-sabji-wala")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (Double.parseDouble(task.getResult().getString("version")) > Double.parseDouble(BuildConfig.VERSION_NAME)) {
                            Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
        fAuth = FirebaseAuth.getInstance();
        shopsFragment = new ShopsFragment();
        orderHistoryFragment = new OrderHistoryFragment();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getUserDetails();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContent, shopsFragment)
                .addToBackStack(null)
                .commit();
        getSupportActionBar().setTitle("Home");
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(3).getSubMenu().getItem(0).setTitle(BuildConfig.VERSION_NAME);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_shops) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flContent, shopsFragment)
                    .addToBackStack(null)
                    .commit();
        } else if (item.getItemId() == R.id.nav_log_out) {
            fAuth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else if (item.getItemId() == R.id.nav_order_history) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flContent, orderHistoryFragment)
                    .addToBackStack(null)
                    .commit();

        }
        /*switch (item.getItemId()) {

            R.id.nav_shops:{
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent, shopsFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.nav_log_out: {
                fAuth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.nav_order_history: {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContent, orderHistoryFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            }
        }*/
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getUserDetails() {
        String userId = fAuth.getCurrentUser().getUid();
        String mobile = fAuth.getCurrentUser().getPhoneNumber();
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        imageView = headerView.findViewById(R.id.imageView);
        ivShopPicture = headerView.findViewById(R.id.ivShopPicture);
        TextView tvName = headerView.findViewById(R.id.tvName);
        TextView tvMobile = (TextView) headerView.findViewById(R.id.tvMobile);
        tvMobile.setText(mobile);
        StorageReference mStorageRef;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        imageView.setImageResource(R.drawable.my_sabji_wala_logo);
        imageRef = mStorageRef.child("customer_pictures/" + userId);
        Glide.with(ivShopPicture)
                .load(imageRef)
                .error(R.drawable.default_profile)
                .into(ivShopPicture);
        DocumentReference df = fStore.collection("Customers").document(userId);
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.getData() != null) {
                    tvName.setText(documentSnapshot.getString("name"));
                }
            }
        });

        ConstraintLayout constraintLayout = headerView.findViewById(R.id.llnav_header);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });
    }

    public void setTitle(String title) {
        toolbar.setTitle(title);
    }
}