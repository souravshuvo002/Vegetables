package com.sourav.vegetables.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.devspark.appmsg.AppMsg;
import com.sourav.vegetables.Activity.CartActivity;
import com.sourav.vegetables.Activity.HomeActivity;
import com.sourav.vegetables.Api.ApiURL;
import com.sourav.vegetables.Database.Database;
import com.sourav.vegetables.Model.Cart;
import com.sourav.vegetables.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;

import static com.devspark.appmsg.AppMsg.LENGTH_SHORT;
import static com.devspark.appmsg.AppMsg.STYLE_ALERT;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<Cart> cartList;
    private CartActivity context;
    private int lastPosition = -1;


    public CartAdapter(List<Cart> carts, CartActivity context) {
        this.cartList = carts;
        this.context = context;
    }


    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_cart_item, parent, false);
        return new CartAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CartAdapter.ViewHolder holder, final int position) {
        final Cart cart = cartList.get(position);

        /**
         *  Animation Part
         */
        setFadeAnimation(holder.itemView);

        holder.textViewName.setText(cart.getName());
        holder.textViewUnit.setText("[" + cart.getMin_unit_amount() + " " + cart.getUnit() + "]");
        Picasso.with(context)
                //.load(R.drawable.food)
                .load(ApiURL.SERVER_URL + cart.getImage_url())
                .error(R.drawable.cutlery)
                .into(holder.imageViewItem);

        holder.elegantNumberButton.setNumber(String.valueOf(cart.getQuantity()));
        holder.textViewPrice.setText(new StringBuilder(context.getResources().getString(R.string.currency_sign)).append(cart.getPrice()));

        // Auto Save item when USER changes amount
        holder.elegantNumberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, final int newValue) {

                final android.app.AlertDialog waitingDialog = new SpotsDialog(context);
                waitingDialog.show();
                waitingDialog.setMessage("Updating ...");

                // Delaying action for 1.0 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (newValue > 15) {
                            AppMsg.makeText((Activity) context, "Max limit crossed!", STYLE_ALERT)
                                    .setAnimation(android.R.anim.fade_in, android.R.anim.fade_out).show();
                            holder.elegantNumberButton.setNumber(String.valueOf(15));
                            waitingDialog.dismiss();
                            return;
                        }
                        // Reload carts
                        Cart cartItem = cartList.get(holder.getAdapterPosition());
                        cartItem.setQuantity(String.valueOf(newValue));
                        new Database(context).updateCartItem(cartItem);

                        //Update Total Price
                        double total = 0.0;
                        List<Cart> carts = new Database(context).getCarts();
                        for (Cart cart : carts) {
                            total += (Double.parseDouble(cart.getPrice())) * (Double.parseDouble(cart.getQuantity()));
                        }
                        context.textSubTotal.setText("SubTotal: " + String.valueOf(carts.size() + " items"));
                        context.textViewTotal.setText(context.getResources().getString(R.string.currency_sign) + String.format("%.2f", total));

                        waitingDialog.dismiss();

                    }
                }, 500);
            }
        });

        holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Remove from Cart");
                alertDialog.setMessage("Are you sure you want to remove " + cart.getName() + " - from your cart?");
                alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.setNegativeButton("REMOVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Deleting cart item
                        AppMsg.makeText((Activity) context, cartList.get(holder.getAdapterPosition()).getName() + " item removed", new AppMsg.Style(LENGTH_SHORT, R.color.toastMessageColor))
                                .setAnimation(android.R.anim.fade_in, android.R.anim.fade_out).show();

                        new Database(context).clearCartITemFromFood(cart.getFood_id(), cart.getId_menu());
                        cartList.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());

                        //Update Total Price
                        double total = 0.0;
                        List<Cart> carts = new Database(context).getCarts();

                        if (carts.size() <= 0) {

                            Toasty.success(context, "Empty Cart", Toast.LENGTH_SHORT, true).show();
                            Intent cartIntent = new Intent(context, HomeActivity.class);
                            context.startActivity(cartIntent);
                            context.finish();
                        } else {
                            /*context.layEmpty.setVisibility(View.GONE);
                            // context.layRecyclerView.setVisibility(View.VISIBLE);
                            //context.linearLayoutTotal.setVisibility(View.VISIBLE);
                            context.recyclerViewCart.setVisibility(View.VISIBLE);
                            context.buttonPlaceOrder.setVisibility(View.VISIBLE);*/

                            context.layEmpty.setVisibility(View.GONE);
                            //relLay_1.setVisibility(View.VISIBLE);
                            //linearLayoutTotal.setVisibility(View.VISIBLE);
                            context.layRecyclerView.setVisibility(View.VISIBLE);
                            context.buttonPlaceOrder.setVisibility(View.VISIBLE);
                            context.imageViewRemoveAll.setVisibility(View.VISIBLE);

                            for (Cart cart : carts) {
                                total += (Double.parseDouble(cart.getPrice())) * (Double.parseDouble(cart.getQuantity()));
                            }
                            context.textSubTotal.setText("SubTotal: " + String.valueOf(carts.size() + " items"));
                            context.textViewTotal.setText(context.getResources().getString(R.string.currency_sign) + String.format("%.2f", total));
                            //context.textSubTotal.setText("SubTotal: " + String.valueOf(carts.size() + " items"));
                            //context.textSubTotal.setText(String.valueOf(carts.size()) + " items / Total Cost " + context.getResources().getString(R.string.currency_sign) + String.format("%.2f", total));
                        }
                    }
                });
                AlertDialog dialog = alertDialog.create();
                dialog.show();
                Button b = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                if (b != null) {
                    b.setTextColor(Color.parseColor("#000000"));
                }
                Button b2 = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                if (b2 != null) {
                    b2.setTextColor(Color.parseColor("#000000"));
                }
            }
        });

        /*holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SingleProductActivity.class);
                intent.putExtra("Menu_ID", cart.getCat_id());
                intent.putExtra("Menu_NAME", cart.getSub_cat_name());
                Log.d("Cat_ID: ", cart.getCat_id());
                Common.menu_id = cart.getCat_id();

                context.startActivity(intent);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewUnit, textViewName, textViewPrice;
        public ImageView imageViewItem, imageViewDelete;
        public LinearLayout linearLayout;
        public ElegantNumberButton elegantNumberButton;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewUnit = (TextView) itemView.findViewById(R.id.textViewUnit);
            textViewName = (TextView) itemView.findViewById(R.id.textViewItemName);
            imageViewItem = (ImageView) itemView.findViewById(R.id.image_item);
            imageViewDelete = (ImageView) itemView.findViewById(R.id.imageViewDelete);
            textViewPrice = (TextView) itemView.findViewById(R.id.textViewItemPrice);
            elegantNumberButton = (ElegantNumberButton) itemView.findViewById(R.id.text_amount);
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