package com.example.my_instagram;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.my_instagram.Adapter.CommentAdapter;
import com.example.my_instagram.Model.Comment;
import com.example.my_instagram.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView rvComments;
    private CommentAdapter commentAdapter;
    private ArrayList<Comment> mComments;
    private EditText edtComment;
    private ImageView imgProfile;
    private TextView tvPost;
    private Toolbar toolbar;
    private String postId;
    private String publisherId;
    private FirebaseUser firebaseUser;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        rvComments = findViewById(R.id.rvComments);
        rvComments.setHasFixedSize(true);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        mComments = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, mComments);
        rvComments.setAdapter(commentAdapter);

        edtComment = findViewById(R.id.edtComment);
        imgProfile = findViewById(R.id.imgProfile);
        tvPost = findViewById(R.id.tvPost);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        publisherId = intent.getStringExtra("publisherId");

        tvPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtComment.getText().toString().equals("")) {
                    Toast.makeText(CommentActivity.this, "You can't sent emty comment", Toast.LENGTH_SHORT).show();
                } else {
                    addComment();
                }
            }
        });

        getImage();
        readComments();

    }

    private void addComment() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Comments").child(postId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", edtComment.getText().toString());
        hashMap.put("publisher", firebaseUser.getUid());
        myRef.push().setValue(hashMap);

        addNotification();
        edtComment.setText("");
    }

    private void addNotification(){
        DatabaseReference myRef=FirebaseDatabase.getInstance().getReference("Notifications").child(publisherId);
        HashMap<String, Object> hashMap=new HashMap<>();
        hashMap.put("userId",firebaseUser.getUid());
        hashMap.put("text","commented: "+edtComment.getText().toString());
        hashMap.put("postId",postId);
        hashMap.put("isPost", true);
        myRef.push().setValue(hashMap);
    }



    private void getImage() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageURL()).into(imgProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readComments(){
        DatabaseReference myRef=FirebaseDatabase.getInstance().getReference("Comments").child(postId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mComments.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Comment comment=snapshot.getValue(Comment.class);
                    mComments.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}