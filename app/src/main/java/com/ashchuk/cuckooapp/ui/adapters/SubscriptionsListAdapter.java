package com.ashchuk.cuckooapp.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ashchuk.cuckooapp.databinding.SubscriptionsItemBinding;
import com.ashchuk.cuckooapp.model.entities.Subscription;
import com.ashchuk.cuckooapp.ui.viewholders.SubscriptionsViewHolder;

import java.util.List;

public class SubscriptionsListAdapter extends RecyclerView.Adapter<SubscriptionsViewHolder> {

    public interface SubscriptionOnClickListener {
        void onClick(View v, Subscription subscription);
    }

    private List<Subscription> subscriptions;
    private SubscriptionOnClickListener onClickListener;

    public SubscriptionsListAdapter(List<Subscription> subscriptions, SubscriptionOnClickListener onClickListener) {
        this.subscriptions = subscriptions;
        this.onClickListener = onClickListener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SubscriptionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        SubscriptionsItemBinding binding =
                SubscriptionsItemBinding.inflate(layoutInflater, parent, false);

        return new SubscriptionsViewHolder(binding, onClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscriptionsViewHolder holder, int position) {
        holder.bindViewHolder(subscriptions.get(position));
    }

    @Override
    public int getItemCount() {
        return subscriptions.size();
    }

    public void removeAt(int position) {
        subscriptions.remove(position);
        notifyItemRemoved(position);
    }

    public void updateSubscriptions(List<Subscription> update) {
        subscriptions = update;
        this.notifyDataSetChanged();
    }

    public Subscription getSubscription(int position) {
        return subscriptions.get(position);
    }
}