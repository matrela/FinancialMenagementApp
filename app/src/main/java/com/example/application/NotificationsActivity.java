package com.example.application;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import java.text.DecimalFormat;


public class NotificationsActivity extends AppCompatActivity{

    DecimalFormat plDf = new DecimalFormat("#,##0.00");

    SeekBar percentSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        LinearLayout linearLayout_monthlyTransactionLimit = findViewById(R.id.linearLayoutMonthlyTransactionLimit);
        TextView textViewLimit = findViewById(R.id.textViewLimit);
        percentSeekBar = findViewById(R.id.seekBarMonthlyLimit);
        TextView textViewLimitDescription = findViewById(R.id.textViewLimitDescription);

        percentSeekBar.incrementProgressBy(5);
        percentSeekBar.setMax(100);
        percentSeekBar.setProgress(Balance.getPercents(NotificationsActivity.this));

        textViewLimitDescription.setText(LimitDescription(percentSeekBar.getProgress()));


        if(Balance.getMonthlyLimit(NotificationsActivity.this)==-1) {
            Balance.setIsLimitReach(NotificationsActivity.this, false);
            textViewLimit.setText(R.string.off);
            textViewLimitDescription.setText(R.string.set_limit_first);
            percentSeekBar.setEnabled(false);
        }
        else{
            textViewLimit.setText(plDf.format(Balance.getMonthlyLimit(NotificationsActivity.this)) + " PLN");
        }

        percentSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if(!(Balance.getMonthlyLimit(NotificationsActivity.this)==-1)) {
                    progress = progress / 5;
                    progress = progress * 5;
                    textViewLimitDescription.setText(LimitDescription(progress));
                    Balance.setPercents(NotificationsActivity.this, progress);
                    Balance.setIsLimitReach(NotificationsActivity.this, false);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        linearLayout_monthlyTransactionLimit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(NotificationsActivity.this);

                final EditText edittext = new EditText(NotificationsActivity.this);

                alert.setTitle("Monthly limit");
                alert.setMessage("Set new monthly limit");
                edittext.setHint("Limit not set");
                edittext.setInputType(InputType.TYPE_CLASS_NUMBER);

                if (Balance.getMonthlyLimit(NotificationsActivity.this) != -1) {
                    edittext.setText(String.valueOf(Balance.getMonthlyLimit(NotificationsActivity.this)));
                }

                alert.setView(edittext);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Getting entered limit
                        String enteredMonthlyLimit = edittext.getText().toString();

                        if(enteredMonthlyLimit.isEmpty()) {
                            disableLimits();
                        }else {
                            try {
                                int monthlyLimit = Integer.parseInt(enteredMonthlyLimit);

                                if(monthlyLimit == 0){
                                    disableLimits();
                                }else {
                                    setNewLimit(monthlyLimit);
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                Toast.makeText(NotificationsActivity.this, "Wrong limit set", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    private void setNewLimit(int newMonthlyLimit) {
                        Balance.setMonthlyLimit(NotificationsActivity.this, newMonthlyLimit);
                        Balance.setIsLimitReach(NotificationsActivity.this, false);
                        percentSeekBar.setEnabled(true);
                        textViewLimit.setText(plDf.format(newMonthlyLimit) + " PLN");
                        textViewLimitDescription.setText(LimitDescription(percentSeekBar.getProgress()));
                        Toast.makeText(NotificationsActivity.this, "Limit set to: " + plDf.format(newMonthlyLimit) + " PLN", Toast.LENGTH_SHORT).show();
                    }

                    private void disableLimits() {
                        textViewLimit.setText(R.string.off);
                        textViewLimitDescription.setText("Set limit first");
                        percentSeekBar.setEnabled(false);
                        Balance.setMonthlyLimit(NotificationsActivity.this, -1);
                        Balance.setIsLimitReach(NotificationsActivity.this, false);
                        Toast.makeText(NotificationsActivity.this, "Limits disabled", Toast.LENGTH_SHORT).show();
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

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

    }

    private String LimitDescription(int percents) {
        String StrDesc;
        int limit = Balance.getMonthlyLimit(NotificationsActivity.this);
        double percentOfLimit = limit * (percents/100.0);
        StrDesc = "Notify when spending reach " + percents + "% (" + plDf.format(percentOfLimit) + " PLN) of monthly limit";
        return StrDesc;
    }

}