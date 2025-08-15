package com.prototypes.sabjiwala.order_view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prototypes.sabjiwala.R;
import com.prototypes.sabjiwala.classes.Order;
import com.prototypes.sabjiwala.classes.OrderedVegetable;
import com.prototypes.sabjiwala.classes.Shop;

public class OrderViewActivity extends AppCompatActivity {

    String path;
    FirebaseFirestore fStore;
    TextView shop_name, owner_name, shop_address, delivery_address, order_status;
    TextView total_amount;
    float sub_total;
    float quantity;
    LinearLayout vegetables;
    StorageReference storageReference;
    Toolbar toolbar;
    FloatingActionButton call_button;
    Button cancel_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_view);

        Intent intent = getIntent();
        path = intent.getStringExtra("orderPath");

        cancel_button = findViewById(R.id.cancel_btn);
        cancel_button.setEnabled(false);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               fStore.collection("Orders")
                       .document(path)
                       .update("order_status", "Order Canceled")
                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               finish();
                           }
                       });
            }
        });
        shop_name = findViewById(R.id.shop_name);
        owner_name = findViewById(R.id.owner_name);
        call_button = findViewById(R.id.call_button);
        shop_address = findViewById(R.id.shop_address);
        delivery_address = findViewById(R.id.delivery_address);
        order_status = findViewById(R.id.order_status);
        total_amount = findViewById(R.id.total_amount);
        vegetables = findViewById(R.id.vegetables);
        storageReference = FirebaseStorage.getInstance().getReference();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fStore = FirebaseFirestore.getInstance();

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
                                        String shopName = shop.getShopName();
                                        String ownerName = shop.getOwnerName();
                                        String shopAddress = shop.getShopAddress();
                                        String deliverAddress = order.getCustomer_address();
                                        String orderStatus = order.getOrder_status();
                                        shop_name.setText(shopName);
                                        owner_name.setText(ownerName);
                                        shop_address.setText(shopAddress);
                                        delivery_address.setText(deliverAddress);
                                        order_status.setText(orderStatus);
                                        if (orderStatus.equals("new")) {
                                            cancel_button.setEnabled(true);
                                        }
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
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            OrderedVegetable orderedVegetable = documentSnapshot.toObject(OrderedVegetable.class);
                            View view = getLayoutInflater().inflate(R.layout.order_view_layout, null);
                            ImageView vegetable_image = view.findViewById(R.id.vegetable_image);
                            StorageReference vegetableImage = storageReference.child("MasterList/" + orderedVegetable.getVegetable_name() + orderedVegetable.getVegetable_type() + ".png");
                            Glide.with(vegetable_image)
                                    .load(vegetableImage)
                                    .error(R.drawable.vegetable_image_not_available)
                                    .into(vegetable_image);
                            TextView vegetable_name = view.findViewById(R.id.vegetable_name);
                            TextView vegetable_category = view.findViewById(R.id.vegetable_category);
                            TextView vegetable_type = view.findViewById(R.id.vegetable_type);
                            TextView vegetable_weight = view.findViewById(R.id.vegetable_weight);
                            TextView vegetable_price = view.findViewById(R.id.vegetable_price);
                            String vegetableName = orderedVegetable.getVegetable_name();
                            String vegetablePrice = orderedVegetable.getVegetable_price() + " " + orderedVegetable.getVegetable_price_per_unit();
                            String actualQuantity = orderedVegetable.getVegetable_quantity();
                            String vegetableType = orderedVegetable.getVegetable_type();
                            String vegetableCategory = orderedVegetable.getVegetable_category();
                            float amount = 0f;
                            switch (orderedVegetable.getVegetable_price_per_unit()){
                                case "Per Kg": {
                                    String[] split = orderedVegetable.getVegetable_quantity().split(" ");
                                    if (split[1].equals("gram")) {
                                        amount = Float.parseFloat(orderedVegetable.getVegetable_price()) * Float.parseFloat(split[0]) / 1000f;
                                    }else {
                                        amount = Float.parseFloat(orderedVegetable.getVegetable_price()) * Float.parseFloat(split[0]);
                                    }
                                    sub_total = sub_total + amount;
                                    break;
                                }
                                case "Per Dozen": {
                                    String[] split = orderedVegetable.getVegetable_quantity().split(" ");
                                    amount = Float.parseFloat(orderedVegetable.getVegetable_price()) * Float.parseFloat(split[0])/12f;
                                    sub_total = sub_total + amount;
                                    break;
                                }
                                case "Per Piece": {
                                    String[] split = orderedVegetable.getVegetable_quantity().split(" ");
                                    amount = Float.parseFloat(orderedVegetable.getVegetable_price()) * Float.parseFloat(split[0]);
                                    sub_total = sub_total + amount;
                                    break;
                                }
                                case "Per 100 gram": {
                                    String[] split = orderedVegetable.getVegetable_quantity().split(" ");
                                    amount = Float.parseFloat(orderedVegetable.getVegetable_price()) * Float.parseFloat(split[0])/100f;
                                    sub_total = sub_total + amount;
                                    break;
                                }
                            }
                            vegetable_name.setText(vegetableName);
                            vegetable_price.setText(vegetablePrice);
                            vegetable_category.setText(vegetableCategory);
                            vegetable_type.setText(vegetableType);
                            vegetable_weight.setText(actualQuantity);
                            vegetables.addView(view);
                        }
                        String totalAmount = String.format("%.2f", sub_total);
                        total_amount.setText(totalAmount);
                    }
                });
    }
}