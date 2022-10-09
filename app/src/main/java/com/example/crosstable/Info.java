package com.example.crosstable;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;



public class Info extends AppCompatActivity{
    TextView textView;
    TextView textView1;
    TextView textView2;
    TextView textView3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
        textView =  findViewById(R.id.textView);
        this.textView1 = findViewById(R.id.textView1);
        this.textView2 = findViewById(R.id.textView2);
        this.textView3 = findViewById(R.id.textView3);
        Intent intent = getIntent();
        String vc = intent.getStringExtra("vc");
        String vck = intent.getStringExtra("vck");
        String note = intent.getStringExtra("note");
        String headof = intent.getStringExtra("headof");
        intent.getStringExtra("comp");
        intent.getStringExtra("model");
        intent.getStringExtra("whatis");
        if (note != null) {
            TextView textView4 = this.textView2;
            textView4.setText("Примечание: " + note);
        }
        if (headof == null) headof = "Отсутствует";
        TextView textView5 = this.textView;
        textView5.setText("Артикул KABS: " + vc);
        TextView textView6 = this.textView1;
        textView6.setText("Артикул Karcher: " + vck);
        TextView textView7 = this.textView3;
        textView7.setText("Автор: " + headof);
    }
}