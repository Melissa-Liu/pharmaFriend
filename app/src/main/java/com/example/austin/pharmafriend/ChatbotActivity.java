package com.example.austin.pharmafriend;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ChatbotActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String BaseURI = "https://directline.botframework.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void profileClick(View v){
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }
    public void currentMedsClick(View v){
        Intent i = new Intent(this, currentMedsActivity.class);
        startActivity(i);
    }

    public String onEnter(String text){
        OkHttpClient client = new OkHttpClient.Builder().build();
        String baseUrl = "https://directline.botframework.com/v3" + "/directline/conversations/%s/activities";

        //Creates a new conversation
        JSONObject emptyBodyJson = new JSONObject();
        RequestBody emptyBody = RequestBody.create(MediaType.parse("application/json"), emptyBodyJson.toString());
        Request createConvo = new Request.Builder().post(emptyBody).url("https://directline.botframework.com" +
                "/v3/directline/conversations").header("Authorization", "Bearer JpxDEJksCi0.cwA.osc." +
                "ZdtOIpo-7sihDULvIt7YrdEAMFvFJjvzXGoY912WmqU").build();
        Call convoCall = client.newCall(createConvo);
        String conversationId = "";
        try {
            JSONObject jsonResponse = new JSONObject(convoCall.execute().body().string());
            conversationId = jsonResponse.getString("conversationId");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Posts the user input
        JSONObject body = new JSONObject();
        try {
            body.put("type", "message");
            body.put("text", text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), body.toString());

        String formatted = String.format(baseUrl, conversationId);

        Request userInput = new Request.Builder().post(requestBody).url("https://directline.botframework.com/v3" +
                "/directline/conversations/abc123/activities").header("Authorization", "Bearer JpxDEJksCi0.cwA.osc." +
                "ZdtOIpo-7sihDULvIt7YrdEAMFvFJjvzXGoY912WmqU").build();
        Call userCall = client.newCall(userInput);
        String id = "";
        try {
            JSONObject jsonResponseTwo = new JSONObject(convoCall.execute().body().string());
            id = jsonResponseTwo.getString("id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String newString = String.format(baseUrl, id);
        //Chatbot returns the type of action and name of drug

        Request userGet = new Request.Builder().get().url("https://directline.botframework.com/v3" +
                "/directline/conversations/abc123/activities").header("Authorization", "Bearer JpxDEJksCi0.cwA.osc." +
                "ZdtOIpo-7sihDULvIt7YrdEAMFvFJjvzXGoY912WmqU").build();
        Call getCall = client.newCall(userGet);

        String output = "";
        try {
            JSONObject jsonResponseThree = new JSONObject(getCall.execute().body().string());
            JSONArray messages = jsonResponseThree.getJSONArray("activities");
            int length = messages.length();
            output = messages.getJSONObject(length - 1).getString("text");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    public void chatBotMessage(String request, String drug){

    }



}



