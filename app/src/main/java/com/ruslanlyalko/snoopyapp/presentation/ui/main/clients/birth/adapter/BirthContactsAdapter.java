package com.ruslanlyalko.snoopyapp.presentation.ui.main.clients.birth.adapter;

import android.app.Activity;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.common.DateUtils;
import com.ruslanlyalko.snoopyapp.data.models.Contact;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.clients.contacts.adapter.OnContactClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BirthContactsAdapter extends RecyclerView.Adapter<BirthContactsAdapter.MyViewHolder>
        implements Filterable {

    private final OnContactClickListener mOnContactClickListener;
    private final Activity mActivity;
    private List<Contact> mDataSource = new ArrayList<>();
    private List<Contact> mDataSourceFiltered = new ArrayList<>();
    private MyFilter mFilter;

    public BirthContactsAdapter(final OnContactClickListener onContactClickListener, final Activity activity) {
        mOnContactClickListener = onContactClickListener;
        mActivity = activity;
    }

    public void clearAll() {
        mDataSource.clear();
        mDataSourceFiltered.clear();
        notifyDataSetChanged();
    }

    public void add(final Contact contactComment) {
        if (mDataSource.contains(contactComment)) return;
        mDataSource.add(contactComment);
        mDataSourceFiltered.add(contactComment);
        notifyItemInserted(mDataSource.size());
    }

    public void setData(final List<Contact> contactComments) {
        mDataSource.clear();
        mDataSource.addAll(contactComments);
        mDataSourceFiltered.clear();
        mDataSourceFiltered.addAll(contactComments);
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_contact, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Contact contact = mDataSourceFiltered.get(position);
        holder.bindData(contact);
    }

    @Override
    public int getItemCount() {
        return mDataSourceFiltered.size();
    }

    public Contact getItem(final int position) {
        return mDataSourceFiltered.get(position);
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null)
            mFilter = new MyFilter();
        return mFilter;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_avatar) ImageView mImageView;
        @BindView(R.id.text_name) TextView mTextName;
        @BindView(R.id.text_phone1) TextView mTextPhone1;
        @BindView(R.id.text_phone2) TextView mTextPhone2;
        @BindView(R.id.text_kids) TextView mTextKids;
        @BindView(R.id.layout_root) LinearLayout mLayoutRoot;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void bindData(final Contact contact) {
            mTextName.setText(contact.getName());
            mTextPhone1.setText(contact.getPhone());
            if (contact.getPhone2() != null & !contact.getPhone2().isEmpty())
                mTextPhone2.setText(contact.getPhone2());
            else
                mTextPhone2.setText("");
            String kids = "";
            if (contact.getChildName1() != null && !contact.getChildName1().isEmpty()) {
                kids += contact.getChildName1() + DateUtils.toString(contact.getChildBd1(), " dd.MM") + DateUtils.getAge(contact.getChildBd1());
            }
            if (contact.getChildName2() != null && !contact.getChildName2().isEmpty()) {
                kids += "; " + contact.getChildName2() + DateUtils.toString(contact.getChildBd2(), " dd.MM") + DateUtils.getAge(contact.getChildBd2());
            }
            if (contact.getChildName3() != null && !contact.getChildName3().isEmpty()) {
                kids += "; " + contact.getChildName3() + DateUtils.toString(contact.getChildBd3(), " dd.MM") + DateUtils.getAge(contact.getChildBd3());
            }
            mTextKids.setText(kids);
        }

        @OnClick(R.id.layout_root)
        void onItemClicked() {
            if (mOnContactClickListener != null) {
                final ActivityOptionsCompat options;
                if (getItem(getAdapterPosition()).getPhone2() == null || getItem(getAdapterPosition()).getPhone2().isEmpty())
                    options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity,
                            Pair.create(mImageView, "avatar"),
                            //Pair.create(mTextPhone1, "phone1"),
                            Pair.create(mTextName, "user"));
                else
                    options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity,
                            Pair.create(mImageView, "avatar"),
//                            Pair.create(mTextPhone1, "phone1"),
//                            Pair.create(mTextPhone2, "phone2"),
                            Pair.create(mTextName, "user"));
                mOnContactClickListener.onItemClicked(getAdapterPosition(), options);
            }
        }
    }

    class MyFilter extends Filter {

        @Override
        protected FilterResults performFiltering(final CharSequence charSequence) {
            FilterResults filterResults = new FilterResults();
            ArrayList<Contact> tempList = new ArrayList<>();
            for (int i = 0; i < mDataSource.size(); i++) {
                Contact contact = mDataSource.get(i);
                if (isMatchFilter(charSequence, contact)) {
                    tempList.add(contact);
                }
            }
            filterResults.count = tempList.size();
            filterResults.values = tempList;
            return filterResults;
        }

        private boolean isMatchFilter(final CharSequence charSequence, final Contact contact) {
            if (charSequence.toString().isEmpty()) return true;
            String month = charSequence.toString();
            if (!contact.getChildName1().isEmpty() && DateUtils.toString(contact.getChildBd1(), "MM").equals(month)) {
                return true;
            }
            if (!contact.getChildName2().isEmpty() && DateUtils.toString(contact.getChildBd2(), "MM").equals(month)) {
                return true;
            }
            if (!contact.getChildName3().isEmpty() && DateUtils.toString(contact.getChildBd3(), "MM").equals(month)) {
                return true;
            }
            return false;
        }

        @Override
        protected void publishResults(final CharSequence charSequence, final FilterResults filterResults) {
            mDataSourceFiltered = (ArrayList<Contact>) filterResults.values;
            notifyDataSetChanged();
            notifyDataSetChanged();
        }
    }
}