package com.example.admin.zestawpodroznika;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class LocalisationsListActivity extends AppCompatActivity {

    private ListView localListView;
    private LocDbDirector db;
    private LocalisationAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localisations_list);
        db = new LocDbDirector(this);


        localListView = (ListView) findViewById(R.id.local_list_view);
        adapter = new LocalisationAdapter(this);
        localListView.setAdapter(adapter);
        registerForContextMenu(localListView);


    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.local_list_view) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int positon = info.position;

        switch (item.getItemId()) {
            case R.id.itm_open_on_map: {
                Localisation loc=db.GetLoc(positon);
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", loc.getLati(), loc.getLati());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
                return true;
            }
            case R.id.itm_delete: {

                db.DeleteRow(positon);
                adapter.notifyDataSetChanged();

                return true;
            }
            default:
                return super.onContextItemSelected(item);
        }
    }


}
