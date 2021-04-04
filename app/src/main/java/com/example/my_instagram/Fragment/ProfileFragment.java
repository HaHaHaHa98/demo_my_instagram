package com.example.my_instagram.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.my_instagram.Adapter.PhotoAdapter;
import com.example.my_instagram.EditProfileActivity;
import com.example.my_instagram.Model.Post;
import com.example.my_instagram.Model.User;
import com.example.my_instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class ProfileFragment extends Fragment {

    private ImageView imgProfile, imgOption;
    private ImageButton btnPhoto, btnSavePhoto;
    private TextView tvPosts, tvFollowers, tvFollowing, tvFullName, tvBio, tvUsername;
    private Button btnEdit;
    private RecyclerView rvPhoto, rvSavedPhoto;
    private PhotoAdapter photoAdapter;
    private ArrayList<Post> mPosts;

    private ArrayList<String> list_saved_id;
    private PhotoAdapter savedPhotoAdapter;
    private ArrayList<Post> mySaves;

    private FirebaseUser firebaseUser;
    private String profileId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences pref = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId = pref.getString("profileId", "none");
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        imgOption = view.findViewById(R.id.imgOption);
        imgProfile = view.findViewById(R.id.imgProfile);
        tvPosts = view.findViewById(R.id.tvPost);
        tvFollowers = view.findViewById(R.id.tvFollower);
        tvFollowing = view.findViewById(R.id.tvFollowing);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvBio = view.findViewById(R.id.tvBio);
        tvUsername = view.findViewById(R.id.tvUsername);
        btnEdit = view.findViewById(R.id.btnEdit);
        btnPhoto = view.findViewById(R.id.btnPhoto);
        btnSavePhoto = view.findViewById(R.id.btnSavePhoto);
        rvPhoto = view.findViewById(R.id.rvPhoto);
        rvPhoto.setHasFixedSize(true);
        rvPhoto.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mPosts = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(), mPosts);
        rvPhoto.setAdapter(photoAdapter);


        rvSavedPhoto = view.findViewById(R.id.rvPhotoSave);
        rvSavedPhoto.setHasFixedSize(true);
        rvSavedPhoto.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mySaves = new ArrayList<>();
        savedPhotoAdapter = new PhotoAdapter(getContext(), mySaves);
        rvSavedPhoto.setAdapter(savedPhotoAdapter);

        rvPhoto.setVisibility(View.VISIBLE);
        rvSavedPhoto.setVisibility(View.GONE);
        userInfo();
        getFollowers();
        getNrPosts();
        getMyPhotos();
        getMySavedPhotos();

        if (profileId.equals(firebaseUser.getUid())) {
            btnEdit.setText("Edit Profile");
        } else {
            btnSavePhoto.setVisibility(View.GONE);
        }

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag = btnEdit.getText().toString();
                if (tag.equals("Edit Profile")) {
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                } else if (tag.equals("follows")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(profileId).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("follower").child(profileId).setValue(true);
                    addNotification();

                } else if (tag.equals("following")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                            .child("following").child(firebaseUser.getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                            .child("follower").child(firebaseUser.getUid()).removeValue();

                }
            }
        });

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvPhoto.setVisibility(View.VISIBLE);
                rvSavedPhoto.setVisibility(View.GONE);
            }
        });

        btnSavePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvPhoto.setVisibility(View.GONE);
                rvSavedPhoto.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    private void addNotification() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Notifications").child(profileId);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userId", firebaseUser.getUid());
        hashMap.put("text", "started following you");
        hashMap.put("postId", "");
        hashMap.put("isPost", false);
        myRef.push().setValue(hashMap);
    }

    private void userInfo() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users").child(profileId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null) {
                    return;
                }
                User user = snapshot.getValue(User.class);
                Glide.with(getContext()).load(user.getImageURL()).into(imgProfile);
                tvUsername.setText(user.getUsername());
                tvFullName.setText(user.getFullName());
                tvBio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFollow() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileId).exists()) {
                    btnEdit.setText("following");
                } else {
                    btnEdit.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMyPhotos() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Posts");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPosts.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId)) {
                        mPosts.add(post);
                    }
                }
                Collections.reverse(mPosts);
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getMySavedPhotos() {
        list_saved_id = new ArrayList<>();
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Saves");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    list_saved_id.add(snapshot.getKey());
                }
                readSave();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readSave() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Posts");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mySaves.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);

                    for (String id : list_saved_id) {
                        if (post.getPostId().equals(id)) {
                            mySaves.add(post);
                        }
                    }

                }
                savedPhotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileId).child("followers");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvFollowers.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference myRef1 = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileId).child("following");

        myRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvFollowing.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getNrPosts() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Posts");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId)) {
                        i++;
                    }
                }
                tvPosts.setText("" + i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}