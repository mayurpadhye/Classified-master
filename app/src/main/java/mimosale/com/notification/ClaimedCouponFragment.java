package mimosale.com.notification;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.JsonElement;

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
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClaimedCouponFragment extends Fragment {
@BindView(R.id.rv_claimed_coupons)
    RecyclerView rv_claimed_coupons;
View v;
    ProgressDialog pDialog;
    List<ClaimedCouponPojo> claimedCouponPojoList=new ArrayList<>();
    public ClaimedCouponFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_claimed_coupon, container, false);
        ButterKnife.bind(this,v);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getResources().getString(R.string.loading));
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getActivity(), 1);
        gridLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        rv_claimed_coupons.setLayoutManager(gridLayoutManager1);
        getClaimedCoupons();
        return v;
    }

    public void getClaimedCoupons() {
        try {
            pDialog.show();
            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.getClaimedCoupons(PrefManager.getInstance(getActivity()).getUserId(), "Bearer " + PrefManager.getInstance(getActivity()).getApiToken(), new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        String status = jsonObject.getString("status");
                        if (status.equals("1")) {
                            claimedCouponPojoList.clear();
                            JSONArray data = jsonObject.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject j1 = data.getJSONObject(i);
                                String id = j1.getString("id");
                                String coupon_id = j1.getString("coupon_id");
                                String user_id = j1.getString("user_id");

                                String title = j1.getString("title");
                                String description = j1.getString("description");
                                String discount = j1.getString("discount");
                                String start_date = j1.getString("start_date");
                                String end_date = j1.getString("end_date");
                                String no_of_claims = j1.getString("no_of_claims");
                                String status1 = j1.getString("status");

                                String totalClaimedCoupons = j1.getString("totalClaimedCoupons");
                                String type = j1.getString("type");
                                String coupon_image = j1.getString("coupon_image");
                                String shop_id="";
                                if (j1.has("product_id"))
                                {
                                    shop_id = j1.getString("product_id");
                                }
                                else
                                {
                                     shop_id = j1.getString("shop_id");
                                }


                                claimedCouponPojoList.add(new ClaimedCouponPojo(id, coupon_id, user_id, shop_id, title, description, discount, start_date, end_date, no_of_claims, status1, "", totalClaimedCoupons, type, coupon_image));

                            }
                            ClaimedCouponsAdapter adapter=new ClaimedCouponsAdapter(claimedCouponPojoList, getActivity(), new onRecyclerViewItemClick() {
                                @Override
                                public void onItemClick(View v, int position) {
                                    startActivity(new Intent(getActivity(),MyCouponsDetails.class).putExtra("coupon_id",claimedCouponPojoList.get(position).getCoupon_id()).putExtra("type",claimedCouponPojoList.get(position).getType()).putExtra("Isclaimed","true").putExtra("shop_id",claimedCouponPojoList.get(position).getShop_id()));
                                }
                            });
                            rv_claimed_coupons.setAdapter(adapter);
                            pDialog.dismiss();
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
                    Toast.makeText(getActivity(), getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                    Log.i("fdfdfdfdfdf", "" + error.getMessage());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}
