package mimosale.com.favourite;


import android.app.ProgressDialog;
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
import mimosale.com.home.fragments.ShopSaleAdapter;
import mimosale.com.home.shop_sale.ShopSaleModel;
import mimosale.com.network.RestInterface;
import mimosale.com.network.RetrofitClient;
import mimosale.com.network.WebServiceURLs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteFragment extends Fragment {

View v;
@BindView(R.id.rv_fav)
    RecyclerView rv_fav;
List<FavListPojo> allFavList=new ArrayList<>();
ProgressDialog pDialog;
    public FavouriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_favourite2, container, false);
        ButterKnife.bind(this,v);
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getActivity(), 1);
        gridLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        rv_fav.setLayoutManager(gridLayoutManager1);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getResources().getString(R.string.loading));
        getFavList();

        return v;
    }

    public void getFavList()
    {
        try {
            pDialog.show();
            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.getFavList(PrefManager.getInstance(getActivity()).getUserId(),"Bearer "+PrefManager.getInstance(getActivity()).getApiToken(),new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {
                    //this method call if webservice success
                    try {

                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        String status = jsonObject.getString("status");

                        if (status.equals("1")) {
                            JSONArray data = jsonObject.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject j1 = data.getJSONObject(i);
                                String id = j1.getString("id");
                                String name = j1.getString("name");
                                String user_id = j1.getString("user_id");
                                String preference_id = j1.getString("preference_id");
                            //    String discount = j1.getString("discount");
                                String like_count = j1.getString("like_count");
                                String type = j1.getString("type");
                                String description = j1.getString("description");
                                String price="";
                                if (j1.has("price"))
                                {
                                    price=j1.getString("price");
                                }
                                String selling_price="";
                                if (j1.has("selling_price"))
                                {
                                    selling_price=j1.getString("selling_price");
                                }

                                if (j1.has("low_price"))
                                {
                                    price=j1.getString("low_price")+" - "+j1.getString("high_price");

                                }
                                String image2="";
                                String image1="";
                                if(j1.has("image1")) {

                                    image1 = j1.getString("image1");
                                }
                                if(j1.has("image2"))
                                {
                                    image2 = j1.getString("image2");
                                }

                                String image = "";
                                allFavList.add(new FavListPojo( id,  name,  user_id,  preference_id,  like_count,price,type,  image1,  image2,  "",true,description));
                            }

                            FavoriteListAdapter adapter=new FavoriteListAdapter(allFavList,getActivity());
                            rv_fav.setAdapter(adapter);
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
    }//getFavList

}
