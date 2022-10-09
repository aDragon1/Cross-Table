package com.example.crosstable;

import static com.example.crosstable.DatabaseHelper.COLUMN_COMP;
import static com.example.crosstable.DatabaseHelper.COLUMN_WHATIS;
import static com.example.crosstable.DatabaseHelper.TABLE;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;


public class Detail extends AppCompatActivity{


    ListView ListView;
    EditText EditText;
    TextView TextView;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;
    Cursor c;
    SimpleCursorAdapter CAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        ListView = findViewById(R.id.List);
        EditText = findViewById(R.id.EditText);
        TextView = findViewById(R.id.textView);
        databaseHelper = new DatabaseHelper(getApplicationContext());
        databaseHelper.create_db();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            String comp = getIntent().getStringExtra("comp");
            String model = getIntent().getStringExtra("model");
            String detail = getIntent().getStringExtra("detail");
                if ((detail == null) || (detail.toLowerCase(Locale.ROOT).contains("нет")))
                    TextView.setText("Деталировки отсутствуют");
                else TextView.setText(Html.fromHtml("<a href=\"" + detail + "\">Деталировки</a> "));
            TextView.setMovementMethod(LinkMovementMethod.getInstance());

            db = databaseHelper.open();
            c = db.rawQuery("select * from " + TABLE + " where comp='"+comp+"' and model='"+model+"' group by whatis", null);
            String[] headers = new String[]{COLUMN_WHATIS};
            CAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1,
                    c, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);

            if (!EditText.getText().toString().isEmpty())
                CAdapter.getFilter().filter(EditText.getText().toString());

            ListView.setOnItemClickListener((parent, view, position, id) -> {
                    Intent intent = new Intent( Detail.this, Info.class);
                    Cursor t = db.rawQuery("SELECT * FROM kerInfo where _id='"+id+"'", null);
                    t.moveToFirst();
                    intent.putExtra("comp", comp);
                    intent.putExtra("model", model);
                    intent.putExtra("vc", t.getString(t.getColumnIndex("vc")));
                    intent.putExtra("vck", t.getString(t.getColumnIndex("vck")));
                    intent.putExtra("note", t.getString(t.getColumnIndex("note")));
                    intent.putExtra("headof", t.getString(t.getColumnIndex("headof")));
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

                    return db.rawQuery("select  * from " + TABLE + " group by " + COLUMN_WHATIS, null);
                } else {
                    return db.rawQuery("select * from " + TABLE + " where " +
                            COLUMN_COMP + " like ? group by " + COLUMN_WHATIS, new String[]{"%" + constraint + "%"});
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
