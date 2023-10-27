package com.sourav.vegetables.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.sourav.vegetables.Api.ApiURL;
import com.sourav.vegetables.Model.Banner;
import com.sourav.vegetables.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.ViewHolder> {

    private List<Banner> bannerList;
    private Context mCtx;

    public BannerAdapter(List<Banner> bannerList, Context mCtx) {
        this.bannerList = bannerList;
        this.mCtx = mCtx;

        setHasStableIds(true);
    }

    @Override
    public BannerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_banner, parent, false);
        return new BannerAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final BannerAdapter.ViewHolder holder, final int position) {
        final Banner banner = bannerList.get(position);
        Picasso.with(mCtx)
                .load(ApiURL.SERVER_URL + banner.getImage_url())
                .error(R.drawable.food)
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mCtx, banner.getName() + " clicked", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return bannerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.imageViewItem);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }
}
