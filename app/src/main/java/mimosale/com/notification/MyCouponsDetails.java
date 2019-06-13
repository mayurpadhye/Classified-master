package mimosale.com.notification;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonElement;
import com.romainpiel.shimmer.Shimmer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import mimosale.com.R;
import mimosale.com.helperClass.PrefManager;
import mimosale.com.network.RestInterface;
import mimosale.com.network.RetrofitClient;
import mimosale.com.network.WebServiceURLs;
import mimosale.com.onRecyclerViewItemClick;
import mimosale.com.products.ProductDetailsActivityNew;
import mimosale.com.shop.ShopDetailsActivityNew;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyCouponsDetails extends AppCompatActivity {
    @BindView(R.id.rv_claimer_list)
    RecyclerView rv_claimer_list;
    @BindView(R.id.tv_shop_desc)
    TextView tv_shop_desc;
    @BindView(R.id.tv_shop_name)
    TextView tv_shop_name;
    @BindView(R.id.tv_coupon_number)
    TextView tv_coupon_number;
    @BindView(R.id.tv_desc)
    TextView tv_desc;
    ProgressDialog pDialog;
    @BindView(R.id.tv_discount)
    TextView tv_discount;
    @BindView(R.id.toolbar_title)
            TextView toolbar_title;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.tv_claimer)
            TextView tv_claimer;
    String coupon_id = "", type = "",Isclaimed="";

@BindView(R.id.btn_goto)
Button btn_goto;
String mimo_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_coupons_details);
        ButterKnife.bind(this);
        pDialog = new ProgressDialog(MyCouponsDetails.this);
        pDialog.setMessage(getResources().getString(R.string.loading));
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(MyCouponsDetails.this, 1);
        gridLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        rv_claimer_list.setLayoutManager(gridLayoutManager1);
        coupon_id=getIntent().getStringExtra("coupon_id");
        type=getIntent().getStringExtra("type");
        Isclaimed=getIntent().getStringExtra("Isclaimed");
        if (type.equals("product"))
        {
            btn_goto.setText(getResources().getString(R.string.goto_product));
            mimo_id=getIntent().getStringExtra("shop_id");
        }
        else
        {
            btn_goto.setText(getResources().getString(R.string.goto_shop));
            mimo_id=getIntent().getStringExtra("shop_id");
        }

        btn_goto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("product"))
                {
                    startActivity(new Intent(MyCouponsDetails.this,ProductDetailsActivityNew.class).putExtra("product_id",mimo_id));
                }
                else
                {
                    startActivity(new Intent(MyCouponsDetails.this,ShopDetailsActivityNew.class).putExtra("shop_id" +
                            "",mimo_id));

                }
            }
        });

        if (Isclaimed.equals("false"))
        {
            tv_claimer.setVisibility(View.VISIBLE);
            getCouponDetails();
        }

        else
        {
            getClaimedCoupons();
            tv_claimer.setVisibility(View.GONE);
        }

        toolbar_title.setText(getResources().getString(R.string.coupon_details));
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }//onCreateClose

    public void getCouponDetails() {
        try {
            pDialog.show();
            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.getCouponDetails(coupon_id, type, PrefManager.getInstance(MyCouponsDetails.this).getUserId(), "Bearer " + PrefManager.getInstance(MyCouponsDetails.this).getApiToken(), new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        String status = jsonObject.getString("status");
                        if (status.equals("1")) {
                            pDialog.dismiss();
                            JSONObject data = jsonObject.getJSONObject("data");
                            String id = data.getString("id");
                            String coupon_id = data.getString("coupon_id");
                            String user_id = data.getString("user_id");
                            String shop_id = data.getString("shop_id");
                            String title = data.getString("title");
                            String description = data.getString("description");
                            String discount = data.getString("discount");
                            String start_date = data.getString("start_date");
                            String end_date = data.getString("end_date");
                            String no_of_claims = data.getString("no_of_claims");
                            String totalClaimedCoupons = data.getString("totalClaimedCoupons");
                            tv_discount.setText(discount+"%");
                            tv_desc.setText(description);
                            tv_coupon_number.setText(coupon_id);
                            JSONArray claimerList = data.getJSONArray("claimerList");
                            List<ClaimerListPojo> claimerListPojoList = new ArrayList<>();
                            for (int i = 0; i < claimerList.length(); i++) {
                                JSONObject j1 = claimerList.getJSONObject(i);
                                String user_idd = j1.getString("id");
                                String first_name = j1.getString("first_name");
                                String last_name = j1.getString("last_name");
                                String username = j1.getString("username");
                                String email = j1.getString("email");
                                String mobile = j1.getString("mobile");
                                String profile_image = j1.getString("profile_image");
                                claimerListPojoList.add(new ClaimerListPojo(user_idd, first_name, last_name, username, email, mobile, profile_image));
                            }
                            if (claimerList.length()>0)
                            {
                                tv_claimer.setVisibility(View.VISIBLE);
                                ClaimerListAdapter adapter = new ClaimerListAdapter(claimerListPojoList, MyCouponsDetails.this, new onRecyclerViewItemClick() {
                                    @Override
                                    public void onItemClick(View v,int postion) {

                                    }
                                });
                                rv_claimer_list.setAdapter(adapter);
                            }
                            else
                            {
                                tv_claimer.setVisibility(View.GONE);
                            }

                            JSONObject shop=data.getJSONObject("shop");
                            String name= shop.getString("name");
                            String description_shop= shop.getString("description");
                            tv_shop_name.setText(name);
                            tv_shop_desc.setText(description_shop);

                        }

                    } catch (JSONException | NullPointerException e) {
                        pDialog.dismiss();
                        e.printStackTrace();
                        Log.i("fdfdfdfdfdf", "" + e.getMessage());

                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    pDialog.dismiss();
                    Toast.makeText(MyCouponsDetails.this, getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                    Log.i("fdfdfdfdfdf", "" + error.getMessage());

                }
            });
        } catch (Exception e) {
            pDialog.dismiss();
            e.printStackTrace();

        }
    }


    public void getClaimedCoupons()
    {
        try {
            pDialog.show();
            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.getCliamedCoupons(coupon_id, type, PrefManager.getInstance(MyCouponsDetails.this).getUserId(), "Bearer " + PrefManager.getInstance(MyCouponsDetails.this).getApiToken(), new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        String status = jsonObject.getString("status");
                        if (status.equals("1")) {
                            pDialog.dismiss();
                            JSONArray data = jsonObject.getJSONArray("data");
                            for (int i =0;i<data.length();i++)
                            {
                                JSONObject j1=data.getJSONObject(i);
                                String id = j1.getString("id");
                                String coupon_id = j1.getString("coupon_id");
                                String user_id = j1.getString("user_id");
                                String shop_id = j1.getString("shop_id");
                                String title = j1.getString("title");
                                String description = j1.getString("description");
                                String discount = j1.getString("discount");
                                String start_date = j1.getString("start_date");
                                String end_date = j1.getString("end_date");
                                String no_of_claims = j1.getString("no_of_claims");

                                tv_discount.setText(discount+"%");
                                tv_desc.setText(description);
                                tv_coupon_number.setText(coupon_id);

                                JSONObject shop=j1.getJSONObject("shop");
                                String name= shop.getString("name");
                                String description_shop= shop.getString("description");
                                tv_shop_name.setText(name);
                                tv_shop_desc.setText(description_shop);
                            }



                        }

                    } catch (JSONException | NullPointerException e) {
                        pDialog.dismiss();
                        e.printStackTrace();
                        Log.i("fdfdfdfdfdf", "" + e.getMessage());

                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    pDialog.dismiss();
                    Toast.makeText(MyCouponsDetails.this, getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                    Log.i("fdfdfdfdfdf", "" + error.getMessage());

                }
            });
        } catch (Exception e) {
            pDialog.dismiss();
            e.printStackTrace();

        }
    }
}
