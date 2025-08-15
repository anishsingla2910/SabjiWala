package com.prototypes.sabjiwala.shop_activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prototypes.sabjiwala.MainActivity;
import com.prototypes.sabjiwala.R;
import com.prototypes.sabjiwala.cart_activity.CartActivity;
import com.prototypes.sabjiwala.classes.OrderVegetable;
import com.prototypes.sabjiwala.classes.Shop;
import com.prototypes.sabjiwala.classes.ShopId;
import com.prototypes.sabjiwala.classes.Vegetable;
import com.prototypes.sabjiwala.database_classes.LikedShopDatabaseHelper;

import java.util.ArrayList;

public class ShopActivity extends AppCompatActivity {
    
    private static final String TAG = "ShopActivity";
    public static String path;
    FirebaseFirestore fStore;
    StorageReference firebaseStorage;
    ImageView shop_image;
    Toolbar toolbar;
    TabLayout tabLayout;
    ArrayList<Vegetable> vegetables;
    RecyclerView recyclerView;
    ItemAdapter adapter;
    DatabaseHelper databaseHelper;
    FloatingActionButton flbtn_shop_cart;
    ArrayList<OrderVegetable> mArrayList;
    LikedShopDatabaseHelper db;
    ArrayList<ShopId> shopIds;
    FloatingActionButton likeButton, call_button;
    CollapsingToolbarLayout collapsingToolbarLayout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        
        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        recyclerView = findViewById(R.id.recycler_view);
        db = new LikedShopDatabaseHelper(this);
        databaseHelper = new DatabaseHelper(this, path);
        fStore = FirebaseFirestore.getInstance();
        call_button = findViewById(R.id.call_button);
        shop_image = findViewById(R.id.shop_image);
        firebaseStorage = FirebaseStorage.getInstance().getReference();
        likeButton = findViewById(R.id.favourite_button);
        StorageReference shopImage = firebaseStorage.child("shoppictures/" + path);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        Glide.with(shop_image)
                .load(shopImage)
                .into(shop_image);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShopActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        tabLayout = (TabLayout) findViewById(R.id.packagetablayout);
        flbtn_shop_cart = findViewById(R.id.flbtn_shop_cart);
        flbtn_shop_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ShopActivity.this, CartActivity.class);
                intent1.putExtra("TABLE_NAME", path);
                startActivity(intent1);
                //finish();
            }
        });
        call_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fStore.collection("SabjiWale")
                        .document(path)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Shop shop = task.getResult().toObject(Shop.class).withId(task.getResult().getId());
                                Intent intent1 = new Intent(Intent.ACTION_DIAL);
                                intent1.setData(Uri.parse("tel:" + shop.getShopNumber()));
                                startActivity(intent1);
                            }
                        });
            }
        });
    }
    
    @Override
    public void onStart() {
        super.onStart();
        //mRestaurantRegistration = docRef.addSnapshotListener(this);
        tabLayout.removeAllTabs();
        
        fStore.collection("SabjiWale")
                .document(path)
                .collection("ProductsCategories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                fStore.collection("SabjiWale")
                                        .document(path)
                                        .collection("ProductsCategories")
                                        .document(queryDocumentSnapshot.getId())
                                        .collection("Products")
                                        .whereEqualTo("isSelling", true)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                                if (!value.isEmpty()) {
                                                    String tabName = queryDocumentSnapshot.getId();
                                                    tabLayout.addTab(tabLayout.newTab().setText(tabName));
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                fStore.collection("SabjiWale")
                        .document(path)
                        .collection("ProductsCategories")
                        .document(tab.getText().toString())
                        .collection("Products")
                        .whereEqualTo("isSelling", true)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    vegetables = new ArrayList<>();
                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                        Vegetable v = snapshot.toObject(Vegetable.class).withId(snapshot.getId());
                                        vegetables.add(v);
                                    }
                                    getAllData();
                                    adapter = new ItemAdapter(vegetables, mArrayList, ShopActivity.this);
                                    adapter.setOnItemClickListener(new ItemAdapter.onItemClickListener() {
                                        @Override
                                        public void onItemClicked(Vegetable vegetable, int position, String Quantity) {
                                            databaseHelper.addData(vegetable.getId(), vegetable.getCategory(), Quantity);
                                        }
                                        
                                        @Override
                                        public void onSpinnerClicked(Vegetable vegetable, String Quantity) {
                                            databaseHelper.updateData(vegetable.getId(),
                                                    vegetable.getCategory(), Quantity);
                                        }
                                        
                                        @Override
                                        public void onItemRemoved(String id) {
                                            databaseHelper.deleteData(id);
                                        }
                                    });
                                    recyclerView.setLayoutManager(new LinearLayoutManager(ShopActivity.this));
                                    recyclerView.setAdapter(adapter);
                                }
                            }
                        });
            }
            
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            
            }
            
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            
            }
        });
        getDataFromDatabase();
        checkFavouriteMarked();
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (ShopId shopId : shopIds) {
                    if (path.equals(shopId.getID())) {
                        db.deleteData(path);
                        getDataFromDatabase();
                        checkFavouriteMarked();
                        return;
                    }
                }
                db.addData(path);
                getDataFromDatabase();
                checkFavouriteMarked();
            }
        });
        
        fStore.collection("SabjiWale")
                .document(path)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        Shop shop = documentSnapshot.toObject(Shop.class);
                        String title = shop.getShopName();
                        collapsingToolbarLayout.setTitle(title);
                    }
                });
    }
    
    public void getDataFromDatabase() {
        Cursor cursor = db.getAllData();
        mArrayList = new ArrayList<>();
        shopIds = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ShopId shopId = new ShopId(cursor.getString(cursor.getColumnIndex(db.Col1)));
            shopIds.add(shopId); //add the item
            cursor.moveToNext();
        }
    }
    
    public void checkFavouriteMarked() {
        for (ShopId shopId : shopIds) {
            if (path.equals(shopId.getID())) {
                likeButton.setImageResource(R.drawable.ic_favourite);
                return;
            }
        }
        likeButton.setImageResource(R.drawable.ic_unfavourite);
    }
    
    void getAllData() {
        Cursor c = databaseHelper.getAllData();
        mArrayList = new ArrayList<>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            OrderVegetable o = new OrderVegetable(c.getString(c.getColumnIndex(databaseHelper.Col1)),
                    c.getString(c.getColumnIndex(databaseHelper.Col2)),
                    c.getString(c.getColumnIndex(databaseHelper.Col3)));
            mArrayList.add(o);
            c.moveToNext();
        }
    }
}