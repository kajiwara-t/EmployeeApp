package com.example.kajiwaratakuya.employeeapp;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;

public class WorkPlaceWho extends AppCompatActivity {

    private EmployeeDb employeeDb;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workplace_who);


        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);
        setDatabase();

        EmployeeDb employeeDb = new EmployeeDb(this);
        SQLiteDatabase db = employeeDb.getReadableDatabase();

        Cursor c = db.query("employeeTable",new String[]{
                "id","name","age","birthPlace","workPlace"},"workPlace = ?",new String[]{"WHO"},null,null,null);
        boolean isEof = c.moveToFirst();
        while (isEof){
            TextView textView = new TextView(this);
            textView.setText(String.format("%d / %s / %d才 / %s / %s",c.getInt(0),
                    c.getString(1),c.getInt(2),
                    c.getString(3),c.getString(4)));
            isEof = c.moveToNext();
            layout.addView(textView);
        }
        c.close();
        db.close();
    }

    private void setDatabase() {
        employeeDb = new EmployeeDb(this);
        try {
            employeeDb.createEmptyDataBase();
            db = employeeDb.getReadableDatabase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        } catch(SQLException sqle){
            throw sqle;
        }
    }
}
