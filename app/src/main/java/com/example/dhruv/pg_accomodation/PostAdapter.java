package com.example.dhruv.pg_accomodation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends FirebaseRecyclerAdapter<Post,PostAdapter.ViewHolder> {
    public Context context;

    public PostAdapter(@NonNull FirebaseRecyclerOptions<Post> options) {
        super(options);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull Post model) {

        //set username
        try{
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user").child(model.getPublisher());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    holder.username.setText(user.name);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }catch (Exception e){
            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        }

        //post img
        Glide.with(holder.post_image.getContext()).load(model.getPostimage()).into(holder.post_image);


        //set profile img
        try{
            final String currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference df = FirebaseDatabase.getInstance().getReference("profileiges").child(currentFirebaseUser);
            df.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        User user = snapshot.getValue(User.class);
                        if(user.getProfileimg()!=""){
                            Glide.with(holder.profile_image.getContext()).load(user.getProfileimg()).into(holder.profile_image);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }catch (Exception e){
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //set post title
        if(model.getPosttitle().equals("")){
            holder.title.setVisibility(View.GONE);
        }else {
            holder.title.setVisibility(View.VISIBLE);
            holder.title.setText(model.getPosttitle());
        }

        //set description
        if(model.getPostdescription().equals("")){
            holder.description.setVisibility(View.GONE);
        }else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(model.getPostdescription());
        }

        //set address
        if(model.getPostaddress().equals("")){
            holder.address.setVisibility(View.GONE);
        }else {
            holder.address.setVisibility(View.VISIBLE);
            holder.address.setText(model.getPostaddress());
        }

        //set rent
        if(model.getPostprice().equals("")){
            holder.rent.setVisibility(View.GONE);
        }else {
            holder.rent.setVisibility(View.VISIBLE);
            holder.rent.setText(model.getPostprice());
        }

    }//onbindfunction


    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView post_image;
        public CircleImageView profile_image;
        public TextView username,title,description,address,rent,publisher;

        public ViewHolder(@NotNull View itemView){
            super(itemView);
            profile_image =(CircleImageView) itemView.findViewById(R.id.profile_image);
            post_image = itemView.findViewById(R.id.post_image);
            username = itemView.findViewById(R.id.username);
            title = itemView.findViewById(R.id.post_title);
            description = itemView.findViewById(R.id.post_description);
            address = itemView.findViewById(R.id.addresstv);
            rent = itemView.findViewById(R.id.post_rent);
        }//viewholder method
    }//end of view holder class


}//post adapter class
