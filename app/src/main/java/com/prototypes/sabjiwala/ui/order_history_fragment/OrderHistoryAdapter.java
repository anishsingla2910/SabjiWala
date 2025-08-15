package com.prototypes.sabjiwala.ui.order_history_fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.prototypes.sabjiwala.R;
import com.prototypes.sabjiwala.classes.Order;
import com.prototypes.sabjiwala.classes.OrderHistoryLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.MyViewHolder> {

    onReorderClickButtonListener listener;
    ArrayList<OrderHistoryLayout> orderHistoryLayouts;


    public OrderHistoryAdapter(ArrayList<OrderHistoryLayout> orderHistoryLayouts, onReorderClickButtonListener listener){
        this.orderHistoryLayouts = orderHistoryLayouts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_history_layout, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.sabji_wala_address.setText(orderHistoryLayouts.get(position).getOrders().getCustomer_address());
        String size = orderHistoryLayouts.get(position).getVegetable_sizes();
        holder.no_of_items.setText(size);
        holder.names_of_vegetables.setText(orderHistoryLayouts.get(position).getVegetable_names());
        holder.order_amount.setText(orderHistoryLayouts.get(position).getOrders().getTotal_amount());
        Timestamp timestamp = orderHistoryLayouts.get(position).getOrders().getDeliver_date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        String date = simpleDateFormat.format(new Date(String.valueOf(timestamp.toDate())));
        holder.order_date.setText(date);
        holder.status.setText(orderHistoryLayouts.get(position).getOrders().getOrder_status());
    }

    @Override
    public int getItemCount() {
        return orderHistoryLayouts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView sabji_wala_address,no_of_items,names_of_vegetables,order_date,order_amount, status;
        Button reorder_button;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            sabji_wala_address = itemView.findViewById(R.id.sabji_wala_address);
            no_of_items = itemView.findViewById(R.id.no_of_items);
            names_of_vegetables = itemView.findViewById(R.id.names_of_vegetables);
            order_date = itemView.findViewById(R.id.order_date);
            order_amount = itemView.findViewById(R.id.order_amount);
            reorder_button = itemView.findViewById(R.id.reorder_button);
            status = itemView.findViewById(R.id.status);
            reorder_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION){
                        listener.onClick(orderHistoryLayouts.get(position).getOrders().getSabjiwala_id(), position,
                                orderHistoryLayouts.get(position).getOrders());
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION){
                        listener.onOrderClicked(orderHistoryLayouts.get(position).getOrders().getId());
                    }
                }
            });
        }
    }

    public interface onReorderClickButtonListener{
        void onClick(String path, int position, Order order);
        void onOrderClicked(String path);
    }
}
