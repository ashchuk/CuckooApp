package com.ashchuk.cuckooapp.ui.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ashchuk.cuckooapp.R;
import com.ashchuk.cuckooapp.model.entities.Subscription;
import com.ashchuk.cuckooapp.model.entities.User;
import com.ashchuk.cuckooapp.model.enums.UserStatus;
import com.ashchuk.cuckooapp.ui.viewholders.TodoItemViewHolder;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Date;
import java.util.List;

public class UsersSearchListAdapter extends RecyclerView.Adapter<TodoItemViewHolder> {

    private List<User> users;

    public UsersSearchListAdapter(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TodoItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item, parent, false);
        return new TodoItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoItemViewHolder holder, int position) {
        TextView usernameTextView = holder.itemView.findViewById(R.id.tv_username);
        TextView emailTextView = holder.itemView.findViewById(R.id.tv_email);
        usernameTextView.setText(users.get(position).DisplayName);
        emailTextView.setText(users.get(position).Email);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void removeAt(int position) {
        users.remove(position);
        notifyItemRemoved(position);
    }

    public Subscription createSubscription(int position) {
        User user = users.get(position);
        Subscription subscription = new Subscription();

        subscription.userId = user.Guid;
        subscription.subscriberId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        subscription.status = UserStatus.HOME.getValue();
        subscription.lastUpdateDate = new Date();
        subscription.id = java.util.UUID.randomUUID().toString();
        subscription.DisplayName = user.DisplayName;

        return subscription;
    }

    public void UpdateList(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }
}