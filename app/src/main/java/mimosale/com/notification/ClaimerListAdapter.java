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

public class ClaimerListAdapter extends RecyclerView.Adapter<ClaimerListAdapter.MyViewHolder> {
    List<ClaimerListPojo> claimerListPojoList;
    SimpleDateFormat formatter4 = new SimpleDateFormat("E, MMM dd yyyy");
    Context mctx;
    Shimmer shimmer;
    mimosale.com.onRecyclerViewItemClick onRecyclerViewItemClick;

    public ClaimerListAdapter( List<ClaimerListPojo> claimerListPojoList, Context mctx, onRecyclerViewItemClick onRecyclerViewItemClick) {
        this.claimerListPojoList = claimerListPojoList;
        this.mctx = mctx;
        this.onRecyclerViewItemClick = onRecyclerViewItemClick;
        shimmer = new Shimmer();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_claimer_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final ClaimerListPojo items = claimerListPojoList.get(position);
        holder.tv_name.setText(items.getFirst_name()+" "+items.getLast_name());
        Picasso.with(mctx).load(WebServiceURLs.IMAGE_URL+items.getProfile_image()).into(holder.cv_profile);

    }
    @Override
    public int getItemCount() {
        return claimerListPojoList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.cv_profile)
        CircleImageView cv_profile;
        @BindView(R.id.tv_name)
        TextView tv_name;
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
    }}
