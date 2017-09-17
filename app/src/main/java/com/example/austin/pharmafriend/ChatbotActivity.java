package com.example.austin.pharmafriend;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ChatbotActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private String BaseURI = "https://directline.botframework.com";
    private EditText chat_field;
    private LinearLayout mLinLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        chat_field = (EditText)findViewById(R.id.chat_field);

        mLinLayout = (LinearLayout)findViewById(R.id.message_area);

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

    public String onEnter(View v){
        String userText = chat_field.getText().toString();
        OkHttpClient client = new OkHttpClient.Builder().build();
        String baseUrl = "https://directline.botframework.com/v3" + "/directline/conversations/%s/activities";

        //Creates a new conversation
        JSONObject emptyBodyJson = new JSONObject();
        RequestBody emptyBody = RequestBody.create(MediaType.parse("application/json"), emptyBodyJson.toString());
        Request createConvo = new Request.Builder().post(emptyBody).url("https://directline.botframework.com" +
                "/v3/directline/conversations").header("Authorization", "Bearer JpxDEJksCi0.cwA.osc." +
                "ZdtOIpo-7sihDULvIt7YrdEAMFvFJjvzXGoY912WmqU").build();
        final Call convoCall = client.newCall(createConvo);
        final JSONObject conversationId = new JSONObject();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonResponse = new JSONObject(convoCall.execute().body().string());
                    Log.d("Here", jsonResponse.toString());
                    conversationId.put("conversationId", jsonResponse.getString("conversationId"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Posts the user input
        JSONObject body = new JSONObject();
        JSONObject from = new JSONObject();
        try {
            from.put("id", "user1");
            body.put("type", "message");
            body.put("from", from);
            body.put("userText", userText);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //RequestBody requestBody = RequestBody.create(MediaType.parse("JSON"), body.toString());


        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");

        RequestBody input = RequestBody.create(JSON, body.toString());


        //need to get conv id from conversationId

        String convID = " ";
        try {
            JSONObject arr = new JSONObject(conversationId.toString());
            convID = arr.getString("conversationId");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String formatted = String.format(baseUrl, convID);

        //need to make request  Request Body include JSON
        Request userInput = new Request.Builder().post(input).url(formatted).header("Authorization", "Bearer JpxDEJksCi0.cwA.osc." +
                "ZdtOIpo-7sihDULvIt7YrdEAMFvFJjvzXGoY912WmqU").build();
        final Call userCall = client.newCall(userInput);

        final JSONObject id = new JSONObject();
        Thread threadTwo = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonResponse = new JSONObject(userCall.execute().body().string());
                    Log.d("Here", jsonResponse.toString());
                    id.put("id", jsonResponse.getString("id"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        threadTwo.start();

        try {
            threadTwo.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // need to get id from id json

        String secondId = " ";
        try {
            JSONObject arr = new JSONObject(id.toString());
            secondId = arr.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        secondId = secondId.substring(0, secondId.length() - 8);
        String newString = String.format(baseUrl, secondId);
        //Chatbot returns the type of action and name of drug

        Request userGet = new Request.Builder().get().url(newString).header("Authorization", "Bearer JpxDEJksCi0.cwA.osc." +
                "ZdtOIpo-7sihDULvIt7YrdEAMFvFJjvzXGoY912WmqU").build();
        final Call lastCall = client.newCall(userGet);

        final JSONObject output = new JSONObject();
        final JSONObject[] activity = {new JSONObject()};

        Thread threadThree = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonResponseThree = new JSONObject(lastCall.execute().body().string());
                    JSONArray activities = jsonResponseThree.getJSONArray("activities");
                    activity[0] = activities.getJSONObject(activities.length()-1);
                    output.put("text", activity[0].getString("text"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        threadThree.start();

        try {
            threadThree.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String outputFinal = " ";
        try {
            JSONObject arr = new JSONObject(output.toString());
            outputFinal = arr.getString("text");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return outputFinal;
    }


    /*
    public String getRxCUI(String drugNameResponse){
        //drugNameResponse is returned by onEnter
        int firstIndex = drugNameResponse.indexOf(',') + 1;
        int lastIndex = drugNameResponse.length();
        String drugName = drugNameResponse.substring(firstIndex, lastIndex);
        String baseUrl = "https://rxnav.nlm.nih.gov/REST/rxcui?name=" + drugName;
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url(baseUrl).build();
        final String[] output = {""};
        final JSONObject[] tester = {new JSONObject()};

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    JSONObject jsonResponse = new JSONObject(client.newCall(request).execute().body().string());
                    tester[0] = jsonResponse;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        try{
            thread.join();
        }catch(Exception e){
            e.printStackTrace();
        }
        return "hello";
    };
    */

    public String getDescriptionMessage(ArrayList<String> RxCUIList){
        String parameter = "";
        for( int i = 0; i < RxCUIList.size(); i++){
            if( i != RxCUIList.size() - 1 ){
                parameter = parameter + RxCUIList.get(i) + "+";
            }
            else{
                parameter = parameter + RxCUIList.get(i);
            }
        }
        String baseUrl = "https://rxnav.nlm.nih.gov/REST/interaction/list.json?rxcuis=" + parameter;
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder().url(baseUrl).build();
        final JSONObject[] tester = {new JSONObject()};
        final JSONArray[] arrtest = {new JSONArray()};
        final JSONArray[] arrtest2 = {new JSONArray()};
        final JSONArray[] arrtest3 = {new JSONArray()};
        final JSONObject[] obj = {new JSONObject()};
        final JSONObject[] obj2 = {new JSONObject()};
        final JSONObject[] obj3 = {new JSONObject()};


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    JSONObject jsonResponse = new JSONObject(client.newCall(request).execute().body().string());
                    tester[0] = jsonResponse;
                    arrtest[0] = jsonResponse.getJSONArray("fullInteractionTypeGroup");
                    obj[0] = arrtest[0].getJSONObject(0);
                    arrtest2[0] = obj[0].getJSONArray("fullInteractionType");
                    obj2[0] = arrtest2[0].getJSONObject(0);
                    arrtest3[0] = obj2[0].getJSONArray("interactionPair");
                    obj3[0] = arrtest3[0].getJSONObject(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        try{
            thread.join();
        }catch(Exception e){
            e.printStackTrace();
        }

        String output = "";
        try{
            output = obj3[0].getString("description");
        }catch(Exception e){
            e.printStackTrace();
        }

        return output;
    };

    public void chatBotMessage(String request, String drug){

    }

    public void enterMsg(View v){
        ArrayList<String> rxcuis = new ArrayList<String>(
                Arrays.asList("207106", "152923", "656659"));
        getDescriptionMessage(rxcuis);
    }

    private TextView createNewTextView() {
        final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(this);
        textView.setLayoutParams(lparams);
        String msg = chat_field.getText().toString();
        textView.setText(msg);
        return textView;
    }

}



