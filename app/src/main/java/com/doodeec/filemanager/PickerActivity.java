package com.doodeec.filemanager;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.doodeec.filemanager.FileManagement.FolderManipulationInterface;
import com.doodeec.filemanager.FileManagement.Model.StorageItem;
import com.doodeec.filemanager.FileManagement.StorageManager;
import com.doodeec.filemanager.UIComponents.FolderFragment;

/**
 * Created by Dusan Doodeec Bartos on 10.10.2014.
 */
public class PickerActivity extends Activity implements FolderManipulationInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_dialog);
        setTitle(R.string.setting_dialog_title);

        showFolder(StorageManager.getCurrentFolder());
    }

    private void showFolder(final StorageItem folder) {
        if (folder.getContent(false) != null) {
            FolderFragment folderFragment = new FolderFragment();
            folderFragment.setFolder(folder);
            folderFragment.setInterface(this);

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
            transaction.replace(R.id.dialog_frame, folderFragment, folder.getPath());
            transaction.commit();
        } else {
            StorageManager.readFolder(folder, new Runnable() {
                @Override
                public void run() {
                    showFolder(folder);
                }
            });
        }
    }

    @Override
    public void onFileClicked(StorageItem clickedItem) {
        showFolder(clickedItem);
    }

    @Override
    public void onFileSelected(StorageItem clickedItem) {
        StorageManager.setBasePath(clickedItem.getPath());
        finish();
    }

    @Override
    public boolean isFileSelected(StorageItem itemToCheck) {
        return false;
    }
}
