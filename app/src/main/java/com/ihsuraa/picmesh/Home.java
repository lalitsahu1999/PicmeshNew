package com.ihsuraa.picmesh;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {
    Button button;
    SharedPreferences UserDetails;
    static String[] nameArray = {"Gas", "Insurance", "Electronics", "Other Services"};
    static Integer[] drawableArray = {R.drawable.vector_cake, R.drawable.vector_camera, R.drawable.vector_accont, R.drawable.vector_mylocation};
    private RecyclerView horizontal_recycler_view;
    private ArrayList<statusArrayList> horizontalList;
    private statusAdapter horizontalAdapter;
    private  MyAdapter myAdapter;
    private String userId;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        button = findViewById(R.id.logout);
        UserDetails = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
        setStatusBarColor();
        retrieveValue();



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FirebaseAuth.getInstance().getCurrentUser()!=null){

                    FirebaseAuth.getInstance().signOut();
                    try {

                        if (UserDetails.getAll().size() > 0){
                            UserDetails.edit().clear().apply();
                        }
                        startActivity(new Intent(Home.this, loginActivity.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }


                   // UserDetails.edit().clear().apply();
                    //startActivity(new Intent(Home.this, loginActivity.class));
                }
            }
        });
    }

    private void retrieveValue() {


        userId = FirebaseAuth.getInstance().getUid();
        horizontal_recycler_view= (RecyclerView) findViewById(R.id.status_recyclerview);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("stories").child(userId);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                horizontalList = new ArrayList<statusArrayList>();
                for(DataSnapshot datas: dataSnapshot.getChildren()){

                    String storyUrl = datas.child("url").getValue().toString();
                    String timeStamp = datas.child("timestamp").getValue().toString();

                    horizontalList.add(new statusArrayList(timeStamp, storyUrl));
                }

                if (horizontalList.size()>0){
                   // horizontalAdapter=new statusAdapter(horizontalList);
                    myAdapter = new MyAdapter(Home.this, horizontalList);
                    //Toast.makeText(getApplicationContext(),String.valueOf(horizontalAdapter.getItemCount()),Toast.LENGTH_LONG).show();
                    LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(Home.this, LinearLayoutManager.HORIZONTAL, false);
                    horizontal_recycler_view.setLayoutManager(horizontalLayoutManagaer);
                    horizontal_recycler_view.setAdapter(myAdapter);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });



        /*
        ValueEventListener valueEventListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                horizontalList = new ArrayList<statusArrayList>();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String storyUrl = ds.child("url").getValue(String.class);
                    String timeStamp = ds.child("timestamp").getValue(String.class);
                   // horizontalList.add(new statusArrayList(timeStamp, storyUrl));
                }
                //horizontal_recycler_view= (RecyclerView) findViewById(R.id.status_recyclerview);
                //horizontalAdapter=new statusAdapter(horizontalList);
                //LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(Home.this, LinearLayoutManager.HORIZONTAL, false);
                //horizontal_recycler_view.setLayoutManager(horizontalLayoutManagaer);
                //horizontal_recycler_view.setAdapter(horizontalAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        };

         */
        //ref.addListenerForSingleValueEvent(valueEventListener);
    }

    private void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.statusbar, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.statusbar));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            userId = user.getUid();
        }
    }
}
