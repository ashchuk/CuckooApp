package com.ashchuk.cuckooapp.ui.adapters;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ashchuk.cuckooapp.R;
import com.ashchuk.cuckooapp.model.entities.Subscription;
import com.ashchuk.cuckooapp.ui.viewholders.SubscriptionsViewHolder;

import org.w3c.dom.Text;

import java.util.List;

public class SubscriptionsAdapter extends RecyclerView.Adapter<SubscriptionsViewHolder> {

    public interface SubscriptionOnClickListener  {
        void onClick(View v, Subscription subscription);
    }

    private List<Subscription> subscriptions;
    private SubscriptionOnClickListener onClickListener;

    public SubscriptionsAdapter(List<Subscription> subscriptions, SubscriptionOnClickListener onClickListener) {
        this.subscriptions = subscriptions;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public SubscriptionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subscriptions_item, parent, false);
        return new SubscriptionsViewHolder(view, onClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscriptionsViewHolder holder, int position) {
        TextView textView = holder.itemView.findViewById(R.id.textView);
        textView.setText("Some text");
    }

    @Override
    public int getItemCount() {
        return subscriptions.size();
    }
}