package com.doodeec.filemanager.FileManagement;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.doodeec.filemanager.R;
import com.doodeec.filemanager.UIComponents.CheckableItem;

/**
 * Created by Dusan Doodeec Bartos on 3.10.2014.
 * <p/>
 * Storage Holder for Folder Fragment Adapter
 */
public class StorageItemHolder {

    private CheckableItem mView;
    private ImageView mItemIcon;
    private TextView mItemName;

    public StorageItemHolder(View view) {
        mView = (CheckableItem) view;
        mItemName = (TextView) view.findViewById(R.id.storage_item_name);
        mItemIcon = (ImageView) view.findViewById(R.id.storage_item_icon);

        if (mItemName == null || mItemIcon == null) {
            throw new AssertionError("Views should not be null");
        }
    }

    public void setSelected(boolean isSelected) {
        mView.setChecked(isSelected);
    }

    public void setName(String name) {
        mItemName.setText(name);
    }

    public void setIcon(Drawable icon) {
        mItemIcon.setImageDrawable(icon);
    }
}
