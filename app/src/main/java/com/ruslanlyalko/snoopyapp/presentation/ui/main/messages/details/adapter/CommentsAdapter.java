package com.ruslanlyalko.snoopyapp.presentation.ui.main.messages.details.adapter;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.common.DateUtils;
import com.ruslanlyalko.snoopyapp.data.FirebaseUtils;
import com.ruslanlyalko.snoopyapp.data.models.MessageComment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder> {

    private static final int VIEW_TYPE_MY = 0;
    private static final int VIEW_TYPE_ANOTHER = 1;
    private static final int VIEW_TYPE_PHOTO_MY = 2;
    private static final int VIEW_TYPE_PHOTO_ANOTHER = 3;

    private List<MessageComment> mDataSource = new ArrayList<>();
    private OnCommentClickListener mOnItemClickListener;

    public CommentsAdapter(OnCommentClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public CommentsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case VIEW_TYPE_ANOTHER:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_commet, parent, false);
                return new CommentsAdapter.MyViewHolder(itemView, mOnItemClickListener);
            case VIEW_TYPE_PHOTO_ANOTHER:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_commet_photo, parent, false);
                return new CommentsAdapter.MyViewHolder(itemView, mOnItemClickListener);
            case VIEW_TYPE_PHOTO_MY:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_commet_photo_my, parent, false);
                return new CommentsAdapter.MyViewHolder(itemView, mOnItemClickListener);
            case VIEW_TYPE_MY:
            default:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_commet_my, parent, false);
                return new CommentsAdapter.MyViewHolder(itemView, mOnItemClickListener);
        }
    }

    @Override
    public void onBindViewHolder(final CommentsAdapter.MyViewHolder holder, final int position) {
        final MessageComment user = mDataSource.get(position);
        holder.bindData(user);
    }

    @Override
    public int getItemViewType(final int position) {
        MessageComment item = mDataSource.get(position);
        if (item.getFile() != null && !item.getFile().isEmpty() && !item.getRemoved()) {
            return item.getUserId().equals(FirebaseAuth.getInstance().getUid())
                    ? VIEW_TYPE_PHOTO_MY
                    : VIEW_TYPE_PHOTO_ANOTHER;
        } else {
            return item.getUserId().equals(FirebaseAuth.getInstance().getUid())
                    ? VIEW_TYPE_MY
                    : VIEW_TYPE_ANOTHER;
        }
    }

    @Override
    public int getItemCount() {
        return mDataSource.size();
    }

    public MessageComment getItemAtPosition(final int position) {
        return mDataSource.get(position);
    }

    public void clearAll() {
        mDataSource.clear();
        notifyDataSetChanged();
    }

    public void add(final MessageComment messageComment) {
        if (mDataSource.contains(messageComment)) return;
        mDataSource.add(messageComment);
        notifyItemInserted(mDataSource.size()-1);
    }

    public void addAll(final List<MessageComment> messageComments) {
        mDataSource.addAll(messageComments);
        notifyDataSetChanged();
    }

    public void setData(final List<MessageComment> messageComments) {
        mDataSource.clear();
        mDataSource.addAll(messageComments);
        notifyDataSetChanged();
    }

    public void update(final MessageComment messageComment) {
        for (int i = 0; i < mDataSource.size(); i++) {
            MessageComment current = mDataSource.get(i);
            if (messageComment.getKey().equals(current.getKey())) {
                mDataSource.set(i, messageComment);
                notifyItemChanged(i);
                return;
            }
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_comment_time) TextView mTextCommentTime;
        @BindView(R.id.image_user) ImageView mImageUser;
        @Nullable
        @BindView(R.id.text_user_name)
        TextView mTextUserName;
        @Nullable
        @BindView(R.id.text_comment)
        TextView mTextComment;
        @Nullable
        @BindView(R.id.image_view)
        ImageView mImageView;

        private MessageComment mMessageComment;
        private OnCommentClickListener mOnCommentClickListener;

        public MyViewHolder(View view, final OnCommentClickListener onCommentClickListener) {
            super(view);
            mOnCommentClickListener = onCommentClickListener;
            ButterKnife.bind(this, view);
        }

        void bindData(final MessageComment messageComment) {
            mMessageComment = messageComment;
            if (mTextUserName != null)
                mTextUserName.setText(messageComment.getUserName());
            if (mTextComment != null) {
                mTextComment.setText(messageComment.getRemoved() ? "Повідомлення видалено" : messageComment.getMessage());
                mTextComment.setTextColor(ContextCompat.getColor(mTextComment.getContext(),
                        messageComment.getRemoved() ? R.color.colorComment : R.color.colorBlack));
            }
            mTextCommentTime.setText(DateUtils.toString(messageComment.getDate(), "HH:mm"));
            try {
                if (messageComment.getThumbnail() != null && !messageComment.getThumbnail().isEmpty() && mImageView != null) {
                    Glide.with(mTextCommentTime.getContext())
                            .load(messageComment.getThumbnail())
                            .into(mImageView);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (messageComment.getUserAvatar() != null && !messageComment.getUserAvatar().isEmpty()) {
                    Glide.with(mTextCommentTime.getContext())
                            .load(messageComment.getUserAvatar())
                            .into(mImageUser);
                } else {
                    mImageUser.setImageResource(R.drawable.ic_user_name);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @OnClick(R.id.linear_root)
        void onItemCLick() {
            if (mOnCommentClickListener != null)
                mOnCommentClickListener.onItemClicked(mImageView, getAdapterPosition());
        }

        @OnClick(R.id.card_user)
        void onUserCLick() {
            if (mOnCommentClickListener != null)
                mOnCommentClickListener.onUserClicked(getAdapterPosition());
        }

        @OnLongClick(R.id.linear_root)
        boolean onItemLongCLick() {
            mMessageComment = mDataSource.get(getAdapterPosition());
            if (mMessageComment.getRemoved()) {
                if (FirebaseUtils.isAdmin())
                    Toast.makeText(mTextCommentTime.getContext(), mMessageComment.getMessage(), Toast.LENGTH_SHORT).show();
                return true;
            }
            if (!FirebaseAuth.getInstance().getUid().equals(mMessageComment.getUserId()))
                return false;
            if (mOnCommentClickListener == null) return false;
            mOnCommentClickListener.onItemLongClicked(getAdapterPosition());
            return true;
        }
    }
}