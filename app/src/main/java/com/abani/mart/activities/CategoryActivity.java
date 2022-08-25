package com.abani.mart.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;


import com.abani.mart.adapters.ProductAdapter;
import com.abani.mart.databinding.ActivityCategoryBinding;
import com.abani.mart.models.ProductModel;
import com.abani.mart.utils.Api;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {
    ActivityCategoryBinding binding;

    ////////Product Part same to Category wise product
    private ArrayList<ProductModel> products;
    private ProductAdapter productAdapter;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,"ca-app-pub-9664160363258642/5876416431", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        binding.adView2.loadAd(adRequest);
                        mInterstitialAd.show(CategoryActivity.this);
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                        binding.adView2.loadAd(adRequest);

                    }
                });


        int categoryId = getIntent().getIntExtra("categoryId", 0);
        String categoryName = getIntent().getStringExtra("categoryName");
        getSupportActionBar().setTitle(categoryName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getProducts(categoryId);
    }
    void getProducts(int categoryId){
        products = new ArrayList<>();
        productAdapter = new ProductAdapter(this, products);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        binding.productList.setLayoutManager(layoutManager);
        binding.productList.setAdapter(productAdapter);
        /////Get Data from Database through Volley
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Api.GET_PRODUCTS_URL + "?category_id=" + categoryId;
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject mainObj = new JSONObject(response);
                    if (mainObj.getString("status").equals("success")){
                        JSONArray productsArray = mainObj.getJSONArray("products");
                        for (int i = 0; i<productsArray.length(); i++){
                            JSONObject object = productsArray.getJSONObject(i);
                            ProductModel product = new ProductModel(
                                    object.getString("name"),
                                    Api.PRODUCTS_IMAGE_URL+object.getString("image"),
                                    object.getString("status"),
                                    object.getDouble("price"),
                                    object.getDouble("price_discount"),
                                    object.getInt("id"),
                                    object.getInt("stock")
                            );
                            products.add(product);
                        }
                        productAdapter.notifyDataSetChanged();

                    }else {
                        //Nothing
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}