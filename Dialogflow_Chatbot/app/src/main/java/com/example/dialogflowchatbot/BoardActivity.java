package com.example.dialogflowchatbot;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

//<blockquote class="se2_quote9" style="_zoom:1;margin:0 0 30px 0;padding:10px;border:2px solid #e5e5e5;color:#888888;">
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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

import javax.net.ssl.HttpsURLConnection;

public class BoardActivity extends AppCompatActivity {

    // 로그에 사용할 TAG 변수
    final private String TAG = getClass().getSimpleName();
    private DatabaseReference mPostReference;

    SimpleAdapter arrayAdapter;

    static ArrayList<String> arrayIndex =  new ArrayList<String>();
    static ArrayList<String> arrayData = new ArrayList<String>();

    // 리스트뷰
    ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();



    // 사용할 컴포넌트 선언
    ListView listView;
    Button reg_button;
    String userid = "";

    // 리스트뷰에 사용할 제목 배열
    ArrayList<String> titleList = new ArrayList<>();
    ArrayList<String> userList = new ArrayList<>();
    ArrayList<String> contentList = new ArrayList<>();


    // 클릭했을 때 어떤 게시물을 클릭했는지 게시물 번호를 담기 위한 배열
    ArrayList<Integer> seqList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("게시글");
        // Register에서 넘겨받은 userid 값 가져오기
        userid = getIntent().getStringExtra("userid");

        mPostReference = FirebaseDatabase.getInstance().getReference();

        // 컴포넌트 초기화
        listView = findViewById(R.id.listView);


        // listView 를 클릭했을 때 이벤트 추가
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // 어떤 값을 선택했는지 토스트를 뿌려줌
                // Toast.makeText(BoardActivity.this, adapterView.getItemAtPosition(i)+ " 클릭", Toast.LENGTH_SHORT).show();

            // 게시물의 번호와 userid를 가지고 DetailActivity 로 이동
                Intent intent = new Intent(BoardActivity.this, DetailActivity.class);
                //intent.putExtra("board_seq", seqList.get(i));
                intent.putExtra("userid", userList.get(i));
                intent.putExtra("title", titleList.get(i));
                intent.putExtra("content", contentList.get(i));
                startActivity(intent);
            }
        });

        // 버튼 컴포넌트 초기화
        reg_button = findViewById(R.id.reg_button);

        // 버튼 이벤트 추가
        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // userid 를 가지고 RegisterActivity 로 이동
                Intent intent = new Intent(BoardActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }


    // onResume() 은 해당 액티비티가 화면에 나타날 때 호출됨
    @Override
    protected void onResume() {
        super.onResume();
        // 해당 액티비티가 활성화 될 때, 게시물 리스트를 불러오는 함수를 호출
        getFirebaseDatabase();

    }

    public String setTextLength(String text, int length){
        if(text.length()<length){
            int gap = length - text.length();
            for (int i=0; i<gap; i++){
                text = text + " ";
            }
        }
        return text;
    }

    //게시글 읽어오기
    public void getFirebaseDatabase(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("getFirebaseDatabase", "key: " + dataSnapshot.getChildrenCount());
                arrayData.clear();
                arrayIndex.clear();
                titleList.clear();
                seqList.clear();
                userList.clear();
                contentList.clear();

                list.clear();


                listView.setAdapter(arrayAdapter);
                int i=0;

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    FirebasePost get = postSnapshot.getValue(FirebasePost.class);


                    String[] info = {get.userid, get.title, get.content};

                    String a = setTextLength(info[1], 20);
                    String b = setTextLength(info[2], 20);


                    HashMap<String,String> item = new HashMap<String, String>();
                    item.put("item1", a);
                    item.put("item2", b);
                    list.add(item);


                    arrayAdapter = new SimpleAdapter(BoardActivity.this, list,android.R.layout.simple_list_item_2,new String[] {"item1","item2"},new int[] {android.R.id.text1, android.R.id.text2});
                    listView.setAdapter(arrayAdapter);


                    arrayIndex.add(key);
                    Log.d("getFirebaseDatabase", "key: " + key);
                    //Log.d("getFirebaseDatabase", "info: " + info[0] + info[1] + info[2] );

                    titleList.add(get.title);
                    seqList.add(i++);
                    userList.add(get.userid);
                    contentList.add(get.content);
                }

                arrayAdapter.notifyDataSetChanged();

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
