package com.example.dhruv.pg_accomodation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.dhruv.pg_accomodation.BitMapUtility.StringToBitMap;

public class PostAdapter extends FirebaseRecyclerAdapter<Post, PostAdapter.ViewHolder> {
    public Context context;

    public PostAdapter(@NonNull FirebaseRecyclerOptions<Post> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final Post model) {

        if (model != null) {
            //set username
            try {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user").child(model.getPostOwner());
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //set user details
                        UserModel user = snapshot.getValue(UserModel.class);
                        if (user!=null) {
                            holder.usernameTextView.setText(user.getUsername());
                            Bitmap bitmap;
                            bitmap = StringToBitMap(user.getProfileImage());
                            holder.profileImageView.setImageBitmap(bitmap);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, error.getMessage() + "", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(context, e.getMessage() + "", Toast.LENGTH_SHORT).show();
            }


            //post img
            Glide.with(holder.postImageView.getContext()).load(model.getPostImage()).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    //Toast.makeText(holder.postImageView.getContext(), e.getMessage()+"", Toast.LENGTH_SHORT).show();
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).placeholder(R.drawable.ic_launcher_background).dontAnimate().into(holder.postImageView);
            //Toast.makeText(getC, "", Toast.LENGTH_SHORT).show();
            holder.postImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ViewPostActivity.class);
                    intent.putExtra("postId", model.getPostId() + "");
                    intent.putExtra("ownerId", model.getPostOwner() + "");
                    context.startActivity(intent);
                }
            });

        //set profile img
//        try{
//
//            DatabaseReference df = FirebaseDatabase.getInstance().getReference("profileiges").child(model.getPublisher());
//            df.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if(snapshot.exists()){
//                        User user = snapshot.getValue(User.class);
//                        if(user.getProfileimg()!=""){
//                            Glide.with(holder.profile_image.getContext()).load(user.getProfileimg()).into(holder.profile_image);
//                        }
//                    }
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                }
//            });
//        }catch (Exception e){
//            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//        }

            //set post details
            holder.postTypeTextView.setText(model.getPostType());
            holder.postStatustextView.setText(model.getPostStatus());
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView postImageView;
        public CircleImageView profileImageView;
        public MaterialTextView usernameTextView, postTypeTextView, postStatustextView;
        public AppCompatRatingBar ratingBar;

        public ViewHolder(@NotNull View itemView) {
            super(itemView);
            profileImageView = (CircleImageView) itemView.findViewById(R.id.user_profile_imageView);
            postImageView = itemView.findViewById(R.id.post_image_imageView_postItem);
            usernameTextView = itemView.findViewById(R.id.username_textView);
            postStatustextView = itemView.findViewById(R.id.post_status_textView);
            postTypeTextView = itemView.findViewById(R.id.post_type_textView);
            ratingBar = itemView.findViewById(R.id.post_ratings_ratingBar);
        }
    }
}//post adapter class
