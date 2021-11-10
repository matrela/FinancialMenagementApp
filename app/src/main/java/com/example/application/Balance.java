package com.example.application;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class Balance {
    float currentBalance;

    public Balance(float currentBalance) {
        this.currentBalance = currentBalance;
    }

    public float getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(float currentBalance) {
        this.currentBalance = currentBalance;
    }

    public void calculateCurrentBalance (List<DataModel> dataList){

        currentBalance = 0;

        for (int position = 0; position < dataList.size(); position++){
            currentBalance += dataList.get(position).getAmount();
        }

    }
}

