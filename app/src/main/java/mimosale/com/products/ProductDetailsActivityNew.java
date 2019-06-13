package mimosale.com.products;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import mimosale.com.R;
import mimosale.com.account.AccountFragment;
import mimosale.com.favourite.FavouriteFragment;
import mimosale.com.helperClass.CustomUtils;
import mimosale.com.helperClass.PrefManager;
import mimosale.com.home.HomeActivity;
import mimosale.com.login.LoginActivity;
import mimosale.com.network.RestInterface;
import mimosale.com.network.RetrofitClient;
import mimosale.com.network.WebServiceURLs;
import mimosale.com.notification.NotificationFragment;
import mimosale.com.onItemClickListener;
import mimosale.com.post.SalePostingActivity;
import mimosale.com.shop.ShopDetailsActivityNew;
import mimosale.com.shop.ShopPostingActivity;
import mimosale.com.shop.adapter.ShopImageDetailsAdapter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProductDetailsActivityNew extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.indicator)
    CirclePageIndicator indicator;
    @BindView(R.id.tv_photos)
    TextView tv_photos;
    @BindView(R.id.rl_follow)
    RelativeLayout rl_follow;
    @BindView(R.id.rl_like)
    RelativeLayout rl_like;
    @BindView(R.id.tv_follow_text)
    TextView tv_follow_text;
    @BindView(R.id.tv_like)
    TextView tv_like;
    @BindView(R.id.tv_desc)
    TextView tv_desc;
    @BindView(R.id.rl_discount)
    RelativeLayout rl_discount;
    @BindView(R.id.iv_product_image)
    ImageView iv_product_image;
    @BindView(R.id.tv_discount)
    TextView tv_discount;
    @BindView(R.id.rl_view_more)
    RelativeLayout rl_view_more;
    @BindView(R.id.rl_write_review)
    RelativeLayout rl_write_review;
    String like_status="";
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;
    @BindView(R.id.tv_original_price_tag)
    TextView tv_original_price_tag;
    @BindView(R.id.tv_original_price)
    TextView tv_original_price;
    @BindView(R.id.ll_discounted_price)
    LinearLayout ll_discounted_price;
    @BindView(R.id.ll_original_price)
    LinearLayout ll_original_price;
    @BindView(R.id.tv_discounted_price)
    TextView tv_discounted_price;
    @BindView(R.id.ll_next)
    LinearLayout ll_next;
    @BindView(R.id.tv_brand_name)
    TextView tv_brand_name;
    @BindView(R.id.tv_model_name)
    TextView tv_model_name;
    @BindView(R.id.iv_back)
            ImageView iv_back;
String coupon_id="";
    ProgressDialog pDialog;
    String product_id="";
    List<String> shopImagesPojoList = new ArrayList<>();
    Dialog bottomSheetDialog;
    Button btn_ok;
    TextView tv_spec,tv_size,tv_color,tv_model_no,tv_brand;
    Dialog dialog_review;
    RatingBar ratingBar;
    EditText et_review;
    Button btn_submit_review;

    @BindView(R.id.tv_claim_now)
    TextView tv_claim_now;

    @BindView(R.id.rl_claim_now)
            RelativeLayout rl_claim_now;
    ProgressBar progress_bar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details_new);
        ButterKnife.bind(this);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.loading));
        product_id = getIntent().getStringExtra("product_id");
        bottomSheetDialog=new Dialog(ProductDetailsActivityNew.this);
        bottomSheetDialog.setContentView(R.layout.more_info_bottom_sheet);
        tv_spec=bottomSheetDialog.findViewById(R.id.tv_spec);
        tv_size=bottomSheetDialog.findViewById(R.id.tv_size);
        btn_ok=bottomSheetDialog.findViewById(R.id.btn_ok);
        tv_color=bottomSheetDialog.findViewById(R.id.tv_color);
        tv_model_no=bottomSheetDialog.findViewById(R.id.tv_model_no);
        tv_brand=bottomSheetDialog.findViewById(R.id.tv_brand);
        rl_view_more.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        rl_like.setOnClickListener(this);
        dialog_review=new Dialog(this);
        dialog_review.setContentView(R.layout.dialog_rating);
        progress_bar=dialog_review.findViewById(R.id.progress_bar);
        btn_submit_review=dialog_review.findViewById(R.id.btn_submit_review);
        et_review=dialog_review.findViewById(R.id.et_review);
        ratingBar=dialog_review.findViewById(R.id.ratingBar);
        rl_write_review.setOnClickListener(this);
        btn_submit_review.setOnClickListener(this);
        rl_claim_now.setOnClickListener(this);
        getProductDetails();

    }
    @OnClick(R.id.iv_back)
    void onBackClick()
    {
        finish();
    }


    public void claimCoupon() {


        try {
            pDialog.show();
            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.claim_coupon(PrefManager.getInstance(ProductDetailsActivityNew.this).getUserId(), coupon_id, "product", "Bearer " + PrefManager.getInstance(ProductDetailsActivityNew.this).getApiToken(), new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {

                    try {
                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        pDialog.dismiss();
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equals("1"))
                        {
                            rl_claim_now.setEnabled(false);
                            tv_claim_now.setText(getResources().getString(R.string.claimed));
                        }
                        CustomUtils.showSweetAlert(ProductDetailsActivityNew.this, message, new onItemClickListener() {
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
                    Toast.makeText(ProductDetailsActivityNew.this, getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                    Log.i("fdfdfdfdfdf", "" + error.getMessage());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            pDialog.dismiss();
            Log.i("detailsException", "" + e.toString());
        }
    }
    public void getProductDetails() {
        try {

            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.getProductDetails(product_id, new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {
                    //this method call if webservice success
                    try {


                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        String status = jsonObject.getString("status");

                        if (status.equals("1")) {
                            JSONObject j1 = jsonObject.getJSONObject("data");


                                String id = j1.getString("id");
                                String name = j1.getString("name");
                                String description = j1.getString("description");
                                String price = j1.getString("price");
                                String hash_tag = j1.getString("hash_tags");
                                String status1 = j1.getString("status");
                                String discount = j1.getString("discount");
                                String brand = j1.getString("brand");
                                String model_number = j1.getString("model_number");
                                String quantity = j1.getString("quantity");
                                String color = j1.getString("color");
                                String size = j1.getString("size");
                                String specification = j1.getString("specification");
                                String selling_price = j1.getString("selling_price");
                                String claimed_status = j1.getString("claimed_status");

                                if (claimed_status.equals("1"))
                                {
                                    rl_claim_now.setEnabled(false);
                                    tv_claim_now.setText(getResources().getString(R.string.claimed));
                                }
                                else
                                {
                                    rl_claim_now.setEnabled(true);
                                    tv_claim_now.setText(getResources().getString(R.string.claim_now));
                                }

                                 like_status = j1.getString("like_status");
                                String like_count = j1.getString("like_count");
                                if (like_status.equals("0"))
                                {
                                    tv_like.setText(getResources().getString(R.string.like));
                                }
                                else
                                {
                                    tv_like.setText(getResources().getString(R.string.unlike));
                                }

                                toolbar_title .setText(name);
                                tv_desc.setText(description);
                                if (discount.equals("null") || discount.equals("0"))
                                {
                                    iv_product_image.setVisibility(View.VISIBLE);
                                    rl_discount.setVisibility(View.GONE);
                                    ll_original_price.setVisibility(View.GONE);
                                    ll_next.setVisibility(View.GONE);
                                    tv_discounted_price.setText(price+"Yen");
                                }
                                else
                                {
                                    ll_original_price.setVisibility(View.VISIBLE);
                                    ll_discounted_price.setVisibility(View.VISIBLE);
                                    ll_next.setVisibility(View.VISIBLE);
                                    tv_original_price_tag.setPaintFlags(tv_original_price_tag.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                    tv_original_price.setPaintFlags(tv_original_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                    tv_original_price.setText(price+"Yen");
                                    tv_discounted_price.setText(selling_price+"Yen");
                                }

                               if (brand.equals("null"))
                               {
                                   tv_brand.setText(getResources().getString(R.string.not_avail));
                                   tv_brand_name.setText(getResources().getString(R.string.not_avail));
                               }
                               else
                               {
                                   tv_brand.setText(brand);
                                   tv_brand_name.setText(brand);
                               }
                               if (color.equals("null"))
                               {
                                   tv_color.setText(getResources().getString(R.string.not_avail));

                               }
                               else
                               {
                                   tv_color.setText(color);
                               }
                            if (size.equals("null"))
                            {
                                tv_size.setText(getResources().getString(R.string.not_avail));
                            }
                            else
                            {
                                tv_size.setText(size);
                            }
                            if (specification.equals("null"))
                            {
                                tv_spec.setText(getResources().getString(R.string.not_avail));
                            }
                            else
                            {
                                tv_spec.setText(specification);
                            }
                            if (model_number.equals("null"))
                            {
                                tv_model_no.setText(getResources().getString(R.string.not_avail));
                                tv_model_name.setText(getResources().getString(R.string.not_avail));
                            }
                            else
                            {
                                tv_model_no.setText(model_number);
                                tv_model_name.setText(model_number);
                            }
                                tv_discount.setText(discount+"%");


                            if (j1.has("latest_product_coupon")) {
                                JSONObject latest_shop_coupon1;
                                String latest_shop_string;

                                // JSONObject latest_shop_coupon = j1.getJSONObject("latest_shop_coupon");

                                Object latest_shop_coupon_obj = j1.get("latest_product_coupon");
                                if (latest_shop_coupon_obj instanceof JSONObject) {
                                    rl_discount.setVisibility(View.VISIBLE);
                                    iv_product_image.setVisibility(View.GONE);
                                    latest_shop_coupon1 = (JSONObject) latest_shop_coupon_obj;
                                    String title = latest_shop_coupon1.getString("title");
                                    String coupon_id1 = latest_shop_coupon1.getString("coupon_id");
                                    coupon_id=coupon_id1;
                                    String description_coupon = latest_shop_coupon1.getString("description");
                                    String start_date = latest_shop_coupon1.getString("start_date");
                                    String end_date = latest_shop_coupon1.getString("end_date");
                                    String no_of_claims = latest_shop_coupon1.getString("no_of_claims");
                                    coupon_id = coupon_id1;
                                   /* if (!start_date.equals("null") && !end_date.equals("null")) {
                                        tv_sale_duration.setText(start_date + " - " + end_date);}
                                    else {tv_sale_duration.setText(getResources().getString(R.string.not_avail));}*/

                                }
                                else{
                                    rl_discount.setVisibility(View.GONE);
                                    iv_product_image.setVisibility(View.VISIBLE);

                                }
                            }
                            JSONArray product_images = j1.getJSONArray("product_images");
                                for (int j = 0; j < product_images.length(); j++) {
                                    JSONObject j2 = product_images.getJSONObject(j);
                                    String image_id = j2.getString("id");
                                    String image = j2.getString("image");
                                    shopImagesPojoList.add(image);
                                }
                                if (product_images.length()>=1)
                                {
                                    Picasso.with(ProductDetailsActivityNew.this).load(WebServiceURLs.SHOP_IMAGE+shopImagesPojoList.get(1)).into(iv_product_image);
                                }
                                ShopImageDetailsAdapter bannerAdapter = new ShopImageDetailsAdapter(ProductDetailsActivityNew.this, shopImagesPojoList);
                                pager.setAdapter(bannerAdapter);
                                CirclePageIndicator indicator = (CirclePageIndicator)
                                        findViewById(R.id.indicator);
                                indicator.setViewPager(pager);
                                final float density = getResources().getDisplayMetrics().density;
                                indicator.setRadius(5 * density);
                                }

                                }
                                catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                        Log.i("fdfdfdfdfdf", "" + e.getMessage());
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(ProductDetailsActivityNew.this, getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                    Log.i("fdfdfdfdfdf", "" + error.getMessage());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rl_view_more:
                bottomSheetDialog.show();
                break;
            case R.id.btn_ok:
                bottomSheetDialog.dismiss();
                break;
            case R.id.rl_like:
                if (PrefManager.getInstance(ProductDetailsActivityNew.this).IS_LOGIN())
                {

                    likeUnlikeProduct();
                }

                else
                    dialogLoginWarning("like_product");
                break;

            case R.id.rl_write_review:
                if (PrefManager.getInstance(ProductDetailsActivityNew.this).IS_LOGIN()) {

                    dialog_review.show();
                }
                else
                {
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
                if (PrefManager.getInstance(ProductDetailsActivityNew.this).IS_LOGIN()) {
                    claimCoupon();
                } else {
                    dialogLoginWarning("like_product");
                }

        }
    }
    public void writeReview(String rating,String review) {
        try {
            pDialog.show();
            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.write_review(PrefManager.getInstance(ProductDetailsActivityNew.this).getUserId(), product_id, "product", rating,review, "Bearer " + PrefManager.getInstance(ProductDetailsActivityNew.this).getApiToken(), new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {

                    try {
                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        pDialog.dismiss();
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        CustomUtils.showSweetAlert(ProductDetailsActivityNew.this, message, new onItemClickListener() {
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
                    Toast.makeText(ProductDetailsActivityNew.this, getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                    Log.i("fdfdfdfdfdf", "" + error.getMessage());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            pDialog.dismiss();
            Log.i("detailsException", "" + e.toString());
        }

    }
    public boolean validateReviewDialog()
    {
        if (ratingBar.getRating()==0)
        {
            CustomUtils.showToast(getResources().getString(R.string.please_rate),ProductDetailsActivityNew.this);
            return false;
        }
        if (et_review.getText().toString().trim().isEmpty())
        {
            CustomUtils.showToast(getResources().getString(R.string.please_write_review),ProductDetailsActivityNew.this);
            return false;
        }
        return true;
    }
    public void dialogLoginWarning(final String intent_from) {

        new SweetAlertDialog(ProductDetailsActivityNew.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(ProductDetailsActivityNew.this.getResources().getString(R.string.login_waning))
                .setContentText(getResources().getString(R.string.please_login))
                .setConfirmText(getResources().getString(R.string.login))
                .setCancelText(getResources().getString(R.string.cancel))

                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        Intent i = new Intent(ProductDetailsActivityNew.this, LoginActivity.class);
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

    public void likeUnlikeProduct()
    { String like_flag="";
        if (like_status.equals("0"))
        {
            like_flag="like";
        }
        else
            like_flag="unlike";


        try {
            pDialog.show();
            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.like_product("product", PrefManager.getInstance(ProductDetailsActivityNew.this).getUserId(),like_flag, product_id, "Bearer " + PrefManager.getInstance(ProductDetailsActivityNew.this).getApiToken(), new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {

                    try {
                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        pDialog.dismiss();
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (message.equals("Liked")) {
                            tv_like.setText(getResources().getString(R.string.unlike));
                            like_status="1";

                        } else {
                            tv_like.setText(getResources().getString(R.string.like));
                            like_status="0";
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    pDialog.dismiss();
                    Toast.makeText(ProductDetailsActivityNew.this, getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                    Log.i("fdfdfdfdfdf", "" + error.getMessage());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            pDialog.dismiss();
            Log.i("detailsException", "" + e.toString());
            }
    }//

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {

            if (data != null) {

                if (data.getStringExtra("intent_from").equals("like_product")) {
                   getProductDetails();
                }

            }


        }
    }

}
