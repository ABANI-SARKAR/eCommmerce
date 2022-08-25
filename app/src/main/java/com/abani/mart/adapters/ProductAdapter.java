package com.abani.mart.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abani.mart.R;
import com.abani.mart.activities.ProductDetailsActivity;
import com.abani.mart.databinding.SampleProductBinding;
import com.abani.mart.models.ProductModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    Context context;
    ArrayList<ProductModel> products;

    public ProductAdapter(Context context, ArrayList<ProductModel> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.sample_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ViewHolder holder, int position) {
        ProductModel product = products.get(position);
        Glide.with(context).load(product.getImage()).into(holder.binding.image);
        holder.binding.label.setText(product.getName());
        holder.binding.price.setText("INR " + product.getPrice());

       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(context, ProductDetailsActivity.class);
               intent.putExtra("name", product.getName());
               intent.putExtra("image", product.getImage());
               intent.putExtra("price", product.getPrice());
               intent.putExtra("id", product.getId());
               context.startActivity(intent);
           }
       });
    }
    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        SampleProductBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SampleProductBinding.bind(itemView);
        }
    }
}
