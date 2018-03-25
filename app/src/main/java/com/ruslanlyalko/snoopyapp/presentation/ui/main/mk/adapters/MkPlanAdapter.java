package com.ruslanlyalko.snoopyapp.presentation.ui.main.mk.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.common.DateUtils;
import com.ruslanlyalko.snoopyapp.common.Keys;
import com.ruslanlyalko.snoopyapp.data.configuration.DefaultConfigurations;
import com.ruslanlyalko.snoopyapp.data.models.Report;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.mk.MkDetailsActivity;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.profile.ProfileActivity;
import com.ruslanlyalko.snoopyapp.presentation.widget.SwipeLayout;

import java.util.List;

public class MkPlanAdapter extends RecyclerView.Adapter<MkPlanAdapter.MyViewHolder> {

    private Context mContext;
    private List<Report> reportList;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    public MkPlanAdapter(Context mContext, List<Report> reportList) {
        this.mContext = mContext;
        this.reportList = reportList;
    }

    @Override
    public MkPlanAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_mk_plan, parent, false);
        return new MkPlanAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MkPlanAdapter.MyViewHolder holder, final int position) {
        final Report report = reportList.get(position);
        String mkName = (report.getMkName() == null || report.getMkName().isEmpty()
                ? "МК" : report.getMkName());
        String mkDate = report.getDate().substring(0, report.getDate().lastIndexOf('-'));
        holder.textMkTitleUserName.setText(mkName + " (" + DateUtils.getFirstLetters(report.getUserName()) + ")");
        holder.textDate.setText(mkDate);
        holder.textTotal.setText(report.totalMk + " ГРН");
        holder.textT1.setText(report.mk1 + " дітей");
        holder.textT1Total.setText(((30 + 10 * report.mkt1)) + " грн");
        holder.textT2.setText(report.mk2 + " дітей");
        holder.textT2Total.setText(((30 + 10 * report.mkt2)) + " грн");
        holder.progressBar.setMax(report.totalMk);
        holder.progressBar.setProgress(report.mk1 * (30 + report.mkt1 * 10));
        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, R.id.swipe_menu);
        holder.swipeLayout.setRightSwipeEnabled(true);
        holder.swipeLayout.setBottomSwipeEnabled(false);
        // MK
        holder.buttonMK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (report.getMkRef() != null && !report.getMkRef().isEmpty()) {
                    Intent intent = new Intent(mContext, MkDetailsActivity.class);
                    intent.putExtra(Keys.Extras.EXTRA_ITEM_ID, report.getMkRef());
                    mContext.startActivity(intent);
                }
            }
        });
        // User
        holder.buttonUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(ProfileActivity.getLaunchIntent((Activity) mContext, report.userId));
            }
        });
        // Calendar
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    private void removeReport(Report report, int position) {
        reportList.remove(position);
        notifyDataSetChanged();
        // Delete item from DB
        mDatabase.getReference(DefaultConfigurations.DB_REPORTS)
                .child(getYearFromStr(report.date)).child(getMonthFromStr(report.date)).child(getDayFromStr(report.date))
                .child(report.userId).removeValue();
    }

    private String getYearFromStr(String date) {
        int last = date.lastIndexOf('-');
        return date.substring(last + 1);
    }

    private String getMonthFromStr(String date) {
        int first = date.indexOf('-');
        int last = date.lastIndexOf('-');
        return date.substring(first + 1, last);
    }

    private String getDayFromStr(String date) {
        int first = date.indexOf('-');
        return date.substring(0, first);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView textMkTitleUserName, textDate, textTotal, textT2Total, textT1Total, textT1, textT2;
        public SwipeLayout swipeLayout;
        public ProgressBar progressBar;
        public ImageButton buttonMK, buttonUser, buttonDelete;

        public MyViewHolder(View view) {
            super(view);
            textT1 = view.findViewById(R.id.text_t1);
            textT2 = view.findViewById(R.id.text_t2);
            textMkTitleUserName = view.findViewById(R.id.text_user_name);
            textDate = view.findViewById(R.id.text_date);
            textTotal = view.findViewById(R.id.text_total);
            textT2Total = view.findViewById(R.id.text_bday_total);
            textT1Total = view.findViewById(R.id.text_room_total);
            swipeLayout = view.findViewById(R.id.swipe_layout);
            progressBar = view.findViewById(R.id.progress_bar);
            buttonMK = view.findViewById(R.id.button_user);
            buttonUser = view.findViewById(R.id.button_edit);
            buttonDelete = view.findViewById(R.id.button_comment);
        }
    }
}