package com.ruslanlyalko.snoopyapp.presentation.ui.main.calendar.adapter;

import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.data.models.Report;
import com.ruslanlyalko.snoopyapp.presentation.widget.SwipeLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.MyViewHolder> {

    private final List<Report> mReports;
    private final OnReportClickListener mOnReportClickListener;

    public ReportsAdapter(OnReportClickListener onReportClickListener, List<Report> reports) {
        mOnReportClickListener = onReportClickListener;
        mReports = reports;
    }

    @Override
    public ReportsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_report, parent, false);
        return new ReportsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ReportsAdapter.MyViewHolder holder, final int position) {
        final Report report = mReports.get(position);
        holder.bindData(report);
    }

    @Override
    public int getItemCount() {
        return mReports.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private final Resources mResources;
        @BindView(R.id.card_root) CardView mCardRoot;
        @BindView(R.id.text_user_name) TextView textUserName;
        @BindView(R.id.text_total) TextView textTotal;
        @BindView(R.id.text_bday_total) TextView textBdayTotal;
        @BindView(R.id.text_room_total) TextView textRoomTotal;
        @BindView(R.id.text_mk_total) TextView textMkTotal;
        @BindView(R.id.swipe_layout) SwipeLayout swipeLayout;
        @BindView(R.id.progress_bar) ProgressBar progressBar;
        @BindView(R.id.button_comment) ImageButton buttonComment;
        @BindView(R.id.button_mk) ImageButton buttonMk;
        @BindView(R.id.button_edit) ImageButton buttonEdit;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mResources = view.getResources();
        }

        void bindData(final Report report) {
            textTotal.setText(mResources.getString(R.string.HRN, "" + report.getOrderTotal()));
            progressBar.setMax(report.getOrderTotal());
            progressBar.setSecondaryProgressTintMode(PorterDuff.Mode.OVERLAY);
            swipeLayout.addDrag(SwipeLayout.DragEdge.Right, R.id.swipe_menu);
            swipeLayout.setRightSwipeEnabled(true);
            swipeLayout.setBottomSwipeEnabled(false);
        }

        @OnClick(R.id.button_comment)
        void onCommentsClicked() {
            if (mOnReportClickListener != null)
                mOnReportClickListener.onCommentClicked(mReports.get(getAdapterPosition()));
        }

        @OnClick(R.id.button_mk)
        void onMkClicked() {
            if (mOnReportClickListener != null)
                mOnReportClickListener.onMkClicked(mReports.get(getAdapterPosition()));
        }

        @OnClick(R.id.button_edit)
        void onEditClicked() {
            if (mOnReportClickListener != null)
                mOnReportClickListener.onEditClicked(mReports.get(getAdapterPosition()));
        }

        @OnLongClick(R.id.card_root)
        boolean onCardLongClicked() {
            if (mOnReportClickListener != null)
                mOnReportClickListener.onEditClicked(mReports.get(getAdapterPosition()));
            return true;
        }
    }
}