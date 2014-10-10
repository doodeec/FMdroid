package com.doodeec.filemanager;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.doodeec.filemanager.FileManagement.StorageManager;

import java.util.ArrayList;
import java.util.Arrays;

public class SettingsActivity extends ListActivity {

    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] values = new String[] { "Default folder" };

        final ArrayList<String> list = new ArrayList<String>();
        list.addAll(Arrays.asList(values));

        mAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_2, android.R.id.text1, list) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView fieldName = (TextView) view.findViewById(android.R.id.text1);
                TextView fieldValue = (TextView) view.findViewById(android.R.id.text2);

                fieldName.setText(list.get(position));
                fieldValue.setHint(R.string.default_folder_not_set);
                fieldValue.setText(StorageManager.getCurrentBasePath());
                return view;
            }
        };

        setListAdapter(mAdapter);

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent(SettingsActivity.this, PickerActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
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
