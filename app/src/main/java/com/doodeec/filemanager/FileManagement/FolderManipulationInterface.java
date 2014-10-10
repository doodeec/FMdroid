package com.doodeec.filemanager.FileManagement;

import com.doodeec.filemanager.FileManagement.Model.StorageItem;

/**
 * Created by Dusan Doodeec Bartos on 4.10.2014.
 */
public interface FolderManipulationInterface {
    /**
     * File clicked - if selection mode active, add file to selection, if selection mode not active, open file/folder
     *
     * @param clickedItem clicked item
     */
    public void onFileClicked(StorageItem clickedItem);

    /**
     * Long click action
     *
     * @param clickedItem long clicked item
     */
    public void onFileSelected(StorageItem clickedItem);

    /**
     * Determines if file is currently selected or not
     *
     * @param itemToCheck item to check
     * @return true if item is present in selected files
     */
    public boolean isFileSelected(StorageItem itemToCheck);

    public void onUpClicked(StorageItem folder);
}
