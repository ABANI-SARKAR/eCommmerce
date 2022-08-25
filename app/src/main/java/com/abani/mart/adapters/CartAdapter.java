package com.abani.mart.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abani.mart.R;
import com.abani.mart.databinding.QuantityDialogBinding;
import com.abani.mart.databinding.SampleCartBinding;
import com.abani.mart.models.ProductModel;
import com.bumptech.glide.Glide;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    Context context;
    ArrayList<ProductModel>products;
    CartListener cartListener;
    Cart cart;


    public interface CartListener {
        public void onQuantityChanged();
    }

    public CartAdapter(Context context, ArrayList<ProductModel> products, CartListener cartListener) {
        this.context = context;
        this.products = products;
        this.cartListener = cartListener;
        cart = TinyCartHelper.getCart();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.sample_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel product = products.get(position);
        holder.binding.name.setText(product.getName());
        holder.binding.price.setText("INR " + product.getPrice());
        holder.binding.quantity.setText(product.getQuantity() + " item(s)");
        Glide.with(context).load(product.getImage()).into(holder.binding.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                QuantityDialogBinding dialogBinding = QuantityDialogBinding.inflate(LayoutInflater.from(context));
                AlertDialog dialog = new AlertDialog.Builder(context).setView(dialogBinding.getRoot()).create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));

                dialogBinding.productName.setText(product.getName());
                dialogBinding.productStock.setText("Stock: " + product.getStock());
                dialogBinding.quantity.setText(String.valueOf(product.getQuantity()));

                int stock = product.getStock();


                dialogBinding.minusBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int quantity = product.getQuantity();
                        if (quantity >1)
                        quantity--;
                        product.setQuantity(quantity);
                        dialogBinding.quantity.setText(String.valueOf(quantity));

                        notifyDataSetChanged();
                        cart.updateItem(product, product.getQuantity());
                        cartListener.onQuantityChanged();
                    }
                });
                dialogBinding.plusBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int quantity = product.getQuantity();
                        quantity++;
                        if (quantity>product.getStock()) {
                            Toast.makeText(context, "Maximum Stock Available :" + product.getStock(), Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            product.setQuantity(quantity);
                            dialogBinding.quantity.setText(String.valueOf(quantity));
                        }
                        notifyDataSetChanged();
                        cart.updateItem(product, product.getQuantity());
                        cartListener.onQuantityChanged();
                    }
                });
                dialogBinding.saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
//                        notifyDataSetChanged();
//                        cart.updateItem(product, product.getQuantity());
//                        cartListener.onQuantityChanged();
                    }
                });
                dialog.show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return products.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        SampleCartBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SampleCartBinding.bind(itemView);
        }
    }
}
