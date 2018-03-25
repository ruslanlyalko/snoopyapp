package com.ruslanlyalko.snoopyapp.presentation.ui.main.profile.dashboard.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.common.DateUtils;
import com.ruslanlyalko.snoopyapp.data.models.Credit;
import com.ruslanlyalko.snoopyapp.presentation.widget.SwipeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class CreditsAdapter extends RecyclerView.Adapter<CreditsAdapter.MyViewHolder> {

    private final OnCreditClickListener mOnCreditClickListener;
    private List<Credit> mDataSource = new ArrayList<>();

    public CreditsAdapter(final OnCreditClickListener onCreditClickListener) {
        mOnCreditClickListener = onCreditClickListener;
    }

    @Override
    public CreditsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_credit, parent, false);
        return new CreditsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CreditsAdapter.MyViewHolder holder, final int position) {
        final Credit credit = mDataSource.get(position);
        holder.bindData(credit);
    }

    @Override
    public int getItemCount() {
        return mDataSource.size();
    }

    public Credit getItemAtPosition(final int position) {
        return mDataSource.get(position);
    }

    public void clearAll() {
        mDataSource.clear();
        notifyDataSetChanged();
    }

    public void add(final Credit credit) {
        mDataSource.add(credit);
        notifyDataSetChanged();
    }

    public void setData(final List<Credit> creditList) {
        mDataSource.clear();
        mDataSource.addAll(creditList);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_user_name) TextView textUserName;
        @BindView(R.id.text_position_title) TextView textPositionTitle;
        @BindView(R.id.swipe_layout) SwipeLayout mSwipeLayout;

        private Credit mCredit;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void bindData(final Credit credit) {
            mCredit = credit;
            String money = String.valueOf(credit.getMoney()) + " грн";
            textUserName.setText(money);
            String description = DateUtils.toString(credit.getDate(), "dd-MM-yy ") + credit.getDescription();
            textPositionTitle.setText(description);
            mSwipeLayout.addDrag(SwipeLayout.DragEdge.Right, R.id.swipe_menu);
            mSwipeLayout.setRightSwipeEnabled(true);
            mSwipeLayout.setBottomSwipeEnabled(false);
        }

        @OnLongClick(R.id.linear_user)
        boolean onEditClicked() {
            mOnCreditClickListener.onEditClicked(mCredit);
            return true;
        }

        @OnClick(R.id.button_comment)
        void onDeleteClicked() {
            mOnCreditClickListener.onRemoveClicked(mCredit);
        }
    }
}