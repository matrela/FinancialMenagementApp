package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //PL money format (2 decimals)
    DecimalFormat plDf = new DecimalFormat("#,##0.00");

    LinearLayout linearLayout_ViewAll, linearLayout_Add, linearLayout_updateData;
    ImageView refreshImage;
    TextView txt_balance;
    TextView txt_view_Eur, txt_view_Usd, txt_view_Gbp, txt_view_chf;

    DataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CurrencyRates currencyRates = new CurrencyRates(MainActivity.this);
        currencyRates.updateCurrencyRates();

        txt_balance = findViewById(R.id.textViewBalance);
        linearLayout_ViewAll = findViewById(R.id.ViewAll);
        linearLayout_ViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intern = new Intent(MainActivity.this, DisplayActivity.class);

                intern.putExtra("Go to DisplayActivity", 1);
                startActivity(intern);
            }
        });

        linearLayout_Add  = findViewById(R.id.Add);
        linearLayout_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEditActivity.class);

                intent.putExtra("Go to AddEditActivity", 1);
                startActivity(intent);
            }
        });

        refreshImage = findViewById(R.id.refresh);
        refreshImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<DataModel> dataList = new ArrayList<>();
                dataBaseHelper = new DataBaseHelper(MainActivity.this);

                dataList = dataBaseHelper.getAll();

                Balance balance = new Balance(0);
                balance.calculateCurrentBalance(dataList);
                txt_balance.setText(String.valueOf(plDf.format(balance.getCurrentBalance())) + " PLN");
            }
        });

        txt_view_Eur = findViewById(R.id.EUR);
        txt_view_Usd = findViewById(R.id.USD);
        txt_view_Gbp = findViewById(R.id.GBP);
        txt_view_chf = findViewById(R.id.CHF);

        linearLayout_updateData = findViewById(R.id.CurrencyRate);
        linearLayout_updateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt_view_Eur.setText("EUR: " + plDf.format(currencyRates.getEUR()) + " PLN");
                txt_view_Usd.setText("USD: " + plDf.format(currencyRates.getUSD()) + " PLN");
                txt_view_Gbp.setText("GBP: " + plDf.format(currencyRates.getGBP()) + " PLN");
                txt_view_chf.setText("CHF: " + plDf.format(currencyRates.getCHF()) + " PLN");
            }
        });


    }

}