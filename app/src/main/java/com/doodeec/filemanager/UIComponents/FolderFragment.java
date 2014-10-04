package com.doodeec.filemanager.UIComponents;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.doodeec.filemanager.BaseActivity;
import com.doodeec.filemanager.FileManagement.Model.StorageItem;
import com.doodeec.filemanager.R;

/**
 * Created by Dusan Doodeec Bartos on 3.10.2014.
 */
public class FolderFragment extends Fragment {

    private StorageItem mFolder;
    private FolderAdapter mAdapter;
    private GridView mContentGridView;
    private BaseActivity mActivity;

    /**
     * Sets folder which fragment is bind to
     * @param folder folder
     */
    public void setFolder(StorageItem folder) {
        this.mFolder = folder;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_folder, null);
        mContentGridView = (GridView) contentView.findViewById(R.id.grid_content);

        assert (getActivity() instanceof BaseActivity);

        mActivity = (BaseActivity) getActivity();

        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new FolderAdapter(mActivity, mFolder);
        mContentGridView.setAdapter(mAdapter);

        // bind click listener to open folder/file
        mContentGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                mActivity.onFileClicked(mFolder.getContent().get(position));
                mAdapter.notifyDataSetChanged();
            }
        });

        // bind long click listener to trigger selection mode
        mContentGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mActivity.onFileSelected(mFolder.getContent().get(position));
                mAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }
}
