package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DisplayActivity extends AppCompatActivity {

    ListView dataListView;

    ArrayAdapter dataArrayAdapter;
    DataBaseHelper dataBaseHelper;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    Menu menu;

    List<DataModel> DataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display);

        dataBaseHelper = new DataBaseHelper(DisplayActivity.this);

        DataList = dataBaseHelper.getAll();

        recyclerView = findViewById(R.id.rv_dataList);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        mAdapter = new RecyclerViewAdapter(DataList, DisplayActivity.this);
        recyclerView.setAdapter(mAdapter);

        Collections.sort(DataList, DataModel.DateDescendingComparator);
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.menu_aToz:
                // sort a to z
                Collections.sort(DataList, DataModel.NameAZComparator);
                Toast.makeText(DisplayActivity.this, "Sort A to Z", Toast.LENGTH_SHORT).show();
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_zToa:
                // sort z to a
                Collections.sort(DataList, DataModel.NameZAComparator);
                Toast.makeText(DisplayActivity.this, "Sort Z to A", Toast.LENGTH_SHORT).show();
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_dataAsc:
                Collections.sort(DataList, DataModel.DateAscendingComparator);
                Toast.makeText(DisplayActivity.this, "Sort date ascending", Toast.LENGTH_SHORT).show();
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_dateDesc:
                Collections.sort(DataList, DataModel.DateDescendingComparator);
                Toast.makeText(DisplayActivity.this, "Sort date descending", Toast.LENGTH_SHORT).show();
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_AmountAsc:
                Collections.sort(DataList, DataModel.AmountAscendingComparator);
                Toast.makeText(DisplayActivity.this, "Sort amount ascending", Toast.LENGTH_SHORT).show();
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_AmountDesc:
                Collections.sort(DataList, DataModel.AmountDescendingComparator);
                Toast.makeText(DisplayActivity.this, "Sort amount descending", Toast.LENGTH_SHORT).show();
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.filter:
                AlertDialog.Builder dialog = new AlertDialog.Builder(DisplayActivity.this);
                dialog.setTitle("Choose filters");
                String[] categories = {"Bills", "Food", "Services", "Entertainment"};
                dialog.setMultiChoiceItems(categories, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {

                    }
                });
                dialog.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void ShowDataOnListView(DataBaseHelper dataBaseHelper2) {
        dataArrayAdapter = new ArrayAdapter<DataModel>(DisplayActivity.this, android.R.layout.simple_list_item_1, dataBaseHelper2.getAll());
        dataListView.setAdapter(dataArrayAdapter);
    }



    public DataBaseHelper getDataBaseHelper() {
        return dataBaseHelper;
    }
}