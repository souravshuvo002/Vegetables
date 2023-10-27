package com.sourav.vegetables.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sourav.vegetables.R;

import androidx.recyclerview.widget.RecyclerView;

public class NotificationViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public ImageView imageViewItem;
    public TextView textViewTitle, textViewBody, textViewTime;
    public NotificationViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
        textViewBody = (TextView) itemView.findViewById(R.id.textViewBody);
        textViewTime = (TextView) itemView.findViewById(R.id.textViewTime);
    }


    @Override
    public void onClick(View v) {
        /*Intent intent = new Intent(v.getContext(), SingleHistoryActivity.class);
        Bundle b = new Bundle();
        b.putString("rideId", rideId.getText().toString());
        intent.putExtras(b);
        v.getContext().startActivity(intent);*/
    }
}