package com.example.yc.lab10;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by yc on 2017/12/4.
 */

public class Additem extends AppCompatActivity {
    private Button add_confirm;
    private EditText name_Text;
    private EditText birth_Text;
    private EditText gift_Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additem);

        findView();
        myClick();
    }

    void findView() {
        add_confirm = (Button) findViewById(R.id.add_confirm);
        name_Text = (EditText) findViewById(R.id.name_text);
        birth_Text = (EditText) findViewById(R.id.birth_text);
        gift_Text = (EditText) findViewById(R.id.gift_text);
    }

    void myClick() {
        add_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDataBase db = new myDataBase(getBaseContext());
                SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery("select * from Info where name like ?", new String[]{name_Text.getText().toString()});

                if (name_Text.getText().toString().equals("")) {
                    Toast.makeText(Additem.this, "名字为空，请完善", Toast.LENGTH_SHORT).show();
                } else if (cursor.moveToFirst()) {
                    Toast.makeText(Additem.this, "名字重复啦，请检查", Toast.LENGTH_SHORT).show();
                } else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("name", name_Text.getText().toString());
                    contentValues.put("birth", birth_Text.getText().toString());
                    contentValues.put("gift", gift_Text.getText().toString());
                    sqLiteDatabase.insert("Info", null, contentValues);
                    sqLiteDatabase.close();
                    setResult(1, new Intent());
                    finish();
                }
            }
        });
    }
}
