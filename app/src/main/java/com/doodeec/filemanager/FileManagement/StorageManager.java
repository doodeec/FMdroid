package com.doodeec.filemanager.FileManagement;

import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.doodeec.filemanager.BaseActivity;
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

    private static String folderName = "";
    private static String currentPath = "";
    private static HashMap<String, StorageItem> allContent = new HashMap<String, StorageItem>();

    public static StorageItem getCurrentFolder() {
        return allContent.get(currentPath);
    }

    public static void readFolder(BaseActivity context, Runnable doneListener) {
        File folder = new File(Environment.getExternalStorageDirectory(), currentPath);
        StorageItem baseFolder = new StorageItem(folder);
        readFolder(context, doneListener, baseFolder);
    }

    public synchronized static void readFolder(final BaseActivity context, final Runnable doneListener, final StorageItem folder) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                currentPath = folder.getPath();

                if (!folder.getFile().exists()) {
                    Log.e("FMDROID", "folder does not exist");
                }

                File[] files = folder.getFile().listFiles();
                Arrays.sort(files);

                List<StorageItem> content = new ArrayList<StorageItem>();
                for (File file: files) {
                    content.add(StorageItem.createStorageItem(file));
                }

                folder.setContent(content);
                allContent.put(folder.getPath(), folder);

                context.runOnUiThread(doneListener);
            }
        }).start();
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
