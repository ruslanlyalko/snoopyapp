package com.ruslanlyalko.snoopyapp.presentation.ui.main.clients.birthdays.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.common.DateUtils;
import com.ruslanlyalko.snoopyapp.data.models.Birthday;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BirthdaysAdapter extends RecyclerView.Adapter<BirthdaysAdapter.MyViewHolder>
        implements Filterable {

    private final OnBirthdaysClickListener mOnBirthdaysClickListener;
    private List<Birthday> mDataSource = new ArrayList<>();
    private List<Birthday> mDataSourceFiltered = new ArrayList<>();
    private MyFilter mFilter;

    public BirthdaysAdapter(final OnBirthdaysClickListener onBirthdaysClickListener) {
        mOnBirthdaysClickListener = onBirthdaysClickListener;
    }

    public void clearAll() {
        mDataSource.clear();
        mDataSourceFiltered.clear();
        notifyDataSetChanged();
    }

    public void add(final Birthday birthdayComment) {
        if (mDataSource.contains(birthdayComment)) return;
        mDataSource.add(birthdayComment);
        mDataSourceFiltered.add(birthdayComment);
        notifyItemInserted(mDataSource.size());
    }

    public void setData(final List<Birthday> birthdayComments) {
        mDataSource.clear();
        mDataSource.addAll(birthdayComments);
        mDataSourceFiltered.clear();
        mDataSourceFiltered.addAll(birthdayComments);
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_birthday, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Birthday birthday = mDataSourceFiltered.get(position);
        holder.bindData(birthday);
    }

    @Override
    public int getItemCount() {
        return mDataSourceFiltered.size();
    }

    public Birthday getItem(final int position) {
        return mDataSourceFiltered.get(position);
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null)
            mFilter = new MyFilter();
        return mFilter;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_child_name1) TextView mTextChildName1;
        @BindView(R.id.text_child_bd1) TextView mTextChildBd1;
        @BindView(R.id.text_child_count) TextView mTextChildCount;
        @BindView(R.id.checkbox_birthday_mk) CheckBox mCheckboxBirthdayMk;
        @BindView(R.id.checkbox_birthday_aqua) CheckBox mCheckboxBirthdayAqua;
        @BindView(R.id.checkbox_birthday_cinema) CheckBox mCheckboxBirthdayCinema;
        @BindView(R.id.text_description) TextView mTextDescription;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void bindData(final Birthday birthday) {
            mTextChildName1.setText(birthday.getChildName());
            mTextChildCount.setText(String.valueOf(birthday.getKidsCount()));
            mTextChildBd1.setText(DateUtils.getHowLongTime(birthday.getBdDate(), "EEE dd.MM.yy"));
            mTextDescription.setText(birthday.getDescription());
            mTextDescription.setVisibility(birthday.getDescription() != null & !birthday.getDescription().isEmpty() ? View.VISIBLE : View.GONE);
            mCheckboxBirthdayAqua.setChecked(birthday.getAqua());
            mCheckboxBirthdayMk.setChecked(birthday.getMk());
            mCheckboxBirthdayCinema.setChecked(birthday.getCinema());
        }

        @OnClick(R.id.button_edit)
        void onEditClicked() {
            if (mOnBirthdaysClickListener != null)
                mOnBirthdaysClickListener.onEditClicked(getAdapterPosition());
        }

        @OnClick(R.id.card_root)
        void onItemClicked() {
            if (mOnBirthdaysClickListener != null)
                mOnBirthdaysClickListener.onItemClicked(getAdapterPosition());
        }
    }

    class MyFilter extends Filter {

        @Override
        protected FilterResults performFiltering(final CharSequence charSequence) {
            FilterResults filterResults = new FilterResults();
            ArrayList<Birthday> tempList = new ArrayList<>();
            for (int i = 0; i < mDataSource.size(); i++) {
                Birthday birthday = mDataSource.get(i);
                if (isMatchFilter(charSequence, birthday)) {
                    tempList.add(birthday);
                }
            }
            filterResults.count = tempList.size();
            filterResults.values = tempList;
            return filterResults;
        }

        private boolean isMatchFilter(final CharSequence charSequence, final Birthday birthday) {
            if (charSequence.toString().equals("/")) return true;
            String[] filter = charSequence.toString().split("/", 2);
            String from = filter[0];
            String to = "";
            if (filter.length > 1)
                to = filter[1];
            return DateUtils.isBetween(birthday.getBdDate(), from, to);
        }

        @Override
        protected void publishResults(final CharSequence charSequence, final FilterResults filterResults) {
            mDataSourceFiltered = (ArrayList<Birthday>) filterResults.values;
            notifyDataSetChanged();
            notifyDataSetChanged();
        }
    }
}