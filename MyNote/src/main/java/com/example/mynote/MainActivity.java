package com.example.mynote;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    public DATA mydata;//定义数据库
    private ListView lv_notebook;//首页展示便签列表

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mydata = new DATA(this);//实例化数据库对象
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);//获取浮动按钮
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, com.example.mynote.AddActivity.class);
                startActivity(intent);//启动新建Activity
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
//        navigationView.setItemIconTintList(null);//菜单项显示原本颜色
    }
    @Override
    protected void onStart(){
        super.onStart();
        inital();
    }
    private void inital(){
        lv_notebook = findViewById(R.id.am_note);
        //引用具体控件
        //创建Source类型的list保存数据库中的数据
        List<Source> sourceList = new ArrayList<>();
        //获得可读SQLiteDatabase对象
        SQLiteDatabase db = mydata.getReadableDatabase();

        //查询数据库中的数据
        Cursor cursor = db.query(DATA.TABLE,null,null,
                null,null,null,null);
        if(cursor.moveToFirst()){
            Source source;
            while (!cursor.isAfterLast()){
                //实例化source对象
                source = new Source();

                //把数据库中的一个表中的数据赋值给source
                source.setId(
                        Integer.valueOf(cursor.getString(cursor.getColumnIndex(DATA.ID))));
                source.setTime(
                        cursor.getString(cursor.getColumnIndex(DATA.TIME)));
                source.setTitle(
                        cursor.getString(cursor.getColumnIndex(DATA.TITLE)));
                source.setContent(
                        cursor.getString(cursor.getColumnIndex(DATA.CONTENT)));

                //将source对象存入list对象数组中
                sourceList.add(source);
                cursor.moveToNext();
            }
        }
        cursor.close();
        db.close();

        //创建一个adapter的实例
        final MyBaseAdapter myBaseAdapter = new MyBaseAdapter(sourceList,this,R.layout.note_item);
        lv_notebook.setAdapter(myBaseAdapter);
        int temp=lv_notebook.getChildCount();
        int tmp=lv_notebook.getTop();

        //点击对应笔记
        lv_notebook.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,ShowActivity.class);
                Source source = (Source) lv_notebook.getItemAtPosition(position);
                intent.putExtra(DATA.TITLE,source.getTitle().trim());
                intent.putExtra(DATA.TIME,source.getTime().trim());
                intent.putExtra(DATA.ID,source.getId().toString().trim());
                intent.putExtra(DATA.CONTENT,source.getContent().trim());
                startActivity(intent);
            }
        });


        //长按删除事件
        lv_notebook.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final Source source = (Source) lv_notebook.getItemAtPosition(position);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("提示框")
                        .setMessage("是否删除?")
                        .setPositiveButton("yes",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SQLiteDatabase db = mydata.getWritableDatabase();
                                db.delete(DATA.TABLE,DATA.ID+"=?",new String[]{String.valueOf(source.getId())});
                                db.close();
                                myBaseAdapter.removeItem(position);
                                lv_notebook.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        myBaseAdapter.notifyDataSetChanged();
                                    }
                                });
                                //MainActivity.this.onResume();
                            }
                        })
                        .setNegativeButton("no",null).show();
                return true;
            }
        });
    }


    //创建继承自BaseAdapter的实现类进行ListView的展示
    public class MyBaseAdapter extends BaseAdapter {

        private List<Source> sourceList;
        private int layoutId;
        private Context context;

        public MyBaseAdapter(List<Source> sourceList, Context context, int layoutId) {
            this.sourceList= sourceList;
            this.context = context;
            this.layoutId = layoutId;
        }

        @Override
        //获取数据总数量
        public int getCount() {
            if (sourceList != null && sourceList.size() > 0)
                return sourceList.size();
            else
                return 0;
        }

        @Override
        //获取指定数据的位置
        public Object getItem(int position) {
            if (sourceList != null && sourceList.size() > 0)
                return sourceList.get(position);
            else
                return null;
        }

        @Override
        //获取当前数据的ID
        public long getItemId(int position) {
            return position;
        }

        //ListView加载数据
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(
                        getApplicationContext()).inflate(R.layout.note_item, parent,
                        false);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.note_title);
                viewHolder.content = convertView.findViewById(R.id.note_content);
                viewHolder.time = (TextView) convertView.findViewById(R.id.note_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            String title = sourceList.get(position).getTitle();
            String content = sourceList.get(position).getContent();
            viewHolder.title.setText(title);
            viewHolder.content.setText(content);
            viewHolder.time.setText(sourceList.get(position).getTime());
            return convertView;
        }
        public void removeItem(int position){
            this.sourceList.remove(position);
        }
        class ViewHolder{
            TextView title;
            TextView content;
            TextView time;
        }

    }
}
