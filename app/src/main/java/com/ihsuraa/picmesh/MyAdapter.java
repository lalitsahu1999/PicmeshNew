package com.ihsuraa.picmesh;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private Context context;
    int f =0;
    private ArrayList<statusArrayList> personUtils;

    public MyAdapter(Context context, ArrayList<statusArrayList> personUtils) {
        this.context = context;
        this.personUtils = personUtils;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.itemView.setTag(personUtils.get(position));



        if (position==0){

            holder.uname.setText("add story");
            holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.vector_accont));

        }
        else {
            holder.progressBar.setVisibility(View.VISIBLE);
            statusArrayList pu = personUtils.get(position-1);
           // Toast.makeText(holder.itemView.getContext(),pu.getName(), Toast.LENGTH_SHORT).show();
            holder.uname.setText(pu.getName());
            //holder.image.setImageURI(Uri.parse(pu.getImage()));

            Picasso.get().load(Uri.parse(pu.getImage())).into(holder.image, new Callback() {
                @Override
                public void onSuccess() {
                    holder.progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onError(Exception e) {

                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return personUtils.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView uname;
        public ProgressBar progressBar;
        public ImageView image;

        public ViewHolder(final View itemView) {
            super(itemView);

            uname = (TextView) itemView.findViewById(R.id.txtView);
            image = (ImageView) itemView.findViewById(R.id.image);
            progressBar = (ProgressBar) itemView.findViewById(R.id.testprogress);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    //PersonUtils cpu = (PersonUtils) view.getTag();

                    if (getAdapterPosition()==0){
                       // Intent intent = new Intent(context , StatusCamera.class);
                        context.startActivity(new Intent(context, statusCamera.class));
                     // checkforstatus();
                    }
                    Toast.makeText(view.getContext(), String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();

                }
            });



        }
    }

    private void checkforstatus() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("stories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()) {
                        Toast.makeText(context, "view status", Toast.LENGTH_SHORT).show();

                    }
                    else {

                    }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context,error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}