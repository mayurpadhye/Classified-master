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
import mimosale.com.favourite.FavListPojo;
import mimosale.com.favourite.FavoriteListAdapter;
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
public class MyCouponFragment extends Fragment {
    View v;
    @BindView(R.id.rv_my_coupon)
    RecyclerView rv_my_coupon;
    ProgressDialog pDialog;
    List<MyCouponsPojo> myCouponsPojoList = new ArrayList<>();

    public MyCouponFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_my_coupon, container, false);
        ButterKnife.bind(this, v);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getResources().getString(R.string.loading));
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getActivity(), 1);
        gridLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        rv_my_coupon.setLayoutManager(gridLayoutManager1);
        getMyCoupons();
        return v;
    }

    public void getMyCoupons() {
        try {
            pDialog.show();
            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.getMyCouponsList(PrefManager.getInstance(getActivity()).getUserId(), "Bearer " + PrefManager.getInstance(getActivity()).getApiToken(), new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        String status = jsonObject.getString("status");
                        if (status.equals("1")) {
                            myCouponsPojoList.clear();
                            JSONArray data = jsonObject.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject j1 = data.getJSONObject(i);

                                String type = j1.getString("type");
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
                                String coupon_image = j1.getString("coupon_image");
                                String shop_id="",shop_name="";
                               if (type.equals("shop"))
                                {
                                     shop_id = j1.getString("shop_id");
                                    shop_name = j1.getString("shop_name");


                                }
                                else
                               {

                                   shop_id = j1.getString("product_id");
                                   shop_name = j1.getString("product_name");



                               }
                                myCouponsPojoList.add(new MyCouponsPojo(id, coupon_id, user_id, shop_id, title, description, discount, start_date, end_date, no_of_claims, status1, shop_name, totalClaimedCoupons, type, coupon_image));



                            }
                            MyCouponAdapter adapter=new MyCouponAdapter(myCouponsPojoList, getActivity(), new onRecyclerViewItemClick() {
                                @Override
                                public void onItemClick(View v,int position) {
                                    if (v.getId()==R.id.cv_main)
                                    {
                                        startActivity(new Intent(getActivity(),MyCouponsDetails.class).putExtra("coupon_id",myCouponsPojoList.get(position).getCoupon_id()).putExtra("type",myCouponsPojoList.get(position).getType()).putExtra("Isclaimed","false").putExtra("shop_id",myCouponsPojoList.get(position).getShop_id()));

                                    }
                                }
                            });
                            rv_my_coupon.setAdapter(adapter);

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
