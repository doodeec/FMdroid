package com.doodeec.filemanager.UIComponents;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by Dusan Doodeec Bartos on 4.10.2014.
 */
@SuppressWarnings("unused")
public class SlidingFrameLayout extends LinearLayout {
    private static final String TAG = SlidingFrameLayout.class.getName();

    public SlidingFrameLayout(Context context) {
        super(context);
    }

    public SlidingFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public float getXFraction() {
        int width = getWidth();
        return (width == 0) ? 0 : getX() / (float) width;
    }

    public void setXFraction(float xFraction) {
        int width = getWidth();
        setX((width > 0) ? (xFraction * width) : 0);
    }
}
