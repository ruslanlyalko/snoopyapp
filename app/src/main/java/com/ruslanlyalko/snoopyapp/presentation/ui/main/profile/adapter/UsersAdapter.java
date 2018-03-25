package com.ruslanlyalko.snoopyapp.presentation.ui.main.profile.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.data.FirebaseUtils;
import com.ruslanlyalko.snoopyapp.data.models.User;
import com.ruslanlyalko.snoopyapp.presentation.widget.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {

    private List<User> mDataSource = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    public UsersAdapter(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public UsersAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_profile, parent, false);
        return new UsersAdapter.MyViewHolder(itemView, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(final UsersAdapter.MyViewHolder holder, final int position) {
        final User user = mDataSource.get(position);
        holder.bindData(user);
    }

    @Override
    public int getItemCount() {
        return mDataSource.size();
    }

    public User getItemAtPosition(final int position) {
        return mDataSource.get(position);
    }

    public void add(final User user) {
        mDataSource.add(user);
        notifyItemInserted(mDataSource.size() - 1);
    }

    public void update(final User user) {
        for (int i = 0; i < mDataSource.size(); i++) {
            if (user.getUserId().equalsIgnoreCase(mDataSource.get(i).getUserId())) {
                mDataSource.set(i, user);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_user_name) TextView textUserName;
        @BindView(R.id.text_position_title) TextView textPositionTitle;
        @BindView(R.id.image_user_logo) ImageView imageUserLogo;
        @BindView(R.id.image_notifications_off) ImageView imageNotificationsOff;

        private User mUser;
        private OnItemClickListener mOnItemClickListener;

        public MyViewHolder(View view, final OnItemClickListener onItemClickListener) {
            super(view);
            mOnItemClickListener = onItemClickListener;
            ButterKnife.bind(this, view);
        }

        void bindData(final User user) {
            mUser = user;
            textUserName.setText(user.getUserName());
            textPositionTitle.setText(user.getUserPositionTitle());
            if(FirebaseUtils.isAdmin())
                imageNotificationsOff.setVisibility(user.getReceiveNotifications() ? View.GONE : View.VISIBLE);
            imageUserLogo.setImageResource(user.getIsOnline() ? R.drawable.ic_user_primary : R.drawable.ic_user_name);
        }

        @OnClick(R.id.linear_user)
        void onItemCLick() {
            if (mOnItemClickListener != null)
                mOnItemClickListener.onItemClicked(getAdapterPosition());
        }
    }
}