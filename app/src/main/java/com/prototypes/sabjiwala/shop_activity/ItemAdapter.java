package com.prototypes.sabjiwala.shop_activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.prototypes.sabjiwala.R;
import com.prototypes.sabjiwala.classes.OrderVegetable;
import com.prototypes.sabjiwala.classes.Vegetable;

import java.util.ArrayList;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    Context context;
    ArrayList<Vegetable> vegetables;
    onItemClickListener listener;
    ArrayList<OrderVegetable> orderVegetables;
    ArrayAdapter<String> adapter;
    StorageReference storageReference;
    
    public ItemAdapter(ArrayList<Vegetable> vegetable, ArrayList<OrderVegetable> orderVegetables, Context context1) {
        this.vegetables = vegetable;
        this.orderVegetables = orderVegetables;
        this.context = context1;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(v);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference vegetableImage = storageReference.child("MasterList/" + vegetables.get(position).getName() +
                vegetables.get(position).getType() + ".png");
        Glide.with(holder.item_image)
                .load(vegetableImage)
                .error(R.drawable.vegetable_image_not_available)
                .into(holder.item_image);
        holder.item_name.setText(vegetables.get(position).getName());
        holder.item_type.setText(vegetables.get(position).getType());
        holder.item_price.setText(vegetables.get(position).getPrice());
        holder.item_price_unit.setText(vegetables.get(position).getUnit());
        List<String> qty = vegetables.get(position).getQuantity();
        adapter = new ArrayAdapter<>(holder.quantity.getContext(), R.layout.spinner_layout, R.id.text, qty);
        holder.quantity.setAdapter(adapter);
        for (OrderVegetable orderVegetable : orderVegetables) {
            if (orderVegetable.getID().equals(vegetables.get(position).getId())) {
                holder.quantity.setSelection(adapter.getPosition(orderVegetable.getQUANTITY()));
                holder.add_to_cart_button.setVisibility(View.INVISIBLE);
                holder.add_to_cart_button.setEnabled(false);
                holder.remove_from_cart.setEnabled(true);
                holder.remove_from_cart.setVisibility(View.VISIBLE);
                return;
            }
        }
        holder.remove_from_cart.setEnabled(false);
        holder.remove_from_cart.setVisibility(View.GONE);
        holder.add_to_cart_button.setVisibility(View.VISIBLE);
        holder.add_to_cart_button.setEnabled(true);
    }
    
    @Override
    public int getItemCount() {
        return vegetables.size();
    }
    
    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        this.listener = onItemClickListener;
    }
    
    public interface onItemClickListener {
        void onItemClicked(Vegetable vegetable, int position, String Quantity);
        
        void onSpinnerClicked(Vegetable vegetable, String quantity);
        
        void onItemRemoved(String id);
    }
    
    class ViewHolder extends RecyclerView.ViewHolder {
        
        TextView item_name, item_type, item_description, item_price, item_price_unit;
        ImageView item_image;
        Spinner quantity;
        Button add_to_cart_button, remove_from_cart;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.item_name);
            item_type = itemView.findViewById(R.id.item_type);
            item_description = itemView.findViewById(R.id.item_description);
            item_price = itemView.findViewById(R.id.item_price);
            item_price_unit = itemView.findViewById(R.id.item_price_unit);
            item_image = itemView.findViewById(R.id.item_image);
            remove_from_cart = itemView.findViewById(R.id.remove_from_cart);
            quantity = itemView.findViewById(R.id.spinner);
            add_to_cart_button = itemView.findViewById(R.id.add_to_cart_button);
            remove_from_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Remove from cart");
                    builder.setMessage("Are you sure you want to remove this item from your cart?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION && listener != null) {
                                listener.onItemRemoved(vegetables.get(position).getId());
                            }
                            for (Vegetable vegetable : vegetables) {
                                for (OrderVegetable orderVegetable : orderVegetables) {
                                    if (orderVegetable.getID().equals(vegetable.getId())) {
                                        orderVegetables.remove(orderVegetable);
                                        notifyDataSetChanged();
                                    }
                                }
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
            add_to_cart_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClicked(vegetables.get(position), position, quantity.getSelectedItem().toString());
                        OrderVegetable orderVegetable = new OrderVegetable(vegetables.get(position).getId(),
                                vegetables.get(position).getCategory(),
                                quantity.getSelectedItem().toString());
                        orderVegetables.add(orderVegetable);
                        notifyDataSetChanged();
                    }
                }
            });
            quantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    int positionId = getAdapterPosition();
                    if (positionId != RecyclerView.NO_POSITION && listener != null) {
                        for (OrderVegetable orderVegetable : orderVegetables) {
                            if (vegetables.get(positionId).getId().equals(orderVegetable.getID())) {
                                listener.onSpinnerClicked(vegetables.get(positionId), quantity.getSelectedItem().toString());
                            }
                        }
                    }
                }
                
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                
                }
            });
        }
    }
}
