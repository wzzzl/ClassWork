package com.example.classwork;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NavigationView navigationView=(NavigationView)findViewById(R.id.nv01);
        navigationView.setItemIconTintList(null);
        ImageButton leftmenu=(ImageButton)findViewById(R.id.leftmenu);
        leftmenu.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Navigation.openDrawer(Gravity.START);;
            }
        });
    }


}