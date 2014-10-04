package com.doodeec.filemanager.FileManagement;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.doodeec.filemanager.FileManagement.Model.StorageItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Dusan Doodeec Bartos on 3.10.2014.
 */
public class StorageManager {

    private static Activity mContext;
    private static String currentPath = "";
    private static HashMap<String, StorageItem> allContent = new HashMap<String, StorageItem>();

    public static void setContext(Activity context) {
        mContext = context;
    }

    public static StorageItem getCurrentFolder() {
        return allContent.get(currentPath);
    }

    public static void readFolder(Runnable doneListener) {
        File folder = new File(Environment.getExternalStorageDirectory(), currentPath);
        StorageItem baseFolder = new StorageItem(folder);
        readFolder(baseFolder, doneListener);
    }

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

                Arrays.sort(files);

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

    public static void closeFolder() {
        StorageItem currentFolder = allContent.get(currentPath);
        assert (currentFolder != null);

        File parentFile = currentFolder.getFile().getParentFile();
        if (parentFile != null) {
            if (allContent.get(parentFile.getAbsolutePath()) == null) {
                StorageItem baseFolder = new StorageItem(parentFile);
                readFolder(baseFolder, null);
            } else {
                currentPath = parentFile.getAbsolutePath();
            }
        }
    }


    //TODO handle music
    public static String getMimeType(StorageItem item) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(item.getFile().getAbsolutePath());
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }

        return type;
    }
}
