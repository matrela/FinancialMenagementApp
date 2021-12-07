package com.example.application;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddEditActivity extends AppCompatActivity {

    Button btn_add, btn_viewAll, btn_delete, btn_addNewCategory;
    EditText text_name, text_amount, text_date;
    Spinner categorySpinner;
    ImageButton btn_attachment;
    ImageView ImageViewAttachment;

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
        btn_attachment = findViewById(R.id.imageButton);
        ImageViewAttachment = findViewById(R.id.imageViewAttachment);
        btn_addNewCategory = findViewById(R.id.buttonNewCategory);

        setAdapter();

        final Intent[] intent = {getIntent()};
        int id = intent[0].getIntExtra("id", -1);
        final byte[][] bytes = new byte[1][1];
        if(id >= 0) {

            DataBaseHelper dataBaseHelper;
            dataBaseHelper = new DataBaseHelper(AddEditActivity.this);


            List<String> categories = dataBaseHelper.getAllCategories();

            selectedDataModel = dataBaseHelper.getById(id);
            System.out.println(selectedDataModel.toString());
            text_name.setText(selectedDataModel.getName());
            text_amount.setText(String.valueOf(selectedDataModel.getAmount()));

            // Setting Spinner category
            for (int index = 0; index < categories.size(); index++) {
                if (selectedDataModel.getCategory().equals(categories.get(index))){
                    System.out.println(selectedDataModel.getCategory() + " equals " + categories.get(index));
                    System.out.println("INDEX : " + index);
                    categorySpinner.setSelection(index);

                }
            }

            text_date.setText(selectedDataModel.getDate());

            Toast.makeText(this, "IMAGE: " + Arrays.toString(selectedDataModel.getImage()), Toast.LENGTH_SHORT).show();
            byte[] image = selectedDataModel.getImage();
            Bitmap BitmapImage;
            BitmapImage = BitmapFactory.decodeByteArray(image, 0, image.length);

            ImageViewAttachment.setImageBitmap(BitmapImage);

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
                                text_date.getText().toString(), bytes[0]);
                        Toast.makeText(AddEditActivity.this, dataModel.toString(), Toast.LENGTH_SHORT).show();
                        System.out.println(dataModel.toString());

                        DataBaseHelper dataBaseHelper = new DataBaseHelper(AddEditActivity.this);

                        if(dataBaseHelper.addOne(dataModel)) {
                            Toast.makeText(AddEditActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            text_name.setText("");
                            text_amount.setText("");
                            categorySpinner.setSelection(0);
                            text_date.setText(date);

                            Intent intent = new Intent(AddEditActivity.this, MainActivity.class);
                            intent.putExtra("Go to MainActivity", 1);
                            startActivity(intent);
                        }

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


        ActivityResultLauncher<Intent> selectImageActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {

                            Intent data = result.getData();

                            if (data != null) {
                                Uri selectedImage = data.getData();

                                try {
                                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(AddEditActivity.this.getContentResolver(), selectedImage);
                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                                    bytes[0] = stream.toByteArray();

                                    System.out.println(Arrays.toString(bytes[0]));

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    }
                });

        btn_attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                selectImageActivityResultLauncher.launch(intent);

            }
        });

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

        btn_addNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert = new AlertDialog.Builder(AddEditActivity.this);

                final EditText edittext = new EditText(AddEditActivity.this);
                alert.setTitle("Adding new category");
                alert.setMessage("Enter category name");

                alert.setView(edittext);

                alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Getting entered name
                        String newCategory = edittext.getText().toString();

                        if(newCategory.isEmpty()) {
                            Toast.makeText(AddEditActivity.this, "Name can't be empty.", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            // Adding new category
                            DataBaseHelper dataBaseHelper = new DataBaseHelper(AddEditActivity.this);
                            if (dataBaseHelper.addNewCategory(newCategory)) {
                                Toast.makeText(AddEditActivity.this, "Category '" + newCategory + "' added.", Toast.LENGTH_SHORT).show();
                                setAdapter();
                            } else {
                                Toast.makeText(AddEditActivity.this, "Error. Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // close AlertDialog
                    }
                });

                alert.show();
            }
        });

    }

    private void setAdapter() {
        DataBaseHelper dataBaseHelper;
        dataBaseHelper = new DataBaseHelper(AddEditActivity.this);

        List<String> categories = dataBaseHelper.getAllCategories();

        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        categorySpinner.setAdapter(categoriesAdapter);
    }

}
