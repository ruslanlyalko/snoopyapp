package com.ruslanlyalko.snoopyapp.presentation.ui.main.messages.edit.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.data.models.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MyViewHolder> {

    private List<User> mDataSource = new ArrayList<>();
    private List<Boolean> mMembers = new ArrayList<>();

    public MembersAdapter() {
    }

    @Override
    public MembersAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_member, parent, false);
        return new MembersAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MembersAdapter.MyViewHolder holder, final int position) {
        holder.bindData(mDataSource.get(position), mMembers.get(position));
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
        mMembers.clear();
        notifyDataSetChanged();
    }

    public void add(final User user) {
        mDataSource.add(user);
        mMembers.add(false);
        notifyDataSetChanged();
    }

    public void updateMembers(List<String> membersIds) {
        for (int i = 0; i < mDataSource.size(); i++) {
            mMembers.set(i, membersIds.contains(mDataSource.get(i).getId()));
        }
        notifyDataSetChanged();
    }

    public List<Boolean> getMembers() {
        return mMembers;
    }

    public List<User> getUsers() {
        return mDataSource;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_user_name) TextView textUserName;
        @BindView(R.id.text_position_title) TextView textPositionTitle;
        @BindView(R.id.switch_member) Switch mSwitchMember;
        private User mUser;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void bindData(final User user, final Boolean isMember) {
            mUser = user;
            textUserName.setText(user.getFullName());
            textPositionTitle.setText(user.getPositionTitle());
            mSwitchMember.setChecked(isMember);
        }

        @OnCheckedChanged(R.id.switch_member)
        void onMembersSwitchChanged(boolean checked) {
            mMembers.set(getAdapterPosition(), checked);
        }
    }
}