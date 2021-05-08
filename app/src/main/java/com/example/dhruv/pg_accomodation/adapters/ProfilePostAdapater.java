package com.example.dhruv.pg_accomodation.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dhruv.pg_accomodation.actvities.ViewPostActivity;
import com.example.dhruv.pg_accomodation.actvities.ViewPostActivity2;
import com.example.dhruv.pg_accomodation.models.Post;
import com.example.dhruv.pg_accomodation.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.constraintlayout.utils.widget.ImageFilterButton;
import androidx.recyclerview.widget.RecyclerView;

public class ProfilePostAdapater extends FirebaseRecyclerAdapter<Post, ProfilePostAdapater.ViewHolder> {
    public Context context;

    public ProfilePostAdapater(@NonNull FirebaseRecyclerOptions<Post> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_post_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull final Post post) {

        if (post != null) {
            Glide.with(context).load(post.getPostImage()).into(holder.imageView);
            holder.addressTextView.setText(post.getPostAddress());
            holder.rentTextView.setText(post.getPostRent() + " / month");
            holder.cityTextView.setText(" in  " + post.getPostCity());
            holder.typeTextView.setText(post.getPostType());
        }

        //update post
        holder.post_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogPlus dialog = DialogPlus.newDialog(context)
                        .setGravity(Gravity.CENTER)
                        .setPadding(15,15,15,15)
                        .setMargin(50, 0, 50, 0)
                        .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.post_update))
                        .setExpanded(false)  // This will enable the expand feature, (similar to android L share dialog)
                        .create();
                final View postholder = dialog.getHolderView();
                final EditText title = postholder.findViewById(R.id.upposttitle);
                final EditText description = postholder.findViewById(R.id.uppostdescription);
                final EditText address = postholder.findViewById(R.id.uppostaddress);
                final EditText rent = postholder.findViewById(R.id.uppostrent);
                MaterialButton update = postholder.findViewById(R.id.uppostupdatebtn);


                title.setText(post.getPostType());
                description.setText(post.getPostDescription());
                address.setText(post.getPostAddress());
                rent.setText(post.getPostRent());

                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("postType", title.getText().toString());
                        map.put("postDescription", description.getText().toString());
                        map.put("postAddress", address.getText().toString());
                        map.put("postRent", rent.getText().toString());


                        getRef(position).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Update successfully", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete post");
                builder.setMessage("Are you sure to delete this post?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        getRef(position).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                builder.show();
            }
        });

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ViewPostActivity2.class);
                intent.putExtra("postId",post.getPostId());
                intent.putExtra("ownerId",post.getPostOwner());
                context.startActivity(intent);
            }
        });

    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageFilterButton post_update, post_delete;
        public TextView cityTextView, typeTextView, rentTextView, addressTextView;
        public ImageView imageView;
        public RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.post_image_edit);
            typeTextView = itemView.findViewById(R.id.type_textView_edit);
            rentTextView = itemView.findViewById(R.id.rent_textview_edit);
            addressTextView = itemView.findViewById(R.id.addresss_textview_edit_edit);
            cityTextView = itemView.findViewById(R.id.city_textView_edit);
            relativeLayout = itemView.findViewById(R.id.relative_layout);
            post_update = itemView.findViewById(R.id.post_edit_btn);
            post_delete = itemView.findViewById(R.id.post_delete_btn);
        }
    }
}
