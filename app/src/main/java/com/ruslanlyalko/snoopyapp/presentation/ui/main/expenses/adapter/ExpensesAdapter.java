package com.ruslanlyalko.snoopyapp.presentation.ui.main.expenses.adapter;

import android.content.res.Resources;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ruslanlyalko.snoopyapp.R;
import com.ruslanlyalko.snoopyapp.common.Constants;
import com.ruslanlyalko.snoopyapp.common.DateUtils;
import com.ruslanlyalko.snoopyapp.data.FirebaseUtils;
import com.ruslanlyalko.snoopyapp.data.models.Expense;
import com.ruslanlyalko.snoopyapp.presentation.widget.SwipeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.MyViewHolder> {

    private OnExpenseClickListener mOnExpenseClickListener;
    private List<Expense> mExpenseList = new ArrayList<>();
    private FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

    public ExpensesAdapter(OnExpenseClickListener onExpenseClickListener) {
        this.mOnExpenseClickListener = onExpenseClickListener;
    }

    @Override
    public ExpensesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_expense, parent, false);
        return new ExpensesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ExpensesAdapter.MyViewHolder holder, final int position) {
        final Expense expense = mExpenseList.get(position);
        holder.bindData(expense);
    }

    @Override
    public int getItemCount() {
        return mExpenseList.size();
    }

    public List<Expense> getData() {
        return mExpenseList;
    }

    public void setData(final List<Expense> expenseList) {
        mExpenseList.clear();
        mExpenseList.addAll(expenseList);
        notifyDataSetChanged();
    }

    public void clearData() {
        int count = mExpenseList.size();
        mExpenseList.clear();
        notifyItemRangeRemoved(0, count);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private final Resources mResources;

        @BindView(R.id.text_title1) TextView mTextView;
        @BindView(R.id.text_title2) TextView mTextTitle2;
        @BindView(R.id.text_total) TextView mTextPrice;
        @BindView(R.id.text_date) TextView mTextDate;
        @BindView(R.id.linear_user) LinearLayout mUserLayout;
        @BindView(R.id.button_comment) ImageButton mButtonDelete;
        @BindView(R.id.swipe_menu) LinearLayout mMenuLayout;
        @BindView(R.id.swipe_layout) SwipeLayout mSwipeLayout;
        @BindView(R.id.image_view) ImageView mLogoImage;

        MyViewHolder(View view) {
            super(view);
            mResources = view.getResources();
            ButterKnife.bind(this, view);
        }

        void bindData(final Expense expense) {
            boolean isCurrentUserCost = expense.getUserId().endsWith(mCurrentUser.getUid());
            mTextView.setText(expense.getTitle1());
            mTextTitle2.setText(isCurrentUserCost ? expense.getType() : expense.getType() + "  (" + expense.getUserName() + ")");
            mTextPrice.setText(mResources.getString(R.string.hrn, expense.getPrice() + ""));
            mTextDate.setText(DateUtils.toString(expense.getExpenseDate()));
            mLogoImage.setImageResource(expense.getImage() != null && !expense.getImage().isEmpty() ?
                    R.drawable.ic_image_light : R.drawable.ic_action_cost);
            int diff = DateUtils.getDifference(expense.getExpenseDate());
            boolean justAdded = (diff <= Constants.COST_EDIT_MIN);
            // Avoid delete
            if (!FirebaseUtils.isAdmin() && justAdded) {
                // start this code after 5* minutes
                new Handler().postDelayed(() -> {
                    mSwipeLayout.close();
                    mSwipeLayout.setRightSwipeEnabled(false);
                    mMenuLayout.setVisibility(View.GONE);
                }, (Constants.COST_EDIT_MIN - diff + 1) * 60 * 1000);
            }
            if (FirebaseUtils.isAdmin() || justAdded) {
                mSwipeLayout.addDrag(SwipeLayout.DragEdge.Right, R.id.swipe_menu);
                mSwipeLayout.setRightSwipeEnabled(true);
                mSwipeLayout.setBottomSwipeEnabled(false);
            } else {
                mMenuLayout.setVisibility(View.GONE);
            }
        }

        @OnClick(R.id.linear_user)
        void onPhotoPreviewClicked() {
            if (mOnExpenseClickListener != null) {
                Expense expense = mExpenseList.get(getAdapterPosition());
                if (expense.getImage() != null && !expense.getImage().isEmpty()) {
                    mOnExpenseClickListener.onPhotoPreviewClicked(expense);
                }
            }
        }

        @OnClick(R.id.button_comment)
        void onRemoveClicked() {
            if (mOnExpenseClickListener != null) {
                mOnExpenseClickListener.onRemoveClicked(mExpenseList.get(getAdapterPosition()));
                mSwipeLayout.close();
            }
        }

        @OnLongClick(R.id.linear_user)
        boolean onEditClicked() {
            if (mOnExpenseClickListener != null) {
                mOnExpenseClickListener.onEditClicked(mExpenseList.get(getAdapterPosition()));
                return true;
            }
            return false;
        }
    }
}