package com.ashchuk.cuckooapp.ui.viewholders;

import android.support.v7.widget.RecyclerView;

import com.ashchuk.cuckooapp.databinding.SubscriptionsItemBinding;
import com.ashchuk.cuckooapp.infrastructure.helpers.UserStatusToImgSrcConverter;
import com.ashchuk.cuckooapp.infrastructure.helpers.UserStatusToStringConverter;
import com.ashchuk.cuckooapp.model.entities.Subscription;
import com.ashchuk.cuckooapp.model.enums.UserStatus;
import com.ashchuk.cuckooapp.ui.adapters.SubscriptionsListAdapter;

import java.text.SimpleDateFormat;

public class SubscriptionsViewHolder extends RecyclerView.ViewHolder {

    private Subscription subscription;
    private SubscriptionsItemBinding binding;

    public SubscriptionsViewHolder(SubscriptionsItemBinding binding, SubscriptionsListAdapter.SubscriptionOnClickListener onClickListener) {
        super(binding.getRoot());
        this.binding = binding;
        itemView.setOnClickListener(v -> onClickListener.onClick(itemView, subscription));
    }

    public void bindViewHolder(Subscription subscription) {
        this.subscription = subscription;

        SimpleDateFormat format = new SimpleDateFormat("MM-dd hh:mm");
        String formattedDate = format.format(subscription.lastUpdateDate);

        binding.setSubscription(subscription);
        binding.tvStatusDescription
                .setText(UserStatusToStringConverter.toString(subscription.status));
        binding.ivStatusIcon.setImageResource(UserStatusToImgSrcConverter
                .fromUserStatus(UserStatus.valueOf(subscription.status)));
        binding.tvDate.setText(formattedDate);

        binding.executePendingBindings();
    }
}
