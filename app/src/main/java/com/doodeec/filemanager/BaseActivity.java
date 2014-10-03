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
import android.widget.FrameLayout;
import android.widget.Toast;

import com.doodeec.filemanager.FileManagement.Model.StorageItem;
import com.doodeec.filemanager.FileManagement.StorageManager;
import com.doodeec.filemanager.UIComponents.FolderFragment;


public class BaseActivity extends Activity {

    private FrameLayout contentFrame;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        StorageItem.init(getApplicationContext());

        contentFrame = (FrameLayout) findViewById(R.id.content_view);

        mFragmentManager = getFragmentManager();

        assert (contentFrame != null);


        StorageManager.readFolder(this, new Runnable() {
            @Override
            public void run() {
                reloadFolderContent();
            }
        });
        //TODO open base folder
//        openFolderFragment();
    }

    public void openFolderFragment(final StorageItem folder) {
        assert (folder != null);

        final FolderFragment folderFragment = new FolderFragment();
        folderFragment.setFolder(folder);

        StorageManager.readFolder(this, new Runnable() {
            @Override
            public void run() {
                mTransaction = mFragmentManager.beginTransaction();
//                mTransaction.add(R.id.content_view, folderFragment);
                mTransaction.replace(R.id.content_view, folderFragment);
                mTransaction.addToBackStack(folder.getName());
                mTransaction.commit();
            }
        }, folder);
    }

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

    public void refreshFolder() {
        Log.d("FMDROID", "refresh folder");
        StorageManager.readFolder(this, new Runnable() {
            @Override
            public void run() {
                reloadFolderContent();
            }
        });
    }

    public void reloadFolderContent() {
        Log.d("FMDROID", "reload content");
        openFolderFragment(StorageManager.getCurrentFolder());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
