package com.sourav.vegetables.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sourav.vegetables.Activity.FoodsActivity;
import com.sourav.vegetables.Api.ApiURL;
import com.sourav.vegetables.Common.Common;
import com.sourav.vegetables.Model.Cart;
import com.sourav.vegetables.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class CheckOutAdapter extends RecyclerView.Adapter<CheckOutAdapter.ViewHolder> {

    private List<Cart> cartList;
    private Context context;
    private int lastPosition = -1;


    public CheckOutAdapter(List<Cart> carts, Context context) {
        this.cartList = carts;
        this.context = context;
    }

    @Override
    public CheckOutAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_checkout_items_2, parent, false);
        return new CheckOutAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CheckOutAdapter.ViewHolder holder, final int position) {
        final Cart cart = cartList.get(position);

        /**
         *  Animation Part
         */
        setFadeAnimation(holder.itemView);

        holder.textViewItemName.setText(cart.getName());
        //holder.textViewUnit.setText(cart.getMin_unit_amount() + " " + cart.getUnit());
        holder.textViewUnit.setText("[" + cart.getMin_unit_amount() + " " + cart.getUnit() + "]");
        holder.textViewQuantity.setText("Quantity: " + cart.getQuantity());
        Picasso.with(context)
                //.load(R.drawable.food)
                .load(ApiURL.SERVER_URL + cart.getImage_url())
                .error(R.drawable.cutlery)
                .into(holder.imageViewItem);

        holder.textViewPrice.setText(new StringBuilder(context.getResources().getString(R.string.currency_sign)).append((Double.parseDouble(cart.getPrice())) * (Double.parseDouble(cart.getQuantity()))));

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FoodsActivity.class);
                intent.putExtra("MENU_ID", cart.getId_menu());
                intent.putExtra("MENU_NAME", cart.getMenu_name());
                Common.menu_id = cart.getId_menu();
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewQuantity, textViewItemName, textViewPrice, textViewUnit;
        public ImageView imageViewItem;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewItemName = (TextView) itemView.findViewById(R.id.textViewItemName);
            textViewUnit = (TextView) itemView.findViewById(R.id.textViewUnit);
            textViewQuantity = (TextView) itemView.findViewById(R.id.textViewQuantity);
            imageViewItem = (ImageView) itemView.findViewById(R.id.image_item);
            textViewPrice = (TextView) itemView.findViewById(R.id.textViewItemPrice);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLay);

        }
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