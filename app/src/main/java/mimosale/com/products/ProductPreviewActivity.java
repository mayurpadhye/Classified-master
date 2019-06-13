package mimosale.com.products;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;
import mimosale.com.R;
import mimosale.com.helperClass.PrefManager;
import mimosale.com.my_posting.MyPostingActivity;
import mimosale.com.network.RestInterface;
import mimosale.com.network.RetrofitClient;
import mimosale.com.network.WebServiceURLs;
import mimosale.com.shop.ImageVideoData;
import mimosale.com.shop.ShopSlidingImagesAdapter;
import mimosale.com.shop.adapter.ShopImageDetailsAdapter;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

import static mimosale.com.products.AddProductsActivity.imageFiles_products;
import static mimosale.com.products.AddProductsActivity.image_thumbnails_product;
import static mimosale.com.products.UpdateProductAcitvity.image_thumbnails_product_update;

public class ProductPreviewActivity extends AppCompatActivity implements View.OnClickListener {
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
    @BindView(R.id.btn_back)
     Button btn_back;
    String types="";
    String product_name="";
    String desc="";
    String price="";
    String discount="";
    String brand="";
    String model_number="";
    String quantity="";
    String color="";
    String size="";
    String hash_tag="";
    String specification="";
    String shop_id="";
@BindView(R.id.p_bar)
    ProgressBar p_bar;
    @BindView(R.id.btn_submit)
            Button btn_submit;
String start_date="",end_date="";
    ArrayList<ImageVideoData> image_thumbnail=new ArrayList<>();
    ProgressDialog pDialog;
    String product_id="";
    List<String> shopImagesPojoList = new ArrayList<>();
    BottomSheetDialog bottomSheetDialog;
    Button btn_ok;
    TextView tv_spec,tv_size,tv_color,tv_model_no,tv_brand;
    List<File> image_thumbnail1=new ArrayList<>();
    String coupon_title="",coupon_description="",no_of_claims="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_preview);
        ButterKnife.bind(this);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.loading));
        if (getIntent().hasExtra("product_id"))
        product_id = getIntent().getStringExtra("product_id");
        bottomSheetDialog=new BottomSheetDialog(ProductPreviewActivity.this);
        bottomSheetDialog.setContentView(R.layout.more_info_bottom_sheet);
        tv_spec=bottomSheetDialog.findViewById(R.id.tv_spec);
        tv_size=bottomSheetDialog.findViewById(R.id.tv_size);
        btn_ok=bottomSheetDialog.findViewById(R.id.btn_ok);
        tv_color=bottomSheetDialog.findViewById(R.id.tv_color);
        tv_model_no=bottomSheetDialog.findViewById(R.id.tv_model_no);
        tv_brand=bottomSheetDialog.findViewById(R.id.tv_brand);
        rl_view_more.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        getData();
    }//onCreateClose

    public void getData()
    {

         product_name=getIntent().getStringExtra("product_name");
         desc=getIntent().getStringExtra("desc");
         price=getIntent().getStringExtra("price");
         discount=getIntent().getStringExtra("discount");
         brand=getIntent().getStringExtra("brand");
         model_number=getIntent().getStringExtra("model_number");
         quantity=getIntent().getStringExtra("quantity");
         color=getIntent().getStringExtra("color");
         size=getIntent().getStringExtra("size");
         hash_tag=getIntent().getStringExtra("hash_tag");
         specification=getIntent().getStringExtra("size");
         types=getIntent().getStringExtra("types");
        coupon_title=getIntent().getStringExtra("coupon_title");
        coupon_description=getIntent().getStringExtra("coupon_desc");
        no_of_claims=getIntent().getStringExtra("no_of_coupon");
        start_date=getIntent().getStringExtra("start_date");
        end_date=getIntent().getStringExtra("end_date");
        if(getIntent().hasExtra("shop_id"))
         {
             shop_id=getIntent().getStringExtra("shop_id");
         }

        toolbar_title .setText(product_name);
        tv_desc.setText(desc);
        if (discount.equals(""))
        {
            iv_product_image.setVisibility(View.VISIBLE);
            rl_discount.setVisibility(View.GONE);
            ll_original_price.setVisibility(View.GONE);
            ll_next.setVisibility(View.GONE);
            tv_discounted_price.setText(price+"Yen");
        }
        else
        {
            int original_price=Integer.parseInt(price);
            int dis=Integer.parseInt((discount));
            int selling_price=(original_price/100)*dis;
            int final_selling=original_price-selling_price;
            ll_original_price.setVisibility(View.VISIBLE);
            ll_discounted_price.setVisibility(View.VISIBLE);
            ll_next.setVisibility(View.VISIBLE);
            tv_original_price_tag.setPaintFlags(tv_original_price_tag.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tv_original_price.setPaintFlags(tv_original_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tv_original_price.setText(price+"Yen");
            tv_discounted_price.setText(""+final_selling+"Yen");
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
       // parseJSON();

        if (types.equals("update"))
        {

            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ShopSlidingImagesAdapter bannerAdapter = new ShopSlidingImagesAdapter(image_thumbnails_product_update,layoutInflater,ProductPreviewActivity.this);
            pager.setAdapter(bannerAdapter);
            CirclePageIndicator indicator = (CirclePageIndicator)
                    findViewById(R.id.indicator);
            indicator.setViewPager(pager);
            final float density = getResources().getDisplayMetrics().density;
            indicator.setRadius(5 * density);
        }
        else
        {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ShopSlidingImagesAdapter bannerAdapter = new ShopSlidingImagesAdapter(image_thumbnails_product,layoutInflater,ProductPreviewActivity.this);
            pager.setAdapter(bannerAdapter);
            CirclePageIndicator indicator = (CirclePageIndicator)findViewById(R.id.indicator);
            indicator.setViewPager(pager);
            final float density = getResources().getDisplayMetrics().density;
            indicator.setRadius(5 * density);
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
            case R.id.iv_back:
                finish();
                break;

            case R.id.btn_back:
                finish();
            case  R.id.btn_submit:
                if (types.equals("update"))
                {
                    UpdateProduct();
                }
                else
                {
                    SaveProductDetails();
                }

        }
    }

    public void UpdateProduct() {

        try {
            String user_id = PrefManager.getInstance(ProductPreviewActivity.this).getUserId();
            p_bar.setVisibility(View.VISIBLE);
            MultipartTypedOutput multipartTypedOutput = new MultipartTypedOutput();
            multipartTypedOutput.addPart("name", new TypedString(product_name));
            multipartTypedOutput.addPart("shop_id", new TypedString(shop_id));
            multipartTypedOutput.addPart("description", new TypedString(desc));
            multipartTypedOutput.addPart("price", new TypedString(price));
            multipartTypedOutput.addPart("user_id", new TypedString(user_id));
            multipartTypedOutput.addPart("discount", new TypedString(discount));
            multipartTypedOutput.addPart("brand", new TypedString(brand));
            multipartTypedOutput.addPart("model_number", new TypedString(model_number));
            multipartTypedOutput.addPart("quantity", new TypedString(quantity));
            multipartTypedOutput.addPart("specification", new TypedString(specification));
            multipartTypedOutput.addPart("color", new TypedString(color));
            multipartTypedOutput.addPart("size", new TypedString(size));
            multipartTypedOutput.addPart("product_id", new TypedString(product_id));
            multipartTypedOutput.addPart("preference_id", new TypedString(getIntent().getStringExtra("preference_id")));


            if (!discount.equals(""))
            {
                multipartTypedOutput.addPart("discount", new TypedString(discount));
                multipartTypedOutput.addPart("coupon_title", new TypedString(coupon_title));
                multipartTypedOutput.addPart("coupon_description", new TypedString(coupon_description));
                multipartTypedOutput.addPart("no_of_claims", new TypedString(no_of_claims));
                multipartTypedOutput.addPart("start_date", new TypedString(start_date));
                multipartTypedOutput.addPart("end_date", new TypedString(end_date));
            }

            if (image_thumbnails_product.size() > 0) {
                for (int i = 0; i < image_thumbnails_product.size(); i++) {
                    multipartTypedOutput.addPart("product_photos[]", new TypedFile("application/octet-stream", new File(imageFiles_products.get(i).getAbsolutePath())));
                }
            } else {
                multipartTypedOutput.addPart("product_photos", new TypedString(""));
            }
            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.update_product("Bearer " + PrefManager.getInstance(ProductPreviewActivity.this).getApiToken(),
                    multipartTypedOutput, new Callback<JsonElement>() {
                        @Override
                        public void success(JsonElement jsonElement, Response response) {
                            //this method call if webservice success
                            try {
                                JSONObject jsonObject = new JSONObject(jsonElement.toString());
                                String status = jsonObject.getString("status");
                                p_bar.setVisibility(View.GONE);
                                if (status.equals("1")) {
                                    new SweetAlertDialog(ProductPreviewActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText(getResources().getString(R.string.success))
                                            .setContentText("" + getResources().getString(R.string.product_updated))
                                            .setConfirmText(getResources().getString(R.string.ok))
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    startActivity(new Intent(ProductPreviewActivity.this,MyPostingActivity.class));
                                                    finish();
                                                }
                                            })
                                            .show();
                                } else {
                                    Toast.makeText(ProductPreviewActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }


                            } catch (JSONException | NullPointerException e) {
                                e.printStackTrace();

                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            p_bar.setVisibility(View.GONE);
                            Toast.makeText(ProductPreviewActivity.this, getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                            Log.i("fdfdfdfdfdf", "" + error.getMessage());

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    public void SaveProductDetails() {
        try {
            String user_id = PrefManager.getInstance(ProductPreviewActivity.this).getUserId();
            p_bar.setVisibility(View.VISIBLE);
            MultipartTypedOutput multipartTypedOutput = new MultipartTypedOutput();
            multipartTypedOutput.addPart("name", new TypedString(product_name));
            multipartTypedOutput.addPart("shop_id", new TypedString(shop_id));
            multipartTypedOutput.addPart("description", new TypedString(desc));
            multipartTypedOutput.addPart("price", new TypedString(price));
            multipartTypedOutput.addPart("user_id", new TypedString(user_id));
            multipartTypedOutput.addPart("discount", new TypedString(discount));
            multipartTypedOutput.addPart("brand", new TypedString(brand));
            multipartTypedOutput.addPart("model_number", new TypedString(model_number));
            multipartTypedOutput.addPart("quantity", new TypedString(quantity));
            multipartTypedOutput.addPart("specification", new TypedString(specification));
            multipartTypedOutput.addPart("color", new TypedString(color));
            multipartTypedOutput.addPart("size", new TypedString(size));
            multipartTypedOutput.addPart("preference_id", new TypedString(getIntent().getStringExtra("preference_id")));
            if (!discount.equals(""))
            {
                multipartTypedOutput.addPart("discount", new TypedString(discount));
                multipartTypedOutput.addPart("coupon_title", new TypedString(coupon_title));
                multipartTypedOutput.addPart("coupon_description", new TypedString(coupon_description));
                multipartTypedOutput.addPart("no_of_claims", new TypedString(no_of_claims));
                multipartTypedOutput.addPart("start_date", new TypedString(start_date));
                multipartTypedOutput.addPart("end_date", new TypedString(end_date));
            }
            if (image_thumbnails_product.size() > 0) {
                for (int i = 0; i < image_thumbnails_product.size(); i++) {
                    multipartTypedOutput.addPart("product_photos[]", new TypedFile("application/octet-stream", new File(imageFiles_products.get(i).getAbsolutePath())));
                }
            } else {
                multipartTypedOutput.addPart("product_photos", new TypedString(""));
            }
            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.addProduct("Bearer " + PrefManager.getInstance(ProductPreviewActivity.this).getApiToken(),
                    multipartTypedOutput, new Callback<JsonElement>() {
                        @Override
                        public void success(JsonElement jsonElement, Response response) {
                            try {
                                JSONObject jsonObject = new JSONObject(jsonElement.toString());
                                String status = jsonObject.getString("status");
                                if (status.equals("1")) {
                                    Toast.makeText(ProductPreviewActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                    new SweetAlertDialog(ProductPreviewActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText(getResources().getString(R.string.success))
                                            .setContentText("" + jsonObject.getString("message"))
                                            .setConfirmText(getResources().getString(R.string.ok))
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    finish();
                                                }
                                            })
                                            .show();
                                } else {
                                    Toast.makeText(ProductPreviewActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }

                                p_bar.setVisibility(View.GONE);
                            } catch (JSONException | NullPointerException e) {
                                e.printStackTrace();

                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            p_bar.setVisibility(View.GONE);
                            Toast.makeText(ProductPreviewActivity.this, getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                            Log.i("fdfdfdfdfdf", "" + error.getMessage());

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();

        }


    }
}
