package com.example.dhruv.pg_accomodation.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dhruv.pg_accomodation.R;
import com.example.dhruv.pg_accomodation.actvities.ViewPostActivity;
import com.example.dhruv.pg_accomodation.models.Post;

import java.util.ArrayList;
import java.util.List;

public class SearchPostAdapter extends RecyclerView.Adapter<SearchPostAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Post> posts;

    public SearchPostAdapter(Context context, ArrayList posts) {
        this.context = context;
        this.posts = posts;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_post_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(posts.get(position));
        Post post = posts.get(position);
        setPost(post, holder);
    }

    private void setPost(final Post post, ViewHolder holder) {
        Glide.with(context).load(post.getPostImage()).into(holder.imageView);
        holder.addressTextView.setText(post.getPostAddress());
        holder.rentTextView.setText(post.getPostRent() + " / month");
        holder.cityTextView.setText(" in  " + post.getPostCity());
        holder.typeTextView.setText(post.getPostType());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewPostActivity.class);
                intent.putExtra("ownerId", post.getPostOwner());
                intent.putExtra("postId", post.getPostId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView cityTextView, typeTextView, rentTextView, addressTextView;
        public ImageView imageView;
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.search_post_item1);
            typeTextView = itemView.findViewById(R.id.type_textView);
            rentTextView = itemView.findViewById(R.id.rent_textview);
            addressTextView = itemView.findViewById(R.id.addresss_textview);
            cityTextView = itemView.findViewById(R.id.city_textView);
            relativeLayout = itemView.findViewById(R.id.relative_layout);

        }
    }
}