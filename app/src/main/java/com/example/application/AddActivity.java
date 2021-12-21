package com.example.application;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddActivity extends AppCompatActivity {

    //PL money format (2 decimals)
    DecimalFormat plDf = new DecimalFormat("#,##0.00");

    TextInputEditText editTextName, editTextAmount, editTextDate, editTextAttachment, editTextNotifications;
    AutoCompleteTextView exposedDropdownCategoryMenu;
    Button addButton;

    NotificationManagerCompat manager;

    private DatePickerDialog.OnDateSetListener DateSetListener, mDateSetListener;

    String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
    String dateToDataBase = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    final byte[][] bytes = new byte[1][1];
    boolean isImageAdded = false;
    long timeOfNotification = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        findViewById();

        editTextAmount.setText(R.string.amount_zero);
        setAdapter();
        editTextDate.setText(currentDate);

        manager = NotificationManagerCompat.from(AddActivity.this);

        getDataFromNotification();

        editTextAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                if(Objects.requireNonNull(editTextAmount.getText()).toString().equals(getString(R.string.amount_zero))) {
                    editTextAmount.getText().clear();
                }

                if(!hasFocus){
                    if(editTextAmount.getText().toString().isEmpty()) {
                        editTextAmount.setText(R.string.amount_zero);
                    }
                }

            }
        });

        editTextDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                showDatePickerDialog();
            }
        });

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        exposedDropdownCategoryMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                i+=1;

                if(i==adapterView.getAdapter().getCount()){

                    AlertDialog.Builder alert = new AlertDialog.Builder(AddActivity.this);

                    final EditText edittext = new EditText(AddActivity.this);
                    alert.setTitle("Adding new category");
                    alert.setMessage("Enter category name");

                    alert.setView(edittext);

                    alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Getting entered name
                            String newCategory = edittext.getText().toString();

                            if(newCategory.isEmpty()) {
                                Toast.makeText(AddActivity.this, "Name can't be empty.", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                // Adding new category
                                DataBaseHelper dataBaseHelper = new DataBaseHelper(AddActivity.this);
                                if (dataBaseHelper.addNewCategory(newCategory)) {
                                    Toast.makeText(AddActivity.this, "Category '" + newCategory + "' added.", Toast.LENGTH_SHORT).show();
                                    Log.i("Adding new category", "New category: " + newCategory + " added");
                                    setAdapter();
                                } else {
                                    Toast.makeText(AddActivity.this, "Error. Please try again", Toast.LENGTH_SHORT).show();
                                    Log.e("Adding new category", "Error while adding category");
                                    }
                            }
                            exposedDropdownCategoryMenu.setText(exposedDropdownCategoryMenu.getAdapter().getItem(0).toString(), false);
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            exposedDropdownCategoryMenu.setText(exposedDropdownCategoryMenu.getAdapter().getItem(0).toString(), false);
                            // close AlertDialog
                        }
                    });

                    alert.show();
                }
            }
        });


        DateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);

                SimpleDateFormat dBDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy");

                dateToDataBase = dBDateFormat.format(calendar.getTime());
                String dateToDisplay = displayDateFormat.format(calendar.getTime());
                editTextDate.setText(dateToDisplay);

            }
        };

        editTextAttachment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                getImage();
            }
        });

        editTextAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImage();
            }
        });

        editTextNotifications.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                setNotification();
            }
        });

        editTextNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNotification();
            }
        });


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(Objects.requireNonNull(editTextName.getText()).toString())){
                    Toast.makeText(AddActivity.this, "Enter name", Toast.LENGTH_SHORT).show();
                } else if ((Objects.requireNonNull(editTextAmount.getText())).toString().isEmpty() || editTextAmount.getText().toString().equals(getString(R.string.amount_zero))) {
                    Toast.makeText(AddActivity.this, "Enter amount", Toast.LENGTH_SHORT).show();
                }
                else {
                    DataModel dataModel;
                    try {

                        String name = editTextName.getText().toString();
                        float amount = Float.parseFloat(editTextAmount.getText().toString());
                        String category = exposedDropdownCategoryMenu.getText().toString();

                        if(isImageAdded) {
                            dataModel = new DataModel(-1, name, amount, category, dateToDataBase, bytes[0]);
                        }else {
                            dataModel = new DataModel(-1, name, amount, category, dateToDataBase);
                        }
                        Log.i("Adding DataModel to database", "DataModel created: " + dataModel.toString());

                        DataBaseHelper dataBaseHelper = new DataBaseHelper(AddActivity.this);

                        if(dataBaseHelper.addOne(dataModel)) {
                            Toast.makeText(AddActivity.this, "Data added successfully!", Toast.LENGTH_SHORT).show();
                            Log.i("AddActivity", "DataModel added to database");

                            if(timeOfNotification != -1){
                                String title = "Repeat transaction: \"" + name + "\"";
                                String message = "Amount: " + amount + " PLN" + "\nCategory: " + category + "\nTap here to repeat";

                                JSONObject DataJSON = new JSONObject();
                                DataJSON.put("name", name);
                                DataJSON.put("amount", amount);
                                DataJSON.put("category", category);

                                scheduleNotification(title, message, DataJSON, timeOfNotification);
                            }

                            editTextName.getText().clear();
                            editTextAmount.getText().clear();
                            exposedDropdownCategoryMenu.setText(exposedDropdownCategoryMenu.getAdapter().getItem(0).toString(), false);
                            editTextDate.setText(currentDate);

                            if(Balance.checkIfLimitReach(AddActivity.this)){
                                System.out.println("Limit reach");
                                sendNotificationLimitReach();
                            }

                            Intent intent = new Intent(AddActivity.this, MainActivity.class);
                            intent.putExtra("Go to MainActivity", 0);
                            startActivity(intent);
                        }

                    } catch (Exception e) {
                        Toast.makeText(AddActivity.this, "Error adding", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            private void sendNotificationLimitReach() {

                String title = "Limit reach";
                String message = "Spending limit reach";

                Notification notification = new NotificationCompat.Builder(AddActivity.this, NotificationBuilder.CHANNEL_1_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .build();

                manager.notify(1, notification);
            }
        });

    }

    private void findViewById() {
        editTextName = findViewById(R.id.editTextName);
        editTextAmount = findViewById(R.id.editTextAmount);
        exposedDropdownCategoryMenu = findViewById(R.id.filled_exposed_dropdown);
        editTextDate = findViewById(R.id.editTextDate);
        editTextAttachment = findViewById(R.id.editTextAttachment);
        editTextNotifications = findViewById(R.id.editTextNotification);
        addButton = findViewById(R.id.btnAdd);
    }

    private void getDataFromNotification() {
        Intent getDataIntent = getIntent();
        String receivedData = getDataIntent.getStringExtra(NotificationBuilder.jsonDataFromNotification);

        if(receivedData!=null){
            Log.i("Get data from notification", "Received data: " + receivedData);

            try {
                JSONObject jsonObj = new JSONObject(receivedData);
                String name = jsonObj.getString("name");
                float amount = Float.parseFloat(jsonObj.getString("amount"));
                String category = jsonObj.getString("category");

                Log.i("Get data from notification",
                        "Parsed data: " + "\nname: " + name + "\namount: " + amount + "\ncategory: " + category);

                editTextName.setText(name);
                editTextAmount.setText(plDf.format(amount) + " PLN");

                DataBaseHelper dataBaseHelper;
                dataBaseHelper = new DataBaseHelper(AddActivity.this);

                List<String> categories = dataBaseHelper.getAllCategories();

                for (int index = 0; index < categories.size(); index++) {
                    if (category.equals(categories.get(index))){
                        exposedDropdownCategoryMenu.setText(exposedDropdownCategoryMenu.getAdapter().getItem(index).toString(), false);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("Get data from notification", "Parsing data error");
            }
        }
        Log.i("Get data from notification", "Received data empty");
    }

    private void getImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        selectImageActivityResultLauncher.launch(intent);
    }

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
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(AddActivity.this.getContentResolver(), selectedImage);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                                bytes[0] = stream.toByteArray();

                                Log.i("Converting bitmap image into byte array", "Converted");
                                isImageAdded = true;
                                editTextAttachment.setText("Attachment added");

                            } catch (IOException e) {
                                e.printStackTrace();
                                isImageAdded = false;
                                Log.e("Converting bitmap image into byte array", "Error while converting");
                            }

                        }
                    }
                }
            });

    private void showDatePickerDialog() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                AddActivity.this, android.R.style.Theme_DeviceDefault_Dialog_MinWidth,
                DateSetListener, year, month, day);
        dialog.show();
    }

    private void setNotification(){
        String[] notifications = {"Tomorrow", "Next Week", "Next Month", "Custom"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
        builder.setItems(notifications, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int selectedItem) {
                Calendar cal;
                switch (selectedItem) {
                    case 0:
                        GetNextDate(Calendar.DATE, 1);
                        editTextNotifications.setText("Notification: " + notifications[0]);
                        break;
                    case 1:
                        GetNextDate(Calendar.DATE, 7);
                        editTextNotifications.setText("Notification: " + notifications[1]);
                        break;
                    case 2:
                        GetNextDate(Calendar.MONTH, 1);
                        editTextNotifications.setText("Notification: " + notifications[2]);
                        break;
                    case 3:
                        cal = Calendar.getInstance();
                        int year = cal.get(Calendar.YEAR);
                        int month = cal.get(Calendar.MONTH);
                        int day = cal.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog datePickerDialog = new DatePickerDialog(
                                AddActivity.this,
                                android.R.style.Theme_DeviceDefault_Dialog_MinWidth,
                                mDateSetListener,
                                year, month, day);
                        datePickerDialog.show();
                        break;
                    default:

                }
            }
        });
        builder.show();

        mDateSetListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                Calendar cal = Calendar.getInstance();

                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, day);
                cal.set(Calendar.HOUR_OF_DAY, 8);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                timeOfNotification = cal.getTimeInMillis();
                editTextNotifications.setText("Notification: " + formatter.format(timeOfNotification));
                Log.i("mDateSetListener", "Selected time of notification: " + formatter.format(timeOfNotification));
            }
        };
    }

    private void GetNextDate(int date, int i) {
        Calendar cal;
        cal = Calendar.getInstance();

        cal.add(date, i);
        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        timeOfNotification = cal.getTimeInMillis();

        Log.i("GetNextDate", "Selected time of notification: " + formatter.format(timeOfNotification));
    }

    private void setAdapter() {
        DataBaseHelper dataBaseHelper;
        dataBaseHelper = new DataBaseHelper(AddActivity.this);

        List<String> categories = dataBaseHelper.getAllCategories();
        categories.add("Add new category");

        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        exposedDropdownCategoryMenu.setAdapter(categoriesAdapter);

        exposedDropdownCategoryMenu.setText(exposedDropdownCategoryMenu.getAdapter().getItem(0).toString(), false);
    }

    private void scheduleNotification(String title, String message, JSONObject jsonData, long time) {

        Intent intent = new Intent(AddActivity.this, NotificationBuilder.class);
        intent.putExtra(NotificationBuilder.titleExtra, title);
        intent.putExtra(NotificationBuilder.messageExtra, message);
        intent.putExtra(NotificationBuilder.messageExtra, message);
        intent.putExtra(NotificationBuilder.jsonDataToNotification, jsonData.toString());


        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(),
                NotificationBuilder.getID(),
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) AddActivity.this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent);

        Calendar cal;
        cal = Calendar.getInstance();
        cal.setTimeInMillis(time);

        Log.i("GetNextDate", "Notification scheduled at: " + formatter.format(timeOfNotification));
    }

}