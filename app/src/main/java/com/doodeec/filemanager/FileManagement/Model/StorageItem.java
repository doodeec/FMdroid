package com.doodeec.filemanager.FileManagement.Model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.doodeec.filemanager.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dusan Doodeec Bartos on 3.10.2014.
 * <p/>
 * Extended File definition, not extending directly because of code separation
 */
public class StorageItem {
    public static Drawable FOLDER_ICON = null;
    public static Drawable FILE_ICON = null;

    public static void init(Context context) {
        FOLDER_ICON = context.getResources().getDrawable(R.drawable.ic_action_collection);
        FILE_ICON = context.getResources().getDrawable(R.drawable.ic_action_copy);
    }

    private File mFile;
    private Drawable mIcon;
    private List<StorageItem> mContentItems;
    private boolean isDirectory = false;

    public static StorageItem createStorageItem(File file) {
        StorageItem item = new StorageItem(file);

        if (file.isDirectory()) {
            item.mIcon = FOLDER_ICON;
            item.isDirectory = true;
        } else if (file.isFile()) {
            item.mIcon = FILE_ICON;
        }

        return item;
    }

    public StorageItem(File file) {
        this.mFile = file;
    }

    public void setContent(List<StorageItem> content) {
        if (mFile.isDirectory()) {
            this.mContentItems = content;
        } else {
            Log.e("FMDROID", "Can not add content to leaf file");
        }
    }

    /**
     * @return original associated file instance
     */
    public File getFile() {
        return mFile;
    }

    /**
     * @return content children nodes of this node
     */
    public List<StorageItem> getContent(boolean onlyFolders) {
        if (onlyFolders) {
            List<StorageItem> folders = new ArrayList<StorageItem>();
            for (StorageItem item: mContentItems) {
                if (item.isDirectory) folders.add(item);
            }
            return folders;
        }

        return mContentItems;
    }

    /**
     * @return node drawable icon
     */
    public Drawable getIcon() {
        return mIcon;
    }

    public String getPath() {
        return mFile.getAbsolutePath();
    }

    /**
     * @return node name
     */
    public String getName() {
        return mFile.getName();
    }

    /**
     * @return true if node points to directory, false if it points to single file
     */
    public boolean getIsDirectory() {
        return isDirectory;
    }
}
