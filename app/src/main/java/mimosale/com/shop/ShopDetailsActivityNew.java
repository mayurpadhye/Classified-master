package mimosale.com.shop;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.gson.JsonElement;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import mimosale.com.R;
import mimosale.com.helperClass.CustomUtils;
import mimosale.com.helperClass.PrefManager;
import mimosale.com.home.fragments.AllProductPojo;
import mimosale.com.login.LoginActivity;
import mimosale.com.network.RestInterface;
import mimosale.com.network.RetrofitClient;
import mimosale.com.network.WebServiceURLs;
import mimosale.com.onItemClickListener;
import mimosale.com.products.ProductDetailsActivityNew;
import mimosale.com.shop.adapter.ShopImageDetailsAdapter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static mimosale.com.helperClass.CustomPermissions.MY_PERMISSIONS_REQUEST_CALL_PHONE;

public class ShopDetailsActivityNew extends AppCompatActivity implements View.OnClickListener {

    RecyclerView rv_shop_products;
    TextView toolbar_title;
    ImageView iv_back, iv_phone;
    Button btn_submit, btn_back;
    Dialog dialog;
    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    ViewPager view_pager_shop_details;
    TextView tv_complete_address;
    ImageView iv_navigate;
    String coupon_id = "";
    ImageView iv_product_image;
    RelativeLayout rl_discount;
    // TextView tv_photo_count;
    LinearLayout ll_highlight, ll_info;
    TextView tv_discount, tv_price_range, tv_price_range_detail, tv_location, tv_website, tv_category, tv_tag, tv_shop_name;
    String shop_id = "";
    String lat = "", lan = "";
    //  NestedScrollView nv_main;
    ProgressDialog pDialog;
    TextView tv_desc;
    List<String> shopImagesPojoList = new ArrayList<>();
    List<AllProductPojo> allProductPojoList = new ArrayList<>();
    RelativeLayout rl_view_more;
    RecyclerView rv_products;
    private GoogleMap gmap;
    String like_status = "";
    RelativeLayout rl_follow;
    TextView tv_follow_text, tv_phone_no;
    String phone = "";
    RelativeLayout rl_like;
    TextView tv_photos;
    String status_follow = "follow";
    ImageView iv_follow;
    double lati = 0.0, longi = 0.0;
    TextView tv_like;
    Dialog dialog_view_more;
    TextView tv_price_range_dialog, tv_discount_dialog, tv_sale_duration, tv_website_dialog, tv_address_info;
    Button btn_ok;
    Dialog dialog_review;
    RatingBar ratingBar;
    EditText et_review;
    Button btn_submit_review;
    ProgressBar progress_bar;
    RelativeLayout rl_write_review, rl_claim_now;
    TextView tv_claim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details_new);
        initView();


        //  getAllProducts();
        getShopDeatils();

        iv_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!phone.equals("")) {
                    if (phoneCallPermission()) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + phone));
                        startActivity(intent);
                    } else {
                        phoneCallPermission();
                    }


                }

            }
        });
        iv_navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lati != 0.0 && longi != 0.0) {
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=" + lati + "," + longi + ""));
                    startActivity(intent);
                }

            }
        });

        if (PrefManager.getInstance(ShopDetailsActivityNew.this).IS_LOGIN()) {
            //  rl_follow.setVisibility(View.VISIBLE);
        } else {
            //  rl_follow.setVisibility(View.GONE);
        }
        rl_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PrefManager.getInstance(ShopDetailsActivityNew.this).IS_LOGIN()) {
                    followShop(status_follow);
                } else {
                    Toast.makeText(ShopDetailsActivityNew.this, getResources().getString(R.string.login_msg), Toast.LENGTH_SHORT).show();
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
                if (PrefManager.getInstance(ShopDetailsActivityNew.this).IS_LOGIN()) {
                    //  like_shop();
                }
            }
        });
        rl_like.setOnClickListener(this);
    }

    public void openImagesDialog() {
        Dialog dialog = new Dialog(ShopDetailsActivityNew.this);
        dialog.setContentView(R.layout.dialog_images_pager);
        ViewPager pager = dialog.findViewById(R.id.pager);
        ShopImageDetailsAdapter bannerAdapter = new ShopImageDetailsAdapter(ShopDetailsActivityNew.this, shopImagesPojoList);
        pager.setAdapter(bannerAdapter);
        dialog.show();
    }

    public boolean phoneCallPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?


            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            ActivityCompat.requestPermissions(ShopDetailsActivityNew.this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
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
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + phone));
                    startActivity(intent);

                } else {


                }
                return;
            }


        }
    }

    public void followShop(String follow) {
        try {
            pDialog.show();
            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.followShop(follow, PrefManager.getInstance(ShopDetailsActivityNew.this).getUserId(), shop_id, "Bearer " + PrefManager.getInstance(ShopDetailsActivityNew.this).getApiToken(), new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {

                    try {
                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        pDialog.dismiss();
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (message.equals("Shop followed")) {
                            status_follow = "unfollow";
                            tv_follow_text.setText("Unfollow");
                            //iv_follow.setImageDrawable(getResources().getDrawable(R.drawable.active_like));
                        } else {
                            status_follow = "follow";
                            tv_follow_text.setText("Follow");
                            //iv_follow.setImageDrawable(getResources().getDrawable(R.drawable.like_heart));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    pDialog.dismiss();
                    Toast.makeText(ShopDetailsActivityNew.this, getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                    Log.i("fdfdfdfdfdf", "" + error.getMessage());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            pDialog.dismiss();
            Log.i("detailsException", "" + e.toString());


        }
    }

    public void getShopDeatils() {
        try {
            pDialog.show();
            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.getShopDetails(shop_id, PrefManager.getInstance(ShopDetailsActivityNew.this).getUserId(), new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {
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
                                if (!lat.equals("null") && !lon.equals("null")) {
                                    lati = Double.parseDouble(lat);
                                    longi = Double.parseDouble(lon);
                                }
                                String low_price = j1.getString("low_price");
                                String high_price = j1.getString("high_price");
                                String discount = j1.getString("discount");
                                like_status = j1.getString("like_status");
                                String followStatus = j1.getString("like_status");
                                String like_count = j1.getString("like_count");
                                String web_url = j1.getString("web_url");
                                String hash_tags = j1.getString("hash_tags");
                                String description = j1.getString("description");
                                String claimed_status = j1.getString("claimed_status");



                                if (claimed_status.equals("0")) {
                                    tv_claim.setText(getResources().getString(R.string.claim_now));
                                    rl_claim_now.setEnabled(true);
                                } else {
                                    tv_claim.setText(getResources().getString(R.string.claimed));
                                    rl_claim_now.setEnabled(false);
                                }
                                JSONArray shop_images = j1.getJSONArray("shop_images");
                                tv_complete_address.setText(address_line1);
                                if (!address_line2.equals("null"))
                                    tv_complete_address.append(" " + address_line2);
                                if (like_status.equals("0")) {
                                    tv_like.setText(getResources().getString(R.string.like));
                                } else {
                                    tv_like.setText(getResources().getString(R.string.unlike));
                                }
                                if (!discount.equals("null") || !discount.equals("0")) {
                                    tv_discount.setText("" + discount + "%");
                                    rl_discount.setVisibility(View.VISIBLE);
                                    iv_product_image.setVisibility(View.GONE);
                                    tv_discount_dialog.setText("" + discount + "%");
                                } else {
                                    rl_discount.setVisibility(View.GONE);
                                    iv_product_image.setVisibility(View.VISIBLE);
                                    tv_discount_dialog.setText(getResources().getString(R.string.not_avail));
                                }
                                tv_price_range_dialog.setText(low_price + "Yen - " + high_price + "Yen");
                                tv_address_info.setText(address_line1);
                                tv_website_dialog.setText(web_url);
                                if (followStatus.equals("1")) {
                                    tv_follow_text.setText("Unfollow");
                                    status_follow = "unfollow";
                                    iv_follow.setImageDrawable(getResources().getDrawable(R.drawable.active_like));
                                } else {
                                    tv_follow_text.setText("Follow");
                                    status_follow = "follow";
                                    iv_follow.setImageDrawable(getResources().getDrawable(R.drawable.like_heart));
                                }
                                phone = j1.getString("phone");
                                tv_desc.setText(description);
                                tv_phone_no.setText(phone);
                                if (j1.has("latest_shop_coupon")) {
                                    JSONObject latest_shop_coupon1;
                                    String latest_shop_string;

                                   // JSONObject latest_shop_coupon = j1.getJSONObject("latest_shop_coupon");

                                    Object latest_shop_coupon_obj = j1.get("latest_shop_coupon");
                                    if (latest_shop_coupon_obj instanceof JSONObject) {
                                        rl_discount.setVisibility(View.VISIBLE);
                                        iv_product_image.setVisibility(View.GONE);
                                        latest_shop_coupon1 = (JSONObject) latest_shop_coupon_obj;
                                        String title = latest_shop_coupon1.getString("title");
                                        String coupon_id1 = latest_shop_coupon1.getString("coupon_id");
                                        String description_coupon = latest_shop_coupon1.getString("description");
                                        String start_date = latest_shop_coupon1.getString("start_date");
                                        String end_date = latest_shop_coupon1.getString("end_date");
                                        String no_of_claims = latest_shop_coupon1.getString("no_of_claims");
                                        coupon_id = coupon_id1;
                                        if (!start_date.equals("null") && !end_date.equals("null")) {
                                            tv_sale_duration.setText(start_date + " - " + end_date);}
                                        else {tv_sale_duration.setText(getResources().getString(R.string.not_avail));}

                                    }
                                    else{
                                        rl_discount.setVisibility(View.GONE);
                                        iv_product_image.setVisibility(View.VISIBLE);

                                         }
                                }
                                if (shop_images.length() > 0) {
                                    for (int j = 0; j < shop_images.length(); j++) {
                                        JSONObject j2 = shop_images.getJSONObject(j);
                                        String image_id = j2.getString("id");
                                        String image = j2.getString("image");
                                        shopImagesPojoList.add(image);
                                        Picasso.with(ShopDetailsActivityNew.this).load(WebServiceURLs.SHOP_IMAGE + image).into(iv_product_image);
                                    }
                                    tv_photos.setText("" + shop_images.length() + " " + getResources().getString(R.string.photos));
                                    ShopImageDetailsAdapter bannerAdapter = new ShopImageDetailsAdapter(ShopDetailsActivityNew.this, shopImagesPojoList);
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
                                        allProductPojoList.add(new AllProductPojo(p_id, p_name, p_shop_id, p_user_id, p_description, p_price, p_hash_tag, status1, image1, image2));
                                    }
                                    ShopProductAdapter shopSaleAdapter = new ShopProductAdapter(allProductPojoList, ShopDetailsActivityNew.this);
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
                    Toast.makeText(ShopDetailsActivityNew.this, getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                    Log.i("fdfdfdfdfdf", "" + error.getMessage());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            pDialog.dismiss();
            Log.i("detailsException", "" + e.toString());
        }

    }

    public void initView() {

        rl_claim_now = findViewById(R.id.rl_claim_now);
        tv_claim = findViewById(R.id.tv_claim);
        iv_product_image = findViewById(R.id.iv_product_image);
        rl_write_review = findViewById(R.id.rl_write_review);
        rl_discount = findViewById(R.id.rl_discount);
        tv_follow_text = findViewById(R.id.tv_follow_text);
        tv_phone_no = findViewById(R.id.tv_phone_no);
        tv_desc = findViewById(R.id.tv_desc);
        tv_complete_address = findViewById(R.id.tv_complete_address);
        iv_navigate = findViewById(R.id.iv_navigate);
        toolbar_title = findViewById(R.id.toolbar_title);
        toolbar_title.setText(getIntent().getStringExtra("shop_name"));
        iv_follow = findViewById(R.id.iv_follow);
        rl_like = findViewById(R.id.rl_like);
        tv_photos = findViewById(R.id.tv_photos);
        rl_follow = findViewById(R.id.rl_follow);
        shop_id = getIntent().getStringExtra("shop_id");
        rv_shop_products = findViewById(R.id.rv_shop_products);
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(ShopDetailsActivityNew.this, 1);
        gridLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        rv_shop_products.setLayoutManager(gridLayoutManager1);
        iv_back = findViewById(R.id.iv_back);
        iv_phone = findViewById(R.id.iv_phone);
        btn_submit = findViewById(R.id.btn_submit);
        btn_back = findViewById(R.id.btn_back);
        iv_back.setOnClickListener(this);
        tv_discount = findViewById(R.id.tv_discount);
        tv_shop_name = findViewById(R.id.tv_shop_name);
        tv_category = findViewById(R.id.tv_category);
        tv_price_range_detail = findViewById(R.id.tv_price_range_detail);
        tv_price_range = findViewById(R.id.tv_price_range);
        tv_location = findViewById(R.id.tv_location);
        mPager = (ViewPager) findViewById(R.id.pager);
        rv_products = findViewById(R.id.rv_products);
        rl_view_more = findViewById(R.id.rl_view_more);
        rl_view_more.setOnClickListener(this);
        tv_like = findViewById(R.id.tv_like);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        dialog_view_more = new Dialog(this);
        dialog_view_more.setContentView(R.layout.dialog_shop_info);
        tv_price_range_dialog = dialog_view_more.findViewById(R.id.tv_price_range);
        tv_discount_dialog = dialog_view_more.findViewById(R.id.tv_discount);
        tv_sale_duration = dialog_view_more.findViewById(R.id.tv_sale_duration);
        tv_website_dialog = dialog_view_more.findViewById(R.id.tv_website_dialog);
        tv_address_info = dialog_view_more.findViewById(R.id.tv_address_info);
        btn_ok = dialog_view_more.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(this);
        dialog_review = new Dialog(this);
        dialog_review.setContentView(R.layout.dialog_rating);
        progress_bar = dialog_review.findViewById(R.id.progress_bar);
        btn_submit_review = dialog_review.findViewById(R.id.btn_submit_review);
        et_review = dialog_review.findViewById(R.id.et_review);
        ratingBar = dialog_review.findViewById(R.id.ratingBar);
        btn_submit_review.setOnClickListener(this);
        rl_claim_now.setOnClickListener(this);
        rl_write_review.setOnClickListener(this);


    }//initViewClose

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_like:
                if (PrefManager.getInstance(ShopDetailsActivityNew.this).IS_LOGIN()) {
                    likeUnlikeProduct();
                } else
                    dialogLoginWarning("like_product");
                break;
            case R.id.btn_ok:
                dialog_view_more.dismiss();
                break;
            case R.id.rl_view_more:
                dialog_view_more.show();
                break;
            case R.id.rl_write_review:
                if (PrefManager.getInstance(ShopDetailsActivityNew.this).IS_LOGIN()) {
                    if (PrefManager.getInstance(ShopDetailsActivityNew.this).IS_LOGIN()) {
                        dialog_review.show();
                    } else {
                        dialogLoginWarning("like_product");
                    }

                } else {
                    dialogLoginWarning("product_review");
                }
                break;
            case R.id.btn_submit_review:
               if (validateReviewDialog())
               {
                   writeReview(""+ratingBar.getRating(),et_review.getText().toString().trim());
                   dialog_review.dismiss();
               }
               else
               {

               }

                break;
            case R.id.rl_claim_now:
                if (PrefManager.getInstance(ShopDetailsActivityNew.this).IS_LOGIN()) {
                    claimCoupon();
                } else {
                    dialogLoginWarning("like_product");
                }
                    break;



        }
    }

    public boolean validateReviewDialog()
    {
        if (ratingBar.getRating()==0)
        {
            CustomUtils.showToast(getResources().getString(R.string.please_rate),ShopDetailsActivityNew.this);
            return false;
        }
        if (et_review.getText().toString().trim().isEmpty())
        {
            CustomUtils.showToast(getResources().getString(R.string.please_write_review),ShopDetailsActivityNew.this);
            return false;
        }
        return true;
    }

    public void claimCoupon() {


        try {
            pDialog.show();
            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.claim_coupon(PrefManager.getInstance(ShopDetailsActivityNew.this).getUserId(), coupon_id, "shop", "Bearer " + PrefManager.getInstance(ShopDetailsActivityNew.this).getApiToken(), new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {

                    try {
                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        pDialog.dismiss();
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        CustomUtils.showSweetAlert(ShopDetailsActivityNew.this, message, new onItemClickListener() {
                               @Override
                               public void onClick(SweetAlertDialog v) {
                                   v.dismissWithAnimation();
                               }
                           });
                        } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    pDialog.dismiss();
                    Toast.makeText(ShopDetailsActivityNew.this, getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                    Log.i("fdfdfdfdfdf", "" + error.getMessage());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            pDialog.dismiss();
            Log.i("detailsException", "" + e.toString());
        }
    }

    public void likeUnlikeProduct() {
        String like_flag = "";
        if (like_status.equals("0")) {
            like_flag = "like";
        } else
            like_flag = "unlike";


        try {
            pDialog.show();
            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.like_product("shop", PrefManager.getInstance(ShopDetailsActivityNew.this).getUserId(), like_flag, shop_id, "Bearer " + PrefManager.getInstance(ShopDetailsActivityNew.this).getApiToken(), new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {

                    try {
                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        pDialog.dismiss();
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (message.equals("Liked")) {
                            tv_like.setText(getResources().getString(R.string.unlike));
                            like_status = "1";

                        } else {
                            tv_like.setText(getResources().getString(R.string.like));
                            like_status = "0";
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    pDialog.dismiss();
                    Toast.makeText(ShopDetailsActivityNew.this, getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                    Log.i("fdfdfdfdfdf", "" + error.getMessage());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            pDialog.dismiss();
            Log.i("detailsException", "" + e.toString());
        }
    }

    public void writeReview(String rating,String review) {
        try {
            pDialog.show();
            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.write_review(PrefManager.getInstance(ShopDetailsActivityNew.this).getUserId(), shop_id, "shop", rating,review, "Bearer " + PrefManager.getInstance(ShopDetailsActivityNew.this).getApiToken(), new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {

                    try {
                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        pDialog.dismiss();
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        CustomUtils.showSweetAlert(ShopDetailsActivityNew.this, message, new onItemClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog v) {
                                v.dismissWithAnimation();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    pDialog.dismiss();
                    Toast.makeText(ShopDetailsActivityNew.this, getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                    Log.i("fdfdfdfdfdf", "" + error.getMessage());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            pDialog.dismiss();
            Log.i("detailsException", "" + e.toString());
        }

    }

    public void dialogLoginWarning(final String intent_from) {

        new SweetAlertDialog(ShopDetailsActivityNew.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(ShopDetailsActivityNew.this.getResources().getString(R.string.login_waning))
                .setContentText(getResources().getString(R.string.please_login))
                .setConfirmText(getResources().getString(R.string.login))
                .setCancelText(getResources().getString(R.string.cancel))

                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        Intent i = new Intent(ShopDetailsActivityNew.this, LoginActivity.class);
                        i.putExtra("intent_from", intent_from);
                        startActivityForResult(i, 1);
                        sDialog.dismissWithAnimation();
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                })
                .show();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {

            if (data != null) {

                if (data.getStringExtra("intent_from").equals("like_product")) {
                    getShopDeatils();
                }

            }


        }
    }

}
