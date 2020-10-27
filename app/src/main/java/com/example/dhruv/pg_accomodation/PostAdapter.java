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

//    public Context mContext;
//    public List<Post> mPost;
    public Context context;

    //private FirebaseUser firebaseUser;

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

        Glide.with(holder.post_image.getContext()).load(model.getPostimage()).into(holder.post_image);

        if(model.getPosttitle().equals("")){
            holder.title.setVisibility(View.GONE);
        }else {
            holder.title.setVisibility(View.VISIBLE);
            holder.title.setText(model.getPosttitle());
        }

        if(model.getPostdescription().equals("")){
            holder.description.setVisibility(View.GONE);
        }else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(model.getPostdescription());
        }

        if(model.getPostaddress().equals("")){
            holder.address.setVisibility(View.GONE);
        }else {
            holder.address.setVisibility(View.VISIBLE);
            holder.address.setText(model.getPostaddress());
        }
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

        }

    }//end of view holder class



//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        try{
//
//            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//            Post post = mPost.get(position);
//            Glide.with(mContext).load(post.getPostimage()).into(holder.post_image);
//
//            if(post.getPostdescription().equals("")){
//                holder.description.setVisibility(View.GONE);
//            }else {
//                holder.description.setVisibility(View.VISIBLE);
//                holder.description.setText(post.getPostdescription());
//            }
//            if(post.getPostprice().equals("")){
//                holder.rent.setVisibility(View.GONE);
//            }else {
//                holder.rent.setVisibility(View.VISIBLE);
//                holder.rent.setText(post.getPostdescription());
//            }
//            if(post.getPostaddress().equals("")){
//                holder.address.setVisibility(View.GONE);
//            }else {
//                holder.address.setVisibility(View.VISIBLE);
//                holder.address.setText(post.getPostdescription());
//            }
//            if(post.getPosttitle().equals("")){
//                holder.title.setVisibility(View.GONE);
//            }else {
//                holder.title.setVisibility(View.VISIBLE);
//                holder.title.setText(post.getPostdescription());
//            }
//
//            publisherinfo(holder.profile_image,holder.username,holder.publisher,post.getPublisher());
//
//        }catch (Exception e){
//            Toast.makeText(mContext, "in OnbindViewHolder: "+e.getMessage(), Toast.LENGTH_LONG).show();
//
//        }
//
//
//
//
//
//    }//onbindviewholder



//    @Override
//    public int getItemCount() {
//        return mPost.size() ;
//    }






//    private void publisherinfo(final ImageView image_profile,final TextView username, final TextView publisher,final String userid){
//        try{
//            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user").child(userid);
//            databaseReference.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    User user = snapshot.getValue(User.class);
//                    username.setText(user.name);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//
//        }catch (Exception e){
//            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//
//    }//publiserinfo


}//post adapter class
