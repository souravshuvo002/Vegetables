package com.sourav.vegetables.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sourav.vegetables.Activity.FoodsActivity;
import com.sourav.vegetables.Api.ApiURL;
import com.sourav.vegetables.Common.Common;
import com.sourav.vegetables.Model.Category;
import com.sourav.vegetables.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder>{

    private List<Category> menuList;
    private Context mCtx;

    public MenuAdapter(List<Category> menuList, Context mCtx) {
        this.menuList = menuList;
        this.mCtx = mCtx;
        setHasStableIds(true);
    }

    @Override
    public MenuAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_category_4, parent, false);
        return new MenuAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MenuAdapter.ViewHolder holder, final int position) {
        final Category menu = menuList.get(position);

        /**
         *  Animation Part
         */
        setFadeAnimation(holder.itemView);

        holder.textViewName.setText(menu.getName());
        holder.textViewTotalItems.setText(menu.getTotal_food_items() + " items");
        Picasso.with(mCtx)
                //.load(R.drawable.food)
                .load(ApiURL.SERVER_URL + menu.getImage_url())
                .error(R.drawable.cutlery)
                .into(holder.imageView);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCtx, FoodsActivity.class);
                intent.putExtra("MENU_ID", menu.getId());
                intent.putExtra("MENU_NAME", menu.getName());
                Common.menu_id = menu.getId();
                mCtx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textViewName, textViewTotalItems;
        public ImageView imageView;
        public RelativeLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewName = (TextView) itemView.findViewById(R.id.item_name);
            textViewTotalItems = (TextView) itemView.findViewById(R.id.textViewTotalItems);
            imageView = (ImageView) itemView.findViewById(R.id.item_image);
            linearLayout = (RelativeLayout) itemView.findViewById(R.id.linearLay);

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

    public void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        view.startAnimation(anim);
    }
}