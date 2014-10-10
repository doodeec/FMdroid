package com.doodeec.filemanager;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.Toast;

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

        showFolder(StorageManager.getCurrentFolder(), false, false);
    }

    private void showFolder(final StorageItem folder, final boolean animate, final boolean toStack) {
        if (folder.getContent(false) != null) {
            FolderFragment folderFragment = new FolderFragment();
            folderFragment.setFolder(folder);
            folderFragment.setInterface(this);

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            if (animate) {
                transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
            }
            transaction.replace(R.id.dialog_frame, folderFragment, folder.getPath());
            if (toStack) {
                transaction.addToBackStack(folder.getPath());
            }
            transaction.commit();
        } else {
            StorageManager.readFolder(folder, new Runnable() {
                @Override
                public void run() {
                    showFolder(folder, animate, toStack);
                }
            });
        }
    }

    @Override
    public void onFileClicked(StorageItem clickedItem) {
        showFolder(clickedItem, true, true);
    }

    @Override
    public void onFileSelected(StorageItem clickedItem) {
        if (clickedItem.getIsBlank()) {
            //TODO set current folder
//            StorageManager.setBasePath();
        } else {
            StorageManager.setBasePath(clickedItem.getPath());
        }
        finish();
    }

    @Override
    public boolean isFileSelected(StorageItem itemToCheck) {
        return false;
    }

    @Override
    public void onUpClicked(StorageItem folder) {
        if (!StorageManager.closeFolder()) {
            //root folder reached
            Toast.makeText(this, "You are in root folder", Toast.LENGTH_LONG).show();
        } else if (getFragmentManager().getBackStackEntryCount() > 0) {
            //fragments available to close
            getFragmentManager().popBackStack();
        } else {
            //parent folder has to be created
            //TODO create parent folder and silently add (without backstack)
            showFolder(StorageItem.createStorageItem(folder.getFile().getParentFile()), false, false);
        }
    }
}
