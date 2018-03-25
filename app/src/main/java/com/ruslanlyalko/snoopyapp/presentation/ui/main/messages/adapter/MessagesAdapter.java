package com.ruslanlyalko.snoopyapp.presentation.ui.main.messages.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.common.DateUtils;
import com.ruslanlyalko.snoopyapp.data.models.Message;
import com.ruslanlyalko.snoopyapp.data.models.Notification;
import com.ruslanlyalko.snoopyapp.presentation.ui.main.messages.details.MessageDetailsActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {

    private Context mContext;
    private List<Message> mDataSource = new ArrayList<>();
    private List<Notification> mNotifications = new ArrayList<>();

    public MessagesAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void updateNotifications(final List<Notification> notifications) {
        mNotifications.clear();
        mNotifications.addAll(notifications);
        notifyDataSetChanged();
    }

    public void clearAll() {
        mDataSource.clear();
        notifyDataSetChanged();
    }

    public void add(final Message messageComment) {
        if (mDataSource.contains(messageComment)) return;
        mDataSource.add(messageComment);
        notifyItemInserted(mDataSource.size());
    }

    public void addAll(final List<Message> messageComments) {
        mDataSource.addAll(messageComments);
        notifyDataSetChanged();
    }

    public void update(final Message messageComment) {
        for (int i = 0; i < mDataSource.size(); i++) {
            Message current = mDataSource.get(i);
            if (messageComment.getKey().equals(current.getKey())) {
                mDataSource.set(i, messageComment);
                notifyItemChanged(i);
                return;
            }
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_message, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Message message = mDataSource.get(position);
        holder.bindData(message);
    }

    @Override
    public int getItemCount() {
        return mDataSource.size();
    }

    public void setData(final List<Message> list) {
        mDataSource.clear();
        mDataSource.addAll(list);
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title1) TextView title1;
        @BindView(R.id.title2) TextView title2;
        @BindView(R.id.text_date) TextView date;
        @BindView(R.id.layout_root) LinearLayout mLinearLayout;
        @BindView(R.id.image_view) ImageView mImageView;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void bindData(final Message message) {
            boolean isNew = mNotifications.contains(new Notification(message.getKey()));
            mImageView.setImageDrawable(mImageView.getResources().getDrawable(isNew
                    ? R.drawable.ic_comment_primary : R.drawable.ic_comment));
            title1.setText(message.getTitle1());
            title2.setText(message.getLastComment() != null && !message.getLastComment().isEmpty()
                    ? message.getLastComment() : message.getTitle2());
            date.setText(DateUtils.getUpdatedAt(message.getUpdatedAt()));
            mLinearLayout.setOnClickListener(v ->
                    mContext.startActivity(MessageDetailsActivity.getLaunchIntent(mContext, message.getKey())));
        }
    }
}