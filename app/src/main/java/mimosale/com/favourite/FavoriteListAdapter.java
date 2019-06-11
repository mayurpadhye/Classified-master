package mimosale.com.favourite;

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

import mimosale.com.R;
import mimosale.com.helperClass.PrefManager;
import mimosale.com.home.fragments.ShopSaleAdapter;
import mimosale.com.home.shop_sale.ShopSaleModel;
import mimosale.com.network.RestInterface;
import mimosale.com.network.RetrofitClient;
import mimosale.com.network.WebServiceURLs;
import mimosale.com.products.ProductDetailsActivityNew;
import mimosale.com.shop.ShopDetailsActivityNew;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.MyViewHolder> {
    List<FavListPojo> all_fav_list;
    Context mctx;
    Shimmer shimmer;
    ProgressDialog pDialog;

    public FavoriteListAdapter(List<FavListPojo> all_fav_list, Context mctx) {
        this.all_fav_list = all_fav_list;
        this.mctx = mctx;
        shimmer = new Shimmer();
        pDialog = new ProgressDialog(mctx);
        pDialog.setMessage(mctx.getResources().getString(R.string.loading));
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_shop_sale, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

            final FavListPojo items = all_fav_list.get(position);
            holder.tv_fragment.animate().alpha(0f).setDuration(2000);
            holder.tv_fragment.animate().alpha(1f).setDuration(2000);
            holder.tv_fragment.bringToFront();
            holder.tv_product_name.setText(items.getName());
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.placeholder_logo);
            requestOptions.fitCenter();
            holder.shimmer_view_container1.startShimmerAnimation();
            shimmer.start(holder.shimmer_premium);
            Picasso.with(mctx).load(WebServiceURLs.SHOP_IMAGE + items.getImage1()).resize(120, 120).into(holder.iv_product_image1);
            Picasso.with(mctx).load(WebServiceURLs.SHOP_IMAGE + items.getImage2()).resize(120, 120).into(holder.iv_product_image2);
            // Glide.with(mctx).load("http://4.bp.blogspot.com/-zlCqi4iWQb8/Tk_iLyLnoVI/AAAAAAAAED4/egErzO8ARQ0/s1600/s%25C3%25BCti+4548.jpg").into(holder.iv_product_image1);
            //   Glide.with(mctx).setDefaultRequestOptions(requestOptions).load(WebServiceURLs.SHOP_IMAGE + items.getImage2()).into(holder.iv_product_image2);
            holder.tv_price_range.setText("" + items.getPrice());
          holder.spark_button.setChecked(true);

            holder.tv_desc.setText(items.getDescription());
            if (!items.getDiscount().equals("null") )
                holder.tv_discount.setText(items.getDiscount() + "%");
            else
            {
                holder.tv_discount.setVisibility(View.GONE);
            }

            if (position == 0) {
                holder.ratingBar.setRating(2.5f);
            }
        holder.spark_button.setEventListener(new SparkEventListener(){
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                if (buttonState) {

                    if (PrefManager.getInstance(mctx).IS_LOGIN())
                    {
                       // addToFavorite(items.getId(),position);
                    }
                    else
                    {
                       // dialogLoginWarning("shop_fav");

                    }

                } else {

                    if (PrefManager.getInstance(mctx).IS_LOGIN())
                    {
                        removeFromFavorite(items.getId(),position,items.getType());
                    }
                    else
                    {
                     ///   dialogLoginWarning("shop_fav");

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

/*

            holder.cv_shop_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (items.getType().equals("shop"))
                        mctx.startActivity(new Intent(mctx,ShopDetailsActivityNew.class).putExtra("shop_id",items.getId()).putExtra("shop_name",items.getName()));

                    else
                        mctx.startActivity(new Intent(mctx, ProductDetailsActivityNew.class).putExtra("product_id", items.getId()));
                }
            });
*/


    }


    @Override
    public int getItemCount() {
        return all_fav_list.size();
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


    public void removeFromFavorite(String shop_id, final int position,String type)
    {
        try {
            pDialog.show();
            RetrofitClient retrofitClient = new RetrofitClient();
            RestInterface service = retrofitClient.getAPIClient(WebServiceURLs.DOMAIN_NAME);
            service.remove_from_fav(type, PrefManager.getInstance(mctx).getUserId(), shop_id, "Bearer " + PrefManager.getInstance(mctx).getApiToken(), new Callback<JsonElement>() {
                @Override
                public void success(JsonElement jsonElement, Response response) {
                    try {
                        JSONObject jsonObject = new JSONObject(jsonElement.toString());
                        pDialog.dismiss();
                        String status = jsonObject.getString("status");
                        String message = jsonObject.getString("message");
                        if (status.equals("1")) {
                            Toast.makeText(mctx, ""+message, Toast.LENGTH_SHORT).show();
                            all_fav_list.remove(position);
                            notifyDataSetChanged();

                        } else {

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

}
