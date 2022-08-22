package com.com.bestlady.worker;

import android.os.AsyncTask;

import com.com.bestlady.dbutilities.AppDatabase;
import com.com.bestlady.model.Products;

import java.util.ArrayList;
import java.util.List;

public class SaveProduct extends AsyncTask<Void, Void, Void> {
    private AppDatabase db;
    private List<Products> fishDetails;
    public SaveProduct(AppDatabase db, List<Products> fishDetails) {
        this.db = db;
        this.fishDetails = fishDetails;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if(db!=null){
            if(fishDetails !=null && fishDetails.size()>0) {
                List<String> nameList = new ArrayList<>();
                for(int i = 0; i< fishDetails.size(); i++){
                    nameList.add(fishDetails.get(i).getName());
                    fishDetails.get(i).setQuantity(db.cartItemDao().getCartCount(fishDetails.get(i).getName()));
                }
                db.foodDetailsDao().save(fishDetails);
                db.foodDetailsDao().deleteOtherFoods(nameList);
            }else{
                db.foodDetailsDao().deleteAll();
            }
        }
        return null;
    }
}
