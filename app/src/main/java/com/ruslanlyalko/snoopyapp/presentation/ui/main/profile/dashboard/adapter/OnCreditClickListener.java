package com.ruslanlyalko.snoopyapp.presentation.ui.main.profile.dashboard.adapter;

import com.ruslanlyalko.snoopyapp.data.models.Credit;

/**
 * Created by Ruslan Lyalko
 * on 12.11.2017.
 */

public interface OnCreditClickListener {

    void onRemoveClicked(Credit credit);

    void onEditClicked(Credit credit);
}
