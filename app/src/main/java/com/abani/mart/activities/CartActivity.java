package com.abani.mart.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.abani.mart.adapters.CartAdapter;
import com.abani.mart.databinding.ActivityCartBinding;
import com.abani.mart.models.ProductModel;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.model.Item;
import com.hishd.tinycart.util.TinyCartHelper;

import java.util.ArrayList;
import java.util.Map;

public class CartActivity extends AppCompatActivity {
    ActivityCartBinding binding;

    ///Add to Cart Part
    ArrayList<ProductModel> product;
    CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        product = new ArrayList<>();
        Cart cart = TinyCartHelper.getCart();
        for (Map.Entry<Item, Integer> item : cart.getAllItemsWithQty().entrySet()){
            ProductModel productI = (ProductModel) item.getKey();
            int quantity = item.getValue();
            productI.setQuantity(quantity);
            product.add(productI);
            binding.subTotal.setText(String.format("INR % .2f", cart.getTotalPrice()));
        }

        adapter = new CartAdapter(this, product, new CartAdapter.CartListener() {
            @Override
            public void onQuantityChanged() {
                binding.subTotal.setText(String.format("INR % .2f", cart.getTotalPrice()));
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.cartList.setLayoutManager(layoutManager);
        DividerItemDecoration decoration = new DividerItemDecoration(this, layoutManager.getOrientation());
        binding.cartList.addItemDecoration(decoration);
        binding.cartList.setAdapter(adapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CheckoutActivity.class));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}