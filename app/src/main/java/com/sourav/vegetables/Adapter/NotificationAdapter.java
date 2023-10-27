package com.sourav.vegetables.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

import com.sourav.vegetables.Model.Notification;
import com.sourav.vegetables.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationViewHolders> {

    private List<Notification> itemList;
    private Context context;

    public NotificationAdapter(List<Notification> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public NotificationViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_notification, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        NotificationViewHolders rcv = new NotificationViewHolders(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(NotificationViewHolders holder, final int position) {

        // List item Animation
        setFadeAnimation(holder.itemView);

        holder.textViewTitle.setText(itemList.get(position).getTitle());
        holder.textViewBody.setText(itemList.get(position).getBody());
        holder.textViewTime.setText(itemList.get(position).getSend_time());

    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }


    public void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        view.startAnimation(anim);
    }

}