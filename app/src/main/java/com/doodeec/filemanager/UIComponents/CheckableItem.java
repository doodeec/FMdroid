package com.doodeec.filemanager.UIComponents;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.RelativeLayout;

/**
 * Created by Dusan Doodeec Bartos on 4.10.2014.
 */
@SuppressWarnings("unused")
public class CheckableItem extends RelativeLayout implements Checkable {
    private boolean mChecked = false;
    private static final int[] CHECKED_STATE_SET = { android.R.attr.state_checked };

    public CheckableItem(Context context) {
        super(context);
    }

    public CheckableItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckableItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked())
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        return drawableState;
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void setChecked(boolean isChecked) {
        mChecked = isChecked;
        refreshDrawableState();
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }
}
