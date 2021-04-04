package com.example.my_instagram.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_instagram.Adapter.PostAdapter;
import com.example.my_instagram.Model.Post;
import com.example.my_instagram.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PostDetailFragment extends Fragment {

    private String postId;
    private RecyclerView rvDetail;
    private PostAdapter postAdapter;
    private ArrayList<Post> mPosts;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_detail, container, false);

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        postId = prefs.getString("postId", "none");

        mPosts = new ArrayList<>();
        rvDetail = view.findViewById(R.id.rvDetails);
        rvDetail.setHasFixedSize(true);
        rvDetail.setLayoutManager(new LinearLayoutManager(getContext()));
        postAdapter = new PostAdapter(getContext(), mPosts);
        rvDetail.setAdapter(postAdapter);
        readDetailPost();

        return view;


    }

    private void readDetailPost() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPosts.clear();
                Post post = snapshot.getValue(Post.class);
                mPosts.add(post);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}