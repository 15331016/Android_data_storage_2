package com.example.yc.lab10;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    private Button add;
    private ListView listView;
    private SimpleAdapter simpleAdapter;
    private List<Map<String, String>> data;
    private myDataBase db;
    private SQLiteDatabase sqLiteDatabase;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findView();
        init();//对列表里应该呈现的数据信息进行更新
        myClick();
    }

    void findView() {
        add = (Button) findViewById(R.id.add);
        listView = (ListView) findViewById(R.id.listview);
    }

    void init() {
        db = new myDataBase(getBaseContext());
        sqLiteDatabase = db.getWritableDatabase();
        cursor = sqLiteDatabase.rawQuery("select * from Info", null);
        data = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name_ = cursor.getString(1);
                String birth_ = cursor.getString(2);
                String gift_ = cursor.getString(3);
                Map<String, String> map = new HashMap<>();
                map.put("name", name_);
                map.put("birth", birth_);
                map.put("gift", gift_);
                data.add(map);
            }
            simpleAdapter = new SimpleAdapter(MainActivity.this, data, R.layout.item,
                    new String[]{"name", "birth", "gift"}, new int[]{R.id.Name, R.id.Birth, R.id.Gift});
            listView.setAdapter(simpleAdapter);
        }
    }

    void myClick() {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Additem.class);
                startActivityForResult(intent, 1);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                showDialog(MainActivity.this, data.get(arg2));//弹出信息dialog
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    final int arg = arg2;
                    HashMap<String, String> map = (HashMap<String, String>) arg0.getItemAtPosition(arg2);
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setMessage("是否删除？")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    db = new myDataBase(getBaseContext());
                                    sqLiteDatabase = db.getWritableDatabase();
                                    sqLiteDatabase.execSQL("DELETE FROM Info WHERE name  = ?", new String[]{data.get(arg).get("name")});
                                    data.remove(arg);
                                    simpleAdapter.notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(MainActivity.this, "不删除联系人", Toast.LENGTH_SHORT).show();
                                }
                            }).create();
                    alertDialog.show();
                    return true;
                }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intentData) {
        if (requestCode == 1) {
            if (resultCode == 1) {
                init();
            }
        }
    }

    //创建一个自定义的dialog，里面包含信息
    public void showDialog(Context context , Map<String, String> map) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_style, null);
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(view);

        final TextView Name = (TextView) view.findViewById(R.id.Name_text);
        final EditText Birth = (EditText) view.findViewById(R.id.Birth_text);
        final EditText Gift = (EditText) view.findViewById(R.id.Gift_text);
        TextView telephone = (TextView) view.findViewById(R.id.telephone);
        Button abandon = (Button) view.findViewById(R.id.add_abandon);
        Button confirm = (Button) view.findViewById(R.id.add_confirm);

        Name.setText(map.get("name"));
        Birth.setText(map.get("birth"));
        Gift.setText(map.get("gift"));

        //读取联系人电话
        String find_tel = "";
        cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String str = cursor.getString(cursor.getColumnIndex("_id"));
            if (cursor.getString(cursor.getColumnIndex("display_name")).equals(Name.getText().toString())) {
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex("has_phone_number"))) > 0) {
                    Cursor tcursor = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, "contact_id = " + str, null, null);
                    while (tcursor.moveToNext()) {
                        find_tel = find_tel + tcursor.getString(tcursor.getColumnIndex("data1"));
                    }
                    tcursor.close();
                }
            }
        }
        cursor.close();
        if(find_tel.equals(""))
            find_tel = "无";
        telephone.setText(find_tel);


        abandon.setOnClickListener(new View.OnClickListener() {//点击“放弃修改”button
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {//点击“保存修改”button
            public void onClick(View view) {
                String old_name = Name.getText().toString();
                String new_birth = Birth.getText().toString();
                String new_gift = Gift.getText().toString();
                db = new myDataBase(getBaseContext());
                sqLiteDatabase = db.getWritableDatabase();
                sqLiteDatabase.execSQL("update Info set birth = ? where name = ?", new Object[]{new_birth, old_name});
                sqLiteDatabase.execSQL("update Info set gift = ? where name = ?", new Object[]{new_gift, old_name});
                sqLiteDatabase.close();
                init();
                dialog.dismiss();
            }
        });
        
        dialog.show();
        //设置dialog的大小
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = 920;
        lp.height = 820;
        dialog.getWindow().setAttributes(lp);
    }
}
