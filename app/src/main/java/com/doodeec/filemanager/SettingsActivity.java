package com.doodeec.filemanager;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.doodeec.filemanager.FileManagement.StorageManager;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingsActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] values = new String[] { "Default folder" };

        final ArrayList<String> list = new ArrayList<String>();
        list.addAll(Arrays.asList(values));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_2, android.R.id.text1, list) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView fieldName = (TextView) view.findViewById(android.R.id.text1);
                TextView fieldValue = (TextView) view.findViewById(android.R.id.text2);

                fieldName.setText(list.get(position));
                fieldValue.setText(StorageManager.getCurrentBasePath());
                return view;
            }
        };

        setListAdapter(adapter);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //TODO open selection dialog
                        Toast.makeText(SettingsActivity.this, "Select default folder", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
