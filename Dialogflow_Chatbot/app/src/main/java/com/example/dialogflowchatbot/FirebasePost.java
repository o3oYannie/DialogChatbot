package com.example.dialogflowchatbot;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class FirebasePost {
    public String userid;
    public String title;
    public String content;
    public int comment=-1;



    public FirebasePost(){
        // Default constructor required for calls to DataSnapshot.getValue(FirebasePost.class)
    }


    public FirebasePost(String userid, String title, String content) {
        this.userid = userid;
        this.title = title;
        this.content = content;
    }

    public FirebasePost(String userid, String title, String content, int comment){
        this.userid = userid;
        this.title = title;
        this.content = content;
        this.comment = comment;
    }

    public FirebasePost(int comment){
        this.comment = comment;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userid", userid);
        result.put("title", title);
        result.put("content", content);
        result.put("comment", comment );
        return result;
    }
}