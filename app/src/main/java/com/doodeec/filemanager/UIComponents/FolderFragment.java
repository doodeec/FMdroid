package com.doodeec.filemanager.UIComponents;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.doodeec.filemanager.BaseActivity;
import com.doodeec.filemanager.FileManagement.Model.StorageItem;
import com.doodeec.filemanager.R;

/**
 * Created by Dusan Doodeec Bartos on 3.10.2014.
 */
public class FolderFragment extends Fragment {

    private StorageItem mFolder;
    private GridView mContentView;

    /**
     * Sets folder which fragment is bind to
     * @param folder folder
     */
    public void setFolder(StorageItem folder) {
        this.mFolder = folder;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = (GridView) inflater.inflate(R.layout.fragment_folder, null);
        return mContentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContentView.setAdapter(new FolderAdapter(getActivity(), mFolder));

        mContentView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();

                assert (getActivity() instanceof BaseActivity);

                // opens new folder fragment, or file intent
                StorageItem clickedItem = mFolder.getContent().get(position);
                if (clickedItem.getIsDirectory()) {
                    ((BaseActivity) getActivity()).openFolderFragment(clickedItem);
                } else {
                    ((BaseActivity) getActivity()).openFileIntent(clickedItem);
                }
            }
        });
    }
}
