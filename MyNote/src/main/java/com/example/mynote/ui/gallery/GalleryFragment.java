package com.example.mynote.ui.gallery;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mynote.DATA;
import com.example.mynote.MainActivity;
import com.example.mynote.R;
import com.example.mynote.ShowActivity;
import com.example.mynote.Source;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    DATA mydata;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        final TextView textView = root.findViewById(R.id.text_gallery);
        galleryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        final ListView listView;
        mydata=new DATA(getActivity());
        View galleryLayout=inflater.inflate(R.layout.fragment_gallery,container,false);
        listView=galleryLayout.findViewById(R.id.list_work);
        List<Source> sourceList=new ArrayList<>();
        //获得可读SQLiteDatabase对象
        SQLiteDatabase db;
        db = mydata.getReadableDatabase();

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
        listView.setAdapter(myBaseAdapter);
        //点击对应笔记
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ShowActivity.class);
                Source source = (Source) listView.getItemAtPosition(position);
                intent.putExtra(DATA.TITLE,source.getTitle().trim());
                intent.putExtra(DATA.TIME,source.getTime().trim());
                intent.putExtra(DATA.ID,source.getId().toString().trim());
                intent.putExtra(DATA.CONTENT,source.getContent().trim());
                startActivity(intent);
            }
        });


        //长按删除事件
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final Source source = (Source) listView.getItemAtPosition(position);
                new AlertDialog.Builder(getActivity())
                        .setTitle("提示框")
                        .setMessage("是否删除?")
                        .setPositiveButton("yes",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SQLiteDatabase db = mydata.getWritableDatabase();
                                db.delete(DATA.TABLE,DATA.ID+"=?",new String[]{String.valueOf(source.getId())});
                                db.close();
                                myBaseAdapter.removeItem(position);
                                listView.post(new Runnable() {
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
        return root;
    }




    //创建继承自BaseAdapter的实现类进行ListView的展示
    class MyBaseAdapter extends BaseAdapter {

        private List<Source> sourceList;
        private int layoutId;
        private GalleryFragment context;

        public MyBaseAdapter(List<Source> sourceList, GalleryFragment context, int layoutId) {
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
                MyBaseAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(
                        getActivity().getApplicationContext()).inflate(R.layout.note_item, parent,
                        false);
                viewHolder = new MyBaseAdapter.ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.note_title);
                viewHolder.content = convertView.findViewById(R.id.note_content);
                viewHolder.time = (TextView) convertView.findViewById(R.id.note_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (MyBaseAdapter.ViewHolder) convertView.getTag();
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
