package com.example.application;

import android.content.Context;

import java.util.List;

public class Balance {
    static float balance;

    public static float getBalance(Context context) {
        calculateCurrentBalance (context);
        return balance;
    }

    private static void calculateCurrentBalance (Context context){

        List<DataModel> dataList;
        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        dataList = dataBaseHelper.getAll();

        balance = 0;

        for (int position = 0; position < dataList.size(); position++){
            balance += dataList.get(position).getAmount();
        }
    }

}

