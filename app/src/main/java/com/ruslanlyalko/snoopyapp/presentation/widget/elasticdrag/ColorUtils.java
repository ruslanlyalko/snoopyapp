package com.ruslanlyalko.snoopyapp.presentation.widget.elasticdrag;

import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;

public class ColorUtils {

    public static @CheckResult
    @ColorInt
    int modifyAlpha(@ColorInt int color,
                    @IntRange(from = 0, to = 255) int alpha) {
        return (color & 0x00ffffff) | (alpha << 24);
    }
}
