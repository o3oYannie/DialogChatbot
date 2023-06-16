package com.example.dialogflowchatbot;

import android.content.Intent;
import android.os.Bundle;
//import android.os.Message;
import com.example.dialogflowchatbot.adapters.ChatAdapter;
import com.example.dialogflowchatbot.helpers.SendMessageInBg;
import com.example.dialogflowchatbot.models.Message;

import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dialogflowchatbot.interfaces.BotReply;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.common.collect.Lists;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements BotReply {

    RecyclerView chatView;
    ChatAdapter chatAdapter;
    List<Message> messageList = new ArrayList<>();
    EditText editMessage;
    ImageButton btnSend;

    //dialogFlow
    private SessionsClient sessionsClient;
    private SessionName sessionName;
    private String uuid = UUID.randomUUID().toString();
    private String TAG = "mainactivity";
    private String[] ops ={"건물", "트랙", "학사일정", "셔틀버스", "장학금", "동아리", "전화번호"};
    private String op;

    String option;
    ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();


        chatView = findViewById(R.id.chatView);
        editMessage = findViewById(R.id.editMessage);
        btnSend = findViewById(R.id.btnSend);

        Intent intent = getIntent();

        option = intent.getExtras().getString("options");

        chatAdapter = new ChatAdapter(messageList, this);
        chatView.setAdapter(chatAdapter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                String message = editMessage.getText().toString();
                if (!message.isEmpty()) {
                    messageList.add(new Message(message, false));
                    editMessage.setText("");
                    sendMessageToBot(message);
                    Objects.requireNonNull(chatView.getAdapter()).notifyDataSetChanged();
                    Objects.requireNonNull(chatView.getLayoutManager())
                            .scrollToPosition(messageList.size() - 1);
                } else {
                    Toast.makeText(MainActivity.this, "Please enter text!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setUpBot();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = new Intent(MainActivity.this, BoardActivity.class);
        startActivity(i);
        return true;
    }

    private void setUpBot() {

        try {
            InputStream stream = null;

            if(option.equals("str")){
                stream = this.getResources().openRawResource(R.raw.structure);
                actionBar.setTitle("건물 물어보살!");
                op = ops[0];
            }
            else if(option.equals("dept")){
                stream = this.getResources().openRawResource(R.raw.credential);
                actionBar.setTitle("트랙 물어보살!");
                op = ops[1];
            }
            else if(option.equals("plan")){
                stream = this.getResources().openRawResource(R.raw.plan);
                actionBar.setTitle("일정 물어보살!");
                op = ops[2];
            }
            else if(option.equals("shuttle")){
                stream = this.getResources().openRawResource(R.raw.shuttle);
                actionBar.setTitle("셔틀 물어보살!");
                op = ops[3];
            }
            else if(option.equals("scholarship")){
                stream = this.getResources().openRawResource(R.raw.scholarship);
                actionBar.setTitle("장학금 물어보살!");
                op = ops[4];
            }
            else if(option.equals("club")){
                stream = this.getResources().openRawResource(R.raw.club);
                actionBar.setTitle("동아리 물어보살!");
                op = ops[5];
            }
            else if(option.equals("num")){
                stream = this.getResources().openRawResource(R.raw.number);
                actionBar.setTitle("전화번호 물어보살!");
                op = ops[6];
            }

            GoogleCredentials credentials = GoogleCredentials.fromStream(stream)
                    .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
            String projectId = ((ServiceAccountCredentials) credentials).getProjectId();

            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(
                    FixedCredentialsProvider.create(credentials)).build();
            sessionsClient = SessionsClient.create(sessionsSettings);
            sessionName = SessionName.of(projectId, uuid);

            Log.d(TAG, "projectId : " + projectId);


            messageList.add(new Message(op+"에 대해서 물어보살!", true));
            chatAdapter.notifyDataSetChanged();
            Objects.requireNonNull(chatView.getLayoutManager()).scrollToPosition(messageList.size() - 1);

        } catch (Exception e) {
            Log.d(TAG, "setUpBot: " + e.getMessage());
        }
    }

    private void sendMessageToBot(String message) {

        QueryInput input = QueryInput.newBuilder()
                .setText(TextInput.newBuilder().setText(message).setLanguageCode("ko-KO")).build();
        //Toast.makeText(this, input.toString(), Toast.LENGTH_SHORT).show();
        new SendMessageInBg(this, sessionName, sessionsClient, input).execute();

    }

    @Override
    public void callback(DetectIntentResponse returnResponse) {
        if(returnResponse!=null) {
            String botReply = returnResponse.getQueryResult().getFulfillmentText();
            if(!botReply.isEmpty()){
                messageList.add(new Message(botReply, true));
                chatAdapter.notifyDataSetChanged();
                Objects.requireNonNull(chatView.getLayoutManager()).scrollToPosition(messageList.size() - 1);

                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        //딜레이 후 시작할 코드 작성
                        messageList.add(new Message("더 궁금한 게 있으면 물어보살!", true));
                        chatAdapter.notifyDataSetChanged();
                        Objects.requireNonNull(chatView.getLayoutManager()).scrollToPosition(messageList.size() - 1);

                    }
                }, 2000);// 2초 정도 딜레이를 준 후 시작


            }

            else {
                Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "failed to connect!", Toast.LENGTH_SHORT).show();
        }
    }
}