package com.example.application;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    String selectedFilter = "all";
    boolean[] checkedItems = {false, false, false, false};

    List<DataModel> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_display);

        dataBaseHelper = new DataBaseHelper(DisplayActivity.this);

        dataList = dataBaseHelper.getAll();

        recyclerView = findViewById(R.id.rv_dataList);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        mAdapter = new RecyclerViewAdapter(dataList, DisplayActivity.this);
        recyclerView.setAdapter(mAdapter);

        Collections.sort(dataList, DataModel.DateDescendingComparator);
        mAdapter.notifyDataSetChanged();

        searchWidget();

    }

    private void searchWidget(){
        SearchView searchView = (SearchView) findViewById(R.id.Search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                List<DataModel> filteredData = new ArrayList<DataModel>();

                for(DataModel d: dataList){
                    if(d.getName().toLowerCase().contains(s.toLowerCase())){
                        filteredData.add(d);
                    }
                }

                mAdapter = new RecyclerViewAdapter(filteredData, DisplayActivity.this);
                recyclerView.setAdapter(mAdapter);

                return false;
            }
        });
    }

    private void filter(String status){
        selectedFilter = status;

        List<DataModel> filteredData = new ArrayList<DataModel>();

        for(DataModel d: dataList){
            if(d.getCategory().toLowerCase().contains(status.toLowerCase())){
                filteredData.add(d);
            }
        }

        mAdapter = new RecyclerViewAdapter(filteredData, DisplayActivity.this);
        recyclerView.setAdapter(mAdapter);
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
                Collections.sort(dataList, DataModel.NameAZComparator);
                Toast.makeText(DisplayActivity.this, "Sort A to Z", Toast.LENGTH_SHORT).show();
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_zToa:
                // sort z to a
                Collections.sort(dataList, DataModel.NameZAComparator);
                Toast.makeText(DisplayActivity.this, "Sort Z to A", Toast.LENGTH_SHORT).show();
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_dataAsc:
                Collections.sort(dataList, DataModel.DateAscendingComparator);
                Toast.makeText(DisplayActivity.this, "Sort date ascending", Toast.LENGTH_SHORT).show();
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_dateDesc:
                Collections.sort(dataList, DataModel.DateDescendingComparator);
                Toast.makeText(DisplayActivity.this, "Sort date descending", Toast.LENGTH_SHORT).show();
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_AmountAsc:
                Collections.sort(dataList, DataModel.AmountAscendingComparator);
                Toast.makeText(DisplayActivity.this, "Sort amount ascending", Toast.LENGTH_SHORT).show();
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_AmountDesc:
                Collections.sort(dataList, DataModel.AmountDescendingComparator);
                Toast.makeText(DisplayActivity.this, "Sort amount descending", Toast.LENGTH_SHORT).show();
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.filter:
                AlertDialog.Builder filterDialog = new AlertDialog.Builder(DisplayActivity.this);

                filterDialog.setTitle("Choose filters");
                String[] categories = {"Bills", "Food", "Services", "Entertainment"};

                filterDialog.setMultiChoiceItems(categories, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {

                        if(isChecked){
                            checkedItems[position] = true;
                        }else {
                            checkedItems[position] = false;
                        }

                        for (int i=0; i<checkedItems.length; i++){
                            if(checkedItems[i]){
                                System.out.println(categories[i].toString());
                                filter(categories[i]);
                            }
                        }

                    }
                });
                filterDialog.show();
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