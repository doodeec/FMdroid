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
import android.widget.Toast;

import com.doodeec.filemanager.FileManagement.FolderManipulationInterface;
import com.doodeec.filemanager.FileManagement.Model.StorageItem;
import com.doodeec.filemanager.FileManagement.StorageManager;
import com.doodeec.filemanager.UIComponents.FolderFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dusan Doodeec Bartos on 3.10.2014.
 * <p/>
 * Base application activity - entry point
 */
public class BaseActivity extends Activity implements FolderManipulationInterface {

    private FragmentManager mFragmentManager;
    private List<StorageItem> mSelectedFiles;
    private boolean mSelectModeActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        StorageItem.init(getApplicationContext());
        StorageManager.setContext(this);

        mFragmentManager = getFragmentManager();
        mSelectedFiles = new ArrayList<StorageItem>();

        // just checking if fragment transaction won't crash
        assert (findViewById(R.id.content_view) != null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (StorageManager.getCurrentFolder() == null) {
            StorageManager.readFolder(new Runnable() {
                @Override
                public void run() {
                    openFragment(StorageManager.getCurrentFolder(), null);
                }
            });
        } else {
            openFragment(StorageManager.getCurrentFolder(), StorageManager.getCurrentFolder().getPath());
        }
    }

    /**
     * Reads folder content and displays new folder fragment when done
     *
     * @param folder folder to open
     */
    public void readAndOpenFolderFragment(final StorageItem folder) {
        assert (folder != null);

        StorageManager.readFolder(folder, new Runnable() {
            @Override
            public void run() {
                openFragment(folder, folder.getName());
            }
        });
    }

    /**
     * Updates title in action bar to selected files count
     */
    private void updateSelectedFilesCountTitle() {
        String title = String.format(getResources().getString(R.string.select_mode_title), mSelectedFiles.size());
        updateActionbarFolderTitle(title);
    }

    /**
     * Updates title in action bar to given string
     */
    private void updateActionbarFolderTitle(String title) {
        assert (getActionBar() != null);
        getActionBar().setTitle(title);
    }

    /**
     * Updates title in action bar to corresponding path
     */
    private void updateActionbarFolderTitle() {
        assert (getActionBar() != null);
        getActionBar().setTitle(StorageManager.getCurrentFolder().getPath());
    }

    /**
     * Calls open fragment in regular direction (opening folder)
     * For calling reverse operation (closing folder with unavailable parent)
     * use {@link #openFragment(com.doodeec.filemanager.FileManagement.Model.StorageItem, String, boolean)}
     *
     * @param folder        folder to open
     * @param backStackName backStack name to track
     */
    private void openFragment(StorageItem folder, String backStackName) {
        openFragment(folder, backStackName, false);
    }

    /**
     * Opens new folder fragment which will be added to content view
     *
     * @param folder        folder to open
     * @param backStackName backStack name to track
     */
    private void openFragment(StorageItem folder, String backStackName, boolean reverse) {
        final FolderFragment folderFragment = new FolderFragment();
        folderFragment.setFolder(folder);
        folderFragment.setInterface(this);

        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.content_view, folderFragment, folder.getPath());

        if (!reverse) {
            transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
            if (backStackName != null) {
                transaction.addToBackStack(backStackName);
            }
        } else {
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
        }

        transaction.commit();
        updateActionbarFolderTitle();
    }

    /**
     * Open file in default associated file browser
     *
     * @param file file to open
     */
    private void openFileIntent(StorageItem file) {
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
     * Reloads content of currently opened folder
     */
    private void refreshFolder() {
        Log.d("FMDROID", "refresh folder");
        StorageManager.readFolder(StorageManager.getCurrentFolder(), new Runnable() {
            @Override
            public void run() {
                reloadFolderContent();
            }
        });
    }

    /**
     * Reloads content inside gridView
     */
    private void reloadFolderContent() {
        Log.d("FMDROID", "reload current folder content");
        FolderFragment topFragment = getTopFragment();
        assert (topFragment != null);

        topFragment.setFolder(StorageManager.getCurrentFolder());
        topFragment.notifyAdapter();
    }

    /**
     * @see com.doodeec.filemanager.FileManagement.FolderManipulationInterface
     */
    @Override
    public void onFileClicked(StorageItem clickedItem) {
        if (mSelectModeActive) {
            if (mSelectedFiles.contains(clickedItem)) {
                mSelectedFiles.remove(clickedItem);

                //close selection mode if it was the last item
                if (mSelectedFiles.size() == 0) {
                    setSelectionMode(false);
                }
            } else {
                mSelectedFiles.add(clickedItem);
            }
            updateSelectedFilesCountTitle();
        } else if (clickedItem.getIsDirectory()) {
            readAndOpenFolderFragment(clickedItem);
        } else {
            openFileIntent(clickedItem);
        }
    }

    /**
     * @see com.doodeec.filemanager.FileManagement.FolderManipulationInterface
     */
    @Override
    public void onFileSelected(StorageItem clickedItem) {
        assert (clickedItem != null);

        if (!mSelectModeActive) {
            setSelectionMode(true);
        }

        if (!mSelectedFiles.contains(clickedItem)) {
            mSelectedFiles.add(clickedItem);
        }
        updateSelectedFilesCountTitle();
    }

    /**
     * Determines if file is currently selected or not
     *
     * @param itemToCheck item to check
     * @return true if item is present in selected files
     */
    public boolean isFileSelected(StorageItem itemToCheck) {
        for (StorageItem item : mSelectedFiles) {
            if (item.getFile().getAbsolutePath().equals(itemToCheck.getFile().getAbsolutePath())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Turns selection mode on/off
     *
     * @param isOpened true if selection mode is to be turned on
     */
    private void setSelectionMode(boolean isOpened) {
        mSelectedFiles.clear();
        mSelectModeActive = isOpened;
        invalidateOptionsMenu();

        if (!isOpened) {
            FolderFragment topFragment = getTopFragment();
            assert (topFragment != null);

            topFragment.notifyAdapter();
            updateActionbarFolderTitle();
        }
    }

    /**
     * Deletes all selected files
     */
    private void removeSelectedFiles() {
        for (StorageItem item : mSelectedFiles) {
            // for debug purposes
            Toast.makeText(this, "Delete " + item.getPath(), Toast.LENGTH_SHORT).show();
            /*if (!item.getFile().delete()) {
                Log.e("FMDROID", "File couldn't be deleted");
            }*/
        }
        reloadFolderContent();
    }

    /**
     * @return fragment of currently opened folder
     */
    private FolderFragment getTopFragment() {
        return (FolderFragment) getFragmentManager().findFragmentByTag(StorageManager.getCurrentFolder().getPath());
    }

    /**
     * Opens settings activity
     */
    private void openSettings() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    @Override
    public void onBackPressed() {
        // close selection mode first (if active)
        if (mSelectModeActive) {
            setSelectionMode(false);
        } else {
            if (!StorageManager.closeFolder()) {
                //root folder reached
                Toast.makeText(this, "You are in root folder", Toast.LENGTH_LONG).show();
            } else if (mFragmentManager.getBackStackEntryCount() > 0) {
                //fragments available to close
                super.onBackPressed();
            } else {
                //parent folder has to be created
                //TODO create parent folder and silently add (without backstack)
            }
        }
        updateActionbarFolderTitle();
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
                case R.id.action_close:
                    isVisible = mSelectModeActive;
                    break;
            }
            menu.getItem(i).setVisible(isVisible);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refreshFolder();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            case R.id.action_remove:
                removeSelectedFiles();
                return true;
            case R.id.action_close:
                setSelectionMode(false);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
