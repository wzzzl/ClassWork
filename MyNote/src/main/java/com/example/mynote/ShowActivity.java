package com.example.mynote;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.mynote.R;

public class ShowActivity extends AppCompatActivity {

    private Button btnSave;
    private Button btnCancel;
    private TextView showTime;
    private EditText showContent;
    private EditText showTitle;

    private Source source;
    DATA mdata;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        init();
    }

    public void init() {
        mdata = new DATA(this);
        btnCancel = findViewById(R.id.show_cancel);
        btnSave = findViewById(R.id.show_save);
        showTime = findViewById(R.id.show_time);
        showTitle = findViewById(R.id.show_title);
        showContent = findViewById(R.id.show_content);

        Intent intent = this.getIntent();
        if (intent != null) {
            source = new Source();

            source.setTime(intent.getStringExtra(DATA.TIME));
            source.setTitle(intent.getStringExtra(DATA.TITLE));
            source.setContent(intent.getStringExtra(DATA.CONTENT));
            source.setId(Integer.valueOf(intent.getStringExtra(DATA.ID)));

            showTime.setText(source.getTime());
            showTitle.setText(source.getTitle());
            showContent.setText(source.getContent());
        }

        //按钮点击事件
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = mdata.getWritableDatabase();
                ContentValues values = new ContentValues();
                String content = showContent.getText().toString();
                String title = showTitle.getText().toString();

                values.put(DATA.TIME, getTime());
                values.put(DATA.TITLE,title);
                values.put(DATA.CONTENT,content);

                db.update(DATA.TABLE,values,DATA.ID+"=?",new String[]{source.getId().toString()});
                Toast.makeText(ShowActivity.this,"修改成功",Toast.LENGTH_LONG).show();
                db.close();
                Intent intent = new Intent(ShowActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String content = showContent.getText().toString();
                final String title = showTitle.getText().toString();
                new AlertDialog.Builder(ShowActivity.this)
                        .setTitle("提示框")
                        .setMessage("是否保存当前内容?")
                        .setPositiveButton("yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SQLiteDatabase db = mdata.getWritableDatabase();
                                        ContentValues values = new ContentValues();
                                        values.put(DATA.TIME, getTime());
                                        values.put(DATA.TITLE,title);
                                        values.put(DATA.CONTENT,content);
                                        db.update(DATA.TABLE,values,DATA.ID+"=?",new String[]{source.getId().toString()});
                                        Toast.makeText(ShowActivity.this,"修改成功",Toast.LENGTH_LONG).show();
                                        db.close();
                                        Intent intent = new Intent(ShowActivity.this,MainActivity.class);
                                        startActivity(intent);
                                    }
                                })
                        .setNegativeButton("no",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(ShowActivity.this,MainActivity.class);
                                        startActivity(intent);
                                    }
                                }).show();
            }
        });
    }

    String getTime() {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }
}