package com.example.dialogflowchatbot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class CategoryActivity extends AppCompatActivity {


    Button btn_str;
    Button btn_dept;
    Button btn_shuttle;
    Button btn_club;
    Button btn_num;
    Button btn_scholarship;
    Button btn_plan;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        btn_str = findViewById(R.id.btn_str);
        btn_dept = findViewById(R.id.btn_dept);
        btn_plan = findViewById(R.id.btn_plan);
        btn_club = findViewById(R.id.btn_club);
        btn_shuttle = findViewById(R.id.btn_shuttle);
        btn_scholarship = findViewById(R.id.btn_scholorchip);
        btn_num = findViewById(R.id.btn_num);

        String[] options = {"str", "dept", "plan", "shuttle", "scholarship", "club", "num"};

        btn_str.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CategoryActivity.this, MainActivity.class);
                i.putExtra("options", options[0]);
                startActivity(i);
            }
        });

        btn_dept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CategoryActivity.this, MainActivity.class);
                i.putExtra("options", options[1]);
                startActivity(i);
            }
        });

        btn_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CategoryActivity.this, MainActivity.class);
                i.putExtra("options", options[2]);
                startActivity(i);
            }
        });

        btn_shuttle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CategoryActivity.this, MainActivity.class);
                i.putExtra("options", options[3]);
                startActivity(i);
            }
        });

        btn_scholarship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CategoryActivity.this, MainActivity.class);
                i.putExtra("options", options[4]);
                startActivity(i);
            }
        });

        btn_club.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CategoryActivity.this, MainActivity.class);
                i.putExtra("options", options[5]);
                startActivity(i);
            }
        });

        btn_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CategoryActivity.this, MainActivity.class);
                i.putExtra("options", options[6]);
                startActivity(i);
            }
        });
    }


}
