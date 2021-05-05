package com.example.dhruv.pg_accomodation.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dhruv.pg_accomodation.R;
import com.example.dhruv.pg_accomodation.actvities.ViewPostActivity;
import com.example.dhruv.pg_accomodation.models.Post;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import static androidx.recyclerview.widget.RecyclerView.*;

public class SearchFragment extends Fragment {
    private RecyclerView recyclerView;
    private ArrayList<Post> postArrayList;
    private TextInputEditText autoCompleteTextView;
    private AppCompatAutoCompleteTextView filterSpinner;
    private static int count = 0;

    public SearchFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.search_recyclerview);
        autoCompleteTextView = view.findViewById(R.id.search_edittext);
//        filterSpinner = view.findViewById(R.id.filter_spinner);
        initFilterAdapter();

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence str, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable str) {
                filter(str.toString().toLowerCase());
            }

        });


        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layout);
        postArrayList = new ArrayList<>();

        //get all data of all posts
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Iterable<DataSnapshot> iterable = snapshot.getChildren();
                ArrayList<Post> postArray = new ArrayList<>();
                for (DataSnapshot d : iterable) {
                    Post post = d.getValue(Post.class);
                    postArray.add(post);
                }
                Adapter postRecyclerAdapter = new SearchPostRecyclerAdapter(getContext(), postArray);
                recyclerView.setAdapter(postRecyclerAdapter);
                setPostList(postArray);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setPostList(ArrayList<Post> postArray) {
        this.postArrayList = postArray;
    }

    private void filter(String rawText) {
        ArrayList<Post> filteredPosts = new ArrayList<>();

        for (Post post : postArrayList) {
            if (post.getPostType().toLowerCase().contains(rawText)) {
                filteredPosts.add(post);
            }
        }

        for (Post post : postArrayList) {
            if (post.getPostRent().toLowerCase().contains(rawText)) {
                filteredPosts.add(post);
            }
        }

        for (Post post : postArrayList) {
            if (post.getPostCity().toLowerCase().contains(rawText) || post.getPostAddress().toLowerCase().trim().contains(rawText)) {
                filteredPosts.add(post);
            }
        }
        Adapter postRecyclerAdapter = new SearchPostRecyclerAdapter(getContext(), filteredPosts);
        recyclerView.setAdapter(postRecyclerAdapter);

    }

    private void initFilterAdapter() {
        ArrayList<String> filters = new ArrayList<>();
        filters.add("Type");
        filters.add("Price");
        filters.add("City");
        filters.add("Address");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, filters);
//        filterSpinner.setAdapter(adapter);
    }
}


class SearchPostRecyclerAdapter extends RecyclerView.Adapter<SearchPostRecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Post> posts;

    public SearchPostRecyclerAdapter(Context context, ArrayList posts) {
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

        holder.relativeLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ViewPostActivity.class);
                intent.putExtra("ownerId",post.getPostOwner());
                intent.putExtra("postId",post.getPostId());
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