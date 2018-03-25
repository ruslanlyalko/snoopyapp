package com.ruslanlyalko.snoopyapp.presentation.ui.main.calendar.adapter;

import com.ruslanlyalko.snoopyapp.data.models.Report;

/**
 * Created by Ruslan Lyalko
 * on 12.11.2017.
 */

public interface OnReportClickListener {

    void onCommentClicked(Report report);

    void onMkClicked(Report report);

    void onEditClicked(Report report);
}
