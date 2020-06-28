package com.arkapp.payoassignment.utils;

import com.github.mikephil.charting.formatter.ValueFormatter;

/**
 * Created by Abdul Rehman.
 * Contact email - abdulrehman0796@gmail.com
 */
public class PieValueFormatter extends ValueFormatter {
    @Override
    public String getFormattedValue(float value) {
        return String.format("Rs %s", value);
    }
}
