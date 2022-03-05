package com.study.daily.customviewexample;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.text.NumberFormat;

public class PriceTextView extends androidx.appcompat.widget.AppCompatTextView {
    private static NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();

    private float mPrice;

    public PriceTextView(Context context) {
        this(context, null);
    }

    public PriceTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PriceTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.PriceTextView,
                defStyleAttr,
                0);

        if (array.hasValue(R.styleable.PriceTextView_price)) {
            setPrice(array.getFloat(R.styleable.PriceTextView_price, 0));
        }

        array.recycle();
    }

    public void setPrice(float price) {
        mPrice = price;
        setText(CURRENCY_FORMAT.format(price));
    }

    public float getPrice() {
        return mPrice;
    }
}
