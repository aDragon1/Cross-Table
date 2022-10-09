package com.example.crosstable;


import static com.example.crosstable.DatabaseHelper.COLUMN_COMP;
import static com.example.crosstable.DatabaseHelper.TABLE;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    android.widget.ListView ListView;
    android.widget.EditText EditText;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor c;
    SimpleCursorAdapter CAdapter;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView = findViewById(R.id.List);
        EditText = findViewById(R.id.EditText);
        databaseHelper = new DatabaseHelper(getApplicationContext());
        databaseHelper.create_db();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            db = databaseHelper.open();
            c = db.rawQuery("select * from " + TABLE + " group by " + COLUMN_COMP, null);
            String[] headers = new String[]{COLUMN_COMP};
            CAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,
                    c, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);

            if (!EditText.getText().toString().isEmpty())
                CAdapter.getFilter().filter(EditText.getText().toString());

            ListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, Model.class);
            Cursor t = db.rawQuery("SELECT comp FROM kerInfo  where _id='"+id+"'", null);
            t.moveToFirst();
            String comp = t.getString(t.getColumnIndexOrThrow("comp"));
            intent.putExtra("comp",comp);
            startActivity(intent);
                t.close();
            });

            EditText.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                }
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    CAdapter.getFilter().filter(s.toString());
                }
            });

            CAdapter.setFilterQueryProvider(constraint -> {

                if (constraint == null || constraint.length() == 0) {

                    return db.rawQuery("select  * from " + TABLE + " group by " + COLUMN_COMP, null);
                } else {
                    return db.rawQuery("select * from " + TABLE + " where " +
                            COLUMN_COMP + " like ? " + " group by " + COLUMN_COMP, new String[]{"%" + constraint + "%"});
                }
            });
            ListView.setAdapter(CAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Закрываем подключение и курсор
        db.close();
        c.close();
    }

}