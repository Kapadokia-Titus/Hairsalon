package com.com.bestlady.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.com.bestlady.R;
import com.com.bestlady.dbutilities.AppDatabase;
import com.com.bestlady.model.Products;
import com.com.bestlady.ui.IndividualActivity;
import com.com.bestlady.ui.RatingTextView;

import java.util.List;

import static com.com.bestlady.ui.HomeScreenActivity.INTENT_UPDATE_FOOD;

public class SellersProductAdapter  extends RecyclerView.Adapter<SellersProductAdapter.RecyclerViewHolders> {

    private List<Products> foodList;
    private Handler handler;

    public void setData(List<Products> data) {
        this.foodList = data;
    }

    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tName,tPrice,tCount;
        private ImageView iFood;
        private AppCompatImageView iPlus,iMinus;
        private RelativeLayout foodCard;
        private RatingTextView tRating;


        RecyclerViewHolders(View itemView) {
            super(itemView);
            foodCard = itemView.findViewById(R.id.food_card);
            tName = foodCard.findViewById(R.id.t_food_name);
            tPrice = foodCard.findViewById(R.id.t_price);
            tCount = foodCard.findViewById(R.id.t_count);
            tRating = foodCard.findViewById(R.id.t_rating);
            iPlus = foodCard.findViewById(R.id.i_plus);
            iMinus = foodCard.findViewById(R.id.i_minus);
            iFood = foodCard.findViewById(R.id.i_food_image);
            foodCard.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view.getId()== R.id.food_card){
                AppDatabase.getDatabase(view.getContext()).foodDetailsDao().save(foodList.get(getAdapterPosition()));
                Intent i = new Intent(view.getContext(), IndividualActivity.class);
                i.putExtra("name",foodList.get(getAdapterPosition()).getName());
                view.getContext().startActivity(i);
            }
        }
    }

    private Intent getUpdateIntent(int position) {
        Intent i = new Intent(INTENT_UPDATE_FOOD);
        i.putExtra("position",position);
        return i;
    }

    public SellersProductAdapter(List<Products> listDetails) {
        this.foodList = listDetails;
        this.handler = new Handler();
    }

    @NonNull
    @Override
    public RecyclerViewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        @SuppressLint("InflateParams") View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_sellers_item, null);
        return new RecyclerViewHolders(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolders holder, int position) {
        final Products products = foodList.get(holder.getAdapterPosition());
        holder.tName.setText(products.getName());
        holder.tPrice.setText("Ksh. " + products.getPrice());
        LoadImage li = new LoadImage(holder.iFood,holder.getAdapterPosition());
        handler.post(li);
    }



    @Override
    public int getItemCount() {
        return this.foodList.size();
    }


    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public Products getItem(int position){
        if(position!= -1) {
            return foodList.get(position);
        }else {
            return null;
        }
    }

    private class LoadImage implements Runnable {
        ImageView imageView;
        int position;

        LoadImage(ImageView imageView, int adapterPosition) {
            this.imageView = imageView;
            this.position = adapterPosition;
        }

        @Override
        public void run() {
            Glide.with(imageView.getContext()).load(foodList.get(position).getImageUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(R.drawable.ic_food)
                    .into(imageView);

        }
    }
}