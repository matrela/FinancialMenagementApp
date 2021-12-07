package com.example.application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String FINANCE_TABLE = "FINANCE_TABLE";
    public static final String CATEGORY_TABLE = "CATEGORY_TABLE";

    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_AMOUNT = "AMOUNT";
    public static final String COLUMN_CATEGORY = "CATEGORY";
    public static final String COLUMN_DATE = "DATE";
    public static final String COLUMN_IMAGE = "IMAGE";

    public DataBaseHelper(@Nullable Context context) {
        super(context, "finances.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + FINANCE_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + COLUMN_NAME + " TEXT, " + COLUMN_AMOUNT + " REAL, " + COLUMN_CATEGORY + " TEXT, " + COLUMN_DATE + " TEXT, " + COLUMN_IMAGE + " BLOB)";

        String createCategoryTableStatement = "CREATE TABLE " + CATEGORY_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT)";

        db.execSQL(createTableStatement);
        db.execSQL(createCategoryTableStatement);

        //Adding basic categories
        db.execSQL("INSERT INTO " + CATEGORY_TABLE + " (" + COLUMN_NAME + " ) VALUES ('Bills')");
        db.execSQL("INSERT INTO " + CATEGORY_TABLE + " (" + COLUMN_NAME + " ) VALUES ('Food')");
        db.execSQL("INSERT INTO " + CATEGORY_TABLE + " (" + COLUMN_NAME + " ) VALUES ('Services')");
        db.execSQL("INSERT INTO " + CATEGORY_TABLE + " (" + COLUMN_NAME + " ) VALUES ('Entertainment')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public boolean addNewCategory(String categoryName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, categoryName);

        long insert = db.insert(CATEGORY_TABLE, null, cv);
        if (insert == -1){
            return false;
        }
        else {
            return true;
        }
    }

    public boolean addOne(DataModel dataModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, dataModel.getName());
        cv.put(COLUMN_AMOUNT, dataModel.getAmount());
        cv.put(COLUMN_CATEGORY, dataModel.getCategory());
        cv.put(COLUMN_DATE, dataModel.getDate());
        cv.put(COLUMN_IMAGE, dataModel.getImage());

        long insert = db.insert(FINANCE_TABLE, null, cv);
        if (insert == -1){
            return false;
        }
        else {
            return true;
        }
    }

    public boolean deleteOne(DataModel dataModel) {

        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + FINANCE_TABLE + " WHERE " + COLUMN_ID + " = " + dataModel.getId();

        try (Cursor cursor = db.rawQuery(queryString, null)) {

            if (cursor.moveToFirst()) {
                return true;
            } else {
                return false;
            }
        }
    }

    public List<String> getAllCategories(){
        List<String> categoriesList = new ArrayList<>();

        String queryString = "SELECT * FROM " + CATEGORY_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor != null){

            while(cursor.moveToNext()) {
                String name = cursor.getString(1);
                categoriesList.add(name);
            }
        }

        // close both the cursor and the db when done.
        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return categoriesList;
    }

    public List<DataModel> getAll() {

        List<DataModel> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM " + FINANCE_TABLE;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst()) {
            // loop through the cursor (result set) and create new data objects.
            // Put them into the return list
            do {
                int ID = cursor.getInt(0);
                String name = cursor.getString(1);
                float amount = cursor.getFloat(2);
                String category = cursor.getString(3);
                String date = cursor.getString(4);
                byte[] image = cursor.getBlob(5);

                DataModel newData = new DataModel(ID, name, amount, category, date, image);
                returnList.add(newData);

            }while (cursor.moveToNext());
        }
        else {
            //failure. do not add anything to the list.
        }

        // close both the cursor and the db when done.
        cursor.close();
        db.close();

        return returnList;
    }

    public DataModel getById(int id){

        String queryString = "SELECT * FROM " + FINANCE_TABLE + " WHERE " + COLUMN_ID + " = '" + id + "'";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(queryString, null);

        DataModel newDataModel = null;
        
        if(cursor != null){

            while(cursor.moveToNext()) {
                int ID = cursor.getInt(0);
                String name = cursor.getString(1);
                float amount = cursor.getFloat(2);
                String category = cursor.getString(3);
                String date = cursor.getString(4);
                byte[] image = cursor.getBlob(5);

                newDataModel = new DataModel(ID, name, amount, category, date, image);
            }
        }

        // close both the cursor and the db when done.
        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return newDataModel;
    }
}
