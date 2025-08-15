package com.prototypes.sabjiwala.ui.shopsfragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prototypes.sabjiwala.MainActivity;
import com.prototypes.sabjiwala.R;
import com.prototypes.sabjiwala.choose_package.ChooseSabjiWalaActivity;
import com.prototypes.sabjiwala.classes.Shop;
import com.prototypes.sabjiwala.classes.ShopId;
import com.prototypes.sabjiwala.database_classes.LikedShopDatabaseHelper;
import com.prototypes.sabjiwala.shop_activity.ShopActivity;

import java.util.ArrayList;

public class ShopsFragment extends Fragment {

    FirebaseFirestore fStore;
    ArrayList<Shop> shops;
    RecyclerView recyclerView;
    ShopsAdapter adapter;
    FloatingActionButton floatingActionButton;
    LikedShopDatabaseHelper databaseHelper;
    ArrayList<ShopId> shopIds;
    RelativeLayout relativeLayout;

    public ShopsFragment() {

    }

    public static ShopsFragment newInstance() {
        ShopsFragment fragment = new ShopsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shops, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fStore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.recyclerView);
        databaseHelper = new LikedShopDatabaseHelper(getActivity());
        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        relativeLayout = view.findViewById(R.id.relativeLayout);
        relativeLayout.setEnabled(false);
        relativeLayout.setVisibility(View.GONE);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChooseSabjiWalaActivity.class);
                startActivity(intent);
            }
        });
        ((MainActivity)getActivity()).setTitle("Home");
        /*int progress = databaseHelper.deleteData("m0");
        if (progress > 0){
            Toast.makeText(getActivity(), "Success data deleted", Toast.LENGTH_SHORT).show();
        }*/

    }

    @Override
    public void onStart() {
        super.onStart();
        shops = new ArrayList<>();
        getAllData();
        for (ShopId id : shopIds) {
            fStore.collection("SabjiWale")
                    .document(id.getID())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot value = task.getResult();
                            Shop shop = value.toObject(Shop.class).withId(value.getId());
                            shops.add(shop);

                            adapter = new ShopsAdapter(shops, shopIds);
                            adapter.onItemClick(new ShopsAdapter.onItemClickListener() {
                                @Override
                                public void onItemClicked(int position) {
                                    String path = shops.get(position).id;
                                    if (shops.get(position).isShopStatus()) {
                                        Intent intent = new Intent(getActivity(), ShopActivity.class);
                                        intent.putExtra("path", path);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onLikeButtonClicked(Shop shop) {
                                    getAllData();
                                    for (ShopId shopId : shopIds) {
                                        if (shopId.getID().equals(shop.getId())) {
                                            int progress = databaseHelper.deleteData(shop.getId());
                                            shopIds.remove(shopId);
                                            checkFilter();
                                            return;
                                        }
                                    }
                                    boolean progress = databaseHelper.addData(shop.getId());
                                }
                            });
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.setAdapter(adapter);
                        }
                    });
        }
        if (shopIds.isEmpty()){
            relativeLayout.setEnabled(true);
            relativeLayout.setVisibility(View.VISIBLE);
        }
    }
    
    private void checkFilter() {
        if (shopIds.isEmpty()){
            relativeLayout.setEnabled(true);
            relativeLayout.setVisibility(View.VISIBLE);
        }
    }
    
    public void getAllData() {
        shopIds = new ArrayList<>();
        Cursor c = databaseHelper.getAllData();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            ShopId shopId = new ShopId(c.getString(c.getColumnIndex(databaseHelper.Col1)));
            shopIds.add(shopId);
            c.moveToNext();
        }
    }
}