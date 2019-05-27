package mimosale.com.shop;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import mimosale.com.BottomSheetMapFragment;
import mimosale.com.R;
import mimosale.com.following.FollowingShopActivity;
import mimosale.com.helperClass.PrefManager;
import mimosale.com.home.BannerAdapter;
import mimosale.com.home.HomeActivity;
import mimosale.com.home.fragments.AllProductPojo;
import mimosale.com.home.shop_sale.ProductsAdapter;
import mimosale.com.network.RestInterface;
import mimosale.com.network.RetrofitClient;
import mimosale.com.network.WebServiceURLs;
import mimosale.com.shop.adapter.ShopImageDetailsAdapter;
import mimosale.com.shop.pojoClass.ShopImagesPojo;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static mimosale.com.helperClass.CustomPermissions.MY_PERMISSIONS_REQUEST_CALL_PHONE;
import static mimosale.com.helperClass.CustomPermissions.MY_PERMISSIONS_REQUEST_LOCATION;

public class ShopDetailActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {
    ShimmerTextView tv_avg_rating;
    Shimmer shimmer;
    RecyclerView rv_shop_products;

    ImageView iv_back, iv_phone;
    Button btn_submit, btn_back;
    Dialog dialog;
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    ViewPager view_pager_shop_details;
    TabLayout tabLayout;
    // TextView tv_photo_count;
    LinearLayout ll_highlight, ll_info;
    TextView tv_discount, tv_price_range, tv_price_range_detail, tv_location, tv_website, tv_category, tv_tag, tv_shop_name;
    String shop_id = "";
    String lat = "", lan = "";
    //  NestedScrollView nv_main;
    ProgressDialog pDialog;
    List<String> shopImagesPojoList = new ArrayList<>();
    List<AllProductPojo> allProductPojoList = new ArrayList<>();
    ExpandableTextView expTv1;
    RecyclerView rv_products;
    private GoogleMap gmap;
    RelativeLayout rl_direction;
    RelativeLayout rl_follow;
    TextView tv_follow_text;
    String phone = "";
    RelativeLayout rl_like;
    TextView tv_photos;
    String status_follow = "follow";
    ImageView iv_follow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);
        initView();
        rv_products = findViewById(R.id.rv_products);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        //  getAllProducts();
        getShopDeatils();
        rl_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView();
            }
        });
        iv_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!phone.equals("")) {
                    phoneCallPermission();

                }

            }
        });

        if (PrefManager.getInstance(ShopDetailActivity.this).IS_LOGIN()) {
          //  rl_follow.setVisibility(View.VISIBLE);
        } else {
          //  rl_follow.setVisibility(View.GONE);
        }
        rl_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PrefManager.getInstance(ShopDetailActivity.this).IS_LOGIN()) {
                    followShop(status_follow);
                } else {
                    Toast.makeText(ShopDetailActivity.this, getResources().getString(R.string.login_msg), Toast.LENGTH_SHORT).show();
                }

            }
        });
        tv_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagesDialog();
            }
        });
        rl_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PrefManager.getInstance(ShopDetailActivity.this).IS_LOGIN()) {
                    like_shop();
                }
            }
        });
    }


    public void like_shop() {/*
        try {
            pDialog.show();
            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.like_shop(shop_id, PrefManager.getInstance(ShopDetailActivity.this).getUserId(), new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {
                    //this method call if webservice success
                    try {
                        pDialog.dismiss();


                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        String status = jsonObject.getString("status");

                        if (status.equals("1")) {
                        }


                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        Log.i("detailsException", "" + e.toString());

                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    pDialog.dismiss();
                    Toast.makeText(ShopDetailActivity.this, getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                    Log.i("fdfdfdfdfdf", "" + error.getMessage());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }


    public void openImagesDialog() {
        Dialog dialog = new Dialog(ShopDetailActivity.this);
        dialog.setContentView(R.layout.dialog_images_pager);
        ViewPager pager = dialog.findViewById(R.id.pager);
        ShopImageDetailsAdapter bannerAdapter = new ShopImageDetailsAdapter(ShopDetailActivity.this, shopImagesPojoList);
        pager.setAdapter(bannerAdapter);
        dialog.show();
    }


    public void initView() {
        rl_direction = findViewById(R.id.rl_direction);
        tv_follow_text = findViewById(R.id.tv_follow_text);
        iv_follow = findViewById(R.id.iv_follow);
        rl_like = findViewById(R.id.rl_like);
        tv_photos = findViewById(R.id.tv_photos);
        rl_follow = findViewById(R.id.rl_follow);
        shimmer = new Shimmer();
        tv_avg_rating = findViewById(R.id.tv_avg_rating);
        shop_id = getIntent().getStringExtra("shop_id");
        shimmer.start(tv_avg_rating);
        rv_shop_products = findViewById(R.id.rv_shop_products);
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(ShopDetailActivity.this, 1);
        gridLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        rv_shop_products.setLayoutManager(gridLayoutManager1);
        ll_highlight = findViewById(R.id.ll_highlight);
        ll_info = findViewById(R.id.ll_info);

        iv_back = findViewById(R.id.iv_back);
        iv_phone = findViewById(R.id.iv_phone);
        btn_submit = findViewById(R.id.btn_submit);
        btn_back = findViewById(R.id.btn_back);
        btn_submit.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        // Add five tabs.  Three have icons and two have text titles
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.highlight)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.info)));
        expTv1 = (ExpandableTextView) findViewById(R.id.expand_text_view)
                .findViewById(R.id.expand_text_view);

        // IMPORTANT - call setText on the ExpandableTextView to set the text content to display
        expTv1.setText(getString(R.string.lorem_epsum));
        tv_discount = findViewById(R.id.tv_discount);
        tv_shop_name = findViewById(R.id.tv_shop_name);
        tv_category = findViewById(R.id.tv_category);
        tv_price_range_detail = findViewById(R.id.tv_price_range_detail);
        tv_price_range = findViewById(R.id.tv_price_range);
        tv_location = findViewById(R.id.tv_location);
        tv_website = findViewById(R.id.tv_website);
        tv_tag = findViewById(R.id.tv_tag);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition() == 0) {
                    ll_highlight.setVisibility(View.VISIBLE);
                    ll_info.setVisibility(View.GONE);

                } else if (tabLayout.getSelectedTabPosition() == 1) {
                    ll_highlight.setVisibility(View.GONE);
                    ll_info.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        mPager = (ViewPager) findViewById(R.id.pager);


    }//initViewClose


    public void getShopDeatils() {
        try {
            pDialog.show();
            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.getShopDetails(shop_id, PrefManager.getInstance(ShopDetailActivity.this).getUserId(), new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {
                    //this method call if webservice success
                    try {
                        pDialog.dismiss();
                        allProductPojoList.clear();

                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        String status = jsonObject.getString("status");

                        if (status.equals("1")) {
                            JSONArray data = jsonObject.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject j1 = data.getJSONObject(i);
                                String id = j1.getString("id");
                                String name = j1.getString("name");
                                String preference_id = j1.getString("preference_id");
                                String address_line1 = j1.getString("address_line1");
                                String address_line2 = j1.getString("address_line2");
                                String city = j1.getString("city");
                                String state = j1.getString("state");
                                String country = j1.getString("country");
                                String pincode = j1.getString("pincode");
                                String lat = j1.getString("lat");
                                String lon = j1.getString("lon");
                                String low_price = j1.getString("low_price");
                                String high_price = j1.getString("high_price");
                                String min_discount = j1.getString("min_discount");
                                String max_discount = j1.getString("max_discount");
                                String start_date = j1.getString("start_date");
                                String end_date = j1.getString("end_date");
                                String followStatus = j1.getString("followStatus");
                                if (followStatus.equals("1")) {
                                    tv_follow_text.setText("Unfollow");
                                    status_follow = "unfollow";
                                    iv_follow.setImageDrawable(getResources().getDrawable(R.drawable.follower_colored));
                                } else {
                                    tv_follow_text.setText("Follow");
                                    status_follow = "follow";
                                    iv_follow.setImageDrawable(getResources().getDrawable(R.drawable.follower));
                                }
                                if (min_discount.equals("null")) {
                                    tv_discount.setVisibility(View.INVISIBLE);
                                } else {
                                    tv_discount.setVisibility(View.VISIBLE);
                                }

                                phone = j1.getString("phone");
                                String hash_tags = j1.getString("hash_tags");
                                String description = j1.getString("description");
                                String web_url = j1.getString("web_url");
                                JSONArray shop_images = j1.getJSONArray("shop_images");
                                //  shopImagesPojoList.clear();


                                tv_discount.setText(min_discount + "% - " + max_discount + "%");
                                tv_price_range.setText(low_price + " - " + high_price);
                                tv_price_range_detail.setText("\u00A5" + low_price + " - \u00A5" + high_price);
                                tv_location.setText(address_line1 + " " + address_line2 + "\n" + city + " - " + pincode);
                                //    tv_category.setText(shop_category);
                                tv_tag.setText(hash_tags);
                                tv_website.setText(web_url);
                                tv_shop_name.setText(name);


// IMPORTANT - call setText on the ExpandableTextView to set the text content to display
                                expTv1.setText(description);
                                if (shop_images.length() > 0) {

                                    for (int j = 0; j < shop_images.length(); j++) {

                                        JSONObject j2 = shop_images.getJSONObject(j);
                                        String image_id = j2.getString("id");
                                        String image = j2.getString("image");
                                        shopImagesPojoList.add(image);
                                    }
                                    tv_photos.setText("" + shop_images.length() + " " + getResources().getString(R.string.photos));
                                    ShopImageDetailsAdapter bannerAdapter = new ShopImageDetailsAdapter(ShopDetailActivity.this, shopImagesPojoList);
                                    mPager.setAdapter(bannerAdapter);

                                    CirclePageIndicator indicator = (CirclePageIndicator)
                                            findViewById(R.id.indicator);

                                    indicator.setViewPager(mPager);

                                    final float density = getResources().getDisplayMetrics().density;
                                    indicator.setRadius(5 * density);
                                }

                                JSONArray products = j1.getJSONArray("products");

                                if (products.length() > 0) {
                                    for (int k = 0; k < products.length(); k++) {
                                        JSONObject j2 = products.getJSONObject(k);
                                        String p_id = j2.getString("id");
                                        String p_name = j2.getString("name");
                                        String p_shop_id = j2.getString("shop_id");
                                        String p_user_id = j2.getString("user_id");
                                        String p_description = j2.getString("description");
                                        String p_price = j2.getString("price");
                                        String p_hash_tag = j2.getString("hash_tags");
                                        String status1 = j2.getString("status");
                                        String image1 = "";
                                        String image2 = "";
                                        if (j2.has("image1")) {
                                            image1 = j2.getString("image1");
                                        }
                                        if (j2.has("image2")) {
                                            image2 = j2.getString("image2");
                                        }


                                        String image = "";
                            /* JSONArray product_images=j1.getJSONArray("product_images");
                                for (int j=0;j<product_images.length();j++)
                                {
                                    JSONObject j3=product_images.getJSONObject(k);
                                    image=j3.getString("image");
                                }*/
                                        allProductPojoList.add(new AllProductPojo(p_id, p_name, p_shop_id, p_user_id, p_description, p_price, p_hash_tag, status1, image1, image2));

                                    }

                                    ShopProductAdapter shopSaleAdapter = new ShopProductAdapter(allProductPojoList, ShopDetailActivity.this);
                                    rv_shop_products.setAdapter(shopSaleAdapter);
                                }


                            }
                        }


                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                        pDialog.dismiss();
                        Log.i("detailsException", "" + e.toString());

                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    pDialog.dismiss();
                    Toast.makeText(ShopDetailActivity.this, getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                    Log.i("fdfdfdfdfdf", "" + error.getMessage());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            pDialog.dismiss();
            Log.i("detailsException", "" + e.toString());


        }

    }

    public void mapView() {

        BottomSheetMapFragment addPhotoBottomDialogFragment =
                BottomSheetMapFragment.newInstance();
        addPhotoBottomDialogFragment.show(getSupportFragmentManager(),
                "add_photo_dialog_fragment");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(12);
        LatLng ny = new LatLng(40.7143528, -74.0059731);
        gmap.moveCamera(CameraUpdateFactory.newLatLng(ny));
    }

    public boolean phoneCallPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CALL_PHONE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(ShopDetailActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
            }


            return false;

        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CALL_PHONE)
                            == PackageManager.PERMISSION_GRANTED) {

                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + phone));
                        startActivity(intent);

                    }

                } else {
                    phoneCallPermission();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }


        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_submit:

                break;
            case R.id.skip:
                dialog.dismiss();
                break;
            case R.id.btn_submit_dialog:
                dialog.dismiss();
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }


    public void followShop(String follow) {
        try {
            pDialog.show();
            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.followShop(follow, PrefManager.getInstance(ShopDetailActivity.this).getUserId(), shop_id, "Bearer " + PrefManager.getInstance(ShopDetailActivity.this).getApiToken(), new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {

                    try {
                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        pDialog.dismiss();
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (message.equals("Shop followed")) {
                            status_follow="unfollow";
                            tv_follow_text.setText("Unfollow");
                            iv_follow.setImageDrawable(getResources().getDrawable(R.drawable.follower_colored));
                        } else {
                            status_follow="follow";
                            tv_follow_text.setText("Follow");
                            iv_follow.setImageDrawable(getResources().getDrawable(R.drawable.follower));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    pDialog.dismiss();
                    Toast.makeText(ShopDetailActivity.this, getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                    Log.i("fdfdfdfdfdf", "" + error.getMessage());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            pDialog.dismiss();
            Log.i("detailsException", "" + e.toString());


        }
    }
}
