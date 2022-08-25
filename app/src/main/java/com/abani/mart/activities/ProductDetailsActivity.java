package com.abani.mart.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.abani.mart.R;
import com.abani.mart.databinding.ActivityProductDetailsBinding;
import com.abani.mart.models.ProductModel;
import com.abani.mart.utils.Api;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.hishd.tinycart.model.Cart;
import com.hishd.tinycart.util.TinyCartHelper;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductDetailsActivity extends AppCompatActivity {

    ActivityProductDetailsBinding binding;
    ProductModel currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String name = getIntent().getStringExtra("name");
        String image = getIntent().getStringExtra("image");
        double price = getIntent().getDoubleExtra("price",0);
        int id = getIntent().getIntExtra("id", 0);
        Glide.with(this).load(image).into(binding.productImg);


        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initProductDescription(id);
//Add To Cart Start
        Cart cart = TinyCartHelper.getCart();
        binding.addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cart.addItem(currentProduct, 1);
                binding.addToCartBtn.setEnabled(false);
                Toast.makeText(ProductDetailsActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();
            }
        });
//Add To Cart End
    }
    void initProductDescription(int id){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Api.GET_PRODUCT_DETAILS_URL + id;
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject mainObj = new JSONObject(response);
                if (mainObj.getString("status").equals("success")){
                    JSONObject object = mainObj.getJSONObject("product");
                    String description = object.getString("description");
                    binding.productDesc.setText(
                            Html.fromHtml(description)
                    );
                    currentProduct = new ProductModel(
                            object.getString("name"),
                            Api.PRODUCTS_IMAGE_URL+object.getString("image"),
                            object.getString("status"),
                            object.getDouble("price"),
                            object.getDouble("price_discount"),
                            object.getInt("id"),
                            object.getInt("stock")
                    );

                }else {
                    //Nothing
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {

        });
        queue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cart, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.cart){
            startActivity(new Intent(this, CartActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}