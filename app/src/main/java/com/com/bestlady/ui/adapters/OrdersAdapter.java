package com.com.bestlady.ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.com.bestlady.R;
import com.com.bestlady.model.Orders;
import com.com.bestlady.viewmodel.OrdersViewModel;

import java.util.ArrayList;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.RecyclerViewHolders> {
    private List<Orders> ordersList;
    private OrdersViewModel ordersViewModel;
    public OrdersAdapter(ArrayList<Orders> ordersList, OrdersViewModel ordersViewModel) {
        this.ordersList = ordersList;
        this.ordersViewModel = ordersViewModel;
    }
    public void setData(List<Orders> data) {
        this.ordersList = data;
    }

    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        private AppCompatImageView iDelete;
        private TextView tName,tPrice,tTotalPrice,tQuantity;


        RecyclerViewHolders(View itemView) {
            super(itemView);

            iDelete = itemView.findViewById(R.id.i_delete);
            tName = itemView.findViewById(R.id.t_name);
            tPrice = itemView.findViewById(R.id.t_price);
            tTotalPrice = itemView.findViewById(R.id.t_total_price);
            tQuantity = itemView.findViewById(R.id.t_quantity);
            iDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view.getId()==R.id.i_delete){
                // handle cancellation of an order
            }
        }
    }

    @NonNull
    @Override
    public RecyclerViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        @SuppressLint("InflateParams") View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cart_item, null);
        return new RecyclerViewHolders(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolders holder, int position) {
        Orders orders = ordersList.get(holder.getAdapterPosition());
        holder.tName.setText(orders.getName());
        holder.tPrice.setText("Ksh. "+orders.getPrice());
        holder.tQuantity.setText(String.valueOf(orders.getQuantity()));
        Double quantity = Double.parseDouble(orders.getQuantity());
        Double price= Double.parseDouble(orders.getPrice());
        holder.tTotalPrice.setText("Ksh. "+ String.valueOf(quantity*price));
    }


    @Override
    public int getItemCount() {
        return this.ordersList.size();
    }


    public long getItemId(int position) {
        return super.getItemId(position);
    }

}
