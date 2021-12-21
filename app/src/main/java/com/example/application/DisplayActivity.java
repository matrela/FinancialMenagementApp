package com.example.application;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DisplayActivity extends AppCompatActivity {

    DataBaseHelper dataBaseHelper;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    boolean[] checkedItems = new boolean[100];

    private final ArrayList<String> selectedFilters = new ArrayList<>();
    private String currentSearch;

    List<DataModel> dataList = new ArrayList<>();
    List<DataModel> filteredData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectedFilters.add("all");
        currentSearch = "";

        setContentView(R.layout.activity_display);

        FloatingActionButton fab = findViewById(R.id.fab);

        dataBaseHelper = new DataBaseHelper(DisplayActivity.this);

        dataList = dataBaseHelper.getAll();

        recyclerView = findViewById(R.id.rv_dataList);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        Collections.sort(dataList, DataModel.DateDescendingComparator);
        mAdapter = new RecyclerViewAdapter(dataList, DisplayActivity.this);
        recyclerView.setAdapter(mAdapter);

        searchWidget();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DisplayActivity.this, AddActivity.class);

                intent.putExtra("Go to AddActivity", 1);
                startActivity(intent);
            }
        });

    }

    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void searchWidget(){
        SearchView searchView = findViewById(R.id.Search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                currentSearch = s;
                filteredData.clear();

                for(DataModel d: dataList){
                    if(d.getName().toLowerCase().contains(s.toLowerCase())){
                        if(selectedFilters.contains("all")) {
                            filteredData.add(d);
                        }
                        else {
                            for (String filter: selectedFilters) {
                                if (d.getCategory().toLowerCase().contains(filter.toLowerCase())) {
                                    filteredData.add(d);
                                }
                            }
                        }
                    }
                }

                mAdapter = new RecyclerViewAdapter(filteredData, DisplayActivity.this);
                recyclerView.setAdapter(mAdapter);

                return false;
            }
        });
    }

    private void filter(String category){
        selectedFilters.add(category);

        filteredData.clear();

        for (DataModel d : dataList) {
            for (String filter : selectedFilters) {
                if (d.getCategory().toLowerCase().contains(filter.toLowerCase())) {
                    if(currentSearch.equals("")){
                        filteredData.add(d);
                    }else {
                        if(d.getName().toLowerCase().contains(currentSearch.toLowerCase())){
                            filteredData.add(d);
                        }
                    }
                }
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
                if(!currentSearch.isEmpty() || !selectedFilters.contains("all")){
                    Collections.sort(filteredData, DataModel.NameAZComparator);
                }else {
                    Collections.sort(dataList, DataModel.NameAZComparator);
                }
                Toast.makeText(DisplayActivity.this, "Sort A to Z", Toast.LENGTH_SHORT).show();
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_zToa:
                if(!currentSearch.isEmpty() || !selectedFilters.contains("all")){
                    Collections.sort(filteredData, DataModel.NameZAComparator);
                }else {
                    Collections.sort(dataList, DataModel.NameZAComparator);
                }
                Toast.makeText(DisplayActivity.this, "Sort Z to A", Toast.LENGTH_SHORT).show();
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_dataAsc:
                if(!currentSearch.isEmpty() || !selectedFilters.contains("all")){
                    Collections.sort(filteredData, DataModel.DateAscendingComparator);
                }else {
                    Collections.sort(dataList, DataModel.DateAscendingComparator);
                }
                Toast.makeText(DisplayActivity.this, "Sort date ascending", Toast.LENGTH_SHORT).show();
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_dateDesc:
                if(!currentSearch.isEmpty() || !selectedFilters.contains("all")){
                    Collections.sort(filteredData, DataModel.DateDescendingComparator);
                }else{
                    Collections.sort(dataList, DataModel.DateDescendingComparator);
                }
                Toast.makeText(DisplayActivity.this, "Sort date descending", Toast.LENGTH_SHORT).show();
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_AmountAsc:
                if(!currentSearch.isEmpty() || !selectedFilters.contains("all")){
                    Collections.sort(filteredData, DataModel.AmountAscendingComparator);
                }else {
                    Collections.sort(dataList, DataModel.AmountAscendingComparator);
                }
                Toast.makeText(DisplayActivity.this, "Sort amount ascending", Toast.LENGTH_SHORT).show();
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.menu_AmountDesc:
                if(!currentSearch.isEmpty() || !selectedFilters.contains("all")){
                    Collections.sort(filteredData, DataModel.AmountDescendingComparator);
                }else{
                    Collections.sort(dataList, DataModel.AmountDescendingComparator);
                }
                Toast.makeText(DisplayActivity.this, "Sort amount descending", Toast.LENGTH_SHORT).show();
                mAdapter.notifyDataSetChanged();
                return true;
            case R.id.filter:

                DataBaseHelper dataBaseHelper;
                dataBaseHelper = new DataBaseHelper(DisplayActivity.this);

                List<String> categories = dataBaseHelper.getAllCategories();

                String[] cat = categories.toArray(new String[0]);


                AlertDialog.Builder filterDialog = new AlertDialog.Builder(DisplayActivity.this);
                filterDialog.setTitle("Choose filters");

                filterDialog.setMultiChoiceItems(cat, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {

                        if(isChecked){
                            checkedItems[position] = true;
                        }else {
                            checkedItems[position] = false;
                        }

                        selectedFilters.clear();

                        for (int i=0; i<checkedItems.length; i++){
                            if(checkedItems[i]){
                                filter(cat[i]);
                            }
                        }

                        if(selectedFilters.isEmpty()){
                            selectedFilters.add("all");
                            if(currentSearch.isEmpty()) {
                                mAdapter = new RecyclerViewAdapter(dataList, DisplayActivity.this);
                            }else{
                                filteredData.clear();
                                for(DataModel d: dataList) {
                                    if (d.getName().toLowerCase().contains(currentSearch.toLowerCase())) {
                                        filteredData.add(d);
                                    }
                                }
                                mAdapter = new RecyclerViewAdapter(filteredData, DisplayActivity.this);
                            }
                            recyclerView.setAdapter(mAdapter);
                        }

                    }
                });
                filterDialog.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}