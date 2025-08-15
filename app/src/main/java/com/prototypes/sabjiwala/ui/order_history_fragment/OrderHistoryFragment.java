package com.prototypes.sabjiwala.ui.order_history_fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.prototypes.sabjiwala.MainActivity;
import com.prototypes.sabjiwala.R;
import com.prototypes.sabjiwala.cart_activity.CartActivity;
import com.prototypes.sabjiwala.classes.Order;
import com.prototypes.sabjiwala.classes.OrderHistoryLayout;
import com.prototypes.sabjiwala.classes.OrderedVegetable;
import com.prototypes.sabjiwala.classes.Shop;
import com.prototypes.sabjiwala.order_view.OrderViewActivity;
import com.prototypes.sabjiwala.shop_activity.DatabaseHelper;

import java.util.ArrayList;

public class OrderHistoryFragment extends Fragment {

    private static final String TAG = "OrderHistoryFragment";
    FirebaseFirestore fStore;
    RecyclerView recyclerView;
    FirebaseAuth fAuth;
    String userId;
    ArrayList<Order> orders;
    ArrayList<ArrayList<OrderedVegetable>> orderedVegetables;
    OrderHistoryAdapter adapter;
    String name;
    DatabaseHelper databaseHelper;
    ArrayList<OrderHistoryLayout> orderHistoryLayouts = new ArrayList<>();

    public OrderHistoryFragment() {
        // Required empty public constructor
    }

    public static OrderHistoryFragment newInstance() {
        OrderHistoryFragment fragment = new OrderHistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_view);
        ((MainActivity) getActivity()).setTitle("Order History");
    }

    @Override
    public void onStart() {
        super.onStart();
        getData();
    }

    @Override
    public void onResume() {
        super.onResume();
        //getData();
    }

    void getData() {
        fAuth = FirebaseAuth.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        orderedVegetables = new ArrayList<>();
        fStore = FirebaseFirestore.getInstance();
        orders = new ArrayList<>();
        orderHistoryLayouts = new ArrayList<>();
        fStore.collection("Orders")
                .whereEqualTo("customer_id", userId)
                .orderBy("order_date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            Order order = documentSnapshot.toObject(Order.class).withId(documentSnapshot.getId());
                            orders.add(order);
                            fStore.collection("Orders")
                                    .document(order.getId())
                                    .collection("order_items")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            name = "";
                                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                OrderedVegetable orderedVegetable = queryDocumentSnapshot.toObject(OrderedVegetable.class);
                                                name = orderedVegetable.getVegetable_name() + ", " + name;
                                            }
                                            String size = String.valueOf(task.getResult().size()) + " Items";
                                            OrderHistoryLayout orderHistoryLayout = new OrderHistoryLayout(name, size, order);
                                            orderHistoryLayouts.add(orderHistoryLayout);
                                            if (orderHistoryLayouts.size() == orders.size()) {
                                                setAdapter();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    void setAdapter() {
        adapter = new OrderHistoryAdapter(orderHistoryLayouts, new OrderHistoryAdapter.onReorderClickButtonListener() {
            @Override
            public void onClick(String path, int position, Order order) {
                fStore.collection("SabjiWale")
                        .document(order.getSabjiwala_id())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Shop shop = task.getResult().toObject(Shop.class);
                                if (shop.isShopStatus()) {
                                    databaseHelper = new DatabaseHelper(getContext(), path);
                                    databaseHelper.deleteAllData();
                                    fStore.collection("Orders")
                                            .document(order.getId())
                                            .collection("order_items")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                        OrderedVegetable vegetable = documentSnapshot.toObject(OrderedVegetable.class);
                                                        databaseHelper.addData(vegetable.getVegetable_id(), vegetable.getVegetable_category(),
                                                                vegetable.getVegetable_quantity());
                                                    }
                                                    Intent intent = new Intent(getActivity(), CartActivity.class);
                                                    intent.putExtra("TABLE_NAME", path);
                                                    startActivity(intent);
                                                }
                                            });
                                }else {
                                    Toast.makeText(getContext(), "Sorry, shop is not available now. Please try later", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

            @Override
            public void onOrderClicked(String path) {
                Intent intent = new Intent(getActivity(), OrderViewActivity.class);
                intent.putExtra("orderPath", path);
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
}