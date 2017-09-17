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
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.austin.pharmafriend.R.id.OTC_field;
import static com.example.austin.pharmafriend.R.id.OTC_list;

public class ProfileActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView history_list;
    private EditText history_field;

    private ArrayList<String> history_array;
    private ArrayAdapter<String> h_adapter;

    DatabaseReference historydbref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        historydbref = FirebaseDatabase.getInstance().getReference("medicalHistory");

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //new
        history_list = (ListView)findViewById(R.id.history_list);

        history_array = new ArrayList<>();
        h_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, history_array);

        history_list.setAdapter(h_adapter);

        history_field = (EditText)findViewById(R.id.history_field);

        historydbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for( DataSnapshot ds : dataSnapshot.getChildren()) {
                    String data = ds.getValue().toString();
                    history_array.add(data);
                    h_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }

    public void addhistory(View v){
        String text = history_field.getText().toString();
        history_array.add(text);
        h_adapter.notifyDataSetChanged();
        historydbref.push().setValue(text);
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
    public void currentMedsClick(View v){
        Intent i = new Intent(this, currentMedsActivity.class);
        startActivity(i);
    }
}
