package com.example.mynote;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.mynote.R;



public class AddActivity extends AppCompatActivity {
    DATA myDb;
    private Button btnCancel;
    private Button btnSave;
    private EditText titleEditText;
    private EditText contentEditText;
    private TextView timeTextView;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
//    定义一个数据库，两个按钮，和三个编辑框

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        init();
        if(timeTextView.getText().length()==0)
            timeTextView.setText(getTime());
    }

    private void init(){

        myDb = new DATA(this);
        SQLiteDatabase db = myDb.getReadableDatabase();
        titleEditText = findViewById(R.id.et_title);
        contentEditText = findViewById(R.id.et_content);
        timeTextView = findViewById(R.id.et_time);
        radioGroup=findViewById(R.id.et_rGroup);
        radioButton=findViewById(radioGroup.getCheckedRadioButtonId());
        btnCancel = findViewById(R.id.et_cancel);
        btnSave = findViewById(R.id.et_save);

        //按钮点击事件
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SQLiteDatabase db = myDb.getWritableDatabase();
                ContentValues values = new ContentValues();

                String title= titleEditText.getText().toString();
                String content=contentEditText.getText().toString();
                String time= timeTextView.getText().toString();
 //               String type=radioButton.getText().toString();

                if("".equals(titleEditText.getText().toString())){
                    Toast.makeText(AddActivity.this,"标题不能为空",Toast.LENGTH_LONG).show();
                    return;
                }
                if("".equals(contentEditText.getText().toString())) {
                    Toast.makeText(AddActivity.this,"内容不能为空",Toast.LENGTH_LONG).show();
                    return;
                }
                values.put(DATA.TITLE,title);
                values.put(DATA.CONTENT,content);
                values.put(DATA.TIME,time);
 //               values.put(DATA.TYPE,type);
                db.insert(DATA.TABLE,null,values);
                Toast.makeText(AddActivity.this,"保存成功",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AddActivity.this,MainActivity.class);
                startActivity(intent);
                db.close();
            }
        });
    }

    //获取当前时间
    private String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String str = sdf.format(date);
        return str;
    }
}