package com.prototypes.sabjiwala.order_confirmation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.prototypes.sabjiwala.MainActivity;
import com.prototypes.sabjiwala.R;
import com.prototypes.sabjiwala.classes.Order;
import com.prototypes.sabjiwala.classes.OrderedVegetable;
import com.prototypes.sabjiwala.classes.Shop;
import com.prototypes.sabjiwala.shop_activity.DatabaseHelper;

public class OrderConfirmationActivity extends AppCompatActivity {

    String path;
    DatabaseHelper databaseHelper;
    Toolbar toolbar;
    FirebaseFirestore fStore;
    TextView address, owner_name, shop_name;
    LinearLayout linearLayout2;
    float total;
    MaterialTextView total_amount;
    FloatingActionButton call_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        databaseHelper = new DatabaseHelper(OrderConfirmationActivity.this, path);

        fStore = FirebaseFirestore.getInstance();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        owner_name = findViewById(R.id.owner_name);
        shop_name = findViewById(R.id.shop_name);
        total_amount = findViewById(R.id.total_amount);
        address = findViewById(R.id.address);
        linearLayout2 = findViewById(R.id.linearLayout2);
        call_button = findViewById(R.id.call_button);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(OrderConfirmationActivity.this, MainActivity.class);
                startActivity(intent1);
                finish();
            }
        });

        fStore.collection("Orders")
                .document(path)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Order order = task.getResult().toObject(Order.class).withId(task.getResult().getId());
                        fStore.collection("SabjiWale")
                                .document(order.getSabjiwala_id())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        Shop shop = task.getResult().toObject(Shop.class).withId(task.getResult().getId());
                                        String address_string = shop.getShopAddress();
                                        String shop_name_string = shop.getShopName();
                                        String owner_name_string = shop.getOwnerName();
                                        address.setText(address_string);
                                        shop_name.setText(shop_name_string);
                                        owner_name.setText(owner_name_string);
                                        call_button.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent1 = new Intent(Intent.ACTION_DIAL);
                                                intent1.setData(Uri.parse("tel:" + shop.getShopNumber()));
                                                startActivity(intent1);
                                            }
                                        });
                                    }
                                });
                    }
                });

        fStore.collection("Orders")
                .document(path)
                .collection("order_items")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            OrderedVegetable orderedVegetable = queryDocumentSnapshot.toObject(OrderedVegetable.class);
                            View v = getLayoutInflater().inflate(R.layout.bill_layout, null);
                            TextView tv_name = v.findViewById(R.id.tv_name);
                            TextView tv_quantity = v.findViewById(R.id.tv_quantity);
                            TextView tv_price = v.findViewById(R.id.tv_price);
                            TextView tv_amount = v.findViewById(R.id.tv_amount);
                            tv_price.setText(getString(R.string.rupee_symbol) + orderedVegetable.getVegetable_price());
                            String item_name = orderedVegetable.getVegetable_name();
                            tv_quantity.setText(orderedVegetable.getVegetable_quantity());
                            tv_name.setText(item_name);
                            float amount = 0;
                            switch (orderedVegetable.getVegetable_price_per_unit()){
                                case "Per Kg": {
                                    String[] split = orderedVegetable.getVegetable_quantity().split(" ");
                                    if (split[1].equals("gram")) {
                                        amount = Float.parseFloat(orderedVegetable.getVegetable_price()) * Float.parseFloat(split[0]) / 1000f;
                                    }else {
                                        amount = Float.parseFloat(orderedVegetable.getVegetable_price()) * Float.parseFloat(split[0]);
                                    }
                                    total = total + amount;
                                    break;
                                }
                                case "Per Dozen": {
                                    String[] split = orderedVegetable.getVegetable_quantity().split(" ");
                                    amount = Float.parseFloat(orderedVegetable.getVegetable_price()) * Float.parseFloat(split[0])/12f;
                                    total = total + amount;
                                    break;
                                }
                                case "Per Piece": {
                                    String[] split = orderedVegetable.getVegetable_quantity().split(" ");
                                    amount = Float.parseFloat(orderedVegetable.getVegetable_price()) * Float.parseFloat(split[0]);
                                    total = total + amount;
                                    break;
                                }
                                case "Per 100 gram": {
                                    String[] split = orderedVegetable.getVegetable_quantity().split(" ");
                                    amount = Float.parseFloat(orderedVegetable.getVegetable_price()) * Float.parseFloat(split[0])/100f;
                                    total = total + amount;
                                    break;
                                }
                            }
                            tv_amount.setText(getString(R.string.rupee_symbol) + String.format("%.2f", amount));
                            linearLayout2.addView(v);
                        }
                        total_amount.setText(String.format("%.2f", total));
                    }
                });
    }
}