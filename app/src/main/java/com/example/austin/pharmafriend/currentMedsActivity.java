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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    private EditText prescription_field;
    private EditText OTC_field;

    DatabaseReference prescriptiondbref;
    DatabaseReference OTCdbref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OTCdbref = FirebaseDatabase.getInstance().getReference("OTCs");
        prescriptiondbref = FirebaseDatabase.getInstance().getReference("prescriptions");
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

        prescription_field = (EditText)findViewById(R.id.prescription_field);
        OTC_field = (EditText)findViewById(R.id.OTC_field);

        OTCdbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for( DataSnapshot ds : dataSnapshot.getChildren()) {
                    String data = ds.getValue().toString();
                    OTC_array.add(data);
                    o_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
        prescriptiondbref.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for( DataSnapshot ds : dataSnapshot.getChildren()) {
                    String data = ds.getValue().toString();
                    prescription_array.add(data);
                    p_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });
    }

    public void addPrescription(View v){
        String text = prescription_field.getText().toString();
        prescription_array.add(text);
        p_adapter.notifyDataSetChanged();
        prescriptiondbref.push().setValue(text);
    }

    public void addOTC(View v){
        String text = OTC_field.getText().toString();
        OTC_array.add(text);
        o_adapter.notifyDataSetChanged();
        OTCdbref.push().setValue(text);
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
