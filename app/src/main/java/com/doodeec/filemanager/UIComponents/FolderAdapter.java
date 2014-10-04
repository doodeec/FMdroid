package com.doodeec.filemanager.UIComponents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.doodeec.filemanager.BaseActivity;
import com.doodeec.filemanager.FileManagement.Model.StorageItem;
import com.doodeec.filemanager.FileManagement.StorageItemHolder;
import com.doodeec.filemanager.R;

import java.util.List;

/**
 * Created by Dusan Doodeec Bartos on 3.10.2014.
 * <p/>
 * GridView folder adapter
 */
public class FolderAdapter extends BaseAdapter {

    private BaseActivity mContext;
    private List<StorageItem> mContentItems;
    private LayoutInflater mLayoutInflater;

    public FolderAdapter(BaseActivity context, StorageItem folder) {
        this.mContext = context;
        this.mContentItems = folder.getContent();
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StorageItem item = mContentItems.get(position);
        StorageItemHolder holder;

        // using view holder pattern to reuse views
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.storage_item, null);
            holder = new StorageItemHolder(convertView);
            convertView.setTag(holder);
        }

        holder = (StorageItemHolder) convertView.getTag();
        holder.setName(item.getName());
        holder.setIcon(item.getIcon());
        holder.setSelected(mContext.isFileSelected(item));

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return mContentItems.get(position);
    }

    @Override
    public int getCount() {
        return mContentItems.size();
    }
}
