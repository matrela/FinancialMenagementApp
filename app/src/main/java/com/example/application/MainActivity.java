package com.example.application;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    //PL money format (2 decimals)
    DecimalFormat plDf = new DecimalFormat("#,##0.00");

    LinearLayout linearLayout_ViewAll, linearLayout_Add, linearLayout_updateData, linearLayout_notifications;
    TextView txt_balance, txt_view_Eur, txt_view_Usd, txt_view_Gbp, txt_view_chf;

    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_view_Eur = findViewById(R.id.EUR);
        txt_view_Usd = findViewById(R.id.USD);
        txt_view_Gbp = findViewById(R.id.GBP);
        txt_view_chf = findViewById(R.id.CHF);

        CurrencyRates.updateCurrencyRates(MainActivity.this, new ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        txt_view_Eur.setText("EUR: " + plDf.format(CurrencyRates.getEUR()) + " PLN");
                        txt_view_Usd.setText("USD: " + plDf.format(CurrencyRates.getUSD()) + " PLN");
                        txt_view_Gbp.setText("GBP: " + plDf.format(CurrencyRates.getGBP()) + " PLN");
                        txt_view_chf.setText("CHF: " + plDf.format(CurrencyRates.getCHF()) + " PLN");
                    }
                }
        );

        createNotificationChannels();

        txt_balance = findViewById(R.id.textViewBalance);
        txt_balance.setText(getStrBalance());

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

        linearLayout_updateData = findViewById(R.id.CurrencyRate);
        linearLayout_updateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt_view_Eur.setText("EUR: " + plDf.format(CurrencyRates.getEUR()) + " PLN");
                txt_view_Usd.setText("USD: " + plDf.format(CurrencyRates.getUSD()) + " PLN");
                txt_view_Gbp.setText("GBP: " + plDf.format(CurrencyRates.getGBP()) + " PLN");
                txt_view_chf.setText("CHF: " + plDf.format(CurrencyRates.getCHF()) + " PLN");
            }
        });

        linearLayout_notifications = findViewById(R.id.Notifications);
        linearLayout_notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Notifications.class);

                intent.putExtra("Go to Notifications class", 1);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onRestart() {
        super.onRestart();
        txt_balance.setText(getStrBalance());
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_1_ID, "Channel 1", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("This is Channel 1");

            NotificationChannel channel2 = new NotificationChannel(CHANNEL_1_ID, "Channel 2", NotificationManager.IMPORTANCE_LOW);
            channel2.setDescription("This is Channel 2");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }
    }

    private String getStrBalance(){
        return plDf.format(Balance.getBalance(MainActivity.this)) + " PLN";
    }
}