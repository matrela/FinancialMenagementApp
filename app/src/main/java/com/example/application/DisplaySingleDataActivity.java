package com.example.application;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;
import java.util.Arrays;

public class DisplaySingleDataActivity extends AppCompatActivity {

    //PL money format (2 decimals)
    DecimalFormat plDf = new DecimalFormat("#,##0.00");

    TextInputEditText editTextName, editTextAmount, editTextAmountCategory, editTextDate;
    ShapeableImageView shapeableImageView;
    Button deleteButton;

    Boolean isClicked;

    DataModel selectedDataModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_single_data);

        editTextName = findViewById(R.id.editTextName);
        editTextAmount = findViewById(R.id.editTextAmount);
        editTextAmountCategory = findViewById(R.id.filled_exposed_dropdown);
        editTextDate = findViewById(R.id.editTextDate);
        shapeableImageView = findViewById(R.id.image);
        deleteButton = findViewById(R.id.btnDelete);


        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);

        if (id >= 0) {

            DataBaseHelper dataBaseHelper;
            dataBaseHelper = new DataBaseHelper(DisplaySingleDataActivity.this);

            selectedDataModel = dataBaseHelper.getById(id);

            editTextName.setText(selectedDataModel.getName());
            editTextAmount.setText(plDf.format(selectedDataModel.getAmount()) + getString(R.string.pln));
            editTextAmountCategory.setText(selectedDataModel.getCategory());
            editTextDate.setText(selectedDataModel.getDate());

            System.out.println("IMAGE: " + Arrays.toString(selectedDataModel.getImage()));

            if(!(selectedDataModel.getImage() == null)) {

                byte[] image = selectedDataModel.getImage();
                Bitmap BitmapImage;
                BitmapImage = BitmapFactory.decodeByteArray(image, 0, image.length);

                shapeableImageView.setImageBitmap(BitmapImage);
            }
        }

        isClicked = false;

        shapeableImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isClicked) {
                    shapeableImageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                    shapeableImageView.setScaleType(ShapeableImageView.ScaleType.FIT_XY);
                    isClicked = true;
                }
                else{
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = Gravity.CENTER;
                    layoutParams.bottomMargin = 20;
                    shapeableImageView.setLayoutParams(layoutParams);
                    isClicked = false;
                }

            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataBaseHelper dataBaseHelper;
                dataBaseHelper = new DataBaseHelper(DisplaySingleDataActivity.this);

                if (dataBaseHelper.deleteOne(selectedDataModel)){
                    Toast.makeText(DisplaySingleDataActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(DisplaySingleDataActivity.this, DisplayActivity.class);

                    intent.putExtra("Go to DisplayActivity class", 1);
                    startActivity(intent);
                    DisplaySingleDataActivity.this.finish();
                }
            }
        });

    }
}