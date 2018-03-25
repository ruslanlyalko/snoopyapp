package com.ruslanlyalko.snoopyapp.presentation.ui.main.expenses.adapter;

import com.ruslanlyalko.snoopyapp.data.models.Expense;

/**
 * Created by Ruslan Lyalko
 * on 12.11.2017.
 */

public interface OnExpenseClickListener {

    void onRemoveClicked(Expense expense);

    void onEditClicked(Expense expense);

    void onPhotoPreviewClicked(Expense expense);
}
