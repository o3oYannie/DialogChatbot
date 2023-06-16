package com.example.dialogflowchatbot;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.protobuf.StringValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class DetailActivity extends AppCompatActivity {

    // 로그에 사용할 TAG
    final private String TAG = getClass().getSimpleName();

    private DatabaseReference mPostReference;

    // 사용할 컴포넌트 선언
    ListView listView;


    static ArrayList<String> arrayIndex =  new ArrayList<String>();
    static ArrayList<String> arrayData = new ArrayList<String>();

    ArrayList<String> uqdateList = new ArrayList<String>();
    ArrayList<String> tmpList = new ArrayList<String>();


    ArrayList<String> commentList = new ArrayList<>();

    ArrayAdapter<String> arrayAdapter;



    // 사용할 컴포넌트 선언
    TextView title_tv, content_tv, username_tv;
    LinearLayout comment_layout;
    EditText comment_et;
    Button reg_button;


    // 유저 변수
    String userid = "";
    String title = "";
    String content = "";
    String [] key ;
    String k = "";

    int seq=0;
    int comment_seq;
    int comment_temp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("게시글");

        // BoardActivity 에서 넘긴 변수들을 받아줌
        userid = getIntent().getStringExtra("userid");
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");

        // 컴포넌트 초기화
        title_tv = findViewById(R.id.title_tv);
        content_tv = findViewById(R.id.content_tv);
        username_tv = findViewById(R.id.username);

        listView = findViewById(R.id.listView);
        comment_et = findViewById(R.id.comment_et);
        reg_button = findViewById(R.id.reg_button);


        username_tv.setText(userid);
        title_tv.setText(title);
        content_tv.setText(content);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        getFirebaseDatabase();

        // 등록하기 버튼을 눌렀을 때 댓글 등록 함수 호출
        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getFirebaseDatabase();
                postFirebaseDatabase(true);
                getFirebaseDatabase();
            }
        });

        // listView 를 클릭했을 때 이벤트 추가
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
                builder.setTitle("댓글 삭제").setMessage("삭제하시겠습니까?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                      int count = arrayAdapter.getCount();

                        if (count > 0) {
                            // 현재 선택된 아이템의 position 획득.
                            int checked = i;
                            if (checked > -1 && checked < count) {
                                // 아이템 삭제
                                removeFirebaseDatabase(checked);
                                //Log.d("getFirebaseDatabase", "~~~~~~~~~~~~~~~~~~~~~~~ " + i);
                                Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();

                            }
                            else{
                                Toast.makeText(DetailActivity.this, "삭제할 댓글이 없습니다", Toast.LENGTH_SHORT).show();
                            }
                        }


                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Toast.makeText(getApplicationContext(), "취소되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();


            }
        });

    }


    // onResume() 은 해당 액티비티가 화면에 나타날 때 호출됨
    @Override
    protected void onResume() {
        super.onResume();
        // 해당 액티비티가 활성화 될 때, 게시물 리스트를 불러오는 함수를 호출
            //Toast.makeText(this, commentList+"안 비었다", Toast.LENGTH_SHORT).show();
            getFirebaseDatabase();

    }


    // comment 추가
    public void postFirebaseDatabase(boolean add){


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebasePost get = dataSnapshot.getValue(FirebasePost.class);
                if(userid.equals(username_tv))
                    comment_temp=get.comment;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("getFirebaseDatabase","loadPost:onCancelled", databaseError.toException());
            }
        };






        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;

        childUpdates.put("/id_list/" + userid + "/comment"  ,  comment_temp);

        mPostReference.child("/id_list/"+userid+"/comment"+ comment_temp++ + "/" ).setValue(comment_et.getText().toString());
        mPostReference.updateChildren(childUpdates);
        comment_et.setText("");
    }

    //comment 읽어오기
    public void getFirebaseDatabase(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("getFirebaseDatabase", "key: " + dataSnapshot.getChildrenCount());
                arrayData.clear();
                arrayIndex.clear();
                commentList.clear();
                arrayAdapter.clear();
                listView.setAdapter(arrayAdapter);

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    FirebasePost get = postSnapshot.getValue(FirebasePost.class);

                    comment_seq = get.comment;


                    Log.d("getFirebaseDatabase", "----------------------- " + get.comment );
                    if(userid.equals(get.userid) ){
                        //Toast.makeText(DetailActivity.this, key, Toast.LENGTH_SHORT).show();

                        for (int i=0;i<comment_seq+1;i++) {
                            //Log.d("getFirebaseDatabase", "!!!!!!!!!!!!!!!!!!!!!!info: " + key);
                            String c = postSnapshot.child("comment" + i).getValue().toString();

                            //Toast.makeText(DetailActivity.this, postSnapshot.child("comment").getValue().toString(), Toast.LENGTH_SHORT).show();
                            arrayData.add(c);
                            commentList.add(c);
                            arrayAdapter.add(c);
                            //Toast.makeText(DetailActivity.this, arrayData.get(i), Toast.LENGTH_SHORT).show();
                        }

                    }
                }

                arrayAdapter.notifyDataSetChanged();
                listView.setSelection(arrayAdapter.getCount() -1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("getFirebaseDatabase","loadPost:onCancelled", databaseError.toException());
            }
        };
        Query sortbyAge = FirebaseDatabase.getInstance().getReference().child("id_list").orderByChild("id");
        sortbyAge.addListenerForSingleValueEvent(postListener);
    }



    //comment 삭제하기
    public void removeFirebaseDatabase(int index){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("getFirebaseDatabase", "key: " + dataSnapshot.getChildrenCount());
                uqdateList.clear();
                arrayAdapter.clear();

                listView.setAdapter(arrayAdapter);


                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    FirebasePost get = postSnapshot.getValue(FirebasePost.class);

                    mPostReference = FirebaseDatabase.getInstance().getReference();
                    Map<String, Object> childUpdates = new HashMap<>();


                    comment_seq=get.comment;


                    //Log.d("getFirebaseDatabase", "!!!!!!!!!!!!!!!!!!!!!!info: " );
                    if(userid.equals(get.userid) ){
                        //Toast.makeText(DetailActivity.this, key, Toast.LENGTH_SHORT).show();

                      // 배열에 저장
                        for (int i=0;i<comment_seq+1;i++) {
                            Log.d("getFirebaseDatabase", "!!!!!!!!!!!!!!!!!!!!!!info: " + key);
                            String c = postSnapshot.child("comment" + i).getValue().toString();

                            uqdateList.add(c); // 배열에 저장
                            arrayAdapter.add(c);

                            tmpList.clear();
                        }

                        // 값 삭제
                        for (int i=0;i< comment_seq+1 ;i++) {
                            Log.d("getFirebaseDatabase", "!!!!!!!!!!!!!!!!!!!!!!info: " + key);
                            if(index == 0 && get.comment == 0){
                                childUpdates.put("/id_list/" + userid + "/comment"  , -1);
                                break;
                            }

                            else {
                                //Log.d("getFirebaseDatabase", "~~~~~~~~~~~~~~~~~~~~~~~ " + );

                                if(i == index) {
                                    for (int j = 0; j < i; j++) {
                                        String s = uqdateList.get(j);
                                        tmpList.add(s);
                                        Log.d("getFirebaseDatabase", "~~~~~~~~~~~~~~~~~~~~~~~1::: " +tmpList.get(j) + s);
                                    }
                                    for (int x = i; x < comment_seq; x++) {
                                        String t = uqdateList.get(x + 1);
                                        tmpList.add(t);

                                        Log.d("getFirebaseDatabase", "~~~~~~~~~~~~~~~~~~~~~~~2::: " +tmpList.get(x) +t);
                                    }
                                }
                            }



                        }



                        // 파이어베이스 값 비우기
                        for (int j = 0; j < comment_seq+1; j++){
                            mPostReference.child("/id_list/"+userid+"/comment"+ j+ "/").setValue(null);
                            mPostReference.updateChildren(childUpdates);
                        }


                        if(comment_seq > -1){
                            childUpdates.put("/id_list/" + userid + "/comment", --comment_seq);
                            mPostReference.updateChildren(childUpdates);


                            for (int i = 0; i < comment_seq + 1; i++) {
                                mPostReference.child("/id_list/" + userid + "/comment" + i + "/").setValue(tmpList.get(i));
                                //Log.d("getFirebaseDatabase", "~~~~~~~~~~~~~~~~~~~~~~~ " + tmpList.get(i));
                                mPostReference.updateChildren(childUpdates);
                            }
                        }


                        getFirebaseDatabase();
                    }
                }

                arrayAdapter.notifyDataSetChanged();
                listView.setSelection(arrayAdapter.getCount() -1);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("getFirebaseDatabase","loadPost:onCancelled", databaseError.toException());
            }
        };
        Query sortbyAge = FirebaseDatabase.getInstance().getReference().child("id_list").orderByChild("id");
        sortbyAge.addListenerForSingleValueEvent(postListener);
    }


}

