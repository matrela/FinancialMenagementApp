package com.example.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Balance {

    static float balance;
    static int monthlyLimit = -1;
    static int percents = 80;
    static boolean isLimitReach = false;

    private static final String LIMITS_PREFERENCES_NAME = "com.example.application";
    private static final String MONTHLY_LIMIT = "monthlyLimit";
    private static final String PERCENT_LIMIT = "percents";
    public static final String IS_LIMIT_REACH = "isLimitReach";

    public static int getMonthlyLimit(Context context) {
        monthlyLimit = loadLimitsFromPreferences(context, MONTHLY_LIMIT);
        return monthlyLimit;
    }

    public static void setMonthlyLimit(Context context, int newLimit) {
        monthlyLimit = newLimit;
        saveLimitsToPreferences(context, newLimit, MONTHLY_LIMIT);
    }

    public static int getPercents(Context context) {
        percents = loadLimitsFromPreferences(context, PERCENT_LIMIT);
        return percents;
    }

    public static void setPercents(Context context, int newPercent) {
        percents = newPercent;
        saveLimitsToPreferences(context, newPercent, PERCENT_LIMIT);
    }

    public static boolean getIsIsLimitReach(Context context) {
        SharedPreferences limitsPreferences = context.getSharedPreferences(LIMITS_PREFERENCES_NAME, Context.MODE_PRIVATE);
        isLimitReach = limitsPreferences.getBoolean(IS_LIMIT_REACH, true);
        return isLimitReach;
    }

    public static void setIsLimitReach(Context context, boolean isLimitReach) {
        Balance.isLimitReach = isLimitReach;
        SharedPreferences limitsPreferences = context.getSharedPreferences(LIMITS_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = limitsPreferences.edit();
        editor.putBoolean(IS_LIMIT_REACH, isLimitReach);
        editor.apply();
    }

    private static void saveLimitsToPreferences(Context context, int newLimit, String type) {
        SharedPreferences limitsPreferences = context.getSharedPreferences(LIMITS_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = limitsPreferences.edit();
        editor.putInt(type, newLimit);
        editor.apply();
    }

    private static int loadLimitsFromPreferences(Context context, String type){
        SharedPreferences limitsPreferences = context.getSharedPreferences(LIMITS_PREFERENCES_NAME, Context.MODE_PRIVATE);
        return limitsPreferences.getInt(type, 0);
    }

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

    public static Boolean checkIfLimitReach(Context context){
        if(getMonthlyLimit(context)!=-1) {

            Date date = new Date();

            SimpleDateFormat mdf = new SimpleDateFormat("MM");
            String month = mdf.format(date);

            List<DataModel> dataList;
            DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
            dataList = dataBaseHelper.getByMonth(month);

            float sum = 0;
            for (int position = 0; position < dataList.size(); position++){

                if(dataList.get(position).getAmount()<0){
                    sum+= Math.abs(dataList.get(position).getAmount());
                }
                System.out.println(dataList.get(position).toString());
            }

            System.out.println("SUM: " + sum);

            float percentOfLimit = (float) (monthlyLimit * (percents / 100.0));
            System.out.println("percentOfLimit: " + percentOfLimit);

            if (sum>=percentOfLimit && !getIsIsLimitReach(context)){
                Log.i("checkIfLimitReach", "Limit: " + percentOfLimit + " reach");
                setIsLimitReach(context, true);
                return true;
            }else {
                setIsLimitReach(context, false);
            }
        }else{
            Log.i("checkIfLimitReach","Limit not set");
        }

        return false;
    }

}

