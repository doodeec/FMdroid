package com.doodeec.filemanager.FileManagement;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.doodeec.filemanager.R;

/**
 * Created by Dusan Doodeec Bartos on 3.10.2014.
 */
public class StorageItemHolder {

    private ImageView mItemIcon;
    private TextView mItemName;

    public StorageItemHolder(View view) {
        mItemName = (TextView) view.findViewById(R.id.storage_item_name);
        mItemIcon = (ImageView) view.findViewById(R.id.storage_item_icon);

        assert (mItemName != null);
        assert (mItemIcon != null);
    }

    public void setName(String name) {
        mItemName.setText(name);
    }

    public void setIcon(Drawable icon) {
        mItemIcon.setImageDrawable(icon);
    }
}
