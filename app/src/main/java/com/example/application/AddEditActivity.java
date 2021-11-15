package com.example.application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.TextUtilsCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddEditActivity extends AppCompatActivity {

    Button btn_add, btn_viewAll, btn_delete;
    EditText text_name, text_amount, text_date;
    Spinner categorySpinner;

    private DatePickerDialog.OnDateSetListener DateSetListener;

    String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

    DataModel selectedDataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        btn_add = findViewById(R.id.btnAdd);
        btn_viewAll = findViewById(R.id.btnViewAll2);
        btn_delete = findViewById(R.id.btnDelete);
        text_name = findViewById(R.id.editTextName);
        text_amount = findViewById(R.id.editTextAmount);
        categorySpinner = findViewById(R.id.editTextCategory);
        text_date = findViewById(R.id.editTextDate);

        ArrayList<String> categories = new ArrayList<>();
        categories.add("Bills");
        categories.add("Food");
        categories.add("Services");
        categories.add("Entertainment");

        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        categorySpinner.setAdapter(categoriesAdapter);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);

        if(id >= 0) {

            DataBaseHelper dataBaseHelper;
            dataBaseHelper = new DataBaseHelper(AddEditActivity.this);

            selectedDataModel = dataBaseHelper.getById(id);
            System.out.println(selectedDataModel.toString());
            text_name.setText(selectedDataModel.getName());
            text_amount.setText(String.valueOf(selectedDataModel.getAmount()));

            switch(selectedDataModel.getCategory()){
                case "Bills":
                    categorySpinner.setSelection(0);
                    break;
                case "Food":
                    categorySpinner.setSelection(1);
                    break;
                case "Services":
                    categorySpinner.setSelection(2);
                    break;
                case "Entertainment":
                    categorySpinner.setSelection(3);
                    break;
                default:
                    categorySpinner.setSelection(0);
            }

            text_date.setText(selectedDataModel.getDate());


        } else{
            text_date.setText(date);
        }


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(text_name.getText().toString())){
                    Toast.makeText(AddEditActivity.this, "Enter name", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(text_amount.getText().toString())) {
                    Toast.makeText(AddEditActivity.this, "Enter amount", Toast.LENGTH_SHORT).show();
                }
                else {
                    DataModel dataModel;
                    try {
                        dataModel = new DataModel(-1, text_name.getText().toString(),
                                Float.parseFloat(text_amount.getText().toString()), categorySpinner.getSelectedItem().toString(),
                                text_date.getText().toString());
                        Toast.makeText(AddEditActivity.this, dataModel.toString(), Toast.LENGTH_SHORT).show();
                        System.out.println(dataModel.toString());

                        DataBaseHelper dataBaseHelper = new DataBaseHelper(AddEditActivity.this);

                        boolean success = dataBaseHelper.addOne(dataModel);

                        Toast.makeText(AddEditActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        text_name.setText("");
                        text_amount.setText("");
                        categorySpinner.setSelection(0);
                        text_date.setText(date);


                    } catch (Exception e) {
                        Toast.makeText(AddEditActivity.this, "Error adding", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        text_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        AddEditActivity.this, android.R.style.Theme_DeviceDefault_Dialog_MinWidth,
                        DateSetListener, year, month, day);
                dialog.show();
            }
        });

        DateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                String date = dayOfMonth + "/" + month + "/" + year;
                text_date.setText(date);
            }
        };

        btn_viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddEditActivity.this, DisplayActivity.class);

                intent.putExtra("Go to DisplayActivity", 1);
                startActivity(intent);
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataBaseHelper dataBaseHelper;
                dataBaseHelper = new DataBaseHelper(AddEditActivity.this);

                if (dataBaseHelper.deleteOne(selectedDataModel)){
                    Toast.makeText(AddEditActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}