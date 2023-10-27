package com.sourav.vegetables.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.appmsg.AppMsg;
import com.sourav.vegetables.Activity.FoodsActivity;
import com.sourav.vegetables.Api.ApiURL;
import com.sourav.vegetables.Common.Common;
import com.sourav.vegetables.Database.Database;
import com.sourav.vegetables.Model.Cart;
import com.sourav.vegetables.Model.Order;
import com.sourav.vegetables.R;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import retrofit2.Retrofit;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

public class SingleOrderStatusAdapter extends RecyclerView.Adapter<SingleOrderStatusAdapter.ViewHolder> {

    private List<Order> orderList;
    private Context context;
    private int lastPosition = -1;

    public SingleOrderStatusAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;

    }

    @Override
    public SingleOrderStatusAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_single_order_status_items_2, parent, false);
        return new SingleOrderStatusAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SingleOrderStatusAdapter.ViewHolder holder, final int position) {
        final Order order = orderList.get(position);

        /**
         *  Animation Part
         */
        setFadeAnimation(holder.itemView);

        Picasso.with(context)
                //.load(R.drawable.food)
                .load(ApiURL.SERVER_URL + order.getFood_image_url())
                .error(R.drawable.cutlery)
                .into(holder.imageViewItem);

        holder.textViewItemUnit.setText("[" + order.getFood_min_unit_amount() + " " + order.getFood_unit() + "]");
        holder.textViewItemName.setText(order.getFood_name());
        holder.textViewItemPrice.setText("Unit Price: " + context.getResources().getString(R.string.currency_sign) + order.getFood_price());
        holder.textViewItemTotalPrice.setText("Total Price: " + context.getResources().getString(R.string.currency_sign) + order.getFood_total_price());
        holder.textViewItemQuantity.setText("Quantity: " + order.getFood_quantity());
        holder.textViewMenuName.setText(order.getMenu_name());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FoodsActivity.class);
                intent.putExtra("MENU_ID", order.getId_menu());
                intent.putExtra("MENU_NAME", order.getMenu_name());
                Common.menu_id = order.getId_menu();
                Common.menu_name = order.getMenu_name();

                context.startActivity(intent);
            }
        });

        holder.btnReorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (new Database(context).getCarts().size() >= Integer.parseInt(context.getResources().getString(R.string.cart_item_limit))) {
                    createCartLimitErrorDialog();
                    return;
                }

                // Checking if the product is already added or not
                if (!new Database(context).checkExistence(order.getId_food())) {
                    new Database(context).addToCart(new Cart(
                            order.getId_food(),
                            order.getFood_name(),
                            order.getFood_description(),
                            order.getFood_price(),
                            order.getFood_image_url(),
                            order.getFood_min_unit_amount(),
                            order.getFood_unit(),
                            "1",
                            order.getId_menu(),
                            order.getMenu_name()
                    ));

                    /*AppMsg.makeText((Activity) context, order.getFood_name() + " added to cart (1st) time", new AppMsg.Style(LENGTH_LONG, R.color.toastMessageColor))
                            .setAnimation(android.R.anim.fade_in, android.R.anim.fade_out).show();*/

                    Toasty.success(context, order.getFood_name() + " added to cart (1st) time", Toast.LENGTH_SHORT, true).show();

                } else {
                    // if item is exist in cart then simply increase the quantity value
                    String quantity = new Database(context).countQuantity(order.getId_food(), order.getId_menu());
                    int q1 = Integer.parseInt(quantity) + 1;

                    if (q1 > 15) {

                        //AppMsg.makeText((Activity) context, "Limit Crossed! (Max quantity amount is 15)", new AppMsg.Style(LENGTH_LONG, R.color.toastMessageColor)).setAnimation(android.R.anim.fade_in, android.R.anim.fade_out).show();

                        Toasty.error(context, "Limit Crossed! (Max quantity amount is 15)", Toast.LENGTH_SHORT, true).show();


                    } else {
                        // Update carts
                        new Database(context).updateCartItemReorder(String.valueOf(q1), order.getId_food(), order.getId_menu());

                        /*AppMsg.makeText((Activity) context, order.getFood_name() + " added to cart (" + q1 + ") times", new AppMsg.Style(LENGTH_LONG, R.color.toastMessageColor))
                                .setAnimation(android.R.anim.fade_in, android.R.anim.fade_out).show();*/
                        Toasty.success(context, order.getFood_name() + " added to cart (" + q1 + ") times", Toast.LENGTH_SHORT, true).show();

                    }
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageViewItem;
        public TextView textViewItemUnit, textViewItemName, textViewItemPrice, textViewItemTotalPrice, textViewItemQuantity,
                textViewMenuName;
        public Button btnReorder;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            imageViewItem = (ImageView) itemView.findViewById(R.id.image_item);
            textViewItemUnit = (TextView) itemView.findViewById(R.id.textViewItemUnit);
            textViewItemName = (TextView) itemView.findViewById(R.id.textViewItemName);
            textViewItemPrice = (TextView) itemView.findViewById(R.id.textViewItemPrice);
            textViewItemTotalPrice = (TextView) itemView.findViewById(R.id.textViewItemTotalPrice);
            textViewItemQuantity = (TextView) itemView.findViewById(R.id.textViewItemQuantity);
            textViewMenuName = (TextView) itemView.findViewById(R.id.textViewMenuName);
            btnReorder = (Button) itemView.findViewById(R.id.btn_reOrder);
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

    public void createCartLimitErrorDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("You can only add " + context.getResources().getString(R.string.cart_item_limit) + " items in a single order.")
                .setTitle("Unable to add item")
                .setCancelable(false)
                .setPositiveButton("Close",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }
                );
        AlertDialog alert = builder.create();
        alert.show();
        Button b = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        if (b != null) {
            b.setTextColor(Color.parseColor("#000000"));
        }
        Button b2 = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (b2 != null) {
            b2.setTextColor(Color.parseColor("#000000"));
        }
    }

}
