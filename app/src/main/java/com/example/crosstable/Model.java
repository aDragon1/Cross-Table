package com.example.crosstable;


import static com.example.crosstable.DatabaseHelper.COLUMN_MMODEL;
import static com.example.crosstable.DatabaseHelper.TABLE;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

public class Model extends AppCompatActivity {


    ListView ListView;
    EditText EditText;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor c;
    SimpleCursorAdapter CAdapter;

    @Override
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
            String comp = getIntent().getStringExtra("comp");
            db = databaseHelper.open();
            c = db.rawQuery("select * from " + TABLE + " where comp='"+comp+"' group by " + COLUMN_MMODEL, null);
            String[] headers = new String[]{COLUMN_MMODEL};
            CAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,
                    c, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);

            if (!EditText.getText().toString().isEmpty())
                CAdapter.getFilter().filter(EditText.getText().toString());

            ListView.setOnItemClickListener((parent, view, position, id) -> {
                Intent intent = new Intent(Model.this, Detail.class);
                Cursor t = db.rawQuery("SELECT model,detail FROM kerInfo  where _id='"+id+"'", null);
                t.moveToFirst();
                String detail = t.getString(t.getColumnIndexOrThrow("detail"));
                String model = t.getString(t.getColumnIndexOrThrow("model"));
                intent.putExtra("model",model);
                intent.putExtra("comp",comp);
                intent.putExtra("detail",detail);
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

                    return db.rawQuery("select  * from " + TABLE + " where comp='"+comp+"' group by " + COLUMN_MMODEL, null);
                } else {
                    return db.rawQuery("select  * from " + TABLE + " where comp='"+comp+"' " +
                            "like ? ", new String[]{"%" + constraint + "%"});
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