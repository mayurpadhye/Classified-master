package mimosale.com.notification;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.romainpiel.shimmer.Shimmer;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import mimosale.com.R;
import mimosale.com.network.WebServiceURLs;
import mimosale.com.onRecyclerViewItemClick;

public class ClaimedCouponsAdapter extends RecyclerView.Adapter<ClaimedCouponsAdapter.MyViewHolder> {
    List<ClaimedCouponPojo> claimedCouponPojoList;
    SimpleDateFormat formatter4 = new SimpleDateFormat("E, MMM dd yyyy");
    Context mctx;
    Shimmer shimmer;
    onRecyclerViewItemClick onRecyclerViewItemClick;
    public ClaimedCouponsAdapter(List<ClaimedCouponPojo> claimedCouponPojoList, Context mctx,onRecyclerViewItemClick onRecyclerViewItemClick) {
        this.claimedCouponPojoList = claimedCouponPojoList;
        this.mctx = mctx;
this.onRecyclerViewItemClick=onRecyclerViewItemClick;
        shimmer = new Shimmer();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_coupon_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final ClaimedCouponPojo items = claimedCouponPojoList.get(position);
        holder.tv_title.setText(items.getTitle());
        holder.tv_shop_name.setText(items.getShop_name());
        Picasso.with(mctx).load(WebServiceURLs.SHOP_IMAGE + items.getCoupon_image()).into(holder.cv_image);
        try {

            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
            Date date = inputFormat.parse(items.getEnd_date());
            String outputDateStr = outputFormat.format(date);
            holder.tv_validity.setText(mctx.getResources().getString(R.string.valid_till) + ": " + outputDateStr);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return claimedCouponPojoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        @BindView(R.id.cv_image)
        CircleImageView cv_image;
        @BindView(R.id.tv_title)
        TextView tv_title;
        @BindView(R.id.tv_shop_name)
        TextView tv_shop_name;
        @BindView(R.id.tv_validity)
        TextView tv_validity;
        @BindView(R.id.tv_discount)
        TextView tv_discount;

        @BindView(R.id.cv_main)
        CardView cv_main;
        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            cv_main.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onRecyclerViewItemClick.onItemClick(v,getAdapterPosition());
        }
    }

}
