package com.sourav.vegetables.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.TypedValue;
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
import com.sourav.vegetables.Activity.CartActivity;
import com.sourav.vegetables.Activity.FoodsActivity;
import com.sourav.vegetables.Activity.SearchActivity;
import com.sourav.vegetables.Api.ApiURL;
import com.sourav.vegetables.Common.Common;
import com.sourav.vegetables.Database.Database;
import com.sourav.vegetables.Model.Cart;
import com.sourav.vegetables.Model.Foods;
import com.sourav.vegetables.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SearchFoodAdapter extends RecyclerView.Adapter<SearchFoodAdapter.ViewHolder> {

    private List<Foods> foodsList;
    private SearchActivity context;


    public SearchFoodAdapter(List<Foods> foodsList, SearchActivity context) {
        this.foodsList = foodsList;
        this.context = context;
        setHasStableIds(true);
        viewCart();

    }

    @Override
    public SearchFoodAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_foods_2, parent, false);
        return new SearchFoodAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final SearchFoodAdapter.ViewHolder holder, final int position) {

        final Foods foods = foodsList.get(position);

        setFadeAnimation(holder.itemView);

        holder.textViewName.setText(foods.getName());
        if (foods.getMin_unit_amount().equalsIgnoreCase("null")) {
            holder.textViewUnit.setText(foods.getUnit());
        } else {
            holder.textViewUnit.setText("[" + foods.getMin_unit_amount() + " " + foods.getUnit() + "]");
        }

        if (foods.getDiscount_price().equals("0.0000")) {
            DecimalFormat df2 = new DecimalFormat("####0.00");
            double price = Double.parseDouble(foods.getPrice());

            holder.textViewPrice.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            holder.textViewPrice.setText(new StringBuilder(context.getResources().getString(R.string.currency_sign)).append(df2.format(price)));
            holder.linearLayoutDiscount.setVisibility(View.GONE);
            holder.textViewPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
        } else {

            DecimalFormat df2 = new DecimalFormat("####0.00");
            double price = Double.parseDouble(foods.getPrice());
            double discountPrice = Double.parseDouble(foods.getDiscount_price());

            double original_price = Double.parseDouble(foods.getPrice());
            double selling_price = Double.parseDouble(foods.getDiscount_price());

            holder.textViewPrice.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary1));
            holder.textViewPrice.setText(new StringBuilder(context.getResources().getString(R.string.currency_sign)).append(df2.format(price)));
            holder.textViewPrice.setPaintFlags(holder.textViewPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.linearLayoutDiscount.setVisibility(View.VISIBLE);
            holder.textViewDiscount.setText(new StringBuilder(context.getResources().getString(R.string.currency_sign)).append(df2.format(discountPrice)));

        }

        Picasso.with(context)
                //.load(R.drawable.food)
                .load(ApiURL.SERVER_URL + foods.getImage_url())
                .error(R.drawable.cutlery)
                .into(holder.imageView);
        if (new Database(context).checkExistence(foods.getId())) {
            holder.buttonAddToCart.setBackgroundResource(R.color.colorPrimary1);
            String quantity = new Database(context).countQuantity(foods.getId(), foods.getId_menu());
            holder.buttonAddToCart.setText("Long press to remove selection " + "(x " + quantity + " times)");
            holder.buttonAddToCart.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);

            // Removing Number button and making buttonAddToCart width match_parent
            holder.elegantNumberButton.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.buttonAddToCart.getLayoutParams();
            params.width = 100;
            holder.buttonAddToCart.setLayoutParams(params);

            holder.buttonAddToCart.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    // Remove item from Cart
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle("Remove from Cart");
                    alertDialog.setMessage("Are you sure you want to remove " + foods.getName() + " - from your cart?");
                    alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.setNegativeButton("REMOVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            new Database(context).clearCartITemFromFood(foods.getId(), foods.getId_menu());
                            holder.elegantNumberButton.setVisibility(View.VISIBLE);
                            holder.buttonAddToCart.setText("Buy");
                            holder.buttonAddToCart.setBackgroundColor(Color.parseColor("#000000"));

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

                    return false;
                }
            });
        }

        holder.buttonAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (new Database(context).getCarts().size() >= Integer.parseInt(context.getResources().getString(R.string.cart_item_limit))) {
                    createCartLimitErrorDialog();
                    return;
                }

                // Checking if the foods is already added or not
                if (!new Database(context).checkExistence(foods.getId())) {

                    if (foods.getDiscount_price().equals("0.0000")) {
                        new Database(context).addToCart(new Cart(
                                foods.getId(),
                                foods.getName(),
                                foods.getDescription(),
                                foods.getPrice(),
                                foods.getImage_url(),
                                foods.getMin_unit_amount(),
                                foods.getUnit(),
                                holder.elegantNumberButton.getNumber(),
                                foods.getId_menu(),
                                foods.getMenu_name()
                        ));
                    } else {
                        new Database(context).addToCart(new Cart(
                                foods.getId(),
                                foods.getName(),
                                foods.getDescription(),
                                foods.getDiscount_price(),
                                foods.getImage_url(),
                                foods.getMin_unit_amount(),
                                foods.getUnit(),
                                holder.elegantNumberButton.getNumber(),
                                foods.getId_menu(),
                                foods.getMenu_name()
                        ));
                    }

                    //Toast.makeText(mCtx, foods.getName() + " added to cart", Toast.LENGTH_SHORT).show();
                    /*AppMsg.makeText((Activity) mCtx, foods.getName() + " added to cart", new AppMsg.Style(LENGTH_SHORT, R.color.toastColorInfo))
                            .setAnimation(android.R.anim.fade_in, android.R.anim.fade_out).show();*/
                    holder.buttonAddToCart.setBackgroundResource(R.color.colorPrimary1);
                    holder.buttonAddToCart.setText("Long press to remove selection " + "(x " + holder.elegantNumberButton.getNumber() + " times)");
                    holder.buttonAddToCart.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f);
                    // Removing Number button and making buttonAddToCart width match_parent
                    holder.elegantNumberButton.setVisibility(View.GONE);
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.buttonAddToCart.getLayoutParams();
                    params.width = 100;
                    holder.buttonAddToCart.setLayoutParams(params);

                    viewCart();

                } else {
                    Toast.makeText(context, "Long press to edit selection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.buttonAddToCart.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (new Database(context).checkExistence(foods.getId())) {
                    // Remove item from Cart
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    alertDialog.setTitle("Remove from Cart");
                    alertDialog.setMessage("Are you sure you want to remove " + foods.getName() + " - from your cart?");
                    alertDialog.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.setNegativeButton("REMOVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            new Database(context).clearCartITemFromFood(foods.getId(), foods.getId_menu());
                            holder.elegantNumberButton.setVisibility(View.VISIBLE);
                            holder.elegantNumberButton.setNumber(String.valueOf(1));
                            holder.buttonAddToCart.setText("Buy");
                            holder.buttonAddToCart.setTypeface(Typeface.DEFAULT_BOLD);
                            holder.buttonAddToCart.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                            holder.buttonAddToCart.setBackgroundResource(R.color.colorPrimary);

                            viewCart();

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
                return false;
            }
        });

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FoodsActivity.class);
                intent.putExtra("MENU_ID", foods.getId_menu());
                intent.putExtra("MENU_NAME", foods.getMenu_name());
                Common.menu_id = foods.getId_menu();
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return foodsList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewName, textViewUnit, textViewPrice, textViewDiscount;
        public ImageView imageView;
        public Button buttonAddToCart;
        public ElegantNumberButton elegantNumberButton;
        public LinearLayout linearLayout, sub_item, linearLayoutDiscount;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewName = (TextView) itemView.findViewById(R.id.item_name);
            textViewUnit = (TextView) itemView.findViewById(R.id.item_unit);
            textViewPrice = (TextView) itemView.findViewById(R.id.item_price);
            textViewDiscount = (TextView) itemView.findViewById(R.id.item_discount_price);
            imageView = (ImageView) itemView.findViewById(R.id.item_image);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLay);
            linearLayoutDiscount = (LinearLayout) itemView.findViewById(R.id.linearLayDiscount);
            buttonAddToCart = (Button) itemView.findViewById(R.id.btn_addCart);
            elegantNumberButton = (ElegantNumberButton) itemView.findViewById(R.id.txt_count);
            sub_item = (LinearLayout) itemView.findViewById(R.id.sub_item);
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


    public void updateList(List<Foods> list) {
        this.foodsList = list;
        notifyDataSetChanged();
    }

    public void viewCart() {
        // Test
        int count = new Database(context).cartItemCount();

        if (count > 0) {
            context.layoutViewCart.setVisibility(View.VISIBLE);

            if (count == 1) {
                context.textViewItems.setText(count + " item in cart");
            } else {
                context.textViewItems.setText(count + " items in cart");
            }

            //Update Total Price
            double total = 0.0;
            List<Cart> carts = new Database(context).getCarts();
            for (Cart cart : carts) {
                total += (Double.parseDouble(cart.getPrice())) * (Double.parseDouble(cart.getQuantity()));
            }
            context.textViewPrice.setText(context.getResources().getString(R.string.currency_sign) + String.format("%.2f", total));

            context.layoutCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, CartActivity.class));
                    //mCtx.finish();
                }
            });
        } else {
            context.layoutViewCart.setVisibility(View.GONE);
        }
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