package com.example.dhruv.pg_accomodation.profile_RecycleView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.dhruv.pg_accomodation.Post;
import com.example.dhruv.pg_accomodation.R;
import com.example.dhruv.pg_accomodation.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.utils.widget.ImageFilterButton;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapater extends FirebaseRecyclerAdapter<Recycleview_post,RecyclerViewAdapater.ViewHolder> {
    public Context context;

    public RecyclerViewAdapater(@NonNull FirebaseRecyclerOptions<Recycleview_post> options , Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull final Recycleview_post model) {



        //set username
        try{
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user").child(model.getPublisher());
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    holder.username.setText(user.getName());
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }catch (Exception e){
            Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        }

        //post img
        try{
            Glide.with(holder.post_image.getContext()).load(model.getPostimage()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    //Toast.makeText(context, "Error: in glide"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(holder.post_image);
        }catch (Exception e){
            Toast.makeText(context, "G Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }




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
                            try{
                                Glide.with(holder.profile_image.getContext()).load(user.getProfileimg()).into(holder.profile_image);
                            }catch (Exception e){
                                Toast.makeText(context, "G Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Error "+error.getMessage(), Toast.LENGTH_SHORT).show();
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

        //update post
        holder.post_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogPlus dialog = DialogPlus.newDialog(context)
                        .setGravity(Gravity.CENTER)
                        .setMargin(50,0,50,0)
                        .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.post_update))
                        .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                        .create();
                final View postholder = dialog.getHolderView();
                final EditText title = postholder.findViewById(R.id.upposttitle);
                final EditText description = postholder.findViewById(R.id.uppostdescription);
                final EditText address = postholder.findViewById(R.id.uppostaddress);
                final EditText rent = postholder.findViewById(R.id.uppostrent);
                Button update = postholder.findViewById(R.id.uppostupdatebtn);
                title.setText(model.getPosttitle());
                description.setText(model.getPostdescription());
                address.setText(model.getPostaddress());
                rent.setText(model.getPostprice());
                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String,Object> map = new HashMap<>();
                        map.put("posttitle",title.getText().toString());
                        map.put("postdescription",description.getText().toString());
                        map.put("postaddress",address.getText().toString());
                        map.put("postprice",rent.getText().toString());
                        FirebaseDatabase.getInstance().getReference().child("Posts").child(String.valueOf(getRef(position).getKey()))
                                .updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(context, "Update successfully", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });

                    }
                });
                dialog.show();
            }
        });

        //delete post
        holder.post_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }//onbidview

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_post_items, parent,false);
        return new ViewHolder(view);
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView post_image;
        public CircleImageView profile_image;
        public TextView username,title,description,address,rent,publisher;
        public ImageFilterButton post_update,post_delete;

        public ViewHolder(@NotNull View itemView){
            super(itemView);
            profile_image =(CircleImageView) itemView.findViewById(R.id.p_profile_image);
            post_image = itemView.findViewById(R.id.p_post_image);
            username = itemView.findViewById(R.id.p_username);
            title = itemView.findViewById(R.id.p_post_title);
            description = itemView.findViewById(R.id.p_post_description);
            address = itemView.findViewById(R.id.p_addresstv);
            rent = itemView.findViewById(R.id.p_post_rent);
            post_update = itemView.findViewById(R.id.posteditbutton);
            post_delete = itemView.findViewById(R.id.postdeletebtn);
        }//viewholder method
    }//end of view holder class


}//post adapter class
