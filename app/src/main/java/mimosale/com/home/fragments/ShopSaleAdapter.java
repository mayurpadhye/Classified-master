package mimosale.com.home.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;

import cn.pedant.SweetAlert.SweetAlertDialog;
import mimosale.com.R;

import mimosale.com.helperClass.PrefManager;
import mimosale.com.home.shop_sale.ShopSaleModel;
import mimosale.com.login.LoginActivity;
import mimosale.com.network.RestInterface;
import mimosale.com.network.RetrofitClient;
import mimosale.com.network.WebServiceURLs;
import mimosale.com.products.ProductDetailsActivityNew;
import mimosale.com.shop.ShopDetailsActivityNew;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.google.gson.JsonElement;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;
import com.squareup.picasso.Picasso;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ShopSaleAdapter extends RecyclerView.Adapter<ShopSaleAdapter.MyViewHolder> {
    List<ShopSaleModel> allProductPojoList;

    Context mctx;
    Shimmer shimmer;
    ProgressDialog pDialog;

    public ShopSaleAdapter(List<ShopSaleModel> allProductPojoList, Context mctx) {
        this.allProductPojoList = allProductPojoList;
        this.mctx = mctx;
        shimmer = new Shimmer();
        pDialog = new ProgressDialog(mctx);
        pDialog.setMessage("Loading...");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;

        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_shop_sale, parent, false);
        itemView.getLayoutParams().width = (int) (getScreenWidth() / 1.5);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        if (position == allProductPojoList.size()) {


           /* holder.cv_add_pref.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //homeFragment.addPost();
                    mctx.startActivity(new Intent(mctx, MyPreferencesActivity.class));

                }
            });*/

        } else {
            final ShopSaleModel items = allProductPojoList.get(position);

            holder.tv_fragment.animate().alpha(0f).setDuration(2000);
            holder.tv_fragment.animate().alpha(1f).setDuration(2000);
            holder.tv_fragment.bringToFront();
            holder.tv_product_name.setText(items.getName());
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.placeholder_logo);
            requestOptions.fitCenter();
            holder.shimmer_view_container1.startShimmerAnimation();
            shimmer.start(holder.shimmer_premium);
            Picasso.with(mctx).load(WebServiceURLs.SHOP_IMAGE + items.getImage()).resize(120, 120).into(holder.iv_product_image1);
            Picasso.with(mctx).load(WebServiceURLs.SHOP_IMAGE + items.getImage2()).resize(120, 120).into(holder.iv_product_image2);
            holder.tv_price_range.setText("" + items.getLow_price() + "-" + items.getHigh_price());
            holder.tv_desc.setText(items.getDescription());
            if (!items.getDiscount().equals("null"))
                holder.tv_discount.setText(items.getDiscount() + "%");
            else {
                holder.tv_discount.setVisibility(View.GONE);
            }

            if (items.getFav_status().equals("1")) {
                holder.spark_button.setChecked(true);
            } else {
                holder.spark_button.setChecked(false);
            }

            if (position == 0) {
                holder.ratingBar.setRating(2.5f);
            }

            holder.cv_shop_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (items.getType().equals("shop"))
                        mctx.startActivity(new Intent(mctx, ShopDetailsActivityNew.class).putExtra("shop_id", items.getId()).putExtra("shop_name", items.getName()));

                    else
                        mctx.startActivity(new Intent(mctx, ProductDetailsActivityNew.class).putExtra("product_id", items.getId()));
                }
            });

            if (items.getLike_status().equals("1"))
            {
                holder.iv_like.setImageDrawable(mctx.getResources().getDrawable(R.drawable.like_hand_red));
            }
            else
            {
                holder.iv_like.setImageDrawable(mctx.getResources().getDrawable(R.drawable.like_black));
            }

            holder.iv_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PrefManager.getInstance(mctx).IS_LOGIN())
                    {
                        likeProduct(items.getId(),position,holder.iv_like,items.getLike_status());
                    }
                    else
                    {
                        dialogLoginWarning("shop_fav",holder.spark_button);

                    }
                }
            });

            holder.spark_button.setEventListener(new SparkEventListener() {
                @Override
                public void onEvent(ImageView button, boolean buttonState) {
                    if (buttonState) {

                        if (PrefManager.getInstance(mctx).IS_LOGIN()) {
                            addToFavorite(items.getId(), position, holder.spark_button);
                        } else {
                            dialogLoginWarning("shop_fav", holder.spark_button);

                        }

                    } else {

                        if (PrefManager.getInstance(mctx).IS_LOGIN()) {
                            removeFromFavorite(items.getId(), position, holder.spark_button);
                        } else {
                            dialogLoginWarning("shop_fav", holder.spark_button);

                        }
                        // Button is inactive
                    }
                }

                @Override
                public void onEventAnimationEnd(ImageView button, boolean buttonState) {

                }

                @Override
                public void onEventAnimationStart(ImageView button, boolean buttonState) {

                }
            });

        }

    }


    @Override
    public int getItemCount() {
        return allProductPojoList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView iv_product_image2, iv_product_image1;
        ImageView iv_like;
        TextView tv_desc, tv_price_range, tv_product_name, tv_discount, tv_fragment;
        RatingBar ratingBar;
        SparkButton spark_button;
        ShimmerTextView shimmer_premium;
        CardView cv_shop_main;
        ShimmerFrameLayout shimmer_view_container1;
        RelativeLayout rl_view_more;

        public MyViewHolder(View view) {
            super(view);
            tv_fragment = view.findViewById(R.id.tv_premium);
            rl_view_more = view.findViewById(R.id.rl_view_more);
            iv_product_image2 = (RoundedImageView) view.findViewById(R.id.iv_product_image2);
            iv_product_image1 = (RoundedImageView) view.findViewById(R.id.iv_product_image1);
            iv_like = (ImageView) view.findViewById(R.id.iv_like);
            tv_desc = (TextView) view.findViewById(R.id.tv_desc);
            tv_discount = (TextView) view.findViewById(R.id.tv_discount);
            tv_price_range = (TextView) view.findViewById(R.id.tv_price_range);
            tv_product_name = (TextView) view.findViewById(R.id.tv_product_name);
            spark_button = (SparkButton) view.findViewById(R.id.spark_button);
            ratingBar = view.findViewById(R.id.ratingBar);
            shimmer_premium = view.findViewById(R.id.shimmer_premium);
            shimmer_view_container1 = view.findViewById(R.id.shimmer_view_container1);
            cv_shop_main = view.findViewById(R.id.cv_shop_main);

            //   shimmer.start(shimmer_premium);
        }
    }

    public int getScreenWidth() {

        WindowManager wm = (WindowManager) mctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size.x;
    }


    public void likeProduct(String product_id, final int position, final ImageView iv_like, final String like_status) {
        String like_flag = "";
        if (like_status.equals("0")) {
            like_flag = "like";
        } else
            like_flag = "unlike";


        try {
            pDialog.show();
            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.like_product("shop", PrefManager.getInstance(mctx).getUserId(), like_flag, product_id, "Bearer " + PrefManager.getInstance(mctx).getApiToken(), new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {

                    try {
                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        pDialog.dismiss();
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (message.equals("Liked")) {
                            allProductPojoList.get(position).setLike_status("1");
                            notifyItemChanged(position);
                            iv_like.setImageDrawable(mctx.getResources().getDrawable(R.drawable.like_hand_black));

                        } else {
                            allProductPojoList.get(position).setLike_status("0");
                            notifyItemChanged(position);
                            iv_like.setImageDrawable(mctx.getResources().getDrawable(R.drawable.like_black));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    pDialog.dismiss();
                    Toast.makeText(mctx, mctx.getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                    Log.i("fdfdfdfdfdf", "" + error.getMessage());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            pDialog.dismiss();
            Log.i("detailsException", "" + e.toString());
        }
    }

    public void addToFavorite(String shop_id, int position, final SparkButton spark_button) {
        try {
            pDialog.show();
            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.add_to_fav("shop", PrefManager.getInstance(mctx).getUserId(), shop_id, "Bearer " + PrefManager.getInstance(mctx).getApiToken(), new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {

                    try {
                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        pDialog.dismiss();
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equals("1")) {
                            spark_button.setChecked(true);
                            allProductPojoList.get(position).setFav_status("1");
                            notifyItemChanged(position);
                            Toast.makeText(mctx, "" + message, Toast.LENGTH_SHORT).show();

                        } else {
                            spark_button.setChecked(true);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    pDialog.dismiss();
                    Toast.makeText(mctx, mctx.getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                    Log.i("fdfdfdfdfdf", "" + error.getMessage());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            pDialog.dismiss();
            Log.i("detailsException", "" + e.toString());
        }

    }

    public void removeFromFavorite(String shop_id, int position, final SparkButton sparkButton) {
        try {
            pDialog.show();
            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.remove_from_fav("shop", PrefManager.getInstance(mctx).getUserId(), shop_id, "Bearer " + PrefManager.getInstance(mctx).getApiToken(), new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        pDialog.dismiss();
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equals("1")) {
                            Toast.makeText(mctx, "" + message, Toast.LENGTH_SHORT).show();
                            sparkButton.setChecked(false);
                            allProductPojoList.get(position).setFav_status("0");
                            notifyItemChanged(position);
                        } else {
                            sparkButton.setChecked(true);
                            allProductPojoList.get(position).setFav_status("1");
                            notifyItemChanged(position);
                        }

                    } catch (JSONException e) {
                        sparkButton.setChecked(true);
                        e.printStackTrace();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    pDialog.dismiss();
                    sparkButton.setChecked(true);
                    Toast.makeText(mctx, mctx.getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                    Log.i("fdfdfdfdfdf", "" + error.getMessage());
                    }
            });
        } catch (Exception e) {
            e.printStackTrace();
            pDialog.dismiss();
            Log.i("detailsException", "" + e.toString());
        }

    }

    public void dialogLoginWarning(final String intent_from, final SparkButton spark_button) {

        new SweetAlertDialog(mctx, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(mctx.getResources().getString(R.string.login_waning))
                .setContentText(mctx.getResources().getString(R.string.please_login))
                .setConfirmText(mctx.getResources().getString(R.string.login))
                .setCancelText(mctx.getResources().getString(R.string.cancel))

                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        Intent i = new Intent(mctx, LoginActivity.class);
                        i.putExtra("intent_from", intent_from);
                        ((Activity) mctx).startActivityForResult(i, 1);
                        sDialog.dismissWithAnimation();
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        spark_button.setChecked(false);
                    }
                })
                .show();

    }

}
