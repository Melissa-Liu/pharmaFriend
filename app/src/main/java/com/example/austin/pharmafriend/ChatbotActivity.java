package com.example.austin.pharmafriend;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.Menu;
import android.app.Activity;
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

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import org.xmlpull.v1.XmlPullParser;
import android.graphics.Color;
import android.util.AttributeSet;
import java.lang.Object;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

public class ChatbotActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private String BaseURI = "https://directline.botframework.com";
    private EditText chat_field;
    private LinearLayout mLinLayout;

    private ListView chat_list;
    private ArrayList<String> chat_array;
    private ArrayAdapter<String> c_adapter;
    public boolean human = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        chat_field = (EditText)findViewById(R.id.chat_field);

        //mLinLayout = (LinearLayout)findViewById(R.id.chat_area);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chat_list = (ListView)findViewById(R.id.chat_list);

        chat_array = new ArrayList<>();

        c_adapter = new ArrayAdapter<String>(this, R.layout.textviewattributess, R.id.textViewAttributes, chat_array){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the current item from ListView
                View view = super.getView(position,convertView,parent);
                if(position%2 == 1)
                {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(30, 0, 0, 0);
                    // Set a background color for ListView regular row/item
                    view.setBackgroundColor(Color.parseColor("#009999"));
                    view.setMinimumWidth(1100);
                    view.setMinimumHeight(50);



                    //view.setGravity(Gravity.RIGHT);
                }
                else
                {
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(100,100,300,100);
                    // Set the background color for alternate row/item
                    view.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    view.setMinimumWidth(1100);

                    //chat_field.setGravity(Gravity.LEFT);

                }
                return view;
            }
        };

        chat_list.setAdapter(c_adapter);

        chat_field = (EditText)findViewById(R.id.chat_field);
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

    public String getRxCUI(String drugNameResponse){
        //drugNameResponse is returned by onEnter
        int firstIndex = drugNameResponse.indexOf(',') + 1;
        int lastIndex = drugNameResponse.length();
        String drugName = drugNameResponse.substring(firstIndex, lastIndex);
        String baseUrl = "https://rxnav.nlm.nih.gov/REST/rxcui?name=" + drugName;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(baseUrl).build();
        String output = "";
        try{
            Response response = client.newCall(request).execute();
            output = response.toString();

        }catch(Exception e){
            e.printStackTrace();
        }
        return output;
    };

    public void chatBotMessage(String request, String drug){

    }

//    public void enterMsg(View v){
//        mLinLayout.addView(createNewTextView());
//    }

//    protected void createList() {
//
//        //final LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        chat_list = (ListView)findViewById(R.id.chat_list);
//
//        chat_array = new ArrayList<>();
//        c_adapter = new ArrayAdapter<String>(this, R.layout.textviewattributess, R.id.textViewAttributes, chat_array);
//
//        chat_list.setAdapter(c_adapter);
//
//        chat_field = (EditText)findViewById(R.id.chat_field);
//
//
//        /*int i = this.getResources().getIdentifier("textViewAttributes", "style", this.getPackageName());
//        System.out.print(i);
//        XmlPullParser parser = this.getResources().getXml(R.values.);
//        //AttributeSet attributes = Xml.asAttributeSet(parser);
//        final TextView textView = new TextView(this,attributes);*/
////        textView.setLayoutParams(lparams);
////        String msg = chat_field.getText().toString();
////        textView.setText(msg);
////        return textView;
//    }

    public void addchat(View v){

       /* if (human) {
            c_adapter = new ArrayAdapter<String>(this, R.layout.textviewattributess, R.id.textViewAttributes, chat_array);
            human = false;
        }
        else {
            c_adapter = new ArrayAdapter<String>(this, R.layout.textviewattributestwo, R.id.textViewAttributes2, chat_array);
            human = true;
        }
*/

        String text = chat_field.getText().toString();
        chat_array.add(text);
        c_adapter.notifyDataSetChanged();
    }




}




