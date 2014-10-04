package com.doodeec.filemanager.FileManagement;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.doodeec.filemanager.FileManagement.Model.StorageItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dusan Doodeec Bartos on 3.10.2014.
 * <p/>
 * Storage manager handles folder reading/keeping track of currently opened folder
 */
public class StorageManager {

    private static final String PREF_KEY = "com.doodeec.fmdroid";
    private static final String DEFAULT_FOLDER_KEY = "defaultFolder";

    private static Activity mContext;
    private static String currentPath = "";
    private static String currentBasePath = "";
    private static HashMap<String, StorageItem> allContent = new HashMap<String, StorageItem>();
    private static SharedPreferences mPreferences;

    private static File rootFolder = new File(Environment.getExternalStorageDirectory(), "");

    /**
     * Register context reference for thread callbacks
     *
     * @param context activity
     */
    public static void setContext(Activity context) {
        mContext = context;
        mPreferences = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);

        currentBasePath = mPreferences.getString(DEFAULT_FOLDER_KEY, "");
    }

    /**
     * @return currently shown(opened) folder
     */
    public static StorageItem getCurrentFolder() {
        return allContent.get(currentPath);
    }

    /**
     * Sets base path of folder to open when app is started
     */
    public static void setBasePath(String defaultFolder) {
        currentBasePath = defaultFolder;
        mPreferences.edit().putString(DEFAULT_FOLDER_KEY, defaultFolder).apply();
    }

    /**
     * @return currently set base folder (to open when app starts)
     */
    public static String getCurrentBasePath() {
        return currentBasePath;
    }

    /**
     * Used initially when folder is not selected yet
     *
     * @param doneListener finished callback
     */
    public static void readFolder(Runnable doneListener) {
        File folder = currentBasePath.equals("") ? rootFolder : new File(currentBasePath);
        StorageItem baseFolder = new StorageItem(folder);
        readFolder(baseFolder, doneListener);
    }

    /**
     * Reads folder content in own thread
     *
     * @param folder       folder to read
     * @param doneListener finished callback
     */
    public synchronized static void readFolder(final StorageItem folder, final Runnable doneListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                currentPath = folder.getPath();

                if (!folder.getFile().exists()) {
                    Log.e("FMDROID", "folder does not exist");
                    return;
                }

                File[] files = folder.getFile().listFiles();
                assert (files != null);

                //sorts alphabetically
                Arrays.sort(files);
                //sorts folders in the front
                Arrays.sort(files, new FileComparator());

                List<StorageItem> content = new ArrayList<StorageItem>();
                for (File file : files) {
                    content.add(StorageItem.createStorageItem(file));
                }

                folder.setContent(content);
                allContent.put(folder.getPath(), folder);

                mContext.runOnUiThread(doneListener);
            }
        }).start();
    }

    /**
     * Keeps track of currently opened folder (for refreshing functionality)
     */
    public static boolean closeFolder() {
        StorageItem currentFolder = allContent.get(currentPath);
        assert (currentFolder != null);

        if (currentFolder.getPath().equals(rootFolder.getAbsolutePath())) {
            return false;
        } else {
            File parentFile = currentFolder.getFile().getParentFile();
            if (parentFile != null) {
                if (allContent.get(parentFile.getAbsolutePath()) == null) {
                    StorageItem baseFolder = new StorageItem(parentFile);
                    readFolder(baseFolder, null);
                } else {
                    currentPath = parentFile.getAbsolutePath();
                }
            }
            return true;
        }
    }

    /**
     * @param file file to observe
     * @return extension of the file
     */
    public static String getExtension(File file) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
        if (extension.equals("")) {
            if (file.getAbsolutePath().contains(".mp3")) {
                extension = "mp3";
            }
            //TODO other non-recognized extensions
        }
        return extension;
    }

    /**
     * @param item file to open
     * @return mime type of file, to be added to file intent
     */
    public static String getMimeType(StorageItem item) {
        String type = null;
        String extension = getExtension(item.getFile());
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }

        return type;
    }


    /**
     * Comparator for sorting files
     * Folders will came before single files
     */
    private static class FileComparator implements Comparator<File> {
        private enum FileType {
            Directory(0),
            SingleFile(1);

            public Integer mValue;

            private FileType(Integer value) {
                this.mValue = value;
            }
        }

        private FileType getFileType(File file) {
            return file.isDirectory() ? FileType.Directory : FileType.SingleFile;
        }

        public int compare(File f1, File f2) {
            return getFileType(f1).mValue.compareTo(getFileType(f2).mValue);
        }
    }
}
