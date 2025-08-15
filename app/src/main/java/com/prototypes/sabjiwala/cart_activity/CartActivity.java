package com.prototypes.sabjiwala.cart_activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prototypes.sabjiwala.R;
import com.prototypes.sabjiwala.classes.Customer;
import com.prototypes.sabjiwala.classes.OrderVegetable;
import com.prototypes.sabjiwala.classes.Shop;
import com.prototypes.sabjiwala.classes.Vegetable;
import com.prototypes.sabjiwala.order_confirmation.OrderConfirmationActivity;
import com.prototypes.sabjiwala.shop_activity.DatabaseHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {
    
    int i = 0;
    String path;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    DatabaseHelper databaseHelper;
    ArrayList<OrderVegetable> orderVegetables;
    LinearLayout linearLayout;
    StorageReference storageReference;
    Toolbar toolbar;
    TextView shop_name, owner_name, address;
    TextView sub_total;
    MaterialCardView cardView;
    float subTotal;
    RelativeLayout relativeLayout;
    Button explore_menu_btn;
    ArrayList<Vegetable> vegetables;
    ArrayList<Float> prices;
    TextView customer_address;
    MaterialTextView change_address_btn;
    MaterialCardView material_card_view;
    Button place_order_btn;
    RelativeLayout relative_layout;
    String userId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        
        Intent intent = getIntent();
        path = intent.getStringExtra("TABLE_NAME");
        
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        linearLayout = findViewById(R.id.cart_items_linear_layout);
        prices = new ArrayList<>();
        databaseHelper = new DatabaseHelper(CartActivity.this, path);
        orderVegetables = new ArrayList<>();
        storageReference = FirebaseStorage.getInstance().getReference();
        toolbar = findViewById(R.id.toolbar);
        shop_name = findViewById(R.id.shop_name);
        owner_name = findViewById(R.id.owner_name);
        relative_layout = findViewById(R.id.relativeLayout1);
        address = findViewById(R.id.address);
        cardView = findViewById(R.id.cardView);
        sub_total = findViewById(R.id.sub_total);
        vegetables = new ArrayList<>();
        customer_address = findViewById(R.id.customer_address);
        change_address_btn = findViewById(R.id.change_address_btn);
        userId = fAuth.getCurrentUser().getUid();
        place_order_btn = findViewById(R.id.place_order_btn);
        material_card_view = findViewById(R.id.material_card_view);
        
        explore_menu_btn = findViewById(R.id.explore_menu_btn);
        relativeLayout = findViewById(R.id.relativeLayout);
        relativeLayout.setVisibility(View.INVISIBLE);
        relativeLayout.setEnabled(false);
        
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        explore_menu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setTitle("My Cart");
        Cursor cursor = databaseHelper.getAllData();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            OrderVegetable o = new OrderVegetable(cursor.getString(cursor.getColumnIndex(databaseHelper.Col1)),
                    cursor.getString(cursor.getColumnIndex(databaseHelper.Col2)),
                    cursor.getString(cursor.getColumnIndex(databaseHelper.Col3)));
            orderVegetables.add(o); //add the item
            cursor.moveToNext();
        }
        
        place_order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fStore.collection("SabjiWale")
                        .document(path)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> tas) {
                                Shop shop = tas.getResult().toObject(Shop.class).withId(tas.getResult().getId());
                                fStore.collection("Customers")
                                        .document(userId)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                Customer customer = task.getResult().toObject(Customer.class);
                                                Date date = Calendar.getInstance().getTime();
                                                Timestamp timestamp = new Timestamp(date);
                                                Map<String, Object> map = new HashMap<>();
                                                map.put("customer_address", customer_address.getText().toString());
                                                map.put("customer_id", userId);
                                                map.put("customer_name", customer.getName());
                                                map.put("customer_phone_number", fAuth.getCurrentUser().getPhoneNumber());
                                                map.put("deliver_date", timestamp);
                                                map.put("order_date", timestamp);
                                                map.put("order_status", "new");
                                                map.put("payment_mode", "COD");
                                                map.put("payment_status", "not paid");
                                                map.put("sabjiwala_id", path);
                                                map.put("total_amount", sub_total.getText().toString());
                                                fStore.collection("Orders")
                                                        .add(map)
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                String id = task.getResult().getId();
                                                                for (OrderVegetable orderVegetable : orderVegetables) {
                                                                    fStore.collection("SabjiWale")
                                                                            .document(path)
                                                                            .collection("ProductsCategories")
                                                                            .document(orderVegetable.getCATEGORY())
                                                                            .collection("Products")
                                                                            .document(orderVegetable.getID())
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<DocumentSnapshot> tasks) {
                                                                                    if (tasks.isSuccessful()) {
                                                                                        Vegetable vegetable = tasks.getResult().toObject(Vegetable.class);
                                                                                        Map<String, Object> map = new HashMap<>();
                                                                                        map.put("vegetable_name", vegetable.getName());
                                                                                        map.put("vegetable_quantity", orderVegetable.getQUANTITY());
                                                                                        map.put("vegetable_category", vegetable.getCategory());
                                                                                        map.put("vegetable_price", vegetable.getPrice());
                                                                                        map.put("vegetable_price_per_unit", vegetable.getUnit());
                                                                                        map.put("vegetable_type", vegetable.getType());
                                                                                        map.put("vegetable_id", orderVegetable.getID());
                                                                                        map.put("vegetable_name_hindi", vegetable.getName_hindi());
                                                                                        map.put("vegetable_type_hindi", vegetable.getType_hindi());
                                                                                        map.put("vegetable_price_per_unit_hindi", vegetable.getUnit_hindi());
                                                                                        fStore.collection("Orders")
                                                                                                .document(id)
                                                                                                .collection("order_items")
                                                                                                .add(map)
                                                                                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                                                        databaseHelper.deleteAllData();
                                                                                                        Intent intent = new Intent(CartActivity.this, OrderConfirmationActivity.class);
                                                                                                        intent.putExtra("path", id);
                                                                                                        startActivity(intent);
                                                                                                        finish();
                                                                                                    }
                                                                                                });
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                                Toast.makeText(CartActivity.this, "Order placed", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        });
                            }
                        });
            }
        });
        
        change_address_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(CartActivity.this);
                alertDialog.setTitle("Change address");
                alertDialog.setMessage("Change your address");
                EditText editText = new EditText(CartActivity.this);
                editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                editText.setLines(2);
                editText.setHint("Address");
                TextInputLayout inputLayout = new TextInputLayout(CartActivity.this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(20, 0, 20, 0);
                inputLayout.setLayoutParams(layoutParams);
                inputLayout.addView(editText);
                alertDialog.setView(inputLayout);
                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newAddress = editText.getText().toString();
                        fStore.collection("Customers")
                                .document(userId)
                                .update("address", newAddress);
                        customer_address.setText(newAddress);
                    }
                });
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });
        
        for (OrderVegetable orderVegetable : orderVegetables) {
            fStore.collection("SabjiWale")
                    .document(path)
                    .collection("ProductsCategories")
                    .document(orderVegetable.getCATEGORY())
                    .collection("Products")
                    .document(orderVegetable.getID())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Vegetable vegetable = task.getResult().toObject(Vegetable.class).withId(task.getResult().getId());
                            vegetables.add(vegetable);
                            onStart();
                        }
                    });
        }
        
        fStore.collection("Customers")
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        Customer customer = documentSnapshot.toObject(Customer.class);
                        String customerAddress = customer.getAddress();
                        customer_address.setText(customerAddress);
                    }
                });
        
        fStore.collection("SabjiWale")
                .document(path)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Shop shop = task.getResult().toObject(Shop.class).withId(task.getResult().getId());
                            String shopName = shop.getShopName();
                            String ownerName = shop.getOwnerName();
                            String Address = shop.getShopAddress();
                            address.setText(Address);
                            shop_name.setText(shopName);
                            owner_name.setText(ownerName);
                        }
                    }
                });
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        linearLayout.removeAllViews();
        subTotal = 0f;
        prices = new ArrayList<>();
        if (subTotal == 0f) {
            sub_total.setText("₹0.0");
        }
        for (OrderVegetable orderVegetable : orderVegetables) {
            for (Vegetable vegetable : vegetables) {
                if (vegetable.getId().equals(orderVegetable.getID())) {
                    displaySubTotal(vegetable, orderVegetable);
                    String total = "₹" + String.format("%.2f", subTotal);
                    sub_total.setText(total);
                    View view = getLayoutInflater().inflate(R.layout.cart_item, null);
                    String itemName = vegetable.getName();
                    String itemType = vegetable.getType();
                    String itemPriceUnit = vegetable.getUnit();
                    Button remove_from_cart = view.findViewById(R.id.remove_from_cart);
                    String itemPrice = vegetable.getPrice();
                    StorageReference vegetableImage = storageReference.child("MasterList/" + vegetable.getName() + vegetable.getType() + ".png");
                    ImageView item_image = view.findViewById(R.id.item_image);
                    Glide.with(item_image)
                            .load(vegetableImage)
                            .error(R.drawable.vegetable_image_not_available)
                            .into(item_image);
                    TextView item_name = view.findViewById(R.id.item_name);
                    TextView item_type = view.findViewById(R.id.item_type);
                    TextView item_price = view.findViewById(R.id.item_price);
                    TextView item_price_unit = view.findViewById(R.id.item_price_unit);
                    ImageButton drop_down_arrow = view.findViewById(R.id.drop_down_arrow);
                    TextView text_quantity = view.findViewById(R.id.text_quantity);
                    text_quantity.setText(orderVegetable.getQUANTITY());
                    i++;
                    remove_from_cart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                            builder.setTitle("Remove from cart");
                            builder.setMessage("Are you sure you want to remove this item from your cart?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    databaseHelper.deleteData(vegetable.getId());
                                    linearLayout.removeView(view);
                                    orderVegetables.remove(orderVegetable);
                                    i--;
                                    if (i <= 0) {
                                        cardView.setVisibility(View.INVISIBLE);
                                        relativeLayout.setEnabled(true);
                                        relativeLayout.setVisibility(View.VISIBLE);
                                        material_card_view.setEnabled(false);
                                        material_card_view.setVisibility(View.INVISIBLE);
                                        relative_layout.setEnabled(false);
                                        relative_layout.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.show();
                        }
                    });
                    drop_down_arrow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(CartActivity.this);
                            alertDialog.setTitle("Select the quantity");
                            List<String> arrayList = vegetable.getQuantity();
                            LinearLayout linearLayout = new LinearLayout(CartActivity.this);
                            linearLayout.setOrientation(LinearLayout.VERTICAL);
                            alertDialog.setView(linearLayout);
                            AlertDialog dialog = alertDialog.show();
                            for (String quantity : arrayList) {
                                Button button = new Button(CartActivity.this);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                                params.setMargins(20, 20, 20, 20);
                                button.setLayoutParams(params);
                                button.setText(quantity);
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        databaseHelper.updateData(vegetable.getId(), vegetable.getCategory(),
                                                button.getText().toString());
                                        text_quantity.setText(button.getText().toString());
                                        OrderVegetable orderVegetable1 = new OrderVegetable(orderVegetable.getID(),
                                                orderVegetable.getCATEGORY(), button.getText().toString());
                                        orderVegetables.remove(orderVegetable);
                                        orderVegetables.add(orderVegetable1);
                                        dialog.cancel();
                                        onStart();
                                    }
                                });
                                linearLayout.addView(button);
                            }
                        }
                    });
                    item_name.setText(itemName);
                    item_type.setText(itemType);
                    item_price.setText(itemPrice);
                    item_price_unit.setText(itemPriceUnit);
                    linearLayout.addView(view);
                }
            }
        }
        if (orderVegetables.size() <= 0) {
            cardView.setVisibility(View.INVISIBLE);
            relativeLayout.setEnabled(true);
            relativeLayout.setVisibility(View.VISIBLE);
            material_card_view.setEnabled(false);
            material_card_view.setVisibility(View.INVISIBLE);
            relative_layout.setEnabled(false);
            relative_layout.setVisibility(View.INVISIBLE);
        }
    }
    
    public void displaySubTotal(@NotNull Vegetable vegetable, @NotNull OrderVegetable orderVegetable) {
        float totalPrice = 0;
        switch (vegetable.getUnit()) {
            case "Per Kg": {
                int vegetablePrice = Integer.parseInt(vegetable.getPrice());
                String[] split = orderVegetable.getQUANTITY().split(" ");
                if (split[1].equals("gram")) {
                    totalPrice = vegetablePrice * Float.parseFloat(split[0]) / 1000f;
                } else {
                    totalPrice = vegetablePrice * Float.parseFloat(split[0]);
                }
                break;
            }
            case "Per Dozen": {
                int vegetablePrice = Integer.parseInt(vegetable.getPrice());
                String[] split = orderVegetable.getQUANTITY().split(" ");
                totalPrice = vegetablePrice * Float.parseFloat(split[0]) / 12f;
                break;
            }
            case "Per Piece": {
                int vegetablePrice = Integer.parseInt(vegetable.getPrice());
                String[] split = orderVegetable.getQUANTITY().split(" ");
                totalPrice = vegetablePrice + Float.parseFloat(split[0]);
                break;
            }
            case "Per 100 gram": {
                int vegetablePrice = Integer.parseInt(vegetable.getPrice());
                String[] split = orderVegetable.getQUANTITY().split(" ");
                totalPrice = vegetablePrice + Float.parseFloat(split[0]) / 100f;
                break;
            }
        }
        subTotal += totalPrice;
        prices.add(totalPrice);
    }
}