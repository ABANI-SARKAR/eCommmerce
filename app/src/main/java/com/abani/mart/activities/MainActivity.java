package com.abani.mart.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.abani.mart.adapters.CategoryAdapter;
import com.abani.mart.adapters.ProductAdapter;
import com.abani.mart.databinding.ActivityMainBinding;
import com.abani.mart.models.CategoryModel;
import com.abani.mart.models.ProductModel;
import com.abani.mart.utils.Api;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
//import com.onesignal.OneSignal;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String ONESIGNAL_APP_ID = "2fd51194-506f-426c-ab35-26247e6139fe";

    ActivityMainBinding binding;

////Category Part
    ArrayList<CategoryModel> categories;
    CategoryAdapter categoryAdapter;
////////Product Part
    ArrayList<ProductModel> products;
    ProductAdapter productAdapter;
/*................................................................................................*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });

        AdRequest request = new AdRequest.Builder().build();
        binding.adView.loadAd(request);

        binding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Toast.makeText(MainActivity.this, "Ad is loaded", Toast.LENGTH_SHORT).show();
            }
        });
        binding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                binding.adView.loadAd(request);
            }
        });






//        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
//        OneSignal.initWithContext(this);
//        OneSignal.setAppId(ONESIGNAL_APP_ID);

        binding.searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("query", text.toString());
                startActivity(intent);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        initCategories(); initProducts(); initSlider();
    }
/*................................................................................................*/
    void initSlider(){
    RequestQueue queue = Volley.newRequestQueue(this);
    StringRequest request = new StringRequest(Request.Method.GET, Api.GET_OFFERS_URL, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                JSONObject mainObj = new JSONObject(response);
                if (mainObj.getString("status").equals("success")){
                    JSONArray sliderArray = mainObj.getJSONArray("news_infos");
                    for (int i = 0; i<sliderArray.length();i++){
                        JSONObject object = sliderArray.getJSONObject(i);
                        binding.carousel.addData(
                                new CarouselItem(
                                        Api.NEWS_IMAGE_URL+object.getString("image"),
                                        object.getString("title")
                                )
                        );
                    }
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

    void initCategories(){
        categories = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this, categories);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        binding.categoriesList.setLayoutManager(layoutManager);
        binding.categoriesList.setAdapter(categoryAdapter);
        /////Get Data from Database through Volley
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, Api.GET_CATEGORIES_URL, response -> {
            try {
                JSONObject mainObj = new JSONObject(response);
                if (mainObj.getString("status").equals("success")){
                    JSONArray categoriesArray = mainObj.getJSONArray("categories");
                    for (int i = 0; i<categoriesArray.length(); i++){
                        JSONObject object = categoriesArray.getJSONObject(i);
                        CategoryModel category = new CategoryModel(
                                object.getString("name"),
                                Api.CATEGORIES_IMAGE_URL + object.getString("icon"),
                                object.getString("color"),
                                object.getString("brief"),
                                object.getInt("id")
                        );
                        categories.add(category);
                    }
                    categoryAdapter.notifyDataSetChanged();
                }else {
                    //Nothing
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }

    void initProducts(){
        products = new ArrayList<>();
        productAdapter = new ProductAdapter(this, products);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        binding.productList.setLayoutManager(layoutManager);
        binding.productList.setAdapter(productAdapter);
        /////Get Data from Database through Volley
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Api.GET_PRODUCTS_URL + "?count=8";
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




}