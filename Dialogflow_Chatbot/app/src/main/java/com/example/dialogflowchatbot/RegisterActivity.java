package com.example.dialogflowchatbot;

import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class RegisterActivity extends AppCompatActivity {

    // 로그에 사용할 TAG 변수 선언
    final private String TAG = getClass().getSimpleName();
    private DatabaseReference mPostReference;

    // 사용할 컴포넌트 선언
    EditText title_et, content_et, username;
    Button reg_button;

    // 유저아이디 변수
    String userid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("글 등록하기");
        // ListActivity 에서 넘긴 userid 를 변수로 받음
        userid = getIntent().getStringExtra("userid");

        // 컴포넌트 초기화
        title_et = findViewById(R.id.title_et);
        content_et = findViewById(R.id.content_et);
        reg_button = findViewById(R.id.reg_button);
        username = findViewById(R.id.username);

        // 버튼 이벤트 추가
        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 게시물 등록 함수

                postFirebaseDatabase(true);
                Intent i = new Intent(RegisterActivity.this, BoardActivity.class);
                i.putExtra("userid", username.getText().toString());
                startActivity(i);
            }
        });

    }

    public void postFirebaseDatabase(boolean add){
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            FirebasePost post = new FirebasePost(username.getText().toString(), title_et.getText().toString(), content_et.getText().toString());
            postValues = post.toMap();
        }
        childUpdates.put("/id_list/" + username.getText().toString() ,postValues);
        mPostReference.updateChildren(childUpdates);
    }

}