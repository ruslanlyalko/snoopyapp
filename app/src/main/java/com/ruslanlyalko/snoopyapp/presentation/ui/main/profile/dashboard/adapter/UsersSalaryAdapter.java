package com.ruslanlyalko.snoopyapp.presentation.ui.main.profile.dashboard.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.data.models.User;
import com.ruslanlyalko.snoopyapp.presentation.widget.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UsersSalaryAdapter extends RecyclerView.Adapter<UsersSalaryAdapter.MyViewHolder> {

    private List<User> mDataSource = new ArrayList<>();
    private List<Integer> mSalary = new ArrayList<>();
    private OnItemClickListener mOnItemClickListener;

    public UsersSalaryAdapter(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public UsersSalaryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_profile, parent, false);
        return new UsersSalaryAdapter.MyViewHolder(itemView, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(final UsersSalaryAdapter.MyViewHolder holder, final int position) {
        final User user = mDataSource.get(position);
        holder.bindData(user, mSalary.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataSource.size();
    }

    public User getItemAtPosition(final int position) {
        return mDataSource.get(position);
    }

    public void clearAll() {
        mDataSource.clear();
        mSalary.clear();
        notifyDataSetChanged();
    }

    public void add(final User user, final int salary) {
        mDataSource.add(user);
        mSalary.add(salary);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_user_name) TextView textUserName;
        @BindView(R.id.text_position_title) TextView textPositionTitle;
        private User mUser;
        private OnItemClickListener mOnItemClickListener;

        public MyViewHolder(View view, final OnItemClickListener onItemClickListener) {
            super(view);
            mOnItemClickListener = onItemClickListener;
            ButterKnife.bind(this, view);
        }

        void bindData(final User user, final Integer salary) {
            mUser = user;
            String text = user.getFullName() + " - " + salary + " грн";
            textUserName.setText(text);
            textPositionTitle.setText(user.getPositionTitle());
        }

        @OnClick(R.id.linear_user)
        void onItemCLick() {
            if (mOnItemClickListener != null)
                mOnItemClickListener.onItemClicked(getAdapterPosition());
        }
    }
}