package com.example.austin.pharmafriend;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import java.util.ArrayList;

import static android.R.attr.id;
import static android.R.id.button1;

public class currentMedsActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private RelativeLayout reLayout;

    private ListView prescriptions_list;
    private ListView OTC_list;

    private ArrayList<String> prescription_array;
    private ArrayList<String> OTC_array;
    private ArrayAdapter<String> p_adapter;
    private ArrayAdapter<String> o_adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_meds);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prescriptions_list = (ListView)findViewById(R.id.prescription_list);
        OTC_list = (ListView)findViewById(R.id.OTC_list);

        prescription_array = new ArrayList<>();
        p_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, prescription_array);

        OTC_array = new ArrayList<>();
        o_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, OTC_array);

        prescriptions_list.setAdapter(p_adapter);
        OTC_list.setAdapter(o_adapter);
    }
    public void addPrescription(View v){
        prescription_array.add("Prescription");
        p_adapter.notifyDataSetChanged();
    }

    public void addOTC(View v){
        OTC_array.add("OTC");
        o_adapter.notifyDataSetChanged();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if(mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void chatbotClick(View v){
        Intent i = new Intent(this, ChatbotActivity.class);
        startActivity(i);
    }
    public void profileClick(View v){
        Intent i = new Intent(this, ProfileActivity.class);
        startActivity(i);
    }

}
