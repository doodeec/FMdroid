package com.doodeec.filemanager;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.doodeec.filemanager.FileManagement.Model.StorageItem;
import com.doodeec.filemanager.FileManagement.StorageManager;
import com.doodeec.filemanager.UIComponents.FolderFragment;

import java.util.ArrayList;
import java.util.List;


public class BaseActivity extends Activity {

    private RelativeLayout contentFrame;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mTransaction;
    private List<StorageItem> mSelectedFiles;
    private boolean mSelectModeActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        StorageItem.init(getApplicationContext());
        StorageManager.setContext(this);

        contentFrame = (RelativeLayout) findViewById(R.id.content_view);

        mFragmentManager = getFragmentManager();
        mSelectedFiles = new ArrayList<StorageItem>();

        assert (contentFrame != null);


        StorageManager.readFolder(new Runnable() {
            @Override
            public void run() {
                openFragment(StorageManager.getCurrentFolder(), null);
            }
        });
        //TODO open base set folder
    }

    //TODO back button updates current folder

    private void openFolderFragment(StorageItem folder) {
        assert (folder != null);

        openFragment(folder, folder.getName());
    }

    public void readAndOpenFolderFragment(final StorageItem folder) {
        assert (folder != null);

        StorageManager.readFolder(new Runnable() {
            @Override
            public void run() {
                openFragment(folder, folder.getName());
            }
        }, folder);
    }

    /**
     * Opens new folder fragment which will be added to content view
     *
     * @param folder     folder to open
     * @param folderName backStack name to track
     */
    public void openFragment(StorageItem folder, String folderName) {
        final FolderFragment folderFragment = new FolderFragment();
        folderFragment.setFolder(folder);

        mTransaction = mFragmentManager.beginTransaction();
        mTransaction.add(R.id.content_view, folderFragment);
        mTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);

        if (folderName != null) {
            mTransaction.addToBackStack(folderName);
        }

        mTransaction.commit();
    }

    /**
     * Open file in default associated file browser
     *
     * @param file file to open
     */
    public void openFileIntent(StorageItem file) {
        assert (file != null);

        Uri fileToOpen = Uri.fromFile(file.getFile());
        Intent openIntent = new Intent(android.content.Intent.ACTION_VIEW);
        String mimeType = StorageManager.getMimeType(file);

        if (mimeType != null) {
            openIntent.setDataAndType(fileToOpen, mimeType);
            startActivity(openIntent);
        } else {
            Toast.makeText(this, "Unrecognized mime format", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Refreshes folder content and reloads the gridView
     */
    public void refreshFolder() {
        Log.d("FMDROID", "refresh folder");
        StorageManager.readFolder(new Runnable() {
            @Override
            public void run() {
                reloadFolderContent();
            }
        });
    }

    public void reloadFolderContent() {
        Log.d("FMDROID", "reload current folder content");
        openFolderFragment(StorageManager.getCurrentFolder());
    }

    /**
     * File clicked - if selection mode active, add file to selection, if selection mode not active, open file/folder
     * @param clickedItem clicked item
     */
    public void onFileClicked(StorageItem clickedItem) {
        if (mSelectModeActive) {
            mSelectedFiles.add(clickedItem);
        } else if (clickedItem.getIsDirectory()) {
            readAndOpenFolderFragment(clickedItem);
        } else {
            openFileIntent(clickedItem);
        }
    }

    /**
     * Long click action
     * @param clickedItem long clicked item
     */
    public void onFileSelected(StorageItem clickedItem) {
        assert (clickedItem != null);

        if (!mSelectModeActive) {
            setSelectionMode(true);
        }
        mSelectedFiles.add(clickedItem);
    }

    /**
     * Determines if file is currently selected or not
     * @param itemToCheck item to check
     * @return true if item is present in selected files
     */
    public boolean isFileSelected(StorageItem itemToCheck) {
        for (StorageItem item: mSelectedFiles) {
            if (item.getFile().getAbsolutePath().equals(itemToCheck.getFile().getAbsolutePath())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Turns selection mode on/off
     * @param isOpened true if selection mode is to be turned on
     */
    private void setSelectionMode(boolean isOpened) {
        mSelectedFiles.clear();
        mSelectModeActive = isOpened;
        invalidateOptionsMenu();
    }

    /**
     * Deletes all selected files
     */
    private void removeSelectedFiles() {
        for (StorageItem item: mSelectedFiles) {
            if (!item.getFile().delete()) {
                Log.e("FMDROID", "File couldn't be deleted");
            }
        }
        reloadFolderContent();
    }

    @Override
    public void onBackPressed() {
        //TODO notify adapter
        // close selection mode first
        if (mSelectModeActive) {
            setSelectionMode(false);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            boolean isVisible = false;
            switch (menu.getItem(i).getItemId()) {
                case R.id.action_refresh:
                    isVisible = !mSelectModeActive;
                    break;
                case R.id.action_settings:
                    isVisible = !mSelectModeActive;
                    break;
                case R.id.action_remove:
                    isVisible = mSelectModeActive;
                    break;
            }
            menu.getItem(i).setVisible(isVisible);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refreshFolder();
                return true;
            case R.id.action_settings:
                //TODO settings
                return true;
            case R.id.action_remove:
                removeSelectedFiles();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
