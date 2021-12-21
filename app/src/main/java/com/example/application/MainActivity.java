package com.example.application;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //PL money format (2 decimals)
    DecimalFormat plDf = new DecimalFormat("#,##0.00");

    LinearLayout linearLayout_viewAll, linearLayout_add, linearLayout_updateData, linearLayout_notifications;
    TextView txt_balance, txt_view_Eur, txt_view_Usd, txt_view_Gbp, txt_view_chf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewsById();

        downloadAndSetCurrencyRates();

        createNotificationChannels();

        setBalance();

        linearLayout_viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intern = new Intent(MainActivity.this, DisplayActivity.class);

                intern.putExtra("Go to DisplayActivity", 1);
                startActivity(intern);
            }
        });

        linearLayout_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);

                intent.putExtra("Go to AddActivity", 1);
                startActivity(intent);
            }
        });

        linearLayout_updateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadAndSetCurrencyRates();
            }
        });

        linearLayout_notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NotificationsActivity.class);

                intent.putExtra("Go to Notifications class", 1);
                startActivity(intent);
            }
        });


    }

    private void findViewsById() {
        txt_view_Eur = findViewById(R.id.EUR);
        txt_view_Usd = findViewById(R.id.USD);
        txt_view_Gbp = findViewById(R.id.GBP);
        txt_view_chf = findViewById(R.id.CHF);
        txt_balance = findViewById(R.id.textViewBalance);
        linearLayout_viewAll = findViewById(R.id.ViewAll);
        linearLayout_add = findViewById(R.id.Add);
        linearLayout_updateData = findViewById(R.id.CurrencyRate);
        linearLayout_notifications = findViewById(R.id.Notifications);
    }

    private void downloadAndSetCurrencyRates() {
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
    }

    @Override
    public void onRestart() {
        super.onRestart();
        setBalance();
    }

    private void createNotificationChannels() {
        NotificationChannel channel1 = new NotificationChannel(
                NotificationBuilder.CHANNEL_1_ID,
                "Channel 1",
                NotificationManager.IMPORTANCE_HIGH
        );
        channel1.setDescription("This is Channel 1");

        NotificationChannel channel2 = new NotificationChannel(
                NotificationBuilder.CHANNEL_2_ID,
                "Channel 2",
                NotificationManager.IMPORTANCE_LOW
        );
        channel2.setDescription("This is Channel 2");

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel1);
        manager.createNotificationChannel(channel2);
    }


    private void createNotificationChanel() {
        String name = "Notification Chanel";
        String description = "A Description of the Chanel";
        int importanceDefault = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(NotificationBuilder.channelID, name, importanceDefault);
        channel.setDescription(description);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);

        Log.i("MainActivity", "Notification channel created");
    }

    private void setBalance() {
        txt_balance.setText(plDf.format(Balance.getBalance(MainActivity.this)) + " PLN");
    }
}