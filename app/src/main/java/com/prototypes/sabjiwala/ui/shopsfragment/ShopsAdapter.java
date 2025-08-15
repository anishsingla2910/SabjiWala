package com.prototypes.sabjiwala.ui.shopsfragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prototypes.sabjiwala.R;
import com.prototypes.sabjiwala.classes.Shop;
import com.prototypes.sabjiwala.classes.ShopId;

import java.util.ArrayList;

public class ShopsAdapter extends RecyclerView.Adapter<ShopsAdapter.ViewHolder> {

    ArrayList<Shop> shops;
    StorageReference storageReference;
    onItemClickListener listener;
    ArrayList<ShopId> shopIds;

    public ShopsAdapter(ArrayList<Shop> shop, ArrayList<ShopId> shopIds) {
        this.shops = shop;
        this.shopIds = shopIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_adapter_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.shop_name.setText(shops.get(position).getShopName());
        holder.shop_address.setText(shops.get(position).getShopAddress());
        if (shops.get(position).isShopStatus()){
            holder.shop_not_available.setVisibility(View.GONE);
        }else {
            holder.shop_not_available.setVisibility(View.VISIBLE);
        }
        holder.owner_name.setText(shops.get(position).getOwnerName());
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference ownerImage = storageReference.child("shop_owner_pictures/" + shops.get(position).getId());
        Glide.with(holder.owner_image)
                .load(ownerImage)
                .error(R.drawable.default_profile)
                .into(holder.owner_image);
        StorageReference shopImage = storageReference.child("shoppictures/" + shops.get(position).getId());
        Glide.with(holder.shop_image)
                .load(shopImage)
                .error(R.drawable.shop_image_not_available)
                .into(holder.shop_image);
        for (ShopId shopId : shopIds){
            if (shopId.getID().equals(shops.get(position).getId())){
                holder.image_button_like.setImageResource(R.drawable.ic_favourite);
                return;
            }
        }
        holder.image_button_like.setImageResource(R.drawable.ic_unfavourite);
    }

    @Override
    public int getItemCount() {
        return shops.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView owner_image, shop_image;
        TextView shop_name, owner_name, shop_address, shop_not_available;
        RelativeLayout relativeLayout;
        ImageButton image_button_like;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            shop_not_available = itemView.findViewById(R.id.shop_not_availbale);
            owner_image = itemView.findViewById(R.id.owner_image);
            shop_image = itemView.findViewById(R.id.shop_image);
            shop_name = itemView.findViewById(R.id.shop_name);
            owner_name = itemView.findViewById(R.id.owner_name);
            shop_address = itemView.findViewById(R.id.shop_address);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
            image_button_like = itemView.findViewById(R.id.image_button_like);
            image_button_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onLikeButtonClicked(shops.get(position));
                        shops.remove(shops.get(position));
                        notifyDataSetChanged();
                    }
                }
            });
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClicked(position);
                    }
                }
            });
        }
    }

    public interface onItemClickListener {
        void onItemClicked(int position);
        void onLikeButtonClicked(Shop shop);
    }

    public void onItemClick(onItemClickListener listener) {
        this.listener = listener;
    }
}
